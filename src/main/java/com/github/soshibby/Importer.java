package com.github.soshibby;

import com.github.soshibby.converters.TransactionConverter;
import com.github.soshibby.integration.GoogleSpreadsheet;
import com.github.soshibby.swedbank.Swedbank;
import com.github.soshibby.swedbank.exceptions.SwedbankAuthenticationException;
import com.github.soshibby.swedbank.exceptions.SwedbankServerException;
import com.github.soshibby.swedbank.exceptions.SwedbankUnauthorizedException;
import com.github.soshibby.swedbank.exceptions.SwedbankUserException;
import com.github.soshibby.swedbank.types.Account;
import com.github.soshibby.swedbank.types.AccountDetails;
import com.github.soshibby.swedbank.types.AccountList;
import com.github.soshibby.swedbank.types.Transaction;
import com.github.soshibby.swedbank.util.Assert;
import com.github.soshibby.types.ImportHistory;
import com.github.soshibby.types.Table;
import com.github.soshibby.utils.ImportHistoryUtil;
import com.github.soshibby.utils.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 02/02/2017.
 */
public class Importer {

    private static final Logger log = LoggerFactory.getLogger(Importer.class);
    private Swedbank swedbank;
    private String spreadsheetId;

    public Importer(Swedbank swedbank, String spreadsheetId) {
        Assert.notNull(swedbank, "Swedbank object is null.");
        Assert.notEmpty(spreadsheetId, "No spreadsheet id specified. Spreadsheet id was empty.");

        this.swedbank = swedbank;
        this.spreadsheetId = spreadsheetId;
    }

    public ImportHistory importAllAccounts(AccountList accountList, ImportHistory importHistory) throws SwedbankServerException, SwedbankUnauthorizedException, SwedbankAuthenticationException, SwedbankUserException, IOException {
        log.info("Importing all accounts.");
        List<Account> accounts = new ArrayList();
        accounts.addAll(accountList.getTransactionAccounts());
        accounts.addAll(accountList.getCardAccounts());
        accounts.addAll(accountList.getLoanAccounts());
        accounts.addAll(accountList.getSavingAccounts());
        accounts.addAll(accountList.getTransactionDisposalAccounts());

        for (Account account : accounts) {
            log.info("Importing transactions on account '{}'.", account.getName());
            Integer lastTransactionHash = ImportHistoryUtil.getLastTransactionHash(importHistory, account);
            log.debug("Last transaction hash when importing was {}.", lastTransactionHash);

            List<Transaction> importedTransactions;

            try {
                importedTransactions = importAccount(account, lastTransactionHash);
            } catch (Exception e) {
                log.error("Failed to import transactions.", e);
                continue;
            }

            if (importedTransactions.size() > 0) {
                Transaction newestTransaction = importedTransactions.get(0);
                ImportHistoryUtil.setLastTransaction(importHistory, account, newestTransaction);
            }
        }

        return importHistory;
    }

    public List<Transaction> importAccount(Account account) throws IOException, SwedbankServerException, SwedbankUnauthorizedException, SwedbankAuthenticationException, SwedbankUserException {
        return importAccount(account, null);
    }

    public List<Transaction> importAccount(Account account, Integer stopAtTransactionHash) throws IOException, SwedbankServerException, SwedbankUnauthorizedException, SwedbankAuthenticationException, SwedbankUserException {
        log.info("Importing account: {}.", account);
        Assert.notNull(account, "Can't import account. Account object is null.");
        Assert.notEmpty(account.getName(), "Account name cannot be empty.");
        Assert.notEmpty(account.getId(), "Account id cannot be empty.");
        Assert.notFalse(swedbank.isLoggedIn(), "You must be logged in to be able to import Swedbank account.");

        String sheetName = account.getName();
        if (!GoogleSpreadsheet.existsSheetName(spreadsheetId, sheetName)) {
            log.debug("No spreadsheet found with the name {} on spreadsheet with id {}. Creating a new sheet.", sheetName, spreadsheetId);
            GoogleSpreadsheet.createSheet(spreadsheetId, sheetName);
            createSheetTemplate(sheetName);
        } else {
            log.debug("Existing sheet found with the name {}.", sheetName);
        }

        int page = 0;
        AccountDetails accountDetails;
        List<Transaction> importedTransactions = new ArrayList();

        do {
            accountDetails = swedbank.accountDetails(account, 100, page);
            importAccountDetails(sheetName, account);
            List<Transaction> transactions = TransactionUtil.filterTransactions(accountDetails.getTransactions(), stopAtTransactionHash);
            importTransactions(sheetName, accountDetails.getAccount(), transactions);
            importedTransactions.addAll(transactions);
            page++;
        } while(accountDetails != null && accountDetails.isMoreTransactionsAvailable() && !TransactionUtil.containsTransactionHash(accountDetails.getTransactions(), stopAtTransactionHash));

        log.info("Account import finished.");
        return importedTransactions;
    }

    public void importAccountDetails(String sheetName, Account account) {
        try {
            Table table = new Table(1, 0);
            table.newRow()
                    .newColumn(account.getName())
                    .newColumn(account.getBalance())
                    .newColumn(account.getFullyFormattedNumber());

            GoogleSpreadsheet.update(spreadsheetId, sheetName, table);
        } catch (IOException e) {
            throw new RuntimeException("Failed to import account details.", e);
        }
    }

    public void createSheetTemplate(String sheetName) {
        try {
            Table table = new Table();
            table.newRow()
                    .newColumn("Name")
                    .newColumn("Balance")
                    .newColumn("Account Number");
            table.newRow();
            table.newRow();
            table.newRow()
                    .newColumn("Date")
                    .newColumn("Account Number")
                    .newColumn("Name")
                    .newColumn("Description")
                    .newColumn("Amount")
                    .newColumn("Account Balance")
                    .newColumn("Category Names")
                    .newColumn("Category Groups")
                    .newColumn("Transaction Id");

            GoogleSpreadsheet.update(spreadsheetId, sheetName, table);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create sheet template.", e);
        }
    }

    public void importTransactions(String sheetName, Account account, List<Transaction> transactions) throws IOException {
        Table table = TransactionConverter.convert(account, transactions);
        table.setRowOffset(4);
        GoogleSpreadsheet.append(spreadsheetId, sheetName, table);
    }

}
