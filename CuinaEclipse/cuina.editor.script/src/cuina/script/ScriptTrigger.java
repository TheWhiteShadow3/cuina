package cuina.script;

import cuina.event.Trigger;
import cuina.event.Event;

/**
 * Die SkriptCall-Klasse stellt eine einfache Implementierung des Triggers für Skriptaktionen da.
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

	public Event getEvent()
	{
		return event;
	}

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
		// dummy
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return (this.event.equals(event) && (eventArg == null || eventArg.equals(arg)));
	}
}
