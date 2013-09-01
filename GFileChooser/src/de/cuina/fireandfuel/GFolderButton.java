package de.cuina.fireandfuel;

import java.io.File;

import javax.swing.JButton;

public class GFolderButton extends JButton
{
	private static final long serialVersionUID = 1660905739545030527L;
	private File folder;

	public GFolderButton(File folder)
	{
		super(folder.getName());
		if(folder.equals(new File("/")))
			super.setText("/");
		if(folder.getAbsolutePath().matches("[a-zA-Z]:[\\\\]"))
			super.setText(folder.getAbsolutePath());
		if(folder.getAbsolutePath().equals("C:\\Users"))
			super.setText("Benutzer");
		this.folder = folder;
	}

	public File getFolder()
	{
		return folder;
	}

	public void setFolder(File folder)
	{
		this.folder = folder;
	}
}
