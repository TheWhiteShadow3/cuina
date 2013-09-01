package cuina.eventx;

import java.io.Serializable;

public  class Command implements Serializable
{
	private static final long serialVersionUID = -2740462535751840405L;
	
	public String context;
	public String name;
	public Object args;
	public int indent;
	
	public Command(String context, String name, int indent, Object args)
	{
		this.context = context;
		this.name = name;
		this.args = args;
		this.indent = indent;
	}
	
	public Command(String context, String name, int indent, Object... args)
	{
		this(context, name, indent, (Object) args);
	}
	
	public Command(String context, String name, int indent, int i)
	{
		this(context, name, indent, Integer.valueOf(i));
	}
	
	public Command(String context, String name, int indent, double d)
	{
		this(context, name, indent, new Object[] {Double.valueOf(d)});
	}
	
	@Override
	public String toString()
	{
		return context + '.' + name + '(' + args + ')';
	}
}

