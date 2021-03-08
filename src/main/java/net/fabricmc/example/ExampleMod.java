package net.fabricmc.example;


import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.LiteralText;

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
