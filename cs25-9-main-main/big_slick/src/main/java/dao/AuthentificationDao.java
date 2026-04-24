package dao;

import database.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Contains all connections with the SQL of the authentification.
 */
public class AuthentificationDao {

    /// Name of the table affected by this Dao.
    private static String tableName = "Authentification";

    /**
     * Gets password on database based on the username.
     * @param username username to find password
     * @return password
     */
    public static String getHashedPasswordByUsername(String username) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "SELECT password FROM " + tableName + " WHERE username = ?"; //TODO: Test this statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet st = ps.executeQuery();
            if (st.next()) {
                return st.getString("password");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the password of a user.
     * @param username username of the user
     * @param hashedPassword password (already hashed) to be updated to
     * @return If update was successful or not
     */
    public static boolean updatePassword(String username, String hashedPassword) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "UPDATE " + tableName + " SET password = ? WHERE username = ?"; //TODO: Test This Statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, hashedPassword);
            ps.setString(2, username);
            int rs = ps.executeUpdate();
            conn.commit();
            return 1 == rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the given username already exists on database.
     * @param username username being checked
     * @return if username exists or not
     */
    public static boolean isUsernameAlreadyInDatabase(String username) {
        try (Connection conn = DatabaseHandler.getConnection()){
            String sql = "SELECT EXISTS(SELECT * FROM " + tableName + " WHERE username = ?) AS user_exists"; //TODO: Test This Statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return 1==rs.getInt("user_exists");
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds user to the database.
     * @param username username of user being added
     * @param password already hashed password of user being added
     * @return if addition was done successfully
     */
    public static boolean addUserToDatabase(String username, String password) {
        try (Connection conn = DatabaseHandler.getConnection()){
            if (!isUsernameAlreadyInDatabase(username)) {
                String sql = "INSERT INTO " + tableName + " (username, password) VALUES (?, ?)"; //TODO: Test This Statement
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                int rs = ps.executeUpdate();
                conn.commit();
                return rs > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a user from the database.
     * @param username username of user being deleted
     * @param password password of user being deleted
     * @return if deletion was successfully done
     */
    public static boolean deleteUserFromDatabase(String username, String password) {
        try (Connection conn = DatabaseHandler.getConnection()){
            String sql = "DELETE FROM " + tableName + " WHERE username = ? AND password = ?"; //TODO: Test This Statement
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            int rs = ps.executeUpdate();
            conn.commit();
            return rs == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
