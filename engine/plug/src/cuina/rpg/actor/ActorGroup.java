package cuina.rpg.actor;

import cuina.rpg.Attributable;
import cuina.rpg.SlotInventory;
import cuina.rpg.inventory.Inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ActorGroup implements Attributable, Serializable, Iterable<Actor>
{
	private static final long	serialVersionUID	= -1751053863394558012L;

	private ArrayList<Actor> actors = new ArrayList<Actor>(4);
	
	/** Liste der Attribute, wie Gold, ... */
	private HashMap<String, Attribut> atts = new HashMap<String, Attribut>();
	
	private Inventory inventar = new SlotInventory(16);
	
	public ActorGroup()
	{
		// Geldsack
		addAttribut(new Attribut("GM", 9999999, 100));
	}

	public Actor getActor(int index)
	{
		return actors.get(index);
	}
	
	public boolean addActor(Actor actor)
	{
		return actors.add(actor);
	}

	public boolean removeActor(Actor actor)
	{
		return actors.remove(actor);
	}
	
	public int size()
	{
		return actors.size();
	}

	public boolean containsActor(Actor actor)
	{
		return actors.contains(actor);
	}

	public void addAttribut(Attribut att)
	{
		atts.put(att.getName(), att);
	}
	
	public void removeAttribut(String name)
	{
		atts.remove(name);
	}
	
	@Override
	public Attribut getAttribut(String name)
	{
		return atts.get(name);
	}

	public Inventory getInventar()
	{
		return inventar;
	}

	public void setInventar(Inventory inventar)
	{
		this.inventar = inventar;
	}

	@Override
	public Iterator<Actor> iterator()
	{
		return actors.iterator();
	}
}
