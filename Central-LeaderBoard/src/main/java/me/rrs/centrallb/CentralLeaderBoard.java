package me.rrs.centrallb;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.spigot.CentralBotAPI;
import me.rrs.centrallb.commands.discord.Leaderboard;
import me.rrs.centrallb.commands.minecraft.DelBoard;
import me.rrs.centrallb.commands.minecraft.SetBoard;
import me.rrs.centrallb.utils.UpdateAPI;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CentralLeaderBoard extends JavaPlugin {


    public static YamlDocument getConfiguration() {
        return config;
    }
    public static CentralLeaderBoard getInstance() {
        return instance;
    }
    public static Database getDatabase() {
        return database;
    }
    public static CentralBotAPI getApi() {
        return api;
    }

    private static YamlDocument config;
    private static Database database;
    private static CentralLeaderBoard instance;
    private static CentralBotAPI api;


    @Override
    public void onEnable() {
        instance = this;
        api = new CentralBotAPI(this);
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

        getCommand("addboard").setExecutor(new SetBoard());
        getCommand("delboard").setExecutor(new DelBoard());

        api.getJda().addEventListener(new Leaderboard());

        List<SlashCommandData> commands = new ArrayList<>();

        SlashCommandData command = Commands.slash(config.getString("Config.Command"), "Show LeaderBoard")
                .setGuildOnly(true);
        OptionData boardOption = new OptionData(OptionType.STRING, "board", "Give a board name", true);
        for (String board: database.getBoardList()){
            boardOption.addChoice(board, board);
        }

        OptionData timeOption = new OptionData(OptionType.STRING, "time", "Time of the Leaderboard", false)
                .addChoice("All-Time", "alltime")
                .addChoice("Hourly", "hourly")
                .addChoice("Daily", "daily")
                .addChoice("Weekly", "weekly")
                .addChoice("Monthly", "monthly")
                .addChoice("Yearly", "yearly");

        command.addOptions(boardOption, timeOption);

        commands.add(command);

        api.registerSlashCommand(this, commands);


        log("&m&l[&6&lCentralBot&r&m&l]&r Hooked into ajLeaderboards");

        updateChecker();


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->{
            if (updateAPI.hasSpigotUpdate("107379")) {
                String newVersion = updateAPI.getSpigotVersion("107379");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using CentralBot " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107379/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
        }, 0L, 20L * 60 * 60 * 2);
    }

    public static void log (String log){
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', log));
    }
}
