package io.github.paulem.autoconnect;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoConnect implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("auto-connect");

	@Override
	public void onInitialize() {
		LOGGER.info("AutoConnect enabled!");
	}
}