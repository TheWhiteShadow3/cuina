package cuina.editor.object;


public interface IExtensionContext
{
	public ObjectAdapter getObjectAdapter();
	
	public void fireDataChanged();
	
	public void setErrorMessage(String message);
}
