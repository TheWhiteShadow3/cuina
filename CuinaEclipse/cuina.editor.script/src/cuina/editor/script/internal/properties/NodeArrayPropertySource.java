package cuina.editor.script.internal.properties;

import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.ruby.ast.ListNode;
import cuina.editor.script.ruby.ast.Node;

import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class NodeArrayPropertySource implements IPropertySource
{
	private TreeEditor editor;
	private List<Node> list;
	
	public NodeArrayPropertySource(TreeEditor editor, ListNode listNode)
	{
		this(editor, listNode.getChildren());
	}
	
	public NodeArrayPropertySource(TreeEditor editor, List<Node> list)
	{
		this.editor = editor;
		this.list = list;
	}

	@Override
	public Object getEditableValue()
	{
		return list;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		IPropertyDescriptor[] descriptors = new PropertyDescriptor[list.size()];
		
		for(int i = 0; i < list.size(); i++)
		{
			descriptors[i] = new PropertyDescriptor(new Integer(i), list.get(i).getNodeName());
		}
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id)
	{
		return new NodePropertySource(editor, list.get((Integer) id));
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
		if (editor == null) return;
		
		list.set((Integer) id, (Node) value);
	}
}
