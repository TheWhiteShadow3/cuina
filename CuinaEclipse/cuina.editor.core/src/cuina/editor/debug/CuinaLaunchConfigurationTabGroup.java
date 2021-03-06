package cuina.editor.debug;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;


public class CuinaLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup
{
   @Override
   public void createTabs(ILaunchConfigurationDialog dialog, String mode)
   {
       ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[]
       {
           new CuinaTab(),
           new CuinaArgumentTab(),
           new CommonTab()
       };
       setTabs(tabs);
   }
}