package cuina.editor.script.internal.ruby;

public class ParseException extends Exception
{
	private static final long serialVersionUID = -4051613885815019779L;

	public ParseException()
    {
        super();
    }
    
    public ParseException(String string)
    {
        super(string);
    }
 
    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
 
    public ParseException(Throwable cause)
    {
        super(cause);
    }
}