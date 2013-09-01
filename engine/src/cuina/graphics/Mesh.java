package cuina.graphics;

import org.lwjgl.opengl.GL11;


public class Mesh
{
//	private ArrayList<Vector> vectors = new ArrayList<Vector>(32);
//	private ArrayList<Vector> vectors = new ArrayList<Vector>(32);
	private float size;
	private int modelID;
	private Image image;
	
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
		this.image = image;
//		float w = texture.getImageWidth() / (float) texture.getTexturWidth();
//		float h = texture.getImageHeight() / (float) texture.getTexturHeight();
		
		//TODO: Da ein Würfel vielleicht nicht immer gewollt ist, sollte hier eine echte Implementation erfolgen.
		GL11.glNewList(modelID, GL11.GL_COMPILE);
		renderCube(image);
		GL11.glEndList();
		modelID = GL11.glGenLists(1);
	}
	
	protected void draw()
	{
		if (image == null || modelID == 0) return;
		image.getTexture().bind();
		GL11.glCallList(modelID);
	}
	
	private void renderCube(Image image)
	{
		float w = image.getWidth() / (float) image.getTexture().getWidth();
		float h = image.getHeight() / (float) image.getTexture().getHeight();
		
        GL11.glBegin(GL11.GL_QUADS);
        // Front Face
        GL11.glNormal3f( 0.0f, 0.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);	GL11.glVertex3f(-size, -size, size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f);		GL11.glVertex3f( size, -size,  size);	// Bottom Right
        GL11.glTexCoord2f(w, h);		GL11.glVertex3f( size,  size, size);	// Top Right
        GL11.glTexCoord2f(0.0f, h);		GL11.glVertex3f(-size,  size,  size);	// Top Left
        // Back Face
        GL11.glNormal3f( 0.0f, 0.0f, -1.0f);
        GL11.glTexCoord2f(w, 0.0f);		GL11.glVertex3f(-size, -size, -size);	// Bottom Right
        GL11.glTexCoord2f(w, h);		GL11.glVertex3f(-size,  size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size,  size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size, -size);	// Bottom Left
        // Top Face
        GL11.glNormal3f( 0.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f(-size,  size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f(-size,  size,  size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f( size,  size,  size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f( size,  size, -size);	// Top Right
        // Bottom Face
        GL11.glNormal3f( 0.0f, -1.0f, 0.0f);
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f(-size, -size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size, -size, -size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size,  size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f(-size, -size,  size);	// Bottom Right
        // Right face
        GL11.glNormal3f( 1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f( size, -size, -size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f( size,  size, -size);	// Top Right
        GL11.glTexCoord2f(0.0f, h); 	GL11.glVertex3f( size,  size,  size);	// Top Left
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f( size, -size,  size);	// Bottom Left
        // Left Face
        GL11.glNormal3f( -1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 0.0f); 	GL11.glVertex3f(-size, -size, -size);	// Bottom Left
        GL11.glTexCoord2f(w, 0.0f); 	GL11.glVertex3f(-size, -size,  size);	// Bottom Right
        GL11.glTexCoord2f(w, h); 		GL11.glVertex3f(-size,  size,  size);	// Top Right
        GL11.glTexCoord2f(0.0f, h);		GL11.glVertex3f(-size,  size, -size);	// Top Left
        GL11.glEnd();
	}
}
