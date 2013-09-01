package cuina.gl;

import java.io.IOException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class LWJGL
{
	public static final int NONE = 0;
	public static final int NO_BACKGROUND = 1;
	public static final int ENABLE_3D = 2;
	private static boolean init;
	
	private LWJGL() {};
	
	public static void init(String libraryPath)
	{
		if (init) return;
		if (libraryPath == null) libraryPath = findPath();
		if (libraryPath == null) return;
		
		System.setProperty("org.lwjgl.librarypath", libraryPath);
		init = true;
	}

	private static String findPath()
	{
		String os = System.getProperty("os.name").toLowerCase();
		String osPath = null;
				
		if (os.contains("linux") || os.contains("bsd") || os.contains("nix")) osPath = "linux";
		if (os.contains("windows")) osPath = "windows";
		if (os.contains("mac os x")) osPath = "macosx";
		if (os.contains("sunos")) osPath = "solaris";
		
		Bundle plugin = Platform.getBundle("cuina.gl");
		try
		{
			return FileLocator.resolve(plugin.getEntry("lib/lwjgl/native/" + osPath)).getPath();
		}
		catch (IOException e) {}
		return null;
	}
	
//	/**
//	 * Ladet das OpenGL-Image, der übergebenen Resource.
//	 * Die Resource muss auf eine Datei mit gültigem Image-Format zeigen.
//	 * <p>
//	 * Es wird zuerst versucht das Bild aus dem Cache der Ressource zu laden.
//	 * Wenn der Cache leer ist, wird die Datei aus dem Dateisystem geladen und in den Cache gelegt.
//	 * </p>
//	 * @param resource Image-Ressource, welche geladen werden soll.
//	 * @return Das Image.
//	 * @throws NullPointerException wenn die übergebene Ressource <code>null</code> ist.
//	 * @throws ResourceException Wenn beim Laden Fehler auftreten
//	 * oder Die Resource auf kein gültiges Image-Format zeigt.
//	 * @throws LWJGLException 
//	 * @throws ClassCastException wenn der Cache nicht leer ist und kein Image beinhaltet.
//	 */
//	public static Texture loadTextur(Object context, Resource resource) throws ResourceException, LWJGLException
//	{
//		if (resource == null) throw new NullPointerException();
//		
//		TexturData data = (TexturData) resource.getData();
//		Texture tex;
//		if (data == null)
//		{
//			data = new TexturData();
//			
//			data.put(context, tex);
//			resource.setData(data);
//		}
//		
//		tex = data.get(context);
//		if (tex == null)
//		{
//			data.put(context, tex);
//			tex = TextureLoader.getTexture(context, resource.getPath().toString());
//		}
//		return tex;
//	}
//	
//	/**
//	 * Ressourcen Cache-Container für Image-Objekte.
//	 * @author TheWhiteShadow
//	 */
//	public static class TexturData implements Disposable
//	{
//		private final HashMap<Object, Texture> dataList = new HashMap<Object, Texture>(8);
//		
//		public TexturData()
//		{}
//		
//		public Texture get(Object context)
//		{
//			return dataList.get(context);
//		}
//
//		public Texture put(Object context, Texture texture)
//		{
//			return dataList.put(context, texture);
//		}
//
//		@Override
//		public void dispose()
//		{
//			for (Texture tex : dataList.values()) tex.dispose();
//			dataList.clear();
//		}
//	}
}
