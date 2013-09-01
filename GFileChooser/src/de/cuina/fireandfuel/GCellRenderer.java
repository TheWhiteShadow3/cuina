package de.cuina.fireandfuel;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;

/**
 * CellRenderer for JTables, must be called for every column of the table
 */
public class GCellRenderer extends JLabel implements TableCellRenderer
{
	private static final long serialVersionUID = -6275664432733476342L;
	private FileSystemView view = FileSystemView.getFileSystemView();
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column)
	{
		if(!System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			if(value instanceof File)
			{
				File file = (File) value;
				setText(file.getName());
				if(file.isDirectory())
					setIcon(new ImageIcon(this.getClass().getResource("icons/folder.png")));
				else setIcon(new ImageIcon(this.getClass().getResource("icons/file.png")));
			} else if(value != null)
				setText(value.toString());
		}
		else
		{
			
			if(value instanceof File)
			{
				File file = (File) value;
				if(file.getAbsolutePath().equals("C:\\Users"))
					setText("Benutzer");
				else
					setText(file.getName());
				setIcon(view.getSystemIcon(file));
			} else if(value != null)
				setText(value.toString());
			
		}
		

		setOpaque(true);

		if(isSelected)
			setBackground(new Color(0x0099FF));
		else if(row % 2 == 1)
			setBackground(new Color(SystemColor.controlHighlight.getRGB()));
		else setBackground(new Color(SystemColor.text.getRGB()));

		return this;
	}

}
