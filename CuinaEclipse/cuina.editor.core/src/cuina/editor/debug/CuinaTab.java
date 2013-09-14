package cuina.editor.debug;
 
import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
 
public class CuinaTab extends AbstractLaunchConfigurationTab
{
	private ProjectBlock projectBlock;
	private EngineBlock engineBlock;
	private PluginBlock pluginBlock;
	ILaunchConfiguration launchConfig;

	public CuinaTab()
	{
		projectBlock = new ProjectBlock(this);
		engineBlock = new EngineBlock(this);
		pluginBlock = new PluginBlock(this);
	}

    @Override
    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
 
        projectBlock.createControl(composite);
        engineBlock.createControl(composite);
        pluginBlock.createControl(composite);
        setControl(composite);
    }
 
    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy config)
    {
    	try
		{
	        projectBlock.setDefaults(config);
	        engineBlock.setDefaults(config);
	        pluginBlock.setDefaults(config);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
    }
 
    @Override
    public void initializeFrom(ILaunchConfiguration config)
    {
    	this.launchConfig = config;
        try
        {
            projectBlock.initializeFrom(config);
            engineBlock.initializeFrom(config);
            pluginBlock.initializeFrom(config);
        }
        catch (CoreException e)
        {
            e.printStackTrace();
        }
    }
 
    @Override
    public void performApply(ILaunchConfigurationWorkingCopy config)
    {
        projectBlock.performApply(config);
        engineBlock.performApply(config);
        pluginBlock.performApply(config);
    }
 
    @Override
    public String getName()
    {
        return "Cuina Engine";
    }
    
    @Override
    public boolean isValid(ILaunchConfiguration launchConfig)
    {
        String error = null;
        error = projectBlock.validate();
        if (error == null)
            error = engineBlock.validate();
        if (error == null)
            error = pluginBlock.validate();
        setErrorMessage(error);
        return error == null;
    }
 
    public void updateTab()
    {
        updateLaunchConfigurationDialog();
    }
    
    public CuinaProject getProject()
	{
		IWorkbenchPage page = CuinaCore.getWorkbenchWindow().getActivePage();
		if (page != null)
		{
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) selection;
				if (!ss.isEmpty())
				{
					Object obj = ss.getFirstElement();
					if (obj instanceof IResource)
					{
						IProject pro = ((IResource) obj).getProject();
						return CuinaCore.getCuinaProject(pro);
					}
				}
			}
			IEditorPart part = page.getActiveEditor();
			if (part != null)
			{
				IEditorInput input = part.getEditorInput();
				IFile file = (IFile) input.getAdapter(IFile.class);
				return CuinaCore.getCuinaProject(file.getProject());
			}
		}
		return null;
	}
}