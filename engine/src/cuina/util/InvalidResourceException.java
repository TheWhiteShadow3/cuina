package cuina.util;

public class InvalidResourceException extends Exception
{
	private static final long serialVersionUID = -190828220933024374L;

	public InvalidResourceException()
	{
		super();
	}

	public InvalidResourceException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidResourceException(String message)
	{
		super(message);
	}

	public InvalidResourceException(Throwable cause)
	{
		super(cause);
	}
	
}
