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
import pl.crystalek.crcapi.config.ConfigHelper;
import pl.crystalek.crcapi.message.MessageAPI;

import java.io.IOException;

public final class ChillBedrockBreaker extends JavaPlugin {

    @Override
    public void onEnable() {
        //check if nbtapi plugin is exist
        final Plugin nbtapi = Bukkit.getPluginManager().getPlugin("NBTAPI");
        if (nbtapi == null) {
            getLogger().severe("Nie odnaleziono pluginu NBTAPI!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //check if nbtapi plugin is enabled
        if (!nbtapi.isEnabled()) {
            getLogger().severe("Odnaleziono plugin NBTAPI, lecz nie jest on uruchomiony!");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //load config
        final ConfigHelper configHelper = new ConfigHelper("config.yml", this);
        try {
            configHelper.checkExist();
            configHelper.load();
        } catch (final IOException exception) {
            getLogger().severe("Nie udało się utworzyć pliku konfiguracyjnego..");
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            exception.printStackTrace();
            return;
        }

        final Config config = new Config(configHelper.getConfiguration(), this);
        if (!config.load()) {
            getLogger().severe("Wyłączanie pluginu");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //load messages
        final MessageAPI messageAPI = new MessageAPI(this);
        if (!messageAPI.init()) {
            return;
        }

        //register listeners
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new PlayerInteractListener(config, messageAPI), this);

        if (!config.isRepairItemInAnvil()) {
            pluginManager.registerEvents(new InventoryClickListener(messageAPI), this);
        }

        CommandRegistry.register(new GiveCommand(config, messageAPI));
    }
}
