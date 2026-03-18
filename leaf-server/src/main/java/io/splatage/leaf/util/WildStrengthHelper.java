package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.entities.WildStrength;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class WildStrengthHelper {

    private static final Identifier MOVEMENT_SPEED_MODIFIER_ID = Identifier.withDefaultNamespace("splatage_wild_strength_movement_speed");
    private static final Identifier ATTACK_DAMAGE_MODIFIER_ID = Identifier.withDefaultNamespace("splatage_wild_strength_attack_damage");

    private WildStrengthHelper() {
    }

    public static void apply(final Mob mob) {
        if (!WildStrength.wildStrengthEnabled) {
            return;
        }

        if (WildStrength.movementSpeedEnabled) {
            applyPercentModifier(
                mob.getAttribute(Attributes.MOVEMENT_SPEED),
                MOVEMENT_SPEED_MODIFIER_ID,
                resolveScaledValue(
                    mob,
                    WildStrength.movementSpeedStartPercent,
                    WildStrength.movementSpeedMaxPercent,
                    WildStrength.movementSpeedDistanceToMax
                )
            );
        }

        if (WildStrength.attackDamageEnabled) {
            applyPercentModifier(
                mob.getAttribute(Attributes.ATTACK_DAMAGE),
                ATTACK_DAMAGE_MODIFIER_ID,
                resolveScaledValue(
                    mob,
                    WildStrength.attackDamageStartPercent,
                    WildStrength.attackDamageMaxPercent,
                    WildStrength.attackDamageDistanceToMax
                )
            );
        }
    }

    public static float resolveEquipmentSpawnChanceMultiplier(final Mob mob) {
        if (!isHostileMob(mob) || !WildStrength.equipmentSpawnChanceEnabled) {
            return 1.0F;
        }

        return (float) Math.max(
            0.0D,
            resolveScaledValue(
                mob,
                WildStrength.equipmentSpawnChanceStartPercent,
                WildStrength.equipmentSpawnChanceMaxPercent,
                WildStrength.equipmentSpawnChanceDistanceToMax
            ) / 100.0D
        );
    }

    public static int resolveEquipmentTierBonus(final Mob mob, final RandomSource random) {
        if (!isHostileMob(mob) || !WildStrength.equipmentTierBonusEnabled) {
            return 0;
        }

        final double bonus = Math.max(
            0.0D,
            resolveScaledValue(
                mob,
                WildStrength.equipmentTierBonusStart,
                WildStrength.equipmentTierBonusMax,
                WildStrength.equipmentTierBonusDistanceToMax
            )
        );

        final int whole = Mth.floor(bonus);
        final double fraction = bonus - whole;

        return whole + (fraction > 0.0D && random.nextDouble() < fraction ? 1 : 0);
    }

    public static float resolveEquipmentEnchantChanceMultiplier(final Mob mob) {
        if (!isHostileMob(mob) || !WildStrength.equipmentEnchantChanceEnabled) {
            return 1.0F;
        }

        return (float) Math.max(
            0.0D,
            resolveScaledValue(
                mob,
                WildStrength.equipmentEnchantChanceStartPercent,
                WildStrength.equipmentEnchantChanceMaxPercent,
                WildStrength.equipmentEnchantChanceDistanceToMax
            ) / 100.0D
        );
    }

    private static boolean isHostileMob(final Mob mob) {
        return mob.getType().getCategory() == MobCategory.MONSTER;
    }

    private static double resolveScaledValue(
        final Mob mob,
        final double startValue,
        final double maxValue,
        final int distanceToMax
    ) {
        if (mob.level().isClientSide()) {
            return startValue;
        }

        final double factor = WildScaling.getBlockDistanceFactor(
            mob.blockPosition(),
            WildStrength.centerX,
            WildStrength.centerZ,
            distanceToMax
        );
        return WildScaling.lerpPercent(factor, startValue, maxValue);
    }

    private static void applyPercentModifier(
        final AttributeInstance attribute,
        final Identifier modifierId,
        final double percent
    ) {
        if (attribute == null) {
            return;
        }

        attribute.removeModifier(modifierId);

        final double amount = (percent / 100.0D) - 1.0D;
        if (amount == 0.0D) {
            return;
        }

        attribute.addPermanentModifier(new AttributeModifier(
            modifierId,
            amount,
            AttributeModifier.Operation.ADD_MULTIPLIED_BASE
        ));
    }
}
