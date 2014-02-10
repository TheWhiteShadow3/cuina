package cuina.eventx;

import cuina.Game;
import cuina.eventx.Interpreter.Result;
import cuina.input.Input;
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
	public static Result newSession()
	{
		Game.newGame();
		return Result.DEFAULT;
	}
	
	@EventMethod
	public static Result endSession()
	{
		Game.endGame();
		return Result.DEFAULT;
	}
	
	@EventMethod
	public static Result runScript(String key, String main, Object[] args)
	{
		ScriptExecuter.executeDirect(key, main, args);
		return Result.DEFAULT;
	}
	
	@EventMethod
	public static Result isButtonDown(String control)
	{
		return new Result(Input.getControl(control).isDown());
	}
	
	@EventMethod
	public static Result waitForButton(String control)
	{
		if (Input.getControl(control).isPressed())
			return Result.DEFAULT;
		else
			return new Result(1, -1, false);
	}
}
