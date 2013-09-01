package cuina.map;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import cuina.database.KeyReference;

/**
 * Rasterbasiertes Spielfeld mit Terraininformationen.
 * <p>
 * Enthält weitere Daten:
 * <dl>
 * <dt>objects</dt>
 * <dd>Liste von Objekten welche Spielobjekte auf der Karte darstellen.</dd>
 * </dl>
 * <dt>events</dt>
 * <dd>Liste von Objekten welche kartenbezogene Events darstellen.</dd>
 * </dl>
 * </p>
 * @author TheWhiteShadow
 */
public class Map implements Serializable
{
	private static final long	serialVersionUID	= 7126463419853704389L;

	public static final int 	LAYERS = 4;
	
	/** Schlüssel der Map. */
	private String key;
	/** Feldanzahl in horizontaler Richtung. */
	public int width;
	/** Feldanzahl in vertikaler Richtung. */
	public int height;
	// transient, da Mapdaten optimiert serialisiert werden.
	/** 3D-Array mit den IDs der einzelnen Felder. */
	transient public short[][][] data;
	/** Spielobjekte auf der Karte. */
	public HashMap<Integer, Object> objects;
	public HashMap<Integer, Object> areas;
	public HashMap<Integer, Object> paths;
	// Referenz zur Tileset-Datenbank
	@KeyReference(name="Tileset")
	public String tilesetKey = "";
	
	public Map(String key, int width, int height)
	{
		this.key = key;
		this.width = width;
		this.height = height;
		this.data = new short[width][height][LAYERS];
		this.objects = new HashMap<Integer, Object>();
		this.areas = new HashMap<Integer, Object>();
		this.paths = new HashMap<Integer, Object>();
	}

	public String getKey()
	{
		return key;
	}

	private final synchronized void writeObject( ObjectOutputStream s ) throws IOException
	{
		s.defaultWriteObject();
		short[] dataStream = new short[width * height * LAYERS];
		for(int x = 0; x < width; x++)
		for(int y = 0; y < height; y++)
		for(int z = 0; z < LAYERS; z++)
		{
			dataStream[x*height*LAYERS + y*LAYERS + z] = data[x][y][z];
		}
		s.writeObject(dataStream);
	}
	
	private final synchronized void readObject( ObjectInputStream s ) throws IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		short[] dataStream = (short[])s.readObject();
		data = new short[width][height][LAYERS];
		for(int x = 0; x < width; x++)
		for(int y = 0; y < height; y++)
		for(int z = 0; z < LAYERS; z++)
		{
			data[x][y][z] = dataStream[x*height*LAYERS + y*LAYERS + z];
//			if (version < 1 && data[x][y][z] > 0)
//				data[x][y][z] += Tileset.AUTOTILES_OFFSET;
		}
//		version = 1;
	}
}

