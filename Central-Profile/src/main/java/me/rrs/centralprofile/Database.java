package me.rrs.centralprofile;

import me.rrs.centralbot.spigot.CentralBotAPI;
import java.sql.*;
import java.util.*;

public class Database {

    final CentralBotAPI api = new CentralBotAPI(CentralProfile.getInstance());

    public void createTable() {
        try (Connection connection = api.getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CentralBotProfile (PAPITitle varchar(255) PRIMARY KEY, PAPIString varchar(255));")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean createPAPITitle(String title, String papiString) {
        try (Connection connection = api.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO CentralBotProfile (PAPITitle, PAPIString) VALUES (?, ?)")) {
            statement.setString(1, title);
            statement.setString(2, papiString);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> getAllPAPITitles() {
        Map<String, String> titles = new LinkedHashMap<>();
        try (Connection connection = api.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT PAPITitle, PAPIString FROM CentralBotProfile")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                titles.put(resultSet.getString("PAPITitle"), resultSet.getString("PAPIString"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

    public boolean updatePAPITitle(String newTitle, String oldTitle) {
        try (Connection connection = api.getConnection()) {
            String sql = "UPDATE CentralBotProfile SET PAPITitle = ? WHERE PAPITitle = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newTitle);
            statement.setString(2, oldTitle);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePAPIString(String newString, String title) {
        try (Connection connection = api.getConnection()) {
            String sql = "UPDATE CentralBotProfile SET PAPIString = ? WHERE PAPITitle = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, newString);
            statement.setString(2, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removePAPITitle(String title) {
        try (Connection connection = api.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM CentralBotProfile WHERE PAPITitle = ?")) {
            statement.setString(1, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getPAPITitles() {
        List<String> titles = new ArrayList<>();
        try (Connection connection = api.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT PAPITitle FROM CentralBotProfile")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("PAPITitle");
                titles.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }
}
