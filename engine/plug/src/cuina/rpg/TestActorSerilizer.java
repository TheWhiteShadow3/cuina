package cuina.rpg;

import cuina.database.DataTable;
import cuina.database.DataTableConverter;
import cuina.rpg.actor.ActorData;
import cuina.rpg.actor.Attribut;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;

import com.thoughtworks.xstream.XStream;

public class TestActorSerilizer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		XStream stream = new XStream();
		stream.registerConverter(new DataTableConverter());
		
		DataTable table = new DataTable<>("Enemy", ActorData.class);
		ActorData data = new ActorData();
		data.name = "Monster";
		data.initialLevel = 1;
		data.setKey("Monster");
		data.initialLevel = 99;
		
		HashMap<String, Attribut> atts = new HashMap<String, Attribut>();
		atts.put("HP", new Attribut("HP", 100, 100));
		atts.put("ATK", new Attribut("ATK", 10));
		atts.put("DEF", new Attribut("DEF", 5));
		
		data.attributes = atts;
		
		table.put(data);
		try
		{
			stream.toXML(table, new FileOutputStream(new File("Enemy.cxd")));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}
