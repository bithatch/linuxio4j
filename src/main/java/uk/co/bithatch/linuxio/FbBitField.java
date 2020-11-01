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

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * The Class FbBitField.
 */
public class FbBitField extends Structure {
	/* beginning of bitfield */
	public int offset;
	
	/* length of bitfield */
	public int length; 
	
	/* !=0: Most significant bit is right */
	public int msb_right; 
	
	/* max value (calculated from length) */
	private int max; 

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("offset", "length", "msb_right");
	}

	/**
	 * Gets the max.
	 *
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * Read.
	 */
	@Override
	public void read() {
		super.read();
		max = pow(2, length) - 1;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "FbBitField [offset=" + offset + ", length=" + length
				+ ", msb_right=" + msb_right + "]";
	}

	private static int pow(int v, int pow) {
		int result = 1;
		for (int i = 1; i <= pow; i++) {
			result *= v;
		}
		return result;
	}

}
