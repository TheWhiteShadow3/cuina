package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.util.Vector;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

/**
 * Statische Library für OpenGl-Operationen.
 */
public class GraphicUtil
{
	private static boolean d3d = true;
	private static boolean light;
	
	/** 
	 * Ein ein Int großer IntBuffer als Referenzübergabe für Textur- und FBO-IDs.
	 * Dieses Objekt darf niemals über eine Methode hinaus benutzt werden,
	 * da sich der Inhalt laufend ändern kann.
	 */
	public static final IntBuffer TEMP_INT_BUFFER = BufferUtils.createIntBuffer(1);
	
	private GraphicUtil() {}
	
	// intern genutzte swap-Objekt
	static final float[] array4 = new float[4];

    public static FloatBuffer vectorToBuffer(Vector v)
    {
    	return vectorToBuffer(v, 0);
    }
	
    public static FloatBuffer vectorToBuffer(Vector v, float w)
    {
		array4[0] = v.x;
		array4[1] = v.y;
		array4[2] = v.z;
		array4[3] = w;
    	FloatBuffer fb = BufferUtils.createFloatBuffer(array4.length);
    	fb.put(array4).flip();
    	return fb;
    }
	
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
//	        glPopMatrix();
	        glLoadIdentity();
	        GLU.gluPerspective(40f, Graphics.getAspectRatio(), 1f, 1000f);
//			if (Graphics.camera != null)
//			{
//				Camera cam = Graphics.camera;
//				GLU.gluLookAt(cam.fromX, cam.fromY, cam.fromZ,
//							  cam.toX, cam.toY, cam.toZ,
//							  cam.upX, cam.upY, cam.upZ);
//			}
			
	        GLCache.setMatrix(GL_MODELVIEW);
//	        glPopMatrix();
	        glLoadIdentity();
	        // turn Depth Testing back on
	        glEnable(GL_DEPTH_TEST);
//	        if (light)
//	        	glEnable(GL_LIGHTING);
	        
	        // turn Depth Testing off and remove invisible sides
//	        glDisable(GL_DEPTH_TEST);
//	        glCullFace(GL_BACK);
	        
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
//	        glPushMatrix();                   // preserve perspective view
	        glLoadIdentity();                 // clear the perspective matrix
	        glOrtho(                          // turn on 2D mode
	        		////viewportX,viewportX+viewportW,    // left, right
	        		////viewportY,viewportY+viewportH,    // bottom, top    !!!
	        		0, Graphics.getWidth(),    // left, right
	        		Graphics.getHeight(), 0,    // bottom, top
	        		-500,500);                        // Zfar, Znear
	        // clear the modelview matrix
	        GLCache.setMatrix(GL_MODELVIEW);
//	        glPushMatrix();				   // Preserve the Modelview Matrix
	        glLoadIdentity();				   // clear the Modelview Matrix
			// disable depth test so further drawing will go over the current scene
			glDisable(GL_DEPTH_TEST);
//			glDisable(GL_LIGHTING);
//			glDisable(GL_POLYGON_SMOOTH);
		}
	}
	
	private static float[] toArray(Vector v, float w)
	{
		array4[0] = v.x;
		array4[1] = v.y;
		array4[2] = v.z;
		array4[3] = w;
		return array4;
	}
    
    public static void setLightPosition(int GLLightHandle, Vector pos)
    {
    	FloatBuffer ltPosition 	= arrayToBuffer(toArray(pos, 1));
    	glLight(GLLightHandle, GL_POSITION, ltPosition);
    }
    
    public static FloatBuffer arrayToBuffer(float[] floatarray)
    {
    	FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    	fb.put(floatarray).flip();
    	return fb;
    }
	
    public static void drawCube(float size, Image image)
    {
    	image.bind();
    	
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
    
    public static void drawSphere(int facets)
    {
        Sphere s = new Sphere();            // an LWJGL class
        s.setOrientation(GLU.GLU_OUTSIDE);  // normals point outwards
        s.setTextureFlag(true);             // generate texture coords
//        GL11.glPushMatrix();
//        {
//	        GL11.glRotatef(-90f, 1,0,0);    // rotate the sphere to align the axis vertically
	        s.draw(1, facets, facets);              // run GL commands to draw sphere
//        }
//        GL11.glPopMatrix();
    }
}
