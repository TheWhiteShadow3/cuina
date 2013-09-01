package cuina.gl;

import cuina.resource.ResourceException;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class TestMain
{
	static Image image;
	static GLPanel panel;
	
	public static void main(String[] args) throws LWJGLException
	{
		LWJGL.init(new File("lib/lwjgl/native/windows").getAbsolutePath());
		
		final org.eclipse.swt.widgets.Display display = new org.eclipse.swt.widgets.Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		panel = new GLPanel(shell, SWT.NONE, LWJGL.NO_BACKGROUND);
		panel.addPaintListener(getPaintListener());
		panel.getGLCanvas().addListener(SWT.Resize, getResizeListener());
		
		try
		{
			image = new Image(panel.canvas, "oppai.jpg");
		}
		catch (ResourceException | LWJGLException e)
		{
			e.printStackTrace();
		}
		
		shell.setSize(1024, 768);
		shell.open();

		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}
	
	private static PaintListener getPaintListener()
	{
		return new PaintListener()
		{
			@Override
			public void paint(GC gc)
			{
				System.out.println("Paint");
				
//				gc.setColor(Color.BLACK);
//				gc.drawImage(image, 15, 15);
				gc.drawImage(image, 0, 0, 2048, 1024,  0, 0, 2048, 1024);
				
				Rectangle bounds = panel.getBounds();
				gc.setColor(Color.RED);
				gc.drawLine(100, 100, bounds.width - 100, bounds.height - 100);
				gc.drawLine(bounds.width - 100, 100, 100, bounds.height - 100);
//				drawTorus(1, 1.9f + ((float) Math.sin((0.004f * 0))), 15, 15);
			}
		};
	}
	
	private static Listener getResizeListener()
	{
		return new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				panel.canvas.redraw();
			}
		};
	}
	
	static void drawTorus(float r, float R, int nsides, int rings)
	{
		float ringDelta = 2.0f * (float) Math.PI / rings;
		float sideDelta = 2.0f * (float) Math.PI / nsides;
		float theta = 0.0f, cosTheta = 1.0f, sinTheta = 0.0f;
		for (int i = rings - 1; i >= 0; i--)
		{
			float theta1 = theta + ringDelta;
			float cosTheta1 = (float) Math.cos(theta1);
			float sinTheta1 = (float) Math.sin(theta1);
			GL11.glBegin(GL11.GL_QUAD_STRIP);
			float phi = 0.0f;
			for (int j = nsides; j >= 0; j--)
			{
				phi += sideDelta;
				float cosPhi = (float) Math.cos(phi);
				float sinPhi = (float) Math.sin(phi);
				float dist = R + r * cosPhi;
				GL11.glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
				GL11.glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
				GL11.glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
				GL11.glVertex3f(cosTheta * dist, -sinTheta * dist, r * sinPhi);
			}
			GL11.glEnd();
			theta = theta1;
			cosTheta = cosTheta1;
			sinTheta = sinTheta1;
		}
	}
}
