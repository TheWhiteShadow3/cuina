package cuina.graphics;
 
import static org.lwjgl.opengl.GL11.*;

import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;
 
/**
 * A utility class to load textures for OpenGL.
 * @author TheWhiteSHadow
 * @version 2.0
 */
public class TextureLoader
{
	public static final int TEXTURE_2D = 1;
	
	public static final int ANIOTROPIC = 1;
	
    private static TextureLoader instance = null;
 
    private TextureLoader() {}
 
    public Texture getTexture(String fileName, int flags) throws LoadingException
    {
        Texture tex = TextureCache.get(fileName);
        if (tex == null) try
        {
            tex = getTexture(ResourceManager.loadImage(fileName), flags);
            TextureCache.put(fileName, tex);
        }
        catch(LoadingException e)
        {
        	throw(e);
        }
    	catch(Exception e)
    	{
    		throw new LoadingException(fileName, e);
    	}
    		
        return tex;
    }
 
    /**
     * Ladet eine Textur zum angegebenen BufferedImage.
     * Wenn cacheKey angegeben ist, wird die geladene Textur im Cache abgelegt.
     * @param bufferedImage Image mit den Bildinformationen. 
     * @param cacheKey Schlüssel nachdem gesucht werden soll.
     * @return die geladene Textur.
     */
    public Texture getTexture(BufferedImage bufferedImage, int flags, String cacheKey)
    {
        Texture tex = TextureCache.get(cacheKey);
        if (tex == null)
        {
            tex = getTexture(bufferedImage, flags);
            if (cacheKey != null) TextureCache.put(cacheKey, tex);
        }
        return tex;
    }
    
    /**
     * Gibt eine Textur aus dem Cache zurück.
     * Wenn die Textur ncht vorhanden ist, wird null zurück gegeben.
     * @param cacheKey Schlüssel nachdem gesucht werden soll.
     * @return Textur aus dem Cache oder null, wenn nicht vorhanden.
     */
    public Texture getCachedTexture(String cacheKey)
    {
        return TextureCache.get(cacheKey);
    }
    
//  /**
//   * Erzeugt eine Textur.
//   * @param bufferedImage 
//   * @param hashString
//   * @return
//   */
//  public Texture getTexture(BufferedImage bufferedImage, String hashString)
//  {
//      Texture tex = TextureCache.get(hashString);
//
//      if(tex != null)
//      {
//          return tex;
//      }
//      tex = getTexture(bufferedImage);
//      if(hashString != null)
//          TextureCache.put(hashString, tex);
//
//      return tex;
//  }
 
    public Texture getTexture(int width, int height, int flags)
    {
        int textureID =  glGenTextures();
        Texture texture = new Texture(GL_TEXTURE_2D, textureID, width, height);
        
        GLCache.bindTexture(texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        Util.checkGLError();
        
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
                texture.getWidth(), texture.getHeight(), 0, GL_RGBA,
                GL_UNSIGNED_BYTE, (ByteBuffer) null);
        
        Util.checkGLError();
        
        return texture;
    }
    
    /**
     * Ladet eine Textur zum angegebenen BufferedImage.
     * @param bufferedImage Image mit den Bildinformationen.
     * @return die geladene Textur.
     */
	private Texture getTexture(BufferedImage bufferedImage, int flags)
	{
		int textureID =  glGenTextures();
		int srcWidth = bufferedImage.getWidth();
		int srcHeight = bufferedImage.getHeight();
		boolean alpha = bufferedImage.getColorModel().hasAlpha();
		ColorSpace colorSpace = bufferedImage.getColorModel().getColorSpace();
		switch(colorSpace.getType())
		{
			case ColorSpace.TYPE_RGB: break;
			default: throw new IllegalArgumentException("unsupported color-format: " + getColorFormat(colorSpace));
		}
		int colorFormat = alpha ? GL_RGBA : GL_RGB;

		Texture texture = new Texture(GL_TEXTURE_2D, textureID, srcWidth, srcHeight);

		byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
		convertColorFormat(data, alpha);

		ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(data, 0, data.length);
		buffer.flip();

		GLCache.bindTexture(texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		if ((flags & ANIOTROPIC) != 0)
		{
			// Due to LWJGL buffer check, you can't use smaller sized buffers (min_size = 16 for glGetFloat()).
			FloatBuffer max_a = BufferUtils.createFloatBuffer(16);
			// Grab the maximum anisotropic filter.
			GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max_a);
			System.out.println(max_a.get(0));
			// Set up the anisotropic filter.
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT,
					max_a.get(0));
		}

		if (texture.getWidth() != srcWidth || texture.getHeight() != srcHeight)
		{
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, colorFormat,
					texture.getWidth(), texture.getHeight(), 0, colorFormat,
					GL_UNSIGNED_BYTE, (ByteBuffer) null);
			GL11.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0,
					srcWidth, srcHeight, colorFormat, GL_UNSIGNED_BYTE, buffer);
		}
		else
		{
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, colorFormat,
					texture.getWidth(), texture.getHeight(), 0, colorFormat,
					GL_UNSIGNED_BYTE, buffer);
		}
		Util.checkGLError();

		return texture;
	}
    
	private String getColorFormat(ColorSpace cs)
	{
		String[] str = new String[cs.getNumComponents()];
		// Die Implementation von ColorSpace#getName(int) ist echt beschissen.
		for (int i = 0; i < str.length; i++)
			str[i] = cs.getName(i);
		return Arrays.toString(str);
	}
	
    private void convertColorFormat(byte[] data, boolean alpha)
    {
        byte temp;
        if (alpha)
        {
        	// Bufferd Image hat die Komponentenabfolge: ABGR, benötigt wird aber RGBA.
	        for(int i = 0; i < data.length; i += 4)
	        {
	            temp = data[i];
	            data[i] = data[i+3];
	            data[i+3] = temp;
	            
	            temp = data[i+1];
	            data[i+1] = data[i+2];
	            data[i+2] = temp;
	        }
        }
        else
        {
        	// Bufferd Image hat die Komponentenabfolge: BGR, benötigt wird aber RGB.
	        for(int i = 0; i < data.length; i += 3)
	        {
	            temp = data[i];
	            data[i] = data[i+2];
	            data[i+2] = temp;
	        }
        }
    }
    
    /**
     * Gibt eine Textur zurück, dessen Image-Größe auf die nächste 2er-Potenz skaliert ist.
     * @param bufferedImage
     * @return The loaded texture.
     */
    public Texture getFilledTexture(BufferedImage bufferedImage, int flags)
    {
        int width  = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int foldWidth  = get2Fold(width);
        int foldHeight = get2Fold(height);
        
        if (width != foldWidth || height != foldHeight)
        {
            BufferedImage scaledImage = new BufferedImage(foldWidth, foldHeight, BufferedImage.TYPE_4BYTE_ABGR);
            scaledImage.getGraphics().drawImage(bufferedImage, 0, 0, foldWidth, foldHeight, null);
            bufferedImage = scaledImage;
        }
        return getTexture(bufferedImage, flags);
    }
 
    public static void clear()
    {
        TextureCache.clear();
        System.gc();
    }
 
    /**
     * Get the closest greater power of 2 to the fold number
     * 
     * @param i
     *            The target number
     * @return The power of 2
     */
    static int get2Fold(int i)
    {
        return 1 << (32-Integer.numberOfLeadingZeros(i-1));
    }
 
//  /**
//   * Convert the buffered image to a texture
//   * 
//   * @param bufferedImage
//   *            The image to convert to a texture
//   * @param texture
//   *            The texture to store the data into
//   * @return A buffer containing the data
//   */
//  private ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture)
//  {
//      ByteBuffer imageBuffer;
//      WritableRaster raster;
//      BufferedImage texImage;
//
////        int texWidth = 2;
////        int texHeight = 2;
//
//      // find the closest power of 2 for the width and height
//      // of the produced texture
//      int texWidth = texture.getWidth();
//      int texHeight = texture.getHeight();
////        while(texWidth < bufferedImage.getWidth())
////        {
////            texWidth *= 2;
////        }
////        while(texHeight < bufferedImage.getHeight())
////        {
////            texHeight *= 2;
////        }
//
////        texture.setTextureWidth(texWidth);
////        texture.setTextureHeight(texHeight);
//
//      // create a raster that can be used by OpenGL as a source
//      // for a texture
//      if(bufferedImage.getColorModel().hasAlpha())
//      {
//          raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
//                  texWidth, texHeight, 4, null);
//          texImage = new BufferedImage(glAlphaColorModel, raster, false,
//                  new Hashtable());
//      } else
//      {
//          raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
//                  texWidth, texHeight, 3, null);
//          texImage = new BufferedImage(glColorModel, raster, false,
//                  new Hashtable());
//      }
//
//      // copy the source image into the produced image
//      Graphics g = texImage.getGraphics();
//      g.setColor(new Color(0f, 0f, 0f, 0f));
//      g.fillRect(0, 0, texWidth, texHeight);
//      g.drawImage(bufferedImage, 0, 0, null);
//
//      // build a byte buffer from the temporary image
//      // that be used by OpenGL to produce a texture.
//      byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();
//
//      imageBuffer = ByteBuffer.allocateDirect(data.length);
//      imageBuffer.order(ByteOrder.nativeOrder());
//      imageBuffer.put(data, 0, data.length);
//      imageBuffer.flip();
//
//      return imageBuffer;
//  }
 
    // public static BufferedImage loadImage(String fileName)
    // {
    // return loadImage(new File(fileName));
    // }
 
    // public static BufferedImage loadImage(File file)
    // {
    // FileInputStream stream;
    // BufferedImage image = null;
    // try
    // {
    // if (!file.isAbsolute())
    // {
    // File new_file = new File(Game.getRootPath(), file.getPath());
    // if (new_file.exists())
    // stream = new FileInputStream(new_file);
    // else
    // {
    // stream = new FileInputStream(System.getProperty("user.dir") +
    // File.separator + file);
    // }
    // }
    // else
    // stream = new FileInputStream(file);
    // image = ImageIO.read(stream);
    //
    // stream.close();
    // }
    // catch (IOException e)
    // {
    // System.err.println(e);
    // try
    // {
    // image = ImageIO.read(new File(System.getProperty("user.dir"),
    // "icon32.png"));
    // }
    // catch (IOException e1)
    // {
    // Game.handleException(e1, 3);
    // }
    // }
    // return image;
    // }
 
    public static ByteBuffer[] loadIcons(String... file_names)
            throws LoadingException
    {
        ArrayList<ByteBuffer> buffers = new ArrayList<ByteBuffer>(
                file_names.length);
        for(int i = 0; i < file_names.length; i++)
        {
            BufferedImage image = ResourceManager.loadImageFromJar(file_names[i]);
            if(image == null)
                continue;
 
            byte[] data = (byte[]) image.getRaster().getDataElements(0, 0,
                    image.getWidth(), image.getHeight(), null);
 
            ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
            buffer.put(data, 0, data.length);
            buffer.rewind();
            buffers.add(buffer);
        }
 
        return buffers.toArray(new ByteBuffer[buffers.size()]);
    }
 
    private static class TextureCache
    {
        private static HashMap<String, CacheEntry> data = new HashMap<String, CacheEntry>();
        private static ReferenceQueue<? super Texture> queue = new ReferenceQueue<Texture>();
        
        private static Texture get(String key)
        {
            cleanupData();
            
            CacheEntry ref = data.get(key);
            return ref != null ? ref.get() : null;
        }
        
        private static void put(String cacheKey, Texture referent)
        {
            cleanupData();
            
            data.put(cacheKey, new CacheEntry(cacheKey, referent));
        }
        
        private static void clear()
        {
            data.clear();
        }
        
        private static void cleanupData()
        {
            CacheEntry e;
            while((e = (CacheEntry)queue.poll()) != null)
            {
                e.dispose();
            }
        }
        
        private static class CacheEntry extends SoftReference<Texture>
        {
            private String cacheKey;
            private int textureID;
            
            public CacheEntry( String cacheKey, Texture referent)
            {
                super(referent, queue);
                this.cacheKey = cacheKey;
                this.textureID = referent.textureID;
            }
            
            public void dispose()
            {
                clear();
                data.remove(cacheKey);
                GL11.glDeleteTextures(textureID);
                System.out.println("[TextureCache] Texture " + textureID + " (key=" + cacheKey + ") wurde aus dem Cache entfernt.");
            }
        }
    }
    
    public synchronized static TextureLoader getInstance()
    {
        if(instance == null)
        {
            instance = new TextureLoader();
            // System.out.println("new instance " + instance);
        }
        // else System.out.println("access instance: " + instance);
        return instance;
    }
}