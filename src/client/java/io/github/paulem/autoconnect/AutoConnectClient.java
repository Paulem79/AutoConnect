package io.github.paulem.autoconnect;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
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
	private @Nullable ServerInfo serverInfo;

    @Override
	public void onInitializeClient() {
		LOGGER.info("AutoConnect enabled!");

		try {
			serverInfo = new ServerInfo(
					"Principal",
					Files.readAllLines(FabricLoader.getInstance().getGameDir().getParent().resolve("ip.txt"), StandardCharsets.UTF_8).getFirst(),
					ServerInfo.ServerType.OTHER
			);
		} catch (IOException e) {
			serverInfo = null;
		}

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof TitleScreen) {
				List<ClickableWidget> buttons = Screens.getButtons(screen);

				// Get the multiplayer button properties
				@Nullable ClickableWidget multiplayerBtn = buttons
						.stream()
						.filter(button -> checkKey(button, "menu.multiplayer"))
						.findFirst()
						.orElse(null);

				// Get the multiplayer button properties
				@Nullable ClickableWidget realmsBtn = buttons
						.stream()
						.filter(button -> checkKey(button, "menu.online"))
						.findFirst()
						.orElse(null);

				if(multiplayerBtn == null) return;

				// Remove the multiplayer button and the realms button
				buttons.remove(multiplayerBtn);
				buttons.removeIf(button -> checkKey(button, "menu.online") && !(button instanceof ModMenuButtonWidget));
				buttons.removeIf(button -> button instanceof ModMenuButtonWidget);

				if(buttons.stream().noneMatch(btn -> btn instanceof ModMenuButtonWidget) && realmsBtn != null) {
					// Add mod menu button
					buttons.add(
							new ModMenuButtonWidget(realmsBtn.getX(), realmsBtn.getY(), realmsBtn.getWidth(), realmsBtn.getHeight(), ModMenuApi.createModsButtonText(), screen)
					);
				}

				if(serverInfo != null) buttons.add(
						ButtonWidget
								.builder(Text.translatable("autoconnect.menu.server"), (button) -> ConnectScreen.connect(screen, client, ServerAddress.parse(serverInfo.address), serverInfo, false, null))
								.dimensions(multiplayerBtn.getX(), multiplayerBtn.getY(),
										multiplayerBtn.getWidth(), multiplayerBtn.getHeight())
								.build()
				);

				else {
					ButtonWidget disabledBtn = ButtonWidget
							.builder(Text.translatable("autoconnect.menu.noserver"), (button) -> {})
							.dimensions(multiplayerBtn.getX(), multiplayerBtn.getY(),
									multiplayerBtn.getWidth(), multiplayerBtn.getHeight())
							.tooltip(Tooltip.of(Text.translatable("autoconnect.tooltip.noserver")))
							.build();

					disabledBtn.active = false;

					buttons.add(disabledBtn);
				}
			} else if(screen instanceof OptionsScreen) {
				List<ClickableWidget> buttons = Screens.getButtons(screen);

				// Get the multiplayer button properties
				@Nullable ClickableWidget telemetryBtn = buttons
						.stream()
						.filter(button -> checkKey(button, "options.telemetry"))
						.findFirst()
						.orElse(null);

				if(telemetryBtn == null) return;

				buttons.add(
						new ModMenuButtonWidget(telemetryBtn.getX(), telemetryBtn.getY(), telemetryBtn.getWidth(), telemetryBtn.getHeight(), ModMenuApi.createModsButtonText(), screen)
				);

				// Remove the telemetry button
				buttons.remove(telemetryBtn);
			}
		});
	}

	@Nullable
	public String getKey(@NotNull ClickableWidget button) {
		if(button.getMessage() == null || button.getMessage().getContent() == null || !(button.getMessage().getContent() instanceof TranslatableTextContent)) return null;
		return ((TranslatableTextContent) button.getMessage().getContent()).getKey();
	}

	public boolean checkKey(@NotNull ClickableWidget button, String translatable) {
		String key = getKey(button);
		return key != null && key.equals(translatable);
	}
}