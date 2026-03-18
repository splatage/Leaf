package io.splatage.leaf.util;

import io.splatage.leaf.config.modules.spawning.HostilePackComposition;
import io.splatage.leaf.config.modules.spawning.HostilePackComposition.HostilePackType;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.MobSpawnSettings;

public final class HostilePackCompositionHelper {

    private HostilePackCompositionHelper() {
    }

    public static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnerData(
        final WeightedList<MobSpawnSettings.SpawnerData> spawners,
        final BlockPos origin,
        final RandomSource random
    ) {
        if (!HostilePackComposition.packCompositionEnabled) {
            return spawners.getRandom(random);
        }

        final List<Weighted<MobSpawnSettings.SpawnerData>> entries = spawners.unwrap();
        int totalWeight = 0;

        for (final Weighted<MobSpawnSettings.SpawnerData> entry : entries) {
            totalWeight += resolveSpawnWeight(entry.value().type(), origin, entry.weight());
        }

        if (totalWeight <= 0) {
            return Optional.empty();
        }

        int selectedWeight = random.nextInt(totalWeight);

        for (final Weighted<MobSpawnSettings.SpawnerData> entry : entries) {
            final int resolvedWeight = resolveSpawnWeight(entry.value().type(), origin, entry.weight());
            if (resolvedWeight <= 0) {
                continue;
            }

            selectedWeight -= resolvedWeight;
            if (selectedWeight < 0) {
                return Optional.of(entry.value());
            }
        }

        return Optional.empty();
    }

    public static int resolveSpawnWeight(final EntityType<?> entityType, final BlockPos origin, final int baseWeight) {
        if (baseWeight <= 0) {
            return 0;
        }

        final HostilePackType type = HostilePackComposition.resolveType(entityType);
        if (type == null || !HostilePackComposition.weightScalingEnabled) {
            return baseWeight;
        }

        final double percent = resolveWeightPercent(origin);
        return Math.max(0, (int) Math.round(baseWeight * Math.max(0.0D, percent) / 100.0D));
    }

    private static double resolveWeightPercent(final BlockPos origin) {
        final double factor = WildScaling.getBlockDistanceFactor(
            origin,
            HostilePackComposition.centerX,
            HostilePackComposition.centerZ,
            HostilePackComposition.weightDistanceToMax
        );
        return WildScaling.lerpPercent(
            factor,
            HostilePackComposition.weightStartPercent,
            HostilePackComposition.weightMaxPercent
        );
    }
}
