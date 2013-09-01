package cuina.editor.script.internal.ruby;

import java.util.HashMap;
import java.util.Set;

import cuina.editor.script.internal.ruby.IdentifierList.IdentifierType;

public class RubyIdentifier
{
	private static final HashMap<String, IdentifierType> keyList;
	
	static
	{
		keyList = new HashMap<String, IdentifierType>(120);
		
		keyList.put("require", IdentifierType.KEYWORD);
		keyList.put("include", IdentifierType.KEYWORD);
		keyList.put("end", IdentifierType.KEYWORD);
		keyList.put("return", IdentifierType.KEYWORD);

		keyList.put("true", IdentifierType.KEYWORD);
		keyList.put("false", IdentifierType.KEYWORD);
		keyList.put("nil", IdentifierType.KEYWORD);

		keyList.put("class", IdentifierType.KEYWORD);
		keyList.put("module", IdentifierType.KEYWORD);
		keyList.put("def", IdentifierType.KEYWORD);
		keyList.put("undef", IdentifierType.KEYWORD);
		keyList.put("private", IdentifierType.KEYWORD);
		keyList.put("public", IdentifierType.KEYWORD);

		keyList.put("super", IdentifierType.KEYWORD);
		keyList.put("self", IdentifierType.KEYWORD);
		keyList.put("new", IdentifierType.KEYWORD);
		keyList.put("alias", IdentifierType.KEYWORD);

		keyList.put("if", IdentifierType.KEYWORD);
		keyList.put("then", IdentifierType.KEYWORD);
		keyList.put("unless", IdentifierType.KEYWORD);
		keyList.put("else", IdentifierType.KEYWORD);
		keyList.put("elsif", IdentifierType.KEYWORD);
		keyList.put("for", IdentifierType.KEYWORD);
		keyList.put("in", IdentifierType.KEYWORD);
		keyList.put("while", IdentifierType.KEYWORD);
		keyList.put("until", IdentifierType.KEYWORD);
		keyList.put("break", IdentifierType.KEYWORD);
		keyList.put("next", IdentifierType.KEYWORD);
		keyList.put("case", IdentifierType.KEYWORD);
		keyList.put("when", IdentifierType.KEYWORD);

		keyList.put("do", IdentifierType.KEYWORD);
		keyList.put("redo", IdentifierType.KEYWORD);
		keyList.put("yield", IdentifierType.KEYWORD);

		keyList.put("begin", IdentifierType.KEYWORD);
		keyList.put("rescue", IdentifierType.KEYWORD);
		keyList.put("retry", IdentifierType.KEYWORD);
		keyList.put("ensure", IdentifierType.KEYWORD);

		keyList.put("and", IdentifierType.KEYWORD);
		keyList.put("or", IdentifierType.KEYWORD);
		keyList.put("not", IdentifierType.KEYWORD);

		keyList.put("__ENCODING__", IdentifierType.SYSTEM_VAR);
		keyList.put("__END__", IdentifierType.SYSTEM_VAR);
		keyList.put("__FILE__", IdentifierType.SYSTEM_VAR);
		keyList.put("__LINE__", IdentifierType.SYSTEM_VAR);

		keyList.put("BEGIN", IdentifierType.KEYWORD);
		keyList.put("END", IdentifierType.KEYWORD);

		// einige Standard-Funktionen
		keyList.put("loop", IdentifierType.METHOD);
		keyList.put("puts", IdentifierType.METHOD);
		keyList.put("attr_reader", IdentifierType.METHOD);
		keyList.put("attr_writer", IdentifierType.METHOD);
		keyList.put("attr_accessor", IdentifierType.METHOD);
		keyList.put("include_class", IdentifierType.METHOD);
	}
	
	public RubyIdentifier(String name)
	{
		
	}
	
	public static boolean isKeyword(String key)
	{
		return keyList.get(key) == IdentifierType.KEYWORD;
	}
	
	public static boolean isBuildinFunction(String key)
	{
		return keyList.get(key) == IdentifierType.METHOD;
	}
	
	public static IdentifierType getType(String key)
	{
		return keyList.get(key);
	}
	
	public static Set<String> getKeys()
	{
		return keyList.keySet();
	}
	
	public static boolean isValidIdentifier(String name)
	{
		if (isKeyword(name)) return false;
		return isValidName(name);
	}
	
	public static boolean isValidName(String name)
	{
		if (name.isEmpty()) return false;
		
		for (int i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);
			if (ch == '_' || Character.isLetter(ch)) continue;
			
			if (i == 0) return false;
			if (Character.isDigit(ch)) continue;
			
			return false;
		}
		return true;
	}
}
