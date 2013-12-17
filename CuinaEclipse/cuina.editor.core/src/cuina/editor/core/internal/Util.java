package cuina.editor.core.internal;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.CuinaProject;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class Util
{
	private static IEclipsePreferences prefs;
	
//	public static String resolveEnviromentVariables(String rawString)
//	{
//		StringBuilder builder = new StringBuilder(rawString.length());
//		int pos = 0;
//		while(pos < rawString.length())
//		{
//			int p1 = rawString.indexOf("${", pos);
//			if (p1 == -1) break;
//			int p2 = rawString.indexOf('}', p1);
//			if (p2 == -1) break;
//			
//			builder.append(rawString.substring(pos, p1));
//			String key = rawString.substring(p1+2, p2);
//			builder.append(System.getenv(key));
//			
//			pos = p2+1;
//		}
//		builder.append(rawString.substring(pos, rawString.length()));
//		
//		return builder.toString();
//	}
	
	
	public static IEclipsePreferences getProjectPreference(CuinaProject project) throws BackingStoreException
	{
		if (prefs == null)
		{
			prefs = new ProjectScope(project.getProject()).getNode(CuinaCore.PLUGIN_ID);
			prefs.sync();
		}
		return prefs;
	}
	
	public static void validateEnginePath(String pathString) throws FileNotFoundException
	{
		Path path = Paths.get(pathString);
		if (Files.notExists(path))
			throw new FileNotFoundException(path.toString());
		
		if ( !path.toString().endsWith(".jar") && !path.toString().endsWith(".exe")
				&& Files.notExists(path.resolve("cuina.engine.jar")) )
		{
			throw new FileNotFoundException(path.toString());
		}
	}
}
