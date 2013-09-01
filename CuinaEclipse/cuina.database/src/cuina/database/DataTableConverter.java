package cuina.database;


import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * XML-Konverter für eine DataTable um den Overhead zu reduzieren und
 * Implementierungs-Details zu verstecken.
 * <p>
 * Erzeugt folgende Struktur:
 * 
 * <pre>
 * &lt;cuina.database.DataTable name="Tabellenname" type="package.Klassenname"&gt;
 *   &lt;package.Klassenname&gt;
 *     &lt;key&gt;Schlüssel&lt;/key&gt;
 *     &lt;name&gt;Name&lt;/name&gt;
 *     ...
 *   &lt;/package.Klassenname&gt;
 * &lt;/cuina.database.DataTable&gt;
 * </pre>
 * </p>
 * 
 * @author TheWhiteShadow
 */
public class DataTableConverter implements Converter
{
	@Override
	public boolean canConvert(Class clazz)
	{
		return (clazz.equals(DataTable.class));
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
        if (source instanceof DataTable)
        {
        	DataTable<?> table = (DataTable) source;
        	writer.addAttribute("name", table.getName());
        	writer.addAttribute("type", table.getElementClass().getName());
        	for (DatabaseObject obj : table.values())
        	{
        		writer.startNode(table.getElementClass().getName());
        		context.convertAnother(obj);
        		writer.endNode();
        	}
        }
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		String name = reader.getAttribute("name");
		if (name == null) throw new ConversionException("Attribut 'name' not found.");
		String type = reader.getAttribute("type");
		if (type == null) throw new ConversionException("Attribut 'type' not found.");

		try
		{
			DataTable<?> table = new DataTable(name, Class.forName(type, true, context.getClass().getClassLoader()));
			unmarshalElements(table, reader, context);
			return table;
		}
		catch (ClassNotFoundException e)
		{
			throw new ConversionException(e);
		}
	}

	private void unmarshalElements(DataTable table, HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		while (reader.hasMoreChildren())
		{
			reader.moveDown();
			try
			{
				Class clazz = Class.forName(reader.getNodeName(), true, context.getClass().getClassLoader());
				DatabaseObject obj = (DatabaseObject) context.convertAnother(table, clazz);
				
				table.put(obj);
			}
			catch (ClassNotFoundException e)
			{
				throw new ConversionException(e);
			}

			reader.moveUp();
		}
	}
}
