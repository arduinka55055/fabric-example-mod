package net.fabricmc.example;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.world.WorldTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.example.SocketConnector;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

import java.io.IOException;
class HorizonThread extends Thread {
    private SocketConnector sockConn;
    HorizonThread(String name,SocketConnector socketConnector){
        super(name);
        System.out.println("init success!");
        this.sockConn=socketConnector;
    }

    public void run() {

        while (true) {
            System.out.println("LOOP happened");
            try {
                String out=this.sockConn.flushMessage();
                ExampleMod.receive(out);
                this.sleep(200);
            } catch (InterruptedException | IOException e) {
                System.out.println("Error happened");
            }
        }
    }
}

class Colors {
    public static String Black = "\u00A70";
    public static String Dark_Blue = "\u00A71";
    public static String Dark_Aqua = "\u00A73";
    public static String Dark_Red = "\u00A74";
    public static String Dark_Purple = "\u00A75";
    public static String Gold = "\u00A76";
    public static String Gray = "\u00A77";
    public static String Dark_Gray = "\u00A78";
    public static String Blue = "\u00A79";
    public static String Green = "\u00A7a";
    public static String Aqua = "\u00A7b";
    public static String Red = "\u00A7c";
    public static String Light_Purple = "\u00A7d";
    public static String Yellow = "\u00A7e";
    public static String White = "\u00A7f";
    public static String Obfuscated = "\u00A7k";
    public static String Bold = "\u00A7l";
    public static String Italic = "\u00A7o";
    public static String Reset = "\u00A7r";
    public static String r = "\u00A7r";
}

public class ExampleMod implements ModInitializer {
    private String incomingPrefix = Colors.Green+"[HoRiZoN %s]> %s"+Colors.r;
    private String outcomingPrefix = Colors.Green+"[HoRiZoN %s]< %S"+Colors.r;
    private String infoPrefix = Colors.Gold+"[!HoRiZoN!]: %s"+Colors.r;
    private SocketConnector socketConnector = new SocketConnector();


    private String getNick(CommandContext<FabricClientCommandSource> context){
        return context.getSource().getPlayer().getName().asString();
    }
    public static void receive(String message){
        MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.SYSTEM, new LiteralText(message), MinecraftClient.getInstance().player.getUuid());

    }
    private int sendmessage(CommandContext<FabricClientCommandSource> context) {
        String message = StringArgumentType.getString(context, "text");
        String nick = getNick(context);
        context.getSource().sendFeedback(new LiteralText(String.format(outcomingPrefix,nick,message)));
        try {
            socketConnector.send(message);
            //context.getSource().sendFeedback(Text.of(received));
        } catch (IOException e) {
            context.getSource().sendFeedback(Text.of(e.getMessage()));
        }
        return 0;
    }

    private int sendlogin(CommandContext<FabricClientCommandSource> context) {
        String password = StringArgumentType.getString(context, "password");
        String ip = StringArgumentType.getString(context, "ip");
        String nick = getNick(context);
        context.getSource().sendFeedback(new LiteralText(String.format(infoPrefix,"Logging in...")));

        try {
            if(socketConnector.login(ip,nick,password)){
                new HorizonThread("HoRiZoNoNtOp!",this.socketConnector).start();
                System.out.println("login happened");
            }
            else {
                context.getSource().sendFeedback(new LiteralText(String.format(infoPrefix,"Login Failed!")));
            }
        } catch (IOException e) {
            context.getSource().sendFeedback(Text.of(e.getMessage()));
        }
        return 0;
    }

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        System.out.println("Hello Fabric world!");
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("hclogin") .then(ClientCommandManager.argument("ip", StringArgumentType.word())
                        .then(ClientCommandManager.argument("password", StringArgumentType.word())
                                .executes(this::sendlogin)
                                )
                        )
                );
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("hc").then(
                ClientCommandManager.argument("text", StringArgumentType.greedyString()).executes(this::sendmessage)));

        // ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        // LiteralText prefix = new LiteralText(".");
        // chatHud.addMessage(prefix.append("a"));

    }

}
