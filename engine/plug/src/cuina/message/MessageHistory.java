package cuina.message;

import cuina.plugin.ForSession;
import cuina.plugin.Plugin;

@ForSession(name="MessageHistory")
public class MessageHistory implements Plugin
{
	private static final long	serialVersionUID	= 4326709091246788497L;
	
	private int capacity = 0; // 0=Kein Limit
	private int size;
	private TextBufferElement first;
	private TextBufferElement last;
	private TextBufferElement current;
	
	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}
	
	public int size()
	{
		return size;
	}
	
	public void clear()
	{
		first = null;
		last = null;
		current = null;
		size = 0;
	}
	
	public void add(String text)
	{
		last = new TextBufferElement(text, last);
		if (size == 0)
		{
			first = last;
			size++;
		}
		else if (capacity > 0 && size >= capacity)
		{
			last = last.prev;
		}
		else
		{
			size++;
		}
		current = last;
	}

	public String pop()
	{
		if (size == 0) return null;
		
		String text = first.value;
		first = first.next;
		size--;
		return text;
	}
	
	public String getLast()
	{
		current = last;
		return getText();
	}
	
	public String getFirst()
	{
		current = first;
		return getText();
	}
	
	public String getText()
	{
		if (current != null)
			return current.value;
		else
			return "";
	}
	
	public String getPrevious()
	{
		if (isPrevious())
		{
			current = current.prev;
			return getText();
		}
		else
		{
			return null;
		}
	}
	
	public String getNext()
	{
		if (isNext())
		{
			current = current.next;
			return getText();
		}
		else
		{
			return null;
		}
	}
	
	public boolean isNext()
	{
		return (current.next != null);
	}
	
	public boolean isPrevious()
	{
		return (current.prev != null);
	}
	
	private class TextBufferElement
	{
		public String value;
		public TextBufferElement prev;
		public TextBufferElement next;
		
		public TextBufferElement(String value, TextBufferElement prev)
		{
			this.value = value;
			this.prev = prev;
		}
	}

	public void dispose()
	{
		first 	= null;
		last 	= null;
		current = null;
		size 	= 0;
	}

	@Override
	public String toString()
	{
		return "capacity=" + capacity + ", size=" + size + ", current=" + getText();
	}
}
