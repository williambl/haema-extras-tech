package com.williambl.haema.extras.tech

import com.williambl.haema.component.VampireComponent
import com.williambl.haema.effect.SunlightSicknessEffect
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobType
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Husk
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

class UltravioletFlashLampItem(properties: Properties) : Item(properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)

        level.playSound(
            null,
            player.x,
            player.y,
            player.z,
            SoundEvents.SNOWBALL_THROW,
            SoundSource.NEUTRAL,
            0.5f,
            0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f)
        )

        level.playSound(
            null,
            player.x,
            player.y,
            player.z,
            HaemaExtrasTech.CAPACITOR_CHARGING,
            SoundSource.NEUTRAL,
            1f,
            1f
        )

        if (!level.isClientSide) {
            val entity = UltravioletFlashLamp(player, level)
            entity.item = itemStack.copy().also {
                it.getOrCreateTagElement(LAMP_DATA_TAG).putBoolean(CHARGING_TAG, true)
            }
            entity.shootFromRotation(player, player.xRot, player.yRot, 0.0f, 1.5f, 1.0f)
            level.addFreshEntity(entity)
        }

        player.awardStat(Stats.ITEM_USED[this])
        if (!player.abilities.instabuild) {
            itemStack.shrink(1)
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide())
    }

    companion object {
        const val CHARGE_DURATION: Int = 30
        const val LAMP_DATA_TAG = "lampData"
        const val FLASHING_TAG = "flashing"
        const val CHARGING_TAG = "charging"
    }
}
