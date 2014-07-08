package cuina.resource;

import java.io.IOException;

import org.eclipse.core.resources.IFile;

/**
 * Fehler beim Zugriff auf eine Datei.
 * Enthält eine angepasste Meldung in der eingestellten Sprache.
 * @author TheWhiteShadow
 */
public class ResourceException extends IOException
{
	private static final long	serialVersionUID	= 1L;

	public static final int LOAD = 1;
	public static final int SAVE = 2;
	
	public static String LOADING_MESSAGE = "'%s' konnte nicht geladen werden.";
	public static String SAVING_MESSAGE = "'%s' konnte nicht gespeicher werden.";
	public static String OTHER_MESSAGE = "Zugriff auf Ressource '%s' fehlgeschlagen.";
	
	public ResourceException() { super(); }
	public ResourceException(String message)
	{
		super(message);
	}
	
	public ResourceException(String message, Exception cause)
	{
		super(message, cause);
	}
	
	public ResourceException(IFile file, int operation)
	{
		super(getMessage(file.toString(), operation));
	}
	
	public ResourceException(String message, int operation)
	{
		super(getMessage(message, operation));
	}
	
	public ResourceException(IFile file, int operation, Exception cause)
	{
		super(getMessage(file.toString(), operation), cause);
	}
	
	public ResourceException(String message, int operation, Exception cause)
	{
		super(getMessage(message, operation), cause);
	}
	
	private static String getMessage(String fileName,  int operation)
	{
		if (operation == LOAD)
			return String.format(LOADING_MESSAGE, fileName);
		else if (operation == SAVE)
			return String.format(SAVING_MESSAGE, fileName);
		else
			return String.format(OTHER_MESSAGE, fileName);
	}
}
