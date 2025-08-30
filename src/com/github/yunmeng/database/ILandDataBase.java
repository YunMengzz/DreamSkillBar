package com.github.yunmeng.database;

import com.github.yunmeng.database.manager.DatabasePlayerProfess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class ILandDataBase {
    private String type = "MySQL";

    public String host;
    public String port;
    public String databaseName;
    public String username;
    public String password;
    public boolean useSSL;
    private String jdbcURL;
    private Connection con;

    private String getJdbcURL() {
        if (this.jdbcURL == null) {
            StringBuilder builder = new StringBuilder("jdbc:").append(this.type.toLowerCase()).append("://");
            if ("MySQL".equalsIgnoreCase(this.type)) {


                builder.append(this.host).append(':').append(this.port).append('/').append(this.databaseName).append("?useSSL").append('=').append(this.useSSL);
            }
            this.jdbcURL = builder.toString();
        }
        return this.jdbcURL;
    }

    public Connection getConnection() throws SQLException {
        this.con = java.sql.DriverManager.getConnection(getJdbcURL(), this.username, this.password);
        return this.con;
    }

    public void closeQuietly() {
        if (this.con != null) {
            try {
                this.con.close();
            } catch (SQLException localSQLException) {
            }
        }
    }

    public void createSkillTable()
            throws SQLException {
        try {
            boolean tableExists = false;
            try {
                Statement statement = this.con.createStatement();
                statement.execute("SELECT 1 FROM `skilldata`");
                statement.close();

                tableExists = true;
            } catch (SQLException localSQLException) {
            }


            if ((!tableExists) &&
                    ("MySQL".equalsIgnoreCase(this.type))) {
                Statement statement = this.con.createStatement();
                statement.execute("CREATE TABLE `" + this.databaseName + "`.`skilldata`  (  `playername` varchar(100) CHARACTER SET utf8 NOT NULL,  `skillname` varchar(100) CHARACTER SET utf8 NULL,  `skilllevel` tinyint(5) NULL);");


                statement.close();
            }
        } catch (SQLException localSQLException1) {
        }
    }

    public void createPlayerDataTable()
            throws SQLException {
        try {
            boolean tableExists = false;
            try {
                Statement statement = this.con.createStatement();
                statement.execute("SELECT 1 FROM `playerdata`");
                statement.close();

                tableExists = true;
            } catch (SQLException localSQLException) {
            }


            if ((!tableExists) &&
                    ("MySQL".equalsIgnoreCase(this.type))) {
                Statement statement = this.con.createStatement();
                statement.execute("CREATE TABLE `" + this.databaseName + "`.`playerdata`  (  `playername` varchar(100) CHARACTER SET utf8 NOT NULL,  `skillpoint` bigint(30) NULL,  `profess` varchar(100) CHARACTER SET utf8 NOT NULL,  PRIMARY KEY (`playername`));");


                statement.close();
            }
        } catch (SQLException localSQLException1) {
        }
    }


    public void createBindTable()
            throws SQLException {
        try {
            boolean tableExists = false;
            try {
                Statement statement = this.con.createStatement();
                statement.execute("SELECT 1 FROM `binddata`");
                statement.close();

                tableExists = true;
            } catch (SQLException localSQLException) {
            }


            if ((!tableExists) &&
                    ("MySQL".equalsIgnoreCase(this.type))) {
                Statement statement = this.con.createStatement();
                statement.execute("CREATE TABLE `" + this.databaseName + "`.`binddata`  (  `playername` varchar(100) NOT NULL,  `key` tinyint(2) NULL,  `keyskill` varchar(100) CHARACTER SET utf8 NULL);");


                statement.close();
            }
        } catch (SQLException localSQLException1) {
        }
    }


    public DatabasePlayerProfess getPlayerProfessData(String playerName) {
        playerName = UnicodeUtil.toUnicode(playerName);
        DatabasePlayerProfess dpp = null;
        try {
            this.con = getConnection();
            PreparedStatement statement = this.con.prepareStatement("SELECT * FROM `playerdata` WHERE `playername`='" + playerName + "'");
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                dpp = new DatabasePlayerProfess(UnicodeUtil.toWord(result.getString(1)), UnicodeUtil.toWord(result.getString(3)), result.getInt(2));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dpp;
    }

    public void updatePlayerProfessData(DatabasePlayerProfess dpp) {
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("UPDATE `playerdata` SET `profess`=? AND `skillpoint`=? WHERE `playername`=?");

            prepareStatement.setString(3, UnicodeUtil.toUnicode(dpp.getName()));
            prepareStatement.setInt(2, dpp.getSkillPoint());
            prepareStatement.setString(1, UnicodeUtil.toUnicode(dpp.getProfess()));

            prepareStatement.execute();
            prepareStatement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insertData(DatabasePlayerProfess dpp) {
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("INSERT INTO `playerdata` (`playername`, `skillpoint`, `profess`) VALUES (?,?,?)");

            prepareStatement.setString(1, UnicodeUtil.toUnicode(dpp.getName()));
            prepareStatement.setInt(2, dpp.getSkillPoint());
            prepareStatement.setString(3, UnicodeUtil.toUnicode(dpp.getProfess()));
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void deleteData(String playerName) {
        playerName = UnicodeUtil.toUnicode(playerName);
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("DELETE FROM `playerdata` WHERE `playername`='" + playerName + "'");
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void deleteAllSkill(String playerName) {
        playerName = UnicodeUtil.toUnicode(playerName);
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("DELETE FROM `skilldata` WHERE `playername`='" + playerName + "'");
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void insertSkill(String name, String skillName, int skillLevel) {
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("INSERT INTO `skilldata` (`playername`, `skillname`, `skilllevel`) VALUES (?,?,?)");

            prepareStatement.setString(1, UnicodeUtil.toUnicode(name));
            prepareStatement.setString(2, UnicodeUtil.toUnicode(skillName));
            prepareStatement.setInt(3, skillLevel);
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public HashMap<String, Integer> getSkills(String playerName) {
        playerName = UnicodeUtil.toUnicode(playerName);
        HashMap<String, Integer> skills = new HashMap();
        try {
            this.con = getConnection();
            PreparedStatement statement = this.con.prepareStatement("SELECT * FROM `skilldata` WHERE `playername`='" + playerName + "'");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                skills.put(UnicodeUtil.toWord(result.getString(2)), Integer.valueOf(result.getInt(3)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return skills;
    }

    public HashMap<Integer, String> getBinds(String playerName) {
        playerName = UnicodeUtil.toUnicode(playerName);
        HashMap<Integer, String> binds = new HashMap();
        try {
            this.con = getConnection();
            PreparedStatement statement = this.con.prepareStatement("SELECT * FROM `binddata` WHERE `playername`='" + playerName + "'");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                binds.put(Integer.valueOf(result.getInt(2)), UnicodeUtil.toWord(result.getString(3)));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return binds;
    }

    public void insertBind(String name, int key, String skillName) {
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("INSERT INTO `binddata` (`playername`, `key`, `keyskill`) VALUES (?,?,?)");

            prepareStatement.setString(1, UnicodeUtil.toUnicode(name));
            prepareStatement.setInt(2, key);
            prepareStatement.setString(3, UnicodeUtil.toUnicode(skillName));
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public void deleteAllBind(String name) {
        String aName = UnicodeUtil.toUnicode(name);
        try {
            this.con = getConnection();
            this.con.createStatement().execute("DELETE FROM `binddata` WHERE `playername`='" + aName + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBind(String name, int key) {
        try {
            this.con = getConnection();
            PreparedStatement prepareStatement = this.con.prepareStatement("DELETE FROM `binddata` WHERE `playername`=? AND `key`=?");
            prepareStatement.setString(1, UnicodeUtil.toUnicode(name));
            prepareStatement.setInt(2, key);
            prepareStatement.execute();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }
}