package cuina.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SaveHashMap<K, V> extends HashMap<K, V>
{
	private static final long serialVersionUID = 362498820763181265L;

	transient private int locks;
	private final EntryStack stack = new EntryStack();
	
	public boolean isLocked()
	{
		return locks > 0;
	}

	public void lock()
	{
		locks++;
	}
	
	public void unlock()
	{
		locks--;
		if (locks > 0) return;
		
		Entry entry;
		while((entry = stack.pull()) != null)
		{
			if (entry.value != null)
				super.put(entry.key, entry.value);
			else
				super.remove(entry.key);
		}
	}

	@Override
	public V put(K key, V value)
	{
		if (isLocked())
		{
			stack.put(key, value);
			return get(key);
		}
			
		return super.put(key, value);
	}

//	@Override
//	public void putAll(Map<? extends K, ? extends V> m)
//	{
//		if (lock)
//		{
//			for ()
//		}
//		
//		super.putAll(m);
//	}
	
	@Override
	public V remove(Object key)
	{
		if (isLocked())
		{
			stack.put( (K) key, null);
			return get(key);
		}
	
		return super.remove(key);
	}
	
	private class EntryStack implements Serializable
	{
		private static final long serialVersionUID = 7922309013528589033L;
		
		private final ArrayList<Entry> list = new ArrayList<Entry>(8);
		private int size = 0;
		
		public void put(K key, V value)
		{
			if (size < list.size())
			{
				list.get(size).key = key;
				list.get(size).value = value;
			}
			else
			{
				list.add(new Entry(key, value));
			}
			size++;
		}
		
		public Entry pull()
		{
			if (size > 0)
				return list.get(--size);
			return null;
		}
	}
	
	private class Entry
	{
		public K key;
		public V value;
		
		public Entry(K key, V value)
		{
			this.key = key;
			this.value = value;
		}
	}
}
