package me.rrs.centralbw;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.spigot.CentralBotAPI;
import me.rrs.centralbw.bedwars1058.commands.discord.BedWars1058PlayerStats;
import me.rrs.centralbw.bedwars1058.listeners.PlayerLeave;
import me.rrs.centralbw.utils.UpdateAPI;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CentralBedWars extends JavaPlugin {



    private static Database database;
    private static YamlDocument config;

    private static CentralBedWars instance;
    public static YamlDocument getConfiguration() {
        return config;
    }
    public static Database getDatabase() {
        return database;
    }
    public static CentralBedWars getInstance() {
        return instance;
    }

    final CentralBotAPI api = new CentralBotAPI(this);
    @Override
    public void onEnable() {
        instance = this;
        checker();

        try {
            config = YamlDocument.create(new File(api.getAddonFolder(this), "config.yml"), getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 10)), "Version").build());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getServer().getPluginManager().registerEvents(new PlayerLeave(), this);

        database = new Database();
        database.createTable();

        api.getJda().addEventListener(new BedWars1058PlayerStats());

        List<SlashCommandData> commands = new ArrayList<>();

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordSRV")){
            commands.add(Commands.slash(config.getString("Config.Command"), "Show BedWars stats")
                    .setGuildOnly(true)
                    .addOption(OptionType.USER, "user", "Give a user", false)
                    .addOption(OptionType.STRING, "name", "Give a user name", false));
        }else {
            commands.add(Commands.slash(config.getString("Config.Command"), "Show BedWars stats")
                    .setGuildOnly(true)
                    .addOption(OptionType.STRING, "name", "Give a user name", true));
        }

        api.registerSlashCommand(this, commands);
        updateChecker();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void checker() {
        String pluginName = getDescription().getName();
        List<String> pluginAuthor = getDescription().getAuthors();
        if (!pluginName.equals("CentralBot-Bedwars1058") || !pluginAuthor.contains("RRS")) {
            getLogger().severe("This plugin is not authorized to run. Shutting down.");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("BedWars1058")){
            getLogger().severe("BedWars1058 is not installed!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->{
            if (updateAPI.hasSpigotUpdate("107498")) {
                String newVersion = updateAPI.getSpigotVersion("107498");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using CentralBot " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107498/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
        }, 0L, 20L * 60 * 60 * 2);
    }
}
