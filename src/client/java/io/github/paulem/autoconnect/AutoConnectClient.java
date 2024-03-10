package io.github.paulem.autoconnect;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ServerInfo;

public class AutoConnectClient implements ClientModInitializer {
	public static final ServerInfo serverInfo = new ServerInfo("Principal", Config.serverAddress, false);

	@Override
	public void onInitializeClient() {
	}
}