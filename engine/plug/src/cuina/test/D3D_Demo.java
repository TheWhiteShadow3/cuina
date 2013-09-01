/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.test;

import cuina.plugin.ForSession;
import cuina.plugin.LifeCycleAdapter;
import cuina.plugin.Plugin;

/* Funktion:
 * Graphics kann nun 3D-Modelle zeichnen. Eine Test-Kamera wird beim initialisiren erstellt.
 * die D3D_Cube-Klasse stellt automatisch 3D an, rendert sich und steltl 3D wieder aus.
 * Hier wird ein Objekt und erstellt, was der Map(World) hinzugefügt wird.
 * Ebenfalls wird hier ein Licht erstellt.
 * 
 * Benutzung:
 * Erstelle einen leern Raum und sorge dafür, dass es im Background-Ordner eine Datei "boden.jpg" gibt.
 * 
 * Aktuell nicht implementiert. Kommt aber wieder
 */
@ForSession(name="D3D_Demo", scenes={"Map"})
public class D3D_Demo extends LifeCycleAdapter implements Plugin
{
	private static final long serialVersionUID = 8908336574337263677L;
	
	@Override
	public void init()
	{
		//Game.getWorld().addObject(new Object3D());
		
		//D3D.setLight(GL11.GL_LIGHT1, Color.WHITE, Color.BLUE, Color.YELLOW, new Vector(0, 5, 10));
	}
}
