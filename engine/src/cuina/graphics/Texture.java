/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;


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
	/** The GL target type */
	int target;

	/** The GL texture ID */
	int textureID;

	/** The width of the texture */
	private int texWidth;
	
	/** The height of the texture */
	private int texHeight;

	/** The width of the image */
	private int srcWidth;

	/** The height of the image */
	private int srcHeight;

	/** Anzahl der Images, die die Textur verwenden. */
	private int useCount = 0;
	
	/**
	 * Erstellt eine neue Textur.
	 * 
	 * @param target
	 *            The GL target
	 * @param textureID
	 *            The GL texture ID
	 */
    protected Texture(int target, int textureID, int width, int height)
    {
        this.target = target;
        this.textureID = textureID;
        this.srcWidth  = width;
        this.srcHeight = height;
        this.texWidth  = TextureLoader.get2Fold(width);
        this.texHeight = TextureLoader.get2Fold(height);
    }

	/**
	 * Bindet die Textur in den GL-Kontext.
	 */
	public void bind()
	{
		GLCache.bindTexture(this);
	}

	public static void unbind()
	{
		GLCache.bindTexture(null);
	}
	
	/**
	 * Gibt die Breite der physikalischen Textur zurück.
	 * 
	 * @return Breite der physikalischen Textur.
	 */
	public int getWidth()
	{
		return texWidth;
	}

	/**
	 * Gibt die Höhe der physikalischen Textur zurück.
	 * 
	 * @return Höhe der physikalischen Textur.
	 */
	public int getHeight()
	{
		return texHeight;
	}

	/**
	 * Gibt die Breite der Bilddatei zurück.
	 * Wenn es sich um eine generierte Textur handelt, entspricht das Ergebnis immer {@link #getWidth()}
	 * @return Breite der Bilddatei.
	 */
	public int getSourceWidth()
	{
		return srcWidth;
	}
	
	/**
	 * Gibt die Höhe der Bilddatei zurück.
	 * Wenn es sich um eine generierte Textur handelt, entspricht das Ergebnis immer {@link #getHeight()}
	 * @return Höhe der Bilddatei.
	 */
	public int getSourceHeight()
	{
		return srcHeight;
	}
	
	/** Gibt die ID der Textur im Speicher zurück. */
	protected int getTextureID()
	{
		return textureID;
	}

	/**
	 * Gibt die Anzahl der Sprites, die die Textur verwenden zurück.
	 */
	protected int getUseCount()
	{
		return useCount;
	}

	/**
	 * Erhöht die Anzahl der Sprites, die die Textur verwenden.
	 */
	protected void addUseCount()
	{
		this.useCount++;
	}

	/**
	 * Vermindert die Anzahl der Sprites, die die Textur verwenden.
	 */
	protected void removeUseCount()
	{
		this.useCount--;
	}
}
