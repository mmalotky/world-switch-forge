package com.mmalotky.worldswitch.events;

import com.mmalotky.worldswitch.IO.IOMethods;
import com.mmalotky.worldswitch.WorldSwitch;
import com.mmalotky.worldswitch.commands.SetWorldCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.NbtComponent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = WorldSwitch.MOD_ID)
public class ModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new SetWorldCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void injectPlayerData(ServerStartingEvent event) {
        String serverDirectory = event.getServer().getServerDirectory().getAbsolutePath();
        String world = event.getServer().getWorldData().getLevelName();

        Path playerData = Path.of(serverDirectory + "/playerData/playerdata");
        Path playerDataCopy = Path.of(serverDirectory + String.format("/%s/playerdata", world));
        String[] players = new File(String.valueOf(playerData)).list();
        if(players == null || players.length == 0) return;

        LOGGER.info("Injecting player data.");
        IOMethods.copyDirectory(playerData, playerDataCopy);

        int x = event.getServer().getWorldData().overworldData().getXSpawn();
        int y = event.getServer().getWorldData().overworldData().getYSpawn();
        int z = event.getServer().getWorldData().overworldData().getZSpawn();
        float angle = event.getServer().getWorldData().overworldData().getSpawnAngle();
        LOGGER.info(String.format("Updating spawn to %s, %s, %s", x, y, z));

        File[] playerFiles = new File(String.valueOf(playerDataCopy)).listFiles();
        if(playerFiles == null) return;
        for(File file : playerFiles) {
            if(file.getName().contains(".dat_old")) continue;
            LOGGER.info("Updating " + file.getName());
            try(FileInputStream stream = new FileInputStream(file)) {
                CompoundTag tag = NbtIo.readCompressed(stream);
                LOGGER.info(tag.get("Pos").getAsString());
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        }

        LOGGER.info("Player Data Set");
    }
}
