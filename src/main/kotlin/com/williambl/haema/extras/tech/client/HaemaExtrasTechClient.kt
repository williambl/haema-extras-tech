package com.williambl.haema.extras.tech.client

import com.williambl.haema.extras.tech.HaemaExtrasTech
import com.williambl.haema.extras.tech.HaemaExtrasTech.id
import com.williambl.haema.extras.tech.UltravioletFlashLampItem
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.ThrownItemRenderer
import net.minecraft.client.renderer.item.ItemProperties
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer
import org.quiltmc.qsl.block.extensions.api.client.BlockRenderLayerMap

object HaemaExtrasTechClient: ClientModInitializer {
    override fun onInitializeClient(mod: ModContainer?) {
        BlockRenderLayerMap.put(RenderType.translucent(), HaemaExtrasTech.ADAPTIVE_GLASS_BLOCK)
        ItemProperties.register(HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP, id("flashing")) { stack, level, _, _ ->
            if (stack.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG).getLong(UltravioletFlashLampItem.FLASHING_TAG) + 2 >= (level?.gameTime ?: 0)) {
                1.0f
            } else {
                0.0f
            }
        }

        ItemProperties.register(HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP, id("charging")) { stack, level, _, _ ->
            if (stack.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG).getBoolean(UltravioletFlashLampItem.CHARGING_TAG)) {
                1.0f
            } else {
                0.0f
            }
        }

        EntityRendererRegistry.register(HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP_ENTITY_TYPE, ::ThrownItemRenderer)
    }
}
