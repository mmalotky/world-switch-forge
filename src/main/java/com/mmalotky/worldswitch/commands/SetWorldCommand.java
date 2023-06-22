package com.mmalotky.worldswitch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SetWorldCommand {
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source)  {

        try {
            File serverProperties = source.getServer().getFile("server.properties");
            Scanner myReader = new Scanner(serverProperties);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if(data.contains("level-name=")) {
                    if(source.getEntity() instanceof Player) source.getEntity().sendMessage(new TextComponent(data), Util.NIL_UUID);
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return Command.SINGLE_SUCCESS;
    }
}
