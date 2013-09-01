package cuina.gl;

import cuina.resource.ResourceException;

import java.awt.image.BufferedImage;

import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;

public class Image
{
	private Texture texture;
	private boolean disposed;
	
	public Image(Texture texture)
	{
		this.texture = texture;
	}
	
	public Image(GLCanvas context, String fileName) throws ResourceException, LWJGLException
	{
		this.texture = TextureLoader.getTexture(context, fileName);
	}
	
	public Image(GLCanvas context, BufferedImage image) throws LWJGLException
	{
		this.texture = TextureLoader.getTexture(context, image, GL11.GL_TEXTURE_2D);
	}

	public int getHeight()
	{
		if (texture != null) return texture.getImageHeight();
		return -1;
	}

	public int getWidth()
	{
		if (texture != null) return texture.getImageWidth();
		return -1;
	}

	public Texture getTexture()
	{
		return texture;
	}

	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	public boolean isDisposed()
	{
		return disposed;
	}

	public void dispose()
	{
		if (texture != null)
		{
			texture.dispose();
			texture = null;
		}
		disposed = true;
	}
}
