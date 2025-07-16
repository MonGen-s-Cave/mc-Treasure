package com.mongenscave.mctreasure;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.mongenscave.mctreasure.config.Config;
import com.mongenscave.mctreasure.hooks.plugins.PlaceholderAPI;
import com.mongenscave.mctreasure.listener.LocationSessionListener;
import com.mongenscave.mctreasure.listener.MenuListener;
import com.mongenscave.mctreasure.listener.TreasureListener;
import com.mongenscave.mctreasure.managers.TreasureManager;
import com.mongenscave.mctreasure.particles.ParticleSystem;
import com.mongenscave.mctreasure.update.UpdateChecker;
import com.mongenscave.mctreasure.utils.LoggerUtils;
import com.mongenscave.mctreasure.utils.PlayerUtils;
import com.mongenscave.mctreasure.utils.RegisterUtils;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperJavaPlugin;

import java.io.File;

public final class McTreasure extends ZapperJavaPlugin {
    @Getter static McTreasure instance;
    @Getter Config language;
    @Getter TaskScheduler scheduler;
    @Getter Config guis;
    @Getter Config treasures;
    @Getter ParticleSystem particleSystem;
    @Getter UpdateChecker updateChecker;
    Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = UniversalScheduler.getScheduler(this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        initializeComponents();

        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new TreasureListener(), this);
        getServer().getPluginManager().registerEvents(new LocationSessionListener(), this);

        particleSystem = new ParticleSystem();

        TreasureManager.getInstance().loadTreasures();
        RegisterUtils.registerCommands();

        new Metrics(this, 25975);
        updateChecker = new UpdateChecker(7857);
        PlaceholderAPI.registerHook();

        LoggerUtils.printStartup();
    }

    @Override
    public void onDisable() {
        if (TreasureManager.getInstance() != null) TreasureManager.getInstance().saveTreasures();

        if (particleSystem != null) {
            particleSystem.shutdown();
            particleSystem = null;
        }

        if (updateChecker != null) {
            updateChecker.shutdown();
            updateChecker = null;
        }

        PlayerUtils.cleanup();
    }

    public Config getConfiguration() {
        return config;
    }

    private void initializeComponents() {
        final GeneralSettings generalSettings = GeneralSettings.builder()
                .setUseDefaults(false)
                .build();

        final LoaderSettings loaderSettings = LoaderSettings.builder()
                .setAutoUpdate(true)
                .build();

        final UpdaterSettings updaterSettings = UpdaterSettings.builder()
                .setKeepAll(true)
                .build();

        config = loadConfig("config.yml", generalSettings, loaderSettings, updaterSettings);
        language = loadConfig("messages.yml", generalSettings, loaderSettings, updaterSettings);
        treasures = loadConfig("treasures.yml", generalSettings, loaderSettings, updaterSettings);
        guis = loadConfig("guis.yml", generalSettings, loaderSettings, updaterSettings);
    }

    @NotNull
    @Contract("_, _, _, _ -> new")
    private Config loadConfig(@NotNull String fileName, @NotNull GeneralSettings generalSettings, @NotNull LoaderSettings loaderSettings, @NotNull UpdaterSettings updaterSettings) {
        return new Config(
                new File(getDataFolder(), fileName),
                getResource(fileName),
                generalSettings,
                loaderSettings,
                DumperSettings.DEFAULT,
                updaterSettings
        );
    }
}