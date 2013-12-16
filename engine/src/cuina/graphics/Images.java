package cuina.graphics;

import cuina.util.LoadingException;

public class Images
{
	private Images() {}
	
	public static Image createImage(String fileName) throws LoadingException
	{
		return createImage(fileName, 0);
	}
	
	public static Image createImage(String fileName, int flags) throws LoadingException
	{
		return new Image(TextureLoader.getInstance().getTexture(fileName, flags));
	}
	
	public static Image createImage(String fileName, int x, int y, int width, int height) throws LoadingException
	{
		return createImage(fileName, x, y, width, height, 0);
	}
	
	public static Image createImage(String fileName, int x, int y, int width, int height, int flags)
			throws LoadingException
	{
		return new Image(TextureLoader.getInstance().getTexture(fileName, flags), x, y, width, height);
	}
	
	public static Image createImage(int width, int height)
	{
		return createImage(width, height, 0);
	}
	
	public static Image createImage(int width, int height, int flags)
	{
		return new Image(TextureLoader.getInstance().getTexture(width, height, flags));
	}
}
