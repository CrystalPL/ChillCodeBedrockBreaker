package pl.chillcode.bedrockbreaker.command;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;
import pl.chillcode.bedrockbreaker.config.Config;
import pl.crystalek.crcapi.command.impl.Command;
import pl.crystalek.crcapi.command.impl.SingleCommand;
import pl.crystalek.crcapi.command.model.CommandData;
import pl.crystalek.crcapi.core.config.exception.ConfigLoadException;
import pl.crystalek.crcapi.message.api.MessageAPI;
import pl.crystalek.crcapi.message.api.replacement.Replacement;

import java.util.List;
import java.util.Map;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReloadCommand extends SingleCommand {
    Config config;

    public ReloadCommand(final MessageAPI messageAPI, final Map<Class<? extends Command>, CommandData> commandDataMap, final Config config) {
        super(messageAPI, commandDataMap);

        this.config = config;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        try {
            config.load();
            config.reloadConfig();

            if (!messageAPI.init()) {
                throw new ConfigLoadException("");
            }

            messageAPI.sendMessage("reload.reload", sender);
        } catch (final ConfigLoadException exception) {
            messageAPI.sendMessage("reload.error", sender, Replacement.of("{ERROR}", exception.getMessage()));
        }
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return null;
    }

    @Override
    public String getPermission() {
        return null;
    }

    @Override
    public boolean isUseConsole() {
        return false;
    }

    @Override
    public String getCommandUsagePath() {
        return null;
    }

    @Override
    public int maxArgumentLength() {
        return 0;
    }

    @Override
    public int minArgumentLength() {
        return 0;
    }
}
