package com.williambl.haema.extras.tech

import com.williambl.haema.component.VampireComponent
import com.williambl.haema.damagesource.SunlightDamageSource
import com.williambl.haema.effect.SunlightSicknessEffect
import com.williambl.haema.extras.tech.mixin.EntityAccessor
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.MobType
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.Husk
import net.minecraft.world.entity.projectile.ThrowableItemProjectile
import net.minecraft.world.item.Item
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

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
            this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 0.5f, 10.0f, false)
        }
        super.handleEntityEvent(id)
    }

    override fun tick() {
        if (this.level.isClientSide()) {
            for (i in 0..5) {
                this.level.addParticle(
                    ParticleTypes.ELECTRIC_SPARK,
                    this.x + this.random.nextGaussian() * this.bbWidth * 2,
                    this.y + this.random.nextGaussian() * this.bbHeight * 2,
                    this.z + this.random.nextGaussian() * this.bbWidth * 2,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }

        val pos = this.position()
        super.tick()
        this.setPos(pos)

        if (this.verticalCollision) {
            this.deltaMovement = this.deltaMovement.scale(0.4)
        }

        this.move(MoverType.SELF, this.deltaMovement)
        if (!this.level.isClientSide) {
            if (this.tickCount == UltravioletFlashLampItem.CHARGE_DURATION - 2) {
                this.item.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG)
                    .putLong(UltravioletFlashLampItem.FLASHING_TAG, level.gameTime)
                this.item.getOrCreateTagElement(UltravioletFlashLampItem.LAMP_DATA_TAG)
                    .putBoolean(UltravioletFlashLampItem.CHARGING_TAG, false)
                this.item = this.item // updates to client
            } else if (this.tickCount >= UltravioletFlashLampItem.CHARGE_DURATION) {
                this.flash()
                this.discard()
            }
        }
    }

    override fun move(type: MoverType, posIn: Vec3) {
        if (this.noPhysics) {
            this.setPos(this.x + posIn.x, this.y + posIn.y, this.z + posIn.z)
        } else {
            var pos = posIn
            this.wasOnFire = this.isOnFire
            if (type == MoverType.PISTON) {
                pos = this.limitPistonMovement(pos)
                if (pos == Vec3.ZERO) {
                    return
                }
            }
            this.level.profiler.push("move")
            if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
                pos = pos.multiply(this.stuckSpeedMultiplier)
                this.stuckSpeedMultiplier = Vec3.ZERO
                this.deltaMovement = Vec3.ZERO
            }
            pos = this.maybeBackOffFromEdge(pos, type)
            val collidedDeltaPos = (this as EntityAccessor).callCollide(pos)
            val d = collidedDeltaPos.lengthSqr()
            if (d > 1.0E-7) {
                if (this.fallDistance != 0.0f && d >= 1.0) {
                    val blockHitResult = this.level
                        .clip(
                            ClipContext(
                                this.position(),
                                this.position().add(collidedDeltaPos),
                                ClipContext.Block.FALLDAMAGE_RESETTING,
                                ClipContext.Fluid.WATER,
                                this
                            )
                        )
                    if (blockHitResult.type != HitResult.Type.MISS) {
                        this.resetFallDistance()
                    }
                }
                this.setPos(this.x + collidedDeltaPos.x, this.y + collidedDeltaPos.y, this.z + collidedDeltaPos.z)
            }
            this.level.profiler.pop()
            this.level.profiler.push("rest")
            val isCollisionX = !Mth.equal(pos.x, collidedDeltaPos.x)
            val isCollisionZ = !Mth.equal(pos.z, collidedDeltaPos.z)
            this.horizontalCollision = isCollisionX || isCollisionZ
            this.verticalCollision = pos.y != collidedDeltaPos.y
            this.verticalCollisionBelow = this.verticalCollision && pos.y < 0.0
            if (this.horizontalCollision) {
                this.minorHorizontalCollision = this.isHorizontalCollisionMinor(collidedDeltaPos)
            } else {
                this.minorHorizontalCollision = false
            }
            this.onGround = this.verticalCollision && pos.y < 0.0
            val blockPos = this.onPos
            val blockState = this.level.getBlockState(blockPos)
            this.checkFallDamage(collidedDeltaPos.y, this.onGround, blockState, blockPos)
            if (this.isRemoved) {
                this.level.profiler.pop()
            } else {
                if (this.horizontalCollision) {
                    val delta = this.deltaMovement
                    this.setDeltaMovement(if (isCollisionX) -delta.x * 0.6 else delta.x, delta.y, if (isCollisionZ) -delta.z * 0.6 else delta.z)
                }
                val block = blockState.block
                if (pos.y != collidedDeltaPos.y) {
                    block.updateEntityAfterFallOn(this.level, this)
                }
                if (this.onGround && !this.isSteppingCarefully) {
                    block.stepOn(this.level, blockPos, blockState, this)
                }
                val movementEmission = this.movementEmission
                if (movementEmission.emitsAnything() && !this.isPassenger) {
                    val e = collidedDeltaPos.x
                    var f = collidedDeltaPos.y
                    val g = collidedDeltaPos.z
                    this.flyDist += (collidedDeltaPos.length() * 0.6).toFloat()
                    if (!blockState.`is`(BlockTags.CLIMBABLE) && !blockState.`is`(Blocks.POWDER_SNOW)) {
                        f = 0.0
                    }
                    this.walkDist += collidedDeltaPos.horizontalDistance().toFloat() * 0.6f
                    this.moveDist += sqrt(e * e + f * f + g * g).toFloat() * 0.6f
                    if (this.moveDist > (this as EntityAccessor).nextStep && !blockState.isAir) {
                        (this as EntityAccessor).nextStep = this.nextStep()
                        if (this.isInWater) {
                            if (movementEmission.emitsSounds()) {
                                val entity = if (this.isVehicle && this.controllingPassenger != null) this.controllingPassenger!! else this
                                val h = if (entity === this) 0.35f else 0.4f
                                val vec33 = entity.deltaMovement
                                val i = min(
                                    1.0f,
                                    sqrt(vec33.x * vec33.x * 0.2f + vec33.y * vec33.y + vec33.z * vec33.z * 0.2f)
                                        .toFloat() * h
                                )
                                this.playSwimSound(i)
                            }
                            if (movementEmission.emitsEvents()) {
                                this.gameEvent(GameEvent.SWIM)
                            }
                        } else {
                            if (movementEmission.emitsSounds()) {
                                (this as EntityAccessor).callPlayAmethystStepSound(blockState)
                                this.playStepSound(blockPos, blockState)
                            }
                            if (movementEmission.emitsEvents() && !blockState.`is`(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
                                this.gameEvent(GameEvent.STEP)
                            }
                        }
                    } else if (blockState.isAir) {
                        this.processFlappingMovement()
                    }
                }
                this.tryCheckInsideBlocks()
                val j = this.blockSpeedFactor
                this.deltaMovement = this.deltaMovement.multiply(j.toDouble(), 1.0, j.toDouble())
                if (this.level
                        .getBlockStatesIfLoaded(this.boundingBox.deflate(1.0E-6))
                        .noneMatch { blockStatex: BlockState ->
                            blockStatex.`is`(BlockTags.FIRE) || blockStatex.`is`(Blocks.LAVA)
                        }
                ) {
                    if (this.remainingFireTicks <= 0) {
                        this.remainingFireTicks = -this.fireImmuneTicks
                    }
                    if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble)) {
                        this.playEntityOnFireExtinguishedSound()
                    }
                }
                if (this.isOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble)) {
                    this.remainingFireTicks = -this.fireImmuneTicks
                }
                this.level.profiler.pop()
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
                VampireComponent.entityKey.getNullable(e)?.isVampire ?: false -> {
                    e.addEffect(
                        MobEffectInstance(
                            SunlightSicknessEffect.instance, 4 * 20,
                            e.getEffect(SunlightSicknessEffect.instance)?.amplifier?.plus(1)?.coerceAtMost(3) ?: 0
                        )
                    )

                    e.hurt(SunlightDamageSource.instance, max(5-0.5*this.distanceTo(e), 0.0).toFloat())
                }
                e.mobType == MobType.UNDEAD && e !is Husk && e !is WitherBoss -> {
                    e.setSecondsOnFire(20)
                    e.hurt(DamageSource.ON_FIRE, max(5-0.5*this.distanceTo(e), 0.0).toFloat())
                }
            }
        }
    }
}
