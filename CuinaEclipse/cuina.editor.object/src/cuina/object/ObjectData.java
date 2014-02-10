package cuina.object;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import cuina.database.KeyReference;
import cuina.event.Trigger;

public class ObjectData implements Serializable
{
	private static final long	serialVersionUID	= -1857109292401703570L;
	
	public int id;
	public String name;
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public Map<String, Object> extensions;
	public ArrayList<Trigger> triggers;
	@KeyReference(name="Template")
	public String templateKey = null; // Muss ein Key enthalten oder null sein!
}