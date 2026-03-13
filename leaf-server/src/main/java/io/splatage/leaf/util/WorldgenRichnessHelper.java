package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.worldgen.OreRichness;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public final class WorldgenRichnessHelper {

    private WorldgenRichnessHelper() {
    }

    public static int resolvePlacementPasses(
        final ServerLevelAccessor level,
        final BlockPos origin,
        final ConfiguredFeature<?, ?> configuredFeature,
        final RandomSource random
    ) {
        final OreConfiguration oreConfiguration = getParticipatingOreConfiguration(configuredFeature);
        if (oreConfiguration == null) {
            return 1;
        }

        final double multiplier = chanceMultiplier(level, origin, oreConfiguration);
        int passes = Math.max(1, Mth.floor(multiplier));
        final double fractional = multiplier - (double) passes;
        if (fractional > 0.0D && random.nextDouble() < fractional) {
            ++passes;
        }
        return passes;
    }

    public static OreConfiguration scaleVeinSize(
        final ServerLevelAccessor level,
        final BlockPos origin,
        final OreConfiguration oreConfiguration
    ) {
        if (!participates(oreConfiguration)) {
            return oreConfiguration;
        }

        final double multiplier = veinSizeMultiplier(level, origin, oreConfiguration);
        final int scaledSize = WildScaling.scaleInt(
            oreConfiguration.size,
            multiplier,
            1,
            Math.max(1, OreRichness.maxGeneratedVeinSize)
        );

        if (scaledSize == oreConfiguration.size) {
            return oreConfiguration;
        }

        return new OreConfiguration(oreConfiguration.targetStates, scaledSize, oreConfiguration.discardChanceOnAirExposure);
    }

    public static boolean participates(final OreConfiguration oreConfiguration) {
        if (!OreRichness.enabled || oreConfiguration == null) {
            return false;
        }

        final List<OreConfiguration.TargetBlockState> targets = oreConfiguration.targetStates;
        if (targets.isEmpty()) {
            return false;
        }

        boolean included = OreRichness.includedTargetBlocks.isEmpty();
        for (final OreConfiguration.TargetBlockState target : targets) {
            final ResourceLocation key = BuiltInRegistries.BLOCK.getKey(target.state.getBlock());
            final String blockId = key.toString().toLowerCase(Locale.ROOT);

            if (OreRichness.excludedTargetBlocks.contains(blockId)) {
                return false;
            }
            if (!included && OreRichness.includedTargetBlocks.contains(blockId)) {
                included = true;
            }
        }

        return included;
    }

    private static OreConfiguration getParticipatingOreConfiguration(final ConfiguredFeature<?, ?> configuredFeature) {
        if (!OreRichness.enabled || configuredFeature == null) {
            return null;
        }

        if (!(configuredFeature.config() instanceof OreConfiguration oreConfiguration)) {
            return null;
        }

        final Feature<?> feature = configuredFeature.feature();
        if (feature != Feature.ORE && feature != Feature.SCATTERED_ORE) {
            return null;
        }

        return participates(oreConfiguration) ? oreConfiguration : null;
    }

    private static double chanceMultiplier(
        final ServerLevelAccessor level,
        final BlockPos origin,
        final OreConfiguration oreConfiguration
    ) {
        if (!participates(oreConfiguration)) {
            return 1.0D;
        }

        final double factor = WildScaling.horizontalFactor(
            level,
            origin.getX(),
            origin.getZ(),
            Math.max(1, OreRichness.chanceDistanceToMax)
        );
        return WildScaling.multiplier(factor, OreRichness.maxChanceMultiplier, OreRichness.chanceCurve);
    }

    private static double veinSizeMultiplier(
        final ServerLevelAccessor level,
        final BlockPos origin,
        final OreConfiguration oreConfiguration
    ) {
        if (!participates(oreConfiguration)) {
            return 1.0D;
        }

        final double factor = WildScaling.horizontalFactor(
            level,
            origin.getX(),
            origin.getZ(),
            Math.max(1, OreRichness.veinSizeDistanceToMax)
        );
        return WildScaling.multiplier(factor, OreRichness.maxVeinSizeMultiplier, OreRichness.veinSizeCurve);
    }
}
