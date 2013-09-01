package cuina.editor.script.internal.ruby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class IdentifierList
{
	private final HashMap<String, LinkedList<Identifier>> list;
	private final Stack<ArrayList<Identifier>> activeIdentifier = new Stack<ArrayList<Identifier>>();
	private int level = 0;
	
	public IdentifierList()
	{
		list = new HashMap<String, LinkedList<Identifier>>();
	}
	
	public void startLevel()
	{
		level++;
	}
	
	public void define(String key, IdentifierType type, int start)
	{
		if (activeIdentifier.contains(key)) throw new IllegalArgumentException("Key already existed!");
		
		LinkedList<Identifier> listSet = list.get(key);
		if (listSet == null)
		{
			listSet = new LinkedList<Identifier>();
			list.put(key, listSet);
		}
		listSet.add(new Identifier(type, start));
		if (activeIdentifier.size() < level - 1)
		{
			activeIdentifier.push(null);
		}
		if (activeIdentifier.size() < level)
		{
			activeIdentifier.push(new ArrayList<Identifier>(4));
		}
	}
	
	public void endLevel(int endPos)
	{
		if (level == 0) return;
		
		if (activeIdentifier.size() == level)
		{
			ArrayList<Identifier> stackSet = activeIdentifier.pop();
			if (stackSet != null)
			{
				for (Identifier ident : stackSet)
				{
					ident.end = endPos;
				}
			}
		}
		level--;
	}
	
	public boolean isDefined(String key)
	{
		return list.containsKey(key);
	}
	
//	public boolean isDefined(String key, int pos)
//	{
//		LinkedList<IdentifierType> listSet = list.get(key);
//		
//		return ;
//	}
	
	public IdentifierType getIdentifierType(String key, int pos)
	{
		ArrayList<Identifier> stackSet;
		for(int i = activeIdentifier.size()-1; i >= 0; i--)
		{
			stackSet = activeIdentifier.get(i);
			if (stackSet != null)
			{
				for (Identifier ident : stackSet)
				{
					if (ident.start < pos && (ident.end == -1 || ident.end >= pos))
					{
						return ident.type;
					}
				}
			}
		}
		return null;
	}
	
	public static enum IdentifierType
	{
    	KEYWORD,
    	SYSTEM_VAR,
    	SYMBOL,
    	MODULE,
    	CLASS,
    	METHOD,
    	GLOBAL_VAR,
    	CLASS_VAR,
    	LOCAL_VAR;
	}
	
    /**
     * Liste an möglichen Typen für Bezeichner.
     * @author TheWhiteShadow
     */
    public static class Identifier
    {
    	public final IdentifierType type;
        /** Startpostition für die Gültigkeit */
        public int start;
        /** Endposition für die Gültigkeit */
        public int end = -1;
    	
    	public Identifier(IdentifierType type, int start)
    	{
    		this.type = type;
    		this.start = start;
    	}
//        /** Typ für Variablen */
//        public String type;
    }
}
