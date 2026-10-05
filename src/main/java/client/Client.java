package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader getMsg;
    private BufferedWriter sendMsg;
    private String user;
    private String password;

    public Client(Socket socket, String user, String password) throws IOException {
        this.socket = socket;
        this.user = user;
        this.password = password;
        this.getMsg = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.sendMsg = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public boolean authenticate() {
        try {
            sendMsg.write(user);
            sendMsg.newLine();
            sendMsg.write(password);
            sendMsg.newLine();
            sendMsg.flush();
            String answer = getMsg.readLine();
            return "AUTH_OK".equals(answer);
        } catch (IOException e) {
            System.out.println("Error durante la autenticación: " + e.getMessage());
            return false;
        }
    }

    public void sendMsg(String msg) {
        try {
            if (socket.isConnected() && !socket.isClosed()) {
                System.out.println(msg);
                sendMsg.write(msg);
                sendMsg.newLine();
                sendMsg.flush();
            }
        } catch (IOException e) {
            cierraTodo();
        }
    }

    public void getMsg() {
        Thread listener = new Thread(() -> {
            try {
                while (socket.isConnected() && !socket.isClosed()) {
                    String msg = getMsg.readLine();
                    if (msg == null) {
                        break;
                    }
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Se perdió la conexión con el servidor.");
            } finally {
                cierraTodo();
            }
        });
        listener.start();
    }

    public void cierraTodo() {
        try {
            if (getMsg != null) {
                getMsg.close();
            }
            if (sendMsg != null) {
                sendMsg.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}