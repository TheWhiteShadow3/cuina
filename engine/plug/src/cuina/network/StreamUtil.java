package cuina.network;

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
	
	
	public static String readString(byte[] buffer, int start)
	{
		int pos;
		for (pos = start; pos < buffer.length; pos++)
			if (buffer[pos] == 0) break;
		if (pos == start || pos == buffer.length) return null;
		return new String(buffer, start, pos - start);
	}
}
