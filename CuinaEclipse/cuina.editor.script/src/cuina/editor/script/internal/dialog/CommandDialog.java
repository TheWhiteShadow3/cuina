package cuina.editor.script.internal.dialog;

import cuina.editor.core.CuinaProject;
import cuina.editor.script.dialog.CommandTab;
import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.internal.ScriptSelection;
import cuina.editor.script.internal.ScriptUtil;
import cuina.editor.script.internal.ruby.RubyIdentifier;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.NodeLabelProvider;
import cuina.editor.script.ruby.TreeEditorEvent;
import cuina.editor.script.ruby.TreeEditorListener;
import cuina.editor.script.ruby.ast.ArgNode;
import cuina.editor.script.ruby.ast.ArrayNode;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.CommentNode;
import cuina.editor.script.ruby.ast.ConstNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.IParameter;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.StrNode;
import cuina.editor.script.ruby.ast.VarNode;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public final class CommandDialog extends TitleAreaDialog implements ScriptDialogContext
{
	private String title;
	private Node node;
	private ScriptDialogContext context;
//	private ClassDefinition clazz;
	private FunctionDefinition function;
	private NodeLabelProvider labelProvider;
	private CommandLineDialogHandler handler;
//	private HashMap<String, VariablesFinder.Variable> vars;
	
	private final ArrayList<CommandTab> tabs = new ArrayList<CommandTab>(4);
	
	private CTabFolder inputTypeTabFolder;
	private Text scriptLine;
//	private VariablesFinder.Variable[] reciverList;
//	private Node[] nameList;
	private ArgumentList argList;
	private ObjectField objField;
	
	private boolean update;
	
//	public static CommandDialog createPageDialog(ScriptDialogContext context, DefNode pageNode, String name)
//	{
//		CommandDialog dialog = new CommandDialog(context, "Skript Seite");
//		
//		dialog.function = ScriptUtil.findLibraryFunction(context.getTreeLibrary(), pageNode);
//		dialog.tabs.add(new FixValueScriptTab());
//		
//		return dialog;
//	}
//	
//	public static CommandDialog createFunctionDialog(ScriptDialogContext context, CallNode callNode, String name)
//	{
//		CommandDialog dialog = new CommandDialog(context, callNode.getName());
//		
//		dialog.function = ScriptUtil.findLibraryFunction(context.getTreeLibrary(), callNode);
//		dialog.tabs.add(new FixValueScriptTab());
//		
//		return dialog;
//	}
	
	public CommandDialog(ScriptDialogContext context, Node node, String title)
	{
		super(context.getShell());
		if (node == null) throw new NullPointerException("node must not be null.");
		
		setHelpAvailable(false);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		setReturnCode(OK | CANCEL);
		this.context = context;
		this.title = title;
		
		init(node);
	}

	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Script-Dialog");
		shell.setSize(640, 480);
	}

	private void init(Node node)
	{
		this.node = node;
		this.labelProvider = new NodeLabelProvider();
		
//		if (node instanceof IHasNext)
//		{
//			Node rec = ((IHasNext) node).getNextNode();
//			if (rec instanceof ConstNode)
//				clazz = ScriptUtil.findClassDefinition(context.getScriptLibrary(), (ConstNode) rec);
//		}
		
		if (node instanceof CallNode)
		{
			function = ScriptUtil.findLibraryFunction(context.getTreeLibrary(), (CallNode) node);
			//TODO: ValueTab von der Funktion herleiten. Noch nicht möglich
		}
		
		tabs.add(new FixValueScriptTab());
		if (!(node instanceof DefNode))
		{
			tabs.add(new VariablesScriptTab());
			tabs.add(new FunctionScriptTab());
			tabs.add(new ExpressionScriptTab());
		}

		
//		this.vars = VariablesFinder.findVariables(parent, -1);
//		System.out.println(vars);
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		this.handler = new CommandLineDialogHandler();
		this.context.getTreeEditor().addTreeEditorListener(handler);
		
		argList = new ArgumentList(context, handler);
		objField = new ObjectField(context, handler);
		
		parent = (Composite) super.createDialogArea(parent);
//		parent = new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
//		parent.setLayout(new GridLayout(1, false));
//		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		SashForm splitter = new SashForm(parent, SWT.HORIZONTAL | SWT.SMOOTH);
		splitter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createScriptLine(parent, 1);
		argList.createControls(splitter);
		
		Composite mainArea = new Composite(splitter, SWT.NONE);
		mainArea.setLayout(new GridLayout());
		mainArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		objField.createControls(mainArea, 1);
		createTabFolder(mainArea, 1);

		splitter.setWeights(new int[] {15, 85});
		initValues();
		
		if (title != null) setTitle(title);
		return parent;
	}
	
	private Composite addTab(CTabFolder tabber, String name)
	{
		CTabItem item = new CTabItem(tabber, SWT.NONE);
		item.setText(name);
		Composite content = new Composite(tabber, SWT.NONE);
//		content.setLayout(new FillLayout());
		item.setControl(content);
		return content;
	}
	
	private void createTabFolder(Composite parent, int coloumns)
	{
		inputTypeTabFolder = new CTabFolder(parent, SWT.BORDER);
		inputTypeTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, coloumns, 1));
		for (CommandTab tab : tabs)
		{
			tab.init(this);
			tab.createControl(addTab(inputTypeTabFolder, tab.getName()));
		}
		inputTypeTabFolder.setSelection(0);
		inputTypeTabFolder.addListener(SWT.Selection, handler);
	}
	
	private void createScriptLine(Composite parent, int coloumns)
	{
		scriptLine = new Text(parent, SWT.READ_ONLY | SWT.BORDER);
		scriptLine.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, coloumns, 1));
	}
	
	private void initValues()
	{
		update = true;
		if (function != null)
		{
			setMessage(function.help);
		}
		objField.setNode(node);
		argList.setNode(node, function);
		updateControls();
		setParameter();
		updateCommand();
		update = false;
	}
	
	private void updateControls()
	{
		if (node instanceof DefNode || node instanceof AsgNode || function == null)
		{
			objField.setEditable(true);
		}
		else
		{
			objField.setEditable(false);
		}
		
		if (argList.getSelectedIndex() == -1)
		{
			for (CommandTab tab : tabs)
				tab.setEnabled(false);
		}
	}
	
	private void setParameter()
	{
		int index = argList.getSelectedIndex();
		if (index == -1) return;
		
		Node argNode = argList.getSelectedNode();
		ValueDefinition param = null;
		if (function != null) param = function.params.get(index);
		
		for (CommandTab tab : tabs)
		{
			tab.setNode(argNode, param);
		}

		if (argNode instanceof CommentNode || argNode instanceof StrNode || argNode instanceof FixNumNode
		 || argNode instanceof ConstNode || argNode instanceof EmptyNode || argNode == null)
			inputTypeTabFolder.setSelection(0);
		else if (argNode instanceof VarNode)
			inputTypeTabFolder.setSelection(1);
		else if (argNode instanceof CallNode)
			inputTypeTabFolder.setSelection(2);
		else//if (argNode instanceof ExpNode)
			inputTypeTabFolder.setSelection(3);
	}
	
	private void updateCommand()
	{
		scriptLine.setText(labelProvider.getText(node));
		validate();
	}
	
	private void validate()
	{
		setErrorMessage(null);
		if (objField.isEditable())
		{
			if (ScriptUtil.getReciver(node) != null)
			{
				if (!RubyIdentifier.isValidName(objField.getReciverName()))
				{
					setErrorMessage("ungültiger Objektname: " + objField.getReciverName());
					return;
				}
			}
			else
			{
				if (!objField.getReciverName().isEmpty())
				{
					setErrorMessage("ungültiger Objektname: " + objField.getReciverName());
					return;
				}
			}
			if (!RubyIdentifier.isValidIdentifier(objField.getNodeName()))
			{
				setErrorMessage("ungültiger Bezeichner: " + objField.getNodeName());
				return;
			}
			setErrorMessage(objField.getErrorString());
		}
		if (getErrorMessage() == null)
			setErrorMessage(validate(node));
	}
	
	private String validate(Node node)
	{
		if (node == null) return "Argument ist null";
		if (node instanceof EmptyNode) return "ungültiges Argument";
		
		if (node instanceof ListNode)
		{
			for (Node n : node.getChilds())
			{
				String str = validate(n);
				if (str != null) return str;
			}
		}
		
		if (node instanceof IParameter)
		{
			String str = validate( ((IParameter) node).getArgument() );
			if (str != null) return str;
		}
		
		return null;
	}
	
	private void setArgument(int index, Node argNode)
	{
		if (index == -1) return;
		
		this.node = argList.getSelectedNode();
		if (this.node instanceof DefNode)
		{	// bei Definitionen wird der Defaultwert des Parameters gesetzt.
			ArrayNode arrayNode = ((DefNode) node).getArgument();
			ArgNode arg = ((ArgNode) arrayNode.getChild(index));
			arg.setDefault(argNode instanceof EmptyNode ? null : argNode);
		}
		else if (this.node instanceof IParameter)
		{
			IParameter pNode = (IParameter) this.node;
			if (pNode.getArgument() instanceof ArrayNode)
				((ArrayNode) pNode.getArgument()).set(index, argNode);
			else
				pNode.setArgument(argNode);
		}
		else if (this.node instanceof CommentNode)
		{
			this.node = argNode; //XXX: Workaround2 um den Dialog für Kommentare benutzbar zu machen.
		}
	}

	@Override
	public void valueChanged(Object source, Node argNode)
	{
		argList.setArgument(argList.getSelectedIndex(), argNode);
//		setArgument(argList.getSelectedIndex(), argNode);
		updateCommand();
	}

	@Override
	public TreeEditor getTreeEditor()
	{
		return context.getTreeEditor();
	}

	@Override
	public TreeLibrary getTreeLibrary()
	{
		return context.getTreeLibrary();
	}
	
	@Override
	public ScriptSelection getSelection()
	{
		return context.getSelection();
	}
	
	@Override
	public CuinaProject getProject()
	{
		return context.getProject();
	}
	
	@Override
	public Node getNode()
	{
		return node;
	}
	
	@Override
	public ScriptPosition getPosition()
	{
		return context.getPosition();
	}
	
	@Override
	public boolean close()
	{
		this.context.getTreeEditor().removeTreeEditorListener(handler);
		return super.close();
	}

	private class CommandLineDialogHandler implements TreeEditorListener, Listener
	{
		@Override
		public void treeNodeAdded(TreeEditorEvent ev)
		{
			updateCommand();
		}

		@Override
		public void treeNodeRemoved(TreeEditorEvent ev)
		{
			updateCommand();
		}

		@Override
		public void treeNodeChanged(TreeEditorEvent ev)
		{
			updateCommand();
		}
		
		@Override
		public void handleEvent(Event event)
		{
			if (update) return;
			
			if (event.widget == objField.getControl())
			{
				updateCommand();
			}
			else if (event.widget == argList.getList())
			{
				if (argList.getSelectedIndex() != -1)
				{
					for (CommandTab tab : tabs)
						tab.setEnabled(true);
				}
				setParameter();
				updateControls();
			}
			else
			{
				int typeIndex = inputTypeTabFolder.getSelectionIndex();
				int argIndex = argList.getSelectedIndex();
				
				Node argNode = tabs.get(typeIndex).getNode();
				setArgument(argIndex, argNode);
				updateCommand();
			}
		}
	}
}
