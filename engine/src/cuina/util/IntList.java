package cuina.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Leichtgewichtige Implementierung einer int-Liste.
 * @author TheWhiteShadow
 */
public class IntList implements Serializable, Iterable<Integer>
{
	private static final long serialVersionUID = 3234877434418546491L;
	
	private int[] ints;
	private int size;
//	private transient int modCount;
	
	public IntList()
	{
		this(10);
	}
	
	public IntList(int size)
	{
		ints = new int[10];
	}
	
	public IntList(int[] i)
	{
		ints = new int[i.length];
		add(i);
	}
	
	public int get(int index)
	{
		return ints[index];
	}

	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}
	
	public boolean contains(int i)
	{
		return indexOf(i) != -1;
	}

	@Override
	public Iterator<Integer> iterator()
	{
		return new Iterator<Integer>()
		{
			private int itrIndex = 0;
			
			@Override
			public boolean hasNext()
			{
				return itrIndex < size;
			}

			@Override
			public Integer next()
			{
				return ints[itrIndex];
			}

			@Override
			public void remove()
			{
				IntList.this.removeAt(itrIndex);
			}
		};
	}

	public int[] toArray()
	{
		return Arrays.copyOf(ints, ints.length);
	}

	public void add(int i)
	{
		ensureCapacity(size+1);
		ints[size] = i;
		size++;
	}
	
	public void add(int[] i)
	{
		ensureCapacity(size + i.length);
		System.arraycopy(i, 0, ints, size, i.length);
		size += i.length;
	}

	public void add(int index, int i)
	{
		ensureCapacity(size+1);
		for(int n = size-1; n > index; n--)
		{
			ints[n] = ints[n-1];
		}
		ints[index] = i;
		size++;
	}
	
	private void ensureCapacity(int minCapacity)
	{
		if (minCapacity <= 0) return;
//		modCount++;
		if (ints.length < minCapacity)
		{
			int newCapacity = minCapacity + (minCapacity >> 1);
	        if (newCapacity < 0) // overflow
	            throw new OutOfMemoryError();
	        
			ints = Arrays.copyOf(ints, newCapacity);
		}
	}

	public void remove(int i)
	{
		removeAt(indexOf(i));
	}

	public void removeAt(int index)
	{
		ints[index] = 0;
		for(int n = index+1; n < size; n++)
		{
			ints[n-1] = ints[n];
		}
//		modCount++;
	}
	
	public void clear()
	{
		size = 0;
//		modCount++;
	}

	public void set(int index, int i)
	{
		ints[index] = i;
//		modCount++;
	}
	
	public int indexOf(int i)
	{
		for (int n = 0; n < size; n++)
			if (ints[n] == i) return n;
		return -1;
	}

	@Override
	public int hashCode()
	{
        int result = 1;
        for (int n = 0; n < size; n++)
            result = 31 * result + ints[n];

        return result;
	}

	@Override
	public boolean equals(Object obj)
	{
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof IntList)) return false;
        IntList other = (IntList) obj;
        
        if (size != other.size) return false;
        for (int n=0; n < size; n++)
            if (ints[n] != other.ints[n]) return false;

        return true;
	}
}
