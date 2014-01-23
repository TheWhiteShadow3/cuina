package cuina;

/**
 * Bietet die Möglichkeit Einträge in einem Kontext nachzuverfolgen.
 * Der Listener kann einem oder mehrerer Kontexte übergeben werden und wird
 * bei Änderungen eines Eintrags informiert.
 * <p>
 * <b>Beispiel:</b>
 * <pre>Game.getContext(Context.GLOBAL).addContextListener(l);</pre>
 * </p>
 * @author TheWhiteShadow
 */
public interface ContextListener
{
	/**
	 * Wird aufgerufen, wenn sich ein Eintrag in einem Kontext, in dem der Listener registriert ist, ändert.
	 * @param context Der Kontext.
	 * @param key der Schlüssel.
	 * @param oldValue alter Wert.
	 * @param newValue neuer Wert.
	 */
	public void entryChanged(Context context, String key, Object oldValue, Object newValue);
	
	/**
	 * Wird aufgerufen, wenn der Kontext seine gültigkeit verliert.
	 * Zum Zeitpunkt des Aufrufs besitzt der Kontext noch alle seine Einträge.
	 * @param context Der Kontext.
	 */
	public void contextDisposing(Context context);
	
	/**
	 * Wird aufgerufen, wenn alle Einträge im Kontext gelöscht werden.
	 * Zum Zeitpunkt des Aufrufs besitzt der Kontext noch alle seine Einträge.
	 * @param context Der Kontext.
	 */
	public void contextClearing(Context context);
}
