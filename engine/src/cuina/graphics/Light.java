package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import cuina.util.Vector;

import java.awt.Color;
import java.nio.FloatBuffer;

public class Light
{
	private static final int MAX_LIGHTS;
	
	private Color ambient;
	private Color diffuse;
	private Color specular;
	// float[], um die 4. Komponente zu speichern.
	private float[] position;
	
	static
	{
		MAX_LIGHTS = glGetInteger(GL_MAX_LIGHTS);
	}
	
	private static FloatBuffer colorToBuffer(Color color)
	{
		return GraphicUtil.arrayToBuffer(color.getRGBComponents(GraphicUtil.array4));
	}
	
	public Light(Color ambient, Color diffuse, Color specular, Vector pos, boolean directional)
	{
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.position = new float[] {pos.x, pos.y, pos.z, directional ? 1 : 0};
	}
	
	public void bind(int glLight)
	{
		if (glLight >= GL_LIGHT0 + MAX_LIGHTS) throw new IllegalArgumentException();
		
        FloatBuffer ltAmbient 	= colorToBuffer(ambient);
        FloatBuffer ltDiffuse 	= colorToBuffer(diffuse);
        FloatBuffer ltSpecular 	= colorToBuffer(specular);
        FloatBuffer ltPosition 	= GraphicUtil.arrayToBuffer(position);
        glLight(glLight, GL_AMBIENT, ltAmbient);
        glLight(glLight, GL_DIFFUSE, ltDiffuse);
        glLight(glLight, GL_SPECULAR, ltSpecular);
        glLight(glLight, GL_POSITION, ltPosition);
        glEnable(glLight);
	}
	
	public void unbind(int glLight)
	{
		glDisable(glLight);
	}
}
