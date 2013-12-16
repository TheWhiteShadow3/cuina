/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import cuina.util.IntList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;

/**
 * Konkrete Implementierung eines Grafik-Kontainers.
 * Die darin abgelegten Elemente werden über WeakReferenzen gehalten.
 * <p>
 * Die Elemente des Kontainers erhalten alle eine Referenz auf diesen.
 * Auf diese Weise ist garantiert, dass er vom übergeordnete Kontainer erst dann für den GC frei gegeben wird,
 * wenn das letzte Element seine harten Referenz im Game-Kontext verloren hat
 * und es auch keine Referenz mehr auf den Kontainer selbst gibt.
 * </p>
 * @author TheWhiteShadow
 */
public class GraphicSet implements Graphic, GraphicContainer
{
	private static final long	serialVersionUID	= -6349477613441460139L;
	
	private final String name;
	private int z;
	private Shader shader;
	private GraphicContainer container;
	private transient ArrayList<GraphicReference> elements = new ArrayList<GraphicReference>();
	private final IntList flags = new IntList();
	
	public GraphicSet(String name)
	{
		this(name, 0, null);
	}
	
	public GraphicSet(String name, int z, GraphicContainer container)
	{
		this.name = name;
		this.z = z;
		
		if (container != null) container.addGraphic(this);
	}

	@Override
	public String getName()
	{
		return name;
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
	
	public void ensureCapacity(int minCapacity)
	{
		elements.ensureCapacity(minCapacity);
	}
	
	void merge(GraphicSet other)
	{
		List<GraphicReference> otherElements = other.elements;
		OUTER_LOOP:
		for (int i = otherElements.size() - 1; i >= 0; i--)
		{
			if (!(otherElements.get(i).get() instanceof GraphicSet)) continue;
			GraphicSet set2 = (GraphicSet) otherElements.get(i).get();
			
			for (GraphicReference ref : elements)
			{
				if (!(ref.get() instanceof GraphicSet)) continue;
				GraphicSet set1 = (GraphicSet) ref.get();
				
				if (set1.equals(set2))
				{
					set1.merge(set2);
					continue OUTER_LOOP;
				}
			}
			addGraphic(otherElements.get(i).get());
		}
	
		disposeElements();
		z = other.z;
		elements = other.elements;
	}
	
	@Override
	public synchronized boolean addGraphic(Graphic graphic)
	{
		if (graphic == this || graphic.getContainer() == this) return false;
		
		if (graphic instanceof GraphicContainer)
		{
			if (!Graphics.GraphicManager.addContainer((GraphicContainer) graphic)) return false;;
		}
		
		if (graphic.getContainer() != null) graphic.getContainer().removeGraphic(graphic);
		graphic.setContainer(this);
		elements.add(new GraphicReference(graphic));
		return true;
	}
	
	@Override
	public synchronized void removeGraphic(Graphic graphic)
	{
		for(int i = 0; i < elements.size(); i++)
		{
			if (elements.get(i).get() == graphic)
			{
				elements.remove(i);
				graphic.setContainer(null);
				
				if (graphic instanceof GraphicContainer)
				{
					Graphics.GraphicManager.removeContainer((GraphicContainer) graphic);
				}
				return;
			}
		}
	}
	
	@Override
	public List<Graphic> toList()
	{
		ArrayList<Graphic> list = new ArrayList<Graphic>(elements.size());
		
		for(int i = 0; i < elements.size(); i++)
		{
			Graphic e = elements.get(i).get();
			if (e != null) list.add(e);
		}
		return list;
	}
	
	public void setDepth(int z)
	{
		this.z = z;
	}

	@Override
	public int getDepth()
	{
		return z;
	}

	@Override
	public void refresh()
	{
		Graphic e;
		for(GraphicReference ref : elements)
		{
			e = ref.get();
			if (e != null) e.refresh();
		}
		if (shader != null) shader.refresh();
	}
	
	@Override
	public void clear()
	{
		elements.clear();
	}

	@Override
	public void dispose()
	{
		disposeElements();
		if (container != null) container.removeGraphic(this);
	}

	private void disposeElements()
	{
		for (int i = 0; i < elements.size(); i++)
		{
			Graphic el = elements.get(i).get();
			if (el != null) el.dispose();
		}
		clear();
	}
	
	@Override
	public void draw()
	{
//		cleanupEntries();
		
		Shader tempShader = Graphics.getShader();
		if (shader != null) Graphics.setShader(shader);
		Collections.sort(elements, ZComparator.INSTANCE);
		
		if (flags.size() > 0)
		{
			GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
			for (int i = 0; i < flags.size(); i++)
			{
				GL11.glEnable(flags.get(i));
			}
		}
		
		Graphic e;
		for (int i = 0; i < elements.size(); i++)
		{
			e = elements.get(i).get();
			if (e != null) try
			{
				e.draw();
			}
			catch(Exception ex)
			{	// kein Exception-Handler, da Dauerfehlermeldung
				ex.printStackTrace();
			}
			else
				elements.remove(i--);
		}
		
		if (flags.size() > 0)
			GL11.glPopAttrib();
		
		Graphics.setShader(tempShader);
	}

//	private void cleanupEntries()
//	{
//		int skip = 0;
//		for(int i = 0; i < elements.size(); i++)
//		{
//			GraphicReference ref = elements.get(i);
//			Graphic e = ref.get();
//			if (e == null || e.disposed())
//			{
//				skip++;
//			}
//			else if (skip > 0)
//			{
//				elements.set(i - skip, ref);
//			}
//		}
//		elements.trimToSize();
//	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this) return true;
		if (other == null) return false;
		if (other.getClass() != GraphicSet.class) return false;
		GraphicSet set = (GraphicSet) other;
		if (name == null || set.name == null) return false;
		return name.equals(set.name);
	}
	
	@Override
	public void setShader(Shader shader)
	{
		this.shader = shader;
	}
	
	@Override
	public void setFlag(int glFlag, boolean value)
	{
		if (value)
		{
			if (flags.contains(glFlag)) return;
			flags.add(glFlag);
		}
		else
		{
			flags.remove(glFlag);
		}
	}
	
	/**
	 * Hüllt ein Graphic in ein GraphicSet ein.
	 * @param graphic Graphic, das eingehüllt werden soll.
	 * @return das einhüllende GraphicSet.
	 */
	public static GraphicSet wrapGraphicElement(Graphic graphic)
	{
		GraphicSet wrapper = new GraphicSet(null, graphic.getDepth(), graphic.getContainer());
//		GraphicContainer c = graphic.getContainer();
//		c.removeGraphic(graphic);
//		c.addGraphic(wrapper);
		wrapper.addGraphic(graphic);
		return wrapper;
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
			ArrayList<Graphic> savingElements = (ArrayList<Graphic>) s.readObject();
			this.elements = new ArrayList<GraphicReference>(savingElements.size());

			for(int i = 0; i < savingElements.size(); i++)
			{
				elements.add(new GraphicReference(savingElements.get(i)));
			}
		}
		catch (ClassNotFoundException e) { /* kommt nicht vor */ }
	}
	
	@Override
	public String toString()
	{
		return "Graphicset: " + name;
	}
}
