package cuina.widget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.Renderer;
import de.matthiasmann.twl.renderer.Texture;

public class PictureWidget extends Widget
{
	Image image;
	String fileURL;
	
	private int x, y;
	private int width, height;
	
	public PictureWidget(String fileURL)
	{
		this.fileURL = fileURL;
	}
	
	@Override
	protected void paintWidget(GUI gui)
	{
		if(fileURL != null)
		{
			if(image == null)
			{
				Renderer renderer = gui.getRenderer();
				try
				{
					Texture texture = renderer.loadTexture(new URL(fileURL), null, null);
					height = texture.getHeight();
					width = texture.getWidth();
					image = texture.getImage(0, 0, width, height, null, false, Texture.Rotation.NONE);
					setSize(width, height);
					setPosition(x, y);
				} catch(MalformedURLException e)
				{
					e.printStackTrace();
				} catch(IOException e)
				{
					e.printStackTrace();
				}
			}
			
			image.draw(null, super.getX(), super.getY());
		}
		
		
    }
}
