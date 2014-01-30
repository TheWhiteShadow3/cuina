package cuina.editor.object.internal;

import cuina.database.Database;
import cuina.database.ui.tree.TreeNode;
import cuina.editor.core.ObjectUtil;
import cuina.editor.map.ITerrainEditor;
import cuina.editor.map.MapEvent;
import cuina.editor.map.TerrainLayer;
import cuina.editor.map.util.MapOperation;
import cuina.editor.map.util.MapSavePoint;
import cuina.editor.object.ObjectAdapter;
import cuina.editor.object.ObjectGraphic;
import cuina.editor.object.internal.properties.ObjectBasePropertyPage;
import cuina.editor.ui.ClipboardUtil;
import cuina.editor.ui.selection.FollowSelectionMode;
import cuina.editor.ui.selection.HighlightingSelectionMode;
import cuina.editor.ui.selection.MoveSelectionMode;
import cuina.editor.ui.selection.Selection;
import cuina.editor.ui.selection.SelectionEvent;
import cuina.editor.ui.selection.SelectionListener;
import cuina.editor.ui.selection.SelectionManager;
import cuina.gl.GC;
import cuina.gl.Image;
import cuina.map.Map;
import cuina.object.ObjectData;
import cuina.object.ObjectTemplate;
import cuina.resource.ResourceException;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ObjectLayer implements TerrainLayer, ISelectionProvider, ISelectionListener, SelectionListener
{
	public static final String LAYER_NAME = "cuina.map.ObjectLayer";
	public static final String OBJECT_ACTION = "cuina.editor.object.objects.selectionAction";
	
	/**
	 * Eine Grafik, die verwendet wird,
	 * wenn das Objekt kein eigenes Bild besitzt oder dieses nicht geladen werden konnte.
	 */
	private ObjectGraphic DEFAULT_GRAPHIC;
	
	private Map map;
	private ITerrainEditor editor;
	private IStructuredSelection selection = StructuredSelection.EMPTY;
	private final ArrayList<ISelectionChangedListener> listener = new ArrayList<ISelectionChangedListener>();
	private final HashMap<ObjectData, ObjectGraphic> graphicCache = new HashMap<ObjectData, ObjectGraphic>();

	private Action copyAction;
	private Action pasteAction;
	private Action deleteAction;
	private Action propertyAction;
	
	private ObjectTemplate cursorTemplate;

	private ObjectSavePointSet savepointBeforMove;
	private MoveSelectionMode moveSelectionMode;
	private FollowSelectionMode followSelectionMode;
	
	@Override
	public String getName()
	{
		return LAYER_NAME;
	}

	@Override
	public int getPriority()
	{
		return 20;
	}

	@Override
	public void install(ITerrainEditor editor)
	{
		this.editor = editor;
		this.map = editor.getMap();
		this.DEFAULT_GRAPHIC = new DefaultObjectGraphic();
		this.moveSelectionMode = new MoveSelectionMode(editor.getGLCanvas());
		this.followSelectionMode = new FollowSelectionMode();
		editor.getSelectionManager().addSelectionListener(this);
		
		createContextMenu();
		
		// XXX: Eclipse Indigo Service Release 2 creates an active page too late,
		// getActivePage() returns null - consequently it throws a null pointer exception
		// workaround: run this in Display thread 
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (page != null)
		{
			page.addSelectionListener(this);
		}
		else
		{
			System.out.println("[TileMapLayer] Info: Run \"getActivePage() returns null\" workaround to register SelectionListener");
			Display.getDefault().asyncExec(new SelectionRunnable());
		}
	}

	// class for "getActivePage() returns null" workaround
	private class SelectionRunnable implements Runnable
	{
		@Override
		public void run()
		{
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			page.addSelectionListener(ObjectLayer.this);
		}
	}
	
	private void createContextMenu()
	{
		copyAction = new Action()
		{
			@Override
			public void run()
			{
				if (selection.isEmpty()) return;
				
				ObjectData data = (ObjectData) getSelection().getFirstElement();
				setTemplate(getTemplateFromObject(ObjectUtil.clone(data)));
				ClipboardUtil.toClipboard(selection);
			}
		};
		copyAction.setText("Kopieren");
		copyAction.setAccelerator(SWT.CONTROL | 'c');
		copyAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		pasteAction = new Action()
		{
			@Override
			public void run()
			{
				IStructuredSelection s = ClipboardUtil.fromClipboard();
				if (s.isEmpty() || !(s.getFirstElement() instanceof ObjectData)) return;
				
				selection = s;
			}
		};
		pasteAction.setText("Einfügen");
		pasteAction.setAccelerator(SWT.CONTROL | 'v');
		pasteAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		
		deleteAction = new Action()
		{
			@Override
			public void run()
			{
				for(Object obj : selection.toArray())
				{
					int id = ((ObjectData) obj).id;
					map.objects.remove(id);
				}
				setSelection(null);
			}
		};
		deleteAction.setText("Entfernen");
		deleteAction.setAccelerator(SWT.CONTROL | 'v');
		deleteAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		
		propertyAction = new Action()
		{
			@Override
			public void run()
			{
				if (selection.isEmpty()) return;
				
				ObjectData obj = (ObjectData) selection.getFirstElement();
				if (obj == null) return;
				
				ObjectAdapter adapter = new ObjectAdapter(editor.getProject(), obj);

				PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
						editor.getGLCanvas().getShell(), adapter, ObjectBasePropertyPage.ID, null, adapter);
				if (dialog != null) dialog.open();
			}
		};
		propertyAction.setText("Eigenschaften");
	}
	
	private ImageDescriptor getSharedImageDescriptor(String id)
	{
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(id);
	}
	
	@Override
	public void paint(GC gc)
	{
		if (map == null) return;
		
		List<Object> selectedObjects = selection.toList();
		for (Object obj : map.objects.values())
		{
			if (selectedObjects.contains(obj)) continue;
			
			if (obj instanceof ObjectData)
			{
				ObjectData data = (ObjectData) obj;
				drawObject(gc, data, data.x, data.y);
			}
		}
		for (Object obj : selectedObjects)
		{
			if (obj instanceof ObjectData)
			{
				ObjectData data = (ObjectData) obj;
				drawObject(gc, data, data.x, data.y);
			}
		}
		
//		if (cursorTemplate != null)
//		{
//			drawObject(gc, cursorTemplate.sourceObject, mouseX, mouseY);
//		}
	}
	
	private ObjectGraphic getObjectGraphic(ObjectData obj)
	{
		ObjectGraphic graphic = graphicCache.get(obj);
		if (graphic == null)
		{
			ObjectAdapter adapter = new ObjectAdapter(editor.getProject(), obj);
			graphic = (ObjectGraphic) adapter.getAdapter(ObjectGraphic.class);
			
			if (graphic == null) graphic = DEFAULT_GRAPHIC;
			
			graphic.setGLCanvas(editor.getGLCanvas());
			graphicCache.put(obj, graphic);
		}
		return graphic;
	}
	
	private void drawObject(GC gc, ObjectData obj, int x, int y)
	{
		ObjectGraphic graphic = getObjectGraphic(obj);
		
		Image image = graphic.getImage();
		if (image == null) return;
		
		Point offset = graphic.getOffset();
		java.awt.Rectangle clip = graphic.getClipping();
		gc.drawImage(image, clip.x, clip.y, clip.width, clip.height,
				x + offset.x, y + offset.y, clip.width, clip.height);
	}
	
	private ObjectTemplate getTemplateFromObject(ObjectData data)
	{
		String key = data.templateKey;
		if (key != null && key.length() > 0)
		{
			Database db = editor.getProject().getService(Database.class);
			try
			{
				return db.<ObjectTemplate>loadTable("Template").get(key);
			}
			catch (ResourceException e)
			{
				e.printStackTrace();
			}
		}
		return new ObjectTemplate(data, data.name);
	}
	
//	private ObjectData getObject(int id)
//	{
//		Object obj = map.objects.get(id);
//		if (obj instanceof ObjectData)
//			return (ObjectData) obj;
//		else
//			return null;
//	}
	
	private ObjectData getObject(int x, int y)
	{
		for (Object obj : map.objects.values())
		{
			if (obj instanceof ObjectData)
			{
				ObjectData data = (ObjectData) obj;
				java.awt.Rectangle box = getObjectBounds(data);
//				System.out.println("Box: " + box);
//				System.out.println("Auswahl: " + (x) + ", " + (y));
				
				if (box.contains(x, y)) return data;
			}
		}
		return null;
	}
	
	private java.awt.Rectangle getObjectBounds(ObjectData obj)
	{
		ObjectGraphic graphic = getObjectGraphic(obj);
		if (graphic != null)
		{
			java.awt.Rectangle box = graphic.getClipping();
			Point point = graphic.getOffset();
			box.x = point.x + obj.x;
			box.y = point.y + obj.y;
			
			return box;
		}
		else
			return new java.awt.Rectangle(0, 0, 16, 16);
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu, org.eclipse.swt.graphics.Point point)
	{
		if (selection.isEmpty())
		{
			fillDefaultContextMenu(menu, point);
			return;
		}
		
		menu.add(new Separator(IWorkbenchActionConstants.M_EDIT));
		menu.add(copyAction);
		menu.add(pasteAction);
		menu.add(deleteAction);
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(propertyAction);
	}

	@Override
	public void fillDefaultContextMenu(IMenuManager menu, org.eclipse.swt.graphics.Point point)
	{
		List<ObjectData> list = getObjects(point.x, point.y, 1, 1);
		for (ObjectData d : list)
		{
			menu.add(new SelectionAction(d));
		}
	}

	@Override
	public void dispose()
	{
		listener.clear();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener l)
	{
		listener.add(l);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener l)
	{
		listener.remove(l);
	}
	
	@Override
	public IStructuredSelection getSelection()
	{
		return selection;
	}

	@Override
	public void setSelection(ISelection selection)
	{
		SelectionManager sh = editor.getSelectionManager();
		sh.clearSelections();
		
		if (selection instanceof IStructuredSelection)
		{
			this.selection = (IStructuredSelection) selection;
			if (!selection.isEmpty())
			{
				for (Object obj : this.selection.toArray())
				{
					java.awt.Rectangle box = getObjectBounds((ObjectData) obj);
					Selection s = sh.addSelection(box.x, box.y, box.width, box.height);
					s.setData(obj);
				}
			}
		}
		else
		{
			this.selection = StructuredSelection.EMPTY;
		}
		editor.getGLCanvas().redraw();
	}
	
	@Override
	public boolean selectionPerformed(Rectangle rect)
	{
		List<ObjectData> list = getObjects(rect.x, rect.y, rect.width, rect.height);
		list.addAll(getSelection().toList());
		
		if (list.isEmpty())
		{
			setSelection(StructuredSelection.EMPTY);
			return false;
		}

		setSelection(new StructuredSelection(list));
		
		editor.setActiveLayer(this);
		editor.getSelectionManager().setSelectionMode(
				new HighlightingSelectionMode(editor.getGLCanvas(), -1), true);
		return true;
	}
	
	@Override
	public boolean selectionPerformed(org.eclipse.swt.graphics.Point point)
	{
		List<ObjectData> list = new ArrayList<ObjectData>();
		list.addAll(getSelection().toList());
		ObjectData obj = getObject(point.x, point.y);
		if (obj != null) list.add(obj);
		
		if (list.isEmpty())
		{
			setSelection(StructuredSelection.EMPTY);
			return false;
		}
		
		setSelection(new StructuredSelection(list));
		
		editor.setActiveLayer(this);
		editor.getSelectionManager().setSelectionMode(
				new HighlightingSelectionMode(editor.getGLCanvas(), -1), true);
		return true;
	}
	
	private List<ObjectData> getObjects(int x, int y, int width, int height)
	{
		List<ObjectData> list = new ArrayList<ObjectData>();
		
		for (Object obj : map.objects.values())
		{
			if (obj instanceof ObjectData)
			{
				if ( getObjectBounds((ObjectData) obj).intersects(x, y, width, height) )
				{
					list.add((ObjectData) obj);
				}
			}
		}
		
		return list;
	}
	
	private void setTemplate(ObjectTemplate template)
	{
		if (template == cursorTemplate) return;
		System.out.println("Setze Template: " + template);
		if (template == null)
		{
			cursorTemplate = null;
			setSelection(null);
			editor.getSelectionManager().setSelectionMode(ITerrainEditor.CURSOR_SELECTION_MODE, true);
		}
		else
		{
			editor.setActiveTool(OBJECT_ACTION);
			
			cursorTemplate = template;
			setSelection(new StructuredSelection(template.sourceObject));
			ObjectGraphic graphic = getObjectGraphic(template.sourceObject);
			if (graphic != null)
			{
				Point p = graphic.getOffset();
				followSelectionMode.setDragOffset(-p.x, -p.y);
			}
			editor.getSelectionManager().setSelectionMode(followSelectionMode, true);
		}
	}

	private int getAvailableID()
	{
		if (map == null) throw new NullPointerException("map is null.");
		
		int i = 1;
		while(map.objects.containsKey(i))
		{
			i++;
		}
		return i;
	}
	
	private ObjectData createNewObject(ObjectTemplate template)
	{
		ObjectData obj	= ObjectUtil.clone(template.sourceObject);
		obj.id			= getAvailableID();
		obj.templateKey = template.getKey();
		return obj;
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (selection instanceof ITreeSelection)
		{
			Object obj = ((ITreeSelection) selection).getFirstElement();
			if (obj instanceof TreeNode)
			{
				ObjectTemplate template = (ObjectTemplate) ((TreeNode) obj).getAdapter(ObjectTemplate.class);
				setTemplate(template);
			}
		}
	}

	@Override
	public void startSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != ObjectLayer.this) return;
		
		if (cursorTemplate != null)
		{
			if (event.mouseEvent.button == 1)
			{
				ObjectData obj = createNewObject(cursorTemplate);
				map.objects.put(obj.id, obj);
				ObjectSavePoint undoPoint = new ObjectSavePoint(ObjectSavePoint.DELETE, obj, obj.x, obj.y);
				ObjectSavePoint redoPoint = new ObjectSavePoint(ObjectSavePoint.CREATE, obj, obj.x, obj.y);
				editor.addOperation(new MapOperation("Create Object", undoPoint, redoPoint));
				editor.fireMapChanged(this, MapEvent.PROP_OBJECTS);
			}
			else
			{
				setTemplate(null);
			}
			return;
		}
		
		if (event.selection != null)
		{
			System.out.println("[ObjectLayer] change Selection-Mode");
			event.manager.setSelectionMode(moveSelectionMode, true);
			
			this.savepointBeforMove = new ObjectSavePointSet();
			
			for(Selection s : event.manager.getSelectionList())
			{
				Object obj = s.getData();
				if (obj instanceof ObjectData)
				{
					ObjectData objData = (ObjectData) obj;
					savepointBeforMove.addSavePoint(
							new ObjectSavePoint(ObjectSavePoint.MOVE, objData, objData.x, objData.y));
				}
			}
		}
		else if (event.mouseEvent.button == 1)
		{
			System.out.println("[ObjectLayer] change Selection-Mode");
			event.manager.setSelectionMode(ITerrainEditor.CURSOR_SELECTION_MODE, true);
			if ((event.mouseEvent.stateMask & SWT.SHIFT) == 0)
			{
				setSelection(null);
			}
		}
	}

	@Override
	public void updateSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != ObjectLayer.this) return;
		
		if (event.manager.getSelectionMode() == moveSelectionMode)
		{
			for(Selection s : event.manager.getSelectionList())
			{
				Object obj = s.getData();
				if (obj instanceof ObjectData)
				{
					ObjectData objData = (ObjectData) obj;
					objData.x += moveSelectionMode.getDX();
					objData.y += moveSelectionMode.getDY();
				}
			}
			moveSelectionMode.resetDelta();
		}
		else if (event.manager.getSelectionMode() == followSelectionMode)
		{
			Selection s = event.manager.getSelection();
			ObjectData objData = (ObjectData) s.getData();
			
			if ((event.mouseEvent.stateMask & SWT.MOD1) != 0)
			{
				int gs = editor.getGridSize();
				objData.x = event.mouseEvent.x / gs * gs;
				objData.y = event.mouseEvent.y / gs * gs;
			}
			else
			{
				objData.x = event.mouseEvent.x;
				objData.y = event.mouseEvent.y;
			}
		}
	}

	@Override
	public void endSelection(SelectionEvent event)
	{
		if (editor.getActiveLayer() != ObjectLayer.this) return;
		
		if (event.manager.getSelectionMode() == moveSelectionMode)
		{
			ObjectSavePointSet newSet = new ObjectSavePointSet();
			
			for(Selection s : event.manager.getSelectionList())
			{
				Object obj = s.getData();
				if (obj instanceof ObjectData)
				{
					ObjectData objData = (ObjectData) obj;
					objData.x += moveSelectionMode.getDX();
					objData.y += moveSelectionMode.getDY();
					newSet.addSavePoint(new ObjectSavePoint(ObjectSavePoint.MOVE, objData, objData.x, objData.y));
				}
			}
			editor.addOperation(new MapOperation("Move Object", savepointBeforMove, newSet));
			savepointBeforMove = null;
			editor.fireMapChanged(this, MapEvent.PROP_OBJECTS);
			
			System.out.println("[ObjectLayer] change Selection-Mode");
			editor.getSelectionManager().setSelectionMode(
					new HighlightingSelectionMode(editor.getGLCanvas(), -1), true);
		}
	}
	
	private class ObjectSavePointSet implements MapSavePoint
	{
		private List<ObjectSavePoint> savePoints = new ArrayList<ObjectSavePoint>(8);
		
		public void addSavePoint(ObjectSavePoint savePoint)
		{
			savePoints.add(savePoint);
		}

		@Override
		public void apply()
		{
			for(ObjectSavePoint sp : savePoints)
			{
				sp.apply();
			}
		}
	}
	
	private class ObjectSavePoint implements MapSavePoint
	{
		public static final int CREATE	= 1;
		public static final int DELETE	= 2;
		public static final int MOVE	= 3;
		
		private int type;
		private ObjectData obj;
		private int x;
		private int y;
		
		public ObjectSavePoint(int type, ObjectData obj, int x, int y)
		{
			this.type = type;
			this.obj = obj;
			this.x = x;
			this.y = y;
		}

		@Override
		public void apply()
		{
			switch(type)
			{
				case CREATE:
					map.objects.put(obj.id, obj);
					break;
				case DELETE:
					map.objects.remove(obj.id);
					break;
				case MOVE:
					obj.x = x;
					obj.y = y;
					break;
			}
		}
	}
	
	private class SelectionAction extends Action
	{
		private ObjectData data;
		
		public SelectionAction(ObjectData data)
		{
			super("Objekt: " + data.name);
			this.data = data;
		}

//		@Override
//		public ImageDescriptor getImageDescriptor()
//		{
//			ResourceProvider rp = editor.getProject().getService(ResourceProvider.class);
//			try
//			{
//				Resource res = rp.getResource(ResourceManager.KEY_GRAPHICS, getObjectGraphic(data).getFilename());
//				if (res == null) return null;
//				
//				return ImageDescriptor.createFromURL(res.getURL());
//			}
//			catch (ResourceException e)
//			{
//				e.printStackTrace();
//			}
//			return null;
//		}

		@Override
		public void run()
		{
			setSelection(new StructuredSelection(data));
		}
	}

	@Override
	public void keyActionPerformed(KeyEvent ev)
	{
		if (ev.keyCode == SWT.DEL)
		{
			deleteAction.run();
		}
	}

	@Override
	public void activated()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivated()
	{
		cursorTemplate = null;
		setSelection(StructuredSelection.EMPTY);
	}
}
