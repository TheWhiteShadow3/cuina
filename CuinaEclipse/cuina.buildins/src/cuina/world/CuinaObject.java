package cuina.world;


import cuina.event.Event;
import cuina.event.Trigger;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public interface CuinaObject extends Serializable
{
	public void		setID(int id);
	public int 		getID();
	
	public String 	getName();
	
	public float 	getX();
	public void 	setX(float x);
	
	public float 	getY();
	public void 	setY(float y);
	
	public float 	getZ();
	public void 	setZ(float z);
	
	public void 	addTrigger(Trigger trigger);
	public boolean 	removeTrigger(Trigger trigger);
	public List<Trigger> getTriggers();
	
	public Set<String> 	getExtensionKeys();
	public void 	addExtension(String key, Object ext);
	public Object 	getExtension(String key);
	
	public void 	update();
	public void 	postUpdate();
	public void 	dispose();
	public boolean 	exists();
	public boolean 	isPersistent();
	
	public void testTriggers(Event event, Object eventArg, Object... callArgs);
}
