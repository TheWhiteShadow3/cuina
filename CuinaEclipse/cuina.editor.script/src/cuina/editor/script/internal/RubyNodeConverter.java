package cuina.editor.script.internal;

import cuina.database.ui.TreeItem;
import cuina.database.ui.tree.TreeRoot;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.prefs.ScriptPreferences;
import cuina.editor.script.internal.properties.NodePropertySource;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.StaticScriptLibrary.ScriptType;
import cuina.editor.script.ruby.NodeLabelProvider;
import cuina.editor.script.ruby.ast.*;

import java.lang.reflect.Method;
import java.util.ArrayList;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class RubyNodeConverter
{	
	private TreeEditor treeEditor;
	private ArrayList<CommandPage> pageList;
	private ScriptType scriptType;

	// Variablen für den Build.
	private int indent;
	private String colorKey;
	private CommandPage page;
	
	
	public RubyNodeConverter(TreeEditor treeEditor)
	{
		this.treeEditor = treeEditor;
//		treeEditor.addTreeEditorListener(this);
	}
	
	/**
	 * Erstellt die Command-Seiten des Skripts.
	 * Wenn das Skript ein Klassen-Typ ist, wird eine Seite für jedem methode erstellt,
	 * ansonsten wird genau eine Seite für den Wurzel-Knoten zurückgegeben.
	 */
	public void createPages()
	{
		pageList = new ArrayList<CommandPage>(8);
		
		RootNode root = treeEditor.getRoot();
		if (root.getChilds() != null && root.getChilds().size() > 0)
		{
			Node first = root.getChilds().get(0);
			if (first instanceof ClassNode)
			{
				ClassNode classNode = (ClassNode) first;
//				scriptBaseType = library.findScriptType(ScriptUtil.getInterface((ClassNode) first));
				Method[] jMethods;
				if (scriptType != null)
					jMethods = scriptType.clazz.getMethods();
				else
					jMethods = new Method[0];
				
				for (Node child : classNode)
				{
					if (child instanceof DefNode)
					{
						boolean important = false;
						for (Method m : jMethods)
						{
							if (ScriptUtil.compareMethods(m, (DefNode) child))
							{
								important = true;
								break;
							}
						}
						pageList.add(new CommandPage(treeEditor, classNode, child,
								((DefNode) child).getName(), important));
					}
				}
			}
			return;
		}
		pageList.add(new CommandPage(treeEditor, true));
	}
	
	public void createSinglePage(TreeEditor treeEditor)
	{
		pageList = new ArrayList<CommandPage>(1);
		
		pageList.add(new CommandPage(treeEditor, true));
	}
	
	public CommandPage addPage(TreeEditor treeEditor, ListNode parent, DefNode node)
	{
		CommandPage page = new CommandPage(treeEditor, parent, node, node.getName(), false);
		pageList.add(page);
		return page;
	}
	
	public ScriptType getScriptType()
	{
		return scriptType;
	}
	
	public void setScriptType(ScriptType scriptType)
	{
		this.scriptType = scriptType;
	}
	
	/**
	 * Gibt die Seitenliste zurück.
	 * Wenn {@link #createPages(Node)} noch nicht aufgerufen wurde wird <code>null</code> zurückgegeben.
	 * @return Die Seitenliste oder <code>null</code>, wenn noch keine Seiten erstellt wurden.
	 */
	public ArrayList<CommandPage> getPageList()
	{
		return pageList;
	}
	
	public int getPageCount()
	{
		return pageList.size();
	}
	
	public void createLines(CommandPage page)
	{
		this.page = page;
		page.lines = new ArrayList<CommandLine>(16);
		
		indent = 0;
		for(Node node : page.node.getChilds())
		{
			addCommandLine(node);
		}
		addMarkLine(page.node);
	}
	
	private void addCommandLine(Node node)
	{
		addCommand(node, true);
	}
	
	private void addMarkLine(Node parent)
	{
		page.lines.add( new CommandLine(page, null, (ListNode) parent,
				ScriptPreferences.CMDLINE_COLOR_DEFAULT, indent, CommandLine.TYPE_MARK));
	}

	private void addCloseLine(Node master)
	{
		page.lines.add( new CommandLine(page, null, (ListNode) master,
				ScriptPreferences.CMDLINE_COLOR_CONTROL, indent, CommandLine.TYPE_SLAVE));
	}

	private void addNodeLine(Node node)
	{
		page.lines.add(new CommandLine(page, node, (ListNode) node.getParent(),
				colorKey, indent, CommandLine.TYPE_NODE));
	}

	private void addCommand(Node node, boolean colorChange)
	{
		if (node instanceof CommentNode)
		{
			if (colorChange) colorKey = ScriptPreferences.CMDLINE_COLOR_COMMENT;
		}
		else if (node instanceof CallNode)
		{
			if (colorChange) colorKey = ScriptPreferences.CMDLINE_COLOR_FUNCTION;
			addFunction((CallNode) node);
		}
		else if (node instanceof IfNode)
		{
			addIf( (IfNode) node);
			addCloseLine(node);
		}
		else if (node instanceof WhileNode)
		{
			addWhile( (WhileNode) node);
		}
		else if (node instanceof CaseNode)
		{
			addCase( (CaseNode) node);
		}
		else if (node instanceof AsgNode)
		{
			if (colorChange) colorKey = ScriptPreferences.CMDLINE_COLOR_ASSIGNMENT;
			addNodeLine( node);
		}
		else if (node instanceof ConstNode)
		{
			addTail((ConstNode) node);
		}
		else
			addNodeLine(node);
	}

	private void addFunction(CallNode callNode)
	{
		addNodeLine(callNode);
		BlockNode body = callNode.getBody();
		if (body != null)
		{
			indent++;
			for(Node node : body.getChilds())
			{
				addCommandLine(node);
			}
			addMarkLine(body);
			indent--;
			addCloseLine(callNode);
		}
	}

	private void addCase(CaseNode caseNode)
	{
		colorKey = ScriptPreferences.CMDLINE_COLOR_CONTROL;
		addNodeLine(caseNode);
		indent++;
		for(Node node : caseNode.getChilds())
		{
			if (node instanceof WhenNode)
			{
				colorKey = ScriptPreferences.CMDLINE_COLOR_CONTROL;
				addNodeLine(node);
				indent++;
				for (Node bodyNode : node.getChilds())
				{
					addCommandLine(bodyNode);
				}
				addMarkLine(node);
				indent--;
			}
			else if (node instanceof ElseNode)
				addElse((ElseNode) node);
			else
				System.err.println("unerwarteter Knoten in Case-Block! " + node);
		}
//		addMarkLine(caseNode);
		indent--;
		addCloseLine(caseNode);
	}

	private void addIf(IfNode ifNode)
	{
		colorKey = ScriptPreferences.CMDLINE_COLOR_CONTROL;
		addNodeLine(ifNode);
		indent++;
		for(Node node : ifNode.getChilds())
		{
			addCommandLine(node);
		}
		addMarkLine(ifNode);
		indent--;
		Node node = ifNode.getElseNode();
		if (node != null)
		{
			if (node instanceof IfNode)
				addIf( (IfNode) ifNode.getElseNode());
			else
				addElse( (ElseNode) node);
		}
	}

	private void addElse(ElseNode elseNode)
	{
		colorKey = ScriptPreferences.CMDLINE_COLOR_CONTROL;
		addNodeLine(elseNode);
		indent++;
		for(Node node : elseNode.getChilds())
		{
			addCommandLine(node);
		}
		addMarkLine(elseNode);
		indent--;
	}

	private void addWhile(WhileNode whileNode)
	{
		colorKey = ScriptPreferences.CMDLINE_COLOR_CONTROL;
		addNodeLine(whileNode);
		indent++;
		for(Node node : whileNode.getBlock())
		{
			addCommandLine(node);
		}
		addMarkLine(whileNode);
		indent--;
		addCloseLine(whileNode);
	}
	
	private void addTail(IHasNext node)
	{
		Node next = node.getNextNode();
		if (next != null)
		{
			addCommand(node.getNextNode(), false);
		}
	}
	
	public class CommandPage implements TreeItem, IAdaptable
	{
		public final TreeEditor treeEditor;
		public boolean important;
		public ScriptPosition position;
		public Node node;
		public String name;
		public ArrayList<CommandLine> lines;

		public CommandPage(TreeEditor treeEditor, ListNode parent, Node node, String name, boolean important)
		{
			this.treeEditor = treeEditor;
			this.node = node;
			this.name = name;
			this.important = important;
			this.position = new ScriptPosition(parent, parent.getChilds().indexOf(node));
			if (position.getIndex() == -1 && node != null)
				throw new IllegalStateException();
		}
		
		public CommandPage(TreeEditor treeEditor, boolean important)
		{
			this.treeEditor = treeEditor;
			this.node = treeEditor.getRoot();
			this.name = "root";
			this.important = important;
			this.position = new ScriptPosition(node);
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public Object[] getChildren(TreeRoot root)
		{
			return lines.toArray();
		}

		@Override
		public boolean hasChildren()
		{
			return lines != null && lines.size() > 0;
		}

		@Override
		public int hashCode()
		{
			return -position.hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof CommandPage)
				return position.equals( ((CommandPage) obj).position);
			return false;
		}

		@Override
		public Object getAdapter(Class adapter)
		{
			if (adapter == ImageDescriptor.class)
			{
				int imageID = important ? Scripts.IMG_PAGE_REQUIRED : Scripts.IMG_PAGE_OPTIONAL;
				return ImageDescriptor.createFromImage(Scripts.getImage(imageID));
			}
			return null;
		}
	}
	
	public static class CommandLine implements TreeItem, IAdaptable
	{
		public static final int TYPE_NODE = 1;
		public static final int TYPE_SLAVE = 2;
		public static final int TYPE_MARK = 3;
		public static final int TYPE_BLOCk = 4;
		
		public final CommandPage page;
		public ScriptPosition position;
		public Node node;
		public String colorKey;
		public int indent;
		public int type;
		
		public CommandLine(CommandPage page, Node node, ListNode parent, String colorKey, int indent, int type)
		{
			this.page = page;
			this.node = node;
			this.colorKey = colorKey;
			this.indent = indent;
			this.type = type;
			this.position = new ScriptPosition(parent, parent.getChilds().indexOf(node));
			if (position.getIndex() == -1 && node != null)
				throw new IllegalStateException();
		}
		
		@Override
		public String toString()
		{
			return super.hashCode() + ": " + node;
		}

		@Override
		public Object getAdapter(Class adapter)
		{
			if (adapter == IPropertySource.class)
			{
				return new CommandLinePropertySource(this);
			}
			return null;
		}

		@Override
		public String getName()
		{
			return node != null ? node.toString() : "";
		}

		@Override
		public boolean hasChildren()
		{
			return false;
		}

		@Override
		public Object[] getChildren(TreeRoot root)
		{
			return new Object[0];
		}
	}
	
	public static class CommandLinePropertySource implements IPropertySource
	{
		private CommandLine line;

		public CommandLinePropertySource(CommandLine line)
		{
			this.line = line;
		}
		
		@Override
		public Object getEditableValue()
		{
			return line;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors()
		{
			PropertyDescriptor desc = new PropertyDescriptor("line.node", "Node");
			desc.setLabelProvider(new NodeLabelProvider());
			return new IPropertyDescriptor[] {desc};
		}

		@Override
		public Object getPropertyValue(Object id)
		{
			switch((String) id)
			{
//				case "line.text": return line.text;
				case "line.node":
					if (line.node != null)
						return new NodePropertySource(line.page.treeEditor, line.node);
					else
						return null;
			}
			return null;
		}

		@Override
		public boolean isPropertySet(Object id)
		{
			return false;
		}

		@Override
		public void resetPropertyValue(Object id)
		{
		}

		@Override
		public void setPropertyValue(Object id, Object value)
		{
			switch((String) id)
			{
				case "line.node":
					line.node = (Node) value;
					break;
			}
		}
	}
}
