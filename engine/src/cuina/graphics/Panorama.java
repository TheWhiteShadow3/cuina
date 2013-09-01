/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.awt.image.BufferedImage;

public class Panorama extends Sprite
{
	private static final long serialVersionUID = -4450304616595613882L;
	
	private static final String PANORAMA_CONTAINER_KEY = "cuina.panoramas";
	private static final Transform2D matrix = new Transform2D();
	
	private String fileName	= null;
	
	private float 	scrollFactor = 1/4f;
	private int 	speedX = 0;
	private int 	speedY = 0;
	
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

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public int getSpeedX()
	{
		return speedX;
	}

	public void setSpeedX(int speedX)
	{
		this.speedX = speedX;
	}

	public int getSpeedY()
	{
		return speedY;
	}

	public void setSpeedY(int speedY)
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
				setImage( new Image(TextureLoader.getInstance().getFilledTexture(image)) );
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
	public void draw()
	{
		if (image == null) refresh();
		
		if (!visible || image == null) return;
		
		this.x -= speedX;
		this.y -= speedY;
		
		float width  = image.getWidth()  * zoomX;
		float height = image.getHeight() * zoomY;
		float realX  = ((this.x + ox) * scrollFactor) / width;
		float realY  = ((this.y + oy) * scrollFactor) / height;
		
//		float width  = image.getWidth()  * zoomX;
//		float height = image.getHeight() * zoomY;
//		float realX  = (this.x - ox) / width;
//		float realY  = (this.y - oy) / height;
		matrix.clear();
		matrix.setView(realX, realY);
		
		matrix.expand(Graphics.getWidth()  / (float)image.getWidth(),
					  Graphics.getHeight() / (float)image.getHeight(), zoomX, zoomY);
//		matrix.stretch(1 / zoomX, 1 / zoomY);
//				realX + Graphics.getWidth() / width,
//				realY + Graphics.getHeight() / height);

		image.draw(matrix);
		
//		GL11.glMatrixMode(GL11.GL_TEXTURE);
//		GL11.glLoadIdentity();
//		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}
}
