/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;


/**
 * Abstrakte Sprite-Klasse für die Verwaltung von Images.
 * Stellt verschiedene Methoden zur Darstellung eines Images bereit.
 * Implementationen dieses Klasse müssen die Methode <code>{@link #refresh()}</code> überschreiben.
 * Sprites werden automatisch über einen <code>{@link GraphicManager}</code> verwaltet.
 * Der Default-GraphicManager kann über <code>{@link Graphics#GraphicManager}</code>
 * gesetzt werden.
 * 
 * @author TheWhiteShadow
 * @version 1.2
 */
public abstract class Sprite extends AbstractGraphic
{
	private static final long	serialVersionUID	= -8939421838969852563L;
	
	/** The width in pixels of this sprite */
	protected float		zoomX = 1.0f;

	/** The height in pixels of this sprite */
	protected float		zoomY = 1.0f;

	protected float		angle = 0f;

	protected float 	x = 0;
	protected float 	y = 0;
	
	protected float  	ox = 0; // Origin-X
	protected float 	oy = 0; // Origin-Y

	//XXX: Entfernt wegen implizitem Aufruf. Führt zu Leicht zu Fehlern.
//	/**
//	 * Erstellt ein neues Sprite ohne Image.
//	 * <p>
//	 * Das neu erstellte Sprite wird dem aktuellen {@link #GraphicManager} hinzugefügt.
//	 * </p>
//	 */
//	public Sprite()
//	{
//		this(null, Graphics.GraphicManager);
//	}
	
	/**
	 * Erstellt ein neues Sprite aus dem übergebenen Image. Dieses darf <code>null</code> sein.
	 * <p>
	 * Das neu erstellte Sprite wird dem aktuellen {@link GraphicManager} hinzugefügt.
	 * </p>
	 * @param image Das Bild, was das Sprite anzeigen soll.
	 */
	public Sprite(Image image)
	{
		this(image, Graphics.GraphicManager);
	}
	
	/**
	 * Erstellt ein neues Sprite aus dem übergebenen Image. Dieses darf <code>null</code> sein.
	 * <p>
	 * Fügt das Sprite dem angegebenen Kontainer hinzu, anstatt dem aktuellen {@link GraphicManager}.
	 * </p>
	 * @param image Das Bild, was das Sprite anzeigen soll.
	 * @param container Der Grafik-Kontainer, dem das Sprite hinzugefügt werden soll.
	 */
	public Sprite(Image image, GraphicContainer container)
	{
		super.setImage(image);
		if (container == null) return;
		container.addGraphic(this);
	}

	public float getZoomX()
	{
		return zoomX;
	}

	public void setZoomX(float zoomX)
	{
		this.zoomX = zoomX;
	}

	public float getZoomY()
	{
		return zoomY;
	}

	public void setZoomY(float zoomY)
	{
		this.zoomY = zoomY;
	}

	public float getAngle()
	{
		return angle;
	}

	public void setAngle(float angle)
	{
		this.angle = angle;
	}

	public int getAlpha()
	{
		return image.getColor().getAlpha();
	}

	public void setAlpha(int alpha)
	{
		image.getColor().setAlpha(alpha);
	}

	public float getX()
	{
		return x;
	}

	public void setX(float x)
	{
		this.x = x;
	}

	public float getY()
	{
		return y;
	}

	public void setY(float y)
	{
		this.y = y;
	}

	/**
	 * Gibt die X-Komponente vom Aufhängungspunkt des Sprites zurück.
	 * @return X-Komponente vom Aufhängungspunkt.
	 */
	public float getOX()
	{
		return ox;
	}

	/**
	 * Setzt die X-Komponente vom Aufhängungspunkt des Sprites.
	 * @param ox X-Komponente vom Aufhängungspunkt.
	 */
	public void setOX(float ox)
	{
		this.ox = ox;
	}

	/**
	 * Gibt die Y-Komponente vom Aufhängungspunkt des Sprites zurück.
	 * @return Y-Komponente vom Aufhängungspunkt.
	 */
	public float getOY()
	{
		return oy;
	}

	/**
	 * Setzt die Y-Komponente vom Aufhängungspunkt des Sprites.
	 * @param oy Y-Komponente vom Aufhängungspunkt.
	 */
	public void setOY(float oy)
	{
		this.oy = oy;
	}

	@Override
	protected void transformAndRender(Transformation matrix)
	{
		if (matrix == null)
		{
			Image.IMAGE_MATRIX.clear();
			Image.IMAGE_MATRIX.setOrigion(ox, oy);
			Image.IMAGE_MATRIX.setPosition(x, y);
			Image.IMAGE_MATRIX.setRotation(angle);
			Image.IMAGE_MATRIX.setScale(zoomX, zoomY);
			matrix = Image.IMAGE_MATRIX;
		}
		super.transformAndRender(matrix);
	}


//	@Override
//	protected void render()
//	{
//		Texture texture = image.getTexture();
//		float width = image.getWidth();
//		float height = image.getHeight();
//		float texX = image.getX() / (float) texture.getWidth();
//		float texY = image.getY() / (float) texture.getHeight();
//		float texWidth  = texX + width  / texture.getWidth();
//		float texHeight = texY + height / texture.getHeight();
//		
//		glBegin(GL_QUADS);
//		{
//			glTexCoord2f(texX, texY);
//			glVertex2f(0, 0);
//
//			glTexCoord2f(texX, texHeight);
//			glVertex2f(0, height);
//
//			glTexCoord2f(texWidth, texHeight);
//			glVertex2f(width, height);
//
//			glTexCoord2f(texWidth, texY);
//			glVertex2f(width, 0);
//		}
//		glEnd();
//	}
}
