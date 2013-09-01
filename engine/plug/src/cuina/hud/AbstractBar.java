package cuina.hud;

import cuina.graphics.Image;
import cuina.graphics.Images;
import cuina.graphics.Sprite;

import org.lwjgl.util.ReadableColor;

@SuppressWarnings("serial")
public abstract class AbstractBar extends Sprite
{
	private int width;
	private int height;
	private double value;
	private double maxValue;
	private ReadableColor color;
	
	public AbstractBar(int x, int y, int width, int height, ReadableColor color)
	{
		this(x, y, width, height, color, 100, 100);
	}
	
	public AbstractBar(int x, int y, int width, int height, ReadableColor color, double value, double maxValue)
	{
		super(null);
		setX(x);
		setY(y);
		setDepth(1000);
		this.width = width;
		this.height = height;
		this.color = color;
		this.value = value;
		this.maxValue = maxValue;
		
		refresh();
	}
	
	@Override
	public void refresh()
	{
		setImage(Images.createImage(width, height));
		setValue(value, color);
	}
	
	public void plus(double value)
	{
		setValue(this.value + value, color);
	}
	
	public void minus(double value)
	{
		setValue(this.value - value, color);
	}
	
	public void setValue(double value, ReadableColor color)
	{
		this.value = Math.min(Math.max(0, value), maxValue);
		this.color = color;
		
		Image image = getImage();
		image.clear(Image.COLOR_TRANSPARENT);
		image.setColor(color);
		image.drawRect(0, 0, (int) (width * value / maxValue), height, true);
		image.setColor(ReadableColor.WHITE);
		image.drawRect(0, 0, width, height, false);
	}
	
	public double getValue()
	{
		return value;
	}

	public double getMaxValue()
	{
		return maxValue;
	}

	public void setMaxValue(double maxValue)
	{
		this.maxValue = maxValue;
		setValue(value, color);
	}

//	@Override
//	protected synchronized void draw()
//	{
//		Image image = getImage();
//		image.setColor(color);
//		image.drawRect(0, 0, (int) (width * value / maxValue), height, true);
//		image.setColor(Color.WHITE);
//		image.drawRect(0, 0, width, height, false);
//	}
}
