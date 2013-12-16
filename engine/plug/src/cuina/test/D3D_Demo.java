/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.test;

import cuina.Input;
import cuina.graphics.GraphicSet;
import cuina.graphics.Graphics;
import cuina.graphics.Light;
import cuina.graphics.Mesh;
import cuina.graphics.Shader;
import cuina.graphics.d3d.Model;
import cuina.map.GameMap;
import cuina.object.BaseObject;
import cuina.plugin.ForSession;
import cuina.plugin.LifeCycle;
import cuina.plugin.LifeCycleAdapter;
import cuina.plugin.Plugin;
import cuina.util.Vector;
import cuina.world.CuinaModel;

import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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
@ForSession(name="D3D_Demo", scenes={"Mapp"})
public class D3D_Demo extends LifeCycleAdapter implements Plugin
{
	private static final long serialVersionUID = 8908336574337263677L;
	
	@Override
	public void init()
	{
		GameMap world = GameMap.getInstance();
		
		BaseObject obj = new BaseObject();
		obj.setID(world.getAvilableID());
		obj.addExtension("model", new Model3D());

		GameMap.getInstance().addObject(obj);
		
		Color ambient = new Color(0.05f, 0f, 0.15f, 1f);
		
		Light light = new Light(Color.WHITE, ambient, Color.WHITE, new Vector(12, 8, 4), false);
		light.bind(GL11.GL_LIGHT0);
	}

	@Override
	public String toString()
	{
		return "3D-Demo";
	}
}

class Model3D implements CuinaModel, LifeCycle
{
	private static final long serialVersionUID = -3338863848877861923L;
	
	private Model model;
	private boolean visible = true;
	private Shader shader;
	private boolean useShader = true;
	private boolean rotate = false;

	private GraphicSet container;
	
	public Model3D()
	{
		shader = new Shader("shader/normal");
		container = new GraphicSet("3D", 0, Graphics.GraphicManager);
		container.setShader(shader);
		
//		GL20.glVertexAttribPointer(tangentLoc, 3, GL_FLOAT,GL_FALSE, 0, tangentArraySkinPointer);
		
//		this.model = new Model(new Mesh(2), "backgrounds/BlueSky.jpg", container);
		this.model = new Model(new Mesh(6), "backgrounds/test_normal.png", container);
		model.pos.z = -6;
//		Graphics.camera.fromZ = 30;
//		Graphics.camera.toZ = 10;
//		Graphics.camera.fromY = 30;
		System.out.println("[D3D_Demo] Debug-Info:");
		System.out.println("\tDrücke Q um den Normal-Shader neu zu laden.");
		System.out.println("\tDrücke W um den Shader ein/aus zu schalten.");
	}

	@Override
	public void init() {}

	@Override
	public void refresh() {}

	@Override
	public void setPosition(float x, float y, float z)
	{
		model.pos.set(x, y, z);
	}

	@Override
	public void dispose()
	{
		model.dispose();
	}

	@Override
	public void setVisible(boolean value)
	{
		this.visible = value;
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public float getX()
	{
		return model.pos.x;
	}

	@Override
	public float getY()
	{
		return model.pos.y;
	}
	
	@Override
	public float getZ()
	{
		return model.pos.z;
	}

	@Override
	public int getWidth()
	{
		return 0;
	}

	@Override
	public int getHeight()
	{
		return 0;
	}

	@Override
	public void update()
	{
		if (Input.isPressed(Keyboard.KEY_W))
		{
			useShader = !useShader;
			container.setShader(useShader ? shader : null);
			System.out.println("Use Shader = " + useShader);
		}
		
		if (Input.isPressed(Keyboard.KEY_Q))
		{
			shader.refresh();
			System.out.println("Shader refreshed.");
		}
		
		if (Input.isPressed(Keyboard.KEY_SPACE))
		{
			rotate = !rotate;
		}
	}
	
	@Override
	public void postUpdate()
	{
		if (rotate)
			model.angle.z += 0.5f;
		else
			model.angle.z = 0;
		
//		GL11.glPushMatrix();
//		D3D.drawSphere(16);
//		GL11.glPopMatrix();
	}
}
