package cuina.script;

import cuina.event.Trigger;
import cuina.event.Event;

/**
 * Die SkriptCall-Klasse stellt eine einfache Implementierung des Triggers da.
 * @author TheWhiteShadow
 */
public class ScriptTrigger implements Trigger
{
	private static final long serialVersionUID = 3920386985408427973L;
	
	public Event event;
	public Object eventArg;
	public String script;
	public String main;
	public boolean active = true;

	public ScriptTrigger(String script, String main)
	{
		this.script = script;
		this.main = main;
	}

	@Override
	public Event getEvent()
	{
		return event;
	}

	@Override
	public Object getEventArg()
	{
		return eventArg;
	}

	public String getScript()
	{
		return script;
	}

	public String getMain()
	{
		return main;
	}

	@Override
	public void run(Object... args)
	{
		ScriptExecuter.execute(script, main, args);
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return isActive() && this.event == event && eventArg.equals(arg);
	}
}
