package io.splatage.leaf.config;

import io.papermc.paper.SparksFly;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@NullMarked
public final class SplatageConfig {

    public static final Logger LOGGER = LogManager.getLogger(SplatageConfig.class.getSimpleName());
    protected static final File I_CONFIG_FOLDER = new File("config");
    protected static final String I_CONFIG_PKG = "io.splatage.leaf.config.modules";
    protected static final String I_GLOBAL_CONFIG_FILE = "splatage-global.yml";

    private static final String SPARK_EXTRA_CONFIG_PROPERTY = "spark.serverconfigs.extra";

    private static SplatageGlobalConfig splatageGlobalConfig;

    private SplatageConfig() {
    }

    public static CompletableFuture<Void> reloadAsync(final CommandSender sender) {
        return CompletableFuture.runAsync(() -> {
            try {
                long begin = System.nanoTime();

                SplatageConfigModules.clearModules();
                loadConfig(false);
                SplatageConfigModules.loadAfterBootstrap();

                final String success = String.format("Successfully reloaded Splatage config in %sms.", (System.nanoTime() - begin) / 1_000_000);
                Command.broadcastCommandMessage(sender, Component.text(success, NamedTextColor.GREEN));
            } catch (Exception e) {
                Command.broadcastCommandMessage(sender, Component.text("Failed to reload Splatage config. See error in console!", NamedTextColor.RED));
                LOGGER.error("Failed to reload Splatage config!", e);
            }
        }, Util.ioPool());
    }

    public static void loadConfig() {
        try {
            long begin = System.nanoTime();
            LOGGER.info("Loading Splatage config...");

            loadConfig(true);

            LOGGER.info("Successfully loaded Splatage config in {}ms.", (System.nanoTime() - begin) / 1_000_000);
        } catch (Exception e) {
            LOGGER.error("Failed to load Splatage config modules!", e);
        }
    }

    private static void loadConfig(final boolean init) throws Exception {
        createDirectory(I_CONFIG_FOLDER);

        splatageGlobalConfig = new SplatageGlobalConfig(init);

        SplatageConfigModules.initModules();
    }

    public static SplatageGlobalConfig config() {
        return splatageGlobalConfig;
    }

    protected static void createDirectory(final File dir) throws IOException {
        try {
            Files.createDirectories(dir.toPath());
        } catch (FileAlreadyExistsException e) {
            if (dir.delete()) {
                createDirectory(dir);
            }
        }
    }

    public static Set<Class<?>> getClasses(final String pack) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packageDirName = pack.replace('.', '/');
        Enumeration<URL> dirs;

        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), StandardCharsets.UTF_8);
                    findClassesInPackageByFile(pack, filePath, classes);
                } else if ("jar".equals(protocol)) {
                    try {
                        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        findClassesInPackageByJar(pack, entries, packageDirName, classes);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return classes;
    }

    private static void findClassesInPackageByFile(final String packageName, final String packagePath, final Set<Class<?>> classes) {
        File dir = new File(packagePath);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] dirfiles = dir.listFiles((file) -> file.isDirectory() || file.getName().endsWith(".class"));
        if (dirfiles != null) {
            for (File file : dirfiles) {
                if (file.isDirectory()) {
                    findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
                } else {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    try {
                        classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static void findClassesInPackageByJar(final String packageName, final Enumeration<JarEntry> entries, final String packageDirName, final Set<Class<?>> classes) {
        String currentPackageName = packageName;

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.charAt(0) == '/') {
                name = name.substring(1);
            }

            if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');

                if (idx != -1) {
                    currentPackageName = name.substring(0, idx).replace('/', '.');
                }

                if (name.endsWith(".class") && !entry.isDirectory()) {
                    String className = name.substring(currentPackageName.length() + 1, name.length() - 6);
                    try {
                        classes.add(Class.forName(currentPackageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static List<String> buildSparkExtraConfigs() {
        List<String> extraConfigs = new ArrayList<>(List.of("config/splatage-global.yml"));

        String existing = System.getProperty(SPARK_EXTRA_CONFIG_PROPERTY);
        if (existing != null && !existing.isBlank()) {
            extraConfigs.addAll(Arrays.asList(existing.split(",")));
        }

        return extraConfigs;
    }

    public static void regSparkExtraConfig() {
        if (SparksFly.isPluginPreferred() && Bukkit.getServer().getPluginManager().getPlugin("spark") != null) {
            String extraConfigs = String.join(",", buildSparkExtraConfigs());
            System.setProperty(SPARK_EXTRA_CONFIG_PROPERTY, extraConfigs);
        }
    }
}
