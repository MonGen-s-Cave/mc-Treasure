package net.netherpulse.nptreasure;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.scheduler.impl.BukkitScheduler;
import lombok.Getter;
import net.netherpulse.nptreasure.listener.LocationSessionListener;
import net.netherpulse.nptreasure.listener.MenuListener;
import net.netherpulse.nptreasure.listener.TreasureListener;
import net.netherpulse.nptreasure.manager.TreasureManager;
import net.netherpulse.nptreasure.particles.ParticleSystem;
import net.netherpulse.nptreasure.utils.RegisterUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import revxrsal.zapper.ZapperJavaPlugin;

import java.io.File;

public final class NpTreasure extends ZapperJavaPlugin {
    @Getter static NpTreasure instance;
    @Getter Config language;
    @Getter BukkitScheduler scheduler;
    @Getter Config guis;
    @Getter Config treasures;
    @Getter ParticleSystem particleSystem;
    Config config;

    @Override
    public void onLoad() {
        instance = this;
        scheduler = new BukkitScheduler(this);
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
    }

    @Override
    public void onDisable() {
        TreasureManager.getInstance().saveTreasures();
        TreasureManager.getInstance().saveCooldowns();
        particleSystem.shutdown();
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
