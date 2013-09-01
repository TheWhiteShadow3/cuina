package cuina.editor.script.internal;

import cuina.editor.script.internal.RubyNodeConverter.CommandLine;
import cuina.editor.script.internal.prefs.ScriptPreferences;
import cuina.editor.script.internal.ruby.Token;
import cuina.editor.script.internal.ruby.Tokenizer;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.NodeLabelProvider;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.Node;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.swt.graphics.Color;

public class ScriptLabelProvider extends NodeLabelProvider implements IColorProvider
{
	private static final String CLOSE_TAG 	= "end";
	private static final String MARK 		= "@>";
	private static final String INDENT;
	
	static
	{
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < ScriptPreferences.getIndentWidth(); i++) builder.append(' ');
		INDENT = builder.toString();
	}

	private HashMap<String, Color> colors = new HashMap<String, Color>();
	private Tokenizer tokenizer = new Tokenizer();
	private IScriptLibrary library;
	
	public ScriptLabelProvider(IScriptLibrary library)
	{
		this.library = library;
	}
	
	@Override
	public String getText(Object element)
	{
		if (element instanceof CommandLine)
		{
			CommandLine line = (CommandLine) element;
			return indentString(line.indent) + getCommandLineString(line);
		}
		return super.getText(element);
	}
	
	private String getCommandLineString(CommandLine line)
	{
		if (line.node != null)
			return super.getText(line.node);
		else
		{
			if (line.type == CommandLine.TYPE_SLAVE)
				return CLOSE_TAG;
			else if (line.type == CommandLine.TYPE_MARK)
				return MARK;
			else
				return "unknown!";
		}
	}
	
	private String indentString(int indent)
	{
		if (indent == 0) return "";
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < indent; i++)
		{
			builder.append(INDENT);
		}
		return builder.toString();
	}

	@Override
	public Color getForeground(Object element)
	{
		if (element instanceof RubyNodeConverter.CommandLine)
		{
			String key = ((RubyNodeConverter.CommandLine) element).colorKey;
			Color color = colors.get(key);
			if (color == null || !ScriptPreferences.getColor(key).equals(color))
			{
				color = ScriptPreferences.getColor(key);
				Color oldColor = colors.put(key, color);
				if (oldColor != null) oldColor.dispose();
			}
			return color;
		}
		else return null;
	}

	@Override
	public Color getBackground(Object element)
	{
		return ScriptPreferences.getColor(ScriptPreferences.CMDLINE_BACKGROUND_COLOR);
	}
	
	@Override
	public void dispose()
	{
		for (Color c : colors.values()) c.dispose();
		super.dispose();
	}

	@Override
	protected String getFunction(CallNode callNode)
	{
		FunctionDefinition func = ScriptUtil.findLibraryFunction(library, callNode);
		if (func == null || func.text == null) return super.getFunction(callNode);
		
		return createFunctionLabel(func, callNode.getArgument().getChilds());
	}
	
	private String createFunctionLabel(FunctionDefinition function, List<Node> arguments)
	{
		StringBuilder builder = new StringBuilder();
		tokenizer.clearTokens();
		tokenizer.parse(function.text);
		Token[] tokens = tokenizer.getTokens();
		
		int start = 0;
		boolean key = false;
		for (Token token : tokens)
		{
			if (key == true)
			{
				for(int i = 0; i < function.params.size(); i++)
				{
					ValueDefinition param = function.params.get(i);
					if ( param.id.equals(token.getValue()) )
					{
						builder.append( getText(arguments.get(i)) );
						break;
					}
				}
				start = token.getEndposition();
				key = false;
			}
			else if (token.charAt(0) == '%')
			{
				key = true;
				builder.append( function.text.substring(start, token.getPos()) );
			}
		}
		builder.append( function.text.substring(start, function.text.length()) );
		return builder.toString();
	}
}
