package io.splatage.leaf.util;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ServerLevelAccessor;

public final class WildScaling {

    private WildScaling() {
    }

    public static double horizontalFactor(final ServerLevelAccessor level, final int x, final int z, final int distanceToMax) {
        if (distanceToMax <= 0) {
            return 1.0D;
        }

        final BlockPos spawn = level.getLevel().getSharedSpawnPos();
        final double dx = (double) x - (double) spawn.getX();
        final double dz = (double) z - (double) spawn.getZ();
        final double distance = Math.sqrt(dx * dx + dz * dz);
        return Mth.clamp(distance / (double) distanceToMax, 0.0D, 1.0D);
    }

    public static double multiplier(final double factor, final double maxMultiplier, final ScalingCurve curve) {
        if (maxMultiplier <= 1.0D) {
            return 1.0D;
        }
        return 1.0D + curve.apply(factor) * (maxMultiplier - 1.0D);
    }

    public static int scaleInt(final int base, final double multiplier, final int min, final int max) {
        return Mth.clamp((int) Math.round((double) base * multiplier), min, max);
    }

    public enum ScalingCurve {
        LINEAR {
            @Override
            public double apply(final double factor) {
                return factor;
            }
        },
        SMOOTHSTEP {
            @Override
            public double apply(final double factor) {
                return factor * factor * (3.0D - 2.0D * factor);
            }
        },
        EASE_IN_QUAD {
            @Override
            public double apply(final double factor) {
                return factor * factor;
            }
        },
        EASE_OUT_QUAD {
            @Override
            public double apply(final double factor) {
                return 1.0D - (1.0D - factor) * (1.0D - factor);
            }
        };

        public abstract double apply(double factor);

        public String configKey() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public static ScalingCurve fromConfig(final String value, final ScalingCurve fallback) {
            if (value == null) {
                return fallback;
            }

            final String normalized = value.trim().toUpperCase(Locale.ROOT);
            for (final ScalingCurve curve : values()) {
                if (curve.name().equals(normalized)) {
                    return curve;
                }
            }
            return fallback;
        }
    }
}
