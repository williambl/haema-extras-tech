package com.williambl.haema.extras.tech.client;

import com.williambl.haema.extras.tech.HaemaExtrasTech;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

/**
 * Until QLK becomes a thing
 */
public class HaemaExtrasTechClientEntrypoint implements ClientModInitializer {
	@Override
	public void onInitializeClient(ModContainer mod) {
		HaemaExtrasTechClient.INSTANCE.onInitializeClient(mod);
	}
}
