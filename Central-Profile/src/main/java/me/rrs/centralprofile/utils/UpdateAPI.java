package me.rrs.centralprofile.utils;

import me.rrs.centralprofile.CentralProfile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;


public class UpdateAPI {

    public boolean hasSpigotUpdate(String resourceId) {
        boolean hasUpdate = false;
        try (InputStream inputStream =
                     new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
             Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext())
                hasUpdate = !CentralProfile.getInstance().getDescription().getVersion().equalsIgnoreCase(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
            hasUpdate = false;
        }
        return hasUpdate;
    }

    public String getSpigotVersion(String resourceId) {
        String newVersion = CentralProfile.getInstance().getDescription().getVersion();
        try (InputStream inputStream =
                     new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openStream();
             Scanner scanner = new Scanner(inputStream)) {
            if (scanner.hasNext()) newVersion = String.valueOf(scanner.next());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newVersion;
    }

}

