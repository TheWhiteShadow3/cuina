package cuina.graphics;

import cuina.util.Vector;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * Util-Klasse für alles, was man so braucht und die eingebundenen Util-Klassen nicht haben.
 * @author TheWhiteShadow
 */
public class CuinaGLUtil
{
	/** 
	 * Ein ein Int großer IntBuffer als Referenzübergabe für Textur- und FBO-IDs.
	 * Dieses Objekt darf niemals über eine Methode hinaus benutzt werden,
	 * da sich der Inhalt laufend ändern kann.
	 */
	public static final IntBuffer TEMP_INT_BUFFER = BufferUtils.createIntBuffer(1);
	
	private CuinaGLUtil() {}
	
	// intern genutzte swap-Objekte
	private static final float[] array4 = new float[4];

	
    public static FloatBuffer VectorToBuffer(Vector v)
    {
    	return VectorToBuffer(v, 0);
    }
	
    public static FloatBuffer VectorToBuffer(Vector v, float w)
    {
		array4[0] = v.x;
		array4[1] = v.y;
		array4[2] = v.z;
		array4[3] = w;
    	FloatBuffer fb = BufferUtils.createFloatBuffer(array4.length);
    	fb.put(array4).flip();
    	return fb;
    }
}
