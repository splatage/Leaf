package io.splatage.leaf.config.modules.spawning;

import io.splatage.leaf.config.SplatageConfigModules;
import net.minecraft.world.entity.MobCategory;

import java.util.Arrays;
import java.util.Locale;

public final class HybridMobCaps extends SplatageConfigModules {

    private static final String BASE_PATH = "spawning.hybrid-mob-caps";

    public static boolean enabled = false;
    public static final int[] hardLimits = new int[MobCategory.values().length];

    @Override
    public void onLoaded() {
        Arrays.fill(hardLimits, -1);

        config.addComment(
            BASE_PATH,
            "Apply hard per-category spawn ceilings in addition to the existing local/global/per-player mob cap checks."
        );

        enabled = config.getBoolean(
            BASE_PATH + ".enabled",
            enabled,
            "Enable hybrid mob caps."
        );

        config.addComment(
            BASE_PATH + ".hard-limits",
            "Hard limits per mob category. Use -1 to disable a category limit."
        );

        for (MobCategory category : MobCategory.values()) {
            final String key = category.name().toLowerCase(Locale.ROOT);
            hardLimits[category.ordinal()] = config.getInt(
                BASE_PATH + ".hard-limits." + key,
                hardLimits[category.ordinal()],
                "Hard limit for " + key + "."
            );
        }
    }
}
