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
import java.sql.SQLOutput;

public class SetWorldCommand {
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source)  {
        String world = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", world));
        if (deleteDirectory(worldFile)) {

        }
        else {
            System.out.printf("Error: World file %s not found.%n", world);
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }

    private boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if(files == null) return false;
        else if(files.length == 0) return directory.delete();

        for(File file : files) {
            System.out.printf("Deleting %s%n", file.getName());
            if(file.isDirectory()) deleteDirectory(file);
            else if(!file.delete()) return false;
        }
        return directory.delete();
    }
}
