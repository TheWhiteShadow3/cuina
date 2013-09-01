package cuina.eventx;

import java.io.Serializable;

/**
 * Definiert ein Aufruf-Argument des Interpreters.
 * Wenn ein Befehl diese Klasse als Argument hat,
 * wird stattdessen das Argument vom Interpreter benutzt.
 * @author TheWhiteShadow
 */
public final class Argument implements Serializable
{
	private static final long serialVersionUID = -7193025547619234946L;
	
	public final int index;

	public Argument(int index)
	{
		this.index = index;
	}
}