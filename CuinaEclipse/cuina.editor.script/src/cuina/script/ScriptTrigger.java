package cuina.script;

import cuina.database.KeyReference;
import cuina.event.AbstractTrigger;
import cuina.event.Event;

/**
 * Die SkriptCall-Klasse stellt eine einfache Implementierung des Triggers für Skriptaktionen da.
 * @author TheWhiteShadow
 */
public class ScriptTrigger extends AbstractTrigger
{
	private static final long serialVersionUID = 3920386985408427973L;
	
	@KeyReference(name="Script")
	private String script;
	private String main;

	public ScriptTrigger(Event event, String script, String main)
	{
		this(event, null, script, main);
	}

	public ScriptTrigger(Event event, Object arg, String script, String main)
	{
		super(event, arg);
		this.script = script;
		this.main = main;
	}
	
	public void setScript(String script)
	{
		this.script = script;
	}

	public void setMain(String main)
	{
		this.main = main;
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
}
