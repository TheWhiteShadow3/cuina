package cuina.editor.script.internal.dialog;

import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.ArrayNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.IParameter;
import cuina.editor.script.ruby.ast.Node;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

public class ArgumentList implements Listener
{
	private static final int MAX_ARGUMENT_COUNT = 20;
	
	private ScriptDialogContext context;
	private Listener callBack;
	private Node node;
	private boolean fixedSize = false;
	private List argList;
	private Button cmdAddArg;
	private Button cmdRemoveArg;
//	private int selectedIndex = -1;
	private ArrayList<Node> arguments = new ArrayList<Node>(8);
	
	public ArgumentList(ScriptDialogContext context, Listener callBack)
	{
		this.context = context;
		this.callBack = callBack;
	}
	
	public boolean isFixedSize()
	{
		return fixedSize;
	}

	public void setFixedSize(boolean value)
	{
		this.fixedSize = value;
		updateButtons();
	}
	
	public int getCount()
	{
		return arguments.size();
	}

	public Node getSelectedNode()
	{
		int index = argList.getSelectionIndex();
		if (index == -1) return null;
		
		return arguments.get(index);
	}
	
	public int getSelectedIndex()
	{
		return argList.getSelectionIndex();
	}
	
	public List getList()
	{
		return argList;
	}
	
	public void createControls(Composite parent)
	{
		Composite block = new Composite(parent, SWT.NONE);
		block.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 2));
		block.setLayout(new GridLayout(2, true));
		
		argList = new org.eclipse.swt.widgets.List(block, SWT.DEFAULT);
		argList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		argList.addListener(SWT.Selection, this);
		
		cmdAddArg = new Button(block, SWT.PUSH);
		cmdAddArg.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		cmdAddArg.setText("Neu");
		cmdAddArg.addListener(SWT.Selection, this);
		
		cmdRemoveArg = new Button(block, SWT.PUSH);
		cmdRemoveArg.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		cmdRemoveArg.setText("Entf.");
		cmdRemoveArg.addListener(SWT.Selection, this);
	}
	
	public void setNode(Node node, FunctionDefinition function)
	{
		this.node = node;
		arguments.clear();
		if (node instanceof CallNode && function != null)
		{
			CallNode callNode = (CallNode) node;
			String[] names = new String[function.params.size()];
			for (int i = 0; i < function.params.size(); i++)
			{
				arguments.add(callNode.getArgument().getChild(i));
				names[i] = function.params.get(i).getLabel();
			}
			argList.setItems(names);
		}
		else if (node instanceof IParameter)
		{
			Node argNode = ((IParameter) node).getArgument();
			if (argNode instanceof ArrayNode)
			{
				String[] names = new String[((ArrayNode) argNode).size()];
				for (int i = 0; i < names.length; i++)
				{
					arguments.add(((ArrayNode) argNode).getChild(i));
					names[i] = "Argument " + Integer.toString(i);
				}
				argList.setItems(names);
			}
			else
			{
				arguments.add(argNode);
				argList.setItems(new String[] {"Argument"});
			}
		}
		else
		{
			arguments.add(node);
			argList.setItems(new String[] {"Value"});
		}
		if (argList.getItemCount() > 0) argList.setSelection(0);
		updateButtons();
	}
	
	public void setArgument(int index, Node newNode)
	{
		if (node instanceof IParameter)
		{
			arguments.set(index, newNode);
			java.util.List<Node> args = ((IParameter) node).getArgument().getChildren();
			args.set(index, newNode);
		}
	}
	
	private void addArgument(String name)
	{
		if (node instanceof IParameter)
		{
			Node argNode = null;
			if (node instanceof DefNode)
				argNode = new ArgNode(name);
			else
				argNode = new EmptyNode();
			if (argNode != null)
			{
				arguments.add(argNode);
				((IParameter) node).addArgument(argNode);
			}
		}
		else
		{
			throw new UnsupportedOperationException("Node does not support arguments.");
		}
		
		argList.add(name);
		argList.select(argList.getItemCount() - 1);
		fireSelectionChanged(new Event());
		updateButtons();
	}
	
	private void removeArgument()
	{
		int index = argList.getSelectionIndex();
		if (node instanceof IParameter)
		{
			((IParameter) node).removeArgument(index);
		}
		arguments.remove(index);
		argList.remove(index);
		argList.select(index - 1);
		fireSelectionChanged(new Event());
		updateButtons();
	}
	
	private void updateButtons()
	{
		cmdAddArg.setEnabled(!fixedSize && arguments.size() < MAX_ARGUMENT_COUNT);
		if (node instanceof AsgNode)
			cmdRemoveArg.setEnabled(!fixedSize && arguments.size() > 1);
		else
			cmdRemoveArg.setEnabled(!fixedSize && arguments.size() > 0);
	}
	
	@Override
	public void handleEvent(Event event)
	{
		if (event.widget == argList)
		{
			fireSelectionChanged(event);
		}
		else if (event.widget == cmdAddArg)
		{
			InputDialog dialog = new InputDialog(context.getShell(), "Argument-Name", "Name:", "arg", null);
			if (dialog.open() == Dialog.OK)
			{
				addArgument(dialog.getValue());
			}
		}
		else if (event.widget == cmdRemoveArg)
		{
			removeArgument();
		}
		else
			throw new IllegalAccessError("Missbrauch mein scheiß Listener-IF nicht!");
	}
	
	private void fireSelectionChanged(Event event)
	{
		event.widget = argList;
		if (argList.getSelectionIndex() != -1)
			event.data = getSelectedNode();
		
		callBack.handleEvent(event);
	}
}
