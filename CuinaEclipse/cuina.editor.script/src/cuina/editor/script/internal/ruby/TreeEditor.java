package cuina.editor.script.internal.ruby;

import cuina.editor.script.internal.ScriptPosition;
import cuina.editor.script.ruby.TreeEditorEvent;
import cuina.editor.script.ruby.TreeEditorListener;
import cuina.editor.script.ruby.ast.BlockNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.IScope;
import cuina.editor.script.ruby.ast.ModuleNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;
import cuina.editor.script.ruby.ast.StrNode;

import java.util.ArrayList;

/**
 * Stellt einen abstrakten Syntax-Baum Model da.
 * Ermöglicht Abfragen und Änderungen an diesem, sowie das registrieren von Listenern.
 * @author TheWhiteShadow
 */
public class TreeEditor
{
	private RootNode root;
//	private Node[] selectionPath;
	private BlockNode selectedBlock;
	private int selectedIndex;
	private final ArrayList<TreeEditorListener> listeners = new ArrayList<TreeEditorListener>(4);

	public TreeEditor(RootNode root)
	{
		this.root = root;
		this.selectedBlock = root;
		this.selectedIndex = -1;
	}

	public RootNode getRoot()
	{
		return root;
	}

	public void setRoot(RootNode root)
	{
		this.root = root;
		fireTreeNodeChanged(new ScriptPosition(root), root);
//		setPosition(root, -1);
	}

	public void addTreeEditorListener(TreeEditorListener l)
	{
		listeners.add(l);
	}

	public void removeTreeEditorListener(TreeEditorListener l)
	{
		listeners.remove(l);
	}

	public BlockNode getSelectedBlock()
	{
//		if (selectionPath == null) return null;
//		
//		for (index = selectionPath.length - 2; index >= 0; index--)
//		{
//			if (selectionPath[index] instanceof BlockNode)
//				return (BlockNode) selectionPath[index];
//		}
//		
//		index = -1;
		return selectedBlock;
	}
	
	public Node getSelectedNode()
	{
		if (selectedBlock == null || selectedIndex < 0 || selectedIndex >= selectedBlock.size()) return null;
		
		return selectedBlock.getChild(selectedIndex);
	}

	//XXX: Selection ist für das Model unrelevant und führt bei absoluten Operationen zu Komplikationen.
//	public void setPosition(BlockNode block, int index)
//	{
//		selectedBlock = block;
//		selectedIndex = index;
//		fireSelectionChanged();
		
//		System.out.println("Set Position: " + block + " on " + index);
//	}

//	@Deprecated
//	public void insertChild(Node node)
//	{
//		if (selectedBlock == null)
//			insertChild(root, -1, node);
//		else
//			insertChild(selectedBlock, selectedIndex, node);
//	}
	
	public void insertChild(ScriptPosition position, Node node)
	{
//		if (parent == null) parent = root;
		position.getParent().add(position.getIndex(), node);
		fireTreeNodeAdded(position, node);
	}
	
	public void removeChild(ScriptPosition position)
	{
		Node n = position.getParent().remove(position.getIndex());
		fireTreeNodeRemove(position, n);
	}
	
	public void changeChild(ScriptPosition position, Node node)
	{
		position.getParent().set(position.getIndex(), node);
		fireTreeNodeChanged(position, node);
	}
	
	public void setNodeName(Node node, String name)
	{
		if (!(node instanceof INamed)) throw new IllegalArgumentException();
		
		((INamed) node).setName(name);
		fireTreeNodeChanged(new ScriptPosition(node), node);
	}
	
	public void setNodeValue(Node node, Object value)
	{
		if (node instanceof StrNode)
			((StrNode) node).setValue(value.toString());
		else if (node instanceof FixNumNode)
			((FixNumNode) node).setValue((Number) value);
		else
			throw new IllegalArgumentException();
		
		fireTreeNodeChanged(new ScriptPosition(node), node);
	}

	public void setParam(CallNode function, int index, Node arg)
	{
		function.getArgument().set(index, arg);
		fireTreeNodeChanged(new ScriptPosition(function), arg);
	}

//	protected void fireSelectionChanged()
//	{
//		TreeEditorEvent ev = new TreeEditorEvent(this, selectedBlock, selectedIndex, getSelectedNode());
//		for (TreeEditorListener l : listeners)
//		{
//			l.selectionChanged(ev);
//		}
//	}

	protected void fireTreeNodeAdded(ScriptPosition position, Node node)
	{
		TreeEditorEvent ev = new TreeEditorEvent(this, position, node);
		for (TreeEditorListener l : listeners)
		{
			l.treeNodeAdded(ev);
		}
	}

	protected void fireTreeNodeRemove(ScriptPosition position, Node node)
	{
		TreeEditorEvent ev = new TreeEditorEvent(this, position, node);
		for (TreeEditorListener l : listeners)
		{
			l.treeNodeRemoved(ev);
		}
	}

	protected void fireTreeNodeChanged(ScriptPosition position, Node node)
	{
		TreeEditorEvent ev = new TreeEditorEvent(this, position, node);
		for (TreeEditorListener l : listeners)
		{
			l.treeNodeChanged(ev);
		}
	}

	public ArrayList<Node> getVariables(BlockNode block)
	{
		ArrayList<Node> list = new ArrayList<Node>(8);
		for (Node node : root.getGlobalVars().values())
		{
			list.add(node);
		}
		findVariables(block, list);

		return list;
	}
	
	public ArrayList<DefNode> getFunctions(BlockNode block)
	{
		ArrayList<DefNode> list = new ArrayList<DefNode>(8);
		for (DefNode node : root.getGlobalFunctions().values())
		{
			list.add(node);
		}
		findFunctions(block, list);

		return list;
	}
	
	private void findVariables(BlockNode block, ArrayList<Node> list)
	{
		Node n = block;
		while(n != null)
		{
			if (n instanceof ClassNode)
			{
				for (Node node : ((ClassNode)n).getInstanceVars().values())
				{
					list.add(node);
				}
			}
			else if (n instanceof ModuleNode)
			{
				for (Node node : ((ModuleNode)n).getStaticVars().values())
				{
					list.add(node);
				}
			}
			else if (n instanceof IScope)
			{
				for (Node node : ((IScope)n).getLocalVars().values())
				{
					list.add(node);
				}
			}
			n = n.getParent();
		}
	}
	
	private void findFunctions(BlockNode block, ArrayList<DefNode> list)
	{
		Node n = block;
		while(n != null)
		{
			if (n instanceof ClassNode)
			{
				for (DefNode node : ((ClassNode)n).getMethods().values())
				{
					list.add(node);
				}
			}
			else if (n instanceof ModuleNode)
			{
				for (DefNode node : ((ModuleNode)n).getStaticFunctions().values())
				{
					list.add(node);
				}
			}
			n = n.getParent();
		}
	}
}
