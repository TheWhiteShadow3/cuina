package cuina.editor.map.internal.layers;

import cuina.editor.map.TileSelection;
import cuina.gl.GC;
import cuina.map.Map;
import cuina.map.Tileset;

import org.lwjgl.util.Color;

public class TilemapUtil
{
	private static Color BACK_COLOR1 = new Color(160, 160, 160);
	private static Color BACK_COLOR2 = new Color(210, 210, 210);
	
	public static void updateAutotiles(Map map, TileSelection layer, int x0, int y0, int z0)
	{
		for (int x = 0; x < layer.getWidth(); x++)
		for (int y = 0; y < layer.getHeight(); y++)
		{
			if (!isEdge(layer, x, y)) continue;
			
			short[] neighbors = getNeighbors(map, layer, x, y, x0, y0, z0);
			if (neighbors[4] >= Tileset.AUTOTILES_OFFSET)
			{
				map.data[x0 + x][y0 + y][z0] = selectAutotileFromNeighbors(neighbors);
			}
	
			for (int i = 0; i < neighbors.length; i++)
			{
				if (neighbors[i] >= Tileset.AUTOTILES_OFFSET)
				{
					if (i != 4)
					{
						int xPos = i % 3 + (x-1);
						int yPos = i / 3 + (y-1);
						map.data[x0 + xPos][y0 + yPos][z0] = selectAutotileFromNeighbors(
								getNeighbors(map, layer, xPos, yPos, x0, y0, z0));
					}
				}
			}
		}
	}
	
	/**
	 * Gibt die Nachbar-Tiles des Feldes zurück.
	 * <p>
	 * Das zurückgegebene Array hat die Anordnung:
	 * <pre>
	 * 0 1 2
	 * 3 <i>4</i> 5
	 * 6 7 8
	 * </pre>
	 * </p>
	 * @param map Die Karte.
	 * @param tiles Die Tiles, welche als Maskierung der Karte dient.
	 * @param x X-Position vom Feld, dessen Nachbarn gesucht werden.
	 * @param y Y-Position vom Feld, dessen Nachbarn gesucht werden.
	 * @param x0 X-Position des TileLayers auf der Karte.
	 * @param y0 Y-Position des TileLayers auf der Karte.
	 * @param z0 Z-Position des TileLayers auf der Karte.
	 * @return Array mit den ID's der Nachbarn.
	 */
	private static short[] getNeighbors(Map map, TileSelection tiles, int x, int y, int x0, int y0, int z0)
	{
		short[] neighboors = new short[9];
		
		int xPos, yPos;
		for (int c = 0; c < 9; c++)
		{
			xPos = (c % 3) + (x-1);
			yPos = (c / 3) + (y-1);
			if (!tiles.containsRel(xPos, yPos) || tiles.get(xPos, yPos) == -1)
			{
				if (x0 + xPos < 0 || y0 + yPos < 0 || x0 + xPos >= map.width ||y0 + yPos >= map.height)
					neighboors[c] = -1;
				else
					neighboors[c] = map.data[x0 + xPos][y0 + yPos][z0];
			}
			else
			{
				neighboors[c] = tiles.get(xPos, yPos);
			}
		}
		
		return neighboors;
	}
	
	/**
	 * Bestimmt die Autotile-ID aus den Nachbar-Tiles um die angegebenen Koordinate auf der Map.
	 * @param x Logische X-Position auf der Map.
	 * @param y Logische Y-Position auf der Map.
	 * @return Autotile-ID
	 */
	private static short selectAutotileFromNeighbors(short[] neighbors)
	{
		int autotileIndex = TilemapUtil.autotileFromID(neighbors[4]);
		if (autotileIndex == -1) return -1;
		
		return TilemapUtil.selectAutotileID(autotileIndex, checkNeighborsSameAutotile(neighbors, autotileIndex));
	}
	
	private static boolean isEdge(TileSelection layer, int x, int y)
	{
		if (layer.get(x, y) == 0) return false;
		if (x == 0 || y == 0 || x == layer.getWidth() - 1 || y == layer.getHeight() - 1) return true;
		
		int xPos, yPos;
		for(int c = 0; c < 9; c++)
		{
			if(c != 4) // 4 ist die Mitte, dessen Nachbarn wir wollen
			{
				xPos = c % 3 + (x-1);
				yPos = c / 3 + (y-1);
				if (layer.get(xPos, yPos) == -1) return true;
			}
		}
		return false;
	}
	
	private static boolean[] checkNeighborsSameAutotile(short[] neighbors, int index)
	{
		boolean[] result = new boolean[9];
		
		for (int i = 0; i < neighbors.length; i++)
		{
			if (i == 4) continue;
			
			if (neighbors[i] == -1)
				result[i] = true;//((flags & STOP_ON_BORDER) == 0);
			else
				result[i] = (TilemapUtil.autotileFromID(neighbors[i]) == index);
		}
		return result;
	}
	
	/**
	 * Gibt den Index des Autotiles zurück, von dem die angegebene ID stammt.
	 * Wenn die ID von keinem Autotile stammt, wird <code>-1</code> zurückgegeben.
	 * @param id ID eines Autotiles.
	 * @return Autotile-Index aus dem die <i>id</i> stammt.
	 */
	public static int autotileFromID(short id)
	{
		if (id < Tileset.AUTOTILES_OFFSET) return -1;
		return ((id - Tileset.AUTOTILES_OFFSET) / 48);
	}
	
	/**
	 * Bestimmt die Autotile-ID aus dem übergebenen Autotile-Index und dem Nachbar-Array.
	 * @param index Autotile-Index
	 * @param neighboors Array der Länge 9, wo gespeichert ist, ob die Nachbarn zum Autotile gehören.
	 * @return Autotile-ID
	 */
	public static short selectAutotileID(int index, boolean[] neighboors)
	{
		int offset = index * 8*6 + Tileset.AUTOTILES_OFFSET; 
		// Außenecken
		if (neighboors[1] && neighboors[3] && neighboors[5] && neighboors[7])
		{
			short result = 0;
			if (!neighboors[0]) result += 1;
			if (!neighboors[2]) result += 2;
			if (!neighboors[6]) result += 8;
			if (!neighboors[8]) result += 4;
			return (short) (result + offset);
		}
		// Rand links
		if (neighboors[1] && neighboors[5] && neighboors[7])
		{
			short result = 16;
			if (!neighboors[2]) result += 1;
			if (!neighboors[8]) result += 2;
			return (short) (result + offset);
		}
		// Rand oben
		if (neighboors[3] && neighboors[5] && neighboors[7])
		{
			short result = 20;
			if (!neighboors[6]) result += 2;
			if (!neighboors[8]) result += 1;
			return (short) (result + offset);
		}
		// Rand rechts
		if (neighboors[1] && neighboors[3] && neighboors[7])
		{
			short result = 24;
			if (!neighboors[6]) result += 1;
			if (!neighboors[0]) result += 2;
			return (short) (result + offset);
		}
		// Rand unten
		if (neighboors[1] && neighboors[3] && neighboors[5])
		{
			short result = 28;
			if (!neighboors[0]) result += 1;
			if (!neighboors[2]) result += 2;
			return (short) (result + offset);
		}
		// beidseitiger Rand
		if (neighboors[1] && neighboors[7]) return (short) (32 + offset);
		if (neighboors[3] && neighboors[5]) return (short) (33 + offset);
		// Innenecke oben links
		if (neighboors[5] && neighboors[7])
		{
			short result = 34;
			if (!neighboors[8]) result += 1;
			return (short) (result + offset);
		}
		// Innenecke oben rechts
		if (neighboors[3] && neighboors[7])
		{
			short result = 36;
			if (!neighboors[6]) result += 1;
			return (short) (result + offset);
		}
		// Innenecke unten rechts
		if (neighboors[1] && neighboors[3])
		{
			short result = 38;
			if (!neighboors[0]) result += 1;
			return (short) (result + offset);
		}
		// Innenecke oben links
		if (neighboors[1] && neighboors[5])
		{
			short result = 40;
			if (!neighboors[2]) result += 1;
			return (short) (result + offset);
		}
		// Endstücke
		if (neighboors[7]) return (short) (42 + offset);
		if (neighboors[5]) return (short) (43 + offset);
		if (neighboors[1]) return (short) (44 + offset);
		if (neighboors[3]) return (short) (45 + offset);
		// Insel (ohne Abfrage, da letzte Möglichkeit)
		return (short) (46 + offset);
		// Die 47 entspricht der 0 und wird daher nicht erreicht.
	}
	
	public static void paintGrid(GC gc, int x1, int y1, int x2, int y2, int size)
	{		
		gc.setColor(BACK_COLOR1);
		gc.fillRectangle(x1, y1, x2, y2);
		// Zeichne Raster
		int minX = x1 / size;
		int minY = y1 / size;
		int maxX = x2 / size;
		int maxY = y2 / size;
		gc.setColor(BACK_COLOR2);
		for (int cx = minX; cx < maxX; cx++)
		{
			for (int cy = minY; cy < maxY; cy++)
			{
				if ((cx + cy) % 2 == 0)
				{
					gc.fillRectangle(cx * size, cy * size, size, size);
				}
			}
		}
	}
	
//	public static void paintGrid(GC gc, int x, int y, int width, int height, int size)
//	{
//		gc.setColor(Color.GREY);
//		int maxX = width / size;
//		int maxY = height / size;
//		for (int cx = 0; cx < maxX; cx++)
//		{
//			for (int cy = 0; cy < maxY; cy++)
//			{
//				gc.drawRectangle(cx * size + x, cy * size + y, size, size);
//			}
//		}
//	}
}
