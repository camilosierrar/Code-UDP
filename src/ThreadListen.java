/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */


import java.io.*;
import java.net.*;

public class ThreadListen extends Thread {

    private InetAddress groupIp;
    private int groupPort;
    /**
     * Initializes the listener
     * @param groupIp : The multicast IP used to communicate 
     * @param groupPort : The port used to communicate
     */
    ThreadListen(InetAddress groupIp, int groupPort) {
        this.groupIp = groupIp;
        this.groupPort = groupPort;
    }

    /**
     * Receives messages.
     */
    public void run() {
        try {
            //Create Multicast Socket
            MulticastSocket multiSoc = new MulticastSocket(groupPort);
            //Join Group
            multiSoc.joinGroup(groupIp);

            //Listen
            while (ThreadWrite.connected) {
                //System.out.println("ThreadListen: connected = " + ThreadWrite.connected);
                // Build datagram packet
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                // Receive
                multiSoc.receive(recv);
                //DatagramPacket
                String message = new String(recv.getData());
                System.out.println(message);

            }
            //System.out.println("Out of Write");
            //ThreadWrite.disconnect(multiSoc, groupIp);
            multiSoc.close();
            
        } catch (Exception e) {
            System.err.println("Error in ThreadListen : " + e);
        }
    }

}