package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.worldgen.OreRichness;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
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

    public static double resolveRegionVeinSizePercent(final ServerLevel level, final ChunkPos center) {
        if (!OreRichness.veinSizeEnabled) {
            return 100.0D;
        }

        final double factor = WildScaling.getChunkDistanceFactor(level, center, OreRichness.veinSizeDistanceToMaxChunksSq);
        return WildScaling.lerpPercent(factor, OreRichness.veinSizeStartPercent, OreRichness.veinSizeMaxPercent);
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

        if (isBlacklisted(oreConfiguration)) {
            return 1;
        }

        final double percent = getFrequencyPercent(level, origin);
        return percentToRepeats(percent, random);
    }

    public static int getScaledOreSize(
        final WorldGenLevel level,
        final OreConfiguration config,
        final BlockPos origin,
        final RandomSource random
    ) {
        if (isBlacklisted(config)) {
            return Math.max(1, config.size);
        }

        final double percent = getVeinSizePercent(level, origin);
        return percentToScaledSize(config.size, percent, random);
    }

    private static double getFrequencyPercent(final WorldGenLevel level, final BlockPos origin) {
        if (!OreRichness.frequencyEnabled) {
            return 100.0D;
        }

        if (level instanceof WorldGenRegion worldGenRegion) {
            return worldGenRegion.splatage$getOreFrequencyPercent();
        }

        final double factor = WildScaling.getBlockDistanceFactor(level.getLevel(), origin, OreRichness.frequencyDistanceToMax);
        return WildScaling.lerpPercent(factor, OreRichness.frequencyStartPercent, OreRichness.frequencyMaxPercent);
    }

    private static double getVeinSizePercent(final WorldGenLevel level, final BlockPos origin) {
        if (!OreRichness.veinSizeEnabled) {
            return 100.0D;
        }

        if (level instanceof WorldGenRegion worldGenRegion) {
            return worldGenRegion.splatage$getOreVeinSizePercent();
        }

        final double factor = WildScaling.getBlockDistanceFactor(level.getLevel(), origin, OreRichness.veinSizeDistanceToMax);
        return WildScaling.lerpPercent(factor, OreRichness.veinSizeStartPercent, OreRichness.veinSizeMaxPercent);
    }

    private static int percentToRepeats(final double percent, final RandomSource random) {
        final double multiplier = Math.max(0.0D, percent / 100.0D);
        final int whole = Mth.floor(multiplier);
        final double fractional = multiplier - whole;
        return whole + (random.nextDouble() < fractional ? 1 : 0);
    }

    private static int percentToScaledSize(final int originalSize, final double percent, final RandomSource random) {
        final double scaled = Math.max(1.0D, originalSize * Math.max(0.0D, percent / 100.0D));
        final int whole = Mth.floor(scaled);
        final double fractional = scaled - whole;
        return Math.max(1, whole + (random.nextDouble() < fractional ? 1 : 0));
    }

    private static boolean isBlacklisted(final OreConfiguration config) {
        final Set<String> blacklist = OreRichness.blacklist;
        if (blacklist.isEmpty()) {
            return false;
        }

        for (final OreConfiguration.TargetBlockState targetBlockState : config.targetStates) {
            final String blockKey = BuiltInRegistries.BLOCK.getKey(targetBlockState.state.getBlock()).toString();
            if (blacklist.contains(blockKey)) {
                return true;
            }
        }

        return false;
    }
}
