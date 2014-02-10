package cuina.eventx;

import cuina.Logger;
import cuina.event.AbstractTrigger;
import cuina.event.Event;

/**
 * Ein Trigger für den Interpreter.
 * <p>
 * Per Default wird die globale Instanz des Interpreters benutzt
 * welche im globalen Kontext unter "Interpreter" liegt.
 * </p>
 * @author TheWhiteShadow
 */
public class InterpreterTrigger extends AbstractTrigger
{
	private static final long serialVersionUID = 5744717666030330178L;

    /**
     * Der Key muss auf eine CommandList zeigen.
     */
//  @KeyReference(name="Event")
    private String key;
    
	public InterpreterTrigger(Event event)
	{
		super(event);
	}
	
	public InterpreterTrigger(Event event, Object arg, String key)
	{
		super(event, arg);
		this.key = key;
	}
    
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
			interpreter.setup(this, args);
		else
			Logger.log(InterpreterTrigger.class, Logger.ERROR, "Can not run trigger. Global interpreter is null.");
	}
}
