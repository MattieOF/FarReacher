package dev.mattware.farreacher.config;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.architectury.utils.GameInstance;
import dev.mattware.farreacher.FarReacher;
import io.netty.buffer.Unpooled;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public class ConfigManager {
    private static FarReacherConfig CONFIG = new FarReacherConfig();
    public static FarReacherConfig.FarReacherServerConfig SERVER_CONFIG;
    // Don't create CLIENT_CONFIG on the server, so we get an error if we try to use it.
    @Environment(EnvType.CLIENT)
    public static FarReacherConfig.FarReacherClientConfig CLIENT_CONFIG;

    public static final short CONFIG_VERSION = 1;

    public static final ResourceLocation SYNC_SERVER_CONFIG_PACKET = new ResourceLocation("farreacher", "sync_server_config");

    public static void initConfig() {
        // Register config class and load it up
        AutoConfig.register(FarReacherConfig.class, Toml4jConfigSerializer::new);
        var configHolder = AutoConfig.getConfigHolder(FarReacherConfig.class);

        // Setup config variables
        CONFIG = configHolder.getConfig();
        if (Platform.getEnvironment() == Env.CLIENT)
            CLIENT_CONFIG = CONFIG.clientConfig;
        SERVER_CONFIG = CONFIG.serverConfig;

        initConfigRefreshHooks(configHolder);
    }

    public static void initConfigClient() {
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            if (Minecraft.getInstance().player == player) {
                // We just quit, reset config to client's server config
                SERVER_CONFIG = CONFIG.serverConfig;
            }
        });

        registerConfigPacketReceiver();
    }

    private static void initConfigRefreshHooks(ConfigHolder<?> configHolder) {
        // Register config sync events
        PlayerEvent.PLAYER_JOIN.register(ConfigManager::sendConfigToPlayer);
        configHolder.registerLoadListener((manager, newData) -> {
            resendConfigIfServer();
            return InteractionResult.PASS;
        });
        configHolder.registerSaveListener((manager, newData) -> {
            resendConfigIfServer();
            return InteractionResult.PASS;
        });
    }

    private static void resendConfigIfServer() {
        FarReacher.LOGGER.info("Resending FarReacher config to all players");
        MinecraftServer server = GameInstance.getServer();
        if (server != null) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            writeServerConfigPacket(buf);
            NetworkManager.sendToPlayers(server.getPlayerList().getPlayers(), SYNC_SERVER_CONFIG_PACKET, buf);
        }
    }

    private static void sendConfigToPlayer(ServerPlayer player) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        writeServerConfigPacket(buf);
        NetworkManager.sendToPlayer(player, SYNC_SERVER_CONFIG_PACKET, buf);
    }

    private static void writeServerConfigPacket(FriendlyByteBuf buf) {
        buf.writeShort(CONFIG_VERSION);
        CONFIG.serverConfig.writeToBuf(buf);
    }

    private static void registerConfigPacketReceiver() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_SERVER_CONFIG_PACKET, (buf, context) -> {
            // We've received the servers config
            FarReacher.LOGGER.info("The server has sent their config over");
            short serverVersion = buf.readShort();
            if (serverVersion != CONFIG_VERSION) {
                FarReacher.LOGGER.error("Server config version " + serverVersion
                        + " doesn't match client config version " + CONFIG_VERSION + "!");
            }
            FarReacherConfig newConfig = new FarReacherConfig();
            newConfig.serverConfig.readFromBuf(buf);
            SERVER_CONFIG = newConfig.serverConfig;
        });
    }
}
