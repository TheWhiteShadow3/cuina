package cuina.editor.debug;
 
import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.CuinaProject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
 
public class CuinaTab extends AbstractLaunchConfigurationTab 
{
	private CuinaProject project;
    private ProjectBlock projectBlock;
    private EngineBlock engineBlock;
    private PluginBlock pluginBlock;
    
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
			readProject(config);
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
        try
        {
        	readProject(config);
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
    	return project;
    }
	
	private void readProject(ILaunchConfiguration config) throws CoreException
	{
		String projectName = config.getAttribute(CuinaLaunch.PROJECT_NAME, (String) null);
		if (projectName == null || projectName.isEmpty()) return;
		
		this.project = CuinaPlugin.getCuinaProject(projectName);
	}
}