package cuina.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerializationProvider
{
	public Object load(InputStream in, ClassLoader cl) throws IOException, ClassNotFoundException;
	public void save(Object obj, OutputStream out) throws IOException;
}
