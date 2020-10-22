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

TODO

### UInput

TODO