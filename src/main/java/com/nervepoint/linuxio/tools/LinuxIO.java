package com.nervepoint.linuxio.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.nervepoint.linuxio.CLib;
import com.nervepoint.linuxio.FbColorMap;
import com.nervepoint.linuxio.FbVariableScreenInfo;
import com.nervepoint.linuxio.FrameBuffer;
import com.nervepoint.linuxio.UInputDevice;
import com.nervepoint.linuxio.UInputDevice.Event;

public class LinuxIO {

    public static void main1(BufferedReader reader) throws Exception {
        FrameBuffer buf = FrameBuffer.getFrameBuffers().get(0);
        try {
            System.out.println(buf.getDeviceFile());
            System.out.println(buf.getFixedScreenInfo());
            System.out.println(buf.getVariableScreenInfo());
            try {
                System.out.println(buf.getColorMap());
            }
            catch(IOException ioe) {
                System.err.println("Could not get colour map");
            }
        } finally {
            buf.close();
        }
    }

    public static void main2(BufferedReader reader) throws Exception {
        FrameBuffer fb = FrameBuffer.getFrameBuffers().get(0);
        try {
            FbVariableScreenInfo si = fb.getVariableScreenInfo();
            Graphics2D g = fb.getGraphics();
            long started = System.currentTimeMillis();
            for (int j = 1; j < 10000; j++) {
                float secTaken = ( (float)( System.currentTimeMillis() - started ) / 1000f );
                float fps = j / secTaken;
                g.setColor(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
                g.fillRect(0, 0, si.xres, si.yres);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Monospaced", Font.BOLD, 32));
                g.drawString(String.format("FPS: %3.1f",fps), 30, 50);
                if(!fb.isMapDirect()) {
                    fb.commit();
                }
            }
        } finally {
            fb.close();
        }
    }

    public static void main3(BufferedReader reader) throws Exception {
        for (int i = 0; i < 80; i++) {
            System.out.println();
        }
        FrameBuffer fb = FrameBuffer.getFrameBuffers().get(0);
        try {
            BufferedImage timg = ImageIO.read(FrameBuffer.class.getResource("/testcard.png"));
            fb.write(timg);
            System.out.print("Press RETURN>");
            reader.readLine();
            for (int i = 0; i < 80; i++) {
                System.out.println();
            }
            Graphics2D graphics = fb.getGraphics();
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, fb.getImage().getWidth(), fb.getImage().getHeight());
        } finally {
            fb.close();
        }
    }

    public static void main4(BufferedReader reader) throws Exception {
        UInputDevice dev = UInputDevice.getFirstKeyboardDevice();
        System.out.println(dev);
        boolean grab = false;
        while (true) {
            List<String> s = new ArrayList<>();
            s.add("Read events");
            switch (menu(reader, "Read events", grab ? "Disable grab" : "Enable grab", "Select device", "Return")) {
                case 1:
                    System.err.println("PRESS ESC to exit");
                    if (grab) {
                        dev.grab();
                    }
                    try {
                        while (true) {
                            Event ev = dev.nextEvent();
                            if (ev == null || ev.getCode() == CLib.KEY_ESC) {
                                break;
                            }
                            System.out.println(ev);
                        }
                    } finally {
                        if (grab && dev.isGrabbed()) {
                            dev.ungrab();
                        }
                    }
                    break;
                case 2:
                    grab = !grab;
                    break;
                case 3:
                    List<String> devNames = new ArrayList<>();
                    List<UInputDevice> devs = new ArrayList<>();
                    for (UInputDevice d : UInputDevice.getAllKeyboardDevices()) {
                        devNames.add(d.getName());
                        devs.add(d);
                    }
                    devNames.add("Return");
                    int opt = menu(reader, devNames.toArray(new String[0]));
                    if (opt != devNames.size()) {
                        dev = devs.get(opt - 1);
                    }
                    break;

                default:
                    return;
            }
        }
    }

    public static void main5(BufferedReader reader) throws Exception {
        UInputDevice firstPointerDevice = UInputDevice.getFirstPointerDevice();
        System.out.println(firstPointerDevice);
        boolean grab = false;
        while (true) {
            List<String> s = new ArrayList<>();
            s.add("Read events");
            switch (menu(reader, "Read events", grab ? "Disable grab" : "Enable grab", "Select device", "Return")) {
                case 1:
                    if (grab) {
                        firstPointerDevice.grab();
                    }
                    try {
                        while (true) {
                            Event ev = firstPointerDevice.nextEvent();
                            if (ev == null || reader.ready()) {
                                break;
                            }
                            System.out.println(ev);
                        }
                    } finally {
                        if (grab) {
                            firstPointerDevice.ungrab();
                        }
                    }
                    break;
                case 2:
                    grab = !grab;
                    break;
                case 3:
                    List<String> devNames = new ArrayList<>();
                    List<UInputDevice> devs = new ArrayList<>();
                    for (UInputDevice d : UInputDevice.getAllPointerDevices()) {
                        devNames.add(d.getName());
                        devs.add(d);
                    }
                    devNames.add("Return");
                    int opt = menu(reader, devNames.toArray(new String[0]));
                    if (opt != devNames.size()) {
                        firstPointerDevice = devs.get(opt - 1);
                    }
                    break;

                default:
                    return;
            }
        }
    }

    public static void main6(BufferedReader reader) throws Exception {
        for (UInputDevice d : UInputDevice.getAvailableDevices()) {
            System.out.println(d);
        }
    }

    public static int menu(BufferedReader reader, String... opts) throws IOException {
        int x = 1;
        for (String o : opts) {
            System.out.println((x++) + ". " + o);
        }
        while (true) {
            System.out.print(">");
            String opt = reader.readLine();
            if (opt == null) {
                return -1;
            }
            try {
                return Integer.parseInt(opt);
            } catch (Exception e) {
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            int opt = menu(reader, "FB List GraphicsDevice", "FB random colours (full speed)", "FB Test Card", "UINPUT Keyboard",
                "UINPUT Pointer", "UINPUT All");
            if (opt == 0) {
                return;
            }
            LinuxIO.class.getDeclaredMethod("main" + opt, BufferedReader.class).invoke(null, reader);
        }
    }
}
