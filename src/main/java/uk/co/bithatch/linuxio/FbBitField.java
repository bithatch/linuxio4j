package uk.co.bithatch.linuxio;

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

import com.sun.jna.Structure;

public class FbBitField extends Structure {

	public int offset; /* beginning of bitfield */
	public int length; /* length of bitfield */
	public int msb_right; /* !=0: Most significant bit is right */

	//
	private int max; /* max value (calculated from length) */

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("offset", "length", "msb_right");
	}

	public int getMax() {
		return max;
	}

	@Override
	public void read() {
		super.read();
		max = pow(2, length) - 1;
	}

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
