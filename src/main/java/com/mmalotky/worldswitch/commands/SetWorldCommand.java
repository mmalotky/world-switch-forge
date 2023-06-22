package com.mmalotky.worldswitch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class SetWorldCommand {
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source)  {
        Entity entity = source.getEntity();
        File serverProperties = source.getServer().getFile("./server.properties");
        String print = "err";

        try (Scanner reader = new Scanner(serverProperties)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if(data.contains("level-name=")) {
                    print = data;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Component comp = new TranslatableComponent("chat.type.announcement", source.getDisplayName(), print);
        source.getServer().getPlayerList().broadcastMessage(comp, ChatType.CHAT, entity != null ? entity.getUUID() : Util.NIL_UUID);

        return Command.SINGLE_SUCCESS;
    }
}
