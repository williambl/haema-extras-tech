package com.williambl.haema.extras.tech

import com.google.common.base.Predicates
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.quiltmc.qsl.item.group.api.QuiltItemGroup
import org.slf4j.LoggerFactory
import java.util.function.Predicate

object HaemaExtrasTech: ModInitializer {
    val LOGGER = LoggerFactory.getLogger(HaemaExtrasTech::class.java)

    val ITEM_GROUP: CreativeModeTab = QuiltItemGroup.builder(id("tech")).icon { ADAPTIVE_GLASS_ITEM.defaultInstance }.build()

    val ADAPTIVE_GLASS_BLOCK: AdaptiveGlassBlock = Registry.register(Registry.BLOCK, id("adaptive_glass"), AdaptiveGlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).noOcclusion().isValidSpawn { _, _, _, _ -> false}.isRedstoneConductor { _, _, _ -> false}.isSuffocating { _, _, _ -> false }.isViewBlocking { _, _, _ -> false }))
    val ADAPTIVE_GLASS_BE: BlockEntityType<AdaptiveGlassBlockEntity> = Registry.register(Registry.BLOCK_ENTITY_TYPE, id("adaptive_glass"), FabricBlockEntityTypeBuilder.create(::AdaptiveGlassBlockEntity).addBlock(ADAPTIVE_GLASS_BLOCK).build())
    val ADAPTIVE_GLASS_ITEM: BlockItem = Registry.register(Registry.ITEM, id("adaptive_glass"), BlockItem(ADAPTIVE_GLASS_BLOCK, Item.Properties().tab(ITEM_GROUP)))

    override fun onInitialize(mod: ModContainer) {
        LOGGER.info("Hi there!")
    }

    fun id(path: String): ResourceLocation = ResourceLocation("haema_extras_tech", path)
}
