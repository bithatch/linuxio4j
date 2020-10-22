# linuxio4j

Java Interface to parts of the Linux I/O system, specifically UInput and the Framebuffer.

## Configuring your project

The library is available in Maven Central, so configure your project according to the
build system you use. 

As from version 2.0, *linuxio4j* requires Java 9 or higher (due to modularity requirements). If you 
want to use this library with Java 8 or earlier, then you will have to continue to use version 
**1.2-SNAPSHOT**. I may consider future 1.0.x releases if anyone needs them.

### Maven

```xml
	<dependency>
		<groupId>uk.co.bithatch</groupId>
		<artifactId>linuxio4j</artifactId>
		<version>2.0</version>
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
3. FB Test Card
4. UINPUT Keyboard
5. UINPUT Pointer
6. UINPUT All
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
	
		/* Get a whole page of random numbers *.
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
```