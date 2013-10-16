package cuina.editor.map.internal;

import cuina.database.DatabaseObject;
import cuina.database.ui.AbstractDatabaseEditorPart;
import cuina.database.ui.IDatabaseEditor;
import cuina.editor.map.TileSelection;
import cuina.editor.ui.ResourceButton;
import cuina.editor.ui.WidgetFactory;
import cuina.map.Tileset;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class TilesetEditor extends AbstractDatabaseEditorPart implements ISelectionChangedListener
{
	public static final int MODE_PASSAGES	= 1;
	public static final int MODE_MASK		= 2;
	public static final int MODE_PRIORITIES = 3;
	public static final int MODE_FLAGS 		= 4;
	public static final int MODE_TAGS 		= 5;
	
	private IDatabaseEditor context;
	private int editMode;
	private boolean update;
	
	private Tileset tileset;
	private int id = -1;
	
	private TilesetPanel tilesetPanel;
	private TileMaskPanel maskPanel;
	private Text nameButton;
	private ResourceButton fileButton;
	private ResourceButton backgroundButton;
	private Button editPassableOP;
	private Button editPriorityOP;
	private Button editTagsOP;
	private Button editFlagsOP;
	private Label tileSizeHint;
	private Label tileInfo;
	private Text tileData;
	private Spinner tileSizeButton;
	private TileFlagPanel flagList;
	
	@Override
	protected boolean applySave()
	{
		tileset.setName(nameButton.getText());
		tileset.setTilesetName(fileButton.getResourceName());
		tileset.setBackgroundName(backgroundButton.getResourceName());
		tileset.setTileSize(tileSizeButton.getSelection());
		
		int dif = tilesetPanel.getTileCount() - tileset.getPassages().length;
		if (dif != 0)
		{
			if (dif > 0 || showWarning("Das Daten-Array ist größer als die Feldanzahl.\n" +
					"Soll das Daten-Array unwiederruflich verkleinert werden?\n"))
			{
				tileset.resizeTileset(tilesetPanel.getTileCount());
			}
		}
		return true;
	}
	
	private boolean showWarning(String message)
	{
		MessageDialog dialog = new MessageDialog(context.getEditorSite().getShell(), "Warnung!", null,
				message, MessageDialog.WARNING, new String[] {"Ja", "Nein"}, Dialog.CANCEL);
		int result = dialog.open();
		return result == Dialog.OK;
	}
	
	@Override
	protected void init(DatabaseObject obj)
	{
		this.tileset = (Tileset) obj;
		tilesetPanel.setTileset(tileset, context.getProject());
		
		nameButton.setText(tileset.getName());
		
		fileButton.setResourceName(tileset.getTilesetName());
		backgroundButton.setResourceName(tileset.getBackgroundName());
		tileSizeButton.setSelection(tileset.getTileSize());
		
		maskPanel.setTileset(context.getProject(), tileset);
	}

	@Override
	public void createPartControl(Composite parent)
	{
		parent.setLayout(new GridLayout(4, false));
		
//		Composite cmdGroup = new Composite(parent, SWT.NONE);
//		cmdGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
//		cmdGroup.setLayout(new GridLayout(2, false));
//		cmdGroup.setSize(256, 256);
		
		Listener handler = getListener();
		
		parent.addKeyListener(new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				System.out.println("keyReleased " + e);
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				System.out.println("keyPressed " + e);
			}
		});
		
		nameButton = WidgetFactory.createText(parent, "Name");
		nameButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		
		tilesetPanel = new TilesetPanel(parent, 256, 256);
		tilesetPanel.getGLCanvas().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 7));
		tilesetPanel.setGridVisible(true);
		tilesetPanel.addSelectionChangedListener(this);
		
		fileButton = WidgetFactory.createImageButton(parent, context.getProject(), "Tileset", null);
		fileButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		fileButton.addListener(SWT.Modify, handler);
		backgroundButton = WidgetFactory.createImageButton(parent, context.getProject(), "Hintergrund", null);
		backgroundButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
		backgroundButton.addListener(SWT.Modify, handler);
		
		tileSizeButton = WidgetFactory.createSpinner(parent, "Tile Größe");
		tileSizeButton.setMinimum(8);
		tileSizeButton.setMaximum(256);
		tileSizeButton.setIncrement(2);
		tileSizeButton.addListener(SWT.Modify, handler);
		
		tileSizeHint = new Label(parent, SWT.NONE);
		tileSizeHint.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		tileSizeHint.setImage(parent.getDisplay().getSystemImage(SWT.ICON_WARNING));
		tileSizeHint.setToolTipText("Die Rasterung stimmt nicht mit der Grafik überein.");
		tileSizeHint.setVisible(false);
		
		createEditGroup(parent, handler);
		createTileDataGroup(parent, handler);
		
		setEditMode(MODE_MASK);
		updateTileData();
	}
	
	private void createEditGroup(Composite parent, Listener handler)
	{
		Group editGroup = new Group(parent, SWT.NONE);
		editGroup.setText("Bearbeitungs Modus");
		editGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		editGroup.setLayout(new RowLayout(SWT.VERTICAL));

		editPassableOP = new Button(editGroup, SWT.RADIO);
		editPassableOP.setText("Passierbarkeit");
		editPassableOP.addListener(SWT.Selection, handler);
		
		editPriorityOP = new Button(editGroup, SWT.RADIO);
		editPriorityOP.setText("Priorität");
		editPriorityOP.addListener(SWT.Selection, handler);
		
		editTagsOP = new Button(editGroup, SWT.RADIO);
		editTagsOP.setText("Terrain-Nummern");
		editTagsOP.addListener(SWT.Selection, handler);
		
		editFlagsOP = new Button(editGroup, SWT.RADIO);
		editFlagsOP.setText("Markierungen");
		editFlagsOP.addListener(SWT.Selection, handler);
	}
	
	private void createTileDataGroup(Composite parent, Listener handler)
	{
		Group tileDataGroup = new Group(parent, SWT.NONE);
		tileDataGroup.setText("Auswahl");
		
		tileDataGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 3, 1));
		tileDataGroup.setLayout(new GridLayout(1, false));
		
		Composite tileInfoBox = new Composite(tileDataGroup, SWT.NONE);
		tileInfoBox.setLayout(new GridLayout(2, false));
		tileInfoBox.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		
		tileInfo = new Label(tileInfoBox, SWT.NONE);
		tileInfo.setLayoutData(new GridData(48, 14));
		
		tileData = new Text(tileInfoBox, SWT.BORDER | SWT.SINGLE);
		tileData.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		tileData.addListener(SWT.DefaultSelection, handler);
		
		new Label(tileDataGroup, SWT.NONE).setText("Passierbarkeit");
		
		maskPanel = new TileMaskPanel(tileDataGroup, SWT.BORDER);
		GridData gd = new GridData(128, 128);
		gd.horizontalSpan = 2;
		gd.minimumWidth = 128;
		gd.minimumHeight = 128;
		maskPanel.setLayoutData(gd);
//		maskPanel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
		maskPanel.addListener(handler);
		
		new Label(tileDataGroup, SWT.NONE).setText("Flags");
		
		flagList = new TileFlagPanel(tileDataGroup, context.getProject().getProject());
		gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd.horizontalSpan = 2;
		gd.minimumHeight = 128;
		flagList.getTable().setLayoutData(gd);
		flagList.addListener(handler);
	}
	
	private void setEditMode(int mode)
	{
		this.editMode = mode;
		
		editPassableOP.setSelection(mode == MODE_MASK);
		editPriorityOP.setSelection(mode == MODE_PRIORITIES);
		editTagsOP.setSelection(mode == MODE_TAGS);
		editFlagsOP.setSelection(mode == MODE_FLAGS);
		
		// Viewmode und Editmode sind äquivalent.
		tilesetPanel.setViewMode(mode);
	}
	
	private void selectTile(int id)
	{
		if (this.id == id) return;
		
		this.id = id;
		updateTileData();
	}
	
	private void updateTileData()
	{
		update = true;
		
		if (id >= 0)
		{
			StringBuilder builder = new StringBuilder(32);
			int p = tileset.getPassages()[id];
			if (p < 0) p += 1 << 16;
			appendHex(builder, p, 4);
			builder.append('-');
			appendHex(builder, tileset.getPriorities()[id], 2);
			builder.append('-');
			appendHex(builder, tileset.getTerrainTags()[id], 2);
			builder.append('-');
			appendHex(builder, tileset.getFlags()[id], 2);
			
			tileInfo.setText("ID: " + String.format("%04d", id));
			tileData.setText(builder.toString());
			flagList.setTilesetField(tileset, id);
		}
		else
		{
			tileInfo.setText("ID: -  ");
			tileData.setText("");
			flagList.clearTilesetField();
		}
		maskPanel.setTileID(id);
		tilesetPanel.refresh();
		
		update = false;
	}
	
	private void appendHex(StringBuilder builder, int value, int count)
	{
		if (value < 0) value += 1 << 16;
		String hexStr = Integer.toHexString(value).toUpperCase();
		for (int i = hexStr.length(); i < count; i++)
			builder.append('0');
		
		builder.append(hexStr);
	}
	
	private Listener getListener()
	{
		return new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				if (update) return;
				
				if (event.widget == editPassableOP) setEditMode(MODE_MASK);
				else if (event.widget == editPriorityOP) setEditMode(MODE_PRIORITIES);
				else if (event.widget == editTagsOP) setEditMode(MODE_TAGS);
				else if (event.widget == editFlagsOP) setEditMode(MODE_FLAGS);
				else if (event.widget == maskPanel)
				{
					context.fireDataChanged(TilesetEditor.this, tileset);
					updateTileData();
				}
				else if (event.widget == fileButton)
				{
					tileset.setTilesetName(fileButton.getResourceName());
					tilesetPanel.setTileset(tileset, context.getProject());
					maskPanel.refreshImage();
					tileSizeHint.setVisible(!tilesetPanel.isGridMatchingImage());
					context.fireDataChanged(TilesetEditor.this, tileset);
				}
				else if (event.widget == backgroundButton)
				{
					tileset.setBackgroundName(backgroundButton.getResourceName());
					context.fireDataChanged(TilesetEditor.this, tileset);
				}
				else if (event.widget == tileSizeButton)
				{
					tileset.setTileSize(tileSizeButton.getSelection());
					context.fireDataChanged(TilesetEditor.this, tileset);
					updateTileData();
					
					tileSizeHint.setVisible(!tilesetPanel.isGridMatchingImage());
				}
				else if (event.widget == tileData)
				{
					parseFieldData(tileData.getText());
					context.fireDataChanged(TilesetEditor.this, tileset);
				}
				else if (event.widget == flagList.getTable())
				{
					context.fireDataChanged(TilesetEditor.this, tileset);
					updateTileData();
				}
			}
		};
	}

	private void parseFieldData(String text)
	{
		if (tileset == null || id <= 0) throw new IllegalArgumentException("No field selected.");
		String[] data = text.split("-");
		if (data.length != 4) throw new IllegalArgumentException("Invalid field format.");
		
		short passage = Short.parseShort(data[0], 16);
		byte priority = Byte.parseByte(data[1], 16);
		byte tags = Byte.parseByte(data[2], 16);
		byte flags = Byte.parseByte(data[3], 16);
		
		tileset.getPassages()[id] = passage;
		tileset.getPriorities()[id] = priority;
		tileset.getTerrainTags()[id] = tags;
		tileset.getFlags()[id] = flags;
		updateTileData();
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (update) return;
		
		TileSelection tiles = (TileSelection) event.getSelection();
		if (tiles.isEmpty())
		{
			selectTile(-1);
			return;
		}
		
		selectTile(tiles.get(0, 0));
		
		switch(editMode)
		{
			case MODE_PASSAGES: editPassages(tiles); break;
			case MODE_PRIORITIES: editByteValue(tiles, tileset.getPriorities()); break;
			case MODE_TAGS: editByteValue(tiles, tileset.getTerrainTags()); break;
//			case MODE_FLAGS: editFlags(tiles); break;
		}
	}

	private void editPassages(TileSelection tiles)
	{
		short[] passages = tileset.getPassages();
		short p = (short) ((passages[tiles.get(0, 0)] == -1) ? 0 : -1);
		
		for(int x = 0; x < tiles.getWidth(); x++)
		for(int y = 0; y < tiles.getHeight(); y++)
		{
			int id = tiles.get(x, y);
			passages[id] = p;
		}
		updateTileData();
	}
	
	private void editByteValue(TileSelection tiles, byte[] data)
	{
		byte p = data[tiles.get(0, 0)];
		p = (byte) ((tilesetPanel.getLastMouseButton() == 1) ? p+1 : p-1);
		if (p < 0) p = 9;
		if (p > 9) p = 0;
		
		for(int x = 0; x < tiles.getWidth(); x++)
		for(int y = 0; y < tiles.getHeight(); y++)
		{
			int id = tiles.get(x, y);
			data[id] = p;
		}
		updateTileData();
	}

	@Override
	public void setFocus()
	{
		tilesetPanel.getGLCanvas().setFocus();
	}
}
