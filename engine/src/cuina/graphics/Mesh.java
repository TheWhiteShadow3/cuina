package cuina.graphics;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import cuina.util.LoadingException;

import java.nio.FloatBuffer;

import jnr.ffi.util.BufferUtil;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class Mesh
{
//	private ArrayList<Vector> vectors = new ArrayList<Vector>(32);
//	private ArrayList<Vector> vectors = new ArrayList<Vector>(32);
	private float size;
	
	public Mesh()
	{
		this(1.0f);
	}
	
	public Mesh(float size)
	{
		this.size = size;
	}
	
	public void render(Image image)
	{
		renderCube(image);
//		renderPlane();
	}
	
	private void renderCube(Image image)
	{
		float w = image.getPercentageWidth();
		float h = image.getPercentageHeight();

		GL11.glBegin(GL11.GL_QUADS);
        // Oben
        GL11.glNormal3f( 0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(w, 0.0f);		GL11.glVertex3f(-size, -size,  size);	// Bottom Left
        GL11.glTexCoord2f(0.0f, 0.0f);	GL11.glVertex3f( size, -size,  size);	// Bottom Right
        GL11.glTexCoord2f(0.0f, h);		GL11.glVertex3f( size,  size,  size);	// Top Right
        GL11.glTexCoord2f(w, h);		GL11.glVertex3f(-size,  size,  size);	// Top Left
        // Unten
        GL11.glNormal3f( 0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(w, 0.0f);		GL11.glVertex3f(-size, -size, -size);	// Bottom Right
        GL11.glTexCoord2f(w, h);		GL11.glVertex3f(-size,  size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size,  size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size, -size);	// Bottom Left
        // Vorne
        GL11.glNormal3f( 0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f(-size,  size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f(-size,  size,  size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f( size,  size,  size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f( size,  size, -size);	// Top Right
        // Hinten
        GL11.glNormal3f( 0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f(-size, -size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size, -size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size,  size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f(-size, -size,  size);	// Bottom Right
        // Links
        GL11.glNormal3f( 1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f( size, -size, -size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f( size,  size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size,  size,  size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size,  size);	// Bottom Left
        // Rechts
        GL11.glNormal3f( -1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f(-size, -size, -size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f(-size, -size,  size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f(-size,  size,  size);	// Top Right
        GL11.glTexCoord2f(0.0f, h);		GL11.glVertex3f(-size,  size, -size);	// Top Left
		GL11.glEnd();

//		FloatBuffer buffer = BufferUtils.createFloatBuffer(6*4*3);
//		fillTangetArray(buffer, new float[] {-1, 0, 0});
//		fillTangetArray(buffer, new float[] { 1, 0, 0});
//		fillTangetArray(buffer, new float[] {-1, 0, 0});
//		fillTangetArray(buffer, new float[] { 1, 0, 0});
//		fillTangetArray(buffer, new float[] {0, -1, 0});
//		fillTangetArray(buffer, new float[] {0,  1, 0});
//		buffer.rewind();
//		
//		GL20.glVertexAttribPointer(4, 6*4*3, false, 0, buffer);
        
//        GL11.glLoadIdentity();
//        GL11.glTranslatef(12, 8, 8);
//        D3D.drawSphere(16);
  }
	
	private void fillTangetArray(FloatBuffer buffer, float[] xyz)
	{
		buffer.put(xyz);
		buffer.put(xyz);
		buffer.put(xyz);
		buffer.put(xyz);
	}
	
	private Image image1;
	private Image image2;

	private void renderPlane()
	{
		if (image1 == null)
		{
			try
			{
				image1 = Images.createImage("backgrounds/Truemmer2.png");
				image2 = Images.createImage("backgrounds/Truemmer2_Normal.png");
			}
			catch (LoadingException e)
			{
				e.printStackTrace();
			}
		}
		
//		float w = image.getPercentageWidth();
//		float h = image.getPercentageHeight();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		glBindTexture(image1.getTexture().target, image1.getTexture().textureID);
//		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
		
		
//		GL13.glActiveTexture(GL13.GL_TEXTURE1);
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
//		glBindTexture(image2.getTexture().target, image2.getTexture().textureID);
//		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

// glMultiTexCoord2fARB(GL_TEXTURE0_ARB, 0.0, 1.0);

		GL11.glBegin(GL11.GL_QUADS);
		
        GL11.glNormal3f( 0.0f, 0.0f, 1.0f);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 0.0f, 0.0f);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 0.0f, 0.0f);	GL11.glVertex3f(-size, -size,  size);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 1, 0.0f);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 1, 0.0f);		GL11.glVertex3f( size, -size,  size);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 1, 1);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 1, 1);			GL11.glVertex3f( size,  size,  size);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE0, 0.0f, 1);
        GL13.glMultiTexCoord2f(GL13.GL_TEXTURE1, 0.0f, 1);		GL11.glVertex3f(-size,  size,  size);

        GL11.glEnd();
        GL11.glDisable(GL_TEXTURE_2D);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
//		GL11.glEnable(GL11.GL_TEXTURE_2D);
//		glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
	}
}
