package cuina.editor.script.internal.ruby;


public class SourceData
{
	private String filename;
	private Token token;
	
	public SourceData() {}
	
	public SourceData(RubySource source, Token token)
	{
		this.filename = source.getFilename();
		this.token = token;
	}

	public String getFilename()
	{
		return filename;
	}
	
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public Token getToken()
	{
		return token;
	}

	public void setToken(Token token)
	{
		this.token = token;
	}
}
