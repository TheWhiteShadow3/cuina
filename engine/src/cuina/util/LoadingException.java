package cuina.util;

import java.io.File;
import java.io.IOException;

/**
 * Fehler beim laden einer Datei.
 * Enthält eine angepasste Meldung in der eingestellten Sprache.
 * @author TheWhiteShadow
 */
public class LoadingException extends IOException
{
	private static final long	serialVersionUID	= 1L;
	
	public static String NotFoundMessage = "'%s' konnte nicht geladen werden.";
	
	public LoadingException() { super(); }
	public LoadingException(String ress)
	{
		super( String.format(NotFoundMessage, ress) );
	}
	public LoadingException(File file)
	{
		super( String.format(NotFoundMessage, file.getPath()) );
	}
	public LoadingException(String ress, Throwable cause)
	{
		super( String.format(NotFoundMessage, ress), cause);
	}
	public LoadingException(File file, Throwable cause)
	{
		super( String.format(NotFoundMessage, file.getPath()), cause);
	}
}
