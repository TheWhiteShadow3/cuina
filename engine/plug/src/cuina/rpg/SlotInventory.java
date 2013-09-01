package cuina.rpg;

import cuina.rpg.inventory.Inventory;
import cuina.rpg.inventory.Item;


/**
 * Das SlotInventory besitzt eine bestimmte Menge an Fächern die
 * jeweils eine bestimmte Anzahl eines Item-Types aufnehmen können.
 * @author TheWhiteShadow
 */
public class SlotInventory implements Inventory
{
	private static final long	serialVersionUID	= 6319802708701820789L;
	
	private Slot[] slots;
	
	public SlotInventory(int slotCount)
	{
		slots = new Slot[slotCount];
		for(int i = 0; i < slots.length; i++)
		{
			slots[i] = new Slot(10);
		}
	}
	
	public int size()
	{
		return slots.length;
	}
	
	public Slot getSlot(int slot)
	{
		return slots[slot];
	}

	@Override
	public boolean addItem(Item item)
	{
		return addItems(item.getKey(), 1) == 1;
	}

	@Override
	public int addItems(String key, int count)
	{
		int rest = count;
		for(Slot slot : slots)
		{
			if ( key.equals(slot.getKey()) )
			{
				rest -= slot.add(key, rest);
				if (rest == 0) return count;
			}
		}
		for(Slot slot : slots)
		{
			if (slot.getKey() == null)
			{
				rest -= slot.add(key, rest);
				if (rest == 0) return count;
			}
		}
		return count - rest;
	}

	@Override
	public boolean containsItem(Item item)
	{
		return getItemCount(item.getKey()) > 0;
	}

	@Override
	public int getItemCount(String key)
	{
		int count = 0;
		for(Slot slot : slots)
		{
			if ( key.equals(slot.getKey()) )
			{
				count += slot.count();
			}
		}
		return count;
	}

	@Override
	public boolean removeItem(Item item)
	{
		return removeItems(item.getKey(), 1) == 1;
	}

	@Override
	public int removeItems(String key, int count)
	{
		int rest = count;
		for(Slot slot : slots)
		{
			if ( key.equals(slot.getKey()) )
			{
				rest -= slot.remove(rest);
				if (rest == 0) return count;
			}
		}
		return count - rest;
	}
	
	public static class Slot
	{
		private String key;
		private int size;
		private int count;
		
		public Slot(int size)
		{
			this.size = size;
		}
		
		public int add(String key, int count)
		{
			if (count <= 0) return 0;
			if (this.key != null && this.key != key) return 0;
			
			this.key = key;
			if (size < count) count = size - this.count;
			this.count += count;
			return count;
		}
		
		public int remove(int count)
		{
			if (count <= 0) return 0;
			
			if (this.count < count) count = this.count;
			this.count -= count;
			if (count == 0) key = null;
			return count;
		}
		
		public String getKey()
		{
			return key;
		}
		
		public int size()
		{
			return size;
		}
		
		public int count()
		{
			return count;
		}
	}
}
