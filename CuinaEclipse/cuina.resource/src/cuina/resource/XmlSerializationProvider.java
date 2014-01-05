package cuina.resource;

import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class XmlSerializationProvider implements SerializationProvider
{
	public final static XStream X_STREAM;
	
	static
	{
		X_STREAM = new XStream();
//		xStream.useAttributeFor("key", String.class);
//		xStream.setClassLoader(XMLSerialisationProvider.class.getClassLoader());
		
		registerRules();
	}
	
	private static void registerRules()
	{
		IConfigurationElement[] elements = Platform.getExtensionRegistry().
				getConfigurationElementsFor("cuina.resource.serialisation.XML");
		
		for (IConfigurationElement e : elements) try
		{
			Bundle plugin = Platform.getBundle(e.getContributor().getName());
		
			for (IConfigurationElement rule : e.getChildren())
			{
				if ( "alias".equals(rule.getName()) )
				{
					String name = rule.getAttribute("name");
					Class<?> clazz = plugin.loadClass(rule.getAttribute("class"));
					X_STREAM.aliasType(name, clazz);
					System.out.println("[XML-SP] registriere Alias: " + name + " -> " + clazz.getName());
				}
				else if ( "converter".equals(rule.getName()) )
				{
					Class<?> clazz = plugin.loadClass(rule.getAttribute("class"));
					Object converter = clazz.newInstance();
					
					if (converter instanceof Converter)
						X_STREAM.registerConverter( (Converter) converter );
					else if (converter instanceof SingleValueConverter)
						X_STREAM.registerConverter( (SingleValueConverter) converter );
					else throw new IllegalArgumentException("Attribut 'class' must be a valid Converter.");
					System.out.println("[XML-SP] registriere Konverter: " + clazz.getName());
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public Object load(InputStream in, ClassLoader cl)
	{
//		xStream.setClassLoader(XMLSerialisationProvider.class.getClassLoader());
//		xStream.setClassLoader(cl);
		return X_STREAM.fromXML(in);
	}

	@Override
	public void save(Object obj, OutputStream out)
	{
		X_STREAM.toXML(obj, out);
	}
}
