package cuina;

/**
 * Ein Event für ein globales Ereignis in der Engine.
 * Globale Ereignisse betreffen die Engine selbst, wie auch die Session und die Szene.
 * @author TheWhiteShadow
 */
public final class GameEvent
{
	/** Signalisiert das Starten der Engine. */
	public static final int START_GAME  		= 1;
	
	/** Signalisiert das Erstellen eines Session. */
	public static final int OPEN_SESSION 		= 2;
	
	/** Signalisiert das Speichern einer Session. */
	public static final int SESSION_SAVED 		= 3;
	
	/** Signalisiert das Laden einer Session. */
	public static final int SESSION_LOADED 		= 4;
	
	/** Signalisiert das Beenden einer Session. */
	public static final int CLOSING_SESSION 	= 5;
	
	/** Signalisiert das Wechseln der Szene. */
	public static final int NEW_SCENE 			= 6;
	
	/** Signalisiert das Herunterfahren der Engine. */
	public static final int END_GAME 			= 7;
	
	/** Gibt den Typ des Events an. */
	public int type;
	
	/** Gibt die aktuelle Szene an. */
	public Scene scene;
	
	/** Gibt die aktuelle Session an. */
	public GameSession session;
	
	/**
	 * Erstellt ein neues GameEvent.
	 * @param type Event-Typ.
	 * @param scene aktuelle Szene.
	 * @param session aktuelle Session.
	 */
	public GameEvent(int type, Scene scene, GameSession session)
	{
		this.type = type;
		this.scene = scene;
		this.session = session;
	}
}
