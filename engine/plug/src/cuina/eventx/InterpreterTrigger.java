package cuina.eventx;

import cuina.Logger;
import cuina.event.Event;
import cuina.event.Trigger;

/**
 * Ein Trigger für den Interpreter.
 * <p>
 * Per Default wird die globale Instanz des Interpreters benutzt
 * welche im globalen Kontext unter "Interpreter" liegt.
 * </p>
 * @author TheWhiteShadow
 */
public class InterpreterTrigger implements Trigger
{
	private static final long serialVersionUID = 5744717666030330178L;
	
    private Event event = Event.NEVER;
    private Object eventArg = null;
    /**
     * Der Key muss auf eine CommandList zeigen.
     */
//  @KeyReference(name="Event")
    private String key;
    private boolean active = true;

    public String getKey()
    {
        return key;
    }

	public void setKey(String key)
	{
		this.key = key;
	}
	
	@Override
	public void run(Object... args)
	{
		Interpreter interpreter = Interpreter.getGlobalInterpreter();
		if (interpreter != null)
			interpreter.setup(key, args);
		else
			Logger.log(InterpreterTrigger.class, Logger.ERROR, "Can not run trigger. Global interpreter is null.");
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}

	public void setEventArg(Object eventArg)
	{
		this.eventArg = eventArg;
	}
	
	public Object getEventArg()
	{
		return eventArg;
	}

	public Event getEvent()
	{
		return event;
	}

	@Override
	public boolean isActive()
	{
		return active;
	}
	
	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return (this.event.equals(event) && (eventArg == null || eventArg.equals(arg)));
	}
}
