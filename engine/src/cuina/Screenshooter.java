package cuina;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import cuina.graphics.Graphics;

public class Screenshooter extends Thread
{
	private ByteBuffer buffer;
	
	public Screenshooter(ByteBuffer buffer)
	{
		this.buffer = buffer;
	}
	
	@Override
	public void run()
	{
		int width = Graphics.getWidth();
		int height = Graphics.getHeight();
		
		File file = new File("screenshot.png");
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		 
		for(int x = 0; x < width; x++)
		 	for(int y = 0; y < height; y++)
		 	{
		 		int i = (x + (width * y)) * 3;
		 		int r = buffer.get(i) & 0xFF;
		 		int g = buffer.get(i + 1) & 0xFF;
		 		int b = buffer.get(i + 2) & 0xFF;
		 		image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		 	}
		 
		try 
		{
			ImageIO.write(image, format, file);
		}
		catch (IOException e) { e.printStackTrace(); }
	}
}
