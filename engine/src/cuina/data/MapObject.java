/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.data;

import cuina.event.Trigger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MapObject implements Serializable, NamedItem
{
	private static final long	serialVersionUID	= -1857109292401703570L;
	
	public int id;
	public String name;
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public CMask cMask = null;
	public Model model = null;
	public Motor motor = null;
	/** Hier sollen zukünftig Objekt-Extensions rein */
	public HashMap<String, Object> extensions;
	public ArrayList<Trigger> triggers;
	public String templateName = null;
	
	@Override
	public String getName()
	{
		return name;
	}
}