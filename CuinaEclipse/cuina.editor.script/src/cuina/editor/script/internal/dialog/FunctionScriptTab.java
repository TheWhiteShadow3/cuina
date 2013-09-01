package cuina.editor.script.internal.dialog;

import cuina.editor.script.dialog.CommandTab;
import cuina.editor.script.library.Function;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.Reciver;
import cuina.editor.script.library.TreeDefinition;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.library.TreeLibrary.LibraryTree;
import cuina.editor.script.library.TreeLibraryContentProvider;
import cuina.editor.script.library.TreeLibrarySorter;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FunctionScriptTab implements CommandTab, ISelectionChangedListener, IDoubleClickListener
{
	private ScriptDialogContext context;
	private TreeViewer functionViewer;
	private Node node;
	private Label info;

	@Override
	public Node getNode()
	{
		return node;
	}

	@Override
	public void init(ScriptDialogContext context)
	{
		this.context = context;
	}

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new FillLayout());
		SashForm splitter = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
		
		functionViewer = new TreeViewer(splitter, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		functionViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		functionViewer.getTree().setHeaderVisible(true);
		functionViewer.getTree().setLinesVisible(true);
		functionViewer.setContentProvider(new TreeLibraryContentProvider());
		functionViewer.setSorter(new TreeLibrarySorter());
		functionViewer.addSelectionChangedListener(this);
		functionViewer.addDoubleClickListener(this);
		
		TreeViewerColumn column = new TreeViewerColumn(functionViewer, SWT.DEFAULT);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Name");
		column.setLabelProvider(new TreeLibrary.NameProvider());
		
		column = new TreeViewerColumn(functionViewer, SWT.DEFAULT);
		column.getColumn().setWidth(200);
		column.getColumn().setText("Parameter");
		column.setLabelProvider(new ParameterListLabelProvider());
		
		column = new TreeViewerColumn(functionViewer, SWT.DEFAULT);
		column.getColumn().setWidth(100);
		column.getColumn().setText("Rückgabewert");
		column.setLabelProvider(new TreeLibrary.TypeProvider());

		info = new Label(splitter, SWT.BORDER | SWT.WRAP);
		info.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		splitter.setWeights(new int[] {80, 20});
	}
	
	@Override
	public void setNode(Node node, ValueDefinition parameter)
	{
		this.node = node;
		TreeLibrary treeLibrary = context.getTreeLibrary();
		LibraryTree tree = treeLibrary.createLibraryTree(context.getPosition());
		functionViewer.setInput(tree);
		functionViewer.expandToLevel(1);
		
		if (node instanceof CallNode)
		{
			Reciver reciver;
			Node next = ((CallNode) node).getNextNode();
			if (next != null)
				reciver = tree.get(((INamed) next).getName());
			else
				reciver = tree.get(null);
			if (reciver == null) return;
			
			TreeDefinition def = reciver.entries.get(((CallNode) node).getName());
			functionViewer.setSelection(new StructuredSelection(def), true);
		}
	}
	
	private Function getSelectedFunction()
	{
		IStructuredSelection selection = (IStructuredSelection) functionViewer.getSelection();
		Object obj = selection.getFirstElement();
		if (obj instanceof Function)
			return (Function) obj;
		else
			return null;
	}
	
	@Override
	public void doubleClick(DoubleClickEvent event)
	{
		Function func = getSelectedFunction();
		if (func == null) return;
		
		CallNode callNode;
		if (node instanceof CallNode && compareCallNode((CallNode) node, func))
		{
			callNode = (CallNode) node;
//			for (int i = callNode.getArgument().size(); i < ((FunctionDefinition) def).params.size(); i++)
//			{
//				callNode.addArgument(new EmptyNode());
//			}
		}
		else
		{
			callNode = createCallNode(func);
		}
		
		CommandDialog dialog = new CommandDialog(context, callNode, func.getLabel());
		if (dialog.open() == Dialog.OK)
		{
			this.node = dialog.getNode();
			context.valueChanged(this, node);
		}
	}
	
	private boolean compareCallNode(CallNode node, Function def)
	{
		if (!node.getName().equals(def.getID())) return false;
		
		INamed next = (INamed) node.getNextNode();
		if (next == null ^ def.getParent().getID().isEmpty()) return false;
		if (!next.getName().equals(def.getParent().getID())) return false;
		
		return true;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		Function func = getSelectedFunction();
		if (func == null) return;
		
		FunctionDefinition def = func.getFunctionDefinition();
		if (def != null)
			info.setText(def.help != null ? def.help : "");
		else
			info.setText("");
	}
	
	private CallNode createCallNode(Function definition)
	{
		CallNode callNode = new CallNode(definition.getID());
		
		int paramCount = 0;
		if (definition.getFunctionDefinition() != null)
			paramCount = definition.getFunctionDefinition().params.size();
		else
			paramCount = definition.getNode().getArgument().size();
		
		for (int i = 0; i < paramCount; i++)
		{
			callNode.addArgument(new EmptyNode());
		}
		if (definition.getParent().getID() != null)
		{
			callNode.setNextNode( new ConstNode(definition.getParent().getID()) );
		}
		
		return callNode;
	}
	
//	@Override
//	public void handleEvent(Event event)
//	{
//		int index = functionList.getSelectionIndex();
//		if (index == -1) return;
//		
//		CallNode callNode = null;
//		if (node instanceof CallNode && event.type == SWT.MouseDoubleClick)
//		{
//			CommandDialog dialog = new CommandDialog(context, node, functions.get(index).getLabel());
//			if (dialog.open() == Dialog.OK)
//			{
//				callNode = (CallNode) dialog.getNode();
//			}
//		}
//		else
//		{
//			String name = functions.get(index).id;
//			callNode = new CallNode(name);
//			
//			if (index < functions.size())
//			{
//				List<ValueDefinition> params = functions.get(index).params;
//				for (int i = 0; i < params.size(); i++)
//				{
//					callNode.addArgument(new EmptyNode());
//				}
//			}
//			String help = functions.get(index).help;
//			info.setText(help != null ? help : "");
//		}
//		if (callNode != null)
//		{
//			node = callNode;
//			context.valueChanged(this, node);
//		}
//	}

	@Override
	public void setEnabled(boolean value)
	{
		functionViewer.getControl().setEnabled(value);
	}

	@Override
	public String getName()
	{
		return "Funktion";
	}
	
	static class ParameterListLabelProvider extends ColumnLabelProvider
	{
		@Override
		public String getText(Object element)
		{
			if (element instanceof Function)
			{
				StringBuilder builder = new StringBuilder();
				builder.append('(');
				FunctionDefinition func = ((Function) element).getFunctionDefinition();
				if (func != null)
				{
					
					for (int i = 0; i < func.params.size(); i++)
					{
						if (i > 0) builder.append(", ");
						builder.append(func.params.get(i).type);
					}
				}
				else
				{
					ListNode args = ((Function) element).getNode().getArgument();
					for (int i = 0; i < args.size(); i++)
					{
						if (i > 0) builder.append(", ");
						builder.append('?');
					}
				}
				builder.append(')');
				return builder.toString();
			}
			return "";
		}
	}
}
