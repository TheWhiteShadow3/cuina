/*
 * Cuina Engine
 * Copyright (C) 2011 - 2012 by Cuina Team (http://www.cuina.byethost12.com/)
 *
 * see license.txt for more info
 */

package cuina.graphics;

import cuina.graphics.transform.Transformation;


/**
 * Abstrakte Sprite-Klasse für die Anzeige von Images.
 * Stellt verschiedene Methoden zur Positionierung und Transformation von Bildern bereit.
 * Implementationen dieses Klasse müssen die Methode <code>{@link #refresh()}</code> überschreiben.
 * <p>
 * Sprites werden, wenn nicht anders angegeben, dem aktuellen {@link GraphicManager} übergeben.
 * </p>
 * 
 * @author TheWhiteShadow
 * @version 1.2
 */
public abstract class Sprite extends AbstractGraphic
{
	@SuppressWarnings("serial")
	public static final Sprite DUMMY_SPRITE = new Sprite(null)
	{ @Override public void refresh() {} };
	
	private static final long	serialVersionUID	= -8939421838969852563L;
	
	protected float		zoomX = 1.0f;
	protected float		zoomY = 1.0f;

	protected float		angle = 0f;

	protected float 	x = 0;
	protected float 	y = 0;
	
	protected float  	ox = 0; // Origin-X
	protected float 	oy = 0; // Origin-Y

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
	 * Fügt das Sprite dem angegebenen Kontainer hinzu, anstatt dem aktuellen {@link GraphicManager}.
	 * 
	 * @param image Das Bild, was das Sprite anzeigen soll.
	 * @param container Der Grafik-Kontainer, dem das Sprite hinzugefügt werden soll.
	 */
	public Sprite(Image image, GraphicContainer container)
	{
		super.setImage(image);
		if (container == null) return;
		container.addGraphic(this);
	}

	/**
	 * Gibt den Faktor der horizontalen Größe des Sprites zurück.
	 * @return Größe in X-Richtung.
	 */
	public float getZoomX()
	{
		return zoomX;
	}

	/**
	 * Setzt den Faktor der horizontalen Größe des Sprites. <i>Default ist 1.</i>
	 * @param zoomX Größe in X-Richtung.
	 */
	public void setZoomX(float zoomX)
	{
		this.zoomX = zoomX;
	}

	/**
	 * Gibt den Faktor der vertikalen Größe des Sprites zurück.
	 * @return Größe in Y-Richtung.
	 */
	public float getZoomY()
	{
		return zoomY;
	}

	/**
	 * Setzt den Faktor der vertikalen Größe des Sprites. <i>Default ist 1.</i>
	 * @param zoomY Größe in Y-Richtung.
	 */
	public void setZoomY(float zoomY)
	{
		this.zoomY = zoomY;
	}

	/**
	 * Gibt den Winkel des Sprites zurück. Gegen dem Urzeigersinn ist positiv.
	 * @return Winkel des Sprites in Grad.
	 */
	public float getAngle()
	{
		return angle;
	}

	/**
	 * Setzt den Winkel des Sprites. Gegen dem Urzeigersinn ist positiv.
	 * @param angle Winkel in Grad.
	 */
	public void setAngle(float angle)
	{
		this.angle = angle;
	}

	/**
	 * Gibt die X-Position vom Sprite zurück.
	 * @return X-Position.
	 */
	public float getX()
	{
		return x;
	}

	/**
	 * Setzt die X-Position vom Sprite.
	 * @param x X-Position.
	 */
	public void setX(float x)
	{
		this.x = x;
	}

	/**
	 * Gibt die Y-Position vom Sprite zurück.
	 * @return Y-Position.
	 */
	public float getY()
	{
		return y;
	}

	/**
	 * Setzt die Y-Position vom Sprite.
	 * @param y Y-Position.
	 */
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
	
	/**
	 * Gibt die Breite vom Sprite zurück.
	 * Die Breite entspricht der Breite vom Bild.
	 * Wenn das Bild <code>null</code> ist wird -1 zurückgegeben.
	 * @return die Breite vom Sprite.
	 */
	public int getWidth()
	{
		if (image == null) return -1;
		
		return image.getWidth();
	}
	
	/**
	 * Gibt die Höhe vom Sprite zurück.
	 * Die Höhe entspricht der Breite vom Bild.
	 * Wenn das Bild <code>null</code> ist wird -1 zurückgegeben.
	 * @return die Höhe vom Sprite.
	 */
	public int getHeight()
	{
		if (image == null) return -1;
		
		return image.getHeight();
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
}
