package cuina.editor.map.internal.layers;

import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.TerrainLayer;
import cuina.editor.ui.AbstractSelectionPanel;
import cuina.editor.ui.ViewLayer;
import cuina.gl.GC;
import cuina.gl.PaintListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;

public class TerrainPanel extends AbstractSelectionPanel
{
	private final ArrayList<TerrainLayer> layers = new ArrayList<TerrainLayer>();
	private int activeIndex = -1;
	
	public TerrainPanel(Composite parent, int width, int height)
	{
		super(parent, width, height);
		addPaintListener(new PaintListener()
		{
			@Override
			public void paint(GC gc)
			{
				paintLayers(gc);
				paintCursor(gc);
			}
		});
	}

	public void installLayers(ITerrainEditor editor)
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor("cuina.mapeditor.layers");

		for (IConfigurationElement e : elements)
		{
			try
			{
				Bundle plugin = Platform.getBundle(e.getContributor().getName());

				Class<?> clazz = plugin.loadClass(e.getAttribute("class"));
				System.out.println("[MapEditor] Registriere Layer: " + clazz.getName());

				TerrainLayer layer = (TerrainLayer) clazz.newInstance();
				layer.install(editor);
				layers.add(layer);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		// sortiere die Ebenen der Priorität entsprechend
		Collections.sort(layers, new Comparator<ViewLayer>()
		{
			@Override
			public int compare(ViewLayer l1, ViewLayer l2)
			{
				return l1.getPriority() - l2.getPriority();
			}
		});
	}

	protected void paintLayers(GC gc)
	{
		for (int i = 0; i < layers.size(); i++)
		{
			layers.get(i).paint(gc);
		}
	}
	
	public TerrainLayer getActiveLayer()
	{
		if (activeIndex == -1) return null;

		return layers.get(activeIndex);
	}

	public List<TerrainLayer> getLayers()
	{
		return layers;
	}
	
	public void setActiveLayer(ViewLayer layer)
	{
		int index = layers.indexOf(layer);
		System.out.println("[TerrainPanel] Layer(" + index + "): " + layer);
//		if (index == -1) return;

		activeIndex = index;
		refresh();
	}

	public void showContextMenu(IMenuManager manager, Point point)
	{
		if (activeIndex == -1)
		{
			int count = 0;
			for (int i = 0; i < layers.size(); i++)
			{
				layers.get(i).fillDefaultContextMenu(manager, point);
				int n = manager.getItems().length;
				if (count < n)
				{
					manager.add(new Separator());
					count = n;
				}
			}
		}
		else try
		{
			layers.get(activeIndex).fillContextMenu(manager, point);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
