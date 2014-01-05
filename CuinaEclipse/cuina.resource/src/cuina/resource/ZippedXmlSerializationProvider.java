package cuina.resource;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZippedXmlSerializationProvider implements SerializationProvider
{
	private final static XmlSerializationProvider XML_SERIALIZAZION_PROVIDER = new XmlSerializationProvider();
	
	@Override
	public Object load(InputStream in, ClassLoader cl) throws IOException, ClassNotFoundException
	{
		BufferedInputStream ois = new BufferedInputStream(in);
		Object obj = XML_SERIALIZAZION_PROVIDER.load(new GZIPInputStream(ois), cl);
		ois.close();
		
		return obj;
	}

	@Override
	public void save(Object obj, OutputStream out) throws IOException
	{
		BufferedOutputStream oos = new BufferedOutputStream(new GZIPOutputStream(out));
		XML_SERIALIZAZION_PROVIDER.save(obj, oos);
		
		oos.flush();
		oos.close();
	}

}
