package cuina.event;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/*
 * XXX: XStream erlaubt keinen eigenen Konverter, wenn die Klasse die Java-internen
 * Serialisierungsmethoden readObject/writeObject besitzt.
 * Diese sind aber wichtig, wenn mal kein Xstream zum serialisieren benutzt wird.
 */
public class EventConverter implements SingleValueConverter
{
	@Override
	public boolean canConvert(Class type)
	{
		return type == Event.class;
	}

	@Override
	public String toString(Object obj)
	{
		return ((Event) obj).getName();
	}

	@Override
	public Object fromString(String str)
	{
		return Event.getEvent(str);
	}
}
