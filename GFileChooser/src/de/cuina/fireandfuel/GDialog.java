package de.cuina.fireandfuel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

public abstract class GDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	public static final int RETURN_CANCEL = 0;
	public static final int RETURN_OK = 1;
	public static final int RETURN_IGNORE = 2;

	private int returnValue = 0;
	private Frame owner;
	private JPanel contentPane;
	private GDialogMenu dialog;

	public void setReturnValue(int value)
	{
		returnValue = value;
	}

	public int getReturnValue()
	{
		return returnValue;
	}

	/**
	 * Erstellt ein Dialog mit den Menü-Buttons OK und Abbrechen.
	 * 
	 * @param editor
	 */
	public GDialog(Frame owner)
	{
		this(owner, GDialogMenu.CMD_OK + GDialogMenu.CMD_CANCEL);
	}

	/**
	 * Erstellt ein Dialog mit den Menü-Buttons OK und Abbrechen.
	 * 
	 * @param editor
	 * @param dialogButtons
	 *            Menü-Buttons der Klasse {@link GDialogMenu}.
	 */
	public GDialog(Frame owner, int dialogButtons)
	{
		super(owner, true);
		if(owner == null)
		{
			// ArrayList<Image> icons = new ArrayList<Image>(2);
			// icons.add(Ress.getImage(Ress.ICON_16));
			// icons.add(Ress.getImage(Ress.ICON_32));
			// setIconImages(icons);
		}
		this.owner = owner;

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// setResizable(false);

		contentPane = new JPanel();
		dialog = new GDialogMenu(new DialogMenuHandler(), dialogButtons);

		super.addImpl(contentPane, BorderLayout.CENTER, -1);
		super.addImpl(dialog, BorderLayout.SOUTH, -1);
	}

	@Override
	public void setLayout(LayoutManager layout)
	{
		if(contentPane == null)
			super.setLayout(layout);
		else contentPane.setLayout(layout);
	}

	@Override
	protected void addImpl(Component comp, Object constraints, int index)
	{
		if(contentPane == null)
			super.addImpl(comp, constraints, index);
		else contentPane.add(comp, constraints, index);
	}

	public GDialogMenu getDialog()
	{
		return dialog;
	}

	/**
	 * Zeigt den Dialog an. Die Position entspricht dabei der, des Mauszeigers,
	 * aber immer 100px vom Bildschirmrand entfernt.
	 * 
	 * @return
	 */
	public int showDialog()
	{ // Setze Minimalgröße nur, Wenn noch nicht gesetzt.
	// System.out.println(getWidth());
		if(getWidth() == 0 || getHeight() == 0)
			pack();

		Dimension d = getToolkit().getScreenSize();
		Point p = MouseInfo.getPointerInfo().getLocation();
		setLocation(Math.min(Math.max(100, p.x - getWidth() / 2), d.width - getWidth() - 100),
				Math.min(Math.max(100, p.y - getHeight() / 2), d.height - getHeight() - 100));

		setVisible(true);
		return returnValue;
	}

	public Frame getOwner()
	{
		return owner;
	}

	// /**
	// * Initialisiert die Dialog-Elemente.
	// * @param contentPane Kontainer für Dialog-Elemente.
	// */
	// protected abstract void initComponents();

	/**
	 * Methode zum Übernehmen der Dialog-Informationen. Wird beim Klick auf OK
	 * oder Übernehmen aufgerufen.
	 */
	public abstract void apply();

	private class DialogMenuHandler implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			switch(e.getActionCommand().charAt(0))
			{
			// OK
			case 'O':
				apply();
				returnValue = RETURN_OK;
				dispose();
				break;
			// Abbrechen
			case 'A':
				returnValue = RETURN_CANCEL;
				dispose();
				break;
			// Übernehmen
			case 'Ü':
				apply();
				break;
			// Ignorieren
			case 'I':
				returnValue = RETURN_IGNORE;
				dispose();
				break;
			}
		}
	}
}
