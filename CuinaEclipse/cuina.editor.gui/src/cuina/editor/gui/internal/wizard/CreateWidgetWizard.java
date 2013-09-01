package cuina.editor.gui.internal.wizard;

import org.eclipse.jface.wizard.Wizard;

import cuina.data.widget.Widget;

public class CreateWidgetWizard extends Wizard
{
	private SelectParentWizardPage parentWizardPage;
	private Class<?> prototype;
	
	public CreateWidgetWizard(Widget root, Widget preSelected, Class<?> prototype)
	{
		parentWizardPage = new SelectParentWizardPage(root, preSelected);
		this.prototype = prototype;
	}
	
	@Override
	public void addPages()
	{
		addPage(parentWizardPage);
	}

	@Override
	public boolean performFinish()
	{
		Widget parent = parentWizardPage.getSelected();
		
		if(!prototype.isInterface())
			try
			{
				Object obj = prototype.newInstance();
				if(obj instanceof Widget)
				{
					Widget child = (Widget) obj;
					child.setKey(child.getClass().getSimpleName() + "_" + Integer.toHexString(child.hashCode()));
					
					parent.add(child);
				}
			} catch(InstantiationException e)
			{
				e.printStackTrace();
			} catch(IllegalAccessException e)
			{
				e.printStackTrace();
			}
		
		
		
		return true;
	}
}