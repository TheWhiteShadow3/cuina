package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.Game;
import cuina.Input;
import cuina.Logger;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.Color;

public class GraphicTest
{
	public static void main(String[] args)
	{
		try
		{
			Logger.setLogFile(null);
			
			new Game().loadConfig();
			
			Graphics.init("Test");
			Display.setVSyncEnabled(true);
	//		Texture texture = loadTextureAlternativ("faces/rosa.png");
	//		final Image image = new Image(texture);
			
			final Image img = Images.createImage("faces/rosa.png");
			
			final Image dynImage = Images.createImage(256, 256);
			dynImage.setColor(Color.GREEN);
			dynImage.drawRect(0, 0, dynImage.getWidth(), dynImage.getHeight(), false);
			dynImage.setColor(Color.WHITE);
			dynImage.drawImage(64, 64, img);
			
			// schalte das Main-Skrip aus
			Game.getIni().set("Game", "Main-Script", null);
			Game.newGame();
//			ValueDisplay vd = new ValueDisplay(20, 10, 0, "Wert ");
			
			Sprite sprite = new Sprite(null)
			{
				@Override
				public void refresh()
				{
					setImage(dynImage);
					image.setRectangle(128, 0, 128, 128);
				}
			};
			sprite.useGenList = false;
//			Matrix m0 = new Matrix.Translation(Matrix.MODEL, 300, 200);
//			Matrix m1 = new Matrix.Rotation(Matrix.MODEL, 0);
//			Matrix m2 = new Matrix.Translation(Matrix.MODEL, -120, -120);
//			Matrix m3 = new Matrix.Scale(Matrix.MODEL, 1, 1);
//			Matrix m4 = new Matrix.Translation(Matrix.TEXTURE, 100, 100);
//			TransformSequence seq = new TransformSequence(m0, m1, m2, m3, m4);
//			sprite.setTransformSequence(seq);
			sprite.setX(240);
			sprite.setY(120);
			
			sprite.setOX(128);
			sprite.setOY(128);
			
			float a = 0;
			while(!Graphics.isCloseRequested())
			{
				Graphics.update();
				a -= 0.2;
				sprite.setAngle(a);
				
				Input.update();
				
				if (Input.isRepeated(Keyboard.KEY_SPACE))
				{
					Game.setVar(0, Game.getVar(0) + 1);
//					vd.update();
				}
//				sprite.zoomX += 0.01f;
//				image.setRectangle(60-a/2, 60-a/2, a, a);
//				m1.angle-=1;
//				m0.x+=0.2f;
//				m0.y+=0.2f;
//				m2.x-=0.05f;
//				m2.y-=0.05f;
//				m3.x+=0.002;
//				m3.y+=0.002;
//				m4.x+=0.005;
//				sprite.setImage(image);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
//			Logger.log(GraphicTest.class, Logger.ERROR, e);
		}
	}
	
	private static Texture loadTextureAlternativ(String filename) throws IOException
	{
		BufferedImage image = ImageIO.read(new FileInputStream(filename));

		int srcWidth = image.getWidth();
		int srcHeight = image.getHeight();
		int width = fold2(srcWidth);
		int height = fold2(srcHeight);

		final AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -height);

		final AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		image = op.filter(image, null);

		final int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
		IntBuffer intBuffer = BufferUtils.createIntBuffer(pixels.length);
		intBuffer.put(pixels);
		intBuffer.rewind();
		
      	int id = glGenTextures();
 
        glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        
		Texture tex = new Texture(GL_TEXTURE_2D, id, srcWidth, srcHeight);
		if (width != srcWidth || height != srcHeight)
		{
			GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (ByteBuffer) null);
		}

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, srcWidth, srcHeight, 0, GL12.GL_BGRA,
				GL12.GL_UNSIGNED_INT_8_8_8_8_REV, intBuffer);

		glBindTexture(GL_TEXTURE_2D, 0);

		return tex;
	}
	
	private static int fold2(int i)
	{
		return 1 << (32-Integer.numberOfLeadingZeros(i-1));
	}
}
