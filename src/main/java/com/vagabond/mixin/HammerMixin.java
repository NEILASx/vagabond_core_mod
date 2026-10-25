//package com.vagabond.mixin;
//
//import com.evandev.manual_labour.content.item.HammerItem;
//import com.vagabond.VagabondCore;
//import net.minecraft.core.Direction;
//import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.sounds.SoundEvent;
//import net.minecraft.sounds.SoundEvents;
//import net.minecraft.world.damagesource.DamageSource;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.TamableAnimal;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import net.minecraft.world.entity.decoration.ArmorStand;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.MaceItem;
//import net.minecraft.world.item.enchantment.EnchantmentHelper;
//import net.minecraft.world.phys.Vec3;
//import org.slf4j.Logger;
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.function.Predicate;
//
//@Mixin(Item.class)
//public abstract class HammerMixin {
//    // i am extending EVERY single item but in the code it checks if its a hammer item defined by that modrinth mod
//
//    @Inject(method = "hurtEnemy", at = @At("TAIL"))
//    private void injectMaceHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
//        VagabondCore.LOGGER.info("testy test test");
//        if (attacker instanceof ServerPlayer serverPlayer && MaceItem.canSmashAttack(serverPlayer)) {
//            ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
//
//
//            if (serverPlayer.isIgnoringFallDamageFromCurrentImpulse() && serverPlayer.currentImpulseImpactPos != null) {
//                if (serverPlayer.currentImpulseImpactPos.y > serverPlayer.position().y) {
//                    serverPlayer.currentImpulseImpactPos = serverPlayer.position();
//                }
//            } else {
//                serverPlayer.currentImpulseImpactPos = serverPlayer.position();
//            }
//
//            serverPlayer.setIgnoreFallDamageFromCurrentImpulse(true);
//            serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
//            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
//
//            if (target.onGround()) {
//                serverPlayer.setSpawnExtraParticlesOnFall(true);
//                SoundEvent soundEvent = serverPlayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
//                serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), soundEvent, serverPlayer.getSoundSource(), 1.0F, 1.0F);
//            } else {
//                serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), SoundEvents.MACE_SMASH_AIR, serverPlayer.getSoundSource(), 1.0F, 1.0F);
//            }
//
//            manual_labour$knockback(serverLevel, serverPlayer, target);
//        }
//    }
//
//    @Inject(method = "postHurtEnemy", at = @At("TAIL"))
//    private void injectMacePostHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker, CallbackInfo ci) {
//        if (MaceItem.canSmashAttack(attacker)) {
//            attacker.resetFallDistance();
//        }
//    }
//
//    @Inject(method = "getAttackDamageBonus", at = @At("RETURN"), cancellable = true)
//    private void injectMaceDamageBonus(Entity target, float damage, DamageSource damageSource, CallbackInfoReturnable<Float> cir) {
//        if (damageSource.getDirectEntity() instanceof LivingEntity livingEntity && MaceItem.canSmashAttack(livingEntity)) {
//            float fallDistance = livingEntity.fallDistance;
//            float bonusDamage;
//
//            if (fallDistance <= 3.0F) {
//                bonusDamage = 4.0F * fallDistance;
//            } else if (fallDistance <= 8.0F) {
//                bonusDamage = 12.0F + 2.0F * (fallDistance - 3.0F);
//            } else {
//                bonusDamage = 22.0F + fallDistance - 8.0F;
//            }
//
//            if (livingEntity.level() instanceof ServerLevel serverLevel) {
//                bonusDamage += EnchantmentHelper.modifyFallBasedDamage(serverLevel, livingEntity.getWeaponItem(), target, damageSource, 0.0F) * fallDistance;
//            }
//
//            cir.setReturnValue(cir.getReturnValue() + bonusDamage);
//        }
//    }
//
//    @Unique
//    private static void manual_labour$knockback(ServerLevel level, Player attacker, Entity target) {
//        level.levelEvent(2013, target.getOnPos(), 750);
//        level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3.5), manual_labour$knockbackPredicate(attacker, target))
//                .forEach(entity -> {
//                    Vec3 direction = entity.position().subtract(target.position());
//                    double power = manual_labour$getKnockbackPower(attacker, entity, direction);
//                    Vec3 impulse = direction.normalize().scale(power);
//                    if (power > 0.0) {
//                        entity.push(impulse.x, 0.7F, impulse.z);
//                        if (entity instanceof ServerPlayer serverPlayer) {
//                            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
//                        }
//                    }
//                });
//    }
//
//    @Unique
//    private static Predicate<LivingEntity> manual_labour$knockbackPredicate(Player attacker, Entity target) {
//        return entity -> {
//            boolean isSpectator = entity.isSpectator();
//            boolean isSelfOrTarget = entity == attacker || entity == target;
//            boolean isAllied = attacker.isAlliedTo(entity);
//            boolean isTamedPet = entity instanceof TamableAnimal animal && animal.isTame() && attacker.getUUID().equals(animal.getOwnerUUID());
//            boolean isMarkerArmorStand = entity instanceof ArmorStand armorStand && armorStand.isMarker();
//            boolean inRange = target.distanceToSqr(entity) <= Math.pow(3.5, 2.0);
//
//            return !isSpectator && !isSelfOrTarget && !isAllied && !isTamedPet && !isMarkerArmorStand && inRange;
//        };
//    }
//
//    @Unique
//    private static double manual_labour$getKnockbackPower(Player attacker, LivingEntity entity, Vec3 delta) {
//        return (3.5 - delta.length())
//                * 0.7F
//                * (double) (attacker.fallDistance > 5.0F ? 2 : 1)
//                * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
//    }
//}