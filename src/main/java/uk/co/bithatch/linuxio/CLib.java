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

	/** The Constant JNA_LIBRARY_NAME. */
	public static final String JNA_LIBRARY_NAME = (com.sun.jna.Platform.isWindows() ? "msvcrt" : "c");
	
	/** The Constant INSTANCE. */
	public static final CLib INSTANCE = Native.load(CLib.JNA_LIBRARY_NAME, CLib.class);

	/** The Constant PROT_WRITE. */
	public static final int PROT_WRITE = (int) 0x2;
	
	/** The Constant PROT_READ. */
	public static final int PROT_READ = (int) 0x1;
	
	/** The Constant MAP_SHARED. */
	public static final int MAP_SHARED = (int) 0x01;

	/** The Constant EVIOCGKEYCODE. */
	public static final int EVIOCGKEYCODE = (int) (((2) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x04) << 0));
	
	/** The Constant EVIOCGREP. */
	public static final int EVIOCGREP = (int) (((2) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x03) << 0));
	
	/** The Constant EVIOCSKEYCODE. */
	public static final int EVIOCSKEYCODE = (int) (((1) << (((0 + 8) + 8) + 14)) | (('E') << (0 + 8)) | ((0x04) << 0));
	
	/** The Constant EVIOCSREP. */
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

	/** The Constant KDMKTONE. */
	public static final int KDMKTONE = 0x4B30;

	/** The pollin. */
	public static short POLLIN = 0x0001;
	
	/** The pollpri. */
	public static short POLLPRI = 0x0002;

	/** The Constant IOC_OUT. */
	public final static int IOC_OUT	= 0x40000000;	/* copy out parameters */
	
	/** The Constant IOC_IN. */
	public final static int IOC_IN	= 0x80000000;	/* copy in parameters */
	
	/** The Constant IOC_NONE. */
	public final static int IOC_NONE  = 0;
	
	/** The Constant IOC_WRITE. */
	public final static int IOC_WRITE = 1;
	
	/** The Constant IOC_READ. */
	public final static int IOC_READ  = 2;

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @return the int
	 */
	public int ioctl(int fd, int cmd);
	
	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param val the val
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, Structure val);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param val the val
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, Pointer val);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param val the val
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, NativeLong[] val);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param val the val
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, int val);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param arg the arg
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, int[] arg);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param arg the arg
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, short[] arg);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param arg the arg
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, IntByReference arg);

	/**
	 * Ioctl.
	 *
	 * @param fd the fd
	 * @param cmd the cmd
	 * @param arg the arg
	 * @return the int
	 */
	public int ioctl(int fd, int cmd, byte[] arg);

	/**
	 * Poll.
	 *
	 * @param fds the fds
	 * @param nfds the nfds
	 * @param timeout the timeout
	 * @return the int
	 */
	public int poll(pollfd[] fds, int nfds, int timeout);

	/**
	 * Open.
	 *
	 * @param path the path
	 * @param flags the flags
	 * @return the int
	 */
	public int open(String path, int flags);

	/**
	 * Close.
	 *
	 * @param fd the fd
	 * @return the int
	 */
	public int close(int fd);

	/**
	 * Read.
	 *
	 * @param fd the fd
	 * @param pointer the pointer
	 * @param nativeLong the native long
	 * @return the native long
	 */
	public NativeLong read(int fd, Pointer pointer, NativeLong nativeLong);

	/**
	 * Write.
	 *
	 * @param fd the fd
	 * @param pointer the pointer
	 * @param nativeLong the native long
	 * @return the native long
	 */
	public NativeLong write(int fd, Structure pointer, NativeLong nativeLong);

	/**
	 * Write.
	 *
	 * @param fd the fd
	 * @param pointer the pointer
	 * @param nativeLong the native long
	 * @return the native long
	 */
	public NativeLong write(int fd, Pointer pointer, NativeLong nativeLong);

	/**
	 * Mmap.
	 *
	 * @param __addr the addr
	 * @param __len the len
	 * @param __prot the prot
	 * @param __flags the flags
	 * @param __fd the fd
	 * @param __offset the offset
	 * @return the pointer
	 */
	Pointer mmap(Pointer __addr, NativeLong __len, int __prot, int __flags, int __fd, NativeLong __offset);

	/**
	 * Munmap.
	 *
	 * @param __addr the addr
	 * @param __len the len
	 * @return the int
	 */
	int munmap(Pointer __addr, NativeLong __len);

	/**
	 * Ttyname.
	 *
	 * @param filedes the filedes
	 * @return the string
	 */
	String ttyname(int filedes);

	// int fb_register_client (struct notifier_block * nb);
	// 179 extern int fb_unregister_client(struct notifier_block *nb);
	/**
	 * Fb register client.
	 *
	 * @return the int
	 */
	// 180 extern int fb_notifier_call_chain(unsigned long val, void *v);
	int fb_register_client();

	/**
	 * Fb unregister client.
	 *
	 * @return the int
	 */
	int fb_unregister_client();

	/**
	 * Fb notifier call chain.
	 *
	 * @param val the val
	 * @return the int
	 */
	int fb_notifier_call_chain(NativeLong val);

	/**
	 */
	class pollfd extends Structure {
		public int fd;
		public short events;
		public short revents;

		@Override
		protected List<String> getFieldOrder() {
			return Arrays.asList("fd", "events", "revents");
		}
	}

	/**
	 *
	 */
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
