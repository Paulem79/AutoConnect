package io.github.paulem.autoconnect;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class AutoConnectClient implements ClientModInitializer {
	private final Logger LOGGER = LoggerFactory.getLogger("AutoConnect");
	private ServerInfo serverInfo;

    @Override
	public void onInitializeClient() {
		LOGGER.info("AutoConnect enabled!");

		try {
			serverInfo = new ServerInfo(
					"Principal",
					Files.readAllLines(FabricLoader.getInstance().getGameDir().getParent().resolve("ip.txt"), StandardCharsets.UTF_8).get(0),
					ServerInfo.ServerType.OTHER
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof TitleScreen) {
				List<ClickableWidget> buttons = Screens.getButtons(screen);

				// Get the multiplayer button properties
				@Nullable ClickableWidget multiplayerBtn = buttons
						.stream()
						.filter(btn -> getKey(btn).equals("menu.multiplayer"))
						.findFirst()
						.orElse(null);

				if(multiplayerBtn == null) return;

				// Remove the multiplayer button and the realms button
				buttons.remove(multiplayerBtn);
				buttons.removeIf(button -> getKey(button).equals("menu.online"));

				buttons.add(
						ButtonWidget
								.builder(Text.translatable("menu.serverautoconnect"), (button) -> ConnectScreen.connect(screen, client, ServerAddress.parse(serverInfo.address), serverInfo, false, null))
								.dimensions(multiplayerBtn.getX(), multiplayerBtn.getY(),
										multiplayerBtn.getWidth(), multiplayerBtn.getHeight())
								.build()
				);
			}
		});
	}

	public String getKey(@NotNull ClickableWidget btn) {
		return ((TranslatableTextContent) btn.getMessage().getContent()).getKey();
	}
}