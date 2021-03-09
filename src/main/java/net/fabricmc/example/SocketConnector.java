package net.fabricmc.example;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/*
time for your server data, @CLEMENTTTTT
int  len=sz;
    pacclen=recvfrom(sockfd,(char*)&buffer,sz,MSG_WAITALL,(struct sockaddr*)&cliaddr,(unsigned int* __restrict)&len);
    if(pacclen<sz)
    {
        std::cout<<"Faulty UDP packet! Expect len "<<sz<<" len="<<pacclen<<" IP="<<inet_ntoa(cliaddr.sin_addr)<<std::endl;
        return;
    }
    std::cout << "Packet passed size check, checking magic" << std::endl;
    if(buffer.magic!="HORIZON")
    {
        std::cout << "Faulty MAGIC " << buffer.magic << "!\n";
        return;
    }
    switch(buffer.op_type)
    {
    case login:
        if(!strcmp(PASS,buffer.deeta2))
        {
            std::cout << "Logic packet password match, adding user "<<buffer.deeta<<std::endl;
            userdatabase.push_back(userentry(buffer.deeta,inet_ntoa(cliaddr.sin_addr)));
            write_ack_packet(&cliaddr);
        }
        else
        {
            std::cout << "Login packet password invalid\n";
            write_den_packet(&cliaddr);
        }
        break;

    case logout:

        for(i=0; i<userdatabase.size(); ++i)
        {
            if(userdatabase[i].ip==inet_ntoa(cliaddr.sin_addr))
            {
                found=true;
                break;
            }

        }
        if(found)
        {
            std::cout << "User " << userdatabase[i].username << "left the chat\n";
            userdatabase.erase(userdatabase.begin()+i);
            write_ack_packet(&cliaddr);
        }
        else{
            std::cout << "client sending logout packet while not logged in\n";
            write_den_packet(&cliaddr);
        }
        break;

    case msg:
        for(i=0; i<userdatabase.size(); ++i)
        {
            if(userdatabase[i].ip==inet_ntoa(cliaddr.sin_addr))
            {
                found=true;
                break;
            }

        }
        if(found)
        {
            std::cout << "User " << userdatabase[i].username << "posted message" << buffer.deeta << std::endl;
        }
        else{
            std::cout << "client sending msg packet while not logged in\n";
        }
        break;

    default:
        std::cout << "Faulty packet op_type " << buffer.op_type << std::endl;
        return;
        break;

    }


 */

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

    public boolean login(String nick, String passcode) throws IOException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("localhost");

        buf = sendpack((byte) 3, nick, passcode);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        //socket.close();
        return true;
    }
    public String send(String nick, String message) throws IOException {
        buf = sendpack((byte) 5, nick, message);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        //socket.close();
        return received;
    }
}
