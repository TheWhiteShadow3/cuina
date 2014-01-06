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
import java.util.HashMap;



/**
 * Die oberste Kontainer-Klasse für Grafik-Elementen.
 * Ermöglicht das Zeichnen einzelner Grafiken oder ganzer Grafik-Sets.
 * Enthaltende Elemente werden automatisch in der korrekten Z-Reihenfolge
 * von niedrig nach hoch gezeichnet.
 * 
 * @author TheWhiteShadow
 */
public final class GraphicManager extends AbstractGraphicContainer implements Serializable
{
	private static final long	serialVersionUID	= -2717287892441444757L;
	
	private HashMap<String, GraphicContainer> containers = new HashMap<String, GraphicContainer>();
	
//	private Shader shader;
//	private transient ArrayList<GraphicReference> elements;
	
	/**
	 * Erzeugt einen neuen GraphicManager.
	 */
	public GraphicManager()
	{
		setName("GraphicManager");
//		this.elements = new ArrayList<GraphicReference>();
	}
	
	/**
	 * Wird beim Wiederherstellen der Session aufgerufen
	 * und vereinigt zwei GraphicManager.
	 * Die Methode sollte nicht manuell aufgerufen werden.
	 * @param other GraphicManager der mit Diesem zusammengelegt werden soll.
	 */
	public void merge(GraphicManager other)
	{
		super.merge(other);
	}
	
	@Override
	protected GraphicManager getGraphicManager()
	{
		return this;
	}
	
	/**
	 * Gibt den GraphicContainer mit dem angegebenen Namen zurück.
	 * Wenn der Kontainer nicht vorhanden ist wird <code>null</code> zurückgegeben.
	 * <p>
	 * Die Kontainer sind globale Objekte und dürfen nicht im Session-Kontext gecachet werden.
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
	
	private void writeObject(ObjectOutputStream s) throws IOException
	{
		System.gc();
		
		s.defaultWriteObject();
		s.writeObject(toList());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream s) throws IOException
	{
		try
		{
			restoreElements((ArrayList<Graphic>) s.readObject());
		}
		catch (ClassNotFoundException e) { /* kommt nicht vor, außer durch Sabotage */ }
	}
	
	public void dispose()
	{
		disposeElements();
		containers.clear();
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
}
