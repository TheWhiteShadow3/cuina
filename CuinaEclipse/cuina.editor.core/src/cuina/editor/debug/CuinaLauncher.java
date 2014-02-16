package cuina.editor.debug;

import cuina.editor.core.CuinaCore;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
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
		try
		{
			this.file = getEnginePath(config).toFile();
			
			JarFile jar = new JarFile(file);
			Attributes atts = jar.getManifest().getMainAttributes();
			this.mainClass = atts.getValue("Main-Class");
			this.classPath = new String[] { file.getName() };
			jar.close();
		}
		catch (Exception e)
		{
			throw new CoreException(new Status(IStatus.ERROR, CuinaCore.PLUGIN_ID, "Cuina-Engine not found.", e));
		}
	}
	
	private Path getEnginePath(ILaunchConfiguration config) throws CoreException
	{
		String att = config.getAttribute(CuinaLaunch.ENGINE_PATH, (String) null);
		String pathName = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(att);
		if (pathName == null) throw new CoreException(new Status(
				IStatus.ERROR, CuinaCore.PLUGIN_ID, "Engine-path is null."));
		
		Path path = Paths.get(pathName);
		if (!path.isAbsolute())
		{
			path = getProjectPath(config).resolve(path);
		}
		
		if (Files.isDirectory(path))
			path = path.resolve("cuina.engine.jar");

		if (Files.notExists(path)) throw new CoreException(new Status(
				IStatus.ERROR, CuinaCore.PLUGIN_ID, "Engine-path not found: " + path.toString()));
		return path;
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
	
	private Path getProjectPath(ILaunchConfiguration config) throws CoreException
	{
		String att = config.getAttribute(CuinaLaunch.PROJECT_NAME, (String) null);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(att);
		return Paths.get(project.getLocation().toOSString());
	}
	
	private File getPluginPath(ILaunchConfiguration config) throws CoreException
	{
		String att = config.getAttribute(CuinaLaunch.PLUGIN_PATH, (String) null);
		if (att == null) return null;
		
		String path = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(att);
		File file = new File(path);

		if (!file.exists()) throw new CoreException(new Status(
				IStatus.ERROR, CuinaCore.PLUGIN_ID, "Plugin-path not found: " + file.toString()));
		return file;
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
			File pluginPath = getPluginPath(config);
			if (pluginPath != null)
			{
				addParameter(builder, "cuina.plugin.path", pluginPath.getAbsolutePath());
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
