package cuina.database.ui;

import cuina.database.ui.DatabaseUtil.ActionProvider;
import cuina.database.ui.internal.DataContentProvider;
import cuina.database.ui.internal.DataLabelProvider;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class DatabaseViewer extends TreeViewer
{
	private String tableName;
//	private IProject project;
	private DataContentProvider contentProvider;
	private ActionProvider actionProvider;
	private MenuManager contextMenuManager;
	
	public DatabaseViewer(Composite parent, String dbName, boolean editorAction)
	{
		this(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, dbName, editorAction);
	}
	
	public DatabaseViewer(Composite parent, int style, String tableName, boolean editorAction)
	{
		super(parent, style);
		this.tableName = tableName;

//		this.editorAction = editorAction;
		
		contentProvider = new DataContentProvider();
		setContentProvider(contentProvider);
		setLabelProvider(new DataLabelProvider());
		makeActions();
		createContextMenu();
	}
	
//	public boolean checkSelection(ISelection selection)
//	{
//		if (selection == null || !(selection instanceof IStructuredSelection)) return false;
//
//			Object obj = ((IStructuredSelection) selection).getFirstElement();
//			if (obj != null && obj instanceof IResource)
//			{
//				setProject( ((IResource) obj).getProject());
//				return true;
//			}
//			
//			return false;
//	}
	
//	/**
//	 * Setzt das Projekt, von dem die Entsprechende Datenbank gelesen werden soll.
//	 * Dazu wird ein DatabaseInput erstellt und dem Viewer zugewiesen.
//	 * @param project Projekt.
//	 */
//	@Override
//	public void setInput(Object input)
//	{
////		if (this.project == project) return;
////		
////		this.project = project;
//		try
//		{
//			setInput(new DatabaseInput(project, tableName, null));
//		}
//		catch (ResourceException e)
//		{
//			e.printStackTrace();
//		}
//	}
	
//	public IProject getProject()
//	{
//		return project;
//	}
	
	public String getTableName()
	{
		return tableName;
	}
	
	@Override
	public DataContentProvider getContentProvider()
	{
		return (DataContentProvider) super.getContentProvider();
	}
	
	@Override
	public TreeSelection getSelection()
	{
		return (TreeSelection) super.getSelection();
	}
	
	public void addDataChangeListener(TreeListener l)
	{
		actionProvider.addDataChangeListener(l);
	}
	
	public void removeDataChangeListener(TreeListener l)
	{
		actionProvider.removeDataChangeListener(l);
	}

	private void makeActions()
	{
		actionProvider = DatabaseUtil.getDefaultActions(this);
		actionProvider.enableEditorActions();
		actionProvider.enableClipboardActions();
		actionProvider.enableDragAndDrop();
	}
	
	public IMenuManager getContextMenuManager()
	{
		return contextMenuManager;
	}
	
	private void createContextMenu()
	{
		contextMenuManager = new MenuManager("#PopupMenu");
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener()
		{
			@Override
			public void menuAboutToShow(IMenuManager manager)
			{
				actionProvider.fillActions(manager);
			}
		});
		Menu menu = contextMenuManager.createContextMenu(getControl());
		getControl().setMenu(menu);
	}
}
