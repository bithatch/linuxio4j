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
   try(UInputDevice mouse = UInputDevice.getFirstPointerDevice()) {
        mouse.grab();
        while (true) {
			Event ev = mouse.nextEvent();
			if (ev == null) {
				break;
			}
			System.out.println(ev);
		}
	}
   }
```

To create a new virtual keyboard device and emit some keys :-

```java
try (UInputDevice dev = new UInputDevice("LinuxIO Test", (short) 0x1234, (short) 0x5678)) {

	dev.getCapabilities().put(
		Type.EV_KEY, new LinkedHashSet<>(Arrays.asList(
			InputEventCodes.KEY_H,
			InputEventCodes.KEY_E, 
			InputEventCodes.KEY_L, 
			InputEventCodes.KEY_O, 
			InputEventCodes.KEY_W, 
			InputEventCodes.KEY_R, 
			InputEventCodes.KEY_D, 
			InputEventCodes.KEY_ENTER)));
	dev.open();
	dev.typeKey(InputEventCodes.KEY_H);
	dev.typeKey(InputEventCodes.KEY_E);
	dev.typeKey(InputEventCodes.KEY_L);
	dev.typeKey(InputEventCodes.KEY_L);
	dev.typeKey(InputEventCodes.KEY_O);
	dev.typeKey(InputEventCodes.KEY_W);
	dev.typeKey(InputEventCodes.KEY_O);
	dev.typeKey(InputEventCodes.KEY_R);
	dev.typeKey(InputEventCodes.KEY_L);
	dev.typeKey(InputEventCodes.KEY_D);
	dev.typeKey(InputEventCodes.KEY_ENTER);
}
```

## History

#### 2.1-SNAPSHOT

 * Added support for creating virtual devices.
 * Restored Java8 compatibility
 
#### 2.0

 * Modularised for Java9+ 
