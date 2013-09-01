package cuina.editor.debug;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;


public class CuinaArgumentTab extends JavaArgumentsTab
{
   @Override
   public void setDefaults(ILaunchConfigurationWorkingCopy config)
   {
       super.setDefaults(config);
       
       config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
       config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, CuinaLaunch.getDefaultVMArgs());
   }
}