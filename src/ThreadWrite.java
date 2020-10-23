
/***
 * ClientWriteThread
 * Date: 13/10/2020
 * Authors: Erwan Versmée, Camilo Sierra
 */

import java.io.*;
import java.net.*;

public class ThreadWrite {

    public static boolean connected = true;

    public static synchronized void disconnect(MulticastSocket s, InetAddress groupAddr, int groupPort, String user) {
        try {
            String line = "--> " + user + " disconnected"; 
            DatagramPacket mes = new DatagramPacket(line.getBytes(),
                line.length(), groupAddr, groupPort);
            s.send(mes);
            s.leaveGroup(groupAddr);
            connected = false;
            s.close();
        } catch (IOException e) {
            System.err.println("Error in ThreadWrite : Couldn't disconnect because of I/O \n"+e);
        }
    }

    /**
     * main method accepts a connection, receives a message from client then sends
     * an echo to the client
     **/

    public static void main(String[] args) throws IOException {
        // Get Parameters
        if (args.length != 3) {
            System.out.println("Usage: java ThreadWrite <EchoServer host> <EchoServer port> <username>");
            System.exit(1);
        }
        System.out.println("We're not checking your Address is for Multicast, \n"
                + "we trust you. We probably shouldn't. \n" + "Don't disappoint us");
        InetAddress groupAddr = InetAddress.getByName(args[0]);
        int groupPort = Integer.parseInt(args[1]);
        String user = args[2];

        // Create Multicast Socket
        MulticastSocket s = new MulticastSocket(groupPort);

        // Run listenner
        ThreadListen listen = new ThreadListen(groupAddr, groupPort);
        listen.start();

        //Connect
        s.joinGroup(groupAddr);

        // Write
        BufferedReader stdIn = null;
        String line = "";
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        while (connected) {
            //Get Message
            line = stdIn.readLine();
            if(line.equals("quit")) {
                disconnect(s, groupAddr,groupPort, user);
                break;
            }

            line = user + " : " + line; 
            DatagramPacket mes = new DatagramPacket(line.getBytes(),
                line.length(), groupAddr, groupPort);

            //Send Message
            s.send(mes);
        }
        System.out.println("Out of Write");
    }
}
