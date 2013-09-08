package cuina.editor.script.internal.properties;

import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.ruby.NodeLabelProvider;
import cuina.editor.script.ruby.ast.ArrayNode;
import cuina.editor.script.ruby.ast.FixNumNode;
import cuina.editor.script.ruby.ast.INamed;
import cuina.editor.script.ruby.ast.IParameter;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.StrNode;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class NodePropertySource implements IPropertySource
{
	private static final String NODE_TYPE 	= "node.type";
	private static final String NODE_NAME 	= "node.name";
	private static final String NODE_ARGS 	= "node.args";
	private static final String NODE_CHILDS = "node.childs";
	private static final String NODE_SVALUE = "node.value.string";
	private static final String NODE_NVALUE = "node.value.number";
	
	private TreeEditor editor;
	private Node node;
	private IPropertyDescriptor[] descriptors;
	private NodeLabelProvider labelProvider = new NodeLabelProvider();
	
	public NodePropertySource(TreeEditor editor, Node node)
	{
		this.editor = editor;
		this.node = node;
	}
	
	@Override
	public Object getEditableValue()
	{
		return node;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		if (descriptors != null) return descriptors;
		
		ArrayList<IPropertyDescriptor> list = new ArrayList<IPropertyDescriptor>();
//		list.add(new PropertyDescriptor(NODE_TYPE, "Type"));
		if (node instanceof INamed)
		{
			list.add(new TextPropertyDescriptor(NODE_NAME, "Name"));
		}
		if (node instanceof IParameter && ((IParameter) node).getArgument() != null)
		{
			PropertyDescriptor desc = new PropertyDescriptor(NODE_ARGS, "Arguments");
			desc.setLabelProvider(labelProvider);
			list.add(desc);
		}
		if (node instanceof ListNode && ((ListNode) node).size() > 0)
		{
			PropertyDescriptor desc = new PropertyDescriptor(NODE_CHILDS, "Kinder");
			desc.setLabelProvider(labelProvider);
			list.add(desc);
		}
		if (node instanceof StrNode)
			list.add(new TextPropertyDescriptor(NODE_SVALUE, "Value"));
		if (node instanceof FixNumNode)
			list.add(new TextPropertyDescriptor(NODE_NVALUE, "Value"));
		
		descriptors = list.toArray(new IPropertyDescriptor[list.size()]);
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		switch ((String) id)
		{
			case NODE_TYPE:
				return node.getNodeType();
			case NODE_NAME:
				return ((INamed)node).getName();
			case NODE_ARGS:
			{
				Node arg =((IParameter)node).getArgument();
				if (arg instanceof ArrayNode)
				{
					if (arg.getChildren().size() > 1)
						return new NodeArrayPropertySource(editor, (ArrayNode) arg);
					else
						return new NodePropertySource(editor, ((ArrayNode) arg).getChild(0));
				}
				else
					return new NodePropertySource(editor, arg);
			}
			case NODE_CHILDS:
				return new NodeArrayPropertySource(editor, ((ListNode)node));
			case NODE_SVALUE:
				return ((StrNode)node).getValue();
			case NODE_NVALUE:
				return ((FixNumNode)node).getValue().toString();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPropertyValue(Object id, Object value)
	{
		if (editor == null) return;
		
		switch ((String) id)
		{
			case NODE_NAME: editor.setNodeName(node, (String) value); break;
			case NODE_SVALUE:
			case NODE_NVALUE: editor.setNodeValue(node, value); break;
		}
	}
}
