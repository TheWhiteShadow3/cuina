package cuina.rpg;

import cuina.map.GameMap;
import cuina.object.BaseObject;
import cuina.world.CuinaModel;

import java.awt.Font;

public class DamagePop extends BaseObject
{
	private static final long serialVersionUID = 1L;

	public static Font damageFont = new Font("Arial", 1, 20);
	
	private int duration = 150;
	private float vx = 0;
	private float vy = -10;
	
	private BaseObject object;
	private DamagePopModel model;
	
	public DamagePop(BaseObject object, int value)
	{
		this.object = object;
//		model = new DamagePopModel(String.valueOf(value));
		addExtension("Model", model);
		GameMap.getInstance().addObject(this);
	}

	public DamagePopModel getModel()
	{
		return model;
	}

	@Override
	public void update()
	{
		duration--;
		if (duration <= 0)
		{
//			model.dispose();
			GameMap.getInstance().removeObject(this);
		}
	}
	
	public class DamagePopModel// extends TextSprite implements CuinaModel
	{
//		private static final long serialVersionUID = 1L;
//		
//		private float xx = 0;
//		private float yy = 0;
//		
//		public DamagePopModel(String text)
//		{
//			super(damageFont, true, 4);
//			setText(text);
//		}
//
//		@Override
//		public void update()
//		{
//			GameMap map = GameMap.getInstance();
//			if (map == null) return;
//			
//			xx += vx;
//			yy += vy;
//			
//			if (yy >= 0)
//			{
//				vy = -vy * 0.85f;
//			}
//			else
//			{
//				vy++;
//			}
//
//			// Bild-Position (Seperat um Abweichung zu ermöglichen)
//			this.x = object.getMapX() + xx - getTextWidth() / 2;
//			this.y = object.getMapY() + yy;
//			setZ(2000);
//		}
//
//		@Override
//		public void setPosition(float x, float y, float z)
//		{
//			this.xx = x;
//			this.yy = y;
//		}
//
//		@Override
//		public int getWidth()
//		{
//			return super.getTextWidth();
//		}
//
//		@Override
//		public int getHeight()
//		{
//			return super.getHeight();
//		}
//
//
//		public float getZ()
//		{
//			return 0;
//		}
	}
}
