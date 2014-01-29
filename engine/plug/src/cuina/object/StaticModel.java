package cuina.object;

import cuina.graphics.PictureSprite;
import cuina.graphics.Sprite;
import cuina.util.LoadingException;
import cuina.world.CuinaModel;

public class StaticModel implements CuinaModel
{
	private static final long serialVersionUID = 905794968107081868L;
	
	private Sprite sprite;

	public StaticModel(String imageName)
	{
		try
		{
			this.sprite = new PictureSprite(imageName);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void refresh()
	{
		sprite.refresh();
	}

	@Override
	public void setPosition(float x, float y, float z)
	{
		sprite.setX(x);
		sprite.setY(y);
		sprite.setDepth((int) z);
	}

	@Override
	public void dispose()
	{
		sprite.dispose();
	}

	@Override
	public void setVisible(boolean value)
	{
		sprite.setVisible(value);
	}

	@Override
	public boolean isVisible()
	{
		return sprite.isVisible();
	}

	@Override
	public float getZ()
	{
		return sprite.getDepth();
	}

	@Override
	public float getX()
	{
		return sprite.getX();
	}

	@Override
	public float getY()
	{
		return sprite.getY();
	}

	@Override
	public int getWidth()
	{
		return sprite.getWidth();
	}

	@Override
	public int getHeight()
	{
		return sprite.getHeight();
	}

	@Override
	public void update() { /* Model ist statisch. */ }
}
