package dev.mattware.farreacher.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.mattware.farreacher.FarReacher;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FarReacher.MOD_ID)
public class FarReacherForge {
    public FarReacherForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FarReacher.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FarReacher.init();
    }
}