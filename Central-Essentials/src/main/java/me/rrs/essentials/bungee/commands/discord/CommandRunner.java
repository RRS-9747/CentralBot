package me.rrs.essentials.bungee.commands.discord;

import dev.dejvokep.boostedyaml.YamlDocument;
import me.rrs.essentials.bungee.CentralEssentials;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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
            // Use this CommandRunner instance as the sender
            ProxyServer.getInstance().getPluginManager().dispatchCommand(this, command);

            // Send the output back to Discord
            String output = outputStream.toString();
            if (output.isEmpty()){
                event.reply("No output with this command!").setEphemeral(true).queue();
            }else {
                event.reply("```" + output + "```").setEphemeral(true).queue();
            }

                outputStream.reset();


        }
    }

    @Override
    public String getName() {
        return "[CentralBot]";
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
    public void sendMessages(String... messages) {

    }

    @Override
    public void sendMessage(BaseComponent... message) {

    }

    @Override
    public void sendMessage(BaseComponent message) {

    }

    @Override
    public Collection<String> getGroups() {
        return null;
    }

    @Override
    public void addGroups(String... groups) {

    }

    @Override
    public void removeGroups(String... groups) {

    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void setPermission(String permission, boolean value) {

    }

    @Override
    public Collection<String> getPermissions() {
        return null;
    }

}
