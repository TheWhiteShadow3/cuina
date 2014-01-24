package cuina.widget;

import cuina.graphics.Image;

/**
 * Dient zur Behandlung von eigenen Formatierungs-Anweisung in Texten.
 * Um diese Klasse zu nutzen muss eine Implementation beim TextParser über die 
 * Methode <code>setTextTokenHandler</code> registriert werden.
 * Dieser ruft dann bei jedem Vorkommen einer unbekannten Formaieranweisung die Methode
 * <code>nextToken</code> des TextTokenHandlers auf.
 * @author TheWhiteShadow
 * @see TextParser
 */
public interface TextTokenHandler
{
	public String nextToken(Image image, FormatCommand cmd);
}
