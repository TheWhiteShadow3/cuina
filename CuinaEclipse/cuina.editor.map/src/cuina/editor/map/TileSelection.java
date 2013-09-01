package cuina.editor.map;

import org.eclipse.jface.viewers.ISelection;

/**
 * Auswahl einer 2D-Ebene von Tile ID's.
 * @author TheWhiteShadow
 */
public class TileSelection implements ISelection
{
	/** Instanz einer leeren Tile-Auswahl. */
	public static final TileSelection EMPTY = new TileSelection(new short[0][0]);
	
	private int x;
	private int y;
	/** 2D-Array(x, y) für die Tiles. */
	private final short[][] data;

	/**
	 * Erstellt eine neues Auswahl mit einem 2D-Array mit Tile-ID's.
	 * <p>
	 * <b>Achtung!</b><br>
	 * Die ID's müssen bei <code>1</code> anfangen,
	 * da die <code>0</code> für nicht gesetzte Felder reserviert ist.
	 * </p>
	 * @param x X-Koordinate der Auswahl.
	 * @param y Y-Koordinate der Auswahl.
	 * @param data 2D-Array mit Tile-ID's.
	 */
	public TileSelection(int x, int y, short[][] data)
	{
		if (data == null) throw new NullPointerException();
		
		this.x = x;
		this.y = y;
		this.data = data;
	}
	
	/**
	 * Erstellt eine neues Auswahl mit einem 2D-Array mit Tile-ID's.
	 * <p>
	 * <b>Achtung!</b><br>
	 * Die ID's müssen bei <code>1</code> anfangen,
	 * da die <code>0</code> für nicht gesetzte Felder reserviert ist.
	 * </p>
	 * @param data 2D-Array mit Tile-ID's.
	 */
	public TileSelection(short[][] data)
	{
		this(0, 0, data);
	}
	
	/**
	 * Erstellt eine neues Auswahl mit einer einzigen Feld.
	 * <p>
	 * <b>Achtung!</b><br>
	 * Die ID mussen bei <code>1</code> anfangen,
	 * da die <code>0</code> für nicht gesetzte Felder reserviert ist.
	 * </p>
	 * @param x X-Koordinate der Auswahl.
	 * @param y Y-Koordinate der Auswahl.
	 * @param id Tile-ID.
	 */
	public TileSelection(int x, int y, int id)
	{
		this.x = x;
		this.y = y;
		this.data = new short[][] {{(short) id}};
	}
	
	/**
	 * Erstellt eine neues Auswahl mit einer einzigen Feld.
	 * <p>
	 * <b>Achtung!</b><br>
	 * Die ID mussen bei <code>1</code> anfangen,
	 * da die <code>0</code> für nicht gesetzte Felder reserviert ist.
	 * </p>
	 * @param id Tile-ID.
	 */
	public TileSelection(int id)
	{
		this(0, 0, id);
	}
	
	short[][] getData()
	{
		return data;
	}
	
	/**
	 * Gibt die tatsächliche ID des Feldes zurück.
	 * <p>
	 * Eine ID von -1 bedeutet, dass das Feld nicht gesetzt ist.
	 * </p>
	 * @param x
	 *            X-Position.
	 * @param y
	 *            Y-Position.
	 * @return Feld-ID.
	 * @throws ArrayIndexOutOfBoundsException
	 *         wenn das Feld nicht existiert.
	 */
	public short get(int x, int y)
	{
		return (short) (data[x][y] - 1);
	}
	
	/**
	 * Gibt des tatsächlichen Wert des Feldes zurück.
	 * 
	 * @param x
	 *            X-Position.
	 * @param y
	 *            Y-Position.
	 * @return Feld.
	 */
	public short getValue(int x, int y)
	{
		return data[x][y];
	}
	
	/**
	 * Setzt die tatsächliche ID des Feldes.
	 * @param x X-Position.
	 * @param y Y-Position.
	 * @param id Feld-ID.
	 * @throws ArrayIndexOutOfBoundsException
	 *         wenn das Feld nicht existiert.
	 */
	public void set(int x, int y, short id)
	{
		data[x][y] = (short) (id+1);
	}
	
	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	/**
	 * Gibt die Breite des Datenarrays zurück.
	 * @return Breite des Datenarrays.
	 */
	public int getWidth()
	{
		return data.length;
	}
	
	/**
	 * Gibt die Höhe des Datenfeldes zurück.
	 * @return Höhe des Datenfeldes.
	 */
	public int getHeight()
	{
		return data.length != 0 ? data[0].length : 0;
	}
	
	/**
	 * Gibt des tatsächlichen Wert des Feldes zurück. Wenn die Position außerhalb der Ebene liegt,
	 * wird die Position so lange um die Breite bzw. Höhe der Ebene verschoben,
	 * bis sie sich wieder innerhalb der Ebene befindet.
	 * 
	 * @param x
	 *            X-Position.
	 * @param y
	 *            Y-Position.
	 * @return Feld.
	 */
	public short getTiled(int x, int y, int x0, int y0)
	{
		return data[mod(x + x0, data.length)][mod(y + y0, data[0].length)];
	}
	
	/** Eine vernünftige Modulo-Funktion ohne Negativ-Fehler. */
	private int mod(int value, int max)
	{
		value %= max;
		return value >= 0 ? value : value + max;
	}
	
	/**
	 * Gibt an, ob ein Feld im Templayer innerhalb der aktuellen Auswahl liegt.
	 * 
	 * @param x
	 *            Absolute X-Position auf der Karte.
	 * @param y
	 *            Absolute Y-Position auf der Karte.
	 * @return <code>true</code>, wenn die Position innerhalb der Auswahl
	 *         existiert, andernfalls <code>false</code>.
	 */
	public boolean contains(int x, int y)
	{
		if (isEmpty()) return false;
		
		return (x >= this.x && y >= this.y && x < this.x + getWidth() && y < this.y + getHeight());
	}
	
	/**
	 * Gibt an, ob ein Feld im Templayer innerhalb der aktuellen Auswahl liegt.
	 * 
	 * @param x
	 *            Realtive X-Position auf der Karte.
	 * @param y
	 *            Realtive Y-Position auf der Karte.
	 * @return <code>true</code>, wenn die Position innerhalb der Auswahl
	 *         existiert, andernfalls <code>false</code>.
	 */
	public boolean containsRel(int x, int y)
	{
		if (isEmpty()) return false;
		
		return (x >= 0 && y >= 0 && x < getWidth() && y < getHeight());
	}

	@Override
	public boolean isEmpty()
	{
		return getWidth() == 0 || getHeight() == 0;
	}
}