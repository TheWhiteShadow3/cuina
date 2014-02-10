package cuina.event;

/**
 * Ein allgemeiner Trigger, der eine beliebige Aktion ausführen kann.
 * @author TheWhiteShadow
 */
public abstract class AbstractTrigger implements Trigger
{
	private static final long serialVersionUID = 6582839276259158753L;

	private Event event;
	private Object arg;
	private boolean active = true;
	
	public AbstractTrigger(Event event)
	{
		this(event, null);
	}
	
	public AbstractTrigger(Event event, Object arg)
	{
		setEvent(event);
		this.arg = arg;
	}
	
	public Object getArgument()
	{
		return arg;
	}

	public void setArgument(Object arg)
	{
		this.arg = arg;
	}
	
	public void setEvent(Event event)
	{
		if (event == null) throw new NullPointerException("Event is null.");
		this.event = event;
	}

	public Event getEvent()
	{
		return event;
	}

	@Override
	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public boolean isActive()
	{
		return active;
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return event.equals(Event.ALWAYS) || this.event.equals(event) && (this.arg == null || this.arg.equals(arg));
	}

	@Override
	public abstract void run(Object... args);
}
