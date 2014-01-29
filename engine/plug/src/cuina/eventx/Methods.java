package cuina.eventx;

import cuina.Game;
import cuina.eventx.Interpreter.Result;
import cuina.script.ScriptExecuter;

public class Methods
{
	private Methods() {}
	
	@EventMethod
	public static Result setScene(String name)
	{
		Game.newScene(name);
		return Result.DEFAULT;
	}
	
	@EventMethod
	public static Result runScript(String key, String main, Object[] args)
	{
		ScriptExecuter.executeDirect(key, main, args);
		return Result.DEFAULT;
	}
}
