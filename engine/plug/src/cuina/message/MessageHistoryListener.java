package cuina.message;

public interface MessageHistoryListener
{
	public void textAdded(String text);
	public void cleared();
}
