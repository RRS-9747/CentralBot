package me.rrs.essentials.spigot.commands.discord;

import dev.dejvokep.boostedyaml.YamlDocument;

import me.rrs.essentials.spigot.CentralEssentials;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CommandRunner extends ListenerAdapter implements CommandSender {
    private final YamlDocument config = CentralEssentials.getConfiguration();
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals(config.getString("Bot.Command-Run"))) {
            List<Role> adminRoles = CentralEssentials.getApi().getAdminRoles();
            Member member = event.getMember();

            if (member == null || member.getRoles().stream().noneMatch(adminRoles::contains)) {
                // User doesn't have an admin role, send an error message
                event.reply("You don't have permission to run this command.").setEphemeral(true).queue();
                return;
            }

            String command = event.getOption("command").getAsString();

            // Execute the command with this CommandRunner instance as the sender
            Bukkit.getScheduler().runTask(CentralEssentials.getInstance(), () -> {
                // Use this CommandRunner instance as the sender
                Bukkit.dispatchCommand(this, command);

                // Send the output back to Discord
                String output = outputStream.toString();
                if (output.isEmpty()){
                    event.reply("No output with this command!").setEphemeral(true).queue();
                }else {
                    event.reply("```" + output + "```").setEphemeral(true).queue();
                }

                outputStream.reset();

            });
        }
    }

    @Override
    public void sendMessage(String message) {
        String stripped = ChatColor.stripColor(message);
        try {
            outputStream.write(stripped.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputStream.write('\n');
    }

    @Override
    public void sendMessage(@NotNull String... messages) {

    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {

    }

    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {

    }

    @NotNull
    @Override
    public Server getServer() {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return "[CentralBot]";
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return true;
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return true;
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return null;
    }

    @NotNull
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return null;
    }

    @Nullable
    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {

    }

    @Override
    public void recalculatePermissions() {

    }

    @NotNull
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
    setOp(true);
    }
}
