package cuina.event;

/**
 * Dieser Trigger stellt ein Wrapper für einen Trigger da.
 * Der Wrapper ermöglicht die Vor-Parametrisierung des Triggers.
 * @author TheWhiteShadow
 */
public class ParameterizedTrigger implements Trigger
{
	private static final long serialVersionUID = 530353529911791387L;
	
	private Trigger trigger;
	private Object[] parameters;

	public Trigger getTrigger()
	{
		return trigger;
	}

	public void setTrigger(Trigger trigger)
	{
		this.trigger = trigger;
	}
	
	@Override
	public Event getEvent()
	{
		return trigger.getEvent();
	}
	
	public Object[] getParameters()
	{
		return parameters;
	}

	public void setParameters(Object[] parameters)
	{
		this.parameters = parameters;
	}

	@Override
	public void setActive(boolean value)
	{
		trigger.setActive(value);
	}

	@Override
	public boolean isActive()
	{
		return trigger.isActive();
	}

	@Override
	public boolean test(Event event, Object arg)
	{
		return trigger.test(event, arg);
	}

	@Override
	public void run(Object... args)
	{
		trigger.run(parameters);
	}
}
