package network;

import master.Util;

import java.io.*;
import java.net.*;

public class NetworkManager {

    enum ManagerType{
        Server,
        Client,
        Dormant
    }

    private static ManagerType managerType = ManagerType.Dormant;

    private static ServerSocket server;
    private static Socket serverSocket;
    private static Socket client;

    private static PrintStream out;
    private static BufferedReader in;

    private static int port = 15055;

    public static void Host(){
        if(managerType != ManagerType.Dormant){
            return;
        }
        managerType = ManagerType.Server;
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(100000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Client(){
        if(managerType != ManagerType.Dormant){
            return;
        }
        managerType = ManagerType.Client;
        client = new Socket();
        try {
            client.setSoTimeout(100000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void Connect(String ipAddr){
        if(managerType != ManagerType.Client){
            return;
        }
        try {
            client.connect(new InetSocketAddress(ipAddr, port));
            in = new BufferedReader( new InputStreamReader(client.getInputStream()));
            out = new PrintStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Connect(){
        if(managerType != ManagerType.Server){
            return;
        }
        try {
            serverSocket = server.accept();
            in = new BufferedReader( new InputStreamReader(serverSocket.getInputStream()));
            out = new PrintStream(serverSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Disconnect(){
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.close();
        if(serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(client != null){
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Send(String toSend){
        out.println(toSend);
    }

    public static void Receive(String varToWrite){
        try {
            String toWrite = "";
            String line = in.readLine();
            while( line != null )
            {
                toWrite = toWrite + line;
                Util.CreateVar(varToWrite, toWrite, true);
                line = in.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
