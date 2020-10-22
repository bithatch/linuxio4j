package uk.co.bithatch.linuxio;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"start", "len", "red", "green", "blue", "transp"})
public class FbColorMap extends Structure {
	public int start;
	public int len;
	public short[] red;
	public short[] green;
	public short[] blue;
	public short[] transp;

	public FbColorMap(int size) {
		red = new short[size];
		green = new short[size];
		blue = new short[size];
		transp = new short[size];
		len = size;
		allocateMemory();
	}

}
