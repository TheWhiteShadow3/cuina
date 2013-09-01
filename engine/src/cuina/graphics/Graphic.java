/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import java.io.Serializable;


/**
 * Stellt die Basisklasse für alle Grafik-Elemete in der Engine da.
 * Jedes Grafik-Element kann einem Kontainer zugewiesen werden.
 * Damit das Grafik-Element gezeichnet werden kann, muss es direkt 
 * oder indirekt dem aktuellen {@link GraphicManager} zugeortnet sein.
 * <p>
 * Es wird Empfohlen Implementierungen von der {@link AbstractGraphic}-Klasse abzuleiten,
 * anstatt direkt von <code>Graphic</code>.
 * </p>
 * @author TheWhiteShadow
 */
public interface Graphic extends Serializable
{
	/**
	 * Gibt den Z-Wert des Elements an. Der Wert wird benutzt um die Zeichenreihenfolge zu bestimmen.
	 * @see ZComparator
	 */
	public int getDepth();
	
	/**
	 * Stellt die Grafiken nach einer Neu-Initialisierung wieder her.
	 */
	public void refresh();
	
	/**
	 * Löscht das GraphicElement indem es aus seinem Kontainer entfernt wird.
	 */
	public void dispose();
	
	public void draw();

	public void setContainer(GraphicContainer container);
	public GraphicContainer getContainer();
}
