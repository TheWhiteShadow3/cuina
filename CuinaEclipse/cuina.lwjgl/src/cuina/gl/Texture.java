/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.gl;

import java.nio.ByteBuffer;

import org.eclipse.swt.opengl.GLCanvas;
import org.lwjgl.opengl.GL11;

/**
 * A texture to be bound within OpenGL. This object is responsible for keeping
 * track of a given OpenGL texture and for calculating the texturing mapping
 * coordinates of the full image.
 * Since textures need to be powers of 2 the actual texture may be considerably
 * bigged that the source image and hence the texture mapping coordinates need
 * to be adjusted to matchup drawing the sprite against the texture.
 */
public final class Texture
{
	private static Texture currentBind;

	final GLCanvas context;
	ByteBuffer buffer;
	int pixelFormat;
	
	/** The GL target type */
	int target;

	/** The GL texture ID */
	int textureID = -1;

	/** The height of the image */
	int height;

	/** The width of the image */
	int width;

	/** The width of the texture */
	int texWidth;

	/** The height of the texture */
	int texHeight;
	
	/**
	 * Create a new texture
	 * 
	 * @param target
	 *            The GL target
	 * @param textureID
	 *            The GL texture ID
	 */
	Texture(GLCanvas context)
	{
		this.context = context;
	}
	
	Texture(GLCanvas context, Texture source)
	{
		this(context);
		buffer = source.buffer;
		target = source.target;
		width = source.width;
		height = source.height;
		texWidth = source.texWidth;
		texHeight = source.texHeight;
		pixelFormat = source.pixelFormat;
	}

	/**
	 * Bindet die Textur in den GL-Kontext.
	 */
	public void bind()
	{
		if (!GL11.glIsTexture(textureID)) throw new IllegalArgumentException("invalid context.");
		if (currentBind == this) return;
		currentBind = this;
		GL11.glBindTexture(target, textureID);
	}
//
//	public static void unbind()
//	{
//		if (currentBind == null) return;
//		currentBind = null;
//		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
//	}

	/**
	 * Get the height of the original image
	 * 
	 * @return The height of the original image
	 */
	public int getImageHeight()
	{
		return height;
	}

	/**
	 * Get the width of the original image
	 * 
	 * @return The width of the original image
	 */
	public int getImageWidth()
	{
		return width;
	}

	/**
	 * Get the height of the physical texture
	 * 
	 * @return The height of physical texture
	 */
	public int getTextureHeight()
	{
		return texHeight;
	}

	/**
	 * Get the width of the physical texture
	 * 
	 * @return The width of physical texture
	 */
	public int getTextureWidth()
	{
		return texWidth;
	}
	
	public float getHorizontalAspect()
	{
		return texWidth / (float)width;
	}
	
	public float getVerticalAspect()
	{
		return texHeight / (float)height;
	}
	
	public void dispose()
	{
		GL11.glDeleteTextures(textureID);
		buffer = null;
	}
}
