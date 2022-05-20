package com.williambl.haema.extras.tech

import com.williambl.haema.component.VampireComponent
import com.williambl.haema.effect.SunlightSicknessEffect
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Blaze
import net.minecraft.world.entity.monster.Husk
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3

class UltravioletFlashLamp: ThrowableItemProjectile {
    constructor(livingEntity: LivingEntity, level: Level) : super(
        HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP_ENTITY_TYPE,
        livingEntity,
        level
    )

    constructor(
        x: Double,
        y: Double,
        z: Double,
        level: Level
    ) : super(HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP_ENTITY_TYPE, x, y, z, level)

    constructor(type: EntityType<out UltravioletFlashLamp>, level: Level) : super(type, level)

    override fun getDefaultItem(): Item = HaemaExtrasTech.ULTRAVIOLET_FLASH_LAMP

    override fun handleEntityEvent(id: Byte) {
        if (id == 3.toByte()) {
            for (i in 0..5) {
                this.level.addParticle(
                    ParticleTypes.FLASH,
                    this.x + this.random.nextGaussian() * 2.0,
                    this.y + this.random.nextGaussian() * 2.0,
                    this.z + this.random.nextGaussian() * 2.0,
                    0.0,
                    0.0,
                    0.0
                )
            }
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5f, 3.0f, false)
        }
        super.handleEntityEvent(id)
    }

    override fun tick() {
        val pos = this.position()
        super.tick()
        this.setPos(pos)
        this.move(MoverType.SELF, this.deltaMovement)
        if (!this.level.isClientSide) {
            if (this.tickCount == UltravioletFlashLampItem.CHARGE_DURATION - 2) {
                this.item.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG)
                    .putLong(UltravioletFlashLampItem.FLASHING_TAG, level.gameTime)
                this.item.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG)
                    .putBoolean(UltravioletFlashLampItem.CHARGING_TAG, false)
            } else if (this.tickCount >= UltravioletFlashLampItem.CHARGE_DURATION) {
                this.flash()
                this.discard()
            }
        }
    }

    private fun flash() {
        this.level.broadcastEntityEvent(this, 3)

        this.level.getEntities(null, this.boundingBox.inflate(16.0)) { e ->
            e is LivingEntity && (VampireComponent.entityKey.getNullable(e)?.isVampire ?: false || e.mobType == MobType.UNDEAD)
        }.forEach { e ->
            when {
                e !is LivingEntity -> return@forEach
                VampireComponent.entityKey.getNullable(e)?.isVampire ?: false -> e.addEffect(
                    MobEffectInstance(
                        SunlightSicknessEffect.instance, 4 * 20
                    )
                )
                e.mobType == MobType.UNDEAD && e !is Husk && e !is WitherBoss -> e.setSecondsOnFire(20)
            }
        }
    }
}
