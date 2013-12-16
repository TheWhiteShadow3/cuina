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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;



/**
 * Dient zur einfachen Verwaltung von Grafik-Elementen.
 * Ermöglicht das Zeichnen einzelner Grafiken oder ganzer Grafik-Sets.
 * Enthaltende Elemente werden automatisch in der korrekten Z-Reihenfolge
 * von niedrig nach hoch gezeichnet.
 * 
 * @author TheWhiteShadow
 */
public final class GraphicManager implements GraphicContainer, Serializable
{
	private static final long	serialVersionUID	= -2717287892441444757L;
	
	private HashMap<String, GraphicContainer> containers = new HashMap<String, GraphicContainer>();
	
	final long id = 0;
	private Shader shader;
	private transient ArrayList<GraphicReference> elements;
	
	/**
	 * Erzeugt einen neuen GraphicManager.
	 */
	public GraphicManager()
	{
		this.elements = new ArrayList<GraphicReference>();
	}
	
	/**
	 * Wird beim Wiederherstellen der Session aufgerufen
	 * und vereinigt zwei GraphicManager.
	 * Die Methode sollte nicht manuell aufgerufen werden.
	 * @param other GraphicManager der mit Diesem zusammengelegt werden soll.
	 */
	public void merge(GraphicManager other)
	{
		if (this == other) throw new IllegalArgumentException();
		
		List<GraphicReference> otherElements = other.elements;
		OUTER_LOOP:
		for (int i = otherElements.size() - 1; i >= 0; i--)
		{
			if (otherElements.get(i).get() instanceof GraphicSet)
			{
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
			}
			addGraphic(otherElements.get(i).get());
		}
	}
	
	public void ensureCapacity(int minCapacity)
	{
		elements.ensureCapacity(minCapacity);
	}
	
	@Override
	public synchronized boolean addGraphic(Graphic graphic)
	{
		if (graphic.getContainer() == this) return false;
		
		if (graphic instanceof GraphicContainer)
		{
			if (!addContainer((GraphicContainer) graphic)) return false;
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
					removeContainer((GraphicContainer) graphic);
				}
				return;
			}
		}
	}
	
	/**
	 * Gibt den GraphicContainer mit dem angegebenen Namen zurück.
	 * Wenn der Kontainer nicht vorhanden ist wird <code>null</code> zurückgegeben.
	 * <p>
	 * Die Kontainer sind Objekte des globalen Kontextes und dürfen nicht im Session-Kontext gecachet werden.
	 * </p>
	 * @param name Name des GraphicContainers.
	 * @return Den Kontainer mit dem angegebenen Namen.
	 * @throws NullPointerException Wenn der angegebene Name <code>null</code> ist.
	 */
	public GraphicContainer getContainer(String name)
	{
		if (name == null) throw new NullPointerException();
		
		return containers.get(name);
	}
	
	boolean addContainer(GraphicContainer container)
	{
		if (container instanceof GraphicManager) throw new IllegalArgumentException();
		
		if (containers.containsKey(container.getName())) return false;
		containers.put(container.getName(), container);
		return true;
	}
	
	void removeContainer(GraphicContainer container)
	{
		containers.remove(container.getName());
	}
	
	/**
	 * Führt einen refresh auf alle enthaltenen Grafik-Elemente aus.
	 */
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
	
	protected synchronized void draw()
	{
//		cleanupEntries();
		
		Graphics.setShader(shader);
		Collections.sort(elements, ZComparator.INSTANCE);
		
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
	
	private void writeObject(ObjectOutputStream s) throws IOException
	{
		System.gc();

//		ArrayList<Graphic> list = new ArrayList<Graphic>(elements.size());
//		
//		for(int i = 0; i < elements.size(); i++)
//		{
//			GraphicReference ref = elements.get(i);
//			Graphic e = ref.get();
//			if (e != null)
//			{
//				if (!(e instanceof GraphicContainer) && ref.context != SESSION) continue;
//				list.add(e);
//			}
//		}
		
		s.defaultWriteObject();
		s.writeObject(toList());
	}
	
	@SuppressWarnings("unchecked")
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
		catch (ClassNotFoundException e) { /* kommt nicht vor, außer durch Sabotage */ }
	}
	
//	/**
//	 * Setzt das Temporär-Flag auf den angegebenen Wert.<br>
//	 * Bei gesetztem Temp-Flag werden erzeugte Grafiken nicht persitenziert.
//	 * Eine mögliche  Anwendung ist ein Speicher-Icon, das während des Speicherns angezeigt,
//	 * aber nicht mitgespeichert werden soll.
//	 * @param value <code>true</code> oder <code>false</code>.
//	 */
//	public void setTempFlag(boolean value)
//	{
//		temp = value;
//	}
//	
//	boolean getTempFlag()
//	{
//		return temp;
//	}
	
	public void dispose()
	{
		for(int i = 0; i < elements.size(); i++)
		{
			Graphic el = elements.get(i).get();
			if (el != null) el.dispose();
		}
		clear();
		containers.clear();
	}

	@Override
	public void setShader(Shader shader)
	{
		this.shader = shader;
	}

	@Override
	public void setContainer(GraphicContainer container)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public GraphicContainer getContainer()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "GraphicManager";
	}

	@Override
	public void setFlag(int glFlag, boolean value)
	{
		throw new UnsupportedOperationException();
	}
}
