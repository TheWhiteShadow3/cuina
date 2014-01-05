package cuina.eventx;

import cuina.database.DataTable;
import cuina.database.Database;

public class InterpreterTest
{
//	private static Interpreter interpreter;

	public static void main(String[] args) throws InterruptedException
	{
		createTestEventList();
	}
	
	public static void createTestEventList()
	{
		Command[] commands = new Command[4];

		commands[0] = new Command("SZENE", "Message.showMessage", 0, "Text<img src=\"herz.png\"/>Text<br/>2. Zeille!\"§$%");
		commands[1] = new Command("INTERNAL", "wait", 0, 30);
		commands[2] = new Command("SZENE", "Message.showMessage", 0, "pantsu 2");
		commands[3] = new Command("INTERNAL", "wait", 0, 60);
		commands[3] = new Command("GLOBAL", "Transition.colorize", 0, new int[] {255, 0, 255, 128, 120});
//		commands[4] = new Command("INTERNAL", "goto", 0, 0);
		
		CommandList list = new CommandList("rosa", commands);
		
		DataTable<CommandList> table = new DataTable<CommandList>("Event", cuina.eventx.CommandList.class);
		table.put(list);
		
		String xml = Database.getXStream().toXML(table);
		System.out.println(xml);
	}
}
