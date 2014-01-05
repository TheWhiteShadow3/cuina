package cuina.editor.map.internal;

import cuina.resource.ResourceException;
import cuina.resource.ResourceManager.Resource;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/** Angepasste Version */
public class TileFactory
{
	private TileFactory()
	{}

	// @formatter:off
	/**
	 * Indexliste des Expansions-Musters für ein 3x4 Image. Die Liste ist
	 * 0-basiert und enthält Werte für jedes Viertel.
	 * <p>
	 * Reihenfolge: oben-links, oben-rechts, unten-links, unten-rechts
	 * </p>
	 */
    private static final short[] EXPANSION_TABLE_A =
    {
          7,  7,  7,  7,  2,  7,  7,  7,  7,  2,  7,  7,  2,  2,  7,  7, 
          7,  7,  7,  2,  2,  7,  7,  2,  7,  2,  7,  2,  2,  2,  7,  2, 
          7,  7,  2,  7,  2,  7,  2,  7,  7,  2,  2,  7,  2,  2,  2,  7, 
          7,  7,  2,  2,  2,  7,  2,  2,  7,  2,  2,  2,  2,  2,  2,  2, 
          6,  6,  6,  6,  6,  2,  6,  6,  6,  6,  6,  2,  6,  2,  6,  2, 
          4,  4,  4,  4,  4,  4,  4,  2,  4,  4,  2,  4,  4,  4,  2,  2, 
          8,  8,  8,  8,  8,  8,  2,  8,  2,  8,  8,  8,  2,  8,  2,  8, 
         10, 10, 10, 10,  2, 10, 10, 10, 10,  2, 10, 10,  2,  2, 10, 10, 
          6,  8,  6,  8,  4,  4, 10, 10,  3,  3,  3,  3,  3,  3,  3,  2, 
          5,  5,  5,  5,  5,  5,  2,  5, 11, 11, 11, 11,  2, 11, 11, 11, 
          9,  9,  9,  9,  9,  2,  9,  9,  3,  5,  3,  5,  3,  3,  9,  9, 
          9, 11,  9, 11,  5,  5, 11, 11,  3,  5,  9, 11,  0,  0,  0,  0, 
    };
    
	/**
	 * Indexliste des Expansions-Musters für ein 3x5 Image. Die Liste ist
	 * 0-basiert und enthält Werte für jedes Viertel.
	 * <p>
	 * Reihenfolge: oben-links, oben-rechts, unten-links, unten-rechts
	 * </p>
	 */
    private static final short[] EXPANSION_TABLE_B =
    {
         10, 10, 10, 10,  1,  1,  1,  1,  2,  2,  2,  2,  1,  2,  1,  2,
          5,  5,  5,  5,  1,  5,  1,  5, 10,  2, 10,  5,  1,  2,  1,  5,
          4,  4,  4,  4,  1,  1,  4,  4,  4,  2,  4,  2,  1,  2,  4,  2,
          4,  5,  4,  5,  1,  5,  4,  5,  4,  2,  4,  5,  1,  2,  4,  5,
          9,  9,  9,  9,  9,  2,  9,  2,  9,  5,  9,  5,  9,  2,  9,  5, 
          7,  7,  7,  7,  7,  7,  7,  5,  7,  7,  4,  7,  7,  7,  4,  5, 
         11, 11, 11, 11,  1, 11,  1, 11,  5, 11,  5, 11,  1, 11,  5, 11,
         13, 13, 13, 13,  1, 13, 13, 13, 13,  2, 13, 13,  1,  2, 13, 13,
          9, 11,  9, 11,  7,  7, 13, 13,  6,  6,  6,  6,  6,  6,  6,  5,
          8,  8,  8,  8,  8,  8,  4,  8, 14, 14, 14, 14,  1, 14, 14, 14,
         12, 12, 12, 12, 12,  2, 12, 12,  6,  8,  6,  8,  6,  6, 12, 12,
         12, 14, 12, 14,  8,  8, 14, 14,  6,  8, 12, 14,  0,  0,  0,  0,
    };
    // @formatter:on

	public static AutotileSet createAutotileSet(Resource resource, int tileSize) throws ResourceException
	{
		BufferedImage rawImage;
		try
		{
			rawImage = ImageIO.read(resource.getPath().toFile());
		}
		catch (IOException e)
		{
			throw new ResourceException(resource.getPath().toString(), ResourceException.LOAD, e);
		}

		int height = rawImage.getHeight();
		int width;
		short[] table = null;
		if (height == tileSize)
		{
			width = tileSize;
		}
		else if (height == tileSize * 4)
		{
			width = tileSize * 3;
			table = EXPANSION_TABLE_A;
		}
		else if (height == tileSize * 5)
		{
			width = tileSize * 3;
			table = EXPANSION_TABLE_B;
		}
		else
		{
			throw new ResourceException("invalid autotile-format.");
		}

		int frames = rawImage.getWidth() / width;
		AutotileSet set = new AutotileSet();
		BufferedImage img;
		for (int i = 0; i < frames; i++)
		{
			BufferedImage frameImage = rawImage.getSubimage(i * width, 0, width, height);
			if (width == tileSize)
			{ // Set-Image entspricht dem Frame-Image
				img = frameImage;
			}
			else
			{ // Set-Image wird entsprechend der Expansions-Tabelle angelegt
				img = new BufferedImage(tileSize * 8, tileSize * 6, BufferedImage.TYPE_4BYTE_ABGR);
				Graphics2D g = img.createGraphics();

				for (int j = 0; j < 48; j++)
				{
					drawTile(g, frameImage, table, j, tileSize);
				}
				// return image;
				// img = createAutotileFrame( rawImage.getSubimage(i * width, 0,
				// width, rawImage.getHeight()) , tileSize);
			}
			set.addFrame(img);
		}

		return set;
	}

	private static void drawTile(Graphics2D g, BufferedImage src, short[] table, int id, int tileSize)
	{
		BufferedImage img;
		int cellSize = tileSize / 2;
		int hc = src.getWidth() / tileSize;
		int srcX, srcY, dstX, dstY;
		// Oben links
		srcX = (table[id * 4] % hc) * tileSize;
		srcY = (table[id * 4] / hc) * tileSize;
		dstX = (id % 8) * tileSize;
		dstY = (id / 8) * tileSize;
		img = src.getSubimage(srcX, srcY, cellSize, cellSize);
		g.drawImage(img, dstX, dstY, null);
		// Oben rechts
		srcX = (table[id * 4 + 1] % hc) * tileSize + cellSize;
		srcY = (table[id * 4 + 1] / hc) * tileSize;
		dstX = (id % 8) * tileSize + cellSize;
		dstY = (id / 8) * tileSize;
		img = src.getSubimage(srcX, srcY, cellSize, cellSize);
		g.drawImage(img, dstX, dstY, null);
		// Unten links
		srcX = (table[id * 4 + 2] % hc) * tileSize;
		srcY = (table[id * 4 + 2] / hc) * tileSize + cellSize;
		dstX = (id % 8) * tileSize;
		dstY = (id / 8) * tileSize + cellSize;
		img = src.getSubimage(srcX, srcY, cellSize, cellSize);
		g.drawImage(img, dstX, dstY, null);
		// Unten rechts
		srcX = (table[id * 4 + 3] % hc) * tileSize + cellSize;
		srcY = (table[id * 4 + 3] / hc) * tileSize + cellSize;
		dstX = (id % 8) * tileSize + cellSize;
		dstY = (id / 8) * tileSize + cellSize;
		img = src.getSubimage(srcX, srcY, cellSize, cellSize);
		g.drawImage(img, dstX, dstY, null);
	}

	public static class AutotileSet
	{
		private final ArrayList<BufferedImage> frames = new ArrayList<BufferedImage>(8);

		public void addFrame(BufferedImage frame)
		{
			frames.add(frame);
		}

		public int frameCount()
		{
			return frames.size();
		}

		public BufferedImage getFrame(int index)
		{
			return frames.get(index);
		}

		public BufferedImage getTileFromFrame(int frame, int index)
		{
			BufferedImage image = frames.get(frame);
			return image.getSubimage((index % 8) * (image.getWidth() / 8), (index / 8) * (image.getWidth() / 8),
					image.getWidth() / 8, image.getWidth() / 8);
		}
	}

//	public static void main(String[] args) throws ResourceException
//	{
		// for(int i = 0; i < EXPANSION_TABLE.length; i++)
		// {
		// if (i % 16 == 0) System.out.println();
		// System.out.print(String.format("%2d, ", EXPANSION_TABLE[i]));
		//
		// }

//		Resource res = new ResourceManager.Resource(ResourceManager.KEY_GRAPHICS,
//				Paths.get("../TestWorkspace/Test/graphics/autotiles/Strasse.png"));
//
//		AutotileSet autotileSet = createAutotileSet(res, 32);
//		
//		showAutotileSet(autotileSet);
//	}
	
//	@SuppressWarnings({ "serial" })
//	private static void showAutotileSet(final AutotileSet autotileSet)
//	{
//		JFrame frame = new JFrame("Autotile-Expansion");
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//		JPanel panel = new JPanel()
//		{
//			@Override
//			protected void paintComponent(Graphics g)
//			{
//				g.setColor(Color.BLACK);
//				g.fillRect(0, 0, getWidth(), getHeight());
//
//				g.drawImage(autotileSet.getFrame(0), 0, 0, this);
//			}
//		};
//		panel.setPreferredSize(new Dimension(256, 192));
//		frame.add(panel);
//
//		frame.pack();
//		frame.setLocationRelativeTo(null);
//		frame.setVisible(true);
//	}
}
