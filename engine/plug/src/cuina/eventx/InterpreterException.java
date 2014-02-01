package cuina.eventx;

import cuina.Logger;
import cuina.eventx.Interpreter.Setup;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * Beschreibt eine Ausnahme im Interpreter.
 * Die Klasse ist in der Lage den Stacktrace vom Interpreter zu schreiben.
 * @author TheWhiteShadow
 */
public class InterpreterException extends Exception
{
	private static final long serialVersionUID = -2416822469694379763L;
	
	private Setup setup;
	private Command cmd;
	
	InterpreterException(Setup setup, Command cmd, Throwable e)
	{
		super("An Exception in called Method occured.", e);
		this.setup = setup;
		this.cmd = cmd;
	}

	InterpreterException(Setup setup, Command cmd, String message)
	{
		super(message);
		this.setup = setup;
		this.cmd = cmd;
	}

	@Override
	public void printStackTrace(PrintStream ps)
	{
		if (cmd != null)
		{
			ps.print("\ton Command ");
			ps.println(cmd);
		}

		Setup s = setup;
		while(s != null)
		{
			ps.print("\tat Index ");
			ps.print(s.index);
			ps.print(" in '");
			ps.print(s.list.getKey());
			if (Logger.logLevel <= Logger.DEBUG)
			{
				ps.print("' called with ");
				ps.print(Arrays.toString(s.args));
			}
			ps.println();
			s = s.parent;
		}
		
		if (Logger.logLevel <= Logger.DEBUG) super.printStackTrace(ps);
		else if (getCause() != null)
		{
			// Ein gekürzter StackTrace
			Throwable ex = getCause();
			ps.print("Caused by: ");
			ps.print(ex.getClass().getName());
			ps.print(": ");
			ps.println(ex.getMessage());
			StackTraceElement[] stackTrace = ex.getStackTrace();
			int length = stackTrace.length - Thread.currentThread().getStackTrace().length - 2;
			for (int i = 0; i < length; i++)
			{
				ps.print("\tat ");
				ps.println(stackTrace[i]);
			}
		}
	}
}
