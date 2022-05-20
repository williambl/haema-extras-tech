package com.williambl.haema.extras.tech

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DispenserBlock
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.item.group.api.QuiltItemGroup
import org.slf4j.LoggerFactory

object HaemaExtrasTech: ModInitializer {
    val LOGGER = LoggerFactory.getLogger(HaemaExtrasTech::class.java)
    val ITEM_GROUP: CreativeModeTab = QuiltItemGroup.builder(id("tech")).icon { ADAPTIVE_GLASS_ITEM.defaultInstance }.build()

    val ADAPTIVE_GLASS_BLOCK: AdaptiveGlassBlock = Registry.register(Registry.BLOCK, id("adaptive_glass"), AdaptiveGlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion().isValidSpawn { _, _, _, _ -> false}.isRedstoneConductor { _, _, _ -> false}.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false }))

    val ADAPTIVE_GLASS_BE: BlockEntityType<AdaptiveGlassBlockEntity> = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("adaptive_glass"), FabricBlockEntityTypeBuilder.create(::AdaptiveGlassBlockEntity).addBlock(ADAPTIVE_GLASS_BLOCK).build())
    val ADAPTIVE_GLASS_ITEM: BlockItem = Registry.register(Registry.ITEM, id("adaptive_glass"), BlockItem(ADAPTIVE_GLASS_BLOCK, Item.Properties().tab(ITEM_GROUP)))

    val ULTRAVIOLET_FLASH_LAMP: UltravioletFlashLampItem = Registry.register(Registry.ITEM, id("ultraviolet_flash_lamp"), UltravioletFlashLampItem(Item.Properties().tab(ITEM_GROUP).durability(10)))
    val ULTRAVIOLET_FLASH_LAMP_ENTITY_TYPE: EntityType<UltravioletFlashLamp> = Registry.register(Registry.ENTITY_TYPE, id("ultraviolet_flash_lamp"), FabricEntityTypeBuilder.create(MobCategory.MISC, ::UltravioletFlashLamp).dimensions(EntityDimensions.fixed(0.25f, 0.25f)).build())

    val CAPACITOR_CHARGING: SoundEvent = Registry.register(Registry.SOUND_EVENT, id("capacitor_charging"), SoundEvent(id("capacitor_charging")))

    override fun onInitialize(mod: ModContainer) {
        DispenserBlock.registerBehavior(ULTRAVIOLET_FLASH_LAMP, UltravioletFlashLampItem.DISPENSE_BEHAVIOUR)
    }

    fun id(path: String): ResourceLocation = ResourceLocation("haema_extras_tech", path)
}
