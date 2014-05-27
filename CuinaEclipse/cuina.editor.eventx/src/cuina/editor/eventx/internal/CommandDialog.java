package cuina.editor.eventx.internal;

import cuina.editor.eventx.internal.editors.TypeEditor;
import cuina.eventx.Command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CommandDialog extends TitleAreaDialog
{
	private static final String[] ARGUMENT_COMBO_ITEMS = new String[] {"", "0", "1", "2", "3", "4", "5", "6", "7"};
	private final Color COLOR_RED;
	
	private Command command;
	private FunctionEntry function;
	private CommandLibrary library;
	private List<TypeEditor> editors;
	private Composite[] editorBlocks;
	
	private CommandDialog(CommandEditorContext context)
	{
		super(context.getShell());
		this.COLOR_RED = context.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.library = context.getCuinaProject().getService(CommandLibrary.class);
	}
	
	public CommandDialog(CommandEditorContext context, Command cmd)
	{
		this(context);
		if (cmd == null) throw new NullPointerException();
		
		this.function = library.getFunction(cmd);
		this.command = cmd;
		init(context);
	}
	
	public CommandDialog(CommandEditorContext context, FunctionEntry func)
	{
		this(context);
		if (func == null) throw new NullPointerException();
		
		this.function = func;
		try
		{
			this.command = library.createCommand(func);
			init(context);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Command-Dialog");
//		shell.setSize(320, 320);
	}
	
	private void init(CommandEditorContext context)
	{
		setHelpAvailable(false);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		setReturnCode(OK | CANCEL);
		
		this.editors = new ArrayList<TypeEditor>(8);
		for (int i = 0; i < function.argTypes.length; i++)
		{
			String type = function.argTypes[i];
			TypeEditor<?> editor = CommandLibrary.newTypeEditor(type);
			editor.init(context, type, command.args[i]);
			editors.add(editor);
		}
		this.editorBlocks = new Composite[editors.size()];
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		parent = (Composite) super.createDialogArea(parent);
		Handler handler = new Handler();
		
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayoutData(new GridData(GridData.FILL_BOTH));
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setAlwaysShowScrollBars(true);
		
		/* 
		 * FIXME: Das ScrolledComposite lässt sich nicht skrollen.
		 * Stattdessen wird der Inhalt abgeschnitten.
		 */
		Composite mainBlock = new Composite(sc, SWT.NONE);
		sc.setContent(mainBlock);
		mainBlock.setLayout(new GridLayout(3, false));
		
		for (int i = 0; i < editors.size(); i++)
		{
			if (i > 0)
			{
				Label separator = new Label(mainBlock, SWT.HORIZONTAL | SWT.SEPARATOR);
				separator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
			}
			String argName =  function.argNames[i];
			if (argName == null) argName = "Arg " + i;
			Label label = new Label(mainBlock, SWT.NONE);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
			label.setText(argName);
			
			Composite outerBlock = new Composite(mainBlock, SWT.NONE);
			outerBlock.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			FillLayout layout = new FillLayout();
			layout.marginWidth = 1;
			layout.marginHeight = 1;
			outerBlock.setLayout(layout);
			Composite innerBlock = new Composite(outerBlock, SWT.NONE);
			innerBlock.setLayout(new FillLayout());
			
			editors.get(i).createComponents(innerBlock);
			editorBlocks[i] = outerBlock;
			
			Group argBlock = new Group(mainBlock, SWT.NONE);
			argBlock.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
			argBlock.setText("Intrp.");
			argBlock.setLayout(new FillLayout());
			Combo argCombo = new Combo(argBlock, SWT.BORDER);
			argCombo.setItems(ARGUMENT_COMBO_ITEMS);
			argCombo.setData(outerBlock);
			argCombo.addListener(SWT.Selection, handler);
		}
		setTitle(function.name);
		return mainBlock;
	}
	
	public Command getCommand()
	{
		return command;
	}

	@Override
	protected void okPressed()
	{
		boolean cancel = false;
		for (int i = 0; i < editors.size(); i++)
		{
			if (editors.get(i).apply())
			{
				editorBlocks[i].setBackground(null);
			}
			else
			{
				editorBlocks[i].setBackground(COLOR_RED);
				if (!cancel) editorBlocks[i].setFocus();
				cancel = true;
			}
		}
		if (cancel) return;
		
		for (int i = 0; i < editors.size(); i++)
		{
			command.args[i] = editors.get(i).getValue();
		}
		
		super.okPressed();
	}
	
	private class Handler implements Listener
	{
		@Override
		public void handleEvent(Event event)
		{
//			if (event.type == SWT.Resize)
//			{
//				handleResize(event.width);
//			}
			if (event.type == SWT.Selection)
			{
				Combo combo = (Combo) event.widget;
				Composite composite = (Composite) event.widget.getData();
				composite.setEnabled(combo.getSelectionIndex() == 0);
			}
		}
		
//		protected void handleResize(Composite composite)
//		{
//			ScrollBar scrollbar = composite.getVerticalBar();
//			
//			scrollbar.setMaximum(viewSize.y);
//			vBar.setThumb(Math.min(viewSize.y, bounds.height));
//			int vPage = viewSize.y - bounds.height;
//			int vSelection = vBar.getSelection();
//			if (vSelection >= vPage)
//			{
//				if (vPage <= 0) vSelection = 0;
//				origin.y = -vSelection;
//			}
//			
//			canvas.scroll(-origin.x, -origin.y, 0, 0, viewSize.x, viewSize.y, false);
//			updateSelectionArea();
//		}
	}
}