package cuina.editor.script.library;

public interface Definition
{
	public String getID();
	public String getLabel();
	public Definition getParent();
	public String getType();
}
