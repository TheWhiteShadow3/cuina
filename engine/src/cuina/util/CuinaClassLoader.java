package cuina.util;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Ein ClassLoader, der in der Lage ist, Klassen aus den Plugins der Engine zu laden.
 * @author TheWhiteShadow
 */
public class CuinaClassLoader extends URLClassLoader
{
	private static CuinaClassLoader instance = new CuinaClassLoader();
	
	public synchronized static CuinaClassLoader getInstance()
	{
		return instance;
	}
	
	private CuinaClassLoader()
	{
		super(new URL[0]);
	}

	@Override
	public synchronized void addURL(URL url)
	{
		super.addURL(url);
	}
}
