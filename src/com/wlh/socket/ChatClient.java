package com.wlh.socket;

import javafx.scene.control.cell.TextFieldListCell;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Created by linghui.wlh on 29/9/17.
 */
public class ChatClient extends Frame {

    Socket s = null;

    DataOutputStream dos = null;

    DataInputStream dis = null;

    boolean bConnected = false;

    TextField tfTxt = new TextField();

    TextArea taContent = new TextArea();

    Thread tRecv = new Thread(new RecvThread());


    public static void main(String [] args){
        new ChatClient().launchFrame(8888);
    }


    public void launchFrame(int port){
        this.setLocation(400, 300);
        this.setSize(300, 300);
        this.add(tfTxt, BorderLayout.SOUTH);
        this.add(taContent, BorderLayout.NORTH);
        this.pack();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        tfTxt.addActionListener(new TFListener());
        this.setVisible(true);
        this.connect(port);
        tRecv.start();
    }

    public void connect(int port){
        try{
            s = new Socket("127.0.0.1", port);

            dos = new DataOutputStream(s.getOutputStream());

            dis = new DataInputStream(s.getInputStream());

            System.out.println("----连接成功----");

            bConnected = true;
        } catch(UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(){
        try{
            dos.close();
            dis.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class TFListener implements ActionListener {

        public void actionPerformed(ActionEvent e){
            String str = tfTxt.getText().trim();

            tfTxt.setText("");

            try{
                dos.writeUTF(str);
                dos.flush();
            } catch(IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private class RecvThread implements Runnable {

        public void run(){
            try{
                while(bConnected) {
                    String str = dis.readUTF();

                    taContent.setText(str + '\n');
                }
            } catch (SocketException e){
                System.out.print("bye");
                e.printStackTrace();
            } catch (EOFException e) {
                System.out.print("bye");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
