package cuina.ks;

import cuina.graphics.PictureSprite;
import cuina.graphics.Sprite;
import cuina.util.LoadingException;

/**
 * Stellt eine Kampfarena da.
 * @author TheWhiteShadow
 */
public class BattleGround
{
	private String backgroundName;
	
	private transient Sprite background;
	
	public BattleGround(BattleGroundData srcGround)
	{
		backgroundName = srcGround.backgroundName;
		try
		{
			background = new PictureSprite(backgroundName);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}
	
	public void refresh()
	{
		background.refresh();
	}
	
	public void dispose()
	{
		background.dispose();
	}
}
