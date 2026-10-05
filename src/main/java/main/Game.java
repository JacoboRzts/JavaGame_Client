package main;

import javax.swing.JFrame;
import java.io.IOException;
import java.net.Socket;
import client.Client;
import io.github.cdimascio.dotenv.Dotenv;

/*
 * Starter point, main class Game
 */
public class Game {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().directory("./src").load();
        String user = dotenv.get("USER");
        String pass = dotenv.get("PASS");
        String server = dotenv.get("SERVER");
        int port = Integer.parseInt(dotenv.get("PORT"));
        JFrame window = new JFrame();
        GamePanel gP = new GamePanel(user, pass, server, port);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);
        window.setTitle("Cloud Game");
        window.setResizable(false);
        window.setVisible(true);
        window.add(gP);
        window.pack();
        gP.startGameThread();
    }
}
