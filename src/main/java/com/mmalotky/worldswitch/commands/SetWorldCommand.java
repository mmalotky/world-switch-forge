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

import java.io.*;
import java.util.*;

public class SetWorldCommand {
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source)  {
        Entity entity = source.getEntity();
        File serverProperties = source.getServer().getFile("./server.properties");
        List<String> lines = new ArrayList<>();
        String print = "err";

        try (Scanner reader = new Scanner(serverProperties)) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if(data.contains("level-name=")) {
                    data = String.format("level-name=%s", UUID.randomUUID());
                }
                lines.add(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(serverProperties))){
            for(String line : lines) {
                writer.write(line);
                writer.newLine();
                if(line.contains("level-name=")) {
                    print = line;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Component comp = new TranslatableComponent("chat.type.announcement", source.getDisplayName(), print);
        source.getServer().getPlayerList().broadcastMessage(comp, ChatType.CHAT, entity != null ? entity.getUUID() : Util.NIL_UUID);

        return Command.SINGLE_SUCCESS;
    }
}
