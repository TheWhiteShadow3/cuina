package cuina.object;

public class BuilderException extends RuntimeException
{
	private static final long serialVersionUID = -3367690291077390496L;

	public BuilderException(String message)
	{
		super("Exception in Build-Process: " + message);
	}
}
