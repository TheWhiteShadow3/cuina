package cuina.data;

import java.awt.Rectangle;
import java.io.Serializable;

public class InterfaceObject implements Serializable, NamedItem
{
	private static final long serialVersionUID = 8484214539632669880L;
	
	public String name;
	public Rectangle bounds;
	
	@Override
	public String getName()
	{
		return name;
	}
}
