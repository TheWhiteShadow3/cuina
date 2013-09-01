package cuina.database.ui;

import cuina.database.DataTable;
import cuina.database.DatabaseInput;
import cuina.database.DatabaseObject;
import cuina.database.NamedItem;
import cuina.database.ui.internal.DataContentProvider;
import cuina.database.ui.internal.DataLabelProvider;
import cuina.database.ui.internal.DatabaseEditor;
import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;
import cuina.database.ui.tree.TreeNode;
import cuina.database.ui.tree.TreeRoot;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class DatabaseUtil
{
	private static IWorkbenchAdapter sharedWorkbenchAdapter;
	
	private DatabaseUtil()
	{}

    /**
     * Gibt eine Liste der Element-Namen aus der Auswahl zurück. Die Liste ist
     * durch Zeilenumbrüchen getrennt.
     * 
     * @param selection
     *            Auswahl
     * @return Auswahl-Liste
     */
    public static String getTextRepresentation(ISelection selection)
    {
        StringBuilder builder = new StringBuilder();
        if (selection instanceof StructuredSelection)
        {
            Object[] elements = ((StructuredSelection) selection).toArray();
            for (int i = 0; i < elements.length; i++)
            {
                if (i > 0) builder.append("\n");
                if (elements[i] instanceof NamedItem)
                    builder.append(((NamedItem) elements[i]).getName());
                else
                {
                    IWorkbenchAdapter adapter = (IWorkbenchAdapter) getAdapter(elements[i], IWorkbenchAdapter.class);
                    if (adapter != null)
                        builder.append(adapter.getLabel(elements[i]));
                    else
                        builder.append(elements[i].toString());
                }
            }
        }
        return builder.toString();
    }
    
    public static Object getAdapter(Object sourceObject, Class adapterType)
    {
        Assert.isNotNull(adapterType);
        if (sourceObject == null) return null;
        if (adapterType.isInstance(sourceObject)) return sourceObject;
 
        if (sourceObject instanceof IAdaptable)
        {
            IAdaptable adaptable = (IAdaptable) sourceObject;
 
            Object result = adaptable.getAdapter(adapterType);
            if (result != null)
            {
                // Sanity-check
                Assert.isTrue(adapterType.isInstance(result));
                return result;
            }
        }
        return null;
    }
 
    /**
     * Gibt einen WorkbenchAdapter für Cuina Datenbank-Elemente zurück.
     * @return WorkbenchAdapter für Cuina Datenbank-Elemente.
     */
    public static IWorkbenchAdapter getWorkbenchAdapter()
    {
    	if (sharedWorkbenchAdapter == null)
    	{
    		sharedWorkbenchAdapter = new IWorkbenchAdapter()
	        {
	            private DataContentProvider contentProvider = new DataContentProvider();
	            private DataLabelProvider labelProvider = new DataLabelProvider();
	            
	            @Override
	            public Object[] getChildren(Object o)
	            {
	                return contentProvider.getElements(o);
	            }
	 
	            @Override
	            public Object getParent(Object o)
	            {
	                return contentProvider.getParent(o);
	            }
	            
	            @Override
	            public ImageDescriptor getImageDescriptor(Object object)
	            {
	                return ImageDescriptor.createFromImage(labelProvider.getImage(object));
	            }
	 
	            @Override
	            public String getLabel(Object o)
	            {
	                return labelProvider.getText(o);
	            }
	        };
    	}
        return sharedWorkbenchAdapter;
    }

	public static ActionProvider getDefaultActions(StructuredViewer viewer)
	{
		return new ActionProvider(viewer);
	}

	public static class ActionProvider
	{
		private StructuredViewer viewer;
		private Action openEditorAction;
		private Action newElement;
		private Action newGroup;
		private Action copyAction;
//		private Action cutAction;
		private Action pasteAction;
		private Action deleteAction;
		private Action changeKeyAction;
		private ArrayList<TreeListener> listeners = new ArrayList<TreeListener>();
		
		ActionProvider(StructuredViewer viewer)
		{
			this.viewer = viewer;
		}
		
		public void fillActions(IMenuManager manager)
		{
			manager.add(new Separator(IWorkbenchActionConstants.NEW_EXT));
			if (newElement != null) manager.add(newElement);
			if (newGroup != null) manager.add(newGroup);
			manager.add(new Separator(IWorkbenchActionConstants.EDIT_START));
			if (openEditorAction != null) manager.add(openEditorAction);
			if (changeKeyAction != null) manager.add(changeKeyAction);
			manager.add(new Separator(IWorkbenchActionConstants.M_EDIT));
//			if (cutAction != null) manager.add(cutAction);
			if (copyAction != null) manager.add(copyAction);
			if (pasteAction != null) manager.add(pasteAction);
			if (deleteAction != null) manager.add(deleteAction);
			manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			manager.add(new PropertyDialogAction(new SameShellProvider(viewer.getControl()), viewer));
		}
		
		public void addDataChangeListener(TreeListener l)
		{
			if (listeners.contains(l)) return;
			listeners.add(l);
		}
		
		public void removeDataChangeListener(TreeListener l)
		{
			listeners.remove(l);
		}

		public void enableDragAndDrop()
		{
			activateDragAndDrop();
			addDropListener();
		}
		
		private void activateDragAndDrop()
		{
			DragSource source = (DragSource) viewer.getControl().getData(DND.DRAG_SOURCE_KEY);
			if (source == null)
			{
				int ops = DND.DROP_COPY | DND.DROP_MOVE;
				source = new DragSource(viewer.getControl(), ops);
			}
			
			source.setTransfer(new Transfer[] {LocalSelectionTransfer.getTransfer(), TextTransfer.getInstance()});
			source.addDragListener(new DragSourceListener()
			{
				private TreeNode[] parents;

				@Override
				public void dragStart(DragSourceEvent event)
				{
					if (getSelection().isEmpty())
						event.doit = false;
					else
						event.detail = DND.DROP_MOVE;
					System.out.println("dragStart");
				}

				@Override
				public void dragSetData(DragSourceEvent event)
				{
					if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType))
					{
						event.data = getParentSelection();
						LocalSelectionTransfer.getTransfer().setSelection(getSelection());
						System.out.println("dragSetData: LocalSelectionTransfer");

						this.parents = getParentNodes();
					}
					else if (TextTransfer.getInstance().isSupportedType(event.dataType))
					{
						event.data = DatabaseUtil.getTextRepresentation(getSelection());
						System.out.println("dragSetData: TextTransfer");
					}
				}

				@Override
				public void dragFinished(DragSourceEvent event)
				{
					if (parents == null || event.detail != DND.DROP_MOVE) return;
					
					viewer.refresh();
					System.out.println("DND finish");
				}
			});
		}

		private void addDropListener()
		{
			DropTarget target = (DropTarget) viewer.getControl().getData(DND.DROP_TARGET_KEY);
			if (target == null)
			{
				int ops = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
				target = new DropTarget(viewer.getControl(), ops);
			}
			
			target.setTransfer(new Transfer[] { LocalSelectionTransfer.getTransfer(), TextTransfer.getInstance() });
			target.addDropListener(new ViewerDropAdapter(viewer)
			{
				private TreeGroup target;
				private int index;
				
				@Override
				public boolean validateDrop(Object target, int operation, TransferData transferData)
				{
					if (target instanceof TreeNode)
					{
						// drop muss reaktiviert werden.
						overrideOperation(DND.DROP_MOVE);
						getCurrentEvent().detail = DND.DROP_MOVE;
						
						return LocalSelectionTransfer.getTransfer().isSupportedType(transferData)
								|| TextTransfer.getInstance().isSupportedType(transferData);
					}
					return false;
				}
				
				@Override
				public boolean performDrop(Object data)
				{
					if (data instanceof ISelection)
					{
						setTargetLocation();
						TreeNode[] nodes = getNodes((ISelection) data);
						if (nodes.length == 0) return false;
						
						DataTable<?> srcTable = nodes[0].getTable();
						DataTable<?> dstTable = ((TreeNode) target).getTable();
						
						if (srcTable.getElementClass() != dstTable.getElementClass()) return false;
						if (srcTable == dstTable)
							getCurrentEvent().detail = DND.DROP_MOVE;
						else
							getCurrentEvent().detail = DND.DROP_COPY;
						
//						System.out.println("DropTarget: " + target + ", index: " + index);
						
						boolean result = false;
						for (int i = nodes.length - 1; i >= 0; i--)
						{
							TreeNode node = nodes[i];
							if (node.getParent() != target)
							{
								node.move(target, index);
								result |= true;
							}
						}
						if (result)
						{
							fireNodesChanged(nodes);
							return true;
						}
					}
//					else if (data instanceof String)
//					{
//						TreeGroup target = getTargetNode();
//						if (target == null) return false;
//
//						String[] lines = ((String) data).split("\n");
//						for (String line : lines)
//						{
//							target.addGroup(line);
//						}
//						fireTreeChanged(null);
//						return true;
//					}
					return false;
				}
				
				private TreeNode[] getNodes(ISelection selection)
				{
					IStructuredSelection ss = (IStructuredSelection) selection;
					return Arrays.copyOf(ss.toArray(), ss.size(), TreeNode[].class);
				}

				private void setTargetLocation()
				{
					TreeGroup node = (TreeGroup) getCurrentTarget();
					if (node == null) return;
					
					if (node.getParent() == null)
					{
						this.target = node;
						this.index = (getCurrentLocation() == LOCATION_AFTER) ? 0 : -1;
						return;
					}
					else if (node instanceof TreeGroup)
					{
						if (getCurrentLocation() == LOCATION_ON)
						{
							this.target = node;
							this.index = -1;
						}
						else
						{
							this.target = node.getParent();
							this.index = getIndex(node);
							if (getCurrentLocation() == LOCATION_AFTER) index++;
						}
					}
					else
					{
						this.target = node.getParent();
						this.index = getIndex(node);
						
						if (getCurrentLocation() != LOCATION_BEFORE) index++;
					}
				}
				
				private int getIndex(TreeNode node)
				{
					TreeNode[] children = node.getParent().getChildren();
					int i;
					for (i = 0; children[i] != node; i++); // no body
					return i;
				}
			});
		}
		
		public void enableEditorActions()
		{
			openEditorAction = new Action()
			{
				@Override
				public void run()
				{
					Object obj = getSelection().getFirstElement();
					if (obj instanceof TreeDataNode)
					{
						String key = ((TreeDataNode) obj).getData().getKey();
						DataTable table =  ((TreeDataNode) obj).getTable();
						DatabaseInput input = new DatabaseInput(getFile(table), key);
					    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					    try
						{
							page.openEditor(input, DatabaseEditor.ID);
						}
						catch (PartInitException e)
						{
							e.printStackTrace();
						}
					}
				}
			};
			openEditorAction.setText("Edit");
			openEditorAction.setAccelerator(SWT.CONTROL | 'e');
			
			newElement = new Action()
			{
				@Override
				public void run()
				{
					TreeRoot root = getTreeRoot();
					String name = root.getTable().createAviableKey(null);
					
					InputDialog dialog = new InputDialog(viewer.getControl().getShell(),
							"Element Name", "Name des Elements", name, null);
					if (dialog.open() == Dialog.CANCEL) return;
					
					name = dialog.getValue();
					TreeGroup parent = getFirstSelectedGroup();
					if (parent == null) parent = root;
					try
					{
						DatabaseObject obj = (DatabaseObject) root.getTable().getElementClass().newInstance();
						obj.setKey(root.getTable().createAviableKey(name));
						obj.setName(name);
						TreeDataNode node = parent.addObject(obj);
						fireNodesAdded(new TreeNode[] {node});
					}
					catch (InstantiationException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			};
			newElement.setText("New Element");
			newElement.setAccelerator(SWT.CONTROL | 'n');
			
			newGroup = new Action()
			{
				@Override
				public void run()
				{
					InputDialog dialog = new InputDialog(viewer.getControl().getShell(),
							"Group Name", "Name der Gruppe", "newGroup", null);
					if (dialog.open() == Dialog.CANCEL) return;
					
					String name = dialog.getValue();
					TreeGroup parent = getFirstSelectedGroup();
					if (parent == null) parent = getTreeRoot();
					TreeGroup node = parent.addGroup(name);
					fireNodesAdded(new TreeNode[] {node});
				}
			};
			newGroup.setText("New Group");
			newGroup.setAccelerator(SWT.CONTROL | 'g');
//			newGroup.setImageDescriptor(ImageDescriptor.createFromImage(img));
			
			changeKeyAction = new Action()
			{
				@Override
				public void run()
				{
					TreeNode node = (TreeNode) getSelection().getFirstElement();
					if (node == null || node instanceof TreeGroup) return;
					TreeDataNode objectNode = (TreeDataNode) node; 
					
					InputDialog dialog = new InputDialog(viewer.getControl().getShell(),
							"Change Key", "Neuer Schlüssel:", objectNode.getKey(), null);
					dialog.open();
					String newKey = dialog.getValue();
					objectNode.changeKey(newKey);
					fireNodesChanged(new TreeNode[] {objectNode});
				}
			};
			changeKeyAction.setText("Change Key...");
			changeKeyAction.setAccelerator(SWT.F2);
			
			
			viewer.addDoubleClickListener(new IDoubleClickListener()
			{
				@Override
				public void doubleClick(DoubleClickEvent event)
				{
					openEditorAction.run();
				}
			});
		}
		
		public void enableClipboardActions()
		{
			copyAction = new Action()
			{
				@Override
				public void run()
				{
					selectionToClipboard();
				}
			};
			copyAction.setText("Copy");
			copyAction.setAccelerator(SWT.CONTROL | 'c');
			copyAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
			
//			cutAction = new Action()
//			{
//				@Override
//				public void run()
//				{
//					selectionToClipboard();
//				}
//			};
//			cutAction.setText("Cut");
//			cutAction.setAccelerator(SWT.CONTROL | 'x');
//			cutAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_CUT));

			pasteAction = new Action()
			{
				@Override
				public void run()
				{
					Clipboard cb = new Clipboard(Display.getDefault());

					StructuredSelection s = (StructuredSelection) cb.getContents(LocalSelectionTransfer.getTransfer());
					cb.dispose();
					if (s == null) return;

					Object obj = getSelection().getFirstElement();
					TreeNode parent;
					if (obj != null)
					{
						if (obj instanceof TreeGroup)
							parent = (TreeGroup) obj;
						else
							parent = ((TreeDataNode) obj).getParent();
					}
					else parent = getTreeRoot();
					
					if (!(parent instanceof TreeGroup))
						throw new IllegalArgumentException("Can not move an Instance of " + parent.getClass());

					Object[] array = s.toArray();
					TreeNode[] nodes = new TreeNode[array.length];
					for (int i = 0; i < array.length; i++)
					{
						TreeNode node = (TreeNode) array[i];
						node.copy((TreeGroup) parent);
						nodes[i] = node;
					}
					fireNodesAdded(nodes);
				}
			};
			pasteAction.setText("Paste");
			pasteAction.setAccelerator(SWT.CONTROL | 'v');
			pasteAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
			
			deleteAction = new Action()
			{
				@Override
				public void run()
				{
					TreeNode[] nodes = getParentNodes();
					TreeRoot root = nodes[0].getRoot();
					for (int i = 0; i < nodes.length; i++)
					{
						nodes[i].remove();
					}
					fireNodesRemoved(root, nodes);
				}
			};
			deleteAction.setText("Delete");
			deleteAction.setAccelerator(SWT.DEL);
			deleteAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		}

		private void selectionToClipboard()
		{
			Clipboard cb = new Clipboard(Display.getDefault());
			try
			{
				
				StructuredSelection selection = getParentSelection();
				// Text
				TextTransfer textTransfer = TextTransfer.getInstance();
				String text = getTextRepresentation(selection);
				// Auswahl
				
				LocalSelectionTransfer selectionTransfer = LocalSelectionTransfer.getTransfer();
				selectionTransfer.setSelection(selection);

				cb.setContents(new Object[]   { text, 		  selection },
							   new Transfer[] { textTransfer, selectionTransfer });
			}
			finally
			{
				cb.dispose();
			}
		}
		
		private TreeRoot getTreeRoot()
		{
			Object obj = null;
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			if (selection != null)
			{
				obj = selection.getFirstElement();
			}
			else
			{
				IStructuredContentProvider contentProvider = (IStructuredContentProvider) viewer.getContentProvider();
				Object[] elements = contentProvider.getElements(viewer.getInput());
				if (elements.length > 0) obj = elements[0];
			}
			if (obj instanceof TreeNode)
				return ((TreeNode) obj).getRoot();
			else
				return null;
		}
		
		private IFile getFile(DataTable table)
		{
			return table.getDatabase().getProject().getProject().getFile(table.getFileName());
		}

		private ImageDescriptor getSharedImageDescriptor(String id)
		{
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(id);
		}

		public TreeSelection getSelection()
		{
			return (TreeSelection) viewer.getSelection();
		}
		
		public TreeNode getFirstSelectedElement()
		{
			return (TreeNode) getSelection().getFirstElement();
		}
		
		public TreeGroup getFirstSelectedGroup()
		{
			TreeNode node = getFirstSelectedElement();
			if (node instanceof TreeDataNode)
				node = node.getParent();
			return (TreeGroup) node;
		}

		private StructuredSelection getParentSelection()
		{
			return new StructuredSelection(getParentNodes());
		}
		
		private TreeNode[] getParentNodes()
		{
			Object[] array = getSelection().toArray();
			ArrayList<TreeNode> data = new ArrayList<TreeNode>(array.length);

			sourceLoop:
			for (int i = 0; i < array.length; i++)
			{
				TreeNode node = (TreeNode) array[i];

				for (int j = 0; j < array.length; j++)
				{
					if (node == array[j]) continue;
					if (((TreeNode) array[j]).isAncestor(node)) continue sourceLoop;
				}
				data.add(node);
			}
			return data.toArray(new TreeNode[data.size()]);
		}
		
		private void fireNodesChanged(TreeNode[] nodes)
		{
			for(int i = 0; i < listeners.size(); i++)
			{
				listeners.get(i).nodesChanged(viewer, nodes[0].getRoot(), nodes);
			}
			viewer.refresh();
		}
		
		private void fireNodesAdded(TreeNode[] nodes)
		{
			for(int i = 0; i < listeners.size(); i++)
			{
				listeners.get(i).nodesAdded(viewer, nodes[0].getRoot(), nodes);
			}
			viewer.setSelection(new StructuredSelection(nodes), true);
			viewer.refresh();
		}
		
		private void fireNodesRemoved(TreeRoot root, TreeNode[] nodes)
		{
			for(int i = 0; i < listeners.size(); i++)
			{
				listeners.get(i).nodesRemoved(viewer, root, nodes);
			}
			viewer.refresh();
		}
		
//		private void save(DataTable table)
//		{
//			Database db = table.getDatabase();
//			try
//			{
//				db.saveTable(table);
//				db.saveMetaData();
//			}
//			catch (ResourceException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		
//		private void saveTree(DataTable table)
//		{
//			try
//			{
//				table.getDatabase().saveMetaData();
//			}
//			catch (ResourceException e)
//			{
//				e.printStackTrace();
//			}
//		}
	}
}
