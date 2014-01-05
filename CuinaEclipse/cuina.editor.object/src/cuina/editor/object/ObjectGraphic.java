package cuina.editor.object;

import cuina.gl.Image;

import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.swt.opengl.GLCanvas;

/**
 * Eine Grafik für ein Objekt.
 * Ein mögliches Model als Objekt-Erweiterung muss dieses Interface implementieren oder
 * über eine AdapterFactory für die Instanzen der Klasse {@link ObjectAdapter} zur verfügung stellen,
 * damit das Model als Grundlage für die Darstellung auf der Karte verwendet wird.
 * @author TheWhiteShadow
 */
public interface ObjectGraphic
{
	/**
	 * Gibt das Image zurück.
	 * Zuvor muss erstmalig {@link #setGLCanvas(GLCanvas)} aufgerufen werden um den Kontext festzulegen.
	 * @return Das Image.
	 * @throws IllegalStateException Wenn der Grafik-Kontext nicht festgelegt wurde.
	 */
	public Image getImage() throws IllegalStateException;
	
	/**
	 * Gibt den Offset zum Aufhängungspunkt des Bildes zurück.
	 * Wenn die Grafik null ist, ist der Rückgabewert nicht definiert.
	 * @return Offset zum Aufhängungspunkt.
	 */
	public Point getOffset();
	
	/**
	 * Setzt den Grafik-Kontex für das Image.
	 * Diese Methode muss aufgerufen werden bevor das Image angefordert wird.
	 * @param canvas auf dem das Image gezeichnet werden soll.
	 */
	public void setGLCanvas(GLCanvas canvas);
	
	/**
	 * Gibt den Rahmen der Grafik zurück.
	 * Wenn die Grafik null ist, ist der Rückgabewert nicht definiert.
	 * @return den Rahmen der Grafik.
	 */
	public Rectangle getClipping();
	
	/**
	 * Gibt den Dateinamen der Grafik zurück, sofern vorhanden.
	 * @return Den Dateinamen.
	 */
	public String getFilename();
}
