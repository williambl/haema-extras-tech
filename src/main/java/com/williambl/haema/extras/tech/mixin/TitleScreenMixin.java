package com.williambl.haema.extras.tech.mixin;

import com.williambl.haema.extras.tech.HaemaExtrasTech;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
	@Inject(method = "init", at = @At("TAIL"))
	public void onInit(CallbackInfo ci) {
		HaemaExtrasTech.LOGGER.info("This line is printed by an example mod mixin!");
	}
}
