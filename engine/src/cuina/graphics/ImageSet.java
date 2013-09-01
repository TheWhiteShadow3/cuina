/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import cuina.Logger;
import cuina.util.LoadingException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Ein Set von Bildern, welche Teile von einem großen Bild darstellen und Methoden bereitstellt,
 * diese unabhängig voneinander anzuzeigen.
 * <p>
 * Das Set selbst stellt kein <code>GraphicElement</code> da.
 * Allerding können Sprites generiert werden, welche seperat benutzt werden können.
 * Die Sprites sind weiterhin mit dem Set verknüpft und reagieren auf die Methoden refresh und dispose.
 * Wird außerdem die refresh-methode eines der Sprites aufgerufen wird, wird zunächst geprüft, 
 * ob das Image des Sets vorhanden ist und im Falle, dass es null ist, neu geladen.
 * </p>
 * @author TheWhiteShadow
 */
public class ImageSet implements Serializable
{
	private static final long	serialVersionUID	= 7606159540168953608L;
	
	private String fileName;
//	transient private BufferedImage rawImage;
	transient private Image[][] images;
	private int xCount;
	private int yCount;
	private final ArrayList<ImageSetSprite> sprites = new ArrayList<ImageSetSprite>();

	private transient Texture texture;
	
	/**
	 * Erstellt ein ImageSet aus dem Dateinamen und der angegebenen Anzahl
	 * an Bildern pro Zeile und Spalte.
	 * @param fileName Dateiname aus dem die Bitmaps erstellt werden sollen.
	 * @param xCount Bildanzahl in einer Zeile.
	 * @param yCount Bildanzahl in einer Spalte.
	 * @throws LoadingException 
	 */
	public ImageSet(String fileName, int xCount, int yCount) throws LoadingException
	{
		if (fileName == null || xCount <= 0 || yCount <= 0)
			throw new IllegalArgumentException();
		
		this.fileName = fileName;
		this.xCount = xCount;
		this.yCount = yCount;
		createImages(TextureLoader.getInstance().getTexture(fileName));
	}
	
	/**
	 * Erstellt ein ImageSet aus einer Texture und der angegebenen Anzahl
	 * an Bildern pro Zeile und Spalte.
	 * @param texture Die Textur.
	 * @param xCount Bildanzahl in einer Zeile.
	 * @param yCount Bildanzahl in einer Spalte.
	 * @throws LoadingException 
	 */
	public ImageSet(Texture texture, int xCount, int yCount) throws LoadingException
	{
		if (texture == null || xCount <= 0 || yCount <= 0)
			throw new IllegalArgumentException();
		
		this.xCount = xCount;
		this.yCount = yCount;
		createImages(texture);
	}
	
	protected ImageSet(int xCount, int yCount)
	{
		if (xCount <= 0 || yCount <= 0)
			throw new IllegalArgumentException();
		
		this.xCount = xCount;
		this.yCount = yCount;
	}

	protected void createImages(Texture texture)
	{
		this.texture = texture;
		int cw = texture.getSourceWidth() / xCount;
		int ch = texture.getSourceHeight() / yCount;
		images = new Image[xCount][yCount];
		for(int x=0; x < xCount; x++)
			for(int y=0; y < yCount; y++)
			{
				images[x][y] = new Image(texture, x * cw, y * ch, cw, ch);
			}
	}
	
	/**
	 * Die Refresh-Methode dient lediglich der Vollständigkeits halber
	 * und muss nicht manuell aufgerufen werden.
	 */
	public void refresh()
	{
		if (fileName != null)
		{
			try
			{
				createImages(TextureLoader.getInstance().getTexture(fileName));
				for(ImageSetSprite sprite : sprites)
				{
					sprite.refresh();
				}
			}
			catch (LoadingException e)
			{
				Logger.log(ImageSet.class, Logger.ERROR, e);
			}
		}
	}
	
//	/**
//	 * Gibt das dem Set zugrunde liegende Image zurück.
//	 * @return Set-Image
//	 */
//	protected BufferedImage getRawImage()
//	{
//		return rawImage;
//	}
	
	public int getWidth()
	{
		return texture.getSourceWidth();
	}
	
	public int getHeight()
	{
		return texture.getSourceHeight();
	}
	
	public Texture getTexture()
	{
		return texture;
	}

	/**
	 * Gibt die Anzahl der Images in X-Richtung zurück.
	 * @return Image-Anzahl in X-Richtung.
	 */
	public int getXCount()
	{
		return xCount;
	}
	
	/**
	 * Gibt die Anzahl der Images in Y-Richtung zurück.
	 * @return Image-Anzahl in Y-Richtung.
	 */
	public int getYCount()
	{
		return yCount;
	}
	
	/**
	 * Gibt das Image zu den angegebenen Feld-Koordinaten des Sets zurück.
	 * @param x X-Position.
	 * @param y Y-Position.
	 * @return Image auf der Position (x, y).
	 */
	public Image getImage(int x, int y)
	{
		return images[x][y];
	}
	
	/**
	 * Gibt ein Sprite zurück, welches als Image den Bildausschnitt
	 * mit den angegebenen Koordinaten besitzt.
	 * @param x X-Position.
	 * @param y Y-Position.
	 * @return Sprite mit dem Image zu der Position (x, y).
	 */
	public Sprite createSprite(int x, int y)
	{
		return createSprite(x, y, Graphics.GraphicManager);
	}
	
	/**
	 * Gibt ein Sprite zurück, welches als Image den Bildausschnitt
	 * mit den angegebenen Koordinaten besitzt.
	 * @param x X-Position.
	 * @param y Y-Position.
	 * @return Sprite mit dem Image zu der Position (x, y).
	 */
	public Sprite createSprite(int x, int y, GraphicContainer container)
	{
		ImageSetSprite sprite = new ImageSetSprite(this, x, y, container);
		sprites.add(sprite);
		return sprite;
	}
	
	public void dispose()
	{
		for (int i = sprites.size()-1; i >= 0; i--)
		{
			sprites.get(i).dispose();
		}
		sprites.clear(); // Sollte bereits der Fall sein.
		for(int x=0; x < xCount; x++)
			for(int y=0; y < yCount; y++)
			{
				images[x][y].dispose();
				images[x][y] = null;
			}
		images = null;
		texture = null;
	}
	
	public boolean disposed()
	{
		return texture == null;
	}
	
	private class ImageSetSprite extends Sprite
	{
		private static final long	serialVersionUID	= 3213337083050584786L;
		
		ImageSet set;
		private int sx;
		private int sy;
		
		public ImageSetSprite(ImageSet set, int x, int y, GraphicContainer container)
		{
			super(set.getImage(x, y), container);
			this.set = set;
			this.sx = x;
			this.sy = y;
		}

		@Override
		public void refresh()
		{
			if (set.images == null) set.refresh();
			if (set.images == null) return;
			setImage(set.getImage(sx, sy));
		}

		@Override
		public void dispose()
		{
			super.dispose();
			set.sprites.remove(this);
		}
	}
}
