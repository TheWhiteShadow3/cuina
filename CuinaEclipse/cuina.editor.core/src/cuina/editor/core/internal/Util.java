package cuina.editor.core.internal;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util
{
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
