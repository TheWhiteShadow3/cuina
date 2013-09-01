package cuina.editor.script.internal.ruby;

public class Token
{
	private final int pos;
	private final int group;
	private final String value;

	public Token(int pos, int group, String value)
	{
		this.pos = pos;
		this.group = group;
		this.value = value;
	}

	public int getPos()
	{
		return pos;
	}

	public int getGroup()
	{
		return group;
	}

	public String getValue()
	{
		return value;
	}

	public int getEndposition()
	{
		return pos + value.length();
	}
	
	public char charAt(int i)
	{
		return value.charAt(i);
	}

	public String toString()
	{
		return value;
	}
}
