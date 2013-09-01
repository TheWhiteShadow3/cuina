package cuina.map;

import cuina.Game;
import cuina.database.DataTable;
import cuina.database.Database;
import cuina.plugin.ForScene;
import cuina.plugin.LifeCycle;
import cuina.plugin.Plugin;
import cuina.script.ScriptExecuter;
import cuina.widget.WidgetContainer;
import cuina.widget.data.FrameNode;
import cuina.widget.data.TextAreaNode;

import java.io.File;

@ForScene(name="TWL", scenes="Map")
public class TWLTest implements Plugin, LifeCycle
{
	private static final long serialVersionUID = -3983183057957801069L;

	private WidgetContainer container1;
	private WidgetContainer container2;
	
	@Override
	public void init()
	{
		ScriptExecuter.executeDirect("create_menu", "create_menu");
	}
	
	@SuppressWarnings("unused")
	private cuina.widget.data.WidgetNode createWidgetData()
	{
		FrameNode frame = new FrameNode();
		frame.key = "myRoot";
		frame.x = 20;
		frame.y = 20;
		frame.width = 240;
		frame.height = 160;
		frame.title = "Frame";
		
		TextAreaNode textarea = new TextAreaNode();
		frame.key = "myText";
		textarea.x = 40;
		textarea.y = 40;
		textarea.width = 200;
		textarea.height = 120;
		textarea.text = "Der Text";
		
		frame.children.add(textarea);
		
		DataTable<cuina.widget.data.WidgetNode> table = new DataTable("Widget", cuina.widget.data.WidgetNode.class);
		table.put(frame);
		Database.saveData(new File(Game.getRootPath() + "/data/Widget.cxd").getAbsoluteFile(), table);
		return frame;
	}

	@Override
	public void update()
	{
	}

	@Override
	public void dispose()
	{
		if (container1 != null) container1.dispose();
		if (container2 != null) container2.dispose();
	}
	
	@Override
	public void postUpdate() {}
}
