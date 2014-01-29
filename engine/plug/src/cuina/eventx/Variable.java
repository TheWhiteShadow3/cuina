package cuina.eventx;

import java.io.Serializable;

/**
 * Definiert eine Variable des Interpreters.
 * Wenn ein Befehl diese Klasse als Argument hat,
 * wird stattdessen die Variable vom Interpreter benutzt.
 * @author TheWhiteShadow
 */
public class Variable implements Serializable
{
	private static final long serialVersionUID = 4886821328698419839L;
	
	public final String name;

	public Variable(String name)
	{
		this.name = name;
	}
}
