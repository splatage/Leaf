package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.worldgen.OreRichness;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

public final class WorldgenRichnessHelper {

    private WorldgenRichnessHelper() {
    }

    public static double resolveRegionFrequencyPercent(final ServerLevel level, final ChunkPos center) {
        if (!OreRichness.frequencyEnabled) {
            return 100.0D;
        }

        final double factor = WildScaling.getChunkDistanceFactor(level, center, OreRichness.frequencyDistanceToMaxChunksSq);
        return WildScaling.lerpPercent(factor, OreRichness.frequencyStartPercent, OreRichness.frequencyMaxPercent);
    }

    public static int getPlacementRepeats(
        final WorldGenLevel level,
        final ConfiguredFeature<?, ?> configuredFeature,
        final BlockPos origin,
        final RandomSource random
    ) {
        if (!(configuredFeature.config() instanceof OreConfiguration oreConfiguration)) {
            return 1;
        }

        if (!participates(oreConfiguration)) {
            return 1;
        }

        final double percent = getFrequencyPercent(level, origin);
        return percentToRepeats(percent, random);
    }

    private static double getFrequencyPercent(final WorldGenLevel level, final BlockPos origin) {
        if (!OreRichness.frequencyEnabled) {
            return 100.0D;
        }

        if (level instanceof RegionOreRichnessAccess access) {
            return access.splatage$getOreFrequencyPercent();
        }

        final double factor = WildScaling.getBlockDistanceFactor(level.getLevel(), origin, OreRichness.frequencyDistanceToMax);
        return WildScaling.lerpPercent(factor, OreRichness.frequencyStartPercent, OreRichness.frequencyMaxPercent);
    }

    private static int percentToRepeats(final double percent, final RandomSource random) {
        final double multiplier = Math.max(0.0D, percent / 100.0D);
        final int whole = Mth.floor(multiplier);
        final double fractional = multiplier - whole;
        return whole + (random.nextDouble() < fractional ? 1 : 0);
    }

    private static boolean participates(final OreConfiguration config) {
        final Set<String> whitelist = OreRichness.whitelist;
        if (whitelist.isEmpty()) {
            return false;
        }

        for (final OreConfiguration.TargetBlockState targetBlockState : config.targetStates) {
            final String blockKey = BuiltInRegistries.BLOCK.getKey(targetBlockState.state.getBlock()).toString();
            if (whitelist.contains(blockKey)) {
                return true;
            }
        }

        return false;
    }
}
