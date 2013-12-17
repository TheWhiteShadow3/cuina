package cuina.graphics.transform;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import cuina.graphics.GLCache;
import cuina.util.Vector;

public class Transform3D implements Transformation
{
	public final Vector pos		= new Vector();
	public final Vector origin	= new Vector();
	public final Vector scale	= new Vector(1, 1, 1);
	public final Vector angle	= new Vector();
	
	public float texX;
	public float texY;
	public float texAngle;
	public float texSX = 1;
	public float texSY = 1;
	
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
		pos.set(0, 0, 0);
		origin.set(0, 0, 0);
	}
	
	public void clearRotation()
	{
		angle.set(0, 0, 0);
	}
	
	public void clearScale()
	{
		this.scale.set(1, 1, 1);
	}
	
	public void setPosition(Vector v)
	{
		this.pos.set(v);
	}
	
	public void setOrigion(Vector v)
	{
		this.origin.set(v);
	}
	
	public void setRotation(Vector v)
	{
		this.angle.set(v);
	}
	
	public void setScale(Vector v)
	{
		this.scale.set(v);
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
	
//	/** Legt einen Port mit dem angegebnen Bildausschnitt fest. */
//	public void setPort(Rectangle rect)
//	{
//		setPort(rect.x, rect.y, rect.width, rect.height);
//	}
//	
//	/** Legt einen Port mit dem angegebnen Bildausschnitt fest. */
//	public void setPort(int x, int y, float scaleX, float scaleY)
//	{
//		this.ox = x;
//		this.oy = y;
//		this.sx = scaleX;
//		this.sy = scaleY;
////		vecX2 = x + width;
////		vecY2 = y + height;
//	}
//	
//	/** Legt einen Port in dem angegebnen Größe fest. */
//	public void setPort(int width, int height)
//	{
//		setPort(0, 0, width, height);
//	}
//	
//	/** Legt einen Port für ein Image fest. */
//	public void setPort(Image image)
//	{
//		setPort(0, 0, image.getWidth(), image.getHeight());
//	}

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
	
//	/**
//	 * Steckt das Image.
//	 * @param scaleX
//	 * @param scaleY
//	 */
//	public void expand(float scaleX, float scaleY, float texScaleX, float texScaleY)
//	{
//		this.sx = scaleX;
//		this.sy = scaleY;
//		this.texSX = scaleX / texScaleX;
//		this.texSY = scaleY / texScaleY;
//	}
	
//	/** Legt einen View für ein Image fest. */
//	public void setView(Image image, float zoomX, float zoomY)
//	{
//		this.texX = image.getX() / (float)image.getTexture().getWidth();
//		this.texY = image.getY() / (float)image.getTexture().getHeight();
//		this.texSX = zoomX;
//		this.texSY = zoomX;
//		this.sx = zoomX;
//		this.sy = zoomY;

//		setView(image.getTexture(), image.getRect());
//		Rectangle rect = image.getRect();
//		texX1 = rect.x / (float) image.getWidth();
//		texY1 = rect.y / (float) image.getHeight();
//		texX2 = (rect.x +
//		texY2 = (rect.y + rect.height) / (float) image.getHeight();
//	}
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
			
		if (pos.x != 1 || pos.y != 1 || pos.z != 1)				glTranslatef(pos.x, pos.y, pos.z);
		if (angle.x != 0) 		glRotatef(angle.x, 1, 0, 0);
		if (angle.y != 0) 		glRotatef(angle.y, 0, 1, 0);
		if (angle.z != 0) 		glRotatef(angle.z, 0, 0, 1);
		if (origin.x != 1 || origin.y != 1 || origin.z != 1)	glTranslatef(origin.x, origin.y, origin.z);
		if (scale.x != 1 || scale.y != 1 || scale.z != 1)		glScalef(scale.x, scale.y, scale.z);
		
		GLCache.setMatrix(GL_TEXTURE);
		glPushMatrix();
		
		if (texX != 0 || texY != 0) 	glTranslatef(texX, texY, 0);
		if (texAngle != 0) 				glRotatef(texAngle, 0, 0, 1);
		if (texSX != 1 || texSY != 1) 	glScalef(texSX, texSY, 1);
	}
	
	@Override
	public void popTransformation()
	{
		GLCache.setMatrix(GL_TEXTURE);
		glPopMatrix();
		
		GLCache.setMatrix(GL_MODELVIEW);
		glPopMatrix();
	}
}