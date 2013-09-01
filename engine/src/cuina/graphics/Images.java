package cuina.graphics;

import cuina.util.LoadingException;

public class Images
{
	private Images() {}
	
	public static Image createImage(String fileName) throws LoadingException
	{
		return new Image(TextureLoader.getInstance().getTexture(fileName));
	}
	
	public static Image createImage(String fileName, int x, int y, int width, int height) throws LoadingException
	{
		return new Image(TextureLoader.getInstance().getTexture(fileName), x, y, width, height);
	}
	
	public static Image createImage(int width, int height)
	{
		return new Image(TextureLoader.getInstance().getTexture(width, height));
	}
}
