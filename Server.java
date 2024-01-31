import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashSet;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
public class Server implements Runnable {
    
    public static final int PORT = 2020;
    private static final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;
    private ArrayList<InetAddress> client_addresses;
    private ArrayList<Integer> client_ports;
    private HashSet<String> existing_clients;

    public Server() throws SocketException {
      this.socket = new DatagramSocket(PORT); 
      System.out.println("Server running and is listning on port " + PORT);
      client_addresses = new ArrayList();
      client_ports = new ArrayList();
      existing_clients = new HashSet();
    }

    @Override
    public void run() {
        
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            try {
                Arrays.fill(buffer, (byte)0);
                DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
                socket.receive(packet);
                String msg = new String(packet.getData()).trim();
                InetAddress clinetAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String id = clinetAddress.toString() + "|" + clientPort;
                
                if (!existing_clients.contains(id)) {
                    existing_clients.add(id);
                    client_addresses.add(clinetAddress);
                    client_ports.add(clientPort);    
                }
                System.out.println(id + ":" + msg);
                //end of receive


                //start sending
                byte[] data = (id + ":" + msg).getBytes();
                for (int i = 0; i < existing_clients.size(); i++) {
                
                    if(
                        clinetAddress.equals(client_addresses.get(i))
                        && clientPort == client_ports.get(i)
                    )continue;
                     
                    InetAddress clientAddressToSend = client_addresses.get(i);
                    int clientPortToSend = client_ports.get(i);
                    packet = new DatagramPacket(data,data.length,clientAddressToSend,clientPortToSend);   
                    socket.send(packet);
                
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        try {
            Server server = new Server();
            new Thread(server).start();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}