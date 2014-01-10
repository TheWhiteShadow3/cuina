package cuina.input;

import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;

public class DirectionalControl extends Control
{
	public static final DirectionalSource WASD 	 = new KeyQuad(Key.KEY_A, Key.KEY_D, Key.KEY_W, Key.KEY_A);
	public static final DirectionalSource ARROWS = new KeyQuad(Key.KEY_LEFT, Key.KEY_RIGHT, Key.KEY_UP, Key.KEY_DOWN);
	
	private float x;
	private float y;
	
	public DirectionalControl(String name, DirectionalSource... keys)
	{
		super(name, keys);
	}
	
	public void remap(DirectionalSource... keys)
	{
		super.remap(keys);
	}
	
	public float getX()
	{
		return x;
	}
	
	public float getY()
	{
		return y;
	}
	
	public float getValue()
	{
		return (float) Math.hypot(getX(), getY());
	}
	
	public float getDirection()
	{
		float dx = getX();
		float dy = getY();
		
		if (dx == 0 || dy == 0)
		{	// In dem Fall brauchen wir nicht rechnen
			if (dx == 0 && dy == 0) return -1;
			if (dx == 0)
				return dy > 0 ? 270 : 90;
			else
				return dx > 0 ? 0 : 180;
		}
		
		if (dy > 0)
			return (float) (360 - Math.acos(dx / Math.hypot(dx, dy)) * 180 / Math.PI);
		else
			return (float) (Math.acos(dx / Math.hypot(dx, dy)) * 180 / Math.PI);
	}

	@Override
	void update()
	{
		super.update();
		
		float nx = 0;
		float ny = 0;
		for (Source s : keys)
		{
			nx += ((DirectionalSource) s).getX();
			ny += ((DirectionalSource) s).getY();
		}
		this.x = Math.min(Math.max(-1, nx), 1);
		this.y = Math.min(Math.max(-1, ny), 1);
	}

	public static interface DirectionalSource extends Source
	{
		public abstract float getX();
		public abstract float getY();
	}
	
	public static class DirectionalButton implements DirectionalSource
	{
		private static final long serialVersionUID = 5723904115874108L;
		
		private int target;
		private int xAxis;
		private int yAxis;
		
		public DirectionalButton(int target, int xAxis, int yAxis)
		{
			this.target = target;
			this.xAxis = xAxis;
			this.yAxis = yAxis;
		}

		@Override
		public float getX()
		{
			if (!Controllers.isCreated()) return 0;
			
			return Controllers.getController(target).getAxisValue(xAxis);
		}

		@Override
		public float getY()
		{
			if (!Controllers.isCreated()) return 0;
			
			return Controllers.getController(target).getAxisValue(yAxis);
		}

		@Override
		public boolean isTriggered()
		{
			return Math.abs(getX()) + Math.abs(getY()) > 0.1f;
		}
	}
	
	private static class KeyQuad implements DirectionalSource
	{
		private static final long serialVersionUID = 2114170464618453658L;
		
		private int left;
		private int right;
		private int up;
		private int down;
		
		public KeyQuad(int left, int right, int up, int down)
		{
			this.left = left;
			this.right = right;
			this.up = up;
			this.down = down;
		}
		
		@Override
		public float getX()
		{
			return getAxisValue(left, right);
		}

		@Override
		public float getY()
		{
			return getAxisValue(up, down);
		}

		@Override
		public boolean isTriggered()
		{
			return Math.abs(getX()) + Math.abs(getY()) != 0;
		}
		
		private float getAxisValue(int neg, int pos)
		{
			if (!Keyboard.isCreated()) return 0;
			
			boolean isNeg = Keyboard.isKeyDown(neg);
			boolean isPos = Keyboard.isKeyDown(pos);
			if (isPos == isNeg) return 0;
			
			return isPos ? +1 : -1;
		}
	}
}
