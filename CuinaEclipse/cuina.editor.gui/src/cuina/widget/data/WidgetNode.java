package cuina.widget.data;

import cuina.database.NamedItem;

import java.io.Serializable;
import java.util.ArrayList;


public class WidgetNode implements NamedItem, Serializable
{
	private static final long serialVersionUID = -5908853649088580099L;
	
	public transient WidgetTree tree;
	public transient WidgetNode parent;
	public String name;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean visible = true;
	public boolean enabled = true;
	
	public final ArrayList<WidgetNode> children = new ArrayList<WidgetNode>(8);

	@Override
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
}
