package cuina.eventx;

import cuina.FrameTimer;
import cuina.InjectionManager;
import cuina.event.Trigger;
import cuina.plugin.LifeCycleAdapter;
import cuina.plugin.Priority;

/**
 * Ein Frame basierter Timer, der nach Ablauf einen Trigger aufrufen kann.
 * @author TheWhiteShadow
 */
@Priority(updatePriority=500)
public class Timer extends LifeCycleAdapter
{
	private Trigger trigger;
	private int startTime;
	private int frames;
	private boolean repeat;
	private boolean run;

	/**
	 * Erstellt einen Timer. Der Timer wird zwar gestartet, muss aber injektiert werden um laufen zu können.
	 * <p>
	 * Falls der Timer dem Session-Kontext zugewiesen wird, bleibt er beim laden der Session erhalten.
	 * Beispielsweise über <code>InjectionManager.injectSessionObject</code>.
	 * </p>
	 * @param trigger Trigger, der ausgelöst werden soll, wenn der Timer abläuft.
	 * @param time Zeit in 1/10 Sekunden.
	 * @param repeat Gibt an, ob der Timer wiederholt laufen soll.
	 * @see InjectionManager
	 */
	public Timer(Trigger trigger, int time, boolean repeat)
	{
		this.trigger = trigger;
		this.repeat = repeat;
		this.startTime = time;
		start();
	}
	
	public void start()
	{
		this.frames = startTime * FrameTimer.getTargetFPS() / 10;
		run = true;
	}
	
	public void stop()
	{
		run = false;
	}
	
	/**
	 * Gibt die verbliebene Zeit zurück.
	 * @return Verbliebene Zeit in 1/10 Sekunden.
	 */
	public int getTime()
	{
		return this.frames - startTime * FrameTimer.getTargetFPS() / 10;
	}
	
	@Override
	public void update()
	{
		if (!run) return;
		
		frames--;
		if (frames <= 0)
		{
			trigger.run();
			if (!repeat)
			{
				run = false;
				return;
			}
			start();
		}
	}
	
	@Override
	public void dispose()
	{
		stop();
	}
}
