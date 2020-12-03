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

import java.util.Locale;

import com.sun.jna.Native;

public class Ioctl {
	
	public static Ioctl INSTANCE = new Ioctl();
	
	int _IOC_NRBITS = 8;
	int _IOC_TYPEBITS = 8;
	int _IOC_SIZEBITS = 14;
	int _IOC_DIRBITS = 2;
	int _IOC_NONE = 0;
	int _IOC_WRITE = 1;
	int _IOC_READ = 2;

	public int ioc(int direction, int request_type, int request_nr, int size) {
		int _IOC_NRSHIFT = 0;
		int _IOC_TYPESHIFT = _IOC_NRSHIFT + _IOC_NRBITS;
		int _IOC_SIZESHIFT = _IOC_TYPESHIFT + _IOC_TYPEBITS;
		int _IOC_DIRSHIFT = _IOC_SIZESHIFT + _IOC_SIZEBITS;
		return ((direction << _IOC_DIRSHIFT) | (request_type << _IOC_TYPESHIFT) | (request_nr << _IOC_NRSHIFT)
				| (size << _IOC_SIZESHIFT));
	}

	static class _IoctlAlpha extends Ioctl {
		{
			_IOC_NRBITS = 8;
			_IOC_TYPEBITS = 8;
			_IOC_SIZEBITS = 13;
			_IOC_DIRBITS = 3;
			_IOC_NONE = 1;
			_IOC_READ = 2;
			_IOC_WRITE = 4;
		}
	}

	static class _IoctlMips extends Ioctl {
		{
			_IOC_SIZEBITS = 13;
			_IOC_DIRBITS = 3;
			_IOC_NONE = 1;
			_IOC_READ = 2;
			_IOC_WRITE = 4;
		}
	}

	static class _IoctlParisc extends Ioctl {
		{
			_IOC_NONE = 0;
			_IOC_WRITE = 2;
			_IOC_READ = 1;
		}
	}

	static class _IoctlPowerPC extends Ioctl {
		{
			_IOC_SIZEBITS = 13;
			_IOC_DIRBITS = 3;
			_IOC_NONE = 1;
			_IOC_READ = 2;
			_IOC_WRITE = 4;
		}
	}

	static class _IoctlSparc extends Ioctl {
		{
			_IOC_NRBITS = 8;
			_IOC_TYPEBITS = 8;
			_IOC_SIZEBITS = 13;
			_IOC_DIRBITS = 3;
			_IOC_NONE = 1;
			_IOC_READ = 2;
			_IOC_WRITE = 4;
		}
	}

	public static Ioctl machineIoctl() {
		String arch = normalizeArch(System.getProperty("os.arch"));
		if ("ppc_32".equals(arch) || "ppc_64".equals(arch) || "ppcle_32".equals(arch) || "ppcle_64".equals(arch)) {
			return new _IoctlPowerPC();
		} else if ("sparc_32".equals(arch) || "sparc_64".equals(arch)) {
			return new _IoctlSparc();
		} else if ("mips_32".equals(arch) || "mips_64".equals(arch)) {
			return new _IoctlMips();
		}
		// TODO others
		return new Ioctl();
	}

	private static String normalize(String value) {
		if (value == null) {
			return "";
		}
		return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
	}

	private static String normalizeArch(String value) {
		value = normalize(value);
		if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
			return "x86_64";
		}
		if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
			return "x86_32";
		}
		if (value.matches("^(ia64w?|itanium64)$")) {
			return "itanium_64";
		}
		if ("ia64n".equals(value)) {
			return "itanium_32";
		}
		if (value.matches("^(sparc|sparc32)$")) {
			return "sparc_32";
		}
		if (value.matches("^(sparcv9|sparc64)$")) {
			return "sparc_64";
		}
		if (value.matches("^(arm|arm32)$")) {
			return "arm_32";
		}
		if ("aarch64".equals(value)) {
			return "aarch_64";
		}
		if (value.matches("^(mips|mips32)$")) {
			return "mips_32";
		}
		if (value.matches("^(mipsel|mips32el)$")) {
			return "mipsel_32";
		}
		if ("mips64".equals(value)) {
			return "mips_64";
		}
		if ("mips64el".equals(value)) {
			return "mipsel_64";
		}
		if (value.matches("^(ppc|ppc32)$")) {
			return "ppc_32";
		}
		if (value.matches("^(ppcle|ppc32le)$")) {
			return "ppcle_32";
		}
		if ("ppc64".equals(value)) {
			return "ppc_64";
		}
		if ("ppc64le".equals(value)) {
			return "ppcle_64";
		}
		if ("s390".equals(value)) {
			return "s390_32";
		}
		if ("s390x".equals(value)) {
			return "s390_64";
		}

		return "unknown";
	}

	public static int determineBitness(String architecture) {
		// try the widely adopted sun specification first.
		String bitness = System.getProperty("sun.arch.data.model", "");

		if (!bitness.isEmpty() && bitness.matches("[0-9]+")) {
			return Integer.parseInt(bitness, 10);
		}

		// bitness from sun.arch.data.model cannot be used. Try the IBM specification.
		bitness = System.getProperty("com.ibm.vm.bitmode", "");

		if (!bitness.isEmpty() && bitness.matches("[0-9]+")) {
			return Integer.parseInt(bitness, 10);
		}

		// as a last resort, try to determine the bitness from the architecture.
		return guessBitnessFromArchitecture(architecture);
	}

	public static int guessBitnessFromArchitecture(final String arch) {
		if (arch.contains("64")) {
			return 64;
		}

		return 32;
	}

	public int _ioc_type_size(Object size) {
		if (size instanceof Integer) {
			return (Integer) size;
		} else if (size instanceof Class) {
			return Native.getNativeSize((Class<?>) size);
		} else {
			return Native.getNativeSize(size.getClass(), size);
		}
	}

	public int _ioc_request_type(Object requestType) {
		if (requestType instanceof Byte) {
			return ((Byte) requestType);
		}
		if (requestType instanceof Character) {
			return ((Character) requestType);
		}
		if (requestType instanceof Integer) {
			return (Integer) requestType;
		}
		if (requestType instanceof String) {
			String rts = (String) requestType;
			if (rts.length() > 1)
				throw new IllegalArgumentException("String too long.");
			else if (rts.length() == 0)
				throw new IllegalArgumentException("Cannot be an empty string.");
			return (int) rts.charAt(0);
		} else {
			throw new IllegalArgumentException(
					String.format("Must be an integer or a string, but was: %s", requestType.getClass()));
		}
	}

	/**
	 * Python implementation of the `_IOC(...)` macro from Linux.
	 * 
	 * This is a portable implementation of the `_IOC(...)` macro from Linux. It
	 * takes a set of parameters, and calculates a ioctl request number based on
	 * those parameters.
	 * 
	 * @param direction    Direction of data transfer in this ioctl. This can be one
	 *                     of:
	 * 
	 *                     ``None``: No data transfer. ``'r'``: Read-only (input)
	 *                     data. ``'w'``: Write-only (output) data. ``'rw'``:
	 *                     Read-write (input and output) data.
	 * @param request_type The ioctl request type. This can be specified as either a
	 *                     string ``'R'`` or an integer ``123``.
	 * @param request_nr   The ioctl request number. This is an integer.
	 * @param size         The number of data bytes transferred in this ioctl.
	 * @return The calculated ioctl request number.
	 */

	public int IOC(String direction, Object request_type, int request_nr, Object size) {

		Ioctl calc = machineIoctl();

		int dir;
		if (direction == null)
			dir = calc._IOC_NONE;
		else if (direction.equals("r") || direction.equals("R"))
			dir = calc._IOC_READ;
		else if (direction.equals("w") || direction.equals("W"))
			dir = calc._IOC_WRITE;
		else if (direction.equals("rw") || direction.equals("RW"))
			dir = calc._IOC_READ | calc._IOC_WRITE;
		else
			throw new IllegalArgumentException("Direction must be null, 'r', 'w' or 'rw'.");

		int rt = _ioc_request_type(request_type);
		int sz = _ioc_type_size(size);
		return calc.ioc(dir, rt, request_nr, sz);
	}

	/**
	 * 
	 * Python implementation of the `_IO(...)` macro from Linux.
	 * 
	 * This is a portable implementation of the `_IO(...)` macro from Linux. The
	 * `_IO(...)` macro calculates a ioctl request number for ioctl request that do
	 * not transfer any data.
	 * 
	 * @param request_type The ioctl request type. This can be specified as either a
	 *                     string ``'R'`` or an integer ``123``.
	 * @param request_nr   The ioctl request number. This is an integer.
	 * @return The calculated ioctl request number.
	 * @return
	 */
	public int IO(Object request_type, int request_nr) {
		Ioctl calc = machineIoctl();
		int rt = _ioc_request_type(request_type);
		return calc.ioc(calc._IOC_NONE, rt, request_nr, 0);
	}

	/**
	 * Python implementation of the `_IOR(...)` macro from Linux.
	 * 
	 * This is a portable implementation of the `_IOR(...)` macro from Linux. The
	 * `_IOR(...)` macro calculates a ioctl request number for ioctl request that
	 * only pass read-only (input) data.
	 * 
	 * @param request_type The ioctl request type. This can be specified as either a
	 *                     string ``'R'`` or an integer ``123``.
	 * @param request_nr   The ioctl request number. This is an integer.
	 * @param size         The size of the associated data. This can either be an
	 *                     integer or a ctypes type.
	 * @return The calculated ioctl request number.
	 */

	public int IOR(Object request_type, int request_nr, Object size) {
		Ioctl calc = machineIoctl();
		int rt = _ioc_request_type(request_type);
		int sz = _ioc_type_size(size);
		return calc.ioc(calc._IOC_READ, rt, request_nr, sz);
	}

	/**
	 * Python implementation of the `_IOW(...)` macro from Linux.
	 * 
	 * This is a portable implementation of the `_IOW(...)` macro from Linux. The
	 * `_IOW(...)` macro calculates a ioctl request number for ioctl request that
	 * only pass write-only (output) data.
	 * 
	 * @param request_type The ioctl request type. This can be specified as either a
	 *                     string ``'R'`` or an integer ``123``.
	 * @param request_nr   The ioctl request number. This is an integer.
	 * @param size         The size of the associated data. This can either be an
	 *                     integer or a ctypes type.
	 * @return The calculated ioctl request number.
	 */
	public int IOW(Object request_type, int request_nr, Object size) {
		Ioctl calc = machineIoctl();
		int rt = _ioc_request_type(request_type);
		int sz = _ioc_type_size(size);
		return calc.ioc(calc._IOC_WRITE, rt, request_nr, sz);
	}

	/**
	 * Python implementation of the `_IOWR(...)` macro from Linux. This is a
	 * portable implementation of the `_IOWR(...)` macro from Linux. The
	 * `_IOWR(...)` macro calculates a ioctl request number for ioctl request that
	 * use the data for both reading (input) and writing (output).
	 * 
	 * @param request_type The ioctl request type. This can be specified as either a
	 *                     string ``'R'`` or an integer ``123``.
	 * @param request_nr   The ioctl request number. This is an integer.
	 * @param size         The size of the associated data. This can either be an
	 *                     integer or a ctypes type.
	 * @return The calculated ioctl request number.
	 */
	public int IOWR(Object request_type, int request_nr, Object size) {
		Ioctl calc = machineIoctl();
		int rt = _ioc_request_type(request_type);
		int sz = _ioc_type_size(size);
		return calc.ioc(calc._IOC_READ | calc._IOC_WRITE, rt, request_nr, sz);
	}
}