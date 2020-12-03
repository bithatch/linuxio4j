/**
 * Linux I/O For Java - A JNA based library providing access to some low-level Linux subsystems
 * Copyright © 2012 Bithatch (tanktarta@gmail.com)
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.bithatch.linuxio;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * The Class FbColorMap.
 */
@FieldOrder({"start", "len", "red", "green", "blue", "transp"})
public class FbColorMap extends Structure {
	
	/** The start. */
	public int start;
	
	/** The len. */
	public int len;
	
	/** The red. */
	public short[] red;
	
	/** The green. */
	public short[] green;
	
	/** The blue. */
	public short[] blue;
	
	/** The transp. */
	public short[] transp;

	/**
	 * Instantiates a new fb color map.
	 *
	 * @param size the size
	 */
	public FbColorMap(int size) {
		red = new short[size];
		green = new short[size];
		blue = new short[size];
		transp = new short[size];
		len = size;
		allocateMemory();
	}

}
