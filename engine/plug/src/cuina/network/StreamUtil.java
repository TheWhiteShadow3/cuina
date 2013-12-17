package cuina.network;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class StreamUtil
{
	public static int byteArrayToInt(byte[] buffer)
	{
		return byteArrayToInt(buffer, 0);
	}
	
	public static int byteArrayToInt(byte[] buffer, int pos)
	{
		if (buffer.length < pos+4) throw new ArrayIndexOutOfBoundsException();
		
		int
		value  = (0xFF & buffer[pos]) << 24;
		value |= (0xFF & buffer[pos+1]) << 16;
		value |= (0xFF & buffer[pos+2]) << 8;
		value |= (0xFF & buffer[pos+3]);

		return value;
	}

	public static byte[] intToByteArray(int val)
	{
		byte[] buffer = new byte[4];

		buffer[0] = (byte) (val >>> 24);
		buffer[1] = (byte) (val >>> 16);
		buffer[2] = (byte) (val >>> 8);
		buffer[3] = (byte) val;

		return buffer;
	}
	
	private static final byte[] LENGTH_BUFFER = new byte[4];
	
	public static Message read(InputStream in) throws IOException
	{
		int flag = in.read();
		int lenght = StreamUtil.byteArrayToInt(LENGTH_BUFFER);
		byte[] buffer = new byte[lenght];
		in.read(buffer);
		return new Message(flag, buffer);
	}
	
	public static String readString(byte[] buffer, int start)
	{
		int pos;
		for (pos = start; pos < buffer.length; pos++)
			if (buffer[pos] == 0) break;
		if (pos == start || pos == buffer.length) return null;
		return new String(buffer, start, pos - start);
	}
	
//	public static Map<String, String> readData(byte[] buffer)
//	{
//		String data = new String(buffer);
//		data.split(regex)
//	}
}
