package com.williambl.haema.extras.tech

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class AdaptiveGlassBlock(properties: Properties) : AbstractGlassBlock(properties), EntityBlock {
    init {
        registerDefaultState(stateDefinition.any().setValue(DARKENED, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(DARKENED)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return HaemaExtrasTech.ADAPTIVE_GLASS_BE.create(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState?,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!level.isClientSide && level.dimensionType().hasSkyLight()) {
            createTickerHelper(blockEntityType, HaemaExtrasTech.ADAPTIVE_GLASS_BE, AdaptiveGlassBlock::tickEntity)
        } else null
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean {
        return !state.getValue(DARKENED)
    }

    companion object {
        val DARKENED = BooleanProperty.create("darkened")

        private fun tickEntity(level: Level, blockPos: BlockPos, blockState: BlockState, blockEntity: AdaptiveGlassBlockEntity) {
            if (level.gameTime % 20L == 0L) {
                level.setBlockAndUpdate(blockPos, blockState.setValue(DARKENED, level.isDay))
            }
        }

        @Suppress("UNCHECKED_CAST")
        private fun <E : BlockEntity, A : BlockEntity> createTickerHelper(
            blockEntityType: BlockEntityType<A>,
            blockEntityType2: BlockEntityType<E>,
            blockEntityTicker: BlockEntityTicker<in E>?
        ): BlockEntityTicker<A>? {
            return if (blockEntityType2 === blockEntityType) blockEntityTicker as BlockEntityTicker<A> else null
        }
    }
}
