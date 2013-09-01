package cuina.hud;

import org.lwjgl.util.ReadableColor;

public class HealthBar extends AbstractBar
{
	private static final long serialVersionUID = 3595401804869326735L;

	public HealthBar(int x, int y, int width, int height)
	{
		super(x, y, width, height, null);
	}

	public void setValue(double value)
	{
		setValue(value, null);
	}
	
	@Override
	public void setValue(double value, ReadableColor color)
	{
		if (color == null)
		{
			if (value < 50)
				if (value < 25)
					color = ReadableColor.RED;
				else
					color = ReadableColor.YELLOW;
			else
				color = ReadableColor.GREEN;
		}
		 
		super.setValue(value, color);
	}
}
