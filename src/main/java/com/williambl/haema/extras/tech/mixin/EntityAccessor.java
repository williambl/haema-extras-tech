package com.williambl.haema.extras.tech.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Accessor
	float getNextStep();

	@Accessor
	void setNextStep(float nextStep);

	@Invoker
	void callPlayAmethystStepSound(BlockState state);

	@Invoker
	Vec3 callCollide(Vec3 vec);
}
