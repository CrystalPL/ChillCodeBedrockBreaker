package pl.chillcode.bedrockbreaker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import pl.chillcode.bedrockbreaker.command.GiveCommand;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.chillcode.bedrockbreaker.listener.InventoryClickListener;
import pl.chillcode.bedrockbreaker.listener.PlayerInteractListener;
import pl.crystalek.crcapi.command.CommandRegistry;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.MessageAPIProvider;

import java.io.IOException;

public final class ChillBedrockBreaker extends JavaPlugin {

    @Override
    public void onEnable() {
        final Plugin nbtapi = Bukkit.getPluginManager().getPlugin("NBTAPI");
        if (nbtapi == null) {
            getLogger().severe("Nie odnaleziono pluginu NBTAPI!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!nbtapi.isEnabled()) {
            getLogger().severe("Odnaleziono plugin NBTAPI, lecz nie jest on uruchomiony!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final MessageAPI messageAPI = Bukkit.getServicesManager().getRegistration(MessageAPIProvider.class).getProvider().getSingleMessage(this);
        if (!messageAPI.init()) {
            return;
        }

        final Config config = new Config(this, "config.yml");
        try {
            config.checkExist();
            config.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        try {
            config.loadConfig();
        } catch (final ConfigLoadException exception) {
            getLogger().severe(exception.getMessage());
            getLogger().severe("Wyłączanie pluginu..");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractListener(config, messageAPI), this);

        if (!config.isRepairItemInAnvil()) {
            pluginManager.registerEvents(new InventoryClickListener(messageAPI), this);
        }

        CommandRegistry.register(new GiveCommand(messageAPI, config.getCommandDataMap(), config));
    }
}
