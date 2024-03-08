package io.github.paulem.autoconnect.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.paulem.autoconnect.AutoConnect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.paulem.autoconnect.AutoConnectClient.serverInfo;

@Mixin(TitleScreen.class)
public class AutoConnectClientMixin extends Screen {
	protected AutoConnectClientMixin(Text title) {
		super(title);
	}

	@Inject(at = @At("HEAD"), method = "initWidgetsNormal")
	private void initWidgetsNormal(int y, int spacingY, CallbackInfo ci) {
		this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.serverautoconnect"), (button) -> {
			ConnectScreen.connect(this, MinecraftClient.getInstance(), ServerAddress.parse(serverInfo.address), serverInfo, false);
		}).dimensions(this.width / 2 - 100, y + spacingY, 200, 20).build());
	}

	@WrapOperation(
			method = "initWidgetsNormal",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;")
	)
	private Element addDrawableIfNotOnlineButton(TitleScreen instance, Element element, Operation<Element> original) {
		if(element instanceof ButtonWidget button &&
				(button.getMessage().contains(Text.translatable("menu.multiplayer")) ||
					(!AutoConnect.isModMenuPresent && button.getMessage().contains(Text.translatable("menu.online"))))
		)
			return element;
        return original.call(instance, element);
    }
}