package cuina.editor.map.internal;

import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.TerrainLayer;
import cuina.editor.map.internal.Activator.LayerDefinition;
import cuina.editor.ui.AbstractSelectionPanel;
import cuina.editor.ui.ViewLayer;
import cuina.gl.GC;
import cuina.gl.PaintListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

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
		for (LayerDefinition def : Activator.getLayerDefinitions().values()) try
		{

			TerrainLayer layer = def.getLayerClass().newInstance();
			layer.install(editor);
			layers.add(layer);
		}
		catch (IllegalAccessException | InstantiationException e)
		{
			e.printStackTrace();
		}
		// sortiere die Ebenen der Priorität entsprechend ein.
		Collections.sort(layers, new Comparator<ViewLayer>()
		{
			@Override
			public int compare(ViewLayer l1, ViewLayer l2)
			{
				return l1.getPriority() - l2.getPriority();
			}
		});
	}
	
	public void refreshLayers()
	{
		for (TerrainLayer layer : layers)
		{
			layer.refresh();
		}
		redraw();
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
		getSelectionManager().clearSeletionMode();
		redraw();
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
