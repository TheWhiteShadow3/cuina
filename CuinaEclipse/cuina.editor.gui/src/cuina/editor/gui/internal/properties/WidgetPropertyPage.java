package cuina.editor.gui.internal.properties;

import cuina.widget.data.WidgetNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

public class WidgetPropertyPage extends PropertyPage
{
	private Text nameText;
	private Spinner xSpinner;
	private Spinner ySpinner;
	private Spinner widthSpinner;
	private Spinner heightSpinner;
	private Button visibleButton;
	private Button enableButton;

	@Override
	protected Control createContents(Composite parent)
	{
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		addWidgetData(composite);
		setValue();
		
		return composite;
	}
	
	private void addWidgetData(Composite parent)
	{
		Composite group = createGroup(parent, 2);
		
		new Label(group, SWT.NONE).setText("Name:");
		nameText = new Text(group, SWT.BORDER);
//		nameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		
		new Label(group, SWT.NONE).setText("X:");
		xSpinner = addSpinner(group, 0, 9999);
		
		new Label(group, SWT.NONE).setText("Y:");
		ySpinner = addSpinner(group, 0, 9999);
		
		new Label(group, SWT.NONE).setText("Width:");
		widthSpinner = addSpinner(group, 0, 9999);
		
		new Label(group, SWT.NONE).setText("Height:");
		heightSpinner = addSpinner(group, 0, 9999);
		
		new Label(group, SWT.NONE).setText("Sichtbar:");
		visibleButton = new Button(group, SWT.CHECK);
		
		new Label(group, SWT.NONE).setText("Aktiv:");
		enableButton = new Button(group, SWT.CHECK);
	}
	
	private Spinner addSpinner(Composite parent, int min, int max)
	{
		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setSize(96, -1);
		spinner.setMinimum(min);
        spinner.setMaximum(max);
        return spinner;
	}
	
    private Composite createGroup(Composite parent, int coloumns)
    {
        Composite group = new Composite(parent, SWT.NONE);
        group.setLayout(new GridLayout(coloumns, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return group;
    }
	
	private void setValue()
	{
		WidgetNode node = (WidgetNode) getElement().getAdapter(WidgetNode.class);
		
		nameText.setText(node.getName());
		xSpinner.setSelection(node.x);
		ySpinner.setSelection(node.y);
		widthSpinner.setSelection(node.width);
		heightSpinner.setSelection(node.height);
		visibleButton.setSelection(node.visible);
		enableButton.setSelection(node.enabled);
	}

	@Override
	public boolean performOk()
	{
		// TODO Auto-generated method stub
		return super.performOk();
	}

	@Override
	public void applyData(Object data)
	{
		// TODO Auto-generated method stub
		super.applyData(data);
	}
	
	
}
