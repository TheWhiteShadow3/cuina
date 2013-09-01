package cuina.animation;


public class ModelGroup<T extends ModelIF>// implements ModelIF
{
//	private static final long serialVersionUID = -9211099124080086776L;
//	
//	private final ArrayList<T> models = new ArrayList<T>(4);
//	private T	model;
//	private int localIndex = 0;
//	private int x;
//	private int y;
//	private int z;
//	
//	public void addModel(T model)
//	{
//		models.add(model);
//	}
//	
//	public void removeModel(T model)
//	{
//		models.remove(model);
//	}
//	
//	public T getModel(int index)
//	{
//		return models.get(index);
//	}
//	
//	@Override
//	public void refresh()
//	{
//		for(ModelIF m : models)
//		{
//			m.refresh();
//		}
//	}
//
//	@Override
//	public void dispose()
//	{
//		for(ModelIF m : models)
//		{
//			m.dispose();
//		}
//	}
//
//	@Override
//	public void setVisible(boolean value)
//	{
//		for(ModelIF m : models)
//		{
//			m.setVisible(false);
//		}
//		model.setVisible(value);
//	}
//
//	@Override
//	public boolean isVisible()
//	{
//		return model.isVisible();
//	}
//
//	@Override
//	public void setPosition(float x, float y, float z)
//	{
//		for(ModelIF m : models)
//		{
//			m.setPosition(x, y, z);
//		}
//	}
//
////	@Override
////	public void update()
////	{
////		model.update();
////	}
//
//	@Override
//	public void updatePosition()
//	{
//		for(ModelIF m : models)
//		{
//			m.updatePosition();
//		}
//	}
//
//	@Override
//	public int getFrames()
//	{
//		return model.getFrames();
//	}
//
//	@Override
//	public int getAnimations()
//	{
//		int n = 0;
//		for(ModelIF m : models)
//		{
//			n += m.getAnimations();
//		}
//		return n;
//	}
//
//	@Override
//	public void setFrame(int frame)
//	{
//		model.setFrame(frame);
//	}
//
//	@Override
//	public void setAnimationIndex(int index)
//	{
//		for(T m : models)
//		{
//			if (index < m.getAnimations())
//			{
//				model = m;
//				model.setAnimationIndex(index);
//				break;
//			}
//			index -= m.getAnimations();
//			if (index < 0) return;
//		}
//		this.localIndex = index;
//	}
//
//	@Override
//	public Sprite getSprite()
//	{
//		return model.getSprite();
//	}
//
//	@Override
//	public boolean isStandAnimation()
//	{
//		return model.isStandAnimation();
//	}
//
//	@Override
//	public void setStandAnimation(boolean standAni)
//	{
//		model.setStandAnimation(standAni);
//	}
//
//	@Override
//	public void forceAnimation(int index)
//	{
//		setAnimationIndex(index);
//		model.forceAnimation(localIndex);
//	}
//
//	@Override
//	public void forceAnimation(int index, int nextAni)
//	{
//		setAnimationIndex(index);
//		model.forceAnimation(localIndex);
//	}
//
//	@Override
//	public void setOffset(float ox, float oy)
//	{
//		model.setOffset(ox, oy);
//	}
//
//	@Override
//	public void update(float dx, float dy)
//	{
//		model.update(dx, dy);
//	}
//
//	@Override
//	public float getZ()
//	{
//		return z;
//	}
//
//	@Override
//	public float getX()
//	{
//		return x;
//	}
//
//	@Override
//	public float getY()
//	{
//		return y;
//	}
//	
//	public T getCurrentModel()
//	{
//		return model;
//	}
//
//	@Override
//	public int getWidth()
//	{
//		return model.getWidth();
//	}
//
//	@Override
//	public int getHeight()
//	{
//		return model.getHeight();
//	}
//
//	@Override
//	public float getOX()
//	{
//		return model.getOX();
//	}
//
//	@Override
//	public float getOY()
//	{
//		return model.getOY();
//	}
}
