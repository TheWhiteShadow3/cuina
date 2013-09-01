package session;

import cuina.graphics.GraphicSet;
import cuina.graphics.Graphics;
import cuina.graphics.PictureSprite;
import cuina.plugin.LifeCycleAdapter;
import cuina.util.LoadingException;

import java.io.Serializable;

import junit.framework.Assert;

@SuppressWarnings("serial")
public class SessionObject extends LifeCycleAdapter implements Serializable
{
	private PictureSprite graphic1; // Wird als harte Referenz gebraucht.
	private PictureSprite graphic2; // Wird als harte Referenz gebraucht.
	public final static GraphicSet SESSION_SET = new GraphicSet("SessionSet", 0, Graphics.GraphicManager);
	
	@Override
	public void init()
	{
		System.out.println("Erstelle Session Grafik");
		try
		{
			graphic1 = new PictureSprite("pictures/CE_Icon128.png", SESSION_SET);
			graphic2 = new PictureSprite("pictures/CE_Icon128.png", Graphics.GraphicManager);
		}
		catch (LoadingException e)
		{
			Assert.fail(e.getMessage());
		}
	}
	
	@Override
	public void postUpdate()
	{
		System.out.println("teste Session-Grafik");
		Assert.assertTrue("Session Grafik 1 ist null!", SESSION_SET.toList().contains(graphic1));
		Assert.assertTrue("Session Grafik 2 ist null!", Graphics.GraphicManager.toList().contains(graphic2));
	}

	@Override
	public void dispose()
	{
		graphic1.dispose();
		graphic1 = null;
		
		graphic2.dispose();
		graphic2 = null;
	}
}
