package dev.mattware.farreacher.config;

import dev.mattware.farreacher.FarReacher;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;
import net.minecraft.network.FriendlyByteBuf;

@Config(name = FarReacher.MOD_ID)
public class FarReacherConfig extends PartitioningSerializer.GlobalData {
    @ConfigEntry.Category("server")
    @ConfigEntry.Gui.TransitiveObject
    public FarReacherServerConfig serverConfig = new FarReacherServerConfig();

    // Client config is still created on the server, even though it's never used.
    // TODO: Change?
    @ConfigEntry.Category("client")
    @ConfigEntry.Gui.TransitiveObject
    public FarReacherClientConfig clientConfig = new FarReacherClientConfig();

    @Config(name = FarReacher.MOD_ID + "-server")
    public static class FarReacherServerConfig implements ConfigData {
        // REMEMBER TO ADD TO writeToBuf and readFromBuf when adding new config values!
        // TODO: make this automatic somehow.. serialisation, reflection?
        public boolean testValue = false;

        public void writeToBuf(FriendlyByteBuf buf) {
            buf.writeBoolean(testValue);
        }

        public void readFromBuf(FriendlyByteBuf buf) {
            testValue = buf.readBoolean();
        }
    }

    @Config(name = FarReacher.MOD_ID + "-client")
    public static class FarReacherClientConfig implements ConfigData {
    }
}
