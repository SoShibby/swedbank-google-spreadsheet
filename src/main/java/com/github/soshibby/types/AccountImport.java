package com.github.soshibby.types;

/**
 * Created by Henrik on 04/02/2017.
 */
public class AccountImport {

    private String accountName;
    private int lastTransactionHash;

    public AccountImport() {

    }

    public AccountImport(String accountName, int lastTransactionHash) {
        this.accountName = accountName;
        this.lastTransactionHash = lastTransactionHash;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getLastTransactionHash() {
        return lastTransactionHash;
    }

    public void setLastTransactionHash(int lastTransactionHash) {
        this.lastTransactionHash = lastTransactionHash;
    }
}
