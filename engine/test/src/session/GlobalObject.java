package session;

import cuina.graphics.Graphics;
import cuina.graphics.PictureSprite;
import cuina.plugin.LifeCycleAdapter;
import cuina.util.LoadingException;

import junit.framework.Assert;

public class GlobalObject extends LifeCycleAdapter
{
	private PictureSprite graphic;
	
	public GlobalObject()
	{
		System.out.println("Erstelle globale Grafik");
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
		System.out.println("teste globale Grafik");
		Assert.assertTrue("Globale Grafik ist null!", Graphics.GraphicManager.toList().contains(graphic));
	}

	@Override
	public void dispose()
	{
//		graphic.dispose();
//		graphic = null;
	}
}