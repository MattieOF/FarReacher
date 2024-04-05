package dev.mattware.farreacher.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.mattware.farreacher.FarReacher;
import dev.mattware.farreacher.config.FarReacherConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FarReacher.MOD_ID)
public class FarReacherForge {
    public FarReacherForge() {
		// Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(FarReacher.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Register config GUI with forge
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (client, parent) -> AutoConfig.getConfigScreen(FarReacherConfig.class, parent).get()
                )));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FarReacher.init();
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(FarReacher::clientSetup);
    }
}
