package cuina.widget.data;

import cuina.database.DatabaseObject;

import java.util.ArrayList;

/**
 * 
 * 
 * @author fireandfuel
 *
 */
public class WidgetNode implements DatabaseObject
{
	private static final long serialVersionUID = -5908853649088580099L;
	
	public String key;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean visible = true;
	public boolean enabled = true;
	
//	public Layout layout;
	public final ArrayList<WidgetNode> children = new ArrayList<WidgetNode>(8);

	@Override
	public String getName()
	{
		return key;
	}

	@Override
	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public String getKey()
	{
		return key;
	}

	@Override
	public void setName(String name)
	{
		this.key = name;
	}
}
