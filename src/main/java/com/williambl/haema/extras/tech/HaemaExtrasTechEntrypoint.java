package com.williambl.haema.extras.tech;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;

/**
 * Until QLK becomes a thing
 */
public class HaemaExtrasTechEntrypoint implements ModInitializer {
	@Override
	public void onInitialize(ModContainer mod) {
		HaemaExtrasTech.INSTANCE.onInitialize(mod);
	}
}
