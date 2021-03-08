package net.fabricmc.example;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.LiteralText;

import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

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
/*
SO I'LL WRITE SENDER
and other socket magic


 */
/*
someone do receiver thread

 */

public class ExampleMod implements ModInitializer {
	private DatagramSocket socket;
	private InetAddress address;
	private byte[] buf;
	private int port = 0xDEAD;//here i am
	private String magic="HORIZON\0";
	private String password="nothing";

    private enum packtypes
    {
        nil,ack,denied,login,logout,msg
    };
	/*HC_packet:
			char magic[8]="HORIZON", //offset=0,size=8
			unsigned char op_type; //offset=9,size=1
			unsigned char ver; //offset=10,size=1
			char deeta[50];    //offset=11,size=50
			char deeta2[50];   //offset=61,size=50 //61 not 51 screw you!

	enum packtypes
{    0   1    2      3     4     5
    nil,ack,denied,login,logout,msg
};
	*/
	private byte[] sendmsgpack(String message){//and watch this bloat
		buf= new byte[110];//it doesn't work
        System.arraycopy(magic.getBytes(),0,buf,0,8);//why is gradle so slow, 1 minute and still stuck at 0% yes, java is slow naturally. ignore it. is is normal behaviour
		//cast the lvalue of the magic comparison code to string, maybe its comparing witht the ppointer
		buf[8]=3;//try compile ok also how do i sync my local examplemod.java with this idk. ctrl s ? i am editing only this file ok

        buf[9]= (byte)10;//crutch to test code
		for(int x = 10; x < message.length()+10; x = x + 1){
			byte[] bytedMessage = message.getBytes();
			buf[x]=bytedMessage[x-10];
		}
		System.arraycopy(password.getBytes(),0,buf,60,password.length());
		return buf;
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		System.out.println("Hello Fabric world!");
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("hc").then(
				ClientCommandManager.argument("string", StringArgumentType.greedyString()).executes(context -> {
					String message = StringArgumentType.getString(context, "string");
					// Test error formatting
					context.getSource().sendFeedback(new LiteralText("HoRiZoN: >" + message));

							//get the localhost IP address, if server is running on some other IP, you need to use that
							try {


									socket = new DatagramSocket();
									address = InetAddress.getByName("localhost");

									buf = sendmsgpack(message);//message.getBytes();
									java.net.DatagramPacket packet
											= new DatagramPacket(buf, buf.length, address, port);
									socket.send(packet);
									packet = new DatagramPacket(buf, buf.length);
									socket.receive(packet);
									String received = new String(
											packet.getData(), 0, packet.getLength());

									socket.close();
								return 0;
							}catch (IOException e) {
								context.getSource().sendError(Text.of(e.getMessage()));
									//e.printStackTrace();
								return 0;
								}

				})
		));

		// ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		// LiteralText prefix = new LiteralText(".");
		// chatHud.addMessage(prefix.append("a"));

	}

}
