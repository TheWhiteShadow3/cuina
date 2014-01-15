package cuina.gl;

import static org.lwjgl.opengl.GL11.*;

import cuina.resource.ResourceException;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GLContext;

/**
 * A utility class to load textures for OpenGL. This source is based on a
 * texture that can be found in the Java Gaming (www.javagaming.org) Wiki. It
 * has been simplified slightly for explicit 2D graphics use.
 * OpenGL uses a particular image format. Since the images that are loaded from
 * disk may not match this format this loader introduces a intermediate image
 * which the source image is copied into. In turn, this image is used as source
 * for the OpenGL texture.
 */
public class TextureLoader
{
	/** The color model including alpha for the GL image */
	private static ColorModel glAlphaColorModel;

	/** The color model for the GL image */
	private static ColorModel glColorModel;
	
	private static final IntBuffer TEMP_ID_BUFFER = BufferUtils.createIntBuffer(1);
	
	private static final HashMap<String, Texture> textureCache = new HashMap<String, Texture>();

	static
	{
		glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}

	/**
	 * Create a new texture ID
	 * 
	 * @return A new texture ID
	 */
	private static int createTextureID()
	{
		glGenTextures(TEMP_ID_BUFFER);
		return TEMP_ID_BUFFER.get(0);
	}

	public static Texture getTexture(GLCanvas context, String fileName) throws ResourceException, LWJGLException
	{
		if (fileName == null) throw new NullPointerException();
		
		Texture tex = textureCache.get(fileName);
		if (tex == null)
		{
			try
			{
				tex = getTexture(context, ImageIO.read(new File(fileName)), GL_TEXTURE_2D);
				textureCache.put(fileName, tex);
				return tex;
			}
			catch (IOException e)
			{
				throw new ResourceException(fileName, ResourceException.LOAD, e);
			}
		}
		else
		{
			if (context == tex.context) return tex;
			
			tex = new Texture(context, tex);
			createTexture(tex);
			return tex;
		}
	}

	/**
	 * Load a texture into OpenGL from a image reference on disk.
	 * 
	 * @param context
	 *            The location of the resource to load
	 * @param bufferedImage Das Image
	 * @param target
	 *            The GL target to load the texture against
	 * @return The loaded texture.
	 * @throws LWJGLException 
	 */
	public static Texture getTexture(GLCanvas context, BufferedImage bufferedImage, int target) throws LWJGLException
	{
		// System.out.println("Textur: " + textureID);
		Texture texture = new Texture(context);
		
		texture.width = bufferedImage.getWidth();
		texture.height = bufferedImage.getHeight();

		// convert that image into a byte buffer of texture data
		texture.buffer = convertImageData(bufferedImage, texture);
		texture.target = target;
		
		if (bufferedImage.getColorModel().hasAlpha())
			texture.pixelFormat = GL_RGBA;
		else
			texture.pixelFormat = GL_RGB;

		createTexture(texture);
		
		return texture;
	}
	
	public static void createTexture(Texture texture) throws LWJGLException
	{
		texture.context.setCurrent();
		GLContext.useContext(texture.context);
		// create the texture ID for this texture
		texture.textureID = createTextureID();
//		System.out.println("neue Textur: " + texture.textureID + " Target: " + texture.target + " Owner: " + texture.context.hashCode() + "; " + debug);
		
		glBindTexture(texture.target, texture.textureID);
		if (texture.target == GL_TEXTURE_2D)
		{
			glTexParameteri(texture.target, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(texture.target, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		}
		glTexImage2D(texture.target, 0, GL_RGBA, texture.texWidth, texture.texHeight, 0,
				texture.pixelFormat, GL_UNSIGNED_BYTE, texture.buffer);
	}
	
//	/**
//	 * Gibt eine Textur zurück, dessen Image-Größe auf die nächste 2er-Potenz scaliert ist.
//	 * @param bufferedImage
//	 * @return The loaded texture.
//	 */
//	public Texture getFilledTexture(BufferedImage bufferedImage)
//	{
//		int width  = bufferedImage.getWidth();
//		int height = bufferedImage.getHeight();
//		int foldWidth  = get2Fold(width);
//		int foldHeight = get2Fold(height);
//		
//		if (width != foldWidth || height != foldHeight)
//		{
//			BufferedImage scaledImage = new BufferedImage(foldWidth, foldHeight, BufferedImage.TYPE_INT_ARGB);
//			scaledImage.getGraphics().drawImage(bufferedImage, 0,0, foldWidth, foldHeight, null);
//			bufferedImage = scaledImage;
//		}
//		return getTexture(bufferedImage);
//	}

	/**
	 * Get the closest greater power of 2 to the fold number
	 * 
	 * @param fold
	 *            The target number
	 * @return The power of 2
	 */
	private static int get2Fold(int fold)
	{
		int ret = 2;
		while(ret < fold)
		{
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A buffer containing the data
	 */
	@SuppressWarnings("rawtypes")
	private static ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture)
	{
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		texWidth = get2Fold(bufferedImage.getWidth());
		texHeight = get2Fold(bufferedImage.getHeight());
//		while(texWidth < bufferedImage.getWidth())
//		{
//			texWidth *= 2;
//		}
//		while(texHeight < bufferedImage.getHeight())
//		{
//			texHeight *= 2;
//		}

		texture.texWidth = texWidth;
		texture.texHeight = texHeight;

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if(bufferedImage.getColorModel().hasAlpha())
		{
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable());
		}
		else
		{
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false, new Hashtable());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}
}
