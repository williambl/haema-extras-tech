package com.williambl.haema.extras.tech

import com.williambl.haema.component.VampireComponent
import com.williambl.haema.effect.SunlightSicknessEffect
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
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

class UltravioletFlashLamp(properties: Properties) : Item(properties) {

    override fun getUseDuration(stack: ItemStack): Int {
        return CHARGE_DURATION
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (level is ServerLevel) {
            level.sendParticles(ParticleTypes.FLASH, livingEntity.x, livingEntity.getY(0.5), livingEntity.z, 5, 2.0, 2.0, 2.0, 0.3)
        }

        level.getEntities(null, AABB.ofSize(Vec3(livingEntity.x, livingEntity.getY(0.5), livingEntity.z), 16.0, 16.0, 16.0)) { e ->
            e is LivingEntity && (VampireComponent.entityKey.getNullable(e)?.isVampire ?: false || e.mobType == MobType.UNDEAD)
        }.forEach { e ->
            when {
                e !is LivingEntity -> return@forEach
                VampireComponent.entityKey.getNullable(e)?.isVampire ?: false -> e.addEffect(MobEffectInstance(SunlightSicknessEffect.instance, 4*20))
                e.mobType == MobType.UNDEAD && e !is Husk && e !is WitherBoss -> e.setSecondsOnFire(20)
            }
        }

        stack.getOrCreateTagElement(LAMP_DATA_TAG).putLong(FLASHING_TAG, level.gameTime)
        stack.getOrCreateTagElement(LAMP_DATA_TAG).putBoolean(CHARGING_TAG, false)
        stack.hurtAndBreak(1, livingEntity) { e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND) }
        livingEntity.playSound(SoundEvents.GENERIC_EXPLODE, 0.5f, 3.0f)
        if (livingEntity is Player) {
            livingEntity.cooldowns.addCooldown(this, 10)
        }
        return stack
    }

    override fun releaseUsing(stack: ItemStack, level: Level, livingEntity: LivingEntity, timeCharged: Int) {
        super.releaseUsing(stack, level, livingEntity, timeCharged)
        stack.getOrCreateTagElement(LAMP_DATA_TAG).putBoolean(CHARGING_TAG, false)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        player.playSound(HaemaExtrasTech.CAPACITOR_CHARGING, 1f, 1f)
        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        stack.getOrCreateTagElement(LAMP_DATA_TAG).putBoolean(CHARGING_TAG, true)
    }

    companion object {
        const val CHARGE_DURATION: Int = 30
        const val LAMP_DATA_TAG = "lampData"
        const val FLASHING_TAG = "flashing"
        const val CHARGING_TAG = "charging"
    }
}
