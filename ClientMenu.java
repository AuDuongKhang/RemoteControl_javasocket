package Client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class ClientMenu extends JFrame implements ActionListener{
 	private JButton jbConnect;
    private JButton jbListProcess;
    private JButton jbListApp;
    private JButton jbScreenShot;
    private JButton jbShutdown;
    private JButton jbListen;
    private JTextField jtHost;
    private Socket client = null;
    private InputStreamReader in = null;
    private OutputStreamWriter out = null;
    private BufferedReader br= null;
    private BufferedWriter bw = null;
	ClientMenu()
	{
        this.pack();
        this.setResizable(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (client != null) {
                    try {
                        bw.write("Disconnect");
                        bw.newLine();
                        bw.flush();
                        client.close();
                        in.close();
                        out.close();
                        br.close();
                        bw.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
		JLabel label = new JLabel();
		ImageIcon image = new ImageIcon("logotruong.png");
		label.setIcon(image);
		Border border = BorderFactory.createLineBorder(Color.blue,2);
		label.setBorder(border);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		
        jbConnect = new JButton("Connect");
        jbConnect.addActionListener(this);
        
        jtHost = new JTextField("Enter host IP");
        jtHost.addFocusListener(new FocusListener()
        {
		    @Override
		    public void focusGained(FocusEvent e)
		    {
		    	jtHost.setText(null);
		    }
			@Override
			public void focusLost(FocusEvent e) {}
        });
        jtHost.setPreferredSize(new Dimension(200,27));
        
        jbListen = new JButton("Listen");
        jbShutdown = new JButton("Shutdown");
        jbScreenShot = new JButton("Screen shot");
        jbListProcess = new JButton("List Process");
        jbListApp = new JButton("List Application");
       
        jbListen.addActionListener(this);
        jbShutdown.addActionListener(this);
        jbScreenShot.addActionListener(this);
        jbListProcess.addActionListener(this);
        jbListApp.addActionListener(this);

		JPanel panel1 = new JPanel();
		panel1.add(jbConnect);
		panel1.add(jtHost);
		this.setTitle("ClientMenu");
		this.setSize(500, 400);
		this.add(panel1);
		this.setVisible(true);
		panel1.setBounds(0,0,500,50);
		JPanel panel2 = new JPanel();
		panel2.setLayout(null);
		panel2.add(jbListen);
		panel2.add(jbShutdown);
		panel2.add(jbScreenShot);
		panel2.add(jbListProcess);
		panel2.add(jbListApp);
		panel2.add(label);
		jbListen.setBounds(50, 60, 150, 75);
		jbListApp.setBounds(275, 60, 150, 75);
		jbScreenShot.setBounds(50, 275, 150, 75);
		jbListProcess.setBounds(275,275,150,75);
		jbShutdown.setBounds(163, 165, 150, 75);
		label.setBounds(38,50,400,310);
		panel2.setBounds(38,50,400,310);
		this.add(panel2);
	}
	public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == jbConnect)
            Connect();
        else if (evt.getSource() == jbListApp)
            ListApp();
        else if (evt.getSource() == jbListProcess)
            ListProcess();
        else if (evt.getSource() == jbScreenShot)
            ScreenShot();
        else if (evt.getSource() == jbShutdown)
            Shutdown();
        else 
            Listen();
    }

    private void Connect() {
        String host_ip = jtHost.getText();

        try {
            client = new Socket(host_ip, 1234);
            in = new InputStreamReader(client.getInputStream());
            out = new OutputStreamWriter(client.getOutputStream());

            br = new BufferedReader(in, 2048);
            bw = new BufferedWriter(out, 2048);

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ListApp() {
        try {
            new ViewRunning("Application", br, bw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ListProcess() {
        try {
            new ViewRunning("Process", br, bw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Listen() {
        try {
            new Listener(br, bw);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void Shutdown() {
        try {
            bw.write("Shutdown");
            bw.newLine();
            bw.flush();

            client.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void ScreenShot() {
        try {
            bw.write("Screen shot");
            bw.newLine();
            bw.flush();

            String recv = br.readLine();

            int data_len = Integer.parseInt(recv);
            int len = 0;
            FileOutputStream fout = new FileOutputStream("Screenshot.png");

            while (len < data_len) {
                recv = br.readLine();
                byte[] data = Base64.getDecoder().decode(recv);
                fout.write(data);
                len += recv.length();
            }

            fout.close();
            
            Path path = Paths.get("Screenshot.png");
            path = path.toAbsolutePath();
            String picPath = path.toString();

            Runtime.getRuntime().exec("powershell \"Start-Process "+picPath + "\"");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}