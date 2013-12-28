package cuina.network;

public interface ChatroomListener
{
	public void messageRecieved(Chatroom room, Chatroom.ChatMessage message);
	public void memberJoined(Chatroom room, Client client);
	public void memberLeaved(Chatroom room, Client client, boolean forced);
}
