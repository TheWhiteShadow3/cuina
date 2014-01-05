package cuina.eventx;

import java.io.Serializable;
import java.util.Arrays;

public class Command implements Serializable
{
	private static final long serialVersionUID = -2740462535751840405L;
	
	public String target;
	public String name;
	public Object[] args;
	public int indent;
	
	public Command(String target, String name, int indent, Object arg)
	{
		this(target, name, indent, new Object[] {arg});
	}
	
	public Command(String target, String name, int indent, Object... args)
	{
		this.target = target;
		this.name = name;
		this.indent = indent;
		this.args = args;
	}
	
	public Command(String target, String name, int indent, int i)
	{
		this(target, name, indent, Integer.valueOf(i));
	}
	
	public Command(String target, String name, int indent, double d)
	{
		this(target, name, indent, Double.valueOf(d));
	}
	
	@Override
	public String toString()
	{
		return target + '.' + name + Arrays.toString(args);
	}
}
