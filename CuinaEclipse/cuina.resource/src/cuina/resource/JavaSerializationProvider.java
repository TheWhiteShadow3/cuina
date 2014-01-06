package cuina.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;

public class JavaSerializationProvider implements SerializationProvider
{
	@Override
	public Object load(InputStream in, final ClassLoader cl) throws IOException, ClassNotFoundException
	{
		return new ObjectInputStream(in)
		{
			@Override
			protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
			{
	            return Class.forName(desc.getName(), false, cl);
			}
		}.readObject();
	}

	@Override
	public void save(Object obj, OutputStream out) throws IOException
	{
		new ObjectOutputStream(out).writeObject(obj);
	}
}
