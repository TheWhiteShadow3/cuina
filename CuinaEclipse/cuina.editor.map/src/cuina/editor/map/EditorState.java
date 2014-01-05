package cuina.editor.map;

import java.util.HashMap;
import java.util.Map;

public class EditorState
{
	private final Map<String, Object> values = new HashMap<String, Object>();
	
	public EditorState()
	{
		
	}
	
	public void setObject(String name, Object value)
	{
		values.put(name, value);
	}
	
	public void setBoolean(String name, boolean value)
	{
		values.put(name, value);
	}
	
	public void setInt(String name, int value)
	{
		values.put(name, value);
	}
	
	public Object getObject(String name)
	{
		return values.get(name);
	}
	
	public boolean getBoolean(String name)
	{
		return (Boolean) values.get(name);
	}
	
	public int getInt(String name)
	{
		return (Integer) values.get(name);
	}
}
