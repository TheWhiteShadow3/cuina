package cuina.util;
 
public class InvalidFileFormatException extends Exception
{
	private static final long serialVersionUID = -1691217540828736062L;

	public InvalidFileFormatException(int line, String message)
    {
        super("(line " + line + "): " + message);
    }
}