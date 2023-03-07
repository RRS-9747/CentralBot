package me.rrs.centralbot.spigot;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.Pattern;
import dev.dejvokep.boostedyaml.dvs.segment.Segment;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.rrs.centralbot.spigot.commands.MainCommand;
import me.rrs.centralbot.spigot.utils.Metrics;
import me.rrs.centralbot.spigot.utils.UpdateAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class CentralBot extends JavaPlugin {

    private static JDA jda;
    private YamlDocument config;
    private static CentralBot instance;
    private static CommandListUpdateAction updateAction;
    private static Database database;
    private final String PREFIX = "&m&l[&6&lCentralBot&r&m&l]&r ";
    private static List<String> playerRole, adminRole;
    private static final Map<String, Map<String, Integer>> addonsList = new HashMap<>();
    private static final List<SlashCommandData> commands = new ArrayList<>();
    private static String guildID;


    public static List<SlashCommandData> getCommands() {
        return commands;
    }
    public static Map<String, Map<String, Integer>> getAddonsList() {
        return addonsList;
    }
    public static List<String> getPlayerRoles(){
        return playerRole;
    }
    public static List<String> getAdminRoles() {
        return adminRole;
    }
    public static JDA getJda() {
        return jda;
    }
    public static CommandListUpdateAction getUpdateAction() {
        return updateAction;
    }
    public static CentralBot getInstance() {
        return instance;
    }
    public static Database getDatabase(){
        return database;
    }
    public static String getGuildID() {
        return guildID;
    }


    @Override
    public void onLoad(){
        instance = this;
        Metrics metrics = new Metrics(this, 17649);
        File dir = new File(getDataFolder().getPath(), "addons");
        if (!dir.exists()){
            dir.mkdir();
        }
        metrics.addCustomChart(new Metrics.DrilldownPie("addon_usage", () -> addonsList));
    }

    @Override
    public void onEnable () {
        checker();
        log(PREFIX + "Loading configuration file...");
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), getResource("config.yml"),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new Pattern(Segment.range(1, Integer.MAX_VALUE),
                            Segment.literal("."), Segment.range(0, 10)), "Version").build());
            log(PREFIX + "✔ Configuration file loaded successfully!");
        } catch (IOException e) {
            log(PREFIX + "✖ Failed to load configuration file!");
            throw new RuntimeException(e);
        }

        getCommand("centralbot").setExecutor(new MainCommand(config));

        guildID = config.getString("Server.ID");

        log(PREFIX + "Setting up database connection...");
        database = new Database(config);
        database.setupDataSource();

        log(PREFIX + "Loading bot...");
        loadBot();


        playerRole = config.getStringList("Server.Player-Role");
        adminRole = config.getStringList("Server.Admin-Role");
        updateAction = jda.updateCommands();
        Bukkit.getScheduler().runTaskLater(this, () ->{
            updateAction.queue();
            log(PREFIX + "Slash Commands Registered!");
        }, 40);

        updateChecker();
    }

    @Override
    public void onDisable () {
        if (jda != null) {
            jda.updateCommands().queue();
            jda.shutdown();
        }
    }


    public static void log (String log) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', log));
    }


    private void updateChecker(){
        UpdateAPI updateAPI = new UpdateAPI();
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->{
            if (updateAPI.hasSpigotUpdate("107799")) {
                String newVersion = updateAPI.getSpigotVersion("107799");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.hasPermission("centralbot.notify")) {
                        p.sendMessage("--------------------------------");
                        p.sendMessage("You are using CentralBot " + getDescription().getVersion());
                        p.sendMessage("However version " + newVersion + " is available.");
                        p.sendMessage("You can download it from: " + "https://www.spigotmc.org/resources/107799/");
                        p.sendMessage("--------------------------------");
                    }
                }
            }
        }, 0L, 20L * 60 * 60 * 2);
    }


    private void checker() {
        String pluginName = getDescription().getName();
        List<String> pluginAuthor = getDescription().getAuthors();
        if (!pluginName.equals("CentralBot") || !pluginAuthor.contains("RRS")) {
            getLogger().severe("This plugin is not authorized to run. Shutting down.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadBot() {
        jda = JDABuilder.createDefault(config.getString("Bot.Token"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .setAutoReconnect(true)
                .setActivity(Activity.of(Activity.ActivityType.valueOf(config.getString("Bot.Activity.Type")),
                        config.getString("Bot.Activity.Name")))
                .setStatus(OnlineStatus.valueOf(config.getString("Bot.Status")))
                .build();
        try {
            jda.awaitReady();
            log(PREFIX + "Logged as &b" + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator());
        } catch (InterruptedException ignored) {
        }
    }


}

