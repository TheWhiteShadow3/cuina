package cuina.editor.debug;
 
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
        projectBlock.setDefaults(config);
        engineBlock.setDefaults(config);
        pluginBlock.setDefaults(config);
    }
 
    @Override
    public void initializeFrom(ILaunchConfiguration config)
    {
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
}