package me.rrs.centralbot.spigot.utils;

import me.rrs.centralbot.spigot.CentralBot;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class UpdateAPI {

    public boolean hasSpigotUpdate(String resourceId) {
        boolean hasUpdate = false;
        try (java.io.InputStream inputStream =
                     new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
             Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext())
                hasUpdate = !CentralBot.getInstance().getDescription().getVersion().equalsIgnoreCase(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
            hasUpdate = false;
        }
        return hasUpdate;
    }

    public String getSpigotVersion(String resourceId) {
        String newVersion = CentralBot.getInstance().getDescription().getVersion();
        try (InputStream inputStream =
                     new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
             java.util.Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) newVersion = String.valueOf(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;
    }

}

