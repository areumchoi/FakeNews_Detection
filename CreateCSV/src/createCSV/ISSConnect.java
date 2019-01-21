package createCSV;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;


////////////////////////////////////////////////////////////
//ISSConnect
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
// ISSSocketOutputStream
////////////////////////////////////////////////////////////


class SocketOutputStream {

private int bufferSize = 1024;
private byte[] buffer = new byte[bufferSize];
private long bufferStart = 0;           // position in file of buffer
private int bufferPosition = 0;         // position in buffer
private OutputStream out;

public SocketOutputStream(OutputStream outputStream){

	out = outputStream;

}

public void writeByte(byte b) throws IOException {

	if (bufferPosition >= bufferSize)
		flush();
	buffer[bufferPosition++] = b ;
}	

public void writeBytes(byte[] b, int length) throws IOException {

	for (int i = 0; i < length; i++)		  // read byte-by-byte
		writeByte(b[i]);
}


public void writeString(String s) throws IOException {

	writeVInt( s.length());
	writeChars(s.getBytes("UTF-16BE"), s.length());
}

public void writeChars(byte b[], int length) throws IOException {
	
	for (int i = 0; i < length*2; i++) 
	{
		writeByte(b[i]);
	}
}

public void writeShort(short i) throws IOException
{
	writeByte((byte)(i >>  8));
    writeByte((byte) i);
}

public void writeInt(int i) throws IOException {
	writeByte((byte)(i >> 24));
	writeByte((byte)(i >> 16));
	writeByte((byte)(i >>  8));
	writeByte((byte) i);
}


public final void writeVInt(int i) throws IOException {
	while ((i & ~0x7F) != 0) {
	  writeByte((byte)((i & 0x7f) | 0x80));
	  i >>>= 7;
	}
	writeByte((byte)i);
}


public void writeLong(long i) throws IOException {
	writeInt((int) (i >> 32));
	writeInt((int) i);
}


public final void writeVLong(long i) throws IOException {
	while ((i & ~0x7F) != 0) {
	  writeByte((byte)((i & 0x7f) | 0x80));
	  i >>>= 7;
	}
	writeByte((byte)i);
}



public void close() throws IOException {
	flush();
	out.close();
	buffer = null;
	bufferPosition = 0;
}


public long getFilePointer() {
return bufferStart + bufferPosition;
}

public void flush() throws IOException
{
	if(bufferPosition>0)
	{
		out.write(buffer,0,bufferPosition);
		out.flush();
	}
	bufferPosition = 0;
}

}



////////////////////////////////////////////////////////////
//SocketInputStream
////////////////////////////////////////////////////////////


class SocketInputStream {

	private int bufferSize = 1024;
	private byte[] buffer = new byte[bufferSize];
	private int bufferPosition = 0;         // position in buffer
	private int bufferLength = 0;
	private InputStream in;
	

	
	
	public SocketInputStream(InputStream inputStream){

		in = inputStream;

	}
	
	 public byte readByte() throws IOException {
		if (bufferPosition >= bufferLength)
			refill();
		return buffer[bufferPosition++];
	}
	

	public void readBytes(byte[] b, int offset, int len) throws IOException
	{
		for (int i = 0; i < len; i++)		  // read byte-by-byte
			b[i + offset] = readByte();
	}
	 public  String readString() throws IOException {
	
	    int length = readVInt();
	    final byte[] chars = new byte[length*2];
	    readChars(chars, length);
	    return new String(chars,0,length*2,"UTF-16BE");
	 }
	 
	 public void readChars(byte[] cs, int length)throws IOException {
		    for (int i = 0; i < length*2; i++) 
			{
				byte b = readByte();		
				cs[i] = b;
			}
		    
	 }

	  public short readShort() throws IOException {
	    return (short) (((readByte() & 0xFF) <<  8) |  (readByte() & 0xFF));
	  }


	  public int readInt() throws IOException {
	    return ((readByte() & 0xFF) << 24) | ((readByte() & 0xFF) << 16)
	         | ((readByte() & 0xFF) <<  8) |  (readByte() & 0xFF);
	  }


	  public int readVInt() throws IOException {

	    byte b = readByte();
	    int i = b & 0x7F;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 7;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 14;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7F) << 21;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    assert (b & 0x80) == 0;
	    return i | ((b & 0x7F) << 28);
	  }


	  public long readLong() throws IOException {
	    return (((long)readInt()) << 32) | (readInt() & 0xFFFFFFFFL);
	  }

	  public long readVLong() throws IOException {

	    byte b = readByte();
	    long i = b & 0x7FL;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 7;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 14;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 21;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 28;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 35;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 42;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    i |= (b & 0x7FL) << 49;
	    if ((b & 0x80) == 0) return i;
	    b = readByte();
	    assert (b & 0x80) == 0;
	    return i | ((b & 0x7FL) << 56);
	  }

	  private void refill() throws IOException
	  {
	  	
		  	if(buffer == null)
		  	{
		  		buffer = new byte[bufferSize];		
		  	}
  	
		  	bufferLength = in.read(buffer, 0, bufferSize);
		  	bufferPosition = 0;
		  	
	
		  	if(bufferLength==0 || bufferLength==-1)
		  	{
		  		throw new IOException("receiveSocketData read past EOF");

		  	}
	  }
}

