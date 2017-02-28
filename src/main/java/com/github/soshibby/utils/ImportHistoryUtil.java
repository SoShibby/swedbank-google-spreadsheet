package com.github.soshibby.utils;

import com.github.soshibby.swedbank.types.Account;
import com.github.soshibby.swedbank.types.Transaction;
import com.github.soshibby.swedbank.util.Assert;
import com.github.soshibby.types.AccountImport;
import com.github.soshibby.types.ImportHistory;

/**
 * Created by Henrik on 04/02/2017.
 */
public class ImportHistoryUtil {

    public static Integer getLastTransactionHash(ImportHistory importHistory, Account account) {
        Assert.notNull(importHistory, "Can't get last transaction. ImportHistory object is null.");

        AccountImport accountImport = getAccountImport(importHistory, account);
        return accountImport == null ? null : accountImport.getLastTransactionHash();
    }

    public static void setLastTransaction(ImportHistory importHistory, Account account, Transaction transaction) {
        Assert.notNull(importHistory, "Can't set last transaction. ImportHistory object is null.");
        Assert.notNull(account, "Can't set last transaction. Account object is null.");
        Assert.notNull(transaction, "Can't set last transaction. Transaction object is null.");
        Assert.notEmpty(account.getName(), "Can't set last transaction. Account name is empty.");

        AccountImport accountImport = getAccountImport(importHistory, account);

        if (accountImport == null) {
            accountImport = new AccountImport(account.getName(), transaction.hashCode());
            importHistory.getAccountImports().add(accountImport);
        } else {
            accountImport.setLastTransactionHash(transaction.hashCode());
        }
    }

    private static AccountImport getAccountImport(ImportHistory importHistory, Account account) {
        for (AccountImport accountImport : importHistory.getAccountImports()) {
            if (account != null && accountImport != null && accountImport.getAccountName() != null && accountImport.getAccountName().equals(account.getName())) {
                return accountImport;
            }
        }

        return null;
    }
}
