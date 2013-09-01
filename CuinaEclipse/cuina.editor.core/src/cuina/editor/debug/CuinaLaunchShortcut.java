package cuina.editor.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;

public class CuinaLaunchShortcut implements ILaunchShortcut
{
	@Override
	public void launch(ISelection selection, String mode)
	{
		if (selection instanceof IStructuredSelection)
		{
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			System.out.println("Launch Selection: " + obj);
			IProject project = null;
			if (obj instanceof IResource)
			{
				project = ((IResource) obj).getProject();
			}
			else if (obj instanceof IAdaptable)
			{
				IFile file = (IFile) ((IAdaptable) obj).getAdapter(IFile.class);
				project = file.getProject();
			}
			launch(project, mode);
		}
	}

	@Override
	public void launch(IEditorPart editor, String mode)
	{
		IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
		System.out.println("Lauch Editor-Input: " + file);
		launch(file.getProject(), mode);
	}

	private ILaunchConfiguration createLaunchConfiguration(IProject project)
	{
		ILaunchConfiguration config = null;
		ILaunchConfigurationWorkingCopy wc = null;
		try
		{
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfigurationType configType = manager.getLaunchConfigurationType(CuinaLaunch.CONFIGURATION_TYPE);
			wc = configType.newInstance(null, manager.generateLaunchConfigurationName(CuinaLaunch.CONFIGURATION_NAME));
			wc.setAttribute(CuinaLaunch.PROJECT_NAME, project.getName());
			wc.setAttribute(CuinaLaunch.ENGINE_PATH, CuinaLaunch.getEnginePath());
			wc.setAttribute(CuinaLaunch.PLUGIN_PATH, "plugins");
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, CuinaLaunch.getDefaultVMArgs());
			config = wc.doSave();
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
		return config;
	}

	private void launch(IProject project, String mode)
	{
		try
		{
			ILaunchConfiguration config = null;
			ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
			ILaunchConfiguration[] configs = manager.getLaunchConfigurations();
			if (configs.length == 0)
			{
				config = createLaunchConfiguration(project);
			}
			else
			{
				if (project == null)
				{
					if (configs.length > 0) configs[0].launch(mode, null);
					return;
				}

				for (ILaunchConfiguration c : configs)
				{
					String attProjectName = c.getAttribute(CuinaLaunch.PROJECT_NAME, (String) null);
					if (project.getName().equals(attProjectName))
					{
						config = c;
						break;
					}
				}
			}
			config.launch(mode, null);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
}
