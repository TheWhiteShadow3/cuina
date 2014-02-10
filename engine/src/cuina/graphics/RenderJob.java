package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.Color;

/**
 * Rendert Zeichenaufträge. GL-Zugriffe können nicht im Event-Thread durchgeführt werden.
 * Daher wird lediglich ein RenderJob erstellt, der später im Game-Thread ausgeführt wird.
 * @author TheWhiteShadow
 */
public class RenderJob
{
	private static final int CLEAR 			= 0;
	private static final int POINT 			= 1;
	private static final int LINE 			= 2;
	private static final int RECT_OUTLINE 	= 3;
	private static final int RECT_FILL 		= 4;
	private static final int IMAGE 			= 5;
	private static final int TEXT 			= 6;
	private static final int VIEW 			= 7;
	
	private final Image owner;
	private Color color;
//	private Font font;
	private int blendMode;
	private int type;
	
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private Object data;
	
	private RenderJob(Image owner)
	{
		this.owner = owner;
		this.color = new Color(owner.getColor());
//		this.font = owner.getFont();
		this.blendMode = owner.getBlendMode();
	}
	
	public static RenderJob clear(Image owner)
	{
		RenderJob job = new RenderJob(owner);
		job.type = CLEAR;
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public static RenderJob addView(Image owner, View view)
	{
		RenderJob job = new RenderJob(owner);
		job.type = VIEW;
		job.data = view;
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public static RenderJob addText(Image owner, int x, int y, int width, String text, int align)
	{
		RenderJob job = new RenderJob(owner);
		job.type = TEXT;
		job.x1 = x;
		job.y1 = y;
		job.x2 = width;
		job.y2 = align;
		job.data = text;
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public static RenderJob addImage(Image owner, int x, int y, Image image)
	{
		RenderJob job = new RenderJob(owner);
		job.type = IMAGE;
		job.x1 = x;
		job.y1 = y;
		job.data = image;
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public static RenderJob addLine(Image owner, int x1, int y1, int x2, int y2)
	{
		RenderJob job = new RenderJob(owner);
		job.type = LINE;
		job.x1 = x1;
		job.y1 = y1;
		job.x2 = x2;
		job.y2 = y2;
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public static RenderJob addRectangle(Image owner, int x1, int y1, int x2, int y2, boolean fill)
	{
		RenderJob job = new RenderJob(owner);
		job.type = fill ? RECT_FILL : RECT_OUTLINE;
		job.x1 = x1;
		job.y1 = y1;
		job.x2 = x2;
		job.y2 = y2;
		Graphics.renderJobs.add(job);
		return job;
	}

	public static RenderJob addView(Image owner, int viewID, int x, int y)
	{
		RenderJob job = new RenderJob(owner);
		job.type = VIEW;
		job.x1 = x;
		job.y1 = y;
		job.data = Graphics.VIEWS.get(viewID);
		Graphics.renderJobs.add(job);
		return job;
	}
	
	public void render()
	{
		if (Thread.currentThread() != Graphics.getGraphicThread()) return;
		
		Texture tex = owner.getTexture();
		if (tex == null) return;
		
		Graphics.bindFBO(tex);
		Graphics.setShader(null);
		Graphics.bindShader();
		
		if (type == VIEW)
		{
			renderView();
			return;
		}
		prepareViewport(tex);
		
		switch(type)
		{
			case CLEAR: glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); break;
			case POINT: renderPoint(); break;
			case LINE: renderLine(); break;
			case RECT_OUTLINE: renderOutlinedRect(); break;
			case RECT_FILL: renderFilledRect(); break;
			case IMAGE: renderImage(); break;
			case TEXT: renderText(); break;
		}
	}
	
	private void prepareViewport(Texture tex)
	{
		glViewport(0, 0, tex.getSourceWidth(), tex.getSourceHeight());
		
		GLCache.setMatrix(GL_PROJECTION);
		glLoadIdentity();

		/*
		 *  Da der Y-Wert nach oben zeigt, wird das Bild vertikal gespiegelt.
		 *  Beim Zeichnen auf eine Textur darf keine Spieglung erfolgen.
		 *  Daher sind bottom und top vertauscht.
		 *  Culling muss aus sein, da hierbei die Normalen falsch rum zeigen.
		 */
		// left, right, bottom, top, Zfar, Znear
		glOrtho(0, tex.getSourceWidth(), 0, tex.getSourceHeight(), -500, 500);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		
		GLCache.setMatrix(GL_MODELVIEW);
		glLoadIdentity();
	}

	@Deprecated
	private void renderText()
	{	// x2 enthält die gewünschte Textbreite und y2 das Alignment
		throw new UnsupportedOperationException();
//		BitmapFont bmpFont = BitmapFont.getBitmapFont(font);
//		char c;
//		for(int i = 0; i < text.length(); i++)
//		{
//			c = text.charAt(i);
//			image = bmpFont.getCharacterImage(c);
//			image.setColor(color);
//			image.setBlendMode(blendMode);
//			renderImage();
//			
//			x1 += bmpFont.getWidth(c);
//		}
	}

	private void renderPoint()
	{
        glDisable(GL_TEXTURE_2D);
        
        GLCache.setBlendMode(blendMode);
        GLCache.setColor(color);
        
		glBegin(GL_POINT);
        {
            glVertex2i(x1, y1);
        }
        glEnd();
	}
	
	private void renderImage()
	{
		Image.IMAGE_MATRIX.clear();
		Image.IMAGE_MATRIX.setPosition(x1, y1);
		
		Image.IMAGE_MATRIX.pushTransformation();
		Image.renderImage((Image) data);
		Image.IMAGE_MATRIX.popTransformation();
	}
	
	private void renderLine()
	{
		glDisable(GL_TEXTURE_2D);

		GLCache.setBlendMode(blendMode);
		GLCache.setColor(color);

		glBegin(GL_LINES);
		{
			glVertex2i(x1, y1);
			glVertex2i(x2, y2);
		}
		glEnd();

		glEnable(GL_TEXTURE_2D);
	}

	private void renderOutlinedRect()
	{
		x1++; y1++;
		glDisable(GL_TEXTURE_2D);

		GLCache.setBlendMode(blendMode);
		GLCache.setColor(color);

		glBegin(GL_LINES);
		{
			glVertex2i(x1, y1);
			glVertex2i(x2, y1);

			glVertex2i(x2, y1);
			glVertex2i(x2, y2);

			glVertex2i(x1, y2);
			glVertex2i(x2, y2);

			glVertex2i(x1, y1);
			glVertex2i(x1, y2);
		}
		glEnd();

		glEnable(GL_TEXTURE_2D);
	}
	
	private void renderFilledRect()
	{
		glDisable(GL_TEXTURE_2D);

		GLCache.setBlendMode(blendMode);
		GLCache.setColor(color);

		glBegin(GL_QUADS);
		{
			glVertex2f(x1, y1);
			glVertex2f(x1, y2);
			glVertex2f(x2, y2);
			glVertex2f(x2, y1);
		}
		glEnd();

		glEnable(GL_TEXTURE_2D);
	}
	
	private void renderView()
	{
		View view = (View) data;
		Texture tex = owner.getTexture();
		
		view.port.set(x1, y1, tex.getSourceWidth(), tex.getSourceHeight());
		view.flipY = !view.flipY;
		view.draw();
		view.flipY = !view.flipY;
	}

	@Override
	public String toString()
	{
		return "RenderJob: " + type;
	}
}