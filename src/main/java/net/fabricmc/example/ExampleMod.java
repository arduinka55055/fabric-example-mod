package net.fabricmc.example;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.LiteralText;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.fabricmc.example.SocketConnector;
import net.minecraft.text.Text;

import java.io.IOException;


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
    private int sendmessage(CommandContext<FabricClientCommandSource> context) {
        String message = StringArgumentType.getString(context, "text");
        String nick = getNick(context);
        context.getSource().sendFeedback(new LiteralText(String.format(incomingPrefix,nick,message)));
        try {
            String received = socketConnector.send(nick,message);
            context.getSource().sendFeedback(Text.of(received));
        } catch (IOException e) {
            context.getSource().sendFeedback(Text.of(e.getMessage()));
        }
        return 0;
    }

    private int sendlogin(CommandContext<FabricClientCommandSource> context) {
        String password = StringArgumentType.getString(context, "password");
        String nick = getNick(context);
        context.getSource().sendFeedback(new LiteralText(String.format(infoPrefix,"Logging in...")));
        try {
            if(socketConnector.login(nick,password)){

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
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("hclogin").then(
                ClientCommandManager.argument("password", StringArgumentType.greedyString()).executes(this::sendlogin)));
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("hc").then(
                ClientCommandManager.argument("text", StringArgumentType.greedyString()).executes(this::sendmessage)));

        // ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
        // LiteralText prefix = new LiteralText(".");
        // chatHud.addMessage(prefix.append("a"));

    }

}
