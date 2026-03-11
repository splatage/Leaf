package io.splatage.leaf.config;

import it.unimi.dsi.fastutil.objects.ObjectArrays;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public abstract class SplatageConfigModules {

    private static final Set<SplatageConfigModules> MODULES = new HashSet<>();

    protected final SplatageGlobalConfig config;

    public SplatageConfigModules() {
        this.config = SplatageConfig.config();
    }

    public static void initModules() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] classes = SplatageConfig.getClasses(SplatageConfig.I_CONFIG_PKG).toArray(new Class[0]);
        ObjectArrays.quickSort(classes, Comparator.comparing(Class::getSimpleName));

        for (Class<?> clazz : classes) {
            if (!SplatageConfigModules.class.isAssignableFrom(clazz)) {
                continue;
            }
            if (clazz == SplatageConfigModules.class) {
                continue;
            }
            if (clazz.isEnum() || clazz.isInterface() || clazz.isAnonymousClass() || clazz.isLocalClass() || clazz.isSynthetic()) {
                continue;
            }
            if (Modifier.isAbstract(clazz.getModifiers())) {
                continue;
            }

            SplatageConfigModules module = (SplatageConfigModules) clazz.getConstructor().newInstance();
            module.onLoaded();
            MODULES.add(module);
        }
    }

    public static void loadAfterBootstrap() {
        for (SplatageConfigModules module : MODULES) {
            module.onPostLoaded();
        }

        try {
            SplatageConfig.config().saveConfig();
        } catch (Exception e) {
            SplatageConfig.LOGGER.error("Failed to save Splatage config file!", e);
        }
    }

    public static void clearModules() {
        MODULES.clear();
    }

    public abstract void onLoaded();

    public void onPostLoaded() {
    }
}
