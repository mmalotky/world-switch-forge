package com.mmalotky.worldswitch.commands;

import com.mmalotky.worldswitch.IO.IOMethods;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;

public class SetWorldCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    public SetWorldCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("world")
                .then(Commands.literal("set").executes(command -> setWorld(command.getSource()))));
    }

    private int setWorld(CommandSourceStack source) {
        String world = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", world));

        savePlayerData(worldFile);

        if (!IOMethods.deleteDirectory(worldFile)) {
            LOGGER.error(String.format("Error: Unable to delete world file %s.", world));
            return 0;
        }
        source.getServer().halt(false);

        return Command.SINGLE_SUCCESS;
    }

    private void savePlayerData(File worldFile) {
        //select files
        File[] files = worldFile.getParentFile().listFiles();
        if(files == null) return;
        File saveFile = Arrays.stream(files)
                .filter(a -> a.getName().equals("playerData"))
                .findFirst()
                .orElse(new File(worldFile.getParentFile().getAbsolutePath() + "/playerData"));
        if(!saveFile.exists() && !saveFile.mkdir()) return;

        //copy data
        LOGGER.info("Caching Player Data.");
        Path playerData = Path.of(worldFile.getAbsolutePath() + "/playerdata");
        Path playerDataCopy = Path.of(saveFile.getAbsolutePath() + "/playerdata");
        IOMethods.copyDirectory(playerData, playerDataCopy);
    }
}
