package cuina.database.ui.tree;

import cuina.database.ui.internal.tree.TreeDataNode;
import cuina.database.ui.internal.tree.TreeGroup;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XML-Konverter für die Baumstruktur einer DataTable um den Overhead zu
 * reduzieren. Der Baum muss mit einem Wurzelelement beginnen
 * <p>
 * Erzeugt folgende Struktur:
 * 
 * <pre>
 * &lt;cuina.database.TreeRoot&gt;
 *   &lt;node name="Gruppe 1"&gt;
 *     &lt;node key="key1"/&gt;
 *   &lt;/node&gt;
 *   &lt;node name="Gruppe 2"&gt;
 *     &lt;node key="key2"/&gt;
 *     &lt;node key="key3"/&gt;
 *   &lt;/node&gt;
 * &lt;/cuina.database.TreeRoot&gt;
 * </pre>
 * 
 * </p>
 * 
 * @author TheWhiteShadow
 */
public class XMLTreeConverter implements Converter
{
	@Override
	public boolean canConvert(Class clazz)
	{
		return (clazz.equals(TreeRoot.class));
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		TreeRoot root = new TreeRoot();
		unmarshalChildren(root, reader, context);

		return root;
	}

	private void unmarshalChildren(TreeGroup parent, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		while (reader.hasMoreChildren())
		{
			reader.moveDown();
			if ("node".equals(reader.getNodeName()))
			{
				String name = reader.getAttributeName(0);
				String value = reader.getAttribute(0);
				TreeNode node = null;
				if ("name".equals(name))
				{
					node = new TreeGroup(value);
					parent.addChild(node, -1);
					unmarshalChildren((TreeGroup) node, reader, context);
				}
				else if ("key".equals(name))
				{
					node = new TreeDataNode(value);
					parent.addChild(node, -1);
				}
				else throw new ConversionException("invalid attribut for node");
			}
			else throw new ConversionException("invalid node '" + reader.getNodeName() + "'");
			reader.moveUp();
		}
	}

	@Override
	public void marshal(Object obj, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		if (obj instanceof TreeGroup)
		{
			TreeGroup group = (TreeGroup) obj;
			if (!group.getName().isEmpty())
				writer.addAttribute("name", group.getName());
			for (TreeNode node : group.getChildren())
			{
				writer.startNode("node");
				marshal(node, writer, context);
				writer.endNode();
			}
		}
		else if (obj instanceof TreeDataNode) writer.addAttribute("key", ((TreeDataNode) obj).getKey());
	}
}
