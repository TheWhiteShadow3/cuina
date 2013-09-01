package cuina.editor.script.internal.dialog;

import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.library.ClassDefinition;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.Reciver;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.library.TreeLibrary.LibraryTree;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.IHasNext;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class ObjectField implements Listener
{
	private TreeLibrary treeLibrary;
	private ScriptDialogContext context;
	private Listener callBack;
	private Node node;
	private String reciverName;
	private String targetName;
	private boolean editable = false;
	private Combo inReciver;
	private Combo inName;
	private Composite block;
	private boolean update;
	private String errorString;
//	private Variable[] reciverList;
	
	public ObjectField(ScriptDialogContext context, Listener callBack)
	{
		this.context = context;
		this.callBack = callBack;
		this.treeLibrary = context.getTreeLibrary();
	}
	
	public void setNode(Node node)
	{
		this.node = node;
		resolve(node);
	}

	public boolean isEditable()
	{
		return editable;
	}

	public String getErrorString()
	{
		return errorString;
	}

	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
	
	public String getReciverName()
	{
		return reciverName;
	}
	
	public String getNodeName()
	{
		return targetName;
	}

	public void createControls(Composite parent, int columns)
	{
		block = new Composite(parent, SWT.NONE);
		block.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, columns, 1));
		block.setLayout(new GridLayout(4, false));
		
		Label txtReciver = new Label(block, SWT.NONE);
		txtReciver.setText("Objekt:");
		
		inReciver = new Combo(block, SWT.BORDER | SWT.DROP_DOWN);
		inReciver.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		inReciver.addListener(SWT.Modify, this);
		
		Label txtName = new Label(block, SWT.NONE);
		txtName.setText("Name:");
		
		inName = new Combo(block, SWT.BORDER | SWT.DROP_DOWN);
		inName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		inName.addListener(SWT.Modify, this);
	}
	
	public Control getControl()
	{
		return block;
	}
	
	//XXX: Variablen machen noch zu großen Aufwand dessen Typ zu ermitteln.
	private void fillReciverList()
	{
		if (node instanceof DefNode)
		{
			inReciver.setItems(new String[] {"", "self"});
			return;
		}
		
		HashMap<String, ClassDefinition> defs = context.getTreeLibrary().getClassDefinitions();
		LibraryTree tree = context.getTreeLibrary().findVariables(context.getPosition(), true);

		HashSet<String> set = new HashSet<String>();
		set.add("self");
		
		for (String id : tree.keySet())
		{
			set.add((id == null) ? "" : id);
		}

		for (ClassDefinition def : defs.values())
		{
			set.add((def.id == null) ? "" : def.id);
		}
		String[] items = new String[set.size()];
		
		Iterator<String> itr = set.iterator();
		for (int i = 0; itr.hasNext(); i++)
		{
			items[i] = itr.next();
		}
		
		inReciver.setItems(items);
	}

	private void fillNameList()
	{
		String[] items;
		ArrayList<String> list = new ArrayList<String>();
		LibraryTree vars = treeLibrary.findVariables(context.getPosition(), false);
		
		System.out.print("Variablen: ");
		if (vars.containsKey(TreeLibrary.DEFAULT_GROUP))
		for (String id : vars.get(TreeLibrary.DEFAULT_GROUP).entries.keySet())
		{
			System.out.print("\t" + id);
			list.add(id);
		}
		System.out.println();
		
		ClassDefinition def = context.getTreeLibrary().getClassDefinition(reciverName);
		if (def != null)
		{
			if (node instanceof AsgNode)
			{
				for (ValueDefinition field : def.fields.values())
				{
					list.add(field.id);
				}
			}
			if (node instanceof CallNode)
			{
				for (FunctionDefinition func : def.methods.values())
				{
					list.add(func.id);
				}
			}
			items = list.toArray(new String[list.size()]);
		}
		else items = new String[0];
		
		targetName = inName.getText();
		inName.setItems(items);
		setComboValue(inName, targetName);
	}
	
	private void resolve(Node node)
	{
		update = true;
		
		fillReciverList();
		
		if (node instanceof AsgNode)
			node = ((AsgNode) node).getAcceptor();
		
		Node nextNode = ScriptUtil.getReciver(node);
		reciverName = (nextNode instanceof INamed) ? ((INamed) nextNode).getName() : "";
		setComboValue(inReciver, reciverName);
		
		fillNameList();
		
		targetName = (node instanceof INamed) ? ((INamed) node).getName() : "";
		setComboValue(inName, targetName);
		
		update = false;
	}
	
	private void setComboValue(Combo combo, String name)
	{
		boolean found = false;
		for (int i = 0; i < combo.getItemCount(); i++)
		{
			if (combo.getItem(i).equals(name))
			{
				combo.select(i);
				found = true;
				break;
			}
		}
		if (!found) combo.setText(name);
		validate();
	}
	
	private void validate()
	{
		errorString = null;
		if ((reciverName.isEmpty() || reciverName.equals("self")) &&
			(node instanceof AsgNode || node instanceof DefNode)) return;

		if (indexOf(inReciver.getItems(), reciverName) == -1 || indexOf(inName.getItems(), targetName) == -1)
			errorString = "Der Aufruf '" + reciverName + "." + targetName + "' ist nicht bekannt!";
	}
	
	private void setReciverName(String name)
	{
		update = true;
		
		IHasNext hasNext = null;
		if (node instanceof AsgNode)
			hasNext = ((AsgNode) node).getAcceptor();
		else  if (node instanceof IHasNext)
			hasNext = (IHasNext) node;
		
		if (hasNext != null)
		{
			reciverName = name;
			validate();
			if (reciverName.isEmpty())
				hasNext.setNextNode(null);
			else
				hasNext.setNextNode(new ConstNode(reciverName));
		}
		fillNameList();
		
		update = false;
	}
	
	private void setNodeName(String name)
	{
		targetName = name;
		if (node instanceof INamed)
		{
			((INamed) node).setName(name);
		}
		else if (node instanceof AsgNode)
		{
			((AsgNode) node).getAcceptor().setName(name);
		}
		validate();
	}

	@Override
	public void handleEvent(Event event)
	{
		if (update) return;
		
		if (event.widget == inReciver)
		{
			setReciverName(inReciver.getText());
			fireSelectionChanged(event);
		}
		else if (event.widget == inName)
		{
			setNodeName(inName.getText());
			fireSelectionChanged(event);
		}
	}
	
	private void fireSelectionChanged(Event event)
	{
		if (node == null) return;
		
		event.widget = getControl();
		event.data = getNodeName();
		callBack.handleEvent(event);
	}
	
//	private String[] addToArray(String[] array, String str)
//	{
//		String[] newArray = Arrays.copyOf(array, array.length + 1);
//		array[array.length - 1] = str;
//		return newArray;
//	}
	
	private int indexOf(String[] array, String str)
	{
		for(int i = 0; i < array.length; i++)
		{
			if (array[i].equals(str)) return i;
		}
		return -1;
	}
}
