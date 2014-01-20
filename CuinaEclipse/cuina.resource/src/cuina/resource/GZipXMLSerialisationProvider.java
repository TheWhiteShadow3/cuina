package cuina.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipXMLSerialisationProvider implements SerializationProvider
{
	private static final int BUFFER_SIZE = 1 << 16; // 64KB
	
	private XmlSerializationProvider XML_SERIALISATIN_PROVIDER = new XmlSerializationProvider();
	
	@Override
	public Object load(InputStream in, ClassLoader cl) throws IOException, ClassNotFoundException
	{
		return XML_SERIALISATIN_PROVIDER.load(new GZIPInputStream(in, BUFFER_SIZE), cl);
	}

	@Override
	public void save(Object obj, OutputStream out) throws IOException
	{
		XML_SERIALISATIN_PROVIDER.save(obj, new GZIPOutputStream(out, BUFFER_SIZE, true));
	}
}
