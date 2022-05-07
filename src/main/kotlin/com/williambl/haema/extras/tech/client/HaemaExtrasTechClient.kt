package com.williambl.haema.extras.tech.client

import com.williambl.haema.extras.tech.HaemaExtrasTech
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap
import org.quiltmc.qsl.block.extensions.mixin.client.RenderLayersMixin

object HaemaExtrasTechClient: ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer?) {
        BlockRenderLayerMap.put(RenderType.translucent(), HaemaExtrasTech.ADAPTIVE_GLASS_BLOCK)
    }
}
