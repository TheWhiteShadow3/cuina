package cuina.widget.test;

import cuina.graphics.Graphics;
import cuina.plugin.LifeCycleAdapter;
import cuina.widget.CuinaWidget;
import cuina.widget.WidgetContainer;
import cuina.widget.WidgetDescriptor;
import cuina.widget.WidgetEventHandler;
import cuina.widget.WidgetFactory;
import cuina.widget.data.ButtonNode;
import cuina.widget.data.FrameNode;
import cuina.widget.data.LabelNode;
import cuina.widget.data.MenuNode;
import cuina.widget.data.PictureNode;

public class TestMenu extends LifeCycleAdapter implements WidgetEventHandler
{
	private FrameNode frame;
	private WidgetContainer container;
	
	public TestMenu()
	{
		this.frame = new FrameNode();
		frame.x = 20;
		frame.y = 20;
		frame.width = Graphics.getWidth() - 60;
		frame.height = Graphics.getHeight() - 76;
		
		MenuNode menu = new MenuNode();
		menu.name = "test";
		menu.x = 20;
		menu.y = 20;
		menu.width = 128;
		menu.height = 256;
		menu.commands = new String[] {"Items", "Fertigkeiten", "Status", "Ausrüstung", "Position", "Steuerung", "Erfolge", "Speichern", "Ende"};
		menu.hGap = 9;
		menu.vGap = 9;
		
		ButtonNode button = new ButtonNode();
		button.name = "picButton";
		button.x = 168;
		button.y = 20;
		
		PictureNode actor1 = new PictureNode();
		actor1.imageName = "faces/rosa.png";
		
		button.add(actor1);
		
		LabelNode name = new LabelNode();
		name.text = "Rosa";
		name.x = 320;
		name.y = 32;
		
		LabelNode type = new LabelNode();
		type.text = "Flitchen";
		type.x = 448;
		type.y = 32;
		
		PictureNode hpLine = new PictureNode();
		hpLine.imageName = "pictures/pseudo_hp_leiste.png";
		hpLine.x = 304;
		hpLine.y = 64;

		LabelNode hp = new LabelNode();
		hp.text = "HP:  400/ 400";
		hp.x = 320;
		hp.y = 56;
		
		LabelNode mp = new LabelNode();
		mp.text = "MP:    76/    90";
		mp.x = 320;
		mp.y = 104;
		
		frame.add(menu);
		frame.add(button);
		frame.add(name);
		frame.add(type);
		frame.add(hpLine);
		frame.add(hp);
		frame.add(mp);
	}

	@Override
	public void init()
	{
		WidgetDescriptor descriptor = WidgetFactory.createWidgetDescriptor(frame, "test");
		descriptor.setGlobalEventHandler(this);
		container = new WidgetContainer(descriptor);
		
//		cuina.widget.Button picButton = new cuina.widget.Button("picButton", false);
//		picButton.setTheme("/button");
//		picButton.setPosition(168, 20);
//		picButton.addCallback(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				System.out.println("pantsu desu");
//			}
//		});
//		
//		Picture actor1 = new Picture("rosa_pic");
//		actor1.setImage("faces/rosa.png");
//		actor1.setTheme("/picture");
//		
//		picButton.add(actor1);
//		
//		Label name = new Label("Rosa");
//		name.setTheme("/label");
//		name.setPosition(320, 32);
//		
//		Label type = new Label("Flitchen");
//		type.setTheme("/label");
//		type.setPosition(448, 32);
//		
//		Picture hpLine = new Picture("hp_pic");
//		hpLine.setImage("pictures/pseudo_hp_leiste.png");
//		hpLine.setTheme("/picture");
//		hpLine.setPosition(304, 64);
//		
//		Label hp = new Label("HP:  400/ 400");
//		hp.setTheme("/label");
//		hp.setPosition(320, 56);
//		
//		Label mp = new Label("MP:    76/    90");
//		mp.setTheme("/label");
//		mp.setPosition(320, 104);
//		
//		add(menu);
//		add(picButton);
//		add(name);
//		add(type);
//		add(hpLine);
//		add(hp);
//		add(mp);
	}

	@Override
	public void dispose()
	{
		container.dispose();
	}

	@Override
	public void handleEvent(String key, CuinaWidget widget, Object arg)
	{
		System.out.println("handleEvent: " + key + "; Argument: " + arg);
	}
	
	

//	@Override
//	protected void layout()
//	{
//		int minWidth = getMinWidth();
//		int minHeight = getMinHeight();
//		if (getWidth() < minWidth || getHeight() < minHeight)
//		{
//			int width = Math.max(getWidth(), minWidth);
//			int height = Math.max(getHeight(), minHeight);
//			if (getParent() != null)
//			{
//				int x = Math.min(getX(), getParent().getInnerRight() - width);
//				int y = Math.min(getY(), getParent().getInnerBottom() - height);
//				setPosition(x, y);
//			}
//			setSize(width, height);
//		}
//
//		layoutTitle();
//		layoutCloseButton();
//		layoutResizeHandle();
//	}
}
