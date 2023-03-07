package me.rrs.essentials.bungee;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.bungee.CentralBotAPI;
import me.rrs.essentials.bungee.commands.discord.CommandRunner;
import me.rrs.essentials.bungee.commands.discord.PlayerList;
import me.rrs.essentials.bungee.utils.UpdateAPI;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CentralEssentials extends Plugin {

    private static YamlDocument config;
    private static CentralEssentials instance;
    private static CentralBotAPI api;

    public static YamlDocument getConfiguration() {
        return config;
    }
    public static CentralEssentials getInstance() {
        return instance;
    }
    public static CentralBotAPI getApi() {
        return api;
    }


    @Override
    public void onEnable() {
        instance = this;
        api = new CentralBotAPI(this);
        try {
            config = YamlDocument.create(new File(api.getAddonFolder(this), "config.yml"), getResourceAsStream("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 10)), "Version").build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        api.getJda().addEventListener(new PlayerList());
        api.getJda().addEventListener(new CommandRunner());

        List<SlashCommandData> commands = new ArrayList<>();

        commands.add(Commands.slash(config.getString("Bot.Command-List"), "Show online player list")
                .setGuildOnly(true));

        commands.add(Commands.slash(config.getString("Bot.Command-Run"), "Run a command")
                .setGuildOnly(true)
                .addOption(OptionType.STRING, "command", "Command to execute", true));

        api.registerSlashCommand(this, commands);

        updateChecker();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        getProxy().getScheduler().schedule(this, () -> {
            if (updateAPI.hasSpigotUpdate("107771")) {
                String newVersion = updateAPI.getSpigotVersion("107771");
                for (ProxiedPlayer p : getProxy().getPlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage(TextComponent.fromLegacyText("--------------------------------"));
                        p.sendMessage(TextComponent.fromLegacyText("You are using Central-PlayerList"  + getDescription().getVersion()));
                        p.sendMessage(TextComponent.fromLegacyText("However version " + newVersion + " is available."));
                        p.sendMessage(TextComponent.fromLegacyText("You can download it from: https://www.spigotmc.org/resources/107771/"));
                        p.sendMessage(TextComponent.fromLegacyText("--------------------------------"));
                    }
                }
            }
        }, 0L, 1, TimeUnit.HOURS);
    }
}
