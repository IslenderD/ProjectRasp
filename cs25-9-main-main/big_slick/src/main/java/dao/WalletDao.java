package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.DatabaseHandler;

/**
 * Contains all connections with the SQL of the money related.
 */
public class WalletDao {

    /// Name of the Wallet Table.
    private static final String tableName = "Wallet";

    /// When an account is created, their wallet starts with this amount.
    private static final Integer startAmount = 1000;

    /// When a User's Wallet has less than this amount, reset it to this amount after some time.
    private static final Integer resetAmount = 200;

    /**
     * Creates a Wallet for a new User.
     * @param username username of new User
     * @return if Wallet was successfully created
     */
    public static boolean createWallet(String username) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "INSERT INTO " + tableName + " (username, amount) VALUES (?, " + startAmount +")";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            int st = ps.executeUpdate();
            conn.commit();
            return st == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the amount of chips inside a User's wallet.
     * @param username the username of the User
     * @return the amount inside the wallet
     */
    public static Integer getAmount(String username) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "SELECT amount FROM " + tableName + " WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet st = ps.executeQuery();
            if (st.next()) {
                return st.getInt("amount");
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Changes the amount of chips in a User's Wallet. To increase the amount in the Wallet, give
     * a positive number representing the amount needed to be added. To decrease the amount in the
     * Wallet, give a negative number representing the amount needed to be removed (ex. if 20 needs
     * to be removed, then {@code amount = -20}
     * @param username the username of the User
     * @param amount the amount needed to be added (positive amount) or removed (negative amount
     * @return if the change was successful
     */
    public static boolean changeAmount(String username, int amount) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            int wallet = getAmount(username) + amount;
            String sql = "UPDATE " + tableName + " SET amount = ? WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, wallet);
            ps.setString(2, username);
            int st = ps.executeUpdate();
            conn.commit();
            return st == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resets a User's Wallet to {@link WalletDao#resetAmount}. This function should be triggered
     * after a User's Wallet is less than {@link WalletDao#resetAmount} for quite some time.
     * @param username the username of the User
     * @return if the User's Wallet is reset
     */
    public static boolean resetAmount(String username) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "UPDATE " + tableName + " SET amount = ? WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, resetAmount);
            ps.setString(2, username);
            int st = ps.executeUpdate();
            conn.commit();
            return st == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a User's Wallet. This function should be deleted if the User gets deleted.
     * @param username the username of the (soon to be) deleted User
     * @return if the Wallet was successfully deleted
     */
    public static boolean deleteWallet(String username) {
        try (Connection conn = DatabaseHandler.getConnection()) {
            String sql = "DELETE " + tableName + " WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            int st = ps.executeUpdate();
            conn.commit();
            return st == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the amount of chips where the wallet starts timer to resets.
     * @return resetAmount
     */
    public static Integer getResetAmount() {
        return resetAmount;
    }
}
