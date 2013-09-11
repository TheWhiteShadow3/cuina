package cuina.editor.debug;

import cuina.editor.core.CuinaPlugin;
import cuina.editor.core.internal.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
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
			
			pathName = Util.resolveEnviromentVariables(pathName);
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

	private File verifyPath(IPath path) throws Exception
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
		throw new IllegalArgumentException(path.toString());
	}

	@Override
	public String getVMArguments(ILaunchConfiguration config) throws CoreException
	{
		String args = super.getVMArguments(config);
		int pathIndex;
		StringBuilder builder = new StringBuilder(args);
		pathIndex = args.indexOf("-Dcuina.game.path"); //$NON-NLS-1$
		if (pathIndex < 0)
		{
			String projectName = config.getAttribute(CuinaLaunch.PROJECT_NAME, (String) null);
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project != null)
			{
				addParameter(builder, "cuina.game.path", project.getLocation().toString());
			}
		}
		pathIndex = args.indexOf("-Dcuina.plugin.path"); //$NON-NLS-1$
		if (pathIndex < 0)
		{
			String pluginPath = config.getAttribute(CuinaLaunch.PLUGIN_PATH, (String) null);
			if (pluginPath != null)
			{
				addParameter(builder, "cuina.plugin.path", pluginPath);
			}
		}
		
		Set<String> pluginList = config.getAttribute(CuinaLaunch.PLUGIN_LIST, (Set<String>) null);
		if (pluginList != null)
		{
			builder.append(" -Dcuina.plugin.list=\"");
			
			Object[] array = pluginList.toArray();
			for (int i = 0; i < array.length; i++)
			{
				if (i > 0) builder.append(';');
				builder.append(array[i]);
			}
			builder.append("\""); 
		}

		return builder.toString();
	}
	
	private void addParameter(StringBuilder builder, String name, String value)
	{
		builder.append(" -D");
		builder.append(name);
		builder.append("=\"").append(value).append("\"");
	}

//	@Override
//	public String[][] getBootpathExt(ILaunchConfiguration config) throws CoreException
//	{
//		String[][] paths = super.getBootpathExt(config);
//		paths[1] = new String[] { file.toString() };
//		return paths;
//	}
}
