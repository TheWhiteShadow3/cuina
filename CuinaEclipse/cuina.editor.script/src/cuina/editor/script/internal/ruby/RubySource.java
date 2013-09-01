package cuina.editor.script.internal.ruby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RubySource
{
	private final String filename;
	private final String sourceCode;
	private final Token[] tokens;
	
	public RubySource(String sourceCode, Token[] tokens, String filename)
	{
		this.filename = filename;
		this.sourceCode = sourceCode;
		this.tokens = tokens;
	}
	
	public RubySource(String sourceCode, String filename)
	{
		this.filename = filename;
		this.sourceCode = sourceCode;
		
		Tokenizer tokenizer = new Tokenizer(100);
		tokenizer.setIgnoreWhitespace(true);
		tokenizer.parse(sourceCode);
		this.tokens = tokenizer.getTokens();
	}
	
	public RubySource(InputStream stream, String filename) throws IOException
	{
		this.filename = filename;
		this.sourceCode = null;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		Tokenizer tokenizer = new Tokenizer();
		tokenizer.setIgnoreWhitespace(true);
		String line;
		while ((line = reader.readLine()) != null)
		{
			tokenizer.parse(line);
		}
		this.tokens = tokenizer.getTokens();
	}

	public String getFilename()
	{
		return filename;
	}

	public String getSourceCode()
	{
		return sourceCode;
	}

	public Token[] getTokens()
	{
		return tokens;
	}
}
