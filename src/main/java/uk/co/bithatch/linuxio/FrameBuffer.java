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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * This is the main class for manipulating and querying the frame buffer.
 * <p>
 * There is one instance of this class per framebuffer device that is available
 * on the system. To get the instance use one of the static methods, such as
 * {@link #getFrameBuffer(String)}.
 * <p>
 * The framebuffer is currently backed by an {@link BufferedImage}, and can
 * either get the {@link Graphics} object and draw to that (using
 * {@link #commit()} when you want to send your drawing to the device, or you
 * can write an image directly using {@link #write(BufferedImage)}.
 * <p>
 * It is also possible to query both the fixed and variable screen information
 * using {@link #getVariableScreenInfo()} and {@link #getFixedScreenInfo()}.
 * <code>
 * FrameBuffer fbdev = FrameBuffer.getFramebuffer("/dev/fb0");
 * Graphics g = fbdev.getGraphics();
 * g.setColor(Color.red);
 * g.drawRect(10, 10, 50, 50);
 * fbdev.commit();
 * </code> 
 */
public class FrameBuffer implements Closeable {

	final static Logger LOG = System.getLogger(FrameBuffer.class.getName());

	static int FBIOGET_VSCREENINFO = 0x4600;
	static int FBIOPUT_VSCREENINFO = 0x4601;
	static int FBIOGET_FSCREENINFO = 0x4602;
	static int FBIOGETCMAP = 0x4604;
	static int FBIOPUTCMAP = 0x4605;

	private static PrintStream currentOut;
	private static PrintStream currentErr;
	private static InputStream currentIn;

	// Will hold the actual image data depending on format
	class Buffer {
		private int[] intBuffer;
		private short[] shortBuffer;
		private byte[] byteBuffer;
		private BufferedImage image;
	}

	private final static CLib C_LIBRARY = CLib.INSTANCE;

	public final static File DEVICES_DIR = new File(System.getProperty("linuxio.frameBufferDeviceDirectory", "/dev"));

	private Buffer buffer;
	private Object lock = new Object();
	private Rectangle bounds;

	/**
	 * Get and open the first available frame buffer or throw an exceptioon.
	 * 
	 * @return opened framebuffer
	 * @throws IOException on any error opening the framebuffer
	 */
	public static FrameBuffer getFrameBuffer() throws IOException {
		List<File> l = getFrameBufferFiles();
		if(l.isEmpty())
			throw new IOException("No frame buffers found.");
		return new FrameBuffer(l.get(0));
	}

	/**
	 * Get a single framebuffer given its device filename
	 * 
	 * @param deviceFileName device file name (e.g. /dev/fb0)
	 * @return opened framebuffer
	 * @throws IOException on any error opening the framebuffer
	 */
	public static FrameBuffer getFrameBuffer(String deviceFileName) throws IOException {
		File deviceFile = new File(deviceFileName);
		return new FrameBuffer(deviceFile);
	}

	/**
	 * Get a list of all the device files that appear to be framebuffer devices.
	 * 
	 * @return list of framebuffer device files
	 * @throws IOException on any error enumerating device fikles
	 */
	public static List<File> getFrameBufferFiles() throws IOException {
		return Arrays.asList(DEVICES_DIR.listFiles(createFilter()));
	}

	/**
	 * Get and open all of the framebuffers available.
	 * 
	 * @return list of opened framebuffers
	 * @throws IOException on any error enumerating or opening the
	 */
	public static List<FrameBuffer> getFrameBuffers() throws IOException {
		List<FrameBuffer> l = new ArrayList<FrameBuffer>();
		if (!DEVICES_DIR.exists()) {
			throw new RuntimeException("Directory " + DEVICES_DIR + " does not exist.");
		}
		for (File f : DEVICES_DIR.listFiles(createFilter())) {
			try {
				l.add(new FrameBuffer(f));
			} catch (IOException ioe) {
				LOG.log(Level.INFO, "Could not open " + f + ", skipping", ioe);
			}
		}
		return Collections.unmodifiableList(l);
	}

	protected static FileFilter createFilter() {
		return new FileFilter() {

			public boolean accept(File pathname) {
				boolean fb = pathname.getName().startsWith("fb");
				if (fb && !pathname.canRead()) {
					LOG.log(Level.INFO, "Skipping " + pathname + " because it is not readable.");
					return false;
				}
				return fb;
			}
		};
	}

	private File deviceFile;
	private FbFixedScreenInfo fixedScreenInfo;
	private Pointer frameBuffer;
	private int fh;
	private FbVariableScreenInfo varScreenInfo;
	private ByteBuffer nativeBuffer;

	private FrameBuffer(File deviceFile) throws IOException {
		this.deviceFile = deviceFile;

		// Get the fixed screen info
		fixedScreenInfo = new FbFixedScreenInfo();
		fh = C_LIBRARY.open(deviceFile.getAbsolutePath(), CLib.O_RDWR);
		try {
			if (C_LIBRARY.ioctl(fh, FBIOGET_FSCREENINFO, fixedScreenInfo) < 0) {
				throw new IOException("ioctl(" + fh + ", FBIOGET_FSCREENINFO, &size) failed [" + deviceFile + "]");
			}
		} catch (IOException ioe) {
			C_LIBRARY.close(fh);
			throw ioe;
		}

		// Mmap the device file, this is where we write the image data
		frameBuffer = CLib.INSTANCE.mmap(null, new NativeLong(fixedScreenInfo.smem_len),
				CLib.PROT_READ | CLib.PROT_WRITE, CLib.MAP_SHARED, fh, new NativeLong(0));
	}

	/**
	 * Set whether the standard input, output and error streams are output. This
	 * convenience method is provided because output on the console can will
	 * overwrite anything you write to the framebuffer (probably not what you want).
	 * 
	 * @param visible whether standard input, output and errors streams are visible
	 *                on the console where the application was launched
	 */
	public static void setStandardStreamsVisible(boolean visible) {
		if (!visible && currentOut == null) {
			currentOut = System.out;
			currentErr = System.err;
			currentIn = System.in;
			System.setOut(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				}
			}));
			System.setErr(new PrintStream(new OutputStream() {
				@Override
				public void write(int b) throws IOException {
				}
			}));
		} else if (visible && currentOut != null) {
			System.setOut(currentOut);
			System.setErr(currentErr);
			System.setIn(currentIn);
		}
	}

	/**
	 * Set whether the console cursor is visible.
	 * 
	 * @param visible
	 */
	public static void setConsoleCursorVisible(boolean visible) {
		File f = new File("/sys/class/graphics/fbcon/cursor_blink");
		if (f.exists() && f.canWrite()) {
			try {
				PrintWriter pw = new PrintWriter(f);
				try {
					pw.println(visible ? "1" : "0");
				} finally {
					pw.close();
				}
			} catch (IOException ioe) {
				LOG.log(Level.WARNING, "Failed to hide system cursor", ioe);
			}
		}
	}
	
	/**
	 * Get the {@link ByteBuffer} that backs this frame buffer. 
	 * 
	 * @return buffer
	 * @throws IOException 
	 */
	public ByteBuffer getBuffer() throws IOException {
		if(nativeBuffer == null) {
			FbVariableScreenInfo screenInfo = getVariableScreenInfo();
			nativeBuffer = frameBuffer.getByteBuffer(0, screenInfo.xres * screenInfo.yres * Math.max(1, screenInfo.bits_per_pixel / 8));
		}
		return nativeBuffer;
	}

	/**
	 * Get the color map to use when using an indexed colour mode.
	 * 
	 * @return color map
	 * @throws IOException on any I/O readng the color map
	 */
	public FbColorMap getColorMap() throws IOException {
		synchronized (lock) {
			FbColorMap map = new FbColorMap(256);
			int ioctl = C_LIBRARY.ioctl(fh, FBIOGETCMAP, map);
			if (ioctl < 0) {
				throw new IOException("ioctl(" + fh + ", FBIOGETCMAP, &size) failed = " + ioctl);
			}
			return map;
		}
	}

	/**
	 * Set the color map to use when using an indexed colour mode.
	 * 
	 * @param map color map
	 * @throws IOException on any I/O writing the new color map
	 */
	public void setColorMap(FbColorMap map) throws IOException {
		// TODO this does not work!
		synchronized (lock) {
			int ioctl = C_LIBRARY.ioctl(fh, FBIOPUTCMAP, map);
			if (ioctl < 0) {
				throw new IOException("ioctl(" + fh + ", FBIOSETCMAP, &size) failed = " + ioctl);
			}
		}
	}

	/**
	 * Get the variable screen info. Details encapsulated by this object may change
	 * during the life of the frame buffer.
	 * 
	 * @return variable screen info
	 * @throws IOException on any I/O reading the screen info
	 */
	public FbVariableScreenInfo getVariableScreenInfo() throws IOException {
		synchronized (lock) {
			// TODO need a way to reset this, or to read fresh details
			if (varScreenInfo == null) {
				varScreenInfo = new FbVariableScreenInfo();
				if (C_LIBRARY.ioctl(fh, FBIOGET_VSCREENINFO, varScreenInfo) < 0) {
					throw new IOException("ioctl(" + fh + ", FBIOGET_VSCREENINFO, &size) failed");
				}
			}
			return varScreenInfo;
		}
	}

	/**
	 * Get a {@link Graphics} object that may be drawn on.
	 * 
	 * @return graphics
	 */
	public Graphics2D getGraphics() {
		synchronized (lock) {
			checkBufferImage();
			return (Graphics2D) buffer.image.getGraphics();
		}
	}

	/**
	 * Write an image to the backing image 
	 * 
	 * @param image image to draw onto the backing image
	 * @throws IOException
	 */
	public void write(BufferedImage image) throws IOException {
		synchronized (lock) {
			Graphics2D graphics = getGraphics();
			FbVariableScreenInfo screenInfo = getVariableScreenInfo();
			graphics.drawImage(image, 0, 0, screenInfo.xres, screenInfo.yres, 0, 0, image.getWidth(), image.getHeight(),
					null);
			commit();
		}
	}

	/**
	 * Commit a specific area of the backing image to the frame buffer. See
	 * {@link FrameBuffer#commit()} for more information.
	 * 
	 * @param area area of backing image to commit to the framebuffer
	 * @throws IOException
	 */
	public void commit(Rectangle area) throws IOException {
		synchronized (lock) {
			checkBufferImage();

			// Keep the rectangle within bounds
			area = (Rectangle) area.clone();
			if (area.x + area.width > bounds.width) {
				area.width = bounds.width - area.x;
			}
			if (area.y + area.height > bounds.height) {
				area.height = bounds.height - area.y;
			}

			// If commiting entire screen, we can optimise a bit
			if (area.width == buffer.image.getWidth() && area.height == buffer.image.getHeight() && area.x == 0
					&& area.y == 0) {
				commit();
				return;
			}

			FbVariableScreenInfo fbs = getVariableScreenInfo();
			int bps = (int) (fbs.bits_per_pixel / 8);
			int offset = ((area.y * buffer.image.getWidth()) + area.x);
			switch (fbs.bits_per_pixel) {
			case 8:
				for (int y = 0; y < area.height; y++) {
					frameBuffer.write(offset * bps, buffer.byteBuffer, offset, area.width);
					offset += buffer.image.getWidth();
				}
				break;
			case 16:
				for (int y = 0; y < area.height; y++) {
					frameBuffer.write(offset * bps, buffer.shortBuffer, offset, area.width);
					offset += buffer.image.getWidth();
				}
				break;
			case 24:
			case 32:
				for (int y = 0; y < area.height; y++) {
					frameBuffer.write(offset * bps, buffer.intBuffer, offset, area.width);
					offset += buffer.image.getWidth();
				}
				break;
			default:
				throw new UnsupportedOperationException("Unknown bpp " + fbs.bits_per_pixel);
			}
		}
	}

	/**
	 * Commit the current backing image to the framebuffer. Typically used after a
	 * call has been made to {@link #getGraphics}, which is then used to draw
	 * several primitives, and finally sending the changed backing image to the
	 * framebuffer with a {@link #commit()}.
	 * 
	 * @throws IOException
	 */
	public void commit() throws IOException {
		synchronized (lock) {
			checkBufferImage();
			FbVariableScreenInfo fbs = getVariableScreenInfo();
			switch (fbs.bits_per_pixel) {
			case 8:
				frameBuffer.write(0, buffer.byteBuffer, 0, buffer.byteBuffer.length);
				break;
			case 16:
				frameBuffer.write(0, buffer.shortBuffer, 0, buffer.shortBuffer.length);
				break;
			case 24:
			case 32:
				frameBuffer.write(0, buffer.intBuffer, 0, buffer.intBuffer.length);
				break;
			default:
				throw new UnsupportedOperationException("Unknown bpp " + fbs.bits_per_pixel);
			}
		}
	}

	/**
	 * Get the file that represents this framebuffer, typically something like
	 * <b>/dev/fb0</b>.
	 * 
	 * @return device file
	 */
	public File getDeviceFile() {
		return deviceFile;
	}

	/**
	 * Create a image compatible with the image backing this framebuffer.
	 * 
	 * @param w width of image
	 * @param h height of image
	 * @return image
	 * @throws IOException
	 */
	public BufferedImage createCompatibleImage(int w, int h) throws IOException {
		synchronized (lock) {
			FbVariableScreenInfo screenInfo = getVariableScreenInfo();
			return createBuffer(w, h, screenInfo).image;
		}
	}
	
	private void checkBufferImage() {
		try {
			if (buffer == null || buffer.image == null) {
				createBufferImage();
			}
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	private void createBufferImage() throws IOException {
		FbVariableScreenInfo screenInfo = getVariableScreenInfo();
		buffer = createBuffer(screenInfo.xres, screenInfo.yres, screenInfo);
		bounds = new Rectangle(0, 0, screenInfo.xres, screenInfo.yres);
	}

	private Buffer createBuffer(int w, int h, FbVariableScreenInfo screenInfo) throws IOException {
		Buffer buffer = new Buffer();

		if (screenInfo.grayscale == 0) {

			int rmask = (0xff & screenInfo.red.getMax()) << screenInfo.red.offset;
			int gmask = (0xff & screenInfo.green.getMax()) << screenInfo.green.offset;
			int bmask = (0xff & screenInfo.blue.getMax()) << screenInfo.blue.offset;

			if (LOG.isLoggable(Level.DEBUG)) {
				LOG.log(Level.DEBUG, "Creating Direct " + screenInfo.bits_per_pixel + " bit colour model");
				LOG.log(Level.DEBUG, "RED  : " + toBinaryString(screenInfo.bits_per_pixel, rmask));
				LOG.log(Level.DEBUG, "GREEN: " + toBinaryString(screenInfo.bits_per_pixel, gmask));
				LOG.log(Level.DEBUG, "BLUE : " + toBinaryString(screenInfo.bits_per_pixel, bmask));
			}

			DataBuffer dataBuffer = null;
			SampleModel sampleModel = null;
			WritableRaster raster = null;

			// TODO
			// 1 bit
			// 2 bit
			// 4 bit

			ColorModel colorModel;
			switch (screenInfo.bits_per_pixel) {
			case 8:
				dataBuffer = new DataBufferByte(buffer.byteBuffer = new byte[w * h], w * h, 0);

				// byte[] webLevels = { 0, 51, 102, (byte) 153, (byte) 204,
				// (byte) 255 };
				// int colorsNumber = webLevels.length * webLevels.length *
				// webLevels.length; /*
				// * 216
				// * colors
				// */
				// byte[] r = new byte[colorsNumber];
				// byte[] g = new byte[colorsNumber];
				// byte[] b = new byte[colorsNumber];
				//
				// r[0] = 0;
				// g[0] = 0;
				// b[0] = 0;
				//
				// for (int i = 0; i < webLevels.length; i++) {
				// for (int j = 0; j < webLevels.length; j++) {
				// for (int k = 0; k < webLevels.length; k++) {
				// int colorNum = i * webLevels.length * webLevels.length +
				// j * webLevels.length + k;
				//
				// r[colorNum] = webLevels[i];
				// g[colorNum] = webLevels[j];
				// b[colorNum] = webLevels[k];
				// }
				// }
				// }

				FbColorMap map;
				// try {
				map = getColorMap();
				// } catch (IOException ioe) {
				// map = new FbColorMap();
				//
				// byte[] webLevels = { 0, 51, 102, (byte) 153, (byte) 204,
				// (byte) 255 };
				// int colorsNumber = webLevels.length * webLevels.length *
				// webLevels.length; /*
				// */
				// map.len = colorsNumber;
				// map.red.rewind();
				// map.green.rewind();
				// map.blue.rewind();
				// map.red.put((byte)0);
				// map.green.put((byte)0);
				// map.blue.put((byte)0);
				//
				// for (int i = 0; i < webLevels.length; i++) {
				// for (int j = 0; j < webLevels.length; j++) {
				// for (int k = 0; k < webLevels.length; k++) {
				// int colorNum = i * webLevels.length * webLevels.length +
				// j * webLevels.length + k;
				// map.red.put((byte)webLevels[i]);
				// map.green.put((byte)webLevels[j]);
				// map.blue.put((byte)webLevels[k]);
				// }
				// }
				// }
				//
				// setColorMap(map);
				//
				// // Get again?
				// map = getColorMap();
				// }
				// short[] redC = map.red.getShortArray(map.start, map.len);
				// short[] greenC = map.green.getShortArray(map.start,
				// map.len);
				// short[] blueC = map.blue.getShortArray(map.start,
				// map.len);
				// short[] redC = map.red.getShortArray(0, map.len);
				// short[] greenC = map.red.getShortArray(0, map.len);
				// short[] blueC = map.red.getShortArray(0, map.len);
				short[] redC = map.red;
				short[] greenC = map.green;
				short[] blueC = map.blue;
				byte[] r = new byte[redC.length];
				byte[] g = new byte[greenC.length];
				byte[] b = new byte[blueC.length];
				for (int i = 0; i < r.length; i++) {
					r[i] = (byte) redC[i];
					b[i] = (byte) greenC[i];
					g[i] = (byte) blueC[i];
					if (LOG.isLoggable(Level.DEBUG))
						LOG.log(Level.DEBUG, "I= " + i + " R=" + r[i] + " G=" + g[i] + " B=" + b[i]);
				}

				colorModel = new IndexColorModel(8, r.length, r, g, b, 0);

				sampleModel = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, w, h, 1, w, new int[] { 0 });

				break;
			case 16:
				dataBuffer = new DataBufferUShort(buffer.shortBuffer = new short[w * h], w * h, 0);
				sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_USHORT, w, h,
						new int[] { rmask, gmask, bmask });
				colorModel = new DirectColorModel(screenInfo.bits_per_pixel, rmask, gmask, bmask);
				break;
			case 24:
			case 32:
				dataBuffer = new DataBufferInt(buffer.intBuffer = new int[w * h], w * h, 0);
				sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, w, h,
						new int[] { rmask, gmask, bmask });
				colorModel = new DirectColorModel(screenInfo.bits_per_pixel, rmask, gmask, bmask);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported bpp " + screenInfo.bits_per_pixel);
			}

			raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
			buffer.image = new BufferedImage(colorModel, raster, false, null);
			
			return buffer;
		} else {
			throw new UnsupportedOperationException();
		}

	}

	private String toBinaryString(int s, int i) {
		return String.format("%" + s + "s", Integer.toBinaryString(i)).replace(' ', '0');
	}

	@Override
	public String toString() {
		FbVariableScreenInfo variableScreenInfo = null;
		try {
			variableScreenInfo = getVariableScreenInfo();
		} catch (IOException e) {
		}
		return "FrameBuffer [deviceFile=" + deviceFile + ", variableScreenInfo=" + variableScreenInfo
				+ ", fixedScreenInfo=" + fixedScreenInfo + "]";
	}

	public FbFixedScreenInfo getFixedScreenInfo() {
		return fixedScreenInfo;
	}

	public BufferedImage getImage() {
		synchronized (lock) {
			checkBufferImage();
			return buffer.image;
		}
	}

	@Override
	public void close() throws IOException {
		C_LIBRARY.close(fh);
	}

	public void copyImageData(BufferedImage subimage, int sx, int sy, int x, int y, int w, int h) {
		int offset = (y * buffer.image.getWidth()) + x;
		int soffset = (sy * subimage.getWidth()) + sx;
		if (x + w > buffer.image.getWidth()) {
			w = buffer.image.getWidth() - x;
		}
		if (y + h > buffer.image.getHeight()) {
			h = buffer.image.getHeight() - y;
		}
		if (sx + w > subimage.getWidth()) {
			w = subimage.getWidth() - sx;
		}
		if (sy + h > subimage.getHeight()) {
			h = subimage.getHeight() - sy;
		}
		switch (subimage.getData().getDataBuffer().getDataType()) {
		case DataBuffer.TYPE_INT:
			int[] intData = ((DataBufferInt) subimage.getData().getDataBuffer()).getData();
			for (int iy = 0; iy < h; iy++) {
				System.arraycopy(intData, soffset, buffer.intBuffer, offset, w);
				offset += buffer.image.getWidth();
				soffset += subimage.getWidth();
			}
			break;
		case DataBuffer.TYPE_BYTE:
			byte[] byteData = ((DataBufferByte) subimage.getData().getDataBuffer()).getData();
			subimage.getData().getDataElements(sx, sy, w, h, byteData);
			for (int iy = 0; iy < h; iy++) {
				System.arraycopy(byteData, soffset, buffer.byteBuffer, offset, w);
				offset += buffer.image.getWidth();
				soffset += subimage.getWidth();
			}
			break;
		case DataBuffer.TYPE_USHORT:
			short[] shortData = ((DataBufferUShort) subimage.getData().getDataBuffer()).getData();
			for (int iy = 0; iy < h; iy++) {
				System.arraycopy(shortData, soffset, buffer.shortBuffer, offset, w);
				offset += buffer.image.getWidth();
				soffset += subimage.getWidth();
			}
			break;
		default:
			throw new UnsupportedOperationException("Unknown data buffer type.");
		}
	}
}
