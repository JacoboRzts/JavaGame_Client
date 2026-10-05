package main;

import client.Client;
import entity.Player;
import tile.TileManager;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.Socket;
import java.io.IOException;

/*
 * Mange the main frame of the game
 */
public class GamePanel extends JPanel implements Runnable {
    final String user, pass, server;
    final int port;
    int lastX, lastY;
    final int originalTileSize = 16;
    final int scale = 3;
    final int tileSize = originalTileSize * scale;
    final int maxCols = 26;
    final int maxRows = 15;
    final int widthScreen = tileSize * maxCols;
    final int heightScreen = tileSize * maxRows;
    final int fps = 40;

    KeyManager kM = new KeyManager();
    TileManager tM = new TileManager(this);
    Player player = new Player(this, kM);
    Thread gameThread;
    Client client;
    Socket socket;


    public GamePanel(String user, String pass, String server, int port) {
        this.user = user;
        this.pass = pass;
        this.server = server;
        this.port = port;
        this.setPreferredSize(new Dimension(this.widthScreen, this.heightScreen));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(kM);
        this.setFocusable(true);
        lastX = player.getX();
        lastY = player.getY();
    }

    public void startGameThread() {
        try {
            this.socket = new Socket(server, port);
            this.client = new Client(socket, user, pass);
            if(client.authenticate()){
                System.out.println("Authenticated, starting the game.");
                gameThread = new Thread(this);
                gameThread.start();
            } else {
                System.out.println("User or password incorrect.");
                client.cierraTodo();
            }
        }
        catch (IOException e){
            System.out.println("The server wasn't found.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / fps;
        double delta = 0;
        long lastTime = System.nanoTime();
        long actualTime;

        while(gameThread != null){
            // Update the screen only 'fps' times.
            actualTime = System.nanoTime();
            delta += (actualTime - lastTime) / drawInterval;
            lastTime = actualTime;
            if (delta >= 1){
                if(lastX != player.getX() || lastY != player.getY()){
                    client.sendMsg("X: "+player.getX() + " Y:"+player.getY());
                    lastX = player.getX();
                    lastY = player.getY();
                }
                update();
                repaint(); // internally call the paintComponent() method to draw into the frame.
                delta--;
            }
         }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        tM.draw(g2);
        player.draw(g2);
        g2.dispose();
    }

    public void update() {
        player.update();
    }

    /** Getters */
    public int getTileSize() { return this.tileSize; }
    public int getMaxCols() { return this.maxCols; }
    public int getMaxRows(){ return this.maxRows; }
    public int getWidthScreen(){ return this.widthScreen; }
    public int getHeightScreen() { return this.heightScreen; }
}
