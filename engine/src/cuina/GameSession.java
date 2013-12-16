package cuina;

import java.io.Serializable;
import java.util.Stack;

public class GameSession implements Serializable
{
	private static final long serialVersionUID	= 455822378602260374L;
	
	public String sceneName;
	public Context context;
	// TODO Schalter- und Variablen-Arrays aus Datei dimensionieren.
	/** Globale Schalter dienen zum Steuern des Spielablaufs. */
	public boolean[] switches = new boolean[100];
	/** Globale Variablen dienen zum Speichern und Rechnen von Zahlenwerten im Spiel. */
	public long[] vars = new long[100];
	private final Stack<Object> stack = new Stack<Object>();
	
	public GameSession(Context context)
	{
		this.context = context;
	}
	
	public Object pop()
	{
		return stack.pop();
	}
	
	public void push(Object value)
	{
		stack.push(value);
	}
	
	public void clearStack()
	{
		stack.clear();
	}
}