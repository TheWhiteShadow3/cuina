package cuina.widget;

import cuina.Input;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Color;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.model.ToggleButtonModel;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;

public class Menu extends Widget implements CuinaWidget
{
	public static final StateKey STATE_SELECTED = StateKey.get("selected");

	private int columns;
	private String[] commands;
	private MenuItem[] buttons = new MenuItem[0];
	private int hGap;
	private int vGap;
	private int index = -1;
	private String name;
	private WidgetEventHandler handler;
	private CycleTintAnimator animator;

	public Menu(String name, int columns)
	{
		this.name = name;
		this.columns = columns;
		setTheme("/menu");
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setEventHandler(WidgetEventHandler handler)
	{
		this.handler = handler;
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

		updateAnimator(this.index, index);
		this.index = index;
		if (this.index != -1)
		{
			buttons[index].getModel().setSelected(true);
			requestKeyboardFocus();
		}
		
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
//        System.out.println("Klick: " + getIndex());
    	if (handler == null) return;
    	
    	handler.handleEvent(name, this, getIndex());
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
    protected boolean handleEvent(Event ev)
    {
		if (ev.isKeyPressedEvent())
		{
			if (!ev.isKeyRepeated())
			{
	            if (ev.getKeyCode() == Event.KEY_RETURN || Input.isPressed(Input.OK))
	            {
	                fireActionPerformed();
	                return true;
	            }
				
	            if (ev.getKeyCode() == Event.KEY_ESCAPE || Input.isPressed(Input.CANCEL))
	            {
	            	setIndex(-1);
	                fireActionPerformed();
	                return true;
	            }
			}
            
			if (ev.getKeyCode() == Event.KEY_UP && (this.index > 0 || !ev.isKeyRepeated()))
			{
				int newIndex = this.index - columns;
				setIndex((newIndex + buttons.length) % buttons.length);
				return true;
			}
			
			if (ev.getKeyCode() == Event.KEY_DOWN && (this.index < buttons.length-columns || !ev.isKeyRepeated()))
			{
				int newIndex = this.index + columns;
				setIndex(newIndex % buttons.length);
				return true;
			}
			
			if (ev.getKeyCode() == Event.KEY_LEFT && (this.index > 0 || !ev.isKeyRepeated()))
			{
				int newIndex = this.index - 1;
				setIndex((newIndex + buttons.length) % buttons.length);
				return true;
			}
			
			if (ev.getKeyCode() == Event.KEY_RIGHT && (this.index < buttons.length-1 || !ev.isKeyRepeated()))
			{
				int newIndex = this.index + 1;
				setIndex(newIndex % buttons.length);
				return true;
			}
		}
		
		return super.handleEvent(ev);
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
        for(int i = 0; i < buttons.length; i++)
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
        protected boolean handleEvent(Event ev)
        {
            if (ev.isMouseEvent() && isMouseInside(ev))
            {
                parent.setIndex(index);
//                setMouseCursor(parent.mouseCursorLink);
                
                if (ev.getType() == Event.Type.MOUSE_BTNDOWN)
                {
                    parent.fireActionPerformed();
                }
            }
 
            if (super.handleEvent(ev)) return true;
 
            return false;
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
            if (isSelected())
                super.setPressed(pressed);
        }
 
        @Override protected void buttonAction() {}
    }
}
