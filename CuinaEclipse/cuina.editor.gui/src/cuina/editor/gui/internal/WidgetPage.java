package cuina.editor.gui.internal;

import cuina.database.ui.DataEditorPage;
import cuina.database.ui.IDatabaseEditor;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.util.Ini;
import cuina.editor.ui.selection.HighlightingSelectionMode;
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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.views.properties.IPropertySource;

import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Texture;

public class WidgetPage implements DataEditorPage<WidgetTree>, ISelectionProvider, SelectionListener, ImageProvider
{
	public static final String TWL_RESOURE_PATH = "cuina.twl.path";
	public static final String TWL_THEME_PATH = "theme.path";
	
	private static final SpanSelectionMode CURSOR_SELECTION_MODE = new SpanSelectionMode();
	
	private IDatabaseEditor context;
	
	private WidgetEditorPanel panel;
//	private EditorPanel editorPanel;
	private IStructuredSelection selection;
	private final ArrayList<ISelectionChangedListener> listener = new ArrayList<ISelectionChangedListener>();
	
	private WidgetFactory widgetFactory;
//	private WidgetLibraryTree widgetLibraryTree;
	
	private WidgetTree tree;
	private Widget rootWidget;
	
	@Override
	public void setValue(WidgetTree tree)
	{
		if (this.tree == tree) return;
		this.tree = tree;
		
		if (rootWidget != null) rootWidget.destroy();
		
		rootWidget = widgetFactory.createWidget(tree.root);

		panel.setViewSize(rootWidget.getRight() + 32, rootWidget.getBottom() + 32);
		panel.setWidgetTree(tree);
	}
	
	@Override
	public void setChildValue(Object obj)
	{
		if (obj instanceof WidgetNode)
		{
			selectWidget((WidgetNode) obj);
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

	@Override
	public void createEditorPage(Composite parent, IDatabaseEditor context)
	{
		this.context = context;
		this.widgetFactory = new WidgetFactory(this);
		
		parent.setLayout(new GridLayout(1, false));
		
		try
		{
			createTabFolder(parent);
		}
		catch (ResourceException e)
		{
			e.printStackTrace();
		}
	}

	private void createTabFolder(Composite parent) throws ResourceException
	{
		panel = new WidgetEditorPanel(parent, 640, 480);
		panel.getGLCanvas().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		panel.getGLCanvas().addListener(SWT.Resize, getResizeListener());
		panel.setThemeURL(getThemeResource().getURL());
		panel.setWidgetFactory(new WidgetFactory(this));
		
		SelectionManager sh = panel.getSelectionHandler();
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
					Rectangle rect = panel.getBounds();
					int minWidth  = Math.max(root.x + root.width, rect.width - 18);
					int minHeight = Math.max(root.y + root.height, rect.height - 18);
					panel.setViewSize(minWidth, minHeight);
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
			Texture tex = panel.getRenderer().loadTexture(res.getURL(), null, null);
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
	
	private void selectWidget(WidgetNode node)
	{
		//TODO: node.widget existiert nicht mehr. Alternative muss gefunden werden.
//		Widget widget = node.widget;
//		selection = new StructuredSelection(widget);
//		SelectionHandler sh = panel.getSelectionHandler();
//		sh.getSelection().setBounds(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
		panel.refresh();
	}
	
	/*
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
			event.manager.setSelectionMode(new HighlightingSelectionMode(panel.getGLCanvas(), 5), true);
		}
	}
}
