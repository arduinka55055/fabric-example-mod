package net.fabricmc.example;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.*;
import net.minecraft.client.MinecraftClient;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.minecraft.server.command.CommandManager.literal;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.*;
import net.fabricmc.fabric.api.command.v1.*;
import net.fabricmc.fabric.api.command.*;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
public class ExampleMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		System.out.println("Hello Fabric world!");
		ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("foo").then(
				ClientCommandManager.argument("number", DoubleArgumentType.doubleArg()).executes(context -> {
					double number = DoubleArgumentType.getDouble(context, "number");

					// Test error formatting
					context.getSource().sendError(new LiteralText("Your number is " + number));

					return 0;
				})
		));

		// ChatHud chatHud = MinecraftClient.getInstance().inGameHud.getChatHud();
		// LiteralText prefix = new LiteralText(".");
		// chatHud.addMessage(prefix.append("a"));

	}

}
