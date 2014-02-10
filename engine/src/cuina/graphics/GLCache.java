package cuina.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;

import cuina.graphics.Image.MultiTexture;

import java.util.List;
import java.util.Objects;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.Color;
import org.lwjgl.util.ReadableColor;

/**
 * Ein Cache um Zustände von Open-GL in der Anwendung zu speichern um die nativen Aufrufe zu minimieren.
 * @author TheWhiteShadow
 */
public class GLCache
{
	private static int matrixMode;
	private static ReadableColor color = Color.WHITE;
	private static int blendMode;
	private static Texture currentTexture;
	private static MultiTexture[] multiTextures = new MultiTexture[4];
	private static Shader currentShader;
	
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
			case Image.COMPOSITE_TEST:
//				glBlend
				glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
				GL14.glBlendEquation(GL14.GL_FUNC_ADD);
				break;
		}
	}
	
	public static void bindShader(Shader shader)
	{
		if (currentShader == shader) return;
		
		shader.bind();
		currentShader = shader;
	}
	
	public static void bindTexture(Texture tex)
	{
		unbindMultiTextures();
		if (currentTexture == tex) return;
		
		if (tex != null)
		{
//			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(tex.target, tex.textureID);
		}
		else
		{
			glDisable(GL_TEXTURE_2D);
//			glBindTexture(currentTexture.target, 0);
		}
		currentTexture = tex;
	}
	
	public static void bindMultiTextures(List<MultiTexture> textures)
	{
		for (int i = 3; i >= 0; i--)
		{
			if (textures.size() > i)
			{
				if (Objects.equals(textures.get(i), multiTextures[i])) continue;
			}
			else
			{
				if (multiTextures[i] == null) continue;
			}
				
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			if (i >= textures.size())
			{
				glBindTexture(multiTextures[i].texture.target, 0);
				glDisable(GL_TEXTURE_2D);
				multiTextures[i] = null;
			}
			else
			{
				MultiTexture mt = textures.get(i);
				glEnable(GL_TEXTURE_2D);
				glBindTexture(mt.texture.target, mt.texture.textureID);
				glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, mt.mixMode);
				multiTextures[i] = mt;
			}
		}
		currentTexture = multiTextures[0].texture;
	}
	
	private static void unbindMultiTextures()
	{
		if (multiTextures[0] == null) return;
		
		for (int i = 3; i >= 1; i--)
		{
			if (multiTextures[i] == null) continue;
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
			glBindTexture(multiTextures[i].texture.target, 0);
			glDisable(GL_TEXTURE_2D);
			multiTextures[i] = null;
		}
		multiTextures[0] = null;
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	public static void restore()
	{
		glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		glMatrixMode(matrixMode);
		bindTexture(null);
	}
	
//	private static int getTarget(int index)
//	{
//		switch(index)
//		{
//			case 0: return GL13.GL_TEXTURE0;
//			case 1: return GL13.GL_TEXTURE1;
//			case 2: return GL13.GL_TEXTURE2;
//			case 3: return GL13.GL_TEXTURE3;
//			default: throw new IllegalArgumentException();
//		}
//	}
}
