package com.mmalotky.worldswitch.commands;


import com.mmalotky.worldswitch.IO.IOMethods;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

public class StashCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    public StashCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stash")
                .then(Commands.argument("target", StringArgumentType.word())
                        .executes(command -> stash(command.getSource(), StringArgumentType.getString(command, "target")))));
    }

    private int stash(CommandSourceStack source, String target) {
        String worldName = source.getServer().getWorldData().getLevelName();
        File worldFile = source.getServer().getFile(String.format("./%s", worldName));
        if(target.equals("playerdata")) stashPlayerData(worldFile);
        return Command.SINGLE_SUCCESS;
    }

    protected static void stashPlayerData(File worldFile) {
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
