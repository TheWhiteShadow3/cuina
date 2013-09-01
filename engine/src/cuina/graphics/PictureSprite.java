package cuina.graphics;

import cuina.util.LoadingException;

import java.awt.Rectangle;

public class PictureSprite extends Sprite
{
	private static final long	serialVersionUID	= 2560146525182670076L;
	
	private String fileName	= null;
	private Rectangle rect;
	
	public PictureSprite(String fileName) throws LoadingException
	{
		this(fileName, Graphics.GraphicManager);
	}
	
	public PictureSprite(String fileName, int x, int y, int width, int height) throws LoadingException
	{
		this(fileName, x, y, width, height, Graphics.GraphicManager);
	}
	
	public PictureSprite(String fileName, GraphicContainer container) throws LoadingException
	{
		super(Images.createImage(fileName), container);
		this.rect = getImage().getRectangle();
		this.fileName = fileName;
	}
	
	public PictureSprite(String fileName, int x, int y, int width, int height, GraphicContainer container)
			throws LoadingException
	{
		super(Images.createImage(fileName, x, y, width, height), container);
		this.rect = getImage().getRectangle();
		this.fileName = fileName;
	}
	
	@Override
	public void refresh()
	{
		if (fileName != null)
		{
			try
			{
				Image img = Images.createImage(fileName, rect.x, rect.y, rect.width, rect.height);
				this.rect = img.getRectangle();
				setImage(img);
			}
			catch (LoadingException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return the filename
	 */
	public String getFilename()
	{
		return fileName;
	}
}
