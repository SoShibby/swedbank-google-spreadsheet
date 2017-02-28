package com.github.soshibby.utils;

import com.github.soshibby.swedbank.types.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 04/02/2017.
 */
public class TransactionUtil {

    public static List<Transaction> filterTransactions(List<Transaction> transactions, Integer stopAtTransactionHash) {
        List<Transaction> filteredTransactions = new ArrayList();

        if (containsTransactionHash(transactions, stopAtTransactionHash)) {
            for (int i = 0; i < indexOfTransaction(transactions, stopAtTransactionHash); i++) {
                filteredTransactions.add(transactions.get(i));
            }
        } else {
            filteredTransactions.addAll(transactions);
        }

        return filteredTransactions;
    }

    public static boolean containsTransactionHash(List<Transaction> transactions, Integer transactionHash) {
        return indexOfTransaction(transactions, transactionHash) == -1 ? false : true;
    }

    public static int indexOfTransaction(List<Transaction> transactions, Integer transactionHash) {
        if (transactionHash == null) {
            return -1;
        }

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);

            if (transaction != null && transaction.hashCode() == transactionHash) {
                return i;
            }
        }

        return -1;
    }

}
