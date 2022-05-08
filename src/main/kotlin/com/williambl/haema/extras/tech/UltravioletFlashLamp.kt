package com.williambl.haema.extras.tech

import com.williambl.haema.component.VampireComponent
import com.williambl.haema.effect.SunlightSicknessEffect
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.function.Predicate

import com.williambl.haema.isVampire
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.MobType
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Husk
import net.minecraft.world.entity.monster.Zombie

class UltravioletFlashLamp(properties: Properties) : Item(properties) {

    override fun getUseDuration(stack: ItemStack): Int {
        return CHARGE_DURATION
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        val looking = livingEntity.lookAngle
        if (level is ServerLevel) {
            level.sendParticles(ParticleTypes.FLASH, livingEntity.x, livingEntity.getY(0.5), livingEntity.z, 5, 5.0, 5.0, 5.0, 0.3)
        }

        level.getEntities(null, AABB.ofSize(Vec3(livingEntity.x, livingEntity.getY(0.5), livingEntity.z), 5.0, 5.0, 5.0)) { e ->
            e is LivingEntity && (VampireComponent.entityKey.getNullable(e)?.isVampire ?: false || e.mobType == MobType.UNDEAD)
        }.forEach { e ->
            when {
                e !is LivingEntity -> return@forEach
                VampireComponent.entityKey.getNullable(e)?.isVampire ?: false -> e.addEffect(MobEffectInstance(SunlightSicknessEffect.instance, 4*20))
                e.mobType == MobType.UNDEAD && e !is Husk && e !is WitherBoss -> e.setSecondsOnFire(20)
            }
        }

        stack.hurtAndBreak(1, livingEntity) { e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND) }
        return stack
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    companion object {
        const val CHARGE_DURATION: Int = 10
    }
}
