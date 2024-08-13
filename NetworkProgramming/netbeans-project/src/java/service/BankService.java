/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import db.AccountDao;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import model.Account;

/**
 * REST Web Service
 *
 * @author achri
 */
@Path("/service")
public class BankService {
    
    public static final String INSERT_SUCCESS = "<h3>Account created successfully.</h3>";
    public static final String INSERT_FAILURE = "<h3>Cannot create account.</h3>";
    private static final String ACCOUNT_DEACTIVATION = "<h3>Account Deactivated.</h3>";
    private static final String ACCOUNT_ACTIVATION = "<h3>Account Activated.</h3>";
    private static final String STATUS_CHANGE_FAILURE = "<h3>Status change failure.</h3>";
    public static final String DELETE_SUCCESS = "<h3>Account Deleted Successfully.</h3>";
    public static final String DELETE_FAILURE = "<h3>Cannot delete account.</h3>";
    public static final String TRANSACTION_ERROR = "<h3>Transaction error.</h3>";
    public static final String INVALID_AMOUNT = "<h3>Invalid amount.</h3>";
    public static final String INACTIVE_ACCOUNT = "<h3>Inactive accounts cannot complete any transaction.</h3>";
    private static final String DEPOSIT_SUCCESS = "<h3>Successful Deposit.</h3>";
    private static final String WITHDRAWAL_SUCCESS = "<h3>Successful Withdrawal.</h3>";
    private static final String INSUFFICIENT_BALANCE = "<h3>Insufficient balance.</h3>";
    private static final String TRANSFER_SUCCESS = "<h3>Transfer successfull.</h3>";
    private static final String TRANSFER_FAILURE = "<h3>Transfer failure.</h3>";
    
    @GET
    @Path("/accountsxml")
    @Produces(MediaType.APPLICATION_XML)
    public List<Account> getAllAccountsXML() {
        return AccountDao.getAllAcounts();
    }

    @GET
    @Path("/accountsjson")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Account> getAllAccountsJSON() {
        return AccountDao.getAllAcounts();
    }

    @GET
    @Path("/accountsxml/{id}")
    @Produces(MediaType.APPLICATION_XML)
    public Account getAccountXML(@PathParam("id") int id) {
        return AccountDao.getAccountById(id);
    }

    @GET
    @Path("/accountsjson/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Account getAccountJSON(@PathParam("id") int id) {
        return AccountDao.getAccountById(id);
    }

    @POST
    @Path("/newaccount")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String createAccount(@FormParam("name") String name, @FormParam("surname") String surname,
            @FormParam("tel") String tel, @FormParam("email") String email) {

        Account ac = new Account(name, surname, tel, email);
        int res = AccountDao.addAccount(ac);
        if (res == 1) {
            return INSERT_SUCCESS;
        }
        return INSERT_FAILURE;
    }

    @PUT
    @Path("/changestatus/{id}")
    @Produces(MediaType.TEXT_HTML)
    public static String changeStatus(@PathParam("id") int id) {
        int res = AccountDao.accountStatus(id);
        if (res != 0)
            if (res == 1)
                return ACCOUNT_ACTIVATION;
            else
                return STATUS_CHANGE_FAILURE;
        else return ACCOUNT_DEACTIVATION;
    }

    @DELETE
    @Path("/deleteaccount")
    @Produces(MediaType.TEXT_HTML)
    public String deleteAccount(@QueryParam("id") int id) {
        int res = AccountDao.deleteAccount(id);
        if (res == 1) 
            return DELETE_SUCCESS;
        return DELETE_FAILURE;
    }

    @PUT
    @Path("/accountdeposit/{id}")
    @Produces(MediaType.TEXT_HTML)
    public static String makeDeposit(@PathParam("id") int id, @QueryParam("amount") double amount) {
        int res = AccountDao.makeDeposit(id, amount);
        switch (res) {
            case -1:
                return INVALID_AMOUNT;
            case -2:
                return INACTIVE_ACCOUNT;
            case 1:
                return DEPOSIT_SUCCESS;
            default:
                return TRANSACTION_ERROR;
        }
    }
   
    @PUT
    @Path("/accountwithdrawal/{id}")
    @Produces(MediaType.TEXT_HTML)
    public static String makeWithdrawal(@PathParam("id") int id, @QueryParam("amount") double amount) {
        int res = AccountDao.makeWithdrawal(id, amount);
        switch (res) {
            case -1:
                return INVALID_AMOUNT;
            case -2:
                return INACTIVE_ACCOUNT;
            case -3:
                return INSUFFICIENT_BALANCE;
            case 1:
                return WITHDRAWAL_SUCCESS;
            default:
                return TRANSACTION_ERROR;
        }
    }

    @PUT
    @Path("/accounttransfer/{id1}")
    @Produces(MediaType.TEXT_HTML)
    public static String makeTransfer(@PathParam("id1") int id1, @QueryParam("id") int id2, @QueryParam("amount") double amount) {
        int res = AccountDao.makeTransfer(id1, id2, amount);
        if (res == 1)
            return TRANSFER_SUCCESS;
        return TRANSFER_FAILURE;
    }
    

}
