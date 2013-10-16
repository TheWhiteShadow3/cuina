package cuina.editor.script.internal;

import cuina.database.DatabaseObject;
import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.database.ui.IDatabaseEditor;
import cuina.editor.core.CuinaProject;
import cuina.editor.core.ObjectUtil;
import cuina.editor.script.Scripts;
import cuina.editor.script.internal.RubyNodeConverter.CommandLine;
import cuina.editor.script.internal.RubyNodeConverter.CommandPage;
import cuina.editor.script.internal.dialog.CommandDialog;
import cuina.editor.script.internal.dialog.EmptyNode;
import cuina.editor.script.internal.dialog.ScriptDialogContext;
import cuina.editor.script.internal.prefs.ScriptPreferences;
import cuina.editor.script.internal.ruby.RubyIdentifier;
import cuina.editor.script.internal.ruby.RubyWriter;
import cuina.editor.script.internal.ruby.TreeEditor;
import cuina.editor.script.library.IScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary;
import cuina.editor.script.library.StaticScriptLibrary.ScriptType;
import cuina.editor.script.library.TreeLibrary;
import cuina.editor.script.ruby.TreeEditorEvent;
import cuina.editor.script.ruby.TreeEditorListener;
import cuina.editor.script.ruby.ast.AsgNode;
import cuina.editor.script.ruby.ast.BlockNode;
import cuina.editor.script.ruby.ast.CallNode;
import cuina.editor.script.ruby.ast.CaseNode;
import cuina.editor.script.ruby.ast.ClassNode;
import cuina.editor.script.ruby.ast.DefNode;
import cuina.editor.script.ruby.ast.IParameter;
import cuina.editor.script.ruby.ast.IfNode;
import cuina.editor.script.ruby.ast.Node;
import cuina.editor.script.ruby.ast.VarNode;
import cuina.editor.script.ruby.ast.WhileNode;
import cuina.script.Script;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public class ScriptEditor extends AbstractDatabaseEditorPart implements ScriptDialogContext, ISelectionProvider
{
	private static final String SCRIPT_NAME_PREFIX = "SCRIPT_";
	
//	private RubyParser parser;
	private TreeEditor treeEditor;
	private Script script;
	private StaticScriptLibrary library;
	private TreeLibrary treeLibary;

	private RubyNodeConverter converter;
	private IDatabaseEditor context;
	private int pageIndex = -1;
	private final ArrayList<ScriptPage> scriptPages = new ArrayList<ScriptPage>();
	private ScriptEditorEventHandler handler;
	
	private Shell shell;
	private Text inName;
	private Combo comboScriptType;
	private CTabFolder pageTabFolder;
	private CommandLibraryBlock buttonBlock;
	private ScriptSelection selection;
	private final ArrayList<ISelectionChangedListener> changeListener = new ArrayList<ISelectionChangedListener>();
	private Button cmdAddPage;
	private boolean modified;
	private boolean update;
	private Button cmdRemovePage;
	private Button cmdEditPage;

	public ScriptEditor()
	{
//		org.eclipse.dltk.ruby.ui.editor.RubyEditor
		
		this.buttonBlock = new CommandLibraryBlock(this);
		this.handler = new ScriptEditorEventHandler();
		
//		parser = new RubyParser(RubyParser.MODE_DEFAULT);
//		treeEditor = parser.getTreeEditor();
	}

	@Override
	protected void init(DatabaseObject obj)
	{
		if (this.script == obj) return;
		
		this.script = (Script) obj;
		modified = false;
		
		if (script != null) parseScriptCode();
		refresh();
	}

	@Override
	protected boolean applySave()
	{
		if (modified)
			script.setCode( new RubyWriter().write(treeEditor.getRoot()) );
		
		return true;
	}

	@Override
	public void setFocus()
	{
		pageTabFolder.setFocus();
	}
	
//	@Override
//	public void setChildValue(Object obj)
//	{
//		if (obj instanceof CommandPage)
//		{
//			ScriptPage page = findPage((CommandPage) obj);
//			setSelection(new ScriptSelection(page, null));
//		}
//		else if (obj instanceof CommandLine)
//		{
//			CommandLine cl = (CommandLine) obj;
//			ScriptPage page = findPage(cl.page);
//			setSelection(new ScriptSelection(page, cl));
//		}
//	}
//	
//	private ScriptPage findPage(CommandPage commandPage)
//	{
//		for (int i = 0; i < scriptPages.size(); i++)
//		{
//			if (scriptPages.get(i).getPage().equals(commandPage))
//			{
//				return scriptPages.get(i);
//			}
//		}
//		return null;
//	}
	
	private void refresh()
	{
		update = true;
		if (script != null)
		{
			treeLibary.setRoot(treeEditor.getRoot());
			converter.setScriptType( library.findScriptType(script.getInterfaceClass()) );
			converter.createPages();
			inName.setText(script.getName());
			inName.setEnabled(true);
			ScriptType type = converter.getScriptType();
			comboScriptType.setText(type == null ? "" : type.name);
			comboScriptType.setEnabled(true);
		}
		else
		{
			converter.createPages();
			inName.setText("");
			inName.setEnabled(false);
			comboScriptType.setText("");
			comboScriptType.setEnabled(false);
		}
		
		for (ScriptPage page : scriptPages)
		{
			page.dispose();
		}
		scriptPages.clear();
		
		for (CommandPage page : converter.getPageList())
		{
			addPage(page);
		}
		
		if (scriptPages.size() > 0)
		{
			pageIndex = 0;
			pageTabFolder.setSelection(0);
		}
		pageTabFolder.redraw();
		update = false;
	}
	
	private void parseScriptCode()
	{
		// inizialisiere Script-Code, wenn null
		if (script.getCode() == null)
		{
			StringBuilder builder = new StringBuilder(48);
			builder.append("class Script_").append(script.getKey());
			builder.append("\nend\nScript_").append(script.getKey());
			builder.append(".new");
			script.setCode(builder.toString());
		}
		
		treeEditor = Scripts.getScriptCache(context.getProject()).getTreeEditor(script);
		treeEditor.addTreeEditorListener(handler);
		converter = new RubyNodeConverter(treeEditor);
	}
	
	private void addPage(CommandPage commandPage)
	{
		ScriptPage page = new ScriptPage(this, commandPage, pageTabFolder);
		scriptPages.add(page);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.shell = parent.getShell();
		this.library = getCuinaProject().getService(StaticScriptLibrary.class);
		this.treeLibary = new TreeLibrary(library);
		
		parent.setLayout(new GridLayout(3, false));
		
		new Label(parent, SWT.NONE).setText("Name:");
		inName = new Text(parent, SWT.BORDER);
		inName.setLayoutData(new GridData(120, 12));
		inName.addListener(SWT.Modify, handler);
		
		Composite pageButtonGroup = new Composite(parent, SWT.NONE);
		pageButtonGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));
		pageButtonGroup.setLayout(new GridLayout(3, true));
		
		cmdAddPage = new Button(pageButtonGroup, SWT.PUSH);
		cmdAddPage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		cmdAddPage.setText("neue Seite");
		cmdAddPage.addListener(SWT.Selection, handler);
		
		cmdRemovePage = new Button(pageButtonGroup, SWT.PUSH);
		cmdRemovePage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		cmdRemovePage.setText("Seite löschen");
		cmdRemovePage.addListener(SWT.Selection, handler);
		
		cmdEditPage = new Button(pageButtonGroup, SWT.PUSH);
		cmdEditPage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		cmdEditPage.setText("Seite editieren");
		cmdEditPage.addListener(SWT.Selection, handler);

		new Label(parent, SWT.NONE).setText("Skript-Typ:");
		comboScriptType = new Combo(parent, SWT.READ_ONLY);
		ScriptType[] list = library.getScriptTypes();
		String [] types = new String[list.length + 1];
		types[0] = "";
		for(int i = 0; i < list.length; i++)
		{
			types[i+1] = list[i].name;
		}
		comboScriptType.setItems(types);
		comboScriptType.setLayoutData(new GridData(120, 12));
		comboScriptType.addListener(SWT.Selection, handler);
		comboScriptType.setEnabled(false);
		
		Composite group = new Composite(parent, SWT.NONE);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		group.setLayout(new GridLayout(2, false));
		
		pageTabFolder = new CTabFolder(group, SWT.BORDER);
		pageTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		pageTabFolder.addListener(SWT.Selection, handler);
		pageTabFolder.setTabHeight(20);
		Display d = pageTabFolder.getDisplay();
		pageTabFolder.setBackground(new Color[]
		{
			d.getSystemColor(SWT.COLOR_WHITE),
			d.getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT),
		},
        new int[] {50}, true);
	
		buttonBlock.createControl(group);
		context.getEditorSite().setSelectionProvider(this);
	}

	public void update()
	{
		ScriptPage page = getCurrentPage();
		if (page == null) return;

		page.refresh();
		fireScriptChanged();
	}

	private ScriptPage getCurrentPage()
	{
		if (pageIndex == -1 || pageIndex >= scriptPages.size()) return null;
		
		return scriptPages.get(pageIndex);
	}
	
	private void changeScriptType(ScriptType type, boolean refresh)
	{
		if (type == converter.getScriptType()) return;
		
		System.out.println("[ScriptEditor] ändere ScriptType in " + type);
		
		ClassNode node = ScriptUtil.getScriptClass(treeEditor.getRoot());
		if (node == null)
		{
			node = new ClassNode("S_" + script.getKey());
			treeEditor.insertChild(new ScriptPosition(treeEditor.getRoot(), 0), node);
		}
		if (type == null)
		{
			ScriptUtil.setInterface(treeEditor, node, null, null);
			script.setInterfaceClass(null);
		}
		else
		{
			ScriptUtil.setInterface(treeEditor, node, type.clazz, type.getFullPath());
			script.setInterfaceClass(type.clazz.getName());
		}
		fireScriptChanged();
		if (refresh) refresh();
	}

	public StaticScriptLibrary getStaticScriptLibrary()
	{
		return library;
	}

	@Override
	public TreeEditor getTreeEditor()
	{
		return treeEditor;
	}
	
	@Override
	public TreeLibrary getTreeLibrary()
	{
		return treeLibary;
	}
	
	@Override
	public CuinaProject getProject()
	{
		return context.getProject();
	}
	
	RubyNodeConverter getNodeConverter()
	{
		return converter;
	}
	
	@Override
	public void valueChanged(Object source, Node node)
	{
		changeNode(node);
	}
	
	public void insertNode(Node node)
	{
		if (selection == null) return;

		ScriptPosition position = selection.getPosition();
		if (position.getParent() instanceof BlockNode)
		{
			treeEditor.insertChild(position, node);
//			fireScriptChanged();
		}
	}
	
	public void changeNode(Node node)
	{
		ScriptPosition position = selection.getPosition();
		treeEditor.changeChild(position, node);
//			fireScriptChanged();
	}
	
	private void fireScriptChanged()
	{
		modified = true;
		context.fireDataChanged(this, script);
	}

	private void fireSelectionChanged()
	{
		SelectionChangedEvent event = new SelectionChangedEvent(this, selection);
		for (int i = 0; i < changeListener.size(); i++)
		{
			changeListener.get(i).selectionChanged(event);
		}
//		System.out.println("[ScriptEditor] Ausgewählter Knoten: " + selection.getPosition().getNode());
	}
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener l)
	{
		changeListener.add(l);
	}
	
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener l)
	{
		changeListener.remove(l);
	}
	
	@Override
	public void setSelection(ISelection selection)
	{
		if (!(selection instanceof ScriptSelection))
			throw new IllegalArgumentException("selection must be a ScriptSelection.");
		if (selection.equals(this.selection)) return;
		
		update = true;
		this.selection = (ScriptSelection) selection;
		int newPageIndex = scriptPages.indexOf(this.selection.getScriptPage());
		if (newPageIndex == -1) throw new IllegalArgumentException("Illegal Page.");
		
		if (newPageIndex != pageIndex)
		{
			this.pageIndex = newPageIndex;
			pageTabFolder.setSelection(getCurrentPage().tabItem);
		}

		ScriptPage page = getCurrentPage();
		for (CommandLine line : page.getPage().lines)
		{
			if ( line.position.equals(this.selection.getPosition()) )
			{
				page.setSelectedLine(line);
				break;
			}
		}
		fireSelectionChanged();
		
		update = false;
	}

	@Override
	public ScriptSelection getSelection()
	{
//		if (selection == null)
//		{
//			ScriptPage page = getCurrentPage();
//			if (page != null)
//			{
//				List<CommandLine> lines = page.getPage().lines;
//				setSelection( new ScriptSelection(page, lines.get(lines.size() - 1)) );
//			}
//		}
		return selection;
	}
	
	private String findNodeLabel(Node node)
	{
		if (node instanceof CallNode)
			return ScriptUtil.findLibraryFunction(library, (CallNode) node).getLabel();
		if (node instanceof AsgNode)
			return ((AsgNode) node).getAcceptor().getName();
		else
			return null;
	}

	@Override
	public Shell getShell()
	{
		return shell;
	}

	@Override
	public Node getNode()
	{
		return getSelection().getPosition().getNode();
	}
	
	@Override
	public ScriptPosition getPosition()
	{
		return getSelection().getPosition();
	}
	
	private class ScriptEditorEventHandler implements TreeEditorListener, Listener,
			ISelectionChangedListener, IDoubleClickListener
	{
		@Override
		public void treeNodeAdded(TreeEditorEvent ev)
		{
			update();
		}

		@Override
		public void treeNodeRemoved(TreeEditorEvent ev)
		{
			update();
		}

		@Override
		public void treeNodeChanged(TreeEditorEvent ev)
		{
			update();
		}
		
		@Override
		public void doubleClick(DoubleClickEvent event)
		{
			CommandLine line = getCurrentPage().getSelectedLine();
			if (line == null) return;
			
			Node node = line.node;
			CommandDialog dialog = null;
			if (node == null)
			{
				switch(ScriptPreferences.getDefaultNode())
				{
					case "CallNode": 	node = new CallNode(""); break;
					case "AsgNode": 	node = new AsgNode(null, new VarNode("var", Node.LOCAL_SCOPE)); break;
					case "IfNode": 		node = new IfNode(null); break;
					case "WhileNode": 	node = new WhileNode(null); break;
					case "CaseNode": 	node = new CaseNode(null); break;
				}
				if (node == null) return;
				
				if (!(node instanceof CallNode)) // CallNode hat keine default Argumente
				{
					((IParameter) node).setArgument(new EmptyNode());
				}
				dialog = new CommandDialog(ScriptEditor.this, node, ScriptUtil.getTitleForNode(node.getClass()));
			}
			else
				dialog = new CommandDialog(ScriptEditor.this, ObjectUtil.clone(node), findNodeLabel(node));
			
			if (dialog.open() == Dialog.OK)
			{
				changeNode(dialog.getNode());
			}
		}

		@Override
		public void selectionChanged(SelectionChangedEvent e)
		{	// Meldet Änderung der ausgewählten Zeile im Editor.
			if (update) return;
			
			ScriptPage page = getCurrentPage();
			CommandLine line = page.getSelectedLine();
			
			setSelection(new ScriptSelection(page, line));
		}

		@Override
		public void handleEvent(Event event)
		{
			if (update) return;
			
			if (event.type == SWT.Modify)
			{
				if (event.widget == comboScriptType)
				{
					scriptTypeChanged();
				}
				else if (event.widget == inName)
				{
					nameChanged();
				}
			}
			else if (event.type == SWT.Selection)
			{
				if (event.widget == pageTabFolder)
				{
					ScriptEditor.this.pageIndex = pageTabFolder.getSelectionIndex();
				}
				else if (event.widget == comboScriptType)
				{
					scriptTypeSelected();
				}
				else if (event.widget == cmdAddPage)
				{
					doAddPage();
				}
				//TODO: Funktionalität ab hier fehlerhaft.
				else if (event.widget == cmdEditPage)
				{
					cmdEditPage();
				}
				else if (event.widget == cmdRemovePage)
				{
					cmdRemovePage();
				}
			}
			else if (event.type == SWT.KeyDown)
			{
				if (event.keyCode == SWT.DEL) // Entf
				{
					removeSelectedLines();
				}
			}
		}
		
		private void scriptTypeChanged()
		{
			int index = comboScriptType.getSelectionIndex();
			if (index == -1) return;
			if (index == 0)
				changeScriptType(null, true);
			else
				changeScriptType(library.getScriptTypes()[index - 1], true);
		}
		
		private void nameChanged()
		{
			String name = inName.getText();
			if (RubyIdentifier.isValidName(name))
			{
				ClassNode classNode = ScriptUtil.getScriptClass(treeEditor.getRoot());
				if (classNode != null)
				{
					script.setName(name);
					classNode.setName(SCRIPT_NAME_PREFIX + name);
					fireScriptChanged();
				}
			}
		}
		
		private void scriptTypeSelected()
		{
			int index = comboScriptType.getSelectionIndex();
			if (index == -1) return;
			ScriptType type = (index == 0) ? null : library.getScriptTypes()[index - 1];
			
			changeScriptType(type, true);
		}
		
		private void doAddPage()
		{
			int index = scriptPages.size();
			String name = "page_" + index;
			DefNode defNode = new DefNode(name);
			ClassNode classNode = ScriptUtil.getScriptClass(treeEditor.getRoot());
			selection = new ScriptSelection(null, classNode, index);
			CommandDialog dialog = new CommandDialog(ScriptEditor.this, defNode, "Seite " + name);
			
			if (dialog.open() == Dialog.OK)
			{
				classNode.add(defNode);
				addPage(converter.addPage(classNode, defNode));
				fireScriptChanged();
			}
		}
		
		private void cmdEditPage()
		{
			if (pageIndex == -1) return;
			
			CommandPage cmdPage = getCurrentPage().commandPage;
			DefNode defNode = (DefNode) cmdPage.node;
			selection = new ScriptSelection(getCurrentPage(), defNode, pageIndex);
			CommandDialog dialog = new CommandDialog(ScriptEditor.this,
					ObjectUtil.clone(defNode), defNode.getName());
			
			if (dialog.open() == Dialog.OK)
			{
//				int index = (converter.getScriptType() != null) ? pageIndex + 1 : pageIndex;
				treeEditor.changeChild(cmdPage.position, dialog.getNode());
				fireScriptChanged();
			}
		}
		
		private void cmdRemovePage()
		{
			if (pageIndex == -1) return;
			
			ScriptPage page = getCurrentPage();
			if (page.deletePage())
			{
				refresh();
			}
		}
		
		private void removeSelectedLines()
		{
			if (selection == null) return;
			
			ScriptPage page = selection.getScriptPage();
			CommandLine[] lines = page.getSelectedLines();
			if (lines.length == 0) return;
			
			for (int i = lines.length - 1; i >= 0; i--)
			{
				if (lines[i].node == null) continue;

				ScriptPosition position = lines[i].position;
				if (selection.getPosition().equals(position)) selection = null;
				
				treeEditor.removeChild(position);
			}
		}
	}

	public static class ScriptPage
	{
		private static Font pageFont;
		
		private ScriptEditor editor;
		private CTabFolder tabFolder;
		private CTabItem tabItem;
		private TableViewer viewer;
		private CommandPage commandPage;
//		private final HashMap<String, ValueDefinition> variables = new HashMap<String, ValueDefinition>();
		
		public ScriptPage(ScriptEditor editor, CommandPage cmdPage, CTabFolder tabFolder)
		{
			this.editor = editor;
			this.commandPage = cmdPage;
			this.tabFolder = tabFolder;
			createControls();
		}
		
		public TableViewer getViewer()
		{
			return viewer;
		}
		
		public CommandPage getPage()
		{
			return commandPage;
		}
		
		public String getName()
		{
			return commandPage.name;
		}
		
		public void refresh()
		{
			viewer.refresh();
		}
		
		public void setSelectedLine(CommandLine line)
		{
			viewer.setSelection(new StructuredSelection(line));
		}

		public CommandLine getSelectedLine()
		{
			return (CommandLine) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
		}
		
		public CommandLine[] getSelectedLines()
		{
			Object[] elements = ((IStructuredSelection) viewer.getSelection()).toArray();
			CommandLine[] lines = new CommandLine[elements.length];
			System.arraycopy(elements, 0, lines, 0, elements.length);
			return lines;
		}
		
		public boolean deletePage()
		{
			if (commandPage.lines.size() > 1 && showCloseMessage() == SWT.NO) return false;
			
			if (commandPage.important)
			{
				MessageBox msg = new MessageBox(tabFolder.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
				msg.setText("Closeing Page " + commandPage.name);
				msg.setMessage("Die Seite '" + commandPage.name + "' wird vom Skript-Typen benötigt.\n" +
						"Soll der Skript-Type auf Default gesetzt werden.");
				if (msg.open() == SWT.YES)
				{
					editor.changeScriptType(null, false);
					commandPage.position = new ScriptPosition(commandPage.node);
				}
			}
			
			editor.treeEditor.removeChild(commandPage.position);
			editor.fireScriptChanged();
			return true;
		}
		
		private void createControls()
		{
			createTabItem();
			
			Composite scriptArea = new Composite(tabItem.getParent(), SWT.NONE);
			tabItem.setControl(scriptArea);
			scriptArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			scriptArea.setLayout(new FillLayout());
			createViewer(scriptArea);
		}
		
		private void createTabItem()
		{
			tabItem = new CTabItem(tabFolder, commandPage.important ? SWT.NONE : SWT.CLOSE);
			Color bgc = tabFolder.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT);
			tabFolder.setBackground(bgc);
			if (commandPage.important)
			{
				tabItem.setImage(Scripts.getImage(Scripts.IMG_PAGE_REQUIRED));
				if (pageFont == null)
				{
					FontData[] data = tabItem.getFont().getFontData();
					data[0].setStyle(SWT.BOLD);
					pageFont = new Font(tabFolder.getDisplay(), data[0]);
				}
				tabItem.setFont(pageFont);
			}
			else
			{
				tabItem.setImage(Scripts.getImage(Scripts.IMG_PAGE_OPTIONAL));
				tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter()
				{
					@Override
					public void close(CTabFolderEvent event)
					{
						if (event.data != null) return;
						event.doit = deletePage();
						event.data = new Boolean(event.doit);
//						if (commandPage.lines.size() > 1)
//						{
//							event.doit = (showCloseMessage() == SWT.YES);
//						}
					}
				});
			}
			tabItem.setText(commandPage.name + ' ');
		}
		
		private int showCloseMessage()
		{
			MessageBox msg = new MessageBox(tabFolder.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
			msg.setText("Closeing Page " + commandPage.name);
			msg.setMessage("Die Seite '" + commandPage.name + "' ist nicht leer.\n" +
					"Soll die Seite trotzdem geschlossen werden?");
			return msg.open();
		}
		
		private void createViewer(Composite parent)
		{
			Composite tableParent = new Composite(parent, SWT.NONE);
			TableColumnLayout tableColumnLayout = new TableColumnLayout();
			tableParent.setLayout(tableColumnLayout);

			viewer = new TableViewer(tableParent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION)
			{
				@Override
				protected void setSelectionToWidget(List list, boolean reveal)
				{
					List oldList = getSelectionFromWidget();
					super.setSelectionToWidget(list, reveal);
					if (getSelectionFromWidget().isEmpty() && !oldList.isEmpty())
					{	// verhindere leere Auswahl durch Index-Vergleich
						int index = ((CommandLine) oldList.get(0)).position.getIndex();
						doSelect(new int[] {index});
					}
				}
			};
			FontData data = ScriptPreferences.getFontData(ScriptPreferences.CMDLINE_FONT);
			viewer.getControl().setFont(new Font(Display.getCurrent(), data));
			
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.DEFAULT);
			column.getColumn().setWidth(400);
			column.setLabelProvider(new ScriptColumnLabelProvider(editor.getTreeLibrary()));
			tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(100, 240, true)); 
			
//			viewer.setLabelProvider(new ScriptLabelProvider(editor.getScriptLibrary()));
			viewer.setContentProvider(new ScriptContentProvider());
			viewer.addSelectionChangedListener(editor.handler);
			viewer.addDoubleClickListener(editor.handler);
			viewer.getControl().addListener(SWT.KeyDown, editor.handler);
			viewer.setInput(commandPage);
		}
		
		public void dispose()
		{
			viewer.getControl().dispose();
			tabItem.dispose();
		}
	}
	
	static class ScriptColumnLabelProvider extends ColumnLabelProvider
	{
		ScriptLabelProvider scriptLabelProvider;
		
		public ScriptColumnLabelProvider(IScriptLibrary library)
		{
			this.scriptLabelProvider = new ScriptLabelProvider(library);
		}

		@Override
		public String getText(Object element)
		{
			return scriptLabelProvider.getText(element);
		}

		@Override
		public Color getForeground(Object element)
		{
			return scriptLabelProvider.getForeground(element);
		}

		@Override
		public Color getBackground(Object element)
		{
			return scriptLabelProvider.getBackground(element);
		}
	}
	
	private class Myp extends AbstractDecoratedTextEditor
	{
		
	}
}
