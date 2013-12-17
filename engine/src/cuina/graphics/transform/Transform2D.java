package cuina.graphics.transform;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import cuina.graphics.GLCache;
import cuina.graphics.Image;

import java.awt.Rectangle;

public class Transform2D implements Transformation
{
	/** X-Position vom Image. */
	public float x;
	/** Y-Position vom Image. */
	public float y;
	
	/** Drehwinkel des Images. */
	public float angle;
	
	/** Origin-X-Position vom Image. */
	public float ox;
	/** Origin-Y-Position vom Image. */
	public float oy;
	
	public float sx = 1;
	public float sy = 1;
	
	public float texX;
	public float texY;
	public float texAngle;
	public float texSX = 1;
	public float texSY = 1;

//	// Die Port-Vektoren
//	/** Obere linke Ecke der Zeichenfläche. */
//	public float vecX1;
//	/** Obere rechte Ecke der Zeichenfläche. */
//	public float vecY1;
//	/** Untere linke Ecke der Zeichenfläche. */
//	public float vecX2;
//	/** Untere rechte Ecke der Zeichenfläche. */
//	public float vecY2;
	
//	// Die View-Vektoren
//	/** Obere linke Ecke der Textur. */
//	public float texX1;
//	/** Obere rechte Ecke der Textur. */
//	public float texY1;
//	/** Untere linke Ecke der Textur. */
//	public float texX2 = 1;
//	/** Untere rechte Ecke der Textur. */
//	public float texY2 = 1;
	
	
	public void clear()
	{
		clearModelTransformation();
		clearTexturTransformation();
	}
	
	public void clearModelTransformation()
	{
		clearPosition();
		clearRotation();
		clearScale();
	}
	
	public void clearTexturTransformation()
	{
		texX = 0;
		texY = 0;
		texAngle = 0;
		texSX = 1;
		texSY = 1;
	}
	
	public void clearPosition()
	{
		x = 0;
		y = 0;
		ox = 0;
		oy = 0;
	}
	
	public void clearRotation()
	{
		angle = 0;
	}
	
	public void clearScale()
	{
		this.sx = 1;
		this.sy = 1;
	}
	
	public void setPosition(float x, float y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void setOrigion(float ox, float oy)
	{
		this.ox = ox;
		this.oy = oy;
	}
	
	public void setRotation(float angle)
	{
		this.angle = angle;
	}
	
	public void setScale(float scaleX, float scaleY)
	{
		this.sx = scaleX;
		this.sy = scaleY;
	}
	
	public void setTexturPosition(float x, float y)
	{
		this.texX = x;
		this.texY = y;
	}
	
	public void setTexturAngle(float angle)
	{
		this.texAngle = angle;
	}
	
//	public void addPortOffset(int ox, int oy)
//	{
//		vecX1 -= ox;
//		vecY1 -= oy;
//		vecX2 -= ox;
//		vecY2 -= oy;
//	}
	
	/** Legt einen Port mit dem angegebnen Bildausschnitt fest. */
	public void setPort(Rectangle rect)
	{
		setPort(rect.x, rect.y, rect.width, rect.height);
	}
	
	/** Legt einen Port mit dem angegebnen Bildausschnitt fest. */
	public void setPort(int x, int y, float scaleX, float scaleY)
	{
		this.ox = x;
		this.oy = y;
		this.sx = scaleX;
		this.sy = scaleY;
//		vecX2 = x + width;
//		vecY2 = y + height;
	}
	
	/** Legt einen Port in dem angegebnen Größe fest. */
	public void setPort(int width, int height)
	{
		setPort(0, 0, width, height);
	}
	
	/** Legt einen Port für ein Image fest. */
	public void setPort(Image image)
	{
		setPort(0, 0, image.getWidth(), image.getHeight());
	}

	/** Legt einen View über die ganze Textur fest. */
	public void setDefaultView()
	{
		texX = 0;
		texY = 0;
		texAngle = 0;
		texSX = 1;
		texSY = 1;
	}
	
	/**
	 * Legt einen View in der angegebenen Größe fest.
	 */
	public void setView(float x, float y)
	{
		texX = x;
		texY = y;
		texSX = 1;
		texSY = 1;
	}
	
	/**
	 * Legt einen View in der angegebenen Größe fest.
	 */
	public void setView(float x, float y, float scaleX, float scaleY)
	{
		this.texX = x;
		this.texY = y;
		this.texSX = scaleX;
		this.texSY = scaleY;
	}
	
	/**
	 * Steckt das Image.
	 * @param scaleX
	 * @param scaleY
	 */
	public void expand(float scaleX, float scaleY, float texScaleX, float texScaleY)
	{
		this.sx = scaleX;
		this.sy = scaleY;
		this.texSX = scaleX / texScaleX;
		this.texSY = scaleY / texScaleY;
	}
	
	/** Legt einen View für ein Image fest. */
	public void setView(Image image, float zoomX, float zoomY)
	{
		this.texX = image.getX() / (float)image.getTexture().getWidth();
		this.texY = image.getY() / (float)image.getTexture().getHeight();
		this.texSX = zoomX;
		this.texSY = zoomX;
		this.sx = zoomX;
		this.sy = zoomY;

//		setView(image.getTexture(), image.getRect());
//		Rectangle rect = image.getRect();
//		texX1 = rect.x / (float) image.getWidth();
//		texY1 = rect.y / (float) image.getHeight();
//		texX2 = (rect.x +
//		texY2 = (rect.y + rect.height) / (float) image.getHeight();
	}
//	
//	/** Legt einen View für eine Textur fest. */
//	public void setView(Texture texture)
//	{
//		texX = 0;
//		texY = 0;
//		texX2 = texture.getImageWidth()  / (float) texture.getTextureWidth();
//		texY2 = texture.getImageHeight() / (float) texture.getTextureHeight();
//	}
	
//	public void setView(Texture texture, Rectangle rect)
//	{
//		setView(texture, rect.x, rect.y, rect.width, rect.height);
//	}
//	
//	public void setView(Texture texture, int x, int y, int width, int height)
//	{
//		texX1 = x / (float) texture.getTextureWidth();
//		texY1 = y / (float) texture.getTextureHeight();
//		texX2 = (x + width)  / (float) texture.getTextureWidth();
//		texY2 = (y + height) / (float) texture.getTextureHeight();
//	}
	float funnyCounter = 0;
	
	@Override
	public void pushTransformation()
	{
		GLCache.setMatrix(GL_MODELVIEW);
		glPushMatrix();
			
		if (x != 0 || y != 0)	glTranslatef(x, y, 0);
		if (angle != 0) 		glRotatef(angle, 0, 0, 1);
		if (ox != 0 || oy != 0) glTranslatef(-ox, -oy, 0);
		if (sx != 1 || sy != 1) glScalef(sx, sy, 1);
		
		GLCache.setMatrix(GL_TEXTURE);
		glPushMatrix();
		
		if (texX != 0 || texY != 0) 	glTranslatef(texX, texY, 0);
		if (texAngle != 0) 				glRotatef(texAngle, 0, 0, 1);
		if (texSX != 1 || texSY != 1) 	glScalef(texSX, texSY, 1);
	}
	
	@Override
	public void popTransformation()
	{
		GLCache.setMatrix(GL_MODELVIEW);
		glPopMatrix();
		
		GLCache.setMatrix(GL_TEXTURE);
		glPopMatrix();
	}
}
