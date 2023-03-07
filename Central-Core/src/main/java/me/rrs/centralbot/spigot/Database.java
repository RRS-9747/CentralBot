package me.rrs.centralbot.spigot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.io.File;

public class Database {

    final YamlDocument config;

    public Database(YamlDocument config){
        this.config = config;
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    private HikariDataSource dataSource;

    public void setupDataSource() {
        String connectionString = config.getString("Database.URL");
        String username = config.getString("Database.User");
        String password = config.getString("Database.Password");
        HikariConfig config = new HikariConfig();

        // Determine the database type based on the JDBC driver
        String databaseType;
        String driverClassName;
        if (connectionString.contains("mysql")) {
            databaseType = "mysql";
            driverClassName = "com.mysql.cj.jdbc.Driver";
        } else if (connectionString.contains("postgresql")) {
            databaseType = "postgresql";
            driverClassName = "org.postgresql.Driver";
        } else if (connectionString.contains("sqlite")) {
            databaseType = "sqlite";
            driverClassName = "org.sqlite.JDBC";
        } else {
            throw new IllegalArgumentException("Unsupported database type");
        }

        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(connectionString);
        config.setUsername(username);
        config.setPassword(password);

        // Configure additional settings based on the database type
        switch (databaseType) {
            case "mysql":
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                break;
            case "postgresql":
                config.addDataSourceProperty("cachePreparedStatements", "true");
                config.addDataSourceProperty("prepareThreshold", "3");
                break;
            case "sqlite":
                String databaseName = connectionString.substring(connectionString.lastIndexOf("/") + 1);
                File dataFolder = CentralBot.getInstance().getDataFolder();
                File dbFile = new File(dataFolder, databaseName.replace("jdbc:sqlite:", ""));
                String absolutePath = dbFile.getAbsolutePath();
                config.setJdbcUrl("jdbc:sqlite:" + absolutePath);
                break;
        }

        this.dataSource = new HikariDataSource(config);

        // Log the database driver being used
        CentralBot.log("&m&l[&6&lCentralBot&r&m&l]&r Using "  + driverClassName +  " for "  + databaseType +  " database");
    }


}

