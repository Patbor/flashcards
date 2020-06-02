package com.company;

import java.util.Objects;

public class Account {
    private String login;
    private int password;



    public void setLogin(String login) {
        this.login = login;
    }



    public void setPassword(int password) {
        this.password = password;
    }
public Account() {

}


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return login.equals(account.login) &&
                password == (account.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, password);
    }
}
