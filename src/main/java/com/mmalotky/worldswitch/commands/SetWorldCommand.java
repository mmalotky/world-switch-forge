package com.mmalotky.worldswitch.commands;

import com.mmalotky.worldswitch.IO.IOMethods;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class SetWorldCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set")
                        .then(Commands.argument("world", StringArgumentType.word())
                                .executes(command -> setWorld(command.getSource(), StringArgumentType.getString(command, "world"))))));
    }

    private int setWorld(CommandSourceStack source, String world) {
        String worldName = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", worldName));

        File worldsFile = source.getServer().getFile("./worlds");
        if(!worldsFile.exists()) worldsFile.mkdir();
        File[] worldsFiles = worldsFile.listFiles();
        if(worldsFiles == null) {
            LOGGER.error("Worlds file not found.");
            return 0;
        }
        if(!world.equals("new") && Arrays.stream(worldsFiles).noneMatch(file -> file.getName().equals(world))) {
            LOGGER.error(String.format("World %s not recognised", world));
            return 0;
        }

        //StashCommand.stashPlayerData(worldFile);

        if(world.equals("new")) {
            try {
                Files.delete(Path.of(worldFile.getAbsolutePath() + "/playerdata"));
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }

            if (!IOMethods.deleteDirectory(worldFile)) {
                LOGGER.error(String.format("Error: Unable to delete world file %s.", worldName));
                return 0;
            }
        }
        else {
            LOGGER.error("Feature not Implemented");
        }

        source.getServer().halt(false);
        return Command.SINGLE_SUCCESS;
    }
}
