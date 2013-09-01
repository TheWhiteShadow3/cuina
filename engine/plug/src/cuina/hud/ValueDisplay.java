package cuina.hud;

import cuina.Context;
import cuina.Game;
import cuina.plugin.LifeCycleAdapter;
import cuina.widget.WidgetContainer;
import cuina.widget.WidgetDescriptor;

import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;

public class ValueDisplay extends LifeCycleAdapter
{
	private WidgetContainer container;
	private Label label;
	private int x;
	private int y;
	private int varIndex;
	private String text;
	
	public ValueDisplay(int x, int y, int varIndex, String text)
	{
		this.x = x;
		this.y = y;
		this.varIndex = varIndex;
		this.text = text;
		this.container = new WidgetContainer(new WidgetDescriptor()
		{
			private static final long serialVersionUID = -7275575637956156111L;
			
			@Override
			public String getTheme() {return null;}
			
			@Override
			public Widget createRoot()
			{
				ValueDisplay.this.label = new Label();
				update();
				return label;
			}

			@Override
			public Widget getWidget(String key)
			{
				return label;
			}

			@Override
			public void postBuild()
			{}
		});
		if (Game.contextExists(Context.SESSION))
		{
			Game.getContext(Context.SESSION).set("VD-" + text, this);
		}
	}
	
	@Override
	public void update()
	{
		label.setPosition(x, y);
		label.setSize(256, 32);
		label.setText(text + Game.getVar(varIndex));
	}
	
	public Label getWidget()
	{
		return label;
	}

	@Override
	public void dispose()
	{
		container.dispose();
		label = null;
	}
}
