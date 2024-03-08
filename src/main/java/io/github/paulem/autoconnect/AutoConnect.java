package io.github.paulem.autoconnect;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoConnect implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("auto-connect");
	public static boolean isModMenuPresent = false;

	@Override
	public void onInitialize() {
		LOGGER.info("AutoConnect enabled!");
		isModMenuPresent = FabricLoader.getInstance().isModLoaded("modmenu");
	}
}