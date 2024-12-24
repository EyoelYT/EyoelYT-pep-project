package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class AccountDAO {

    // FIXME: Not needed?
    public List<Account> getAllAccounts() {
        Connection connection = ConnectionUtil.getConnection();
        List<Account> accounts = new ArrayList<>();

        try {
            String sql = "SELECT * FROM account";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                Account account = new Account(
                                              rs.getInt("account_id"),
                                              rs.getString("username"),
                                              // rs.getString("password")
                                              "---" // don't display password when getting users
                                              );
                accounts.add(account);
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return accounts;
    }

    public List<Message> getAllMessagesByAccount(int account_id) {
        Connection connection = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message WHERE posted_by=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, account_id);

            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()) {
                Message message = new Message(
                                              rs.getInt("message_id"),
                                              rs.getInt("posted_by"),
                                              rs.getString("message_text"),
                                              rs.getLong("time_posted_epoch")
                                              );
                messages.add(message);
            }
        }
        catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    public Account insertAccount(Account account) {
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO account (username, password) VALUES (?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());

            preparedStatement.executeUpdate();

            ResultSet pkeyResultSet = preparedStatement.getGeneratedKeys();
            if (pkeyResultSet.next()) {
                int generated_account_id = (int) pkeyResultSet.getLong(1);
                return new Account(generated_account_id, account.getUsername(), account.getPassword());
            }

        } catch (SQLException e) {
            System.out.println("Error inserting account: " + e.getMessage());
        }

        return null;
    }

    public Account getAccountByUsername(String username) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account WHERE username=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Account(
                                   rs.getInt("account_id"),
                                   rs.getString("username"),
                                   rs.getString("password")
                                   );
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }

    public Account getMessageByAccount(int posted_by) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM message WHERE posted_by=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, posted_by);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new Account(
                                   rs.getInt("account_id"),
                                   rs.getString("posted_by"),
                                   rs.getString("password")
                                   );
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }

    public Account getAccountById(int posted_by) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account WHERE account_id=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, posted_by);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                return new Account(
                                   rs.getInt("account_id"),
                                   rs.getString("posted_by"),
                                   rs.getString("password")
                                   );
            }
        }
        catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }
        return null;
    }

    public Account verifyAccountExists(String username, String password) {
        Connection connection = ConnectionUtil.getConnection();

        try {
            String sql = "SELECT * FROM account WHERE username=? AND password=?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return new Account(
                                   rs.getInt("account_id"),
                                   rs.getString("username"),
                                   rs.getString("password")
                                   );
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
