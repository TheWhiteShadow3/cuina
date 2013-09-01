package cuina.world;

import cuina.graphics.GraphicContainer;

import java.io.Serializable;
import java.util.Set;

public interface CuinaWorld extends Serializable
{
	public static final String INSTANCE_KEY = "World";

	public Set<Integer> getObjectKeys();
	public int 			getObjectCount();
	
	public int 			addObject(CuinaObject obj);
	public CuinaObject	getObject(int id);
	public void 		removeObject(CuinaObject object);
	public void 		removeObject(int id);

	public int getScrollX();
	public void setScrollX(int scrollX);
	public int getScrollY();
	public void setScrollY(int scrollY);

	public void 	update();
	public void 	dispose();
	public boolean 	isFreezed();
	public void 	setFreeze(boolean value);
	
	public int getWidth();
	public int getHeight();

	public GraphicContainer getGraphicContainer();
//	public void load(String key);
}
