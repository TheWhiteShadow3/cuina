package cuina.widget.test;

import cuina.widget.Menu;
import cuina.widget.Picture;

import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ResizableFrame;

public class TestMenu extends ResizableFrame
{
	public Menu menu;
	
	public TestMenu()
	{
		setTheme("/frame");
		setSize(640, 480);
		setFocusKeyEnabled(false);
		
		menu = new Menu("test", 1);
		menu.setCommands(new String[] {"Items", "Fertigkeiten", "Status", "Ausrüstung", "Position", "Steuerung", "Erfolge", "Speichern", "Ende"});
		menu.setGaps(9, 9);
		menu.setPosition(20, 20);
		menu.setSize(128, 256);
		
		cuina.widget.Button picButton = new cuina.widget.Button("picButton", false);
		picButton.setTheme("/button");
		picButton.setPosition(168, 20);
		picButton.addCallback(new Runnable()
		{
			@Override
			public void run()
			{
				System.out.println("pantsu desu");
			}
		});
		
		Picture actor1 = new Picture("rosa_pic");
		actor1.setImage("faces/rosa.png");
		actor1.setTheme("/picture");
		
		picButton.add(actor1);
		
		Label name = new Label("Rosa");
		name.setTheme("/label");
		name.setPosition(320, 32);
		
		Label type = new Label("Flitchen");
		type.setTheme("/label");
		type.setPosition(448, 32);
		
		Picture hpLine = new Picture("hp_pic");
		hpLine.setImage("pictures/pseudo_hp_leiste.png");
		hpLine.setTheme("/picture");
		hpLine.setPosition(304, 64);
		
		Label hp = new Label("HP:  400/ 400");
		hp.setTheme("/label");
		hp.setPosition(320, 56);
		
		Label mp = new Label("MP:    76/    90");
		mp.setTheme("/label");
		mp.setPosition(320, 104);
		
		add(menu);
		add(picButton);
		add(name);
		add(type);
		add(hpLine);
		add(hp);
		add(mp);
	}

	@Override
	protected void layout()
	{
		int minWidth = getMinWidth();
		int minHeight = getMinHeight();
		if (getWidth() < minWidth || getHeight() < minHeight)
		{
			int width = Math.max(getWidth(), minWidth);
			int height = Math.max(getHeight(), minHeight);
			if (getParent() != null)
			{
				int x = Math.min(getX(), getParent().getInnerRight() - width);
				int y = Math.min(getY(), getParent().getInnerBottom() - height);
				setPosition(x, y);
			}
			setSize(width, height);
		}

		layoutTitle();
		layoutCloseButton();
		layoutResizeHandle();
	}
}
