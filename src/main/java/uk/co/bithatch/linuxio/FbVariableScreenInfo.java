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

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

/**
 * The Class FbVariableScreenInfo.
 */
@FieldOrder({"xres", "yres", "xres_virtual", "yres_virtual",
				"xoffset", "yoffset", "bits_per_pixel", "grayscale", "red",
				"green", "blue", "transp", "nonstd", "activate", "height",
				"width", "accel_flags", "pixclock", "left_margin",
				"right_margin", "upper_margin", "lower_margin", "hsync_len",
				"vsync_len", "sync", "vmode", "reserved"})
public class FbVariableScreenInfo extends Structure {
	
	/** The xres. */
	public int xres;
	
	/** The yres. */
	public int yres;
	
	/** The xres virtual. */
	public int xres_virtual;
	
	/** The yres virtual. */
	public int yres_virtual;
	
	/** The xoffset. */
	public int xoffset;
	
	/** The yoffset. */
	public int yoffset;

	/** The bits per pixel. */
	public int bits_per_pixel;
	
	/** The grayscale. */
	public int grayscale;

	/** The red. */
	public FbBitField red = new FbBitField();
	
	/** The green. */
	public FbBitField green = new FbBitField();
	
	/** The blue. */
	public FbBitField blue = new FbBitField();
	
	/** The transp. */
	public FbBitField transp = new FbBitField();

	/** The nonstd. */
	public int nonstd;

	/** The activate. */
	public int activate;

	/** The height. */
	public int height;
	
	/** The width. */
	public int width;

	/** The accel flags. */
	public int accel_flags;

	/** The pixclock. */
	public int pixclock;
	
	/** The left margin. */
	public int left_margin;
	
	/** The right margin. */
	public int right_margin;
	
	/** The upper margin. */
	public int upper_margin;
	
	/** The lower margin. */
	public int lower_margin;
	
	/** The hsync len. */
	public int hsync_len;
	
	/** The vsync len. */
	public int vsync_len;
	
	/** The sync. */
	public int sync;
	
	/** The vmode. */
	public int vmode;
	
	/** The reserved. */
	public int[] reserved = new int[6];

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "FbScreenInfo [xres=" + xres + ", yres=" + yres
				+ ", xres_virtual=" + xres_virtual + ", yres_virtual="
				+ yres_virtual + ", xoffset=" + xoffset + ", yoffset="
				+ yoffset + ", bits_per_pixel=" + bits_per_pixel
				+ ", grayscale=" + grayscale + ", red=" + red + ", green="
				+ green + ", blue=" + blue + ", transp=" + transp + ", nonstd="
				+ nonstd + ", activate=" + activate + ", height=" + height
				+ ", width=" + width + ", accel_flags=" + accel_flags
				+ ", pixclock=" + pixclock + ", left_margin=" + left_margin
				+ ", right_margin=" + right_margin + ", upper_margin="
				+ upper_margin + ", lower_margin=" + lower_margin
				+ ", hysnc_len=" + hsync_len + ", vysnc_len=" + vsync_len
				+ ", sync=" + sync + ", vmode=" + vmode + ", reserved="
				+ Arrays.toString(reserved) + "]";
	}

}
