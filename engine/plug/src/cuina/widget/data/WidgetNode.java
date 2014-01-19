package cuina.widget.data;

import cuina.database.NamedItem;
import cuina.event.Trigger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TheWhiteShadow, fireandfuel
 * 
 */
public class WidgetNode implements NamedItem, Serializable
{
	private static final long serialVersionUID = -5908853649088580099L;
	
	public String name;
	public int x;
	public int y;
	public int width;
	public int height;
	public boolean visible = true;
	public boolean enabled = true;
	
//	public Layout layout;
	public List<Trigger> triggers;
	public final ArrayList<WidgetNode> children = new ArrayList<WidgetNode>(4);

	@Override
	public String getName()
	{
		return name;
	}
	
	public void add(WidgetNode node)
	{
		children.add(node);
	}
	
	public void remove(WidgetNode node)
	{
		children.remove(node);
	}
}
