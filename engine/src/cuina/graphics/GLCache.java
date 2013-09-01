package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * Ein Cache um Zustände von OPen-GL in der Anwendung zu speichern um die nativen Aufrufe zu minimieren.
 * @author TheWhiteShadow
 */
public class GLCache
{
	private static int matrixMode;
	private static ReadableColor color = Color.WHITE;
	private static int blendMode;
	private static Texture currentTexture;
	
	public static void setMatrix(int mode)
	{
		if (matrixMode == mode) return;
		
		matrixMode = mode;
		glMatrixMode(mode);
	}
	
	public static void setColor(ReadableColor color)
	{
		if (GLCache.color.equals(color)) return;
		
		GLCache.color = color;
		glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
	}
	
	public static void setColor(ReadableColor color, int alpha)
	{
		Color c = new Color(color);
		c.setAlpha(alpha);
		
		setColor(c);
	}
	
	/**
	 * Setzt den Blendmodus nach den Werten aus {@link Image}
	 * @param mode
	 */
	public static void setBlendMode(int mode)
	{
		if (blendMode == mode) return;

		blendMode = mode;
		switch (mode)
		{
			case Image.COMPOSITE_NOCHANGE:
				break;
			case Image.COMPOSITE_NORMAL:
				glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
				GL14.glBlendEquation(GL14.GL_FUNC_ADD);
				break;
			case Image.COMPOSITE_ADD:
				glBlendFunc(GL_ONE, GL_ONE);
				GL14.glBlendEquation(GL14.GL_FUNC_ADD);
				break;
			case Image.COMPOSITE_SUB:
				glBlendFunc(GL_ONE, GL_ONE);
				GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);
				break;
			case Image.COMPOSITE_MUL:
				glBlendFunc(GL_DST_COLOR, GL_ZERO); // BlendEquation ist hier egal
				break;
			case Image.COMPOSITE_ABSOLUT:
				glBlendFunc(GL_SRC_ALPHA, GL_SRC_ALPHA);
				// GL14.glBlendEquation(GL14.GL_FUNC_ADD);
				break;
			case Image.COMPOSITE_OVERLAY:
				glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
				GL14.glBlendEquation(GL14.GL_FUNC_ADD);
				break;
		}
	}
	
	public static void bindTexture(Texture tex)
	{
		if (currentTexture == tex) return;
		
		if (tex != null)
			GL11.glBindTexture(tex.target, tex.textureID);
		else
			GL11.glBindTexture(currentTexture.target, 0);
		currentTexture = tex;
	}
	
	public static void restore()
	{
		glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		glMatrixMode(matrixMode);
		currentTexture = null;
	}
}
