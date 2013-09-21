package cuina.editor.gui.internal;

import cuina.database.ui.DataEditorPage;
import cuina.database.ui.IDatabaseEditor;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.util.Ini;
import cuina.editor.gui.internal.tree.WidgetTreeEditor;
import cuina.editor.ui.selection.HighlightingSelectionMode;
import cuina.editor.ui.selection.MoveSelectionMode;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SelectionManager;
import cuina.editor.ui.selection.SpanSelectionMode;
import cuina.resource.ResourceException;
import cuina.resource.ResourceManager;
import cuina.resource.ResourceManager.Resource;
import cuina.resource.ResourceProvider;
import cuina.widget.WidgetFactory;
import cuina.widget.WidgetFactory.FactoryFrame;
import cuina.widget.data.WidgetNode;
import cuina.widget.data.WidgetTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Texture;

public class WidgetPage implements DataEditorPage<WidgetTree>, ISelectionProvider, SelectionListener, ImageProvider
{
	public static final String TWL_RESOURE_PATH = "cuina.twl.path";
	public static final String TWL_THEME_PATH = "theme.path";
	
	private static final SpanSelectionMode CURSOR_SELECTION_MODE = new SpanSelectionMode();
	
	private IDatabaseEditor context;
	
	private WidgetEditorViewer viewer;
	private IStructuredSelection selection;
	private final ArrayList<ISelectionChangedListener> listener = new ArrayList<ISelectionChangedListener>();
	
	private WidgetTree tree;
	private WidgetTreeEditor treeEditor = new WidgetTreeEditor();
//	private WidgetLibraryTree widgetLibraryTree;
	private HighlightingSelectionMode highlightingSelectionMode;
	private MoveSelectionMode moveSelectionMode;
	
	@Override
	public void setValue(WidgetTree tree)
	{
		if (this.tree == tree) return;
		this.tree = tree;

		treeEditor.setWidgetTree(tree);
		SelectionManager sm = viewer.getSelectionManager();
		sm.clearSelections();
		sm.clearSeletionMode();
	}
	
	@Override
	public void setChildValue(Object obj)
	{
		if (obj instanceof WidgetNode)
		{
			setSelection(new StructuredSelection(obj));
		}
	}

	private Resource getThemeResource() throws ResourceException
	{
		Ini ini = getProject().getIni();
		String themePath = ini.get("TWL", TWL_THEME_PATH);
		Resource res = ResourceManager.getResourceProvider(getProject()).getResource(TWL_RESOURE_PATH, themePath);
		return res;
	}
	
	@Override
	public WidgetTree getValue()
	{
		return null;
	}
	
	public CuinaProject getProject()
	{
		return context.getProject();
	}

	public WidgetTreeEditor getTreeEditor()
	{
		return treeEditor;
	}

	@Override
	public void createEditorPage(Composite parent, IDatabaseEditor context)
	{
		this.context = context;
		parent.setLayout(new GridLayout(1, false));
		
		try
		{
			createWidgetEditorViewer(parent);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	private void createWidgetEditorViewer(Composite parent) throws ResourceException
	{
		viewer = new WidgetEditorViewer(parent, 640, 480);
		viewer.getGLCanvas().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		viewer.getGLCanvas().addListener(SWT.Resize, getResizeListener());
		viewer.setThemeURL(getThemeResource().getURL());
		viewer.setWidgetFactory(new WidgetFactory(this));
		viewer.setWidgetTreeEditor(treeEditor);
		this.highlightingSelectionMode = new HighlightingSelectionMode(viewer.getGLCanvas(), 5);
		this.moveSelectionMode = new MoveSelectionMode(viewer.getGLCanvas(), 5, 1);
		
		SelectionManager sh = viewer.getSelectionManager();
		sh.addSelectionListener(this);
		sh.setDisableOutside(false);
	}
	
	private Listener getResizeListener()
	{
		return new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				if (tree != null)
				{
					WidgetNode root = tree.root;
					Rectangle rect = viewer.getBounds();
					int minWidth  = Math.max(root.x + root.width, rect.width - 18);
					int minHeight = Math.max(root.y + root.height, rect.height - 18);
					viewer.setViewSize(minWidth, minHeight);
				}
			}
		};
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener l)
	{
		listener.add(l);
	}

	@Override
	public IStructuredSelection getSelection()
	{
		return selection;
	}

	@Override
	public void setSelection(ISelection selection)
	{
		if (selection instanceof IStructuredSelection)
			this.selection = (IStructuredSelection) selection;
		updateSelection();
	}
	
	private void updateSelection()
	{
		SelectionManager sm = viewer.getSelectionManager();
		sm.clearSelections();
		for (WidgetNode node : (List<WidgetNode>) selection.toList())
		{
			Widget widget = viewer.getWidget(node);
			sm.addSelection(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
		}
		viewer.refresh();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener l)
	{
		listener.remove(l);
	}
	
	@Override
	public Image createImage(String filename) throws ResourceException
	{
		ResourceProvider rp = ResourceManager.getResourceProvider(getProject());
		Resource res = rp.getResource(ResourceManager.KEY_GRAPHICS, filename);
		try
		{
			Texture tex = viewer.getRenderer().loadTexture(res.getURL(), null, null);
			return tex.getImage(0, 0, tex.getWidth(), tex.getHeight(), null, false, Texture.Rotation.NONE);
		}
		catch (IOException e)
		{
			throw new ResourceException(filename, ResourceException.LOAD, e);
		}
	}
	
//	private void createComponentTree(Composite parent)
//	{
//		Group libraryGroup = new Group(parent, SWT.NONE);
//		libraryGroup.setLayout(new GridLayout(1, false));
//		libraryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
//		libraryGroup.setText("Widget-Library");
//		
//		widgetLibraryTree = new WidgetLibraryTree(editorPanel);
//		widgetLibraryTree.createControl(libraryGroup);
//	}
	
//	private void addMouseHandling()
//	{
//		panel.getGLCanvas().addMouseListener(new MouseListener()
//		{
//			@Override
//			public void mouseDoubleClick(MouseEvent e)
//			{}
//
//			@Override
//			public void mouseDown(MouseEvent e)
//			{
//				if (CURSOR_SELECTION_MODE.getMode() != SpanSelectionMode.NONE) return;
//				
//				SelectionManager sh = panel.getSelectionHandler();
//				if (e.button == 1)
//				{
//					Widget widget = panel.getRoot().getWidgetAt(e.x, e.y);
//					widget = getOwner(widget);
//					if (widget != null && widget != panel.getRoot())
//					{
//						if (selection != null && selection.getFirstElement() == widget) return;
//						
//						selection = new StructuredSelection(widget);
//						sh.getSelection().setBounds(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
//					}
//					else
//					{
//						selection = new StructuredSelection();
//						sh.getSelection().clear();
//					}
//				}
//				else if (e.button == 3)
//				{
//					selection = new StructuredSelection();
//					sh.getSelection().clear();
//				}
//				panel.refresh();
//			}
//
//			@Override
//			public void mouseUp(MouseEvent e)
//			{}
//		});
//	}

	
	/**
	 *  Gibt den Owner des Widgets zurück.
	 *  Die meißten Widgets sind selbständig, aber einige gehören zu anderen.
	 *  In diesme Fall gib das Eltern-Widget zurück.
	 */
	private Widget getOwner(Widget widget)
	{
		Widget parent = widget.getParent();
		if (parent instanceof FactoryFrame && ((FactoryFrame) parent).isFrameElement(widget)) return parent;
		
		return widget;
	}

//    private void initSelectionMode()
//    {
//        SelectionManager handler = panel.getSelectionHandler();
//        handler.clearModes();
//        handler.getSelection().clear();
//        handler.setSelectionMode(0, new HighlightingSelectionMode(panel.getGLCanvas(), 5));
//        handler.setSelectionMode(1, CURSOR_SELECTION_MODE);
//        handler.setSelectionMode(3, SelectionMode.NULL_INSTANCE);
//        handler.getSelection().setRaster(0, 0, 1);
//    }

	
//	private class WidgetSelectionHandler implements SelectionListener
//	{
//		private boolean modeChanged;
//		
//		@Override
//		public void selectionChanged(Object source, Selection s)
//		{
//			if (modeChanged)
//			{
//				modeChanged = false;
//				return;
//			}
//			if (WidgetPage.this.selection == null) return;
//			
//			Widget widget = (Widget) WidgetPage.this.selection.getFirstElement();
//			if (widget != null)
//			{
//				widget.setPosition(s.getX(), s.getY());
//				widget.setSize(s.getWidth(), s.getHeight());
////				widget.invalidateLayout();
////				previewPanel.refresh();
//			}
//		}
//
//		@Override
//		public void selectionModeChanged(Object source, SelectionMode oldMode, SelectionMode newMode)
//		{
//			this.modeChanged = true;
//		}
//	}

	@Override
	public void startSelection(SelectionEvent event)
	{
		if (event.selection != null)
		{
			System.out.println("[ObjectLayer] change Selection-Mode");
			event.manager.setSelectionMode(moveSelectionMode, true);
		}
		else
			event.manager.setSelectionMode(CURSOR_SELECTION_MODE, true);
//		if (CURSOR_SELECTION_MODE.getMode() != SpanSelectionMode.NONE) return;
//		
//		SelectionManager sh = panel.getSelectionHandler();
//		if (e.button == 1)
//		{
//			Widget widget = panel.getRoot().getWidgetAt(e.x, e.y);
//			widget = getOwner(widget);
//			if (widget != null && widget != panel.getRoot())
//			{
//				if (selection != null && selection.getFirstElement() == widget) return;
//				
//				selection = new StructuredSelection(widget);
//				sh.getSelection().setBounds(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
//			}
//			else
//			{
//				selection = new StructuredSelection();
//				sh.getSelection().clear();
//			}
//		}
//		else if (e.button == 3)
//		{
//			selection = new StructuredSelection();
//			sh.getSelection().clear();
//		}
//		panel.refresh();
	}

	@Override
	public void updateSelection(SelectionEvent event)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSelection(SelectionEvent event)
	{
		if (event.selection != null)
		{
			if (event.selection.getWidth() == 1 && event.selection.getHeight() == 1)
			{
				selectSingleWidget(event.mouseEvent.x, event.mouseEvent.y);
			}
			event.manager.setSelectionMode(highlightingSelectionMode, true);
		}
	}
	
	private void selectSingleWidget(int x, int y)
	{
		Widget widget = getOwner(viewer.getRootWidget().getWidgetAt(x, y));
		if (widget != null && widget != viewer.getRootWidget())
		{
			WidgetNode node = viewer.findWidgetNode(widget);

			if (node == null) throw new NullPointerException("Node for Widget " + widget + " is null.");
			if (selection != null && selection.getFirstElement() == node) return;
			
			setSelection(new StructuredSelection(node));
		}
		else
		{
			setSelection(StructuredSelection.EMPTY);
		}
	}
}
