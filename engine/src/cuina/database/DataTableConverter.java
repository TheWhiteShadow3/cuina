package cuina.database;


import cuina.util.CuinaClassLoader;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

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
			DataTable<?> table = new DataTable(name, getClass(type));
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
				Class clazz = getClass(reader.getNodeName());
				DatabaseObject obj = (DatabaseObject) context.convertAnother(table, clazz);
				
				table.put(obj);
			}
			catch (ClassNotFoundException e)
			{
				throw new ConversionException(e);
			}
//			expectNode(reader, table.getElementClass().getName());


			reader.moveUp();
		}
	}
	
	private Class getClass(String className) throws ClassNotFoundException
	{
		return Class.forName(className, true, CuinaClassLoader.getInstance());
	}
}
