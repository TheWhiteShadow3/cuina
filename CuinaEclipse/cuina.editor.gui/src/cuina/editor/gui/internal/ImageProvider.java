package cuina.editor.gui.internal;

import cuina.resource.ResourceException;

import de.matthiasmann.twl.renderer.Image;

public interface ImageProvider
{
	public Image createImage(String filename) throws ResourceException;
}
