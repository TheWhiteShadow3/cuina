package de.cuina.fireandfuel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JTable;
import javax.swing.Timer;

public class GTableSearcher
{
	private static String searchText = null;
	
	private static Timer timer = new Timer(2000, new ActionListener()
	{
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			searchText = null;
			((Timer) e.getSource()).stop();
		}
	});
	
	public static void searchInTable(JTable table, String text)
	{
		if(text.length() == 0)
		{
			return;
		}

		if(searchText == null)
		{
			timer.start();
			searchText = text;
		}	
		else
		{
			timer.restart();
			searchText += text;
		}
		
		for(int row = 0; row < table.getRowCount(); row++)
		{
			File val = (File) table.getValueAt(row, 0);
			String value = val != null ? val.getName() : "";
			if(value.toLowerCase().startsWith(searchText.toLowerCase()))
			{
				table.clearSelection();
				table.changeSelection(row, 0, false, false);
				return;
			}
		}
		
		stop();
	}
	
	public static void stop()
	{
		searchText = null;
		timer.stop();
	}
	
	private GTableSearcher()
	{}
}
