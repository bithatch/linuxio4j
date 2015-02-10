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

import com.sun.jna.Structure;

public class FbVariableScreenInfo extends Structure {
	public int xres;
	public int yres;
	public int xres_virtual;
	public int yres_virtual;
	public int xoffset;
	public int yoffset;

	public int bits_per_pixel;
	public int grayscale;

	public FbBitField red = new FbBitField();
	public FbBitField green = new FbBitField();
	public FbBitField blue = new FbBitField();
	public FbBitField transp = new FbBitField();

	public int nonstd;

	public int activate;

	public int height;
	public int width;

	public int accel_flags;

	public int pixclock;
	public int left_margin;
	public int right_margin;
	public int upper_margin;
	public int lower_margin;
	public int hsync_len;
	public int vsync_len;
	public int sync;
	public int vmode;
	public int[] reserved = new int[6];

	@Override
	protected List<?> getFieldOrder() {
		return Arrays.asList("xres", "yres", "xres_virtual", "yres_virtual",
				"xoffset", "yoffset", "bits_per_pixel", "grayscale", "red",
				"green", "blue", "transp", "nonstd", "activate", "height",
				"width", "accel_flags", "pixclock", "left_margin",
				"right_margin", "upper_margin", "lower_margin", "hsync_len",
				"vsync_len", "sync", "vmode", "reserved");
	}

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
