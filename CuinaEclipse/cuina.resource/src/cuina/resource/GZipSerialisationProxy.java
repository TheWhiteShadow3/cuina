package cuina.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipSerialisationProxy implements SerializationProvider
{
	private static final int BUFFER_SIZE = 1 << 16; // 64KB
	
	private SerializationProvider clientProvider;
	
	public GZipSerialisationProxy(SerializationProvider clientProvider)
	{
		this.clientProvider = Objects.requireNonNull(clientProvider);
	}
	
	@Override
	public Object load(InputStream in, ClassLoader cl) throws IOException, ClassNotFoundException
	{
		return clientProvider.load(new GZIPInputStream(in, BUFFER_SIZE), cl);
	}

	@Override
	public void save(Object obj, OutputStream out) throws IOException
	{
		clientProvider.save(obj, new GZIPOutputStream(out, BUFFER_SIZE, true));
	}
	
//	private SerializationProvider getProvider(String name) throws IOException
//	{
//		int p = name.lastIndexOf('.');
//		if (p == -1) throw new IOException("Filename '"+ name +"' has no extension!");
//		
//		String ext = name.substring(p+1);
//		SerializationProvider provider = SerializationManager.getSerializationProvider(ext);
//		if (provider == null) throw new IOException("No provider for extension '" + ext + "' found!");
//		
//		return provider;
//	}
}
