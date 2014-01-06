/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Konkrete Implementierung eines Grafik-Kontainers.
 * Die darin abgelegten Elemente werden über WeakReferenzen gehalten.
 * <p>
 * Die Referenzen des Kontainers auf seine Elemente sind schwach,
 * d.h. wenn alle Referenzen außerhalb des Kontainers auf die Grafik entfallen, wird sie vom GC gelöscht.
 * </p>
 * @author TheWhiteShadow
 */
public class GraphicSet extends AbstractGraphicContainer implements Graphic
{
	private static final long	serialVersionUID	= -6349477613441460139L;
	
	private int depth;
	private GraphicContainer container;
	
	public GraphicSet(String name)
	{
		this(name, 0, null);
	}
	
	public GraphicSet(String name, int depth, GraphicContainer container)
	{
		setName(name);
		this.depth = depth;
		
		if (container != null) container.addGraphic(this);
	}
	
	@Override
	public void setContainer(GraphicContainer container)
	{
		this.container = container;
	}
	
	@Override
	public GraphicContainer getContainer()
	{
		return container;
	}
	
//	void merge(GraphicSet other)
//	{
//		List<GraphicReference> otherElements = other.elements;
//		OUTER_LOOP:
//		for (int i = otherElements.size() - 1; i >= 0; i--)
//		{
//			if (!(otherElements.get(i).get() instanceof GraphicSet)) continue;
//			GraphicSet set2 = (GraphicSet) otherElements.get(i).get();
//			
//			for (GraphicReference ref : elements)
//			{
//				if (!(ref.get() instanceof GraphicSet)) continue;
//				GraphicSet set1 = (GraphicSet) ref.get();
//				
//				if (set1.equals(set2))
//				{
//					set1.merge(set2);
//					continue OUTER_LOOP;
//				}
//			}
//			addGraphic(otherElements.get(i).get());
//		}
//	
//		disposeElements();
//		z = other.z;
//		elements = other.elements;
//	}
	
	@Override
	public boolean addGraphic(Graphic graphic)
	{
		if (graphic == this) return false;
		
		return super.addGraphic(graphic);
	}
	
	public void setDepth(int depth)
	{
		this.depth = depth;
	}

	@Override
	public int getDepth()
	{
		return depth;
	}

	@Override
	public void dispose()
	{
		disposeElements();
		if (container != null) container.removeGraphic(this);
		container = null;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (other.getClass() != GraphicSet.class) return false;
		GraphicSet set = (GraphicSet) other;
		return getName().equals(set.getName());
	}
	
	private void writeObject(ObjectOutputStream s) throws IOException
	{
		System.gc();
		
		s.defaultWriteObject();
		s.writeObject(toList());
	}
	
	private void readObject(ObjectInputStream s) throws IOException
	{
		try
		{
			s.defaultReadObject();
			restoreElements((ArrayList<Graphic>) s.readObject());
		}
		catch (ClassNotFoundException e) { /* kommt nicht vor, außer durch Sabotage */ }
	}
	
	@Override
	public String toString()
	{
		return "Graphicset: " + getName();
	}
}
