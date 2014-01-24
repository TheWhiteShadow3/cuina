package cuina.event;

public class DefaultTrigger implements Trigger
{
	private static final long serialVersionUID = 6582839276259158753L;

	private final Event event;
	private final Runnable action;
	private Object arg;
	private boolean active = true;
	
	public DefaultTrigger(Event event, Runnable action)
	{
		this(event, null, action);
	}
	
	public DefaultTrigger(Event event, Object arg, Runnable action)
	{
		this.event = event;
		this.action = action;
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

	public Event getEvent()
	{
		return event;
	}

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
		return this.event.equals(event) && (this.arg == null || this.arg.equals(arg));
	}

	@Override
	public void run(Object... args)
	{
		action.run();
	}
}
