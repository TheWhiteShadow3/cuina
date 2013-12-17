package cuina.editor.debug;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

public class CuinaVariableResolver implements IDynamicVariableResolver
{
	private static final HashMap<String, String> VALUES = new HashMap<String, String>();

	public static String getValue(String name)
	{
		return VALUES.get(name);
	}

	public static String setValue(String name, String value)
	{
		return VALUES.put(name, value);
	}
	
	@Override
	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException
	{
//		String pathName = System.getenv(EngineReference.CUINA_SYSTEM_VARIABLE);
//		if (pathName == null) return null;
//		
//		Path path = Paths.get(pathName);
//		if (!path.isAbsolute())
//		{
//			path = getProjectPath().resolve(path);
//		}
//		if (Files.notExists(path))
//			throwException("File not found " + path.toString(), null);
//		
//		if (Files.isDirectory(path))
//		{
//			path = path.resolve(EngineReference.ENGINE_JAR);
//			if (Files.notExists(path))
//				throwException("File not found " + path.toString(), null);
//		}
//    	return path.toString();
		
		return VALUES.get(variable.getName());
	}
	
//	private Path getProjectPath() throws CoreException
//	{
//		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
//		String pathString = manager.performStringSubstitution("${selected_resource_path}");
//		return Paths.get(pathString);
//	}
//	
//	private void throwException(String message, Throwable exception) throws CoreException
//	{
//		throw new CoreException(new Status(IStatus.ERROR, CuinaCore.PLUGIN_ID, message, exception));
//	}
}
