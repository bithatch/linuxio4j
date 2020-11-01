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

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"id", "smem_start", "smem_len", "type",
				"type_aux", "visual", "xpanstep", "ypanstep", "ywrapstep",
				"line_length", "mmio_start", "mmio_len", "accel", "reserved"})
public class FbFixedScreenInfo extends Structure {
	public byte[] id = new byte[16];
	public NativeLong smem_start;
	public int smem_len;
	public int type;
	public int type_aux;
	public int visual;
	public short xpanstep;
	public short ypanstep;
	public short ywrapstep;
	public int line_length;
	public NativeLong mmio_start;
	public int mmio_len;
	public int accel;
	public short[] reserved = new short[3];

	//

	@Override
	public String toString() {
		return "FbFixedScreenInfo [id=" + id + ", smem_start=" + smem_start
				+ ", smem_len=" + smem_len + ", type=" + type + ", type_aux="
				+ type_aux + ", visual=" + visual + ", xpanstep=" + xpanstep
				+ ", ypanstep=" + ypanstep + ", ywrapstep=" + ywrapstep
				+ ", line_length=" + line_length + ", mmio_start=" + mmio_start
				+ ", mmio_len=" + mmio_len + ", accel=" + accel + ", reserved="
				+ Arrays.toString(reserved) + "]";
	}

}
