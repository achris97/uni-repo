/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Account;

/**
 *
 * @author achri
 */
public class AccountDao {

    private static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/bankdb", "achris", "qwerty");

        } catch (ClassNotFoundException | SQLException ex) {
        }

        return conn;
    }

    public static List<Account> getAllAcounts() {

        Connection conn = AccountDao.getConnection();
        List<Account> list = new ArrayList<>();

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM Accounts");
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Account ac = new Account(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getDouble(6), rs.getString(7).charAt(0));
                list.add(ac);
            }
            conn.close();
        } catch (SQLException ex) {
        }
        return list;
    }

    public static Account getAccountById(int id) {

        Account ac = new Account();
        Connection conn = AccountDao.getConnection();

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM Accounts WHERE ID=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                ac.setId(rs.getInt(1));
                ac.setName(rs.getString(2));
                ac.setSurname(rs.getString(3));
                ac.setTel(rs.getString(4));
                ac.setEmail(rs.getString(5));
                ac.setBalance(rs.getDouble(6));
                ac.setStatus(rs.getString(7).charAt(0));
            }
            conn.close();
        } catch (SQLException ex) {
        }
        return ac;
    }

    public static int addAccount(Account ac) {

        int s = 0;
        Connection conn = AccountDao.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement("INSERT INTO Accounts (Name, Surname, Tel, email) VALUES (?, ?, ?, ?)");
            pst.setString(1, ac.getName());
            pst.setString(2, ac.getSurname());
            pst.setString(3, ac.getTel());
            pst.setString(4, ac.getEmail());
            s = pst.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
        }
        return s;
    }

    public static int deleteAccount(int id) {
        int res = 0;
        Connection conn = AccountDao.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement("DELETE FROM Accounts WHERE ID =?");
            pst.setInt(1, id);
            res = pst.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
        }
        return res;
    }

    //inactive accounts maintain their balance but they cannot complete any kind of transaction
    public static int accountStatus(int id) {
        Connection conn = AccountDao.getConnection();
        int res = -1;
        char status;
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT Status FROM Accounts WHERE ID=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                status = rs.getString(1).charAt(0);
                if (status == 'I') {
                    status = 'A';
                    res = 1;
                } else if (status == 'A'){
                    status = 'I';
                    res = 0;
                }
                pst = conn.prepareStatement("UPDATE Accounts SET Status=? WHERE ID = ?");
                pst.setString(1, String.valueOf(status));
                pst.setInt(2, id);
                pst.executeUpdate();
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
 
    public static int makeDeposit(int id, double amount) {
        //deposit will be successful if amount is valid and account is active
        if (amount <= 0) return -1;
        Connection conn = AccountDao.getConnection();
        int res = 0;
        char status;
        double balance;

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT Balance, Status FROM Accounts WHERE ID=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble(1);
                status = rs.getString(2).charAt(0);
                if (status == 'I') {
                    return -2;
                }
                balance += amount;
                pst = conn.prepareStatement("UPDATE Accounts SET Balance=? WHERE ID=?");
                pst.setDouble(1, balance);
                pst.setInt(2, id);
                res = pst.executeUpdate();             
            }
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(AccountDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static int makeWithdrawal(int id, double amount) {

        if (amount <= 0) return -1;
        Connection conn = AccountDao.getConnection();
        int res = 0;
        char status;
        double balance;

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT Balance, Status FROM Accounts WHERE ID=?");
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                balance = rs.getDouble(1);
                status = rs.getString(2).charAt(0);
                if (status == 'I') {
                    return -2;
                }
                if (balance - amount >= 0) { //have to check if requested amount is less than the current balance.
                    balance -= amount;
                    pst = conn.prepareStatement("UPDATE Accounts SET Balance=? WHERE ID=?");
                    pst.setDouble(1, balance);
                    pst.setInt(2, id);
                    res = pst.executeUpdate();
                } else {
                    return -3;
                }                
            }
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(AccountDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public static int makeTransfer (int id1, int id2, double amount) {
        if (amount <= 0) return -1;
        if (AccountDao.makeWithdrawal(id1, amount) == 1){
            if (AccountDao.makeDeposit(id2, amount) == 1)
                return 1;
            else
                AccountDao.makeDeposit(id1, amount); //if amount cannot be credited to receiver's account, put it back to sender's.
        }
        return 0;
    }
}