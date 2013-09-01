package cuina.editor.script.internal.ruby;

import java.util.ArrayList;

public class Tokenizer
{
	public static final int NUMBER 		= 1;
	public static final int IDENTIFIER 	= 2;
	public static final int OPERAND 	= 3;
	public static final int BRACE 		= 4;
	public static final int SPECIAL 	= 5;
	public static final int RETURN 		= 6;
	public static final int WHITESPACE	= 7;
	public static final int UNKNOWN		= 8;

	private static final int NOTHING = 0;
	private static final int LINE_COMMENT = 1;
	// private static final int BLOCK_COMMENT = 2;
	private static final int CHAR = 3;
	private static final int STRING = 4;
	
	private final ArrayList<Token> tokens;
	boolean ignoreWhitespace;
	
	private int pos;
	private int start;
	private int group;
	boolean decimalDot = false;
	private int block;
	private String block_break;
	
	public Tokenizer()
	{
		this(32);
	}
	
	public Tokenizer(int initialCapacity)
	{
		tokens = new ArrayList<Token>(initialCapacity);
	}
	
	public void clearTokens()
	{
		tokens.clear();
	}
	
	public boolean isIgnoreWhitespace()
	{
		return ignoreWhitespace;
	}

	public void setIgnoreWhitespace(boolean value)
	{
		this.ignoreWhitespace = value;
	}

	public void parse(String text)
	{
		if (!text.endsWith("\n")) text = text + "\n";

		block = 0;
		pos = 0;
		group = -1;
		start = 0;
		for (pos = 0; pos < text.length(); pos++)
		{
			char c = text.charAt(pos);

			if (block != NOTHING)
			{
				// filtere Escape-Sequenzen in Strings und Chars
				if ((block == STRING || block == CHAR) && c == '\\')
				{
					pos++;
					continue;
				}

				if (isNext(text, block_break))
				{
					if (block_break.charAt(0) != '\n') pos += block_break.length();
					tokens.add(new Token(start, group, text.substring(start, pos)));
					group = -1;
					start = pos--;
					block = NOTHING;
				}
				continue;
			}

			if (ignoreWhitespace && isWhiteSpace(c))
			{
				if (pos > start)
				{
					tokens.add(new Token(start, group, text.substring(start, pos)));
					group = -1;
					start = pos;
				}
				// group = -1;
				start++;
				continue;
			}
			int newGroup = IDENTIFIER;
			if (c == '#') startBlock(LINE_COMMENT, "\n");
			else if (c == '\'') startBlock(CHAR, "\'");
			else if (c == '\"') startBlock(STRING, "\"");
			else newGroup = getCharGroup(c);
			// Trenne Token nach jedem Gruppenwechsel oder jeder Klammer
			if ((group != -1 && group != newGroup) || (newGroup == BRACE && pos > start))
			{
				tokens.add(new Token(start, group, text.substring(start, pos)));
				start = pos;
			}
			group = newGroup;
		}
		String lastStr = text.substring(start, pos);
		if (lastStr.length() > 0) tokens.add(new Token(start, group, lastStr));
	}

	public Token[] getTokens()
	{
//		System.out.println(tokens);
		return tokens.toArray(new Token[tokens.size()]);
	}

	private int getCharGroup(char c)
	{
		if (group != IDENTIFIER)
		{
			if (group != NUMBER && c > 47 && c < 58)
			{
				decimalDot = false;
				return NUMBER;
			}
		}
		
		if (group == NUMBER)
		{
			if ((c > 47 && c < 58) || c == '_')
				return NUMBER;
			else if (c == '.' && !decimalDot)
			{
				decimalDot = true;
				return NUMBER;
			}
		}
		
		if ((c > 47 && c < 58)	// Zahl
		 || (c > 64 && c < 91) 	// Groß A-Z
		 || (c > 96 && c < 123) // Klein a-z)
		 || (c > 191 && c < 215) || (c > 216 && c < 247) || (c > 248 && c < 256) // Umlaute
		 || (c == '_') || (c == '$') || (c == '@')) // Sonderzeichen
			return IDENTIFIER;
		else
		{
			if (c == '-' && group != IDENTIFIER && group != NUMBER) return NUMBER;
			
			if (c == '+' || c == '-' || c == '*' || c == '/' || c == '|' || c == '&' || c == '<' || c == '>'
			 || c == '!' || c == '=') return OPERAND;

			if (c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}') return BRACE;
			
			if (c == '!' || c == '?' || c == ':' || c == '.' || c == ',') return SPECIAL;
			
			if (c == '\n' | c == ';') return RETURN;
			
			if (isWhiteSpace(c)) return WHITESPACE;
		}
		return UNKNOWN;
//		throw new RuntimeException("Invalid Character '" + c + "' on position " + pos);
	}

	private boolean isWhiteSpace(char c)
	{
		return c <= 32 && c != '\n';
	}

	private void startBlock(int block, String block_break)
	{
		this.block = block;
		this.block_break = block_break;
	}

	private boolean isNext(String text, String pattern)
	{
		if (text == null || text.length() < pos + pattern.length()) return false;
		return (pattern.equals(text.substring(pos, pos + pattern.length())));
	}
}
