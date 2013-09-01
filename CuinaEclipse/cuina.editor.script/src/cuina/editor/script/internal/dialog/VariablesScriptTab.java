package cuina.editor.script.internal.dialog;

import cuina.editor.script.dialog.CommandTab;
import cuina.editor.script.library.Reciver;
import cuina.editor.script.library.TreeDefinition;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.library.TreeLibrary.LibraryTree;
import cuina.editor.script.library.TreeLibraryContentProvider;
import cuina.editor.script.library.TreeLibrarySorter;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.library.Variable;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.IHasNext;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.VarNode;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class VariablesScriptTab implements CommandTab, ISelectionChangedListener
{
	private ScriptDialogContext context;
	private TreeViewer varViewer;
	private Node node;

	@Override
	public void init(ScriptDialogContext context)
	{
		this.context = context;
	}

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new GridLayout(1, false));
		
		varViewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		varViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		varViewer.getTree().setHeaderVisible(true);
		varViewer.getTree().setLinesVisible(true);
		varViewer.setContentProvider(new TreeLibraryContentProvider());
		varViewer.setSorter(new TreeLibrarySorter());
		varViewer.addSelectionChangedListener(this);
		
		TreeViewerColumn column = new TreeViewerColumn(varViewer, SWT.DEFAULT);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Name");
		column.setLabelProvider(new TreeLibrary.NameProvider());
		
		column = new TreeViewerColumn(varViewer, SWT.DEFAULT);
		column.getColumn().setWidth(150);
		column.getColumn().setText("Typ");
		column.setLabelProvider(new TreeLibrary.TypeProvider());
		
		column = new TreeViewerColumn(varViewer, SWT.DEFAULT);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Gültigkeit");
		column.setLabelProvider(new TreeLibrary.ScopeProvider());
	}

	@Override
	public Node getNode()
	{
		return node;
	}

	@Override
	public void setNode(Node node, ValueDefinition parameter)
	{
		this.node = node;
		TreeLibrary treeLibrary = context.getTreeLibrary();
		LibraryTree tree = treeLibrary.findVariables(context.getPosition(), true);
		varViewer.setInput(tree);
		varViewer.expandToLevel(1);
		
		if (node instanceof VarNode)
		{
			Reciver reciver;
			Node next = ((VarNode) node).getNextNode();
			if (next != null)
				reciver = tree.get(((INamed) next).getName());
			else
				reciver = tree.get(null);
			if (reciver == null) return;
			
			TreeDefinition def = reciver.entries.get(((VarNode) node).getName());
			varViewer.setSelection(new StructuredSelection(def), true);
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		Object obj = selection.getFirstElement();
		if (!(obj instanceof Variable)) return;
		
		Variable var = (Variable) obj;
		IHasNext newNode;
		if ( Character.isUpperCase(var.getID().charAt(0)) )
			newNode = new ConstNode(var.getID());
		else
			newNode = new VarNode(var.getID(), var.getScope());
		
		if (var.getParent().getID() != null)
			newNode.setNextNode( new ConstNode(var.getParent().getID()) );
		this.node = newNode;
		
		context.valueChanged(this, node);
	}

	@Override
	public void setEnabled(boolean value)
	{
		varViewer.getTree().setEnabled(value);
	}

	@Override
	public String getName()
	{
		return "Variable";
	}
	
//	static class VariableScopeProvider extends TreeLibrary.ScopeProvider
//	{
//		@Override
//		public String getText(Object element)
//		{
//			if (element instanceof TreeDefinition)
//			{
//				INamed node = ((TreeDefinition) element).getNode();
//				
//				if (node instanceof ArgNode) return "argument";
//			}
//			return super.getText(element);
////					if (node instanceof VarNode)
////				{
////					VarNode varNode = (VarNode) node;
////					if (varNode.getNextNode() != null)
////						return ((INamed) varNode.getNextNode()).getName();
////					else
////						return getScopeString(varNode.getScope());
////				}
////				else
////				{
////					return ((Variable) element).getParent().getID();
////				}
////			}
////			return "";
//		}
//	}
}
