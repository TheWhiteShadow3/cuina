package cuina.object;


import cuina.event.Trigger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectData implements Serializable
{
	private static final long	serialVersionUID	= -1857109292401703570L;
	
	public int id;
	public String name;
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public HashMap<String, Object> extensions;
	public ArrayList<Trigger> triggers;
	public String templateKey = null; // Muss ein Key enthalten oder null sein!
}