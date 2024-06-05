package io.github.paulem.autoconnect;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoConnectClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("assets/auto-connect");

	public static boolean isModMenuPresent = false;
	public static final ServerInfo serverInfo = new ServerInfo("Principal", Config.serverAddress, ServerInfo.ServerType.OTHER);

	@Override
	public void onInitializeClient() {
		LOGGER.info("AutoConnect enabled!");
		isModMenuPresent = FabricLoader.getInstance().isModLoaded("modmenu");
	}
}