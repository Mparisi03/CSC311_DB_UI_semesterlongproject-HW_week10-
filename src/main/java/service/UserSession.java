package service;

import dao.DbConnectivityClass;
import model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

public class UserSession {

    private static UserSession instance;
    private String userName;
    private String password;
    private String privileges;

    private Person user;

    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;
        Preferences userPreferences = Preferences.userRoot();
        userPreferences.put("USERNAME",userName);
        userPreferences.put("PASSWORD",password);
        userPreferences.put("PRIVILEGES",privileges);
    }



    public static UserSession getInstace(String userName,String password, String privileges) {
        if(instance == null) {
            synchronized(UserSession.class) {
                if(instance == null) {
                    instance = new UserSession(userName,password,privileges);
                }
            }

        }
        return instance;
    }

    public static UserSession getInstace(String userName,String password) {
        if(instance == null) {
            synchronized(UserSession.class) {
                if(instance == null) {
                    instance = new UserSession(userName,password,"NONE");
                }
            }
        }
        return instance;
    }

    private static List<String> registeredUsers = new ArrayList<>();

    // Method to check if a user exists in the current session or collection
    public static boolean checkUserExistsInSession(String username) {
        return registeredUsers.contains(username);
    }
    public void setCurrentUser(Person person) {
        this.user = person;
    }

    public Person getCurrentUser() {
        return this.user;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    public synchronized void cleanUserSession() {
        this.userName = "";// or null
        this.password = "";
        this.privileges = "";// or null
    }

    @Override
    public synchronized String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }

    public static boolean isUserExists(String username) {
        boolean exists = false;
        String dbUrl = "jdbc:mysql://your_server_url/your_database";
        String dbUsername = "your_db_username";
        String dbPassword = "your_db_password";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exists;
    }

}



