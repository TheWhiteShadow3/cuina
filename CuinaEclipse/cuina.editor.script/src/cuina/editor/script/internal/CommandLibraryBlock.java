package cuina.editor.script.internal;

import cuina.editor.script.internal.CommandLibraryContentProvider.CommandLibraryElement;
import cuina.editor.script.internal.dialog.CommandDialog;
import cuina.editor.script.internal.dialog.EmptyNode;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.CaseNode;
import cuina.editor.script.ruby.ast.CommentNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.IfNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.StrNode;
import cuina.editor.script.ruby.ast.VarNode;
import cuina.editor.script.ruby.ast.WhileNode;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

public class CommandLibraryBlock implements IDoubleClickListener
{
	private ScriptEditor editor;
	private TreeViewer viewer;

	public CommandLibraryBlock(ScriptEditor editor)
	{
		this.editor = editor;
	}

	public void createControl(Composite parent)
	{
		Group group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		group.setText("Command-Library");

		createLibraryList(group);
	}

	private void createLibraryList(Composite parent)
	{
//		HashMap<String, Section> sections = editor.getScriptLibrary().getSections();

		// Default-Buttons
//		Group defaultGroup = new Group(parent, SWT.NONE);
//		defaultGroup.setLayout(new GridLayout(1, false));
//		defaultGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
//		defaultGroup.setText("Control");
		
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.setContentProvider(new CommandLibraryContentProvider());
		viewer.setLabelProvider(new CommandLibrayLabelProvider());
		viewer.setInput(editor.getStaticScriptLibrary());
		viewer.addDoubleClickListener(this);//SelectionChangedListener(this);
//		createDefauldCommand(defaultGroup, "Zuweisung", AsgNode.class);
//		createDefauldCommand(defaultGroup, "Bedingung", IfNode.class);
//		createDefauldCommand(defaultGroup, "Schleife", WhileNode.class);
//		createDefauldCommand(defaultGroup, "Auswahl", CaseNode.class);

		// Library-Function-Buttons
//		for (String key : sections.keySet())
//		{
//			Section section = sections.get(key);
//			Group group = new Group(parent, SWT.NONE);
//			group.setLayout(new GridLayout(1, false));
//			group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
//			String label = section.getLabel();
//			group.setText(label != null ? label : "");
//
//			for (FunctionDefinition func : section.values())
//			{
//				Button cmd = new Button(group, SWT.PUSH);
//				// cmd.setSize(128, 20);
//				cmd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//				cmd.setText(func.getLabel());
//				cmd.setData(func);
//				cmd.addSelectionListener(this);
//			}
//		}
	}

//	private void createDefauldCommand(Composite group, String name, Class<? extends Node> type)
//	{
//		Button cmd = new Button(group, SWT.PUSH);
//		cmd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
//		cmd.setText(name);
//		cmd.setData(type);
//		cmd.addSelectionListener(this);
//	}

	private Node createParameterNode(String type, String def)
	{
		switch (type)
		{
			case "bool":
				return new ConstNode(def);
			case "string":
				return new StrNode(def);
			case "int":
				return new FixNumNode(def != null ? Long.valueOf(def) : 0);
			case "float":
				return new FixNumNode(def != null ? Double.valueOf(def) : 0);
			default:
				return new EmptyNode();
		}
	}

	@Override
	public void doubleClick(DoubleClickEvent ev)
	{
		Object obj = ((IStructuredSelection) ev.getSelection()).getFirstElement();
		if (!(obj instanceof CommandLibraryElement)) return;
		
		CommandLibraryElement cle = (CommandLibraryElement) obj;
		
		Node node = getLibraryNode(cle);
		if (cle.nodeClass == CommentNode.class)
		{
			InputDialog dialog = new InputDialog(editor.getShell(), "Kommentar", "Kommentar:", "", null);
			if (dialog.open() == Dialog.OK)
			{
				editor.insertNode( new CommentNode(dialog.getValue()) );
			}
		}
		else if (node != null)
		{
			if (!(cle.function != null && cle.function.params.size() == 0))
			{
				CommandDialog lineDialog = new CommandDialog(editor, node, cle.name);
				if (lineDialog.open() != Dialog.OK) return;
				{
					node = lineDialog.getNode();
				}
			}
			editor.insertNode(node);
		}
	}
	
	private Node getLibraryNode(CommandLibraryElement cle)
	{
		Node node = null;
		if (cle.function != null)
		{
			CallNode callNode = new CallNode(cle.function.id);
			if (cle.parent.id != null && !cle.parent.id.isEmpty())
				callNode.setNextNode(new ConstNode(cle.parent.id));

			for (ValueDefinition p : cle.function.params)
			{
				callNode.addArgument(createParameterNode(p.type, p.def));
			}

			node = callNode;
		}
		else if (cle.nodeClass != null)
		{
			if (cle.nodeClass == IfNode.class)
			{
				node = new IfNode(null);
			}
			else if (cle.nodeClass == WhileNode.class)
			{
				node = new WhileNode(null);
			}
			else if (cle.nodeClass == CaseNode.class)
			{
				node = new CaseNode(null);
			}
			else if (cle.nodeClass == AsgNode.class)
			{
				AsgNode asgNode = new AsgNode(null, new VarNode("var", Node.LOCAL_SCOPE));
				asgNode.setArgument(new EmptyNode());
				node = asgNode;					
			}
		}
		return node;
	}
}
