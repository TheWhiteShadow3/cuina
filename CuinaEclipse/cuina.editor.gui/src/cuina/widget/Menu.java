package cuina.widget;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.ToggleButtonModel;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;

public class Menu extends Widget
{
	public static final StateKey STATE_SELECTED = StateKey.get("selected");

	private int columns = 1;
	private String[] commands;
	private MenuItem[] buttons = new MenuItem[0];
	private int hGap;
	private int vGap;
	private int index = -1;
//	private String key;
//	private WidgetEventHandler handler;
//	private CycleTintAnimator animator;

	public Menu()
	{
		setTheme("/menu");
	}

//	@Override
//	public String getKey()
//	{
//		return key;
//	}
//
//	@Override
//	public void setEventHandler(WidgetEventHandler handler)
//	{
//		this.handler = handler;
//	}


	public void setColumns(int columns)
	{
		this.columns = columns;
	}
	
	public void setCommands(String[] commands)
	{
		this.commands = commands;

		disposeLabels();
		this.buttons = new MenuItem[commands.length];
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i] = new MenuItem(commands[i], this, i);
			add(buttons[i]);
		}
		if (buttons.length > 0) setIndex(0);
	}

	public int getItemCount()
	{
		return buttons.length;
	}

	public String getItem(int index)
	{
		return commands[index];
	}

	public void setGaps(int hGap, int vGap)
	{
		this.hGap = hGap;
		this.vGap = vGap;
	}

	public int getHGap()
	{
		return hGap;
	}

	public int getVGap()
	{
		return vGap;
	}

	public int getIndex()
	{
		return index;
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return buttons.length / columns;
	}

	public void setIndex(int index)
	{
		if (this.index == index) return;
		if (index >= buttons.length) throw new IndexOutOfBoundsException();

		if (this.index != -1) buttons[this.index].getModel().setSelected(false);

		this.index = index;
		if (this.index != -1)
		{
			buttons[index].getModel().setSelected(true);
			requestKeyboardFocus();
		}

	}

	@Override
	protected void applyTheme(ThemeInfo themeInfo)
	{
		super.applyTheme(themeInfo);
		hGap = themeInfo.getParameter("hGap", 0);
		vGap = themeInfo.getParameter("vGap", 0);
		ParameterMap map = themeInfo.getParameterMap("animation");
		if (map != null)
		{
			Color color = map.getParameter("color", Color.LIGHTBLUE);
			int duration = map.getParameter("duration", 1000);
		}
	}

	@Override
	protected void layout()
	{
		int c = 0;
		int columnWidth = (getInnerWidth() - (columns - 1) * hGap) / columns;
		int x = getInnerX();
		int y = getInnerY();
		MenuItem item;
		for (int i = 0; i < buttons.length; i++)
		{
			item = buttons[i];
			item.setPosition(x, y);
			item.setSize(columnWidth, item.getPreferredHeight());
			if (c >= columns - 1)
			{
				x = getInnerX();
				y += item.getPreferredHeight() + vGap;
				c = 0;
			}
			else
			{
				x += columnWidth + hGap;
				c++;
			}
		}
		if (y > getHeight())
			setInnerSize(getInnerWidth(), y);
	}

	@Override
	public Widget getWidgetAt(int x, int y)
	{
		for (int i = 0; i < buttons.length; i++)
		{
			if (buttons[i].isInside(x, y)) return buttons[i];
		}
		return this;
	}

	@Override
	public int getPreferredInnerHeight()
	{
		int temp, maxHeight = 0;

		validateLayout();
		for (int i = 0, n = buttons.length / columns; i < n; i++)
		{
			temp = buttons[i].getPreferredHeight();
			if (temp > maxHeight) maxHeight = temp;
		}
		return maxHeight;
	}

	@Override
	public int getPreferredWidth()
	{
		return computeSize(getMinWidth(), super.getPreferredWidth(), getMaxWidth());
	}

	private void disposeLabels()
	{
		if (buttons == null) return;

		MenuItem l;
		for (int i = 0; i < buttons.length; i++)
		{
			l = buttons[i];
			if (l != null) l.destroy();
		}
		removeAllChildren();
	}

	private static class MenuItem extends Button
	{
		private Menu parent;
		private int index;

		public MenuItem(String text, Menu parent, int index)
		{
			super(new SelectableButtonModel());
			setTheme("menuitem");
			setText(text);
			setFocusKeyEnabled(false);
			this.parent = parent;
			this.index = index;
		}

		@Override
		protected void disarm()
		{
			super.disarm();
			getModel().setSelected(false);
		}
	}

	private static class SelectableButtonModel extends ToggleButtonModel
	{
		@Override
		public void setPressed(boolean pressed)
		{
			if (isSelected()) super.setPressed(pressed);
		}

		@Override
		protected void buttonAction()
		{}
	}
}
