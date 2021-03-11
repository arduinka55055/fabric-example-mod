package net.fabricmc.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SocketConnector {
    private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    private int port = 0xDEAD;//here i am
    private String magic = "HORIZON\0";
    private String password = "nothing";
    private byte proto_version = 1;

    private enum packtypes {
        nil, ack, denied, login, logout, msg
    }

    /*HC_packet:
            char magic[8]="HORIZON", //offset=0,size=8
            unsigned char op_type; //offset=9,size=1
            unsigned char ver; //offset=10,size=1
            char deeta[50];    //offset=11,size=50
            char deeta2[50];   //offset=61,size=50 //61 not 51 screw you!
    */
    private byte[] sendpack(byte packtype, String data1, String data2) {
        buf = new byte[110];
        System.arraycopy(magic.getBytes(), 0, buf, 0, 8);
        buf[8] = packtype;
        buf[9] = proto_version;
        System.arraycopy(data1.getBytes(), 0, buf, 10, Math.min(data1.length(), 50));
        System.arraycopy(data2.getBytes(), 0, buf, 60, Math.min(data2.length(), 50));
        return buf;
    }
    public boolean login(String ip,String nick, String passcode) throws IOException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(ip);

        buf = sendpack((byte) 3, nick, passcode);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        //socket.close();
        return true;
    }
    public void send(String message) throws IOException {
        buf = sendpack((byte) 5, message, message);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
    }
    public String flushMessage() throws IOException {
        buf = new byte[110];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String message= new String(packet.getData(), 11, 50);
        message = message.split("\0")[0];

        return message;
    }
}
