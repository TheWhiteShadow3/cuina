package cuina.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class JavaSerializationProvider implements SerializationProvider
{
	private static final int BUFFER_SIZE = 1 << 16; // 64KB
	
	@Override
	public Object load(InputStream in, final ClassLoader cl) throws IOException, ClassNotFoundException
	{
		return new ObjectInputStream(new GZIPInputStream(in, BUFFER_SIZE))
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
		new ObjectOutputStream(new GZIPOutputStream(out, BUFFER_SIZE, true)).writeObject(obj);
	}
}
