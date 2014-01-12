package cuina.network;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StreamUtils
{
	public static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static float byteArrayToFloat(byte[] buffer, int pos)
	{
		return Float.intBitsToFloat(byteArrayToInt(buffer, pos));
	}
	
	public static void floatToByteArray(byte[] buffer, int pos, float val)
	{
		intToByteArray(buffer, pos, Float.floatToIntBits(val));
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
	
	public static void intToByteArray(byte[] buffer, int pos, int val)
	{
		buffer[pos] = (byte) (val >>> 24);
		buffer[pos+1] = (byte) (val >>> 16);
		buffer[pos+2] = (byte) (val >>> 8);
		buffer[pos+3] = (byte) val;
	}
	
	public static String readString(byte[] buffer, int start)
	{
		int pos;
		for (pos = start; pos < buffer.length; pos++)
			if (buffer[pos] == 0) break;
		if (pos == start || pos == buffer.length) return null;
		return new String(buffer, start, pos - start);
	}
	
	public static String readString(ByteBuffer buffer)
	{
		int lenght = buffer.get();
		if (lenght < 0) return null;
		
		byte[] bytes = new byte[lenght];
		buffer.get(bytes);
		return new String(bytes, CHARSET);
	}
	
	public static void writeString(ByteBuffer buffer, String str)
	{
		if (str == null)
		{
			buffer.put((byte) -1);
			return;
		}
		buffer.put((byte) str.length());
		buffer.put(str.getBytes(CHARSET));
	}
}
