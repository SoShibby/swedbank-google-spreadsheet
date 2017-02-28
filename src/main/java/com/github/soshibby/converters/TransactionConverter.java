package com.github.soshibby.converters;

import com.github.soshibby.swedbank.types.Account;
import com.github.soshibby.swedbank.types.Transaction;
import com.github.soshibby.types.Table;

import java.util.List;
import java.util.StringJoiner;

/**
 * Created by Henrik on 02/02/2017.
 */
public class TransactionConverter {

    public static Table convert(Account account, List<Transaction> transactions) {
        Table table = new Table();

        transactions.forEach(transaction ->
                table.newRow()
                        .newColumn(transaction.getDate().toString())
                        .newColumn(account.getAccountNumber())
                        .newColumn(account.getName())
                        .newColumn(transaction.getDescription())
                        .newColumn(transaction.getAmount())
                        .newColumn(accountBalance(transaction))
                        .newColumn(categoryNames(transaction))
                        .newColumn(categoryGroups(transaction))
                        .newColumn(transaction.getId())
        );

        return table;
    }

    private static String accountBalance(Transaction transaction) {
        if (transaction == null || transaction.getAccountingBalance() == null || transaction.getAccountingBalance().getAmount() == null) {
            return "-";
        }

        return transaction.getAccountingBalance().getAmount().toString();
    }

    private static String categoryNames(Transaction transaction) {
        StringJoiner sb = new StringJoiner(", ");

        if (transaction == null || transaction.getCategorizations() == null || transaction.getCategorizations().getCategories() == null) {
            return "";
        }

        transaction.getCategorizations().getCategories().forEach(category -> {
            String name = category != null && category.getName() != null ? category.getName() : "";
            sb.add(name);
        });

        return sb.toString();
    }

    private static String categoryGroups(Transaction transaction) {
        StringJoiner sb = new StringJoiner(", ");

        if (transaction == null || transaction.getCategorizations() == null || transaction.getCategorizations().getCategories() == null) {
            return "";
        }

        transaction.getCategorizations().getCategories().forEach(category -> {
            String group = category != null && category.getGroup() != null ? category.getGroup() : "";
            sb.add(group);
        });

        return sb.toString();
    }

}
