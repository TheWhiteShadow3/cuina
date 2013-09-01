package cuina.editor.script.internal.dialog;

import cuina.editor.script.dialog.CommandTab;
import cuina.editor.script.internal.prefs.ScriptPreferences;
import cuina.editor.script.internal.ruby.ParseException;
import cuina.editor.script.internal.ruby.RubyParser;
import cuina.editor.script.internal.ruby.RubySource;
import cuina.editor.script.library.ValueDefinition;
import cuina.editor.script.ruby.NodeLabelProvider;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.RootNode;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExpressionScriptTab implements CommandTab, ModifyListener
{
	private Node node;
	private ScriptDialogContext context;
	private RubyParser parser;
	
	private Text inExp;
	private Label resultTxt;
	
	@Override
	public void setNode(Node node, ValueDefinition parameter)
	{
		this.node = node;
		
		if (node instanceof EmptyNode)
			inExp.setText("");
		else
			inExp.setText(new NodeLabelProvider().getText(node));
	}

	@Override
	public Node getNode()
	{
		return node;
	}

	@Override
	public void init(ScriptDialogContext context)
	{
		this.context = context;
		
		parser = new RubyParser(RubyParser.MODE_STRICT);
	}

	@Override
	public void createControl(Composite parent)
	{
		parent.setLayout(new GridLayout(2, false));
		
		new Label(parent, SWT.NONE).setText("Ausdruck:");
		
		inExp = new Text(parent, SWT.BORDER);
		inExp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		inExp.setFont(new Font(inExp.getDisplay(), ScriptPreferences.getFontData(ScriptPreferences.CMDLINE_FONT)));
		inExp.addModifyListener(this);
		
		resultTxt = new Label(parent, SWT.NONE);
		resultTxt.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		resultTxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
	}

	@Override
	public void modifyText(ModifyEvent ev)
	{
		RubySource source = new RubySource(inExp.getText(), null);
		try
		{
			RootNode root = parser.parse(source);
			if (root.getChilds().size() == 0) root.add(new EmptyNode());
			if (root.getChild(0) != null)
			{
				this.node = root.getChild(0);
				context.valueChanged(this, node);
			}
			resultTxt.setText("");
		}
		catch (ParseException ex)
		{
//			e1.printStackTrace();
			resultTxt.setText(ex.getMessage());
		}
	}

	@Override
	public void setEnabled(boolean value)
	{
		inExp.setEnabled(value);
	}

	@Override
	public String getName()
	{
		return "Ausdruck";
	}
}
