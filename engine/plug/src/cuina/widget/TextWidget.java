package cuina.widget;

import de.matthiasmann.twl.Alignment;
import de.matthiasmann.twl.AnimationState;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;
import de.matthiasmann.twl.renderer.Font;
import de.matthiasmann.twl.renderer.FontCache;
import de.matthiasmann.twl.utils.TextUtil;

/**
 * @author TheWhiteShadow
 */
public class TextWidget extends CuinaWidget
{
	public static final StateKey STATE_HOVER = StateKey.get("hover");
	public static final StateKey STATE_TEXT_CHANGED = StateKey.get("textChanged");
	public static final StateKey STATE_TEXT_SELECTION = StateKey.get("textSelection");

	private static final int NOT_CACHED = -1;

	private Font font;
	private FontCache cache;
	private CharSequence text;
	private int cachedTextWidth = NOT_CACHED;
	private int numTextLines;
	private boolean useCache = true;
	private boolean cacheDirty;
	private Alignment alignment = Alignment.TOPLEFT;

	public TextWidget()
	{
		this(null, false);
	}

	public TextWidget(AnimationState animState, boolean inherit)
	{
		super(animState, inherit);
		this.text = "";
	}

	public Font getFont()
	{
		return font;
	}

	public void setFont(Font font)
	{
		if (cache != null)
		{
			cache.destroy();
			cache = null;
		}
		this.font = font;
		this.cachedTextWidth = NOT_CACHED;
		if (useCache)
		{
			this.cacheDirty = true;
		}
	}

	/**
	 * Sets a new text to be displayed. If CharSequence changes it's content
	 * this function must be called again or correct rendering can't be
	 * guranteed.
	 * 
	 * @param text
	 *            The CharSequence to display
	 */
	protected void setCharSequence(CharSequence text)
	{
		if (text == null) { throw new NullPointerException("text"); }
		this.text = text;
		this.cachedTextWidth = NOT_CACHED;
		this.numTextLines = TextUtil.countNumLines(text);
		this.cacheDirty = true;
		getAnimationState().resetAnimationTime(STATE_TEXT_CHANGED);
	}

	protected CharSequence getCharSequence()
	{
		return text;
	}

	public boolean hasText()
	{
		return numTextLines > 0;
	}

	public boolean isMultilineText()
	{
		return numTextLines > 1;
	}

	public int getLineCount()
	{
		return numTextLines;
	}

	public Alignment getAlignment()
	{
		return alignment;
	}

	public void setAlignment(Alignment alignment)
	{
		if (alignment == null) { throw new NullPointerException("alignment"); }
		if (this.alignment != alignment)
		{
			this.alignment = alignment;
			this.cacheDirty = true;
		}
	}

	public boolean isCached()
	{
		return useCache;
	}

	public void setCache(boolean cache)
	{
		if (this.useCache != cache)
		{
			this.useCache = cache;
			this.cacheDirty = true;
		}
	}

	protected void applyThemeTextWidget(ThemeInfo themeInfo)
	{
		setFont(themeInfo.getFont("font"));
		setAlignment(themeInfo.getParameter("textAlignment", Alignment.TOPLEFT));
	}

	@Override
	protected void applyTheme(ThemeInfo themeInfo)
	{
		super.applyTheme(themeInfo);
		applyThemeTextWidget(themeInfo);
	}

	@Override
	public void destroy()
	{
		if (cache != null)
		{
			cache.destroy();
			cache = null;
		}
		super.destroy();
	}

	protected int computeTextX()
	{
		int x = getInnerX();
		int pos = alignment.getHPosition();
		if (pos > 0) { return x + (getInnerWidth() - computeTextWidth()) * pos / 2; }
		return x;
	}

	protected int computeTextY()
	{
		int y = getInnerY();
		int pos = alignment.getVPosition();
		if (pos > 0) { return y + (getInnerHeight() - computeTextHeight()) * pos / 2; }
		return y;
	}

	@Override
	protected void paintWidget(GUI gui)
	{
		paintLabelText(getAnimationState());
	}

	protected void paintLabelText(de.matthiasmann.twl.renderer.AnimationState animState)
	{
		if (cacheDirty)
		{
			updateCache();
		}
		if (hasText() && font != null)
		{
			int x = computeTextX();
			int y = computeTextY();

			paintTextAt(animState, x, y);
		}
	}

	protected void paintTextAt(de.matthiasmann.twl.renderer.AnimationState animState, int x, int y)
	{
		if (cache != null)
		{
			cache.draw(animState, x, y);
		}
		else if (numTextLines > 1)
		{
			font.drawMultiLineText(animState, x, y, text, computeTextWidth(), alignment.getFontHAlignment());
		}
		else
		{
			font.drawText(animState, x, y, text);
		}
	}

	protected void paintWithSelection(AnimationState animState, int start, int end)
	{
		paintWithSelection(animState, start, end, 0, text.length(), computeTextY());
	}

	protected void paintWithSelection(AnimationState animState, int start, int end, int lineStart, int lineEnd, int y)
	{
		if (cacheDirty)
		{
			updateCache();
		}
		if (hasText() && font != null)
		{
			int x = computeTextX();

			start = limit(start, lineStart, lineEnd);
			end = limit(end, lineStart, lineEnd);

			if (start > lineStart)
			{
				x += font.drawText(animState, x, y, text, lineStart, start);
			}
			if (end > start)
			{
				animState.setAnimationState(STATE_TEXT_SELECTION, true);
				x += font.drawText(animState, x, y, text, start, end);
				animState.setAnimationState(STATE_TEXT_SELECTION, false);
			}
			if (end < lineEnd)
			{
				font.drawText(animState, x, y, text, end, lineEnd);
			}
		}
	}

	private static int limit(int value, int min, int max)
	{
		if (value < min) { return min; }
		if (value > max) { return max; }
		return value;
	}

	@Override
	public int getPreferredInnerWidth()
	{
		int prefWidth = super.getPreferredInnerWidth();
		if (hasText() && font != null)
		{
			prefWidth = Math.max(prefWidth, computeTextWidth());
		}
		return prefWidth;
	}

	@Override
	public int getPreferredInnerHeight()
	{
		int prefHeight = super.getPreferredInnerHeight();
		if (hasText() && font != null)
		{
			prefHeight = Math.max(prefHeight, computeTextHeight());
		}
		return prefHeight;
	}

	public int computeRelativeCursorPositionX(int charIndex)
	{
		return computeRelativeCursorPositionX(0, charIndex);
	}

	public int computeRelativeCursorPositionX(int startIndex, int charIndex)
	{
		if (font != null && charIndex > startIndex) { return font.computeTextWidth(text, startIndex, charIndex); }
		return 0;
	}

	public int computeTextWidth()
	{
		if (font != null)
		{
			if (cachedTextWidth == NOT_CACHED || cacheDirty)
			{
				if (numTextLines > 1)
				{
					cachedTextWidth = font.computeMultiLineTextWidth(text);
				}
				else
				{
					cachedTextWidth = font.computeTextWidth(text);
				}
			}
			return cachedTextWidth;
		}
		return 0;
	}

	public int computeTextHeight()
	{
		if (font != null) { return Math.max(1, numTextLines) * font.getLineHeight(); }
		return 0;
	}

	private void updateCache()
	{
		cacheDirty = false;
		if (useCache && hasText() && font != null)
		{
			if (numTextLines > 1)
			{
				cache = font.cacheMultiLineText(cache, text, font.computeMultiLineTextWidth(text),
						alignment.getFontHAlignment());
			}
			else
			{
				cache = font.cacheText(cache, text);
			}
			if (cache != null)
			{
				cachedTextWidth = cache.getWidth();
			}
		}
		else
		{
			destroy();
		}
	}
}
