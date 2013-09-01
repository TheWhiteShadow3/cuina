package session;

import cuina.graphics.Graphics;
import cuina.graphics.PictureSprite;
import cuina.plugin.LifeCycleAdapter;
import cuina.util.LoadingException;

import junit.framework.Assert;

public class SceneObject extends LifeCycleAdapter
{
	private PictureSprite graphic;
	
	@Override
	public void init()
	{
		System.out.println("Erstelle Szenen Grafik");
		try
		{
			graphic = new PictureSprite("pictures/CE_Icon128.png");
		}
		catch (LoadingException e)
		{
			Assert.fail(e.getMessage());
		}
	}
	
	@Override
	public void postUpdate()
	{
		System.out.println("teste Szenen Grafik");
		Assert.assertTrue("Szenen Grafik ist null!", Graphics.GraphicManager.toList().contains(graphic));
	}

	@Override
	public void dispose()
	{
		graphic.dispose();
	}
}
