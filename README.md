# linuxio4j

Java Interface to parts of the Linux I/O system, specifically UInput/Evdev and the Framebuffer.

## Configuring your project

The library is available in Maven Central, so configure your project according to the
build system you use. 

Version 2.0 was released as Java 9 or higher only, but as from version 2.1-SNAPSHOT,
Java 8 compatibility is restored through the use of a multi release jar (MRJAR). So for Java 9
modularity support, use anything from version 2.0. For Java 8, use any version except
version 2.0. Be aware though, that Java 8 compatibility may be completely removed at some
future version.

### Maven

```xml
	<dependency>
		<groupId>uk.co.bithatch</groupId>
		<artifactId>linuxio4j</artifactId>
		<version>2.0</version>
	</dependency>
```

Or for the current development version (will be occasionally updated between releases).

```xml
	<dependency>
		<groupId>uk.co.bithatch</groupId>
		<artifactId>linuxio4j</artifactId>
		<version>2.1-SNAPSHOT</version>
	</dependency>
```

## Try It

You can run the test application from the command line (requires Maven).

```sh
mvn compile exec:java
```

If all is well, you'll see a simple menu.

```
1. FB List GraphicsDevice
2. FB random colours (full speed)
3. FB noise (direct to buffer)
4. FB Test Card
5. UINPUT Keyboard
6. UINPUT Pointer
7. UINPUT All
8. UINPUT Virtual Device
```

## Usage

To integrate with your own project, here are some basics.

### Framebuffer

To find all the frame buffer devices :-

```java
List<FrameBuffer> fbs = FrameBuffer.getFrameBuffers();
```

To get the resolution of a buffer device :-

```java
	try(FrameBuffer fb = FrameBuffer.getFrameBuffer()) {
		int xres = fb.getVariableScreenInfo().xres;
		int yres = fb.getVariableScreenInfo().xres;
		System.out.println("The buffer is " + xres + " x " + yres);
	}
```

To write a whole screen of random noise directly to the display :-

```java
	try(FrameBuffer fb = FrameBuffer.getFrameBuffer()) {
	
		/* Get a whole page of random numbers */
		byte[] rnd = new byte[fb.getVariableScreenInfo().yres * fb.getVariableScreenInfo().xres * Math.max(1, fb.getVariableScreenInfo().bits_per_pixel / 8)];
		new Random().nextBytes(rnd);
		
		/* Write the noise */
		fb.getBuffer().put(rnd);
	}
	
```

To get a `Graphics` to draw on :-

```java
	try(FrameBuffer fb = FrameBuffer.getFrameBuffer()) {
		Graphics2D g = fb.getGraphics();
		g.setColor(Color.RED);
		g.drawRect(100, 100, 400, 400);
		fb.commit();
	}
	
```

### UInput

To grab and read mouse events :-

```java
   try(InputDevice mouse = InputDevice.getFirstPointerDevice()) {
		 mouse.open();
        mouse.grab();
        while (true) {
			Event ev = mouse.nextEvent();
			if (ev == null) {
				break;
			}
			System.out.println(ev);
		}
	}
```

To create a new virtual keyboard device and emit some keys :-

```java
try (InputDevice dev = new InputDevice("LinuxIO Test", (short) 0x1234, (short) 0x5678)) {

	dev.getCapabilities().put(
		Type.EV_KEY, new LinkedHashSet<>(Arrays.asList(
			EventCode.KEY_H,
			EventCode.KEY_E, 
			EventCode.KEY_L, 
			EventCode.KEY_O, 
			EventCode.KEY_W, 
			EventCode.KEY_R, 
			EventCode.KEY_D, 
			EventCode.KEY_ENTER)));
	dev.open();
	dev.typeKeys(
		EventCode.KEY_H, EventCode.KEY_E, EventCode.KEY_L, EventCode.KEY_L, 	EventCode.KEY_O,
		EventCode.KEY_W, EventCode.KEY_O, EventCode.KEY_R, EventCode.KEY_L, EventCode.KEY_D,
		EventCode.KEY_ENTER);
}
```

Non-blocking monitoring of multiple devices (internally a single thread is created).

```java

		for (InputDevice device : InputDevice.getAllPointerDevices()) {
			device.open();
			device.grab();
			InputController.getInstance().add(device, (d, e) -> {
				System.err.println(d + " = " + e);
			});
		}
```

## History

#### 2.1-SNAPSHOT

 * Added support for creating virtual devices.
 * Restored Java8 compatibility
 * `InputEventCode` renamed to `EventCode` and turned into an `enum`. More convenience methods for typing keys.
 * `UInputDevice` renamed to `InputDevice`.
 * `UInputController` renamed to `InputController`.
 * Added supported for properties and absolute value.
 
#### 2.0

 * Modularised for Java9+ 
