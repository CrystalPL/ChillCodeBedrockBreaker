package pl.chillcode.bedrockbreaker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.chillcode.bedrockbreaker.listener.InventoryClickListener;
import pl.chillcode.bedrockbreaker.listener.PlayerInteractListener;
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.message.MessageAPI;
import pl.crystalek.crcapi.util.LogUtil;

import java.io.IOException;

public final class ChillBedrockBreaker extends JavaPlugin {
    Config config;

    @Override
    public void onEnable() {
        //check if nbtapi plugin is exist
        final Plugin nbtapi = Bukkit.getPluginManager().getPlugin("NBTAPI");
        if (nbtapi == null) {
            LogUtil.error("Nie odnaleziono pluginu NBTAPI!");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //check if nbtapi plugin is enabled
        if (!nbtapi.isEnabled()) {
            LogUtil.error("Odnaleziono plugin NBTAPI, lecz nie jest on uruchomiony!");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //load config
        final ConfigHelper configHelper = new ConfigHelper("config.yml");
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            LogUtil.error("Nie udało się utworzyć pliku konfiguracyjnego..");
            LogUtil.error("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        config = new Config(configHelper.getConfiguration());
        if (!config.load()) {
            LogUtil.error("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //load messages
        final MessageAPI messageAPI = new MessageAPI();
        if (!messageAPI.init()) {
            return;
        }

        //register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractListener(config), this);

        if (!config.isRepairItemInAnvil()) {
            pluginManager.registerEvents(new InventoryClickListener(), this);
        }
    }
}
