package cuina.editor.debug;

import cuina.editor.core.CuinaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

public class CuinaLauncher extends JavaLaunchDelegate
{
	private String mainClass;
	private String[] classPath;
	private File file;

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException
	{
		init(config);
		super.launch(config, mode, launch, monitor);
	}

	private void init(ILaunchConfiguration config) throws CoreException
	{
		String pathName = config.getAttribute(CuinaLaunch.ENGINE_PATH, (String) null);
		try
		{
			if (pathName == null) throw new NullPointerException("engine-path is null.");
	
			file = verifyPath(new Path(pathName));
			if (file.isDirectory())
			{
				file = new File(file, "cuina.engine.jar");
			}

			if (file == null || !file.exists()) throw new FileNotFoundException(file.toString());
			Attributes atts = new JarFile(file).getManifest().getMainAttributes();
			mainClass = atts.getValue("Main-Class");
			classPath = new String[] { file.getName() };
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CuinaPlugin.PLUGIN_ID, "Cuina-Engine not found.", e));
		}
	}

	@Override
	public String getMainTypeName(ILaunchConfiguration config) throws CoreException
	{
		return mainClass;
	}

	@Override
	public File verifyWorkingDirectory(ILaunchConfiguration config)
	{
		return file.getParentFile();
	}
	
	@Override
	public String[] getClasspath(ILaunchConfiguration config)
	{
		return classPath;
	}

	private File verifyPath(IPath path)
	{
		if (path.isAbsolute())
		{
			return path.toFile();
		}
		else
		{
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
			if (res instanceof IContainer && res.exists()) return res.getLocation().toFile();
		}
		return null;
	}

	@Override
	public String getVMArguments(ILaunchConfiguration config) throws CoreException
	{
		String args = super.getVMArguments(config);
		int pathIndex;
		pathIndex = args.indexOf("-Dcuina.game.path"); //$NON-NLS-1$
		if (pathIndex < 0)
		{
			StringBuffer buffer = new StringBuffer(args);
			String projectName = config.getAttribute(CuinaLaunch.PROJECT_NAME, (String) null);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project != null)
			{
				buffer.append(" -Dcuina.game.path="); //$NON-NLS-1$
				buffer.append("\"").append(project.getLocation().toString()).append("\""); //$NON-NLS-1$ $NON-NLS-2$
				args = buffer.toString();
			}
		}
		pathIndex = args.indexOf("-Dcuina.plugin.path"); //$NON-NLS-1$
		if (pathIndex < 0)
		{
			String pluginPath = config.getAttribute(CuinaLaunch.PLUGIN_PATH, (String) null);
			if (pluginPath != null)
			{
				StringBuffer buffer = new StringBuffer(args);
				buffer.append(" -Dcuina.plugin.path="); //$NON-NLS-1$
				buffer.append("\"").append(pluginPath).append("\""); //$NON-NLS-1$ $NON-NLS-2$
				args = buffer.toString();
			}
		}

		return args;
	}

//	@Override
//	public String[][] getBootpathExt(ILaunchConfiguration config) throws CoreException
//	{
//		String[][] paths = super.getBootpathExt(config);
//		paths[1] = new String[] { file.toString() };
//		return paths;
//	}
}
