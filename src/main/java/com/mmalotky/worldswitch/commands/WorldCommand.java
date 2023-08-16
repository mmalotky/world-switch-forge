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

public class WorldCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    public WorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.argument("action", StringArgumentType.word())
                .then(Commands.argument("world", StringArgumentType.word())
                .executes(command -> {
                    String action = StringArgumentType.getString(command, "action");
                    switch (action) {
                        case "set": return setWorld(command.getSource(), StringArgumentType.getString(command, "world"));
                        case "save": return saveWorld(command.getSource(), StringArgumentType.getString(command, "world"));
                        default: LOGGER.info(String.format("%s is not a an action%n", action));
                    }
                    return 0;
                }))));
    }

    private int saveWorld(CommandSourceStack source, String world) {
        File worldsFile = source.getServer().getFile("./worlds");
        if(getWorldsFiles(worldsFile) == null) return 0;

        Path destination = Path.of(String.format("%s/%s",worldsFile.getAbsolutePath(),world));
        if(world.equals("new") || Files.exists(destination)) {
            LOGGER.error(String.format("%s is not available", world));
            return 0;
        }

        String worldName = source.getServer().getWorldData().getLevelName();
        Path origin = source.getServer().getFile(String.format("./%s", worldName)).toPath();
        IOMethods.copyDirectory(origin, destination);

        return Command.SINGLE_SUCCESS;
    }
    private int setWorld(CommandSourceStack source, String world) {
        String worldName = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", worldName));

        File worldsFile = source.getServer().getFile("./worlds");
        File[] worldsFiles = getWorldsFiles(worldsFile);
        if(worldsFiles == null) return 0;

        if(!world.equals("new") && Arrays.stream(worldsFiles).noneMatch(file -> file.getName().equals(world))) {
            LOGGER.error(String.format("World %s not recognised", world));
            return 0;
        }

        if(world.equals("new")) {
            if (!IOMethods.deleteDirectory(worldFile)) {
                LOGGER.error(String.format("Error: Unable to delete world file %s.", worldName));
                return 0;
            }
        }
        else {
            LOGGER.error("Feature not Implemented");
            return 0;
        }

        source.getServer().halt(false);
        return Command.SINGLE_SUCCESS;
    }

    private File[] getWorldsFiles(File worldsFile) {
        if(!worldsFile.exists()) worldsFile.mkdir();
        File[] worldsFiles = worldsFile.listFiles();
        if(worldsFiles == null) {
            LOGGER.error("Worlds file not found.");
        }
        return worldsFiles;
    }
}
