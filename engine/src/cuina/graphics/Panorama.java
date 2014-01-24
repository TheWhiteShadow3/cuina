/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import cuina.graphics.transform.Transformation;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.awt.image.BufferedImage;

public class Panorama extends Sprite
{
	private static final long serialVersionUID = -4450304616595613882L;
	
	private static final String PANORAMA_CONTAINER_KEY = "cuina.panoramas";
	
	private String fileName	= null;
	
	private float 	scrollFactor = 1/4f;
	private float 	speedX = 0;
	private float 	speedY = 0;
	
	public static GraphicContainer getPanoramaContainer()
	{
		GraphicContainer container = Graphics.GraphicManager.getContainer(PANORAMA_CONTAINER_KEY);
		
		if (container == null)
		{
			container = new GraphicSet(PANORAMA_CONTAINER_KEY, -1000, Graphics.GraphicManager);
		}
		return container;
	}
	
	/**
	 * Erstellt ein neues Panorama aus der übergebenen Bilddatei.
	 * Das Bild sollte eine 2er-Potenz als Größe haben.
	 * @param fileName
	 */
	public Panorama(String fileName)
	{
		this(fileName, getPanoramaContainer());
	}
	
	public Panorama(String fileName, GraphicContainer container)
	{
		super(null, container);
		this.fileName = fileName;
	}

	public float getScrollFactor()
	{
		return scrollFactor;
	}

	public void setScrollFactor(float scrollFactor)
	{
		this.scrollFactor = scrollFactor;
	}
	
	public float getSpeedX()
	{
		return speedX;
	}

	public void setSpeedX(float speedX)
	{
		this.speedX = speedX;
	}

	public float getSpeedY()
	{
		return speedY;
	}

	public void setSpeedY(float speedY)
	{
		this.speedY = speedY;
	}

	@Override
	public void refresh()
	{
		if (fileName != null)
		{
			try
			{
				BufferedImage image = ResourceManager.loadImage(fileName);
				setImage( new Image(TextureLoader.getInstance().getFilledTexture(image, 0)) );
			}
			catch (LoadingException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setOffset(int ox, int oy)
	{
		setOX(ox);
		setOY(oy);
	}

	@Override
	protected void transformAndRender(Transformation matrix)
	{
		this.x -= speedX;
		this.y -= speedY;
		
		View view = Graphics.getCurrentView();
		float width  = image.getWidth()  * zoomX;
		float height = image.getHeight() * zoomY;
		float realX  = ((this.x + view.x) * scrollFactor) / width;
		float realY  = ((this.y + view.y) * scrollFactor) / height;
		
		Image.IMAGE_MATRIX.clear();
		Image.IMAGE_MATRIX.x = view.x;
		Image.IMAGE_MATRIX.y = view.y;
		Image.IMAGE_MATRIX.setTexturPosition(realX, realY);
		
		Image.IMAGE_MATRIX.expand(Graphics.getWidth()  / (float)image.getWidth(),
								  Graphics.getHeight() / (float)image.getHeight(), zoomX, zoomY);
		
		super.transformAndRender(Image.IMAGE_MATRIX);
	}

	@Override
	public String toString()
	{
		return "Panorama [fileName=" + fileName + ", speedX=" + speedX + ", speedY=" + speedY + "]";
	}
}
