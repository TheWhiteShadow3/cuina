package de.cuina.fireandfuel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 * Dialog for choosing files
 */
public class GChooserDialog extends GDialog implements MouseListener, KeyListener, ActionListener,
		ItemListener
{
	private static final long serialVersionUID = -5493093067432063149L;

	private JPanel contentPane;

	private JTable fileChooserTable;
	private JTable locationsTable;

	private JPanel historyBar;
	private JScrollPane historyPane;

	private JTextField saveName;
	private JButton newFolder;

	private File[] files;

	private int mode;

	private GFileChooser parentInstance;

	private GTableModel fileChooserTableModel;

	private LinkedList<File> folderHistory = new LinkedList<File>();
	private HashMap<String, File> locations = new HashMap<String, File>();

	private JComboBox<String> fileTypeBox;

	private JButton addressBarButton;
	private JTextField addressField;

	public GChooserDialog(Frame owner, String name, int mode, GFileChooser parent)
	{
		super(owner);
		this.parentInstance = parent;

		initialSetup();
		detectLocations();

		setSize(800, 600);
		setMinimumSize(new Dimension(800, 600));
		setTitle(name);
		this.mode = mode;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		contentPane = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(1, 1, 1, 1);

		locationsTable = new JTable();
		setupLocations();

		setFileChooserTable(new JTable(fileChooserTableModel));
		getFileChooserTable().addMouseListener(this);
		getFileChooserTable().addKeyListener(this);

		Dimension saveNameDimension = new Dimension(630, 30);
		Dimension newFolderDimension = new Dimension(120, 30);
		Dimension historyDimension = new Dimension(630, 60);
		Dimension locationsDimension = new Dimension(150, 445);
		Dimension filesDimension = new Dimension(600, 380);
		Dimension fileTypeDimension = new Dimension(600, 30);

		for(int i = 0; i < getFileChooserTable().getColumnModel().getColumnCount(); i++)
			getFileChooserTable().getColumnModel().getColumn(i)
					.setCellRenderer(new GCellRenderer());

		getFileChooserTable().setRowSorter(fileChooserTableModel.getSorter());
		fileChooserTableModel.getSorter().toggleSortOrder(0);
		getFileChooserTable().setGridColor(new Color(SystemColor.controlHighlight.getRGB()));

		historyBar = new JPanel(new FlowLayout());
		historyPane = new JScrollPane(historyBar);
		setupHistoryBar();

		historyPane.setMinimumSize(historyDimension);
		historyPane.setPreferredSize(historyDimension);

		JScrollPane locationsPane = new JScrollPane(locationsTable);
		locationsPane.setMinimumSize(locationsDimension);
		locationsPane.setPreferredSize(locationsDimension);

		JScrollPane filesPane = new JScrollPane(getFileChooserTable());
		filesPane.setMinimumSize(filesDimension);
		filesPane.setPreferredSize(filesDimension);

		setupFileTypeBox();
		fileTypeBox.setMinimumSize(fileTypeDimension);
		fileTypeBox.setPreferredSize(fileTypeDimension);

		if(mode == GFileChooser.SAVE_DIALOG)
		{
			JPanel savePanel = new JPanel(new FlowLayout());

			saveName = new JTextField();
			saveName.setMinimumSize(saveNameDimension);
			saveName.setPreferredSize(saveNameDimension);
			saveName.setMaximumSize(saveNameDimension);
			saveName.addActionListener(this);

			newFolder = new JButton("New Folder", new ImageIcon(this.getClass().getResource("icons/folder-new.png")));
			newFolder.setMinimumSize(newFolderDimension);
			newFolder.setPreferredSize(newFolderDimension);
			newFolder.setMaximumSize(newFolderDimension);
			newFolder.addActionListener(this);

			savePanel.add(saveName, FlowLayout.LEFT);
			savePanel.add(newFolder);

			c.gridwidth = 4;
			c.gridheight = 1;
			c.weightx = 4;
			c.weighty = 1;
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.NORTHWEST;
			contentPane.add(savePanel, c);

			getFileChooserTable().setSelectionMode(GFileChooser.SINGLE_SELECTION);
		}

		c.gridwidth = 7;
		c.gridheight = 1;
		c.weightx = 7;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 1;
		contentPane.add(historyPane, c);

		c.gridwidth = 1;
		c.gridheight = 6;
		c.weightx = 1;
		c.weighty = 6;
		c.gridx = 0;
		c.gridy = 2;
		c.anchor = GridBagConstraints.NORTHWEST;
		contentPane.add(locationsPane, c);

		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 1;
		c.gridy = 2;
		c.gridwidth = 3;
		c.gridheight = 4;
		c.weightx = 3;
		c.weighty = 3;
		contentPane.add(filesPane, c);

		c.gridwidth = 3;
		c.gridheight = 1;
		c.weightx = 3;
		c.weighty = 1;
		c.gridx = 1;
		c.gridy = 7;
		contentPane.add(fileTypeBox, c);

		addImpl(contentPane, null, 0);
		super.showDialog();
	}

	private void setupLocations()
	{
		String[] row = { "Locations" };

		Set<String> keys = locations.keySet();

		Object[][] columns = new Object[keys.size()][1];
		int index = 0;

		for(String key : keys)
		{
			columns[index][0] = key;
			index++;
		}

		GTableModel model = new GTableModel(row, columns);
		
		locationsTable.setModel(model);
		
		locationsTable.getColumnModel().getColumn(0).setCellRenderer(new GCellRenderer());
		locationsTable.setShowGrid(false);
		locationsTable.setRowSorter(model.getSorter());
		model.getSorter().toggleSortOrder(0);
		
		locationsTable.addMouseListener(this);
		locationsTable.addKeyListener(this);
	}

	private void setupHistoryBar()
	{
		historyBar.removeAll();

		for(int i = 0; i < folderHistory.size(); i++)
		{
			GFolderButton button = new GFolderButton(folderHistory.get(i));
			historyBar.add(button, FlowLayout.LEFT);
			button.addActionListener(this);
		}
		addressBarButton = new JButton(new ImageIcon(this.getClass().getResource("icons/path-enter.png")));
		addressBarButton.addActionListener(this);

		historyBar.add(addressBarButton, FlowLayout.LEFT);
		historyBar.revalidate();
		historyPane.repaint();
	}

	private void setupAddressBar()
	{
		historyBar.removeAll();

		addressField = new JTextField(parentInstance.getCurrentWorkDirectory());
		addressField.addActionListener(this);

		historyBar.add(addressField);

		historyBar.add(addressBarButton, FlowLayout.LEFT);

		historyBar.revalidate();
		historyPane.repaint();
	}

	private void setupFileTypeBox()
	{
		Vector<String> fileDescriptions = new Vector<String>();

		if(parentInstance.getFileFilter().getFilterMode() == GFileChooser.DIRECTORIES_ONLY)
		{
			fileDescriptions.add("Directories");
		} else
		{
			LinkedList<LinkedList<String>> filters = parentInstance.getFileFilter().getFilters();

			for(int i = 0; i < filters.size(); i++)
			{
				String desc = parentInstance.getFileFilter().getDescription(i);

				if(desc == null)
					desc = "";
				else desc += " - ";

				for(int c = 1; c < filters.get(i).size(); c++)
				{
					if(!filters.get(i).get(c).equals("*"))
						desc += filters.get(i).get(c);
					else desc += "All";

					if(c < filters.get(i).size() - 1)
						desc += ",";

					desc += " ";
				}

				desc += "Files";

				fileDescriptions.add(desc);

			}
		}

		fileTypeBox = new JComboBox<String>(fileDescriptions);
		if(fileDescriptions.size() > 0)
		{
			fileTypeBox.setSelectedIndex(1);
			parentInstance.getFileFilter().setCurrentFilterByIndex(1);
		}
		fileTypeBox.addItemListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() instanceof GFolderButton)
		{
			GFolderButton source = (GFolderButton) e.getSource();

			for(int i = 0; i < folderHistory.size(); i++)
			{
				if(source.getFolder().equals(folderHistory.get(i)))
				{
					File dir = folderHistory.get(i);

					File[] newFiles = listFiles(dir);
					fileChooserTableModel = new GTableModel(newFiles);
					getFileChooserTable().setModel(fileChooserTableModel);
					getFileChooserTable().setRowSorter(fileChooserTableModel.getSorter());
					fileChooserTableModel.getSorter().toggleSortOrder(0);
					
					for(int d = 0; d < getFileChooserTable().getColumnModel().getColumnCount(); d++)
						getFileChooserTable().getColumnModel().getColumn(d)
								.setCellRenderer(new GCellRenderer());

					getFileChooserTable().revalidate();

					parentInstance.setCurrentWorkDirectory(dir.getAbsolutePath());

					return;
				}
			}
		}

		if(e.getSource().equals(newFolder))
		{
			String newFolderName = (String) JOptionPane.showInputDialog(new JFrame(),
					"New folders name:", "New Folder", JOptionPane.QUESTION_MESSAGE, null, null,
					"New Folder");
			File newFolder = new File(parentInstance.getCurrentWorkDirectory() + "/"
					+ newFolderName);
			if(newFolder.mkdir())
			{
				File[] newFiles = listFiles(newFolder);
				fileChooserTableModel = new GTableModel(newFiles);
				getFileChooserTable().setModel(fileChooserTableModel);

				getFileChooserTable().setRowSorter(fileChooserTableModel.getSorter());
				fileChooserTableModel.getSorter().toggleSortOrder(0);

				folderHistory.push(newFolder);
				setupHistoryBar();
				contentPane.revalidate();
				parentInstance.setCurrentWorkDirectory(newFolder.getAbsolutePath());
			}

		}

		if(e.getSource().equals(addressBarButton))
		{
			if(addressField == null)
				setupAddressBar();
			else
			{
				setupHistoryBar();
				addressField = null;
			}
		}

		if(e.getSource().equals(addressField))
		{

			File folder = new File(addressField.getText());
			if(folder.exists())
			{
				files = new File[] { folder };
				selectFileOrChangeFolder();
			}
			addressField = null;
		}

		if(e.getSource().equals(saveName))
		{
			if(files != null)
				if(saveName.getText().equals(files[0].getName()))
					return;
			files = new File[] { new File(parentInstance.getCurrentWorkDirectory() + "/"
					+ saveName.getText()) };
		}

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			if(e.getSource().equals(getFileChooserTable()))
			{
				setFilesFromFileTable();
				if(e.getClickCount() == 2 && files != null)
				{
					selectFileOrChangeFolder();
				}
			}

			if(e.getSource().equals(locationsTable))
			{
				setDirectoryFromLocationsTable();
				selectFileOrChangeFolder();
			}
		}

		if(e.getButton() == MouseEvent.BUTTON3)
		{
			JPopupMenu menu = new JPopupMenu();
			JMenuItem refresh = new JMenuItem("refresh", new ImageIcon(this.getClass().getResource("icons/view-refresh.png")));
			refresh.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					files = new File[] { parentInstance.getCurrentDirectory() };
					selectFileOrChangeFolder();
				}
			});

			JCheckBoxMenuItem hiddenFiles = new JCheckBoxMenuItem("show hidden files",
					parentInstance.getFileFilter().isHiddenFilesVisible());
			hiddenFiles.addActionListener(new ActionListener()
			{

				@Override
				public void actionPerformed(ActionEvent e)
				{
					parentInstance.getFileFilter().switchHiddenFilesVisible();
					files = new File[] { parentInstance.getCurrentDirectory() };
					selectFileOrChangeFolder();
				}
			});

			menu.add(refresh);
			menu.add(hiddenFiles);
			menu.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	private void setDirectoryFromLocationsTable()
	{
		Object key = locationsTable.getValueAt(locationsTable.getSelectedRow(), 0);
		files = new File[] { locations.get(key) };
	}

	private void setFilesFromFileTable()
	{
		int selectionMode = getFileChooserTable().getSelectionModel().getSelectionMode();

		if(selectionMode == GFileChooser.SINGLE_SELECTION)
		{
			files = new File[] { (File) getFileChooserTable().getValueAt(
					getFileChooserTable().getSelectedRow(), 0) };
			if(mode == GFileChooser.SAVE_DIALOG)
			{
				File file = files[0];
				if(file.isFile())
					saveName.setText(files[0].getName());
			}
		}

		if(selectionMode == GFileChooser.SINGLE_INTERVAL_SELECTION
				|| selectionMode == GFileChooser.MULTIPLE_INTERVAL_SELECTION)
		{
			int[] rows = getFileChooserTable().getSelectedRows();
			files = new File[getFileChooserTable().getSelectedRowCount()];
			for(int i = 0; i < rows.length; i++)
			{
				files[i] = (File) getFileChooserTable().getValueAt(rows[i], 0);
			}
		}
	}

	private void selectFileOrChangeFolder()
	{
		if(files[0].isFile())
			apply();
		else
		{
			File dir = files[0];

			File[] newFiles = listFiles(dir);
			fileChooserTableModel = new GTableModel(newFiles);
			getFileChooserTable().setModel(fileChooserTableModel);

			getFileChooserTable().setRowSorter(fileChooserTableModel.getSorter());
			fileChooserTableModel.getSorter().toggleSortOrder(0);

			if(parentInstance.getCurrentWorkDirectory().contains(dir.getAbsolutePath())
					&& !dir.equals(folderHistory.peek())
					&& !dir.getAbsolutePath().equals(parentInstance.getCurrentWorkDirectory())
					&& !dir.equals(new File("/")))
			{
				folderHistory.push(dir);
				setupHistoryBar();
				contentPane.revalidate();
				parentInstance.setCurrentWorkDirectory(dir.getAbsolutePath());
			} else
			{
				if(!dir.getName().equals(folderHistory.peek()))
				{
					String[] newHistory;
					String element = "";
					if(System.getProperty("os.name").toLowerCase().contains("windows"))
					{
						newHistory = dir.getAbsolutePath().split("[\\\\]");
						System.out.println(newHistory[0]);
						element = newHistory[0] + "\\";
					} else
					{
						newHistory = dir.getAbsolutePath().split(File.separator);
						element = "/";
					}
					folderHistory = new LinkedList<File>();
					folderHistory.push(new File(element));
					for(int i = 1; i < newHistory.length; i++)
					{
						element += newHistory[i] + "/";
						folderHistory.push(new File(element));
					}

					setupHistoryBar();
					contentPane.revalidate();
				}
				parentInstance.setCurrentWorkDirectory(dir.getAbsolutePath());
			}

			for(int i = 0; i < getFileChooserTable().getColumnModel().getColumnCount(); i++)
				getFileChooserTable().getColumnModel().getColumn(i)
						.setCellRenderer(new GCellRenderer());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		if(e.getSource().equals(fileTypeBox))
		{
			int index = fileTypeBox.getSelectedIndex();

			if(index > -1)
			{
				parentInstance.getFileFilter().setCurrentFilterByIndex(index);
				files = new File[] { parentInstance.getCurrentDirectory() };
				selectFileOrChangeFolder();

			}

		}

	}

	@Override
	public void apply()
	{
		boolean closeit = true;
		if(files != null)
		{
			if(mode == GFileChooser.SAVE_DIALOG && files[0].exists())
			{
				int option = JOptionPane.showConfirmDialog(new JFrame(), "Override file "
						+ files[0].getName() + " ?", "Override file " + files[0].getName() + " ?",
						JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.YES_OPTION)
					parentInstance.setSelectedFiles(files);
			} else parentInstance.setSelectedFiles(files);
		} else
		{
			if(mode == GFileChooser.SAVE_DIALOG)
				if(saveName.getText() != null && !saveName.getText().equals(""))
					parentInstance.setSelectedFiles(new File[] { new File(parentInstance
							.getCurrentWorkDirectory() + "/" + saveName.getText()) });
		}

		if(closeit)
		{
			super.setVisible(false);
			super.dispose();
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if(e.getSource().equals(fileChooserTable))
		{
			switch(e.getKeyCode())
			{
			case KeyEvent.VK_ENTER:
				setFilesFromFileTable();
				selectFileOrChangeFolder();
				break;

			case KeyEvent.VK_BACK_SPACE:
				files = new File[] { folderHistory.get(1) };
				selectFileOrChangeFolder();
				break;

			case KeyEvent.VK_ESCAPE:
				GTableSearcher.stop();
				break;

			case KeyEvent.VK_F5:
				files = new File[] { folderHistory.get(0) };
				selectFileOrChangeFolder();
				break;

			default:
				String key = new String(new char[] { e.getKeyChar() });
				if(key.matches("[a-zA-z0-9.,]"))
					GTableSearcher.searchInTable(fileChooserTable, key);
				break;

			}
		}
		if(e.getSource().equals(locationsTable))
		{
			if(e.getKeyCode() == KeyEvent.VK_ENTER)
			{
				setDirectoryFromLocationsTable();
				selectFileOrChangeFolder();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	public JTable getFileChooserTable()
	{
		return fileChooserTable;
	}

	public void setFileChooserTable(JTable fileChooserTable)
	{
		this.fileChooserTable = fileChooserTable;
	}

	private void initialSetup()
	{
		String[] newHistory;
		String element = "";
		if(System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			newHistory = parentInstance.getCurrentWorkDirectory().split("[\\\\]");
			System.out.println(newHistory[0]);
			element = newHistory[0] + "\\";
		} else
		{
			newHistory = parentInstance.getCurrentWorkDirectory().split(File.separator);
			element = "/";
		}

		folderHistory = new LinkedList<File>();
		folderHistory.push(new File(element));

		for(int i = 1; i < newHistory.length; i++)
		{
			element += newHistory[i] + "/";
			folderHistory.push(new File(element));
		}

		File[] files = listFiles(new File(parentInstance.getCurrentWorkDirectory()));

		fileChooserTableModel = new GTableModel(files);
	}

	public File[] listFiles(File directory)
	{
		File[] files = null;

		if(directory != null)
		{
			if(parentInstance.getFileFilter() != null)
				files = directory.listFiles(parentInstance.getFileFilter());
			else files = directory.listFiles();

			if(files != null)
			{
				Arrays.sort(files, new Comparator<File>()
				{
					@Override
					public int compare(File o1, File o2)
					{
						if(o1.isDirectory() && o2.isFile())
							return -1;

						if(o1.isFile() && o2.isDirectory())
							return 1;

						return o1.getName().compareTo(o2.getName());
					}
				});
			}
		}

		return files;
	}

	private void detectLocations()
	{
		locations.put(System.getProperty("user.name"), new File(System.getProperty("user.home")));
		locations.put("Desktop", new File(System.getProperty("user.home") + "/Desktop"));

		if(System.getProperty("os.name").toLowerCase().contains("windows"))
		{

			for(char i = 'A'; i <= 'Z'; i++)
			{
				File device = new File(i + ":\\");
				if(device.exists())
					locations.put(i + ":", device);
			}

		} else
		{
			locations.put("File System", new File("/"));

			File mountDir = new File("/media");
			if(mountDir.exists())
				if(mountDir.isDirectory())
				{
					File[] mountPoints = mountDir.listFiles(new FileFilter()
					{

						@Override
						public boolean accept(File pathname)
						{
							if(pathname.isDirectory())
								return true;
							return false;
						}

					});

					for(File mountPoint : mountPoints)
						locations.put(mountPoint.getName() + " (mounted)", mountPoint);
				}

			File bookmarksFile = new File(System.getProperty("user.home") + "/.gtk-bookmarks");

			if(bookmarksFile.exists())
				if(bookmarksFile.isFile())
				{
					try
					{
						BufferedReader reader = new BufferedReader(new FileReader(bookmarksFile));
						String line = "";

						while((line = reader.readLine()) != null)
						{
							String[] content = line.split("\\s", 2);
							if(content[0].startsWith("file:/"))
							{
								URI uri = new URI(content[0]);
								if(content.length == 2)
								{
									locations.put(content[1], new File(uri));
								} else
								{
									String[] folders = content[0].split("/");
									locations.put(folders[folders.length - 1], new File(uri));
								}

							}

						}
					} catch (FileNotFoundException e)
					{
						System.err.println("GTK bookmarks file not found!");
					} catch (IOException e)
					{
						e.printStackTrace();
					} catch (URISyntaxException e)
					{
						System.err.println("Malformed GTK bookmarks file!");
					}
				}
		}

	}

	public void addLocation(String location)
	{
		File dir = new File(location);
		if(dir.exists())
			if(dir.isDirectory() && !locations.containsKey(dir.getName()))
				locations.put(dir.getName(), dir);
	}

}