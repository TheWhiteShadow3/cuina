package cuina.network;

import java.io.IOException;

public class NetworkException extends IOException
{
	public static final String UNEXPECTET_RESPONSE = "Unexpected response from Server.";
	
	private static final long serialVersionUID = -4457402030103230250L;

	public NetworkException()
	{
		super();
	}

	public NetworkException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NetworkException(String message)
	{
		super(message);
	}

	public NetworkException(Throwable cause)
	{
		super(cause);
	}
}
