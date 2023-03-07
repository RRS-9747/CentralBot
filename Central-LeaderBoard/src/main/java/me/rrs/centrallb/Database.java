package me.rrs.centrallb;

import me.rrs.centralbot.spigot.CentralBotAPI;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Database {

    public void createTable() {
        try (Connection connection = CentralLeaderBoard.getApi().getConnection();
             PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS CentralLB (Board varchar(255) PRIMARY KEY, RawBoard varchar(255));")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addBoard(String board, String rawBoard) {
        try (Connection connection = CentralLeaderBoard.getApi().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO CentralLB (Board, RawBoard) VALUES (?, ?)")) {
            statement.setString(1, board);
            statement.setString(2, rawBoard);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeBoard(String title) {
        try (Connection connection = CentralLeaderBoard.getApi().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM CentralLB WHERE Board = ?")) {
            statement.setString(1, title);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getRawBoard(String board) {
        try (Connection connection = CentralLeaderBoard.getApi().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT RawBoard FROM CentralLB WHERE Board = ?")) {
            statement.setString(1, board);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("RawBoard");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<String> getBoardList() {
        List<String> titles = new ArrayList<>();
        try (Connection connection = CentralLeaderBoard.getApi().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT Board FROM CentralLB")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String title = resultSet.getString("Board");
                titles.add(title);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }

}
