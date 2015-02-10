package com.nervepoint.linuxio;

/* LinuxIO4J - A Java library for working with Linux I/O systems.
 * 
 * Copyright (C) 2014 - Nervepoint Technologies
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

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

	@Override
	protected List<?> getFieldOrder() {
		return Arrays.asList("start", "len", "red", "green", "blue", "transp");
	}

}
