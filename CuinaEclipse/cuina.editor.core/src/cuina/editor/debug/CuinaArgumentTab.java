package cuina.editor.debug;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;

public class CuinaArgumentTab extends JavaArgumentsTab
{
   @Override
   public void setDefaults(ILaunchConfigurationWorkingCopy config)
   {
       config.setAttribute(ATTR_PROGRAM_ARGUMENTS, CuinaLaunch.getDefaultArgs());
       config.setAttribute(ATTR_VM_ARGUMENTS, CuinaLaunch.getDefaultVMArgs());
   }
}