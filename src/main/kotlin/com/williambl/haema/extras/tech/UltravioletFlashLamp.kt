package com.williambl.haema.extras.tech

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class UltravioletFlashLamp(properties: Properties) : Item(properties) {

    override fun getUseDuration(stack: ItemStack): Int {
        return CHARGE_DURATION
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        val looking = livingEntity.lookAngle
        if (level is ServerLevel) {
            level.sendParticles(ParticleTypes.FLASH, livingEntity.x, livingEntity.eyeY, livingEntity.z, 20, 1.0, 1.0, 1.0, 0.3)
        }
        stack.hurtAndBreak(1, livingEntity) { e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND) }
        return stack
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.CROSSBOW
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        player.startUsingItem(usedHand)
        return InteractionResultHolder.consume(player.getItemInHand(usedHand))
    }

    companion object {
        const val CHARGE_DURATION: Int = 10
    }
}
