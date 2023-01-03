package Client;

import java.awt.event.ActionListener;
import java.io.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ViewRunning extends JFrame {
    public ViewRunning(String type, BufferedReader br, BufferedWriter bw) {
        this.type = type;
        this.br = br;
        this.bw = bw;

        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.setLayout(null);
        this.setSize(500, 600);
        this.initComponents();
        this.setVisible(true);
    }
    
    private String type;
    private BufferedReader br;
    private BufferedWriter bw;
    private JPanel panel;
    private JTable jt;
    private DefaultTableModel table;
    private JButton jbList;
    private JButton jbStart;
    private JButton jbStop;
    private JTextField jtID;

    private void initComponents() {
        panel = new JPanel();
        panel.setBounds(0, 0, 500, 300);

        jt = new JTable();
        table = new DefaultTableModel();
        jt.setModel(table);
        jt.setPreferredScrollableViewportSize(new Dimension(450, 250));
        
        if (type.equals("Process")) {
            jbList = new JButton("Get Process");
            jbList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    getProcess();
                }
            });

            table.addColumn("Process name");
        }
        else {
            jbList = new JButton("Get Application");
            jbList.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    getApp();
                }
            });

            table.addColumn("Application name");
        }
        jbList.setBounds(150, 300, 200, 30);

        table.addColumn("ID");

        jtID = new JTextField("Enter ID/Name");
        jtID.setBounds(175, 400, 150, 30);

        jbStart = new JButton("Start");
        jbStart.setBounds(150, 440, 200, 30);
        jbStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                start();
            }
        });

        jbStop = new JButton("Stop");
        jbStop.setBounds(150, 480, 200, 30);
        jbStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stop();
            }
        });

        panel.add(new JScrollPane(jt));
        add(panel);
        add(jbList);
        add(jtID);
        add(jbStart);
        add(jbStop);
    }

    private void start() {
        String app = jtID.getText();
        if (app.equals("")) return;

        try {
            bw.write("Start");
            bw.newLine();
            bw.flush();
            
            bw.write(app);
            bw.newLine();
            bw.flush();

            Thread.sleep(1000);

            if (this.type.equals("Process"))
                getProcess();
            else 
                getApp();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stop() {
        String app = jtID.getText();
        if (app.equals("")) return;

        try {
            bw.write("Stop");
            bw.newLine();
            bw.flush();
            
            bw.write(app);
            bw.newLine();
            bw.flush();

            Thread.sleep(1000);

            if (this.type.equals("Process"))
                getProcess();
            else 
                getApp();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getProcess() {
        table.setRowCount(0);
        try {
            bw.write("List Process");
            bw.newLine();
            bw.flush();
            
            String info;
            while (true) {
                info = br.readLine();
                if (info.equals("End")) break;
                String[] arr = info.split("\\s+");
                table.addRow(arr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getApp() {
        table.setRowCount(0);
        try {
            bw.write("List App");
            bw.newLine();
            bw.flush();
            
            String info;
            while (true) {
                info = br.readLine();
                if (info.equals("End")) break;
                String[] arr = info.split("\\s+");
                table.addRow(arr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } 
    }
}
