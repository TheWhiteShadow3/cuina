package cuina.editor.ui.selection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Ändert den Mauszeiger innerhalb des Auswahlrechtecks und an dessem Rand zu
 * Pfeilen.
 * <p>
 * Die Klasse dient nur dem User-Feedback für den {@link MoveSelectionMode} Die
 * Auswahl selbst, wird hiermit nicht beeinflusst.
 * </p>
 * 
 * @author TheWhiteShadow
 */
public class HighlightingSelectionMode implements SelectionMode
{
	private Control control;
	private int range;

	public HighlightingSelectionMode(Control control, int borderRange)
	{
		this.control = control;
		this.range = borderRange;
	}

	@Override
	public boolean move(SelectionManager handler, int x, int y)
	{
		for (int i = handler.getSelectionCount() - 1; i >= 0; i--)
		{
			Selection sel = handler.getSelection(i);

			if (sel != null && sel.contains(x, y))
			{
				if (sel.getX() > x - range)
				{
					if (sel.getY() > y - range)
					{
						setCursor(SWT.CURSOR_SIZENWSE);
						return false;
					}
					else if (sel.getY() + sel.getHeight() < y + range)
					{
						setCursor(SWT.CURSOR_SIZENESW);
						return false;
					}
					else
					{
						setCursor(SWT.CURSOR_SIZEWE);
						return false;
					}
				}
				else if (sel.getX() + sel.getWidth() < x + range)
				{
					if (sel.getY() > y - range)
					{
						setCursor(SWT.CURSOR_SIZENESW);
						return false;
					}
					else if (sel.getY() + sel.getHeight() < y + range)
					{
						setCursor(SWT.CURSOR_SIZENWSE);
						return false;
					}
					else
					{
						setCursor(SWT.CURSOR_SIZEWE);
						return false;
					}
				}
				else
				{
					if (sel.getY() > y - range)
					{
						setCursor(SWT.CURSOR_SIZENS);
						return false;
					}
					else if (sel.getY() + sel.getHeight() < y + range)
					{
						setCursor(SWT.CURSOR_SIZENS);
						return false;
					}
					else
					{
						setCursor(SWT.CURSOR_SIZEALL);
						return false;
					}
				}
			}
		}
		setCursor(SWT.CURSOR_ARROW);
		return false;
	}

	@Override
	public void activate(SelectionManager handler, int x, int y)
	{
		move(handler, x, y);
	}

	@Override
	public void deactivate(SelectionManager handler, int x, int y)
	{
		setCursor(SWT.CURSOR_ARROW);
	}

	private void setCursor(int mode)
	{
		control.setCursor(Display.getDefault().getSystemCursor(mode));
	}
}
