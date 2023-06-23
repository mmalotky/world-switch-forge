package com.mmalotky.worldswitch.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;

import java.io.*;

public class SetWorldCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source) {
        String world = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", world));
        if (!deleteDirectory(worldFile)) {
            LOGGER.error(String.format("Error: Unable to delete world file %s.", world));
            return 0;
        }
        source.getServer().halt(false);

        return Command.SINGLE_SUCCESS;
    }

    private boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if(files == null) return false;
        else if(files.length == 0) return directory.delete();

        for(File file : files) {
            LOGGER.info(String.format("Deleting %s", file.getName()));
            if(file.isDirectory()) deleteDirectory(file);
            else if(!file.delete()) return false;
        }
        return directory.delete();
    }
}
