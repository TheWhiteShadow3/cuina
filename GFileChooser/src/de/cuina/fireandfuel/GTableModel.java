package de.cuina.fireandfuel;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Date;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 * Model for a JTable a buildin sorter is included
 * 
 */
public class GTableModel implements TableModel, Comparator<Object>
{
	private String[] columnNames;
	private Object[][] data;

	TableRowSorter<GTableModel> sorter;

	public GTableModel(String[] columnNames, Object[][] data)
	{
		this.columnNames = columnNames;
		this.data = data;

		sorter = new TableRowSorter<GTableModel>(this);
		sorter.setSortsOnUpdates(true);
		for(int i = 0; i < getColumnCount(); i++)
			sorter.setComparator(i, this);
	}

	public GTableModel(File[] files)
	{
		this.columnNames = new String[] { "Name", "Size", "Date" };

		if(files != null)
		{
			data = new Object[files.length][];

			for(int i = 0; i < files.length; i++)
			{
				if(files[i].isFile())
					data[i] = new Object[] { files[i], formatBinaryPrefix(files[i].length()),
							DateFormat.getDateInstance().format(new Date(files[i].lastModified())) };
				else data[i] = new Object[] { files[i], "",
						DateFormat.getDateInstance().format(new Date(files[i].lastModified())) };
			}
		}
		sorter = new TableRowSorter<GTableModel>(this);
		sorter.setSortsOnUpdates(true);
		for(int i = 0; i < getColumnCount(); i++)
			sorter.setComparator(i, this);

	}

	private String formatBinaryPrefix(long size)
	{
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "EB" };
		if(size == 0)
			return 0 + " B";
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " "
				+ units[digitGroups];
	}

	@Override
	public int getRowCount()
	{
		if(data != null)
			return data.length;
		return 0;
	}

	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex)
	{
		return columnNames[columnIndex];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return columnNames[columnIndex].getClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if(rowIndex < data.length)
			if(columnIndex < data[rowIndex].length)
				return data[rowIndex][columnIndex];
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
	}

	@Override
	public void addTableModelListener(TableModelListener l)
	{
	}

	@Override
	public void removeTableModelListener(TableModelListener l)
	{
	}

	@Override
	public int compare(Object o1, Object o2)
	{
		if(o1 instanceof File && o2 instanceof File)
		{
			File f1 = (File) o1;
			File f2 = (File) o2;
			if(f1.isDirectory() && f2.isFile())
				return -1;

			if(f1.isFile() && f2.isDirectory())
				return 1;

			return f1.getName().compareTo(f2.getName());
		}
		if(o1 instanceof String && o2 instanceof String)
		{
			String s1 = (String) o1;
			String s2 = (String) o2;

			return s1.compareTo(s2);
		}
		return 0;
	}

	public TableRowSorter<GTableModel> getSorter()
	{
		return sorter;
	}

}
