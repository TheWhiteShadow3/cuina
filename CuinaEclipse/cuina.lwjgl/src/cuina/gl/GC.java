package cuina.gl;

import static org.lwjgl.opengl.GL11.*;

import org.eclipse.swt.graphics.Rectangle;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * Zentrale Klasse für OpenGL-Zeichenoperationen in SWT.
 * @author TheWhiteShadow
 */
public class GC
{
	private boolean useTextures;
	private Object owner;
	private Color color = new Color(Color.WHITE);
	private Rectangle clip;
	
	public GC(Object owner, Rectangle clip)
	{
		this.owner = owner;
		this.clip = clip;
		setColor(color);
		
		useTextures = glIsEnabled(GL_TEXTURE_2D);
	}
	
	public void setAlpha(int alpha)
	{
		color.setAlpha(alpha);
		setColor(color);
	}

	public void setColor(int red, int green, int blue)
	{
		color.set(red, green, blue);
		setColor(color);
	}
	
	public void setColor(int red, int green, int blue, int alpha)
	{
		color.set(red, green, blue, alpha);
		setColor(color);
	}
	
	/**
	 * 
	 * @param color
	 */
	public void setColor(ReadableColor color)
	{
		this.color.setColor(color);
		glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}
	
	public ReadableColor getColor()
	{
		return color;
	}
	
	/**
	 * Gibt den OpenGL Kontext Besitzer zurück.
	 * @return OpenGL Kontext Besitzer.
	 */
	public Object getContextOwner()
	{
		return owner;
	}
	
	public Rectangle getClip()
	{
		return clip;
	}
	
	public void drawImage(final Image image, int x, int y)
	{
		if (image.isDisposed()) throw new RuntimeException("image is disposed!");
		
		Texture tex = image.getTexture();
		setUseTextures(true);
		tex.bind();

		float x1 = x;
		float y1 = y;
		float x2 = x + tex.getImageWidth();
		float y2 = y + tex.getImageHeight();
		
		float textWidth = tex.getImageWidth()  / (float) tex.getTextureWidth();
		float texHeight = tex.getImageHeight() / (float) tex.getTextureHeight();
		
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex2f(x1, y1);

			glTexCoord2f(0, texHeight);
			glVertex2f(x1, y2);

			glTexCoord2f(textWidth, texHeight);
			glVertex2f(x2, y2);

			glTexCoord2f(textWidth, 0);
			glVertex2f(x2, y1);
		}
		glEnd();
	}
	
	public void drawImage(final Image image, 
			int srcX, int srcY, int srcWidth, int srcHeight,
			int destX, int destY, int destWidth, int destHeight)
	{
		if (image.isDisposed()) throw new RuntimeException("image is disposed!");
		
		Texture tex = image.getTexture();
		setUseTextures(true);
		tex.bind();

		float tx1 = srcX / (float) tex.getTextureWidth();
		float ty1 = srcY / (float) tex.getTextureHeight();
		float tx2 = tx1 + srcWidth / (float) tex.getTextureWidth();
		float ty2 = ty1 + srcHeight / (float) tex.getTextureHeight();

		float vx1 = destX;
		float vy1 = destY;
		float vx2 = vx1 + destWidth;
		float vy2 = vy1 + destHeight;
		
		glBegin(GL_QUADS);
		{
			glTexCoord2f(tx1, ty1);
			glVertex2f(vx1, vy1);

			glTexCoord2f(tx1, ty2);
			glVertex2f(vx1, vy2);

			glTexCoord2f(tx2, ty2);
			glVertex2f(vx2, vy2);

			glTexCoord2f(tx2, ty1);
			glVertex2f(vx2, vy1);
		}
		glEnd();
	}
	
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		setUseTextures(false);
        glBegin(GL_LINES);
        {
            glVertex2i(x1, y1);
            glVertex2i(x2, y2);
        }
        glEnd();
	}
	
	public void fillRectangle(int x, int y, int width, int height)
	{
		setUseTextures(false);
        glBegin(GL_QUADS);
        {
        	glVertex2i(x, y);
			glVertex2i(x + width, y);
			glVertex2i(x + width, y + height);
			glVertex2i(x, y + height);
        }
        glEnd();
	}
	
	public void drawRectangle(int x, int y, int width, int height)
	{
		setUseTextures(false);
        glBegin(GL_LINES);
        {
            glVertex2i(x, y);
            glVertex2i(x + width, y);
            
            glVertex2i(x + width, y);
            glVertex2i(x + width, y + height);
            
            glVertex2i(x + width, y + height-1);
            glVertex2i(x, y + height-1);
            
            glVertex2i(x+1, y + height);
            glVertex2i(x+1, y);
        }
        glEnd();
	}
	
	public void clearMatrix()
	{
		glLoadIdentity();
	}
	
	public void translate2D(float x, float y)
	{
		glTranslatef(x, y, 0);
	}
	
	public void translate(float x, float y, float z)
	{
		glTranslatef(x, y, z);
	}
	
	public void rotate2D(float angle)
	{
		glRotatef(angle, 0, 0, 1);
	}
	
	public void rotate(float angle, float x, float y, float z)
	{
		glRotatef(angle, x, y, z);
	}
	
	private void setUseTextures(boolean value)
	{
		if (value == useTextures) return;
		useTextures = value;
		
		if (useTextures)
			glEnable(GL_TEXTURE_2D);
		else
			glDisable(GL_TEXTURE_2D);
	}
}
