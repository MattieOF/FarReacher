package dev.mattware.farreacher.fabric;

import dev.mattware.farreacher.FarReacher;
import net.fabricmc.api.ModInitializer;

public class FarReacherFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FarReacher.init();
        FarReacher.clientSetup(); // Env check is handled by the function
    }
}
