package uk.co.bithatch.linuxio;

/*
 * LinuxIO4J - A Java library for working with Linux I/O systems.
 * 
 * Copyright (C) 2015 - Nervepoint Technologies
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

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.ptr.IntByReference;

/**
 * This is the JNA interface to the native libraries required for interaction
 * with UInput and the Framebuffer.
 */
public interface CLib extends com.sun.jna.Library {

	public static final String JNA_LIBRARY_NAME = (com.sun.jna.Platform.isWindows() ? "msvcrt" : "c");
	public static final CLib INSTANCE = Native.load(CLib.JNA_LIBRARY_NAME, CLib.class);

	public static final int PROT_WRITE = (int) 0x2;
	public static final int PROT_READ = (int) 0x1;
	public static final int MAP_SHARED = (int) 0x01;

	public static final int EVIOCGKEYCODE = (int) (((2) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x04) << 0));
	public static final int EVIOCGREP = (int) (((2) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x03) << 0));
	public static final int EVIOCSKEYCODE = (int) (((1) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x04) << 0));
	public static final int EVIOCSREP = (int) (((1) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x03) << 0));
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_ACCMODE = (int) 003;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_APPEND = (int) 2000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_ASYNC = (int) 20000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_CLOEXEC = (int) 2000000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_CREAT = (int) 100;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_DIRECTORY = (int) 200000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_DSYNC = (int) 10000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_EXCL = (int) 200;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_FSYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NDELAY = (int) 4000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NOCTTY = (int) 400;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NOFOLLOW = (int) 400000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_NONBLOCK = (int) 4000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RDONLY = (int) 0;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RDWR = (int) 2;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_RSYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_SYNC = (int) 4010000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_TRUNC = (int) 1000;
	/** <i>native declaration : bits/fcntl-linux.h</i> */
	public static final int O_WRONLY = (int) 1;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRBITS = (int) 2;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRMASK = (int) ((1 << 2) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_DIRSHIFT = (int) (((0 + 8) + 8) + 14);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NONE = (int) 0;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRBITS = (int) 8;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRMASK = (int) ((1 << 8) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_NRSHIFT = (int) 0;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_READ = (int) 2;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZEBITS = (int) 14;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZEMASK = (int) ((1 << 14) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_SIZESHIFT = (int) ((0 + 8) + 8);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPEBITS = (int) 8;
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPEMASK = (int) ((1 << 8) - 1);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_TYPESHIFT = (int) (0 + 8);
	/** <i>native declaration : asm-generic/ioctl.h</i> */
	public static final int _IOC_WRITE = (int) 1;

	public static final int KDMKTONE = 0x4B30;

	public static short POLLIN = 0x0001;
	public static short POLLPRI = 0x0002;

	public final static int IOC_OUT	= 0x40000000;	/* copy out parameters */
	public final static int IOC_IN	= 0x80000000;	/* copy in parameters */
	
	public final static int IOC_NONE  = 0;
	public final static int IOC_WRITE = 1;
	public final static int IOC_READ  = 2;

	public int ioctl(int fd, int cmd);
	
	public int ioctl(int fd, int cmd, Structure val);

	public int ioctl(int fd, int cmd, Pointer val);

	public int ioctl(int fd, int cmd, NativeLong[] val);

	public int ioctl(int fd, int cmd, int val);

	public int ioctl(int fd, int cmd, int[] arg);

	public int ioctl(int fd, int cmd, short[] arg);

	public int ioctl(int fd, int cmd, IntByReference arg);

	public int ioctl(int fd, int cmd, byte[] arg);

	public int poll(pollfd[] fds, int nfds, int timeout);

	public int open(String path, int flags);

	public int close(int fd);

	public NativeLong read(int fd, Pointer pointer, NativeLong nativeLong);

	public NativeLong write(int fd, Structure pointer, NativeLong nativeLong);

	public NativeLong write(int fd, Pointer pointer, NativeLong nativeLong);

	Pointer mmap(Pointer __addr, NativeLong __len, int __prot, int __flags, int __fd, NativeLong __offset);

	int munmap(Pointer __addr, NativeLong __len);

	String ttyname(int filedes);

	// int fb_register_client (struct notifier_block * nb);
	// 179 extern int fb_unregister_client(struct notifier_block *nb);
	// 180 extern int fb_notifier_call_chain(unsigned long val, void *v);
	int fb_register_client();

	int fb_unregister_client();

	int fb_notifier_call_chain(NativeLong val);

	class pollfd extends Structure {
		public int fd;
		public short events;
		public short revents;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("fd", "events", "revents");
		}
	}

	@FieldOrder({ "tv_sec", "tv_usec" })
	class timeval extends Structure {
		/**
		 * Seconds.<br>
		 * C type : __time_t
		 */
		public NativeLong tv_sec;
		/**
		 * Microseconds.<br>
		 * C type : __suseconds_t
		 */
		public NativeLong tv_usec;

		public timeval() {
			super();
		}

		/**
		 * @param tv_sec  Seconds.<br>
		 *                C type : __time_t<br>
		 * @param tv_usec Microseconds.<br>
		 *                C type : __suseconds_t
		 */
		public timeval(NativeLong tv_sec, NativeLong tv_usec) {
			super();
			this.tv_sec = tv_sec;
			this.tv_usec = tv_usec;
		}

		public timeval(Pointer peer) {
			super(peer);
		}

		public static class ByReference extends timeval implements Structure.ByReference {

		};

		public static class ByValue extends timeval implements Structure.ByValue {

		};
	}

}
