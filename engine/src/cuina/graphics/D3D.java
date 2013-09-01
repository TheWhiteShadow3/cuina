package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cuina.util.Vector;

/**
 * Statische Library für OpenGl-Operationen, die noch nicht ie eine OO-Struktur eingebettet sind.
 * Darunter zählen Lichter, Renderings
 */
public class D3D
{
	private static boolean d3d = true;
	private static boolean light;
	
	private D3D() {}
	
	/**
	 * Ermöglicht das Wechseln zwischen 2D und 3D-Modus.
	 * Wird von Graphics und momentan nur von 3D-Elementen benutzt 
	 * @param value 3D an/aus
	 */
	public static void set3DView(boolean value)
	{
		if (value == d3d) return;
		d3d = value;
		
		if (d3d)
		{
	        // restore the original positions and views
			GLCache.setMatrix(GL_PROJECTION);
	        glPopMatrix();
	        GLCache.setMatrix(GL_MODELVIEW);
	        glPopMatrix();
	        // turn Depth Testing back on
	        glEnable(GL_DEPTH_TEST);
	        if (light)
	        	glEnable(GL_LIGHTING);
	        
	        // turn Depth Testing off and remove invisible sides
//	        glDisable(GL_DEPTH_TEST);
//	        glCullFace(GL_BACK);
//	        glEnable(GL_CULL_FACE);
	        
	        // set new clear color and blend functions which works with polygon smoothing
//	        glClearColor( 0.0f, 0.0f, 0.0f, 0.0f );
//	        glBlendFunc(GL_SRC_ALPHA_SATURATE, GL_ONE);
//	        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
	        
	        // activate polygon smoothing (anti aliasing)
//	        glDisable(GL_BLEND);
//	        glEnable(GL_POLYGON_SMOOTH);
	        
		}
		else
		{
			// prepare projection matrix to render in 2D
			GLCache.setMatrix(GL_PROJECTION);
	        glPushMatrix();                   // preserve perspective view
	        glLoadIdentity();                 // clear the perspective matrix
	        glOrtho(                          // turn on 2D mode
	        		////viewportX,viewportX+viewportW,    // left, right
	        		////viewportY,viewportY+viewportH,    // bottom, top    !!!
	        		0, Graphics.getWidth(),    // left, right
	        		Graphics.getHeight(), 0,    // bottom, top
	        		-500,500);                        // Zfar, Znear
	        // clear the modelview matrix
	        GLCache.setMatrix(GL_MODELVIEW);
	        glPushMatrix();				   // Preserve the Modelview Matrix
	        glLoadIdentity();				   // clear the Modelview Matrix
			// disable depth test so further drawing will go over the current scene
			glDisable(GL_DEPTH_TEST);
			glDisable(GL_LIGHTING);
//			glDisable(GL_POLYGON_SMOOTH);
		}
	}
	
//	private static final float[] array3 = new float[3];
	private static final float[] array4 = new float[4];
	
	private static float[] toArray(Color color)
	{
		return color.getRGBComponents(array4);
	}
	
	
	private static float[] toArray(Vector v)
	{
		array4[0] = v.x;
		array4[1] = v.y;
		array4[2] = v.z;
		return array4;
	}
	
    public static void setLight( int GLLightHandle, Color diffuse, Color ambient, Color specular, Vector pos)
    {
        FloatBuffer ltDiffuse 	= arrayToBuffer(toArray(diffuse));
        FloatBuffer ltAmbient 	= arrayToBuffer(toArray(ambient));
        FloatBuffer ltSpecular 	= arrayToBuffer(toArray(specular));
        toArray(pos);
        array4[3] = 1;
        FloatBuffer ltPosition 	= arrayToBuffer(array4);
        glLight(GLLightHandle, GL_DIFFUSE, ltDiffuse);   // color of the direct illumination
        glLight(GLLightHandle, GL_SPECULAR, ltSpecular); // color of the highlight
        glLight(GLLightHandle, GL_AMBIENT, ltAmbient);   // color of the reflected light
        glLight(GLLightHandle, GL_POSITION, ltPosition);
        glEnable(GLLightHandle);	// Enable the light (GL_LIGHT1 - 7)
        
        light = true;
//        glLightf(GLLightHandle, GL_QUADRATIC_ATTENUATION, .005F);    // how light beam drops off
    }
    
    public static FloatBuffer arrayToBuffer(float[] floatarray)
    {
    	FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    	fb.put(floatarray).flip();
    	return fb;
    }
	
    public static void drawCube(float size, Image image)
    {
    	image.getTexture().bind();
    	
		float w = image.getWidth() / (float) image.getTexture().getWidth();
		float h = image.getHeight() / (float) image.getTexture().getHeight();
    	
        glBegin(GL_QUADS);
        // Front Face
        glNormal3f( 0.0f, 0.0f, size);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Left
        glTexCoord2f(w, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Right
        glTexCoord2f(w, h); glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Right
        glTexCoord2f(0.0f, h); glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Left
        // Back Face
        glNormal3f( 0.0f, 0.0f, -size);
        glTexCoord2f(w, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Right
        glTexCoord2f(w, h); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Right
        glTexCoord2f(0.0f, h); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Left
        glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Left
        // Top Face
        glNormal3f( 0.0f, size, 0.0f);
        glTexCoord2f(0.0f, h); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f,  1.0f,  1.0f);	// Bottom Left
        glTexCoord2f(w, 0.0f); glVertex3f( 1.0f,  1.0f,  1.0f);	// Bottom Right
        glTexCoord2f(w, h); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        // Bottom Face
        glNormal3f( 0.0f, -size, 0.0f);
        glTexCoord2f(w, h); glVertex3f(-1.0f, -1.0f, -1.0f);	// Top Right
        glTexCoord2f(0.0f, h); glVertex3f( 1.0f, -1.0f, -1.0f);	// Top Left
        glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        glTexCoord2f(w, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        // Right face
        glNormal3f( size, 0.0f, 0.0f);
        glTexCoord2f(w, 0.0f); glVertex3f( 1.0f, -1.0f, -1.0f);	// Bottom Right
        glTexCoord2f(w, h); glVertex3f( 1.0f,  1.0f, -1.0f);	// Top Right
        glTexCoord2f(0.0f, h); glVertex3f( 1.0f,  1.0f,  1.0f);	// Top Left
        glTexCoord2f(0.0f, 0.0f); glVertex3f( 1.0f, -1.0f,  1.0f);	// Bottom Left
        // Left Face
        glNormal3f( -size, 0.0f, 0.0f);
        glTexCoord2f(0.0f, 0.0f); glVertex3f(-1.0f, -1.0f, -1.0f);	// Bottom Left
        glTexCoord2f(w, 0.0f); glVertex3f(-1.0f, -1.0f,  1.0f);	// Bottom Right
        glTexCoord2f(w, h); glVertex3f(-1.0f,  1.0f,  1.0f);	// Top Right
        glTexCoord2f(0.0f, h); glVertex3f(-1.0f,  1.0f, -1.0f);	// Top Left
        glEnd();
    }
}
