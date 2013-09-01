package de.cuina.fireandfuel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GDialogMenu extends JPanel
{
	private static final long serialVersionUID = 1L;

	/** OK-Button (Action-Command: 'OK') **/
	public static final int CMD_OK = 1;
	/** Abbrechen-Button (Action-Command: 'Abbrechen') **/
	public static final int CMD_CANCEL = 2;
	/** Übernehmen-Button (Action-Command: 'Übernehmen') **/
	public static final int CMD_APPLY = 4;
	/** Ignorieren-Button (Action-Command: 'Ignorieren') **/
	public static final int CMD_IGNORE = 8;
	/** OK/Abbrechen/Übernehmen-Button zusammen **/
	public static final int CMD_OK_CANCEL_APPLY = 7;

	private JButton[] buttons = new JButton[4];
	// Button-Größe
	private Dimension d = new Dimension(108, 24);

	public GDialogMenu(ActionListener al, int buttons)
	{
		((FlowLayout) getLayout()).setAlignment(FlowLayout.RIGHT);

		if((buttons & CMD_OK) != 0)
			addButton(0, al, "OK");
		if((buttons & CMD_CANCEL) != 0)
			addButton(1, al, "Abbrechen");
		if((buttons & CMD_APPLY) != 0)
			addButton(2, al, "Übernehmen");
		if((buttons & CMD_IGNORE) != 0)
			addButton(3, al, "Ignorieren");
	}

	private void addButton(int index, ActionListener al, String cmd)
	{
		JButton b = new JButton(cmd);
		b.setActionCommand(cmd);
		b.addActionListener(al);
		b.setPreferredSize(d);
		this.add(b);
		buttons[index] = b;
	}

	public JButton getButton(int type)
	{
		switch(type)
		{
		case CMD_OK:
			return buttons[0];
		case CMD_CANCEL:
			return buttons[1];
		case CMD_APPLY:
			return buttons[2];
		case CMD_IGNORE:
			return buttons[3];
		}
		return null;
	}
}
