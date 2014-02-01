package cuina.editor.map.internal.views;

import cuina.editor.core.CuinaCore;
import cuina.editor.core.EditorContextChangeListener;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.TileSelection;
import cuina.editor.map.internal.TerrainEditor;
import cuina.editor.map.internal.layers.TilemapUtil;
import cuina.editor.ui.AbstractSelectionPanel;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.gl.GC;
import cuina.gl.Image;
import cuina.gl.PaintListener;
import cuina.map.Tileset;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceProvider;
import cuina.resource.ResourceManager.Resource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.ViewPart;
import org.lwjgl.LWJGLException;
import org.lwjgl.util.Color;

public class AutotileView extends ViewPart implements EditorContextChangeListener, Listener
{
	public static final String VIEW_ID = "cuina.editor.map.AutotileView";
	
	private ITerrainEditor editor;
	private Tileset tileset;
	private org.eclipse.swt.widgets.List autotileList;
	private AutotileViewSelectionPanel panel;
	private final List<Image> imageList = new ArrayList<Image>();
	private int index = -1;
	
	@Override
	public void createPartControl(Composite parent)
	{
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		
		autotileList = new org.eclipse.swt.widgets.List(group, SWT.BORDER);
		autotileList.addListener(SWT.Selection, this);
		autotileList.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		panel = new AutotileViewSelectionPanel(group, 256, 256);
		panel.getGLCanvas().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		
		IWorkbenchPage page = getViewSite().getPage();
		IEditorPart editor = page.getActiveEditor();
		if (editor instanceof TerrainEditor)
		{
			setEditor( (TerrainEditor) editor);
		}
		CuinaCore.getDefault().addEditorContextChangeListener(this);
		getSite().setSelectionProvider(panel);
	}

	@Override
	public void setFocus()
	{
		panel.getGLCanvas().setFocus();
	}
	
	private void clearAutotiles()
	{
		imageList.clear();
		autotileList.setItems(new String[0]);
		index = -1;
	}
	
	public void setEditor(ITerrainEditor editor)
	{
		if (this.editor == editor) return;
		
		this.editor = editor;
		if (editor == null)
		{
			clearAutotiles();
			return;
		}
		else
		{
			this.tileset = editor.getTileset();
			if (tileset == null) return;
			loadAutotiles();
		}
		panel.redraw();
	}
	
	private void loadAutotiles()
	{
		String[] autoTileNames = tileset.getAutotiles();
		if (autoTileNames == null)
		{
			clearAutotiles();
			return;
		}
		
		List<String> validNames = new ArrayList<String>();
		for (int i = 0; i < autoTileNames.length; i++)
		{
			if (autoTileNames[i] == null) continue;
			
			validNames.add(autoTileNames[i]);
			try
			{
				Resource res = editor.getProject().getService(ResourceProvider.class).
							getResource(ResourceManager.KEY_GRAPHICS, autoTileNames[i]);
				String pathName = res.getPath().toString();
				imageList.add(new Image(panel.getGLCanvas(), pathName));
			}
			catch (ResourceException | LWJGLException e)
			{
				e.printStackTrace();
			}
		}
		autotileList.setItems(validNames.toArray(new String[validNames.size()]));
		if (imageList.size() > 0) setIndex(0);
	}
	
	private void setIndex(int index)
	{
		this.index = index;
		panel.setSelection(getSelectedTiles());
	}

	@Override
	public void editorContextChange(IEditorPart part, IProject project)
	{
		if (part instanceof TerrainEditor)
			setEditor( (TerrainEditor) part);
		else
			setEditor(null);
	}
	
	private TileSelection getSelectedTiles()
	{
		if (index == -1) return TileSelection.EMPTY;
		
		short value = (short) (Tileset.AUTOTILES_OFFSET + index * 8 * 6 + 1);
				
		return new TileSelection(value);
	}
	
	private class AutotileViewSelectionPanel extends AbstractSelectionPanel implements
			SelectionListener, ISelectionProvider
	{
		private TileSelection tileSelection = TileSelection.EMPTY;
		private final ArrayList<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

		public AutotileViewSelectionPanel(Composite parent, int width, int height)
		{
			super(parent, width, height);
			getSelectionManager().addSelectionListener(this);
			addPaintListener(new PaintListener()
			{
				@Override
				public void paint(GC gc)
				{
					if (index != -1)
					{
						paintAutotiles(gc);
					}
				}
			});
		}
		
		private void paintAutotiles(GC gc)
		{
			Image image = imageList.get(index);
			TilemapUtil.paintGrid(gc, 0, 0, image.getWidth(), image.getHeight(), tileset.getTileSize() / 2);
			
			gc.setColor(Color.WHITE);
			gc.drawImage(image, 0, 0);
			
			paintCursor(gc);
		}

		@Override
		public void addSelectionChangedListener(ISelectionChangedListener l)
		{
			listeners.add(l);
		}

		@Override
		public void removeSelectionChangedListener(ISelectionChangedListener l)
		{
			listeners.remove(l);
		}

		@Override
		public ISelection getSelection()
		{
			return tileSelection;
		}

		@Override
		public void setSelection(ISelection selection)
		{
			if (!(selection instanceof TileSelection)) throw new IllegalArgumentException();

			tileSelection = (TileSelection) selection;
			fireSelectionChanged();
			panel.redraw();
		}

		protected void fireSelectionChanged()
		{
			SelectionChangedEvent event = new SelectionChangedEvent(this, tileSelection);
			for (ISelectionChangedListener l : listeners)
			{
				l.selectionChanged(event);
			}
		}
		
		@Override
		public void startSelection(SelectionEvent event)
		{
//			Selection s = event.manager.getSelection();
//			s.setBounds(x, y, width, height);
		}

		@Override
		public void updateSelection(SelectionEvent event)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void endSelection(SelectionEvent event)
		{
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	public void handleEvent(Event event)
	{
		if (event.widget == autotileList)
		{
			setIndex(autotileList.getSelectionIndex());
		}
	}
}
