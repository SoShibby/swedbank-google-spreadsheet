package com.github.soshibby.types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Henrik on 04/02/2017.
 */
public class ImportHistory {

    private List<AccountImport> accountImports = new ArrayList();

    public List<AccountImport> getAccountImports() {
        return accountImports;
    }

    public void setAccountImports(List<AccountImport> accountImports) {
        this.accountImports = accountImports;
    }
}
