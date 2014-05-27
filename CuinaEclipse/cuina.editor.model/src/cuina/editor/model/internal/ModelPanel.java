package cuina.editor.model.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class ModelPanel extends Canvas
{
	private static final int FRAME_TIME = 200;
	
	private Image image;
	private int frames;
	private int animations;
	private int frameIndex;
	private int animationIndex;
	private Animator animationThread;
	
	public ModelPanel(Composite parent, int style)
	{
		super(parent, style | SWT.DOUBLE_BUFFERED);
		addPaintListener(getPaintListener());
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image)
	{
		this.image = image;
		redraw();
	}
	
	public void setFrames(int frames)
	{
		this.frames = frames;
		redraw();
	}
	
	public void setAnimations(int animations)
	{
		this.animations = animations;
		redraw();
	}
	
	public void startAnimation()
	{
		if (animationThread != null) return;
		
		this.animationThread = new Animator();
		animationThread.start();
	}
	
	public void stopAnimation()
	{
		if (animationThread == null) return;
		
		animationThread.softStop();
		animationThread = null;
	}
	
	public void setFrameIndex(int frame)
	{
		this.frameIndex = frame;
		redraw();
	}

	public void setAnimationIndex(int animation)
	{
		this.animationIndex = animation;
		redraw();
	}
	
	private PaintListener getPaintListener()
	{
		return new PaintListener()
		{
			@Override
			public void paintControl(PaintEvent e)
			{
				if (image == null) return;
				
				ImageData data = image.getImageData();
				int cw = data.width / frames;
				int ch = data.height / animations;
				int f = frameIndex % frames;
				int a = animationIndex % animations;
				e.gc.drawImage(image, cw*f, ch*a, cw, ch, 0, 0, cw, ch);
			}
		};
	}

	private class Animator extends Thread
	{
		private boolean run;
		private Runnable redrawAction;
		
		private Animator()
		{
			super("Model-Animation-Thread");
			setDaemon(true);
			redrawAction = new Runnable()
			{
				@Override
				public void run() { if (!isDisposed()) redraw(); }
			};
		}

		@Override
		public void run()
		{
			run = true;
			try
			{
				while(run)
				{
					sleep(FRAME_TIME);
					frameIndex = (frameIndex + 1) % frames;
					if (!isDisposed())
						getDisplay().asyncExec(redrawAction);
					else
						run = false;
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				run = false;
			}
		}
		
		public void softStop()
		{
			run = false;
		}
	}
}
