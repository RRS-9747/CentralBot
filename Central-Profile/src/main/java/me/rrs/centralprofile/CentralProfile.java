package me.rrs.centralprofile;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.spigot.CentralBotAPI;
import me.rrs.centralprofile.command.discord.StatsCommand;
import me.rrs.centralprofile.command.minecraft.EditPAPICommand;
import me.rrs.centralprofile.command.minecraft.RemovePAPICommand;
import me.rrs.centralprofile.command.minecraft.SetPAPICommand;
import me.rrs.centralprofile.utils.UpdateAPI;
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

public final class CentralProfile extends JavaPlugin {


    private static YamlDocument config;
    private static Database database;
    private static CentralProfile instance;


    public static YamlDocument getConfiguration() {
        return config;
    }
    public static CentralProfile getInstance() {
        return instance;
    }
    public static Database getDatabase() {
        return database;
    }

    final CentralBotAPI api = new CentralBotAPI(this);

    @Override
    public void onEnable() {
        instance = this;
        checker();
        database = new Database();
        database.createTable();

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

        getCommand("addpapi").setExecutor(new SetPAPICommand());
        getCommand("removepapi").setExecutor(new RemovePAPICommand());
        getCommand("editpapi").setExecutor(new EditPAPICommand());



        api.getJda().addEventListener(new StatsCommand());

        List<SlashCommandData> commands = new ArrayList<>();

        if (Bukkit.getServer().getPluginManager().isPluginEnabled("DiscordSRV")){
            commands.add(Commands.slash(config.getString("Bot.Command"), "Show player stats")
                    .setGuildOnly(true)
                    .addOption(OptionType.USER, "user", "Give a user", false)
                    .addOption(OptionType.STRING, "name", "Give a user name", false));
        }else {
            commands.add(Commands.slash(config.getString("Bot.Command"), "Show player stats")
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
        if (!pluginName.equals("Central-Profile") || !pluginAuthor.contains("RRS")) {
            getLogger().severe("This plugin is not authorized to run. Shutting down.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->{
            if (updateAPI.hasSpigotUpdate("107771")) {
                String newVersion = updateAPI.getSpigotVersion("107771");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using CentralBot " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107771/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
        }, 0L, 20L * 60 * 60 * 2);
    }


}
