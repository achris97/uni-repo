/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author achri
 */
@XmlRootElement(name = "account")
public class Account implements Serializable {
    
    private int id;
    private double balance;
    private String name, surname, tel, email;
    private char status;

    public Account() {
    }

    public Account(int id, String name, String surname, String tel, String email, double balance, char status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.tel = tel;
        this.email = email;
        this.balance = balance;
        this.status = status;
    }

    public Account(String name, String surname, String tel, String email) {
        this.name = name;
        this.surname = surname;
        this.tel = tel;
        this.email = email;
    }
        
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }
    
}
