package cuina.test;

import cuina.Input;
import cuina.graphics.GraphicSet;
import cuina.graphics.GraphicUtil;
import cuina.graphics.Graphics;
import cuina.graphics.Shader;
import cuina.graphics.Texture;
import cuina.graphics.TextureLoader;
import cuina.graphics.View;
import cuina.map.GameMap;
import cuina.map.TileMap;
import cuina.movement.Motor;
import cuina.object.BaseObject;
import cuina.rpg.Player;
import cuina.util.LoadingException;
import cuina.util.Rectangle;
import cuina.util.Vector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("deprecation")
public class ShaderTest
{
	private Shader shader;
	private boolean useShader = true;
	private GraphicSet container;
	
	public void init()
	{
//		EventTest.setup();
//		AnimationFunctionTest.setup();
//		AnimationStressTest.setup();
		
		try
		{
			shader = new Shader("shader/normal");
			shader.setUniform("normalTexture", 1);
			shader.setUniform("specularTexture", 2);
			
			GameMap map = GameMap.getInstance();
			Texture normalTex = TextureLoader.getInstance().getTexture("tilesets/Schule_light.png", 0);
			Texture specularTex = TextureLoader.getInstance().getTexture("tilesets/Schule_Spec.png", 0);
			TileMap tileMap = map.getTilemap();
			tileMap.getImageSet().addTexture(normalTex, GL11.GL_MODULATE);
			tileMap.getImageSet().addTexture(specularTex, GL11.GL_MODULATE);
//			tileMap.getAutotileImage(0, 0).addTexture(texture, GL11.GL_MODULATE);
			
			container = (GraphicSet) map.getGraphicContainer();
			container.setShader(shader);
			
			View view = Graphics.VIEWS.get(0);
			view.target = map.getObject(1);
			view.border = new Rectangle(map.getWidth(), map.getHeight());
			
			View view2 = new View(Graphics.getWidth() / 2, Graphics.getHeight());
			view2.port.x = Graphics.getWidth() / 2;
			view2.target = map.getObject(2);
			view2.border = new Rectangle(map.getWidth(), map.getHeight());
			view.width = view2.width;
			view.port.width = view2.port.width;
			Graphics.VIEWS.add(view2);
			
			((Motor) view2.target.getExtension(Motor.EXTENSION_KEY)).setDriver(new Player());
//			Graphics.view.target = map.getObject(2);
			
//			map.getGraphicContainer().setFlag(GL11.GL_LIGHTING, true);
			
//			Light light = new Light(new Color(255, 255, 192), new Color(0, 3, 10), Color.WHITE, new Vector(), false);
//			light.bind(GL11.GL_LIGHT0);
//			light.unbind(GL11.GL_LIGHT0);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
	}

	public void update()
	{
		if (Input.isPressed(Keyboard.KEY_Y))
		{
			useShader = !useShader;
			container.setShader(useShader ? shader : null);
			System.out.println("Use Shader = " + useShader);
		}
		
		if (Input.isPressed(Keyboard.KEY_X))
		{
			shader.refresh();
//			shader.setUniform("normalTexture", 1);
//			shader.setUniform("specularTexture", 2);
			System.out.println("Shader refreshed.");
		}
		
		View view0 = Graphics.VIEWS.get(0);
		BaseObject obj = (BaseObject) view0.target;

		Vector v1 = new Vector(obj.getX(), obj.getY() - 16, 32f);
		Vector v2 = new Vector(obj.getX(), obj.getY() - 16, 128f);
		GraphicUtil.setLightPosition(GL11.GL_LIGHT0, v1);
		GraphicUtil.setLightPosition(GL11.GL_LIGHT1, v2);
//		GlobalContext.<BaseWorld>get("World").update();
//		if (!Game.getWorld().isFreezed())
//		{
//			EventExecuter.update();
//		}
//		EventTest.update();
//		AnimationFunctionTest.update();
//		AnimationStressTest.update();
	}
}
