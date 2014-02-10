package cuina.database;

public class DatabaseObjectReference
{
	public String table;
	public String key;
	
	public DatabaseObjectReference(String table, String key)
	{
		this.table = table;
		this.key = key;
	}
}
