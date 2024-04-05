package dev.mattware.farreacher;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import dev.mattware.farreacher.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarReacher
{
	public static final String MOD_ID = "farreacher";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void init() {
		LOGGER.info("Initialising FarReacher!!");
		ConfigManager.initConfig();
	}

	public static void clientSetup() {
		if (Platform.getEnvironment() != Env.CLIENT) return;

		ConfigManager.initConfigClient();
	}
}
