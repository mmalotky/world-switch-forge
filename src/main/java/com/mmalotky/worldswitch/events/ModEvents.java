package com.mmalotky.worldswitch.events;

import com.mmalotky.worldswitch.IO.IOMethods;
import com.mmalotky.worldswitch.WorldSwitch;
import com.mmalotky.worldswitch.commands.SetWorldCommand;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

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

        LOGGER.info("Updating spawn");
        int x = event.getServer().getWorldData().overworldData().getXSpawn();
        int y = event.getServer().getWorldData().overworldData().getYSpawn();
        int z = event.getServer().getWorldData().overworldData().getZSpawn();
        float angle = event.getServer().getWorldData().overworldData().getSpawnAngle();

        ResourceKey<Level> resourceKey = event.getServer().overworld().dimension();
        List<ServerPlayer> playerList = event.getServer().getPlayerList().getPlayers();
        for (ServerPlayer player : playerList) {
            player.setPos(x,y,z);
            player.setRespawnPosition(resourceKey, new BlockPos(x,y,z), angle, true,false);
        }

    }
}
