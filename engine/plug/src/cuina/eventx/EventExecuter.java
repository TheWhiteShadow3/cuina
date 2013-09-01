package cuina.eventx;

import cuina.script.ScriptExecuter;

import java.util.ArrayList;

import org.jruby.RubyFixnum;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

@Deprecated
public class EventExecuter
{
	private static ArrayList<FiberTask> tasks = new ArrayList<FiberTask>();
	
	private static IRubyObject fiber;

	public static void init()
	{
		ScriptExecuter.getRuntime().getLoadService().require("fiber");
		
		String fiberScript = 
		"def self.wait frames\n" +
		"	Fiber.yield frames\n" +
		"end\n" +
		"def self.stop\n" +
		"	Fiber.yield 0\n" +
		"end\n" +
		"class Fiber_Executer\n" + 
		"	def run block\n" +
		"		return Fiber.new do\n" +
		"			block.call\n" +
		"		end\n" +
		"	end\n" +
		"end\n" +
		"Fiber_Executer.new\n";
		fiber = ScriptExecuter.eval(fiberScript);
	}

	public static void runEvent(String event)
	{
		IRubyObject block = ScriptExecuter.eval("Proc.new {" + event + "}");
		FiberTask task = new FiberTask(fiber.callMethod(getContext(), "run", block));
//		task.resume();
		tasks.add(task);
	}
	
	public static void update()
	{
		for (int i = tasks.size()-1; i >= 0; i--)
		{
			FiberTask task = tasks.get(i);
			task.waitCount--;
			if (task.waitCount > 0) continue;
			
			task.resume();
			if (task.waitCount == 0) break;
			if (task.waitCount < 0) tasks.remove(i);
		}
	}
	
	public static boolean isRunning()
	{
		return tasks.size() > 0;
	}
	
	private static ThreadContext getContext()
	{
		return ScriptExecuter.getRuntime().getCurrentContext();
	}
	
	private static class FiberTask
	{
		public int waitCount;
		public IRubyObject fiber;
		
		public FiberTask(IRubyObject fiber)
		{
			this.fiber = fiber;
		}

		public void resume()
		{
			IRubyObject result = fiber.callMethod(getContext(), "resume");
			
			if (!result.isNil())
				waitCount = (int) ((RubyFixnum) result).getLongValue();
			else
				waitCount = -1;
		}
	}
}
