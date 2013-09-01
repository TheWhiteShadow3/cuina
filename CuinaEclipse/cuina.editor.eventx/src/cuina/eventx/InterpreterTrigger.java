package cuina.eventx;

import cuina.database.KeyReference;
import cuina.event.Event;
import cuina.event.Trigger;

/**
 * Ein Trigger für den Interpreter.
 * <p>
 * Per Default sucht der Trigger den Interpreter im SessionKontext unter "Interpreter".
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
    @KeyReference(name="Event")
    private String key;
    private boolean active = true;

    public String getKey()
    {
        return key;
    }

	@Override
	public void run(Object... args)
	{
		// Dummy
	}

	public void setEvent(Event event)
	{
		this.event = event;
	}

	public void setEventArg(Object eventArg)
	{
		this.eventArg = eventArg;
	}

	public void setKey(String key)
	{
		this.key = key;
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

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return (isActive() && this.event.equals(event) &&
				(eventArg == null || eventArg.equals(arg)));
	}
}
