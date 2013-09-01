/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.awt.Rectangle;

import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * Beschreibt ein vom GL-Kontext gemanagetes Bild und stellt Funktionen zur Verfügung
 * um dieses zu zeichnen und zu bearbeiten. Die Grundlage eines Images ist eine {@link Texture},
 * welches einmalig dem Image übergeben werden muss.
 * @author TheWhiteShadow
 * @version 1.1
 */
public class Image
{
	public static final Color COLOR_TRANSPARENT = new Color(0, 0, 0, 0);
	
	/** Benutzt den Blendmodus, der aktuell in Open-GL eingestellt ist. Sollte nicht benutzt werden. */
	public static final int COMPOSITE_NOCHANGE = 0;
	/** Normaler Blendmodus überlagert die Farben entsprechend dem Alphawert.
	 * <p>Alphafunktion: <code>src = 1-src</code></p> */
	public static final int COMPOSITE_NORMAL = 1;
	/** Additiver Blendmodus addiert die beiden Farbwerte entsprechend dem Alphawert.
	 * <p>Alphafunktion: </p> */
	public static final int COMPOSITE_ADD = 2;
	/** Subtraktiver Blendmodus subtrahiert den neuen Farbwert entsprechend dem Alphawert.
	 * <p>Alphafunktion: </p> */
	public static final int COMPOSITE_SUB = 3;
	/** multiplizierender Blendmodus multipliziert die beiden Farbwerte.
	 * <p>Alphafunktion: </p> */
	public static final int COMPOSITE_MUL = 4;
	/** Absoluter Blendmodus ersetzt den alten Farbwert unabhängig vom Alphawert.
	 * <p>Alphafunktion: <code>src = src</code></p> */
	public static final int COMPOSITE_ABSOLUT = 5;
	/** Überlagernder Blendmodus ersetzt ausschließlich den Farbwert.
	 * <p>Alphafunktion: <code>src = dst</code></p> */
	public static final int COMPOSITE_OVERLAY = 6;

	public static final Transform2D IMAGE_MATRIX = new Transform2D();
	
	/** The texture that stores the image for this sprite */
	private Texture texture;
//	int modelID;

//	BufferedImage rawImage;
	
//	 Koordinaten in der Textur
	
	private final Rectangle rect;
//	private Font font = Graphics.getDefaultFont();
	private Color color = new Color(Graphics.getDefaultColor());

//	private Rectangle2D.Float texRect = new Rectangle2D.Float(0, 0, 1, 1);
	
	private int blendMode = COMPOSITE_NORMAL;

//	public Image(String fileName) throws LoadingException
//	{
//		this.rawImage = ResourceManager.loadImage(fileName);
//		this.width = rawImage.getWidth();
//		this.height = rawImage.getHeight();
//		setRect(0, 0, rawImage.getWidth(), rawImage.getHeight());
//
//		loadTexture(fileName);
//	}
//
//	public Image(String fileName, Rectangle rect) throws LoadingException
//	{
//		this.rawImage = ResourceManager.loadImage(fileName);
//		this.width = rawImage.getWidth();
//		this.height = rawImage.getHeight();
//		setRect(rect);
//
//		loadTexture(fileName);
//	}
	
//	public Image(int width, int height) throws LoadingException
//	{
//		this.texture = TextureLoader.getInstance().getTexture(width, height);
//		texture.addUseCount();
//		this.x = 0;
//		this.y = 0;
//		this.width = texture.getSourceWidth();
//		this.height = texture.getSourceHeight();
//	}
//	
//	public Image(BufferedImage rawImage) throws LoadingException
//	{
//		this.rawImage = ResourceManager.loadImage(fileName);
//		this.texture = TextureLoader.getInstance().getTexture(rawImage, fileName);
//		texture.addUseCount();
//		this.x = 0;
//		this.y = 0;
//		this.width = texture.getSourceWidth();
//		this.height = texture.getSourceHeight();
//	}
//	
//	public Image(String fileName, int x, int y, int width, int height) throws LoadingException
//	{
//		this.rawImage = ResourceManager.loadImage(fileName);
//		this.texture = TextureLoader.getInstance().getTexture(rawImage, fileName);
//		texture.addUseCount();
//		this.x = x;
//		this.y = y;
//		this.width = width;
//		this.height = height;
//	}

	protected Image(Texture texture, int x, int y, int width, int height)
	{
		if (texture == null) throw new NullPointerException();
		
		this.texture = texture;
		texture.addUseCount();
		this.rect = new Rectangle(x, y, width, height);
	}
	
	protected Image(Texture texture)
	{
		this(texture, 0, 0, texture.getSourceWidth(), texture.getSourceHeight());
	}
	
//	public Image(BufferedImage rawImage)
//	{
//		this.rawImage = rawImage;
//		this.width = rawImage.getWidth();
//		this.height = rawImage.getHeight();
//		setRect(0, 0, rawImage.getWidth(), rawImage.getHeight());
//
//		loadTexture(null);
//	}

//	public Image(int width, int height)
//	{
//		this.rawImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//		this.width = rawImage.getWidth();
//		this.height = rawImage.getHeight();
//		setRect(0, 0, rawImage.getWidth(), rawImage.getHeight());
//	}

	// private Image() {}

	// /**
	// * Erstellt eine Kopie des Images mit dem selben Bild als Grundlage.
	// * @param image
	// * @return
	// */
	// public Image copy(Image image)
	// {
	// Image cloneImage = new Image();
	// cloneImage.texture = image.texture;
	// cloneImage.fileName = image.fileName;
	// cloneImage.rawImage = image.rawImage;
	// cloneImage.setRect((Rectangle)image.rect.clone());
	//
	// return cloneImage;
	// }

//	protected boolean loadTexture(String hashString)
//	{
//		if(texture != null) return true;
//		if(rawImage == null) return false;
//		// erlaube Zugriff nur im Game-Thread
//		if(Graphics.getGraphicThread() != Thread.currentThread())
//			return false;
//
//		texture = TextureLoader.getInstance().getTexture(rawImage, hashString);
//		texture.addUseCount();
//
//		return texture != null;
//	}
//
//	protected boolean isLoaded()
//	{
//		return(texture != null);
//	}
//
//	public BufferedImage getRawImage()
//	{
//		return rawImage;
//	}
	
	public int getX()
	{
		return rect.x;
	}

	public int getY()
	{
		return rect.y;
	}
	
	/**
	 * @return the image width
	 */
	public int getWidth()
	{
		return rect.width;
//		if (rawImage != null)
//			return rawImage.getWidth();
//		return -1;
	}


	/**
	 * @return the image height
	 */
	public int getHeight()
	{	
		return rect.height;
//		if (rawImage != null)
//			return rawImage.getHeight();
//		return -1;
	}

	// /**
	// * Gibt den verwendeten Bildausschnitt der zugrunde liegenden Textur
	// zurück.
	// * Dieser Ausschnitt liegt im Bereich 0-1 und ist unabhängig
	// * @return
	// */
	// public Rectangle2D getRect()
	// {
	// return new Rectangle2D.Float(texX, texY, texWidth, texHeight);
	// }

	/** Gibt das Rechteck zurück. Wird intern als Referenz für Sprites benötigt. */
	Rectangle getRectangle()
	{
		return rect;
	}

//	public void setRect(Rectangle rect)
//	{
//		if (rect == null) return;
//		
//		setRect(rect.x, rect.y, rect.width, rect.height);
//	}

	public void setRectangle(int x, int y, int width, int height)
	{
//		if (x == this.x && y == this.y && width == this.width && height == this.height) return;
		
		rect.setBounds(x, y, width, height);
//		if (modelID != 0) GL11.glDeleteLists(modelID, 1);
//		modelID = 0;
	}
	
	/** Bereitet das Image auf manuelles Zeichnen vor. */
	public Texture getTexture()
	{
//		loadTexture(null);
		
		return texture;
	}
	
	public void setColor(ReadableColor color)
	{
		this.color = new Color(color);
	}

	public Color getColor()
	{
		return color;
	}
	
//	public void setFont(Font font)
//	{
//		this.font = font;
//	}
//	
//	public Font getFont()
//	{
//		return font;
//	}
	
	public int getBlendMode()
	{
		return blendMode;
	}

	public void setBlendMode(int blendMode)
	{
		this.blendMode = blendMode;
	}

	/**
	 * Löscht die Farbe des Bildes. Das Ergebnis ist ein schwarzes Bild.
	 */
	public void clear()
	{
		RenderJob.clear(this);
	}

	/**
	 * Löscht die Farbe des Bildes und ersetzt sie durch die angegebene Farbe.
	 * @param color
	 */
	public void clear(ReadableColor color)
	{
		setColor(color);
		int temp = blendMode;
		setBlendMode(COMPOSITE_ABSOLUT);
		drawRect(0, 0, rect.width, rect.height, true);
		setBlendMode(temp);
	}

	public void drawRect(int x, int y, int width, int height, boolean fill)
	{
		RenderJob.addRectangle(this, x, y, x + width, y + height, fill);
	}

	public void drawLine(int x1, int y1, int x2, int y2)
	{
		RenderJob.addLine(this, x1, y1, x2, y2);
	}

	public void drawImage(int x, int y, Image image)
	{	
		RenderJob.addImage(this, x, y, image);
	}

	public void drawString(int x, int y, String text)
	{
		drawString(x, y, 0, text, 0);
	}

	public void drawString(int x, int y, int width, String text, int align)
	{
		if (text == null || text.length() == 0) return;
		
		RenderJob.addText(this, x, y, width, text, align);
	}
	
	public void draw(float x, float y)
	{
		IMAGE_MATRIX.clear();
		IMAGE_MATRIX.setPosition(x, y);
		
		draw(IMAGE_MATRIX);
	}
	
	/** Zeichnet das Image mit der angegebenen Position und Größe. */
	public void draw(float x, float y, float width, float height)
	{
		IMAGE_MATRIX.clear();
		IMAGE_MATRIX.setPosition(x, y);
		IMAGE_MATRIX.setScale(width, height);
		
		draw(IMAGE_MATRIX);
//		draw(x, y, width / (float)getTexture().getTextureWidth(), height / (float)getTexture().getTextureHeight()),
	}
//	
//	/** Zeichnet das Image mit der angegebenen Position und Größe. */
//	protected void draw(float x, float y, float scaleX, float scaleY)
//	{
//		IMAGE_MATRIX.clear();
////		IMAGE_MATRIX.setView(this);
//		IMAGE_MATRIX.setPosition(x, y);
//		IMAGE_MATRIX.setScale(scaleX, scaleY);
//		
//		draw(IMAGE_MATRIX);
//	}
	
//	protected void draw(Transform2D matrix)
//	{
//		draw(matrix, color, COMPOSITE_NORMAL);
//	}

	public void draw(Transformation matrix)
	{
		if (texture == null)
			System.out.println("null!");
		Graphics.prepareImage();
		
		texture.bind();
		
		GLCache.setColor(color);
		GLCache.setBlendMode(blendMode);

		if (matrix != null) matrix.pushTransformation();
		render();
		if (matrix != null) matrix.popTransformation();
	}
	
	private void render()
	{
		float texX = rect.x / (float) texture.getWidth();
		float texY = rect.y / (float) texture.getHeight();
		float texWidth  = texX + rect.width  / (float) texture.getWidth();
		float texHeight = texY + rect.height / (float) texture.getHeight();
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(texX, texY);
			glVertex2f(0, 0);

			glTexCoord2f(texX, texHeight);
			glVertex2f(0, rect.height);

			glTexCoord2f(texWidth, texHeight);
			glVertex2f(rect.width, rect.height);

			glTexCoord2f(texWidth, texY);
			glVertex2f(rect.width, 0);
		}
		glEnd();
	}
	
//	protected void drawTiled(float x, float y, float width, float height, float texOx, float texOy, float alpha)
//	{
//		if(!loadTexture(null)) return;
//		// deaktiviere 3D
//		D3D.set3DView(false);
//		// bind to the appropriate texture for this sprite
//		texture.bind();
//		
//		glPushMatrix();
////		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//		glLoadIdentity();
//		// Blendwert setzen
//		glColor4f(1.0f, 1.0f, 1.0f, alpha);
//		if(modus == 0)
//		{
//			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//		} else
//		{
//			glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA);
//		}
//
//		// translate to the right location and prepare to draw
//		glTranslatef(x, y, 0);
//		
//		// draw a quad textured to match the sprite
//		glBegin(GL_QUADS);
//		{
//			glTexCoord2f(texRect.x, texRect.y);
//			glVertex2f(0, 0);
//
//			glTexCoord2f(texRect.x, texRect.height);
//			glVertex2f(0, height);
//
//			glTexCoord2f(texRect.width, texRect.height);
//			glVertex2f(width, height);
//
//			glTexCoord2f(texRect.width, texRect.y);
//			glVertex2f(width, 0);
//		}
//		glEnd();
//
//		// restore the model view matrix to prevent contamination
//		glPopMatrix();
//	}
	
//	private void renderImage(float ox, float oy, float width, float height)
//	{
//		cacheID = GL11.glGenLists(1);
//		GL11.glNewList(cacheID, GL11.GL_COMPILE);
//		
//		float x2 = width - ox;
//		float y2 = height - oy;
//		
//		glBegin(GL_QUADS);
//		{
//			glTexCoord2f(texRect.x, texRect.y);
//			glVertex2f(-ox, -oy);
//
//			glTexCoord2f(texRect.x, texRect.height);
//			glVertex2f(-ox, y2);
//
//			glTexCoord2f(texRect.width, texRect.height);
//			glVertex2f(x2, y2);
//
//			glTexCoord2f(texRect.width, texRect.y);
//			glVertex2f(x2, -oy);
//		}
//		GL11.glEndList();
//	}
	
//	public int renderMesh(Mesh mesh)
//	{
//		if (!loadTexture(null)) return -1;
//		
//		texture.bind();
//		
//		int modelID = GL11.glGenLists(1);
//		GL11.glNewList(modelID, GL11.GL_COMPILE);
//		mesh.render(this);
//		GL11.glEndList();
//		
//		return modelID;
//	}
//
	/**
	 * Gibt die benutzte Textur frei.
	 */
	public void dispose()
	{
		if(texture == null) return;

		texture.removeUseCount();
		texture = null;
//		if (modelID != 0) GL11.glDeleteLists(modelID, 1);
//		modelID = 0;
	}
}
