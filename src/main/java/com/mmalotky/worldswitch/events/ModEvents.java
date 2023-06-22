package com.mmalotky.worldswitch.events;

import com.mmalotky.worldswitch.WorldSwitch;
import com.mmalotky.worldswitch.commands.SetWorldCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WorldSwitch.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        new SetWorldCommand(event.getDispatcher());
    }
}
