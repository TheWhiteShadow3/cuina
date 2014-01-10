package cuina.graphics;

import cuina.util.IntList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.GL11;

abstract class AbstractGraphicContainer implements GraphicContainer
{
	private String name;
	private transient ArrayList<GraphicReference> elements = new ArrayList<GraphicReference>();
	private Shader shader;
	private final IntList flags = new IntList();
	
	@Override
	public synchronized boolean addGraphic(Graphic graphic)
	{
		if (graphic.getContainer() == this) return false;
		
		if (graphic instanceof GraphicContainer)
		{
			if (!getGraphicManager().addContainer((GraphicContainer) graphic)) return false;
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
					getGraphicManager().removeContainer((GraphicContainer) graphic);
				}
				return;
			}
		}
	}
	
	protected void restoreElements(List<Graphic> storedList)
	{
		if (elements != null) disposeElements();
		this.elements = new ArrayList<GraphicReference>();
		for(int i = 0; i < storedList.size(); i++)
		{
			elements.add(new GraphicReference(storedList.get(i)));
		}
	}
	
	protected GraphicManager getGraphicManager()
	{
		return Graphics.GraphicManager;
	}
	
	protected void setName(String name)
	{
		this.name = Objects.requireNonNull(name);
	}

	@Override
	public String getName()
	{
		return name;
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
	
	public void ensureCapacity(int minCapacity)
	{
		elements.ensureCapacity(minCapacity);
	}
	
	protected void merge(AbstractGraphicContainer other)
	{
		if (this == other) throw new IllegalArgumentException();
		
		List<GraphicReference> otherElements = other.elements;
		OUTER_LOOP:
		for (int i = otherElements.size() - 1; i >= 0; i--)
		{
			if (otherElements.get(i).get() instanceof AbstractGraphicContainer)
			{
				AbstractGraphicContainer set2 = (AbstractGraphicContainer) otherElements.get(i).get();
				
				for (GraphicReference ref : elements)
				{
					if (!(ref.get() instanceof AbstractGraphicContainer)) continue;
					AbstractGraphicContainer set1 = (AbstractGraphicContainer) ref.get();
					
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
	
	public synchronized void draw()
	{
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
	
	protected void disposeElements()
	{
		for (int i = 0; i < elements.size(); i++)
		{
			Graphic el = elements.get(i).get();
			if (el != null) el.dispose();
		}
		clear();
	}


	@Override
	public void clear()
	{
		elements.clear();
	}
	
	public Shader getShader()
	{
		return shader;
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
	 * Hüllt ein Graphic-Objekt in einen Grafik-Kontainer ein.
	 * @param graphic Graphic, das eingehüllt werden soll.
	 * @return das einhüllende GraphicSet.
	 */
	public static AbstractGraphicContainer wrapGraphicElement(Graphic graphic)
	{
		AbstractGraphicContainer wrapper = new GraphicSet(null, graphic.getDepth(), graphic.getContainer());
//		GraphicContainer c = graphic.getContainer();
//		c.removeGraphic(graphic);
//		c.addGraphic(wrapper);
		wrapper.addGraphic(graphic);
		return wrapper;
	}
}
