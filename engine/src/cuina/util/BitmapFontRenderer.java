package cuina.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class BitmapFontRenderer
{
	private static final Graphics2D GRAPHICS = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();
	
	private BitmapFontRenderer() {}
	
	public static BufferedImage renderFont(Font font, boolean shadow, int[] widths)
	{
		FontMetrics fm = GRAPHICS.getFontMetrics(font);
		
		int ascent  = fm.getMaxAscent();
		int descent = fm.getMaxDescent();
		int advance = fm.charWidth('W'); // width of widest char, more reliable than getMaxAdvance();
		int leading = fm.getLeading();

		// calculate size of 10x10 character grid
		int fontHeight = ascent + descent + (leading / 2);
		int fontWidth = advance;
		int maxCharSize = Math.max(fontHeight, fontWidth);
		if (shadow) maxCharSize++;
		
		BufferedImage FontImage = new BufferedImage(maxCharSize * 16, maxCharSize * 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = FontImage.createGraphics();
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		int x, y;
		for(int i = 32; i < 127; i++)
		{
			char c = (char)i;

			g.setFont(font);
			
			x = maxCharSize * (i % 16);
			y = maxCharSize * (i / 16);
			
			if (widths != null)
			{
				widths[i] = fm.charWidth(i);
			}
			
			if (shadow)
			{
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(c), x+1, y + ascent-1);
				g.setColor(Color.WHITE);
			}
			g.drawString(String.valueOf(c), x, y + ascent);
		}
		for(int i = 160; i < 256; i++)
		{
			char c = (char)i;

			g.setFont(font);
			
			x = maxCharSize * (i % 16);
			y = maxCharSize * (i / 16);
			
			if (widths != null)
			{
				widths[i] = fm.charWidth(i);
			}
			
			if (shadow)
			{
				g.setColor(Color.BLACK);
				g.drawString(String.valueOf(c), x+1, y + ascent-1);
				g.setColor(Color.WHITE);
			}
			g.drawString(String.valueOf(c), x, y + ascent);
		}
		
		return FontImage;
	}
	
	@SuppressWarnings("serial")
	public static void main(String[] arg)
	{
		JFrame frame = new JFrame();
		frame.setBounds(100, 100, 600, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(new JPanel()
		{

			@Override
			protected void paintComponent(Graphics g)
			{
				g.setColor(Color.RED);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				Font font = new Font("Times New Roman", 0, 20);
				
				g.drawImage(renderFont(font, true, null), 0, 0, this);
			}
			
		});
		
		frame.setVisible(true);
	}
}
