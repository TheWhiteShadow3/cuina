package cuina.debug;

import static cuina.Context.SESSION;

import cuina.Game;
import cuina.GameEvent;
import cuina.GameListener;
import cuina.util.LoadingException;
import cuina.util.ResourceManager;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

public class Debugger extends JFrame implements ActionListener, GameListener
{
	private static final long serialVersionUID = -6098038422649938126L;

	private Game game;
	private JTabbedPane mainTab;
	private Timer timer;
	private ArrayList<DebugPage> tabs = new ArrayList<DebugPage>();
	
	public Debugger(Game game)
	{
		setTitle("Debugger");
		setBounds(100, 100, 400, 400);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.game = game;

		try
		{
			ArrayList<Image> icons = new ArrayList<Image>(2);
			icons.add(ResourceManager.loadImageFromJar("CE_Icon16.png"));
			icons.add(ResourceManager.loadImageFromJar("CE_Icon32.png"));
			setIconImages(icons);
		}
		catch (LoadingException e)
		{
			e.printStackTrace();
		}
		initComponents();
		
		setVisible(true);
		addPage("Messages", new MessagePage());
		
		timer = new Timer(500, this);
		timer.setRepeats(true);
		timer.start();
	}
	
	public void setGame(Game game)
	{
		this.game = game;
	}
	
	@Override
	public void dispose()
	{
		timer.stop();
		super.dispose();
	}

	private void initComponents()
	{
		mainTab = new JTabbedPane();
		
		JMenuBar menu = new JMenuBar();
		
		JMenu menuGame = new JMenu("Spiel");
		
		menu.add(menuGame);
		
		setJMenuBar(menu);
		add(mainTab, BorderLayout.CENTER);
	}
	
	public void addPage(String name, JComponent page)
	{
		if (page instanceof DebugPage)
			tabs.add((DebugPage)page);
		mainTab.add(name, page);
	}
	
	public void addDataMap(String name, Map<String, Object> data)
	{
		addPage(name, new TabelPage(data));
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		for(DebugPage page : tabs)
		{
			page.update();
		}
	}
	
	@SuppressWarnings("serial")
	private class TabelPage extends JTable implements DebugPage
	{
		public TabelPage(Map<String, Object> data)
		{
			super(new DataModel(data));
		}
		
		@Override
		public void update()
		{
			((DataModel)getModel()).update();
		}
	}
	
	@SuppressWarnings("serial")
	private class DataModel extends AbstractTableModel
	{
		private Map<String, Object> data;
		private String[] names;
		private int size;
		
		public DataModel(Map<String, Object> data)
		{
			this.data = data;
			this.size = data.size();
			names = data.keySet().toArray( new String[data.keySet().size()] );
		}

		@Override
		public int getRowCount()
		{
			return data.size();
		}

		@Override
		public int getColumnCount()
		{
			return 3;
		}

		@Override
		public String getColumnName(int columnIndex)
		{
			switch(columnIndex)
			{
				case 0: return "Name";
				case 1: return "Type";
				case 2: return "Wert";
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return false;//(columnIndex > 0);
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			if (size <= row) return null;
			switch(column)
			{
				case 0: return names[row];
				case 1: 
					Object obj = data.get(names[row]);
					return obj != null ? obj.getClass().getName() : "";
				case 2: return data.get(names[row]);
			}
			return null;
		}
		
		public void update()
		{
			if (size != data.size())
			{
				size = data.size();
				names = data.keySet().toArray( new String[data.keySet().size()] );
			}
			fireTableChanged(new TableModelEvent(this, 0, size - 1));
		}

//		@Override
//		public void setValueAt(Object value, int rowIndex, int columnIndex)
//		{
//			if (columnIndex == 0)
//			{
//				data.get(rowIndex).name = (String)value;
//			}
//			else
//			{
//				data.get(rowIndex).setValue(value);
//			}
//		}
	}
	
	private class DataEntry
	{
		public String name;
		public Object value;
		public String valueString;
		
		public DataEntry(String name, Object value)
		{
			this.name = name;
			setValue(value);
		}
		
		public void setValue(Object value)
		{
			this.value = value;
			valueString = value.toString();
		}
	}
//	@Override
//	public void windowOpened(WindowEvent e) {}
//
//	@Override
//	public void windowClosing(WindowEvent e)
//	{
//		dispose();
//		
//	}
//
//	@Override
//	public void windowClosed(WindowEvent e) {}
//	public void windowIconified(WindowEvent e) {}
//	public void windowDeiconified(WindowEvent e) {}
//	public void windowActivated(WindowEvent e) {}
//	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void gameStateChanged(GameEvent ev)
	{
		switch(ev.type)
		{
			case GameEvent.OPEN_SESSION:
			case GameEvent.SESSION_LOADED:
				addDataMap("Session", Game.getContext(SESSION).getData());
				repaint();
				break;
				
			case GameEvent.NEW_SCENE:
				addDataMap("Scene", Game.getContext(SESSION).getData());
				repaint();
				break;
			
			case GameEvent.END_GAME:
				dispose();
				break;
		}
	}
	
}
