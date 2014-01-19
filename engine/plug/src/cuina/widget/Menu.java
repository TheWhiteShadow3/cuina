package cuina.widget;

import cuina.event.Event;
import cuina.input.DirectionalControl;
import cuina.input.Input;
import cuina.widget.model.ToggleButtonModel;

import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;

public class Menu extends CuinaWidget
{
//	public static final StateKey STATE_SELECTED = StateKey.get("selected");
	public static final Event INDEX_CHANGED = Event.getEvent("cuina.widget.IndexChanged");

	private String controlDirection	= "a1";
	private String controlOK		= "c2";
	private String controlCancel	= "c3";
	
	private int columns;
	private String[] commands;
	private MenuItem[] buttons = new MenuItem[0];
	private int hGap;
	private int vGap;
	private int index = -1;
	private String name;
	private CycleTintAnimator animator;

	public Menu(String name, int columns)
	{
		this.name = name;
		this.columns = columns;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setCommands(String[] commands)
	{
		this.commands = commands;

		disposeLabels();
		Listener handler = getHandler();
		
		this.buttons = new MenuItem[commands.length];
		for (int i = 0; i < buttons.length; i++)
		{
			buttons[i] = new MenuItem(commands[i], this, i);
			buttons[i].getModel().addListener(handler);
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

		updateAnimator(this.index, index);
		this.index = index;
		if (this.index != -1)
		{
			buttons[index].getModel().setSelected(true);
			requestKeyboardFocus();
		}
		Integer i = Integer.valueOf(this.index);
		testTriggers(INDEX_CHANGED, i, i);
	}
	
	private void updateAnimator(int lastIndex, int newIndex)
	{
		if (animator == null) return;
		
		if (lastIndex != -1)
		{
			buttons[lastIndex].setTintAnimator(null);
			animator.stop();
		}
		if (newIndex != -1)
		{
			buttons[newIndex].setTintAnimator(animator);
			animator.start();
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
			this.animator = new CycleTintAnimator(this, color, duration);
			updateAnimator(-1, this.index);
		}
	}

	@Override
	protected void layout()
	{
		if (buttons.length == 0) return;
		
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
//		requestKeyboardFocus();
	}

	protected void fireActionPerformed()
	{
// 		System.out.println("Klick: " + getIndex());
		Integer i = Integer.valueOf(index);
		testTriggers(Button.BUTTON_PRESSED, i, i);
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
	protected void updateWidget()
	{
		if (!hasKeyboardFocus())
		{
			setIndex(-1);
			return;
		}
		
		if (Input.isPressed(controlCancel))
		{
			setIndex(-1);
			fireActionPerformed();
			return;
		}

		if (Input.isPressed(controlOK))
		{
			fireActionPerformed();
			return;
		}

		DirectionalControl c = (DirectionalControl) Input.getControl(controlDirection);
		int dir = c.getDirectionSektor(DirectionalControl.DIRECTION_4);
		if (dir == -1) return;
		
		if (c.isPressed())
		{
			int newIndex = index;
			switch(dir)
			{
				// rechts
				case 0: newIndex += 1; break;
				// oben
				case 1: newIndex -= columns; break;
				 // links
				case 2: newIndex -= 1; break;
				 // unten
				case 3: newIndex += columns; break;
			}
			setIndex((newIndex + buttons.length) % buttons.length);
			return;
		}

		if (c.isRepeated())
		{
			int newIndex = index;
			switch(dir)
			{
				// rechts
				case 0: if (this.index < buttons.length-1) newIndex += 1; break;
				// oben
				case 1: if (this.index > columns) newIndex -= columns; break;
				// links
				case 2: if (this.index > 0) newIndex -= 1; break;
				// unten
				case 3: if (this.index < buttons.length-columns) newIndex += columns; break;
			}
			setIndex((newIndex + buttons.length) % buttons.length);
			return;
		}
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
	
	private Listener getHandler()
	{
		return new Listener()
		{
			@Override
			public void handleEvent(WidgetEvent event)
			{
				if (event.type == WidgetEvent.STATE_CHANGED)
				{
					testTriggers(Button.STATE_CHANGED, null, event.value);
				}
				else if (event.type == WidgetEvent.ACTION_PERFORMED)
				{
					fireActionPerformed();
				}
			}
		};
	}

	private static class MenuItem extends Button
	{
		private Menu parent;
		private int index;

		public MenuItem(String text, Menu parent, int index)
		{
			super(null, false, new SelectableButtonModel());
			setTheme("menuitem");
			setText(text);
			setFocusKeyEnabled(false);
			this.parent = parent;
			this.index = index;
		}

		@Override
		protected void updateWidget()
		{
			if (isMouseInside())
			{
				parent.setIndex(index);
			}
			super.updateWidget();
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
		private static final long serialVersionUID = 3065169164447745150L;

		@Override
		public void setPressed(boolean pressed)
		{
			if (isSelected())
				super.setPressed(pressed);
		}

		@Override
		protected void buttonPressed() {}
	}
}
