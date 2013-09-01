package de.cuina.fireandfuel;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.LinkedList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Replacement for crappy-looking JFileChooser dialog with GTK look'n'feel<br>
 * (JRE 6 & 7 is not 100% compatible with newer GTK 2.6 releases and
 * incompatible to GTK 3.X)<br>
 * <br>
 * A open/save dialog from this class looks like a native GTK 2.6 / 3.X file
 * open/save dialog<br>
 * <br>
 * - compatible with most used JFileChooser methods<br>
 * - supports GTK's folder bookmarks<br>
 * - uses JFileChooser for non-GTK Desktops
 */
public class GFileChooser
{
	public static final int DIRECTORIES_ONLY = JFileChooser.DIRECTORIES_ONLY;
	public static final int FILES_ONLY = JFileChooser.FILES_ONLY;
	public static final int FILES_AND_DIRECTORIES = JFileChooser.FILES_AND_DIRECTORIES;

	public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
	public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
	public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;

	public static final int OPEN_DIALOG = JFileChooser.OPEN_DIALOG;
	public static final int SAVE_DIALOG = JFileChooser.SAVE_DIALOG;

	public static final int SINGLE_SELECTION = DefaultListSelectionModel.SINGLE_SELECTION;
	public static final int SINGLE_INTERVAL_SELECTION = DefaultListSelectionModel.SINGLE_INTERVAL_SELECTION;
	public static final int MULTIPLE_INTERVAL_SELECTION = DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION;

	private String currentDirectory = null;
	private GFileFilter fileFilter;

	private File[] selectedFiles;

	private JFileChooser nativeChooser = null;

	/**
	 * This class makes FileChooser's abstract FileFilter class and Java.IO's
	 * FileFilter interface compatible
	 * 
	 * for FileChooser you have to override getDescription(), too - otherwise a
	 * dialog from FileChooser does not show a description!
	 * 
	 */
	public class GFileFilter extends javax.swing.filechooser.FileFilter implements
			java.io.FileFilter
	{
		private int filterMode = FILES_AND_DIRECTORIES;
		private boolean hiddenFilesVisible = false;
		private LinkedList<LinkedList<String>> filterList = new LinkedList<LinkedList<String>>();
		
		private LinkedList<String> currentFilter;

		public GFileFilter()
		{
			addFilter(null, "*");
		}
		
		public void setDirectoriesOnly()
		{
			filterMode = DIRECTORIES_ONLY;
		}

		public void setFilesOnly()
		{
			filterMode = FILES_ONLY;
		}

		public void setDirectoriesAndFiles()
		{
			filterMode = FILES_AND_DIRECTORIES;
		}

		public int getFilterMode()
		{
			return filterMode;
		}
		
		public void setFilterMode(int mode)
		{
			filterMode = mode;
		}
		
		public boolean isHiddenFilesVisible()
		{
			return hiddenFilesVisible;
		}

		public void switchHiddenFilesVisible()
		{
			hiddenFilesVisible = !hiddenFilesVisible;
		}

		public void addFilter(String desciption, String... filterRule)
		{
			for(LinkedList<String> extsMeta : filterList)
			{
				for(String ext : filterRule)
					if(extsMeta.contains(ext))
						return;
			}

			LinkedList<String> exts = new LinkedList<String>();
			exts.add(desciption);
			for(String ext : filterRule)
				exts.add(ext);
			
			filterList.add(exts);
			currentFilter = exts;
		}

		public void removeFilter(String filterRule)
		{
			for(LinkedList<String> extsMeta : filterList)
			{
				for(String ext : extsMeta)
					if(ext.equals(filterRule))
					{
						filterList.remove(extsMeta);
					}
			}
		}

		public void clearFilters()
		{
			filterList.clear();
		}

		public LinkedList<String> getCurrentFilter()
		{
			return currentFilter;
		}
		
		public void setCurrentFilter(LinkedList<String> currentFilter)
		{
			this.currentFilter = currentFilter;
		}
		
		public void setCurrentFilterByIndex(int index)
		{
			this.currentFilter = filterList.get(index);
		}
		
		public LinkedList<LinkedList<String>> getFilters()
		{
			return filterList;
		}
		
		@Override
		public final boolean accept(File f)
		{
			if((f.getName().startsWith(".") || f.isHidden()) && !isHiddenFilesVisible())
			{
				return false;
			}

			if(filterMode == FILES_ONLY)
			{
				if(f.isDirectory())
					return false;
			}

			if(filterMode == DIRECTORIES_ONLY)
			{
				if(f.isFile())
					return false;
			}

			if(currentFilter == null)
				return true;
			else
			{
				for(int i = 1; i < currentFilter.size(); i++)
				{
					if(currentFilter.get(i).equals("*"))
						return true;
					if(f.isFile() && f.getName().endsWith(currentFilter.get(i)))
						return true;
				}
				if(f.isDirectory())
					return true;
			}

			return false;
		}

		@Override
		public String getDescription()
		{
			return currentFilter.get(0);
		}
		
		public String getDescription(int index)
		{
			return filterList.get(index).get(0);
		}

	}

	public GFileChooser(String startDirectory)
	{
		fileFilter = new GFileFilter();
		
		String osname = System.getProperty("os.name").toLowerCase();
		
		if(osname.contains("linux") || osname.contains("sunos") || osname.contains("windows"))
		{
			this.currentDirectory = startDirectory;
			
		} else 
		{
			nativeChooser = new JFileChooser(startDirectory);
		}
	}

	public int showOpenDialog(Component parent)
	{
		if(!(parent instanceof JFrame))
			return showOpenDialog(new JFrame(), SINGLE_SELECTION);
		return showOpenDialog((JFrame) parent, SINGLE_SELECTION);
	}

	public int showOpenDialog(Frame parent, int selectionMode)
	{
		int result = 0;
		if(nativeChooser != null)
		{
			nativeChooser.setFileFilter(fileFilter);
			result = nativeChooser.showOpenDialog(parent);
			selectedFiles = nativeChooser.getSelectedFiles();

			if(selectedFiles != null)
			{
				selectedFiles = new File[] { nativeChooser.getSelectedFile() };
			}

			return result;
		}

		GChooserDialog dialog = new GChooserDialog(parent, "Open ...", OPEN_DIALOG, this);
		dialog.getFileChooserTable().setSelectionMode(selectionMode);

		result = dialog.getReturnValue();

		switch(result)
		{
		case GDialog.RETURN_OK:
			return APPROVE_OPTION;

		case GDialog.RETURN_CANCEL:
			return CANCEL_OPTION;
		}

		return ERROR_OPTION;

	}

	public int showSaveDialog(Frame parent)
	{
		int result = 0;
		if(nativeChooser != null)
		{
			result = nativeChooser.showSaveDialog(parent);
			selectedFiles = nativeChooser.getSelectedFiles();

			if(selectedFiles != null)
			{
				selectedFiles = new File[] { nativeChooser.getSelectedFile() };
			}

			return result;
		}

		GChooserDialog dialog = new GChooserDialog(parent, "Save as ...", SAVE_DIALOG, this);
		dialog.getFileChooserTable().setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);

		result = dialog.getReturnValue();

		switch(result)
		{
		case GDialog.RETURN_OK:
			return APPROVE_OPTION;

		case GDialog.RETURN_CANCEL:
			return CANCEL_OPTION;
		}

		return ERROR_OPTION;
	}

	public File getCurrentDirectory()
	{
		return new File(currentDirectory);
	}

	public File getSelectedFile()
	{
		if(selectedFiles != null)
			if(selectedFiles.length == 1)
			{
				System.out.println("selected File: " + selectedFiles[0]);
				return selectedFiles[0];
			}
		return null;
	}

	public File[] getSelectedFiles()
	{
		if(selectedFiles != null)
		{
			System.out.println("selected Files:");
			for(File file : selectedFiles)
				System.out.println(file);
			return selectedFiles;
		}
		return null;
	}

	

	public static void main(String[] args)
	{
		String lnfName = UIManager.getSystemLookAndFeelClassName();
		try
		{
			UIManager.setLookAndFeel(lnfName);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		GFileChooser chooser = new GFileChooser(System.getProperty("user.dir"));
		chooser.getFileFilter().addFilter(null, ".java");
		chooser.getFileFilter().addFilter("java class", ".class", ".jar", ".zip");
		chooser.showOpenDialog(new JFrame());
		System.exit(0);
	}

	public void setAcceptAllFileFilterUsed(boolean b)
	{
		// unimplemented!
	}

	public String getCurrentWorkDirectory()
	{
		return currentDirectory;
	}

	public void setCurrentWorkDirectory(String currentDirectory)
	{
		this.currentDirectory = currentDirectory;
	}

	public void setSelectedFiles(File[] selectedFiles)
	{
		this.selectedFiles = selectedFiles;
	}

	public GFileFilter getFileFilter()
	{
		return fileFilter;
	}

}
