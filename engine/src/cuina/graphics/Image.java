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
import static org.lwjgl.opengl.GL13.glMultiTexCoord2f;

import cuina.graphics.transform.Transform2D;
import cuina.util.Rectangle;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
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

	public static final int COMPOSITE_TEST = 7;
	
	public static final Transform2D IMAGE_MATRIX = new Transform2D();
	
	private Texture texture;
	private List<MultiTexture> multiTextures;
	
//	Koordinaten in der Textur
	private final Rectangle rect;
//	private Font font = Graphics.getDefaultFont();
	private Color color = new Color(Graphics.getDefaultColor());

	private int blendMode = COMPOSITE_NORMAL;

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
	
	protected Image(Image image, int x, int y, int width, int height)
	{
		this(image.texture, x, y, width, height);
		this.multiTextures = image.multiTextures;
	}

	public void addTexture(Texture texture, int mixMode)
	{
		if (multiTextures == null)
		{
			multiTextures = new ArrayList<MultiTexture>(4);
			multiTextures.add(new MultiTexture(this.texture, GL11.GL_REPLACE));
		}
		else
		{
			// FIXME: Hard codiertes Limit von 4 Texturen.
			if (multiTextures.size() == 4) throw new IllegalArgumentException();
		}
		multiTextures.add(new MultiTexture(texture, mixMode));
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
		
		rect.set(x, y, width, height);
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
		if (color == null) throw new NullPointerException();
		
		this.color = new Color(color);
	}

	public Color getColor()
	{
		return color;
	}
	
	/**
	 * Gibt die Transparenz des Images zurück.
	 * @return Transparenz von 0-255.
	 */
	public int getAlpha()
	{
		return color.getAlpha();
	}

	/**
	 * Setzt die Transparenz des Images.
	 * @param alpha Transparenz von 0-255.
	 */
	public void setAlpha(int alpha)
	{
		color.setAlpha(alpha);
	}
	
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
	
	public void drawView(int viewID, int x, int y)
	{
		if (viewID == -1) return;
		
		RenderJob.addView(this, viewID, x, y);
	}
	
	/**
	 * Gibt die prozentuale X-Position des Bildausschnitts auf der Textur zurück.
	 * @return prozentuale X-Position des Bildausschnitts.
	 */
	public float getPercentageX()
	{
		return rect.x / (float) texture.getWidth();
	}
	
	/**
	 * Gibt die prozentuale Y-Position des Bildausschnitts auf der Textur zurück.
	 * @return prozentuale Y-Position des Bildausschnitts.
	 */
	public float getPercentageY()
	{
		return rect.y / (float) texture.getHeight();
	}
	
	/**
	 * Gibt die prozentuale Breite des Bildausschnitts auf der Textur zurück.
	 * @return prozentuale Breite des Bildausschnitts.
	 */
	public float getPercentageWidth()
	{
		return rect.width / (float) texture.getWidth();
	}
	
	/**
	 * Gibt die prozentuale Höhe des Bildausschnitts auf der Textur zurück.
	 * @return prozentuale Höhe des Bildausschnitts.
	 */
	public float getPercentageHeight()
	{
		return rect.height / (float) texture.getHeight();
	}
	
//	public void draw(float x, float y)
//	{
//		IMAGE_MATRIX.clear();
//		IMAGE_MATRIX.setPosition(x, y);
//		
//		draw(IMAGE_MATRIX);
//	}
//	
//	/** Zeichnet das Image mit der angegebenen Position und Größe. */
//	public void draw(float x, float y, float width, float height)
//	{
//		IMAGE_MATRIX.clear();
//		IMAGE_MATRIX.setPosition(x, y);
//		IMAGE_MATRIX.setScale(width, height);
//		
//		draw(IMAGE_MATRIX);
////		draw(x, y, width / (float)getTexture().getTextureWidth(), height / (float)getTexture().getTextureHeight()),
//	}
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
	
	/**
	 * Bindet die Textur des Images an den Grefikkontext.
	 */
	public void bind()
	{
		GLCache.bindTexture(texture);
		GLCache.setColor(color);
		GLCache.setBlendMode(blendMode);
	}
	
	public static void unbind()
	{
		GLCache.bindTexture(null);
	}

//	public void draw(Transformation matrix)
//	{
//		if (texture == null)
//			System.out.println("null!");
//		Graphics.prepareImage();
//		
//		texture.bind();
//		
//		GLCache.setColor(color);
//		GLCache.setBlendMode(blendMode);
//
//		if (matrix != null) matrix.pushTransformation();
//		render();
//		if (matrix != null) matrix.popTransformation();
//	}
	
	/**
	 * Zeichnet das angegebenen Image in seiner natürlichen Größe.
	 * <p>
	 * Falls das Image disposed wurde, passiert nichts.
	 * </p>
	 * @param image
	 */
	public static void renderImage(Image image)
	{
		if (image.texture == null) return;
		
		GraphicUtil.set3DView(false);
		if (image.multiTextures != null)
		{
			renderMultiTextureImage(image);
			return;
		}
		image.bind();
		
		Rectangle rect = image.getRectangle();
		float x1 = image.getPercentageX();
		float y1 = image.getPercentageY();
		float x2 = x1 + image.getPercentageWidth();
		float y2 = y1 + image.getPercentageHeight();
		
		glBegin(GL_QUADS);
		{
			glTexCoord2f(x1, y1);
			glVertex2f(0, 0);

			glTexCoord2f(x1, y2);
			glVertex2f(0, rect.height);

			glTexCoord2f(x2, y2);
			glVertex2f(rect.width, rect.height);

			glTexCoord2f(x2, y1);
			glVertex2f(rect.width, 0);
		}
		glEnd();
	}
	
	private static void renderMultiTextureImage(Image image)
	{
		GLCache.bindMultiTextures(image.multiTextures);
		
		Rectangle rect = image.getRectangle();
		float x1 = image.getPercentageX();
		float y1 = image.getPercentageY();
		float x2 = x1 + image.getPercentageWidth();
		float y2 = y1 + image.getPercentageHeight();
		
		glBegin(GL_QUADS);
		{
			for (int i = 0; i < image.multiTextures.size(); i++)
				glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, x1, y1);
			glVertex2f(0, 0);

			for (int i = 0; i < image.multiTextures.size(); i++)
				glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, x1, y2);
			glVertex2f(0, rect.height);

			for (int i = 0; i < image.multiTextures.size(); i++)
				glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, x2, y2);
			glVertex2f(rect.width, rect.height);

			for (int i = 0; i < image.multiTextures.size(); i++)
				glMultiTexCoord2f(GL13.GL_TEXTURE0 + i, x2, y1);
			glVertex2f(rect.width, 0);
		}
		glEnd();
		
//		glBegin(GL_QUADS);
//		{
//			glTexCoord2f(x1, y1);
//			glVertex2f(0, 0);
//
//			glTexCoord2f(x1, y2);
//			glVertex2f(0, rect.height);
//
//			glTexCoord2f(x2, y2);
//			glVertex2f(rect.width, rect.height);
//
//			glTexCoord2f(x2, y1);
//			glVertex2f(rect.width, 0);
//		}
//		glEnd();
		
		
//		GLCache.unbindMultiTextures();
//		
//		for (int i = image.multiTextures.size()-1; i > 0; i++)
//		{
//			GL11.glDisable(GL11.GL_TEXTURE_2D);
//			GL13.glActiveTexture(image.multiTextures.get(i-1).target);
//		}
	}
	
	protected static class MultiTexture
	{
		public Texture texture;
		public int mixMode;
		
		public MultiTexture(Texture texture, int mixMode)
		{
			if (texture == null) throw new NullPointerException();
			
			this.texture = texture;
			this.mixMode = mixMode;
		}
		
//		private int getTarget(int index)
//		{
//			switch(index)
//			{
//				case 0: return GL13.GL_TEXTURE0;
//				case 1: return GL13.GL_TEXTURE1;
//				case 2: return GL13.GL_TEXTURE2;
//				case 3: return GL13.GL_TEXTURE3;
//				default: throw new IllegalArgumentException();
//			}
//		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + mixMode;
			result = prime * result + texture.target;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			MultiTexture other = (MultiTexture) obj;
			if (mixMode != other.mixMode) return false;
			if (texture.target != other.texture.target) return false;
			return true;
		}
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
