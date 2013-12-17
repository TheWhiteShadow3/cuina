package cuina.network;


public class ClientChannel extends Channel
{
//	private String serverName;
//	private int latenz;
//	
	

//	public ClientChannel(String host, int port, String name) throws NetworkException
//	{
//		try
//		{
//			open(new Socket(host, port));
//			
//			while(in.available() == 0)
//			{
//				try
//				{
//					Thread.sleep(5);
//				}
//				catch (InterruptedException e)
//				{
//					e.printStackTrace();
//				}
//			}
//			
//			byte[] buffer;
//			buffer = new byte[1024];
//			in.read(buffer);
//
//			this.serverName = StreamUtil.readString(buffer, 0);
//			this.latenz = (int) System.currentTimeMillis() - StreamUtil.byteArrayToInt(buffer, serverName.length() + 1);
//			this.id = StreamUtil.byteArrayToInt(buffer, serverName.length() + 7);
//			
//			System.out.println("ID: " + id);
//			System.out.println("Latenz: " + latenz);
//			
//			out.write(name.getBytes());
//			out.write(0);
//			out.flush();
//			
////			System.out.println("Bytes: " + Arrays.toString(buffer));
////			System.out.println("Text:  " + new String(buffer));
//		}
//		catch (IOException e)
//		{
//			throw new NetworkException(e);
//		}
//	}
}
