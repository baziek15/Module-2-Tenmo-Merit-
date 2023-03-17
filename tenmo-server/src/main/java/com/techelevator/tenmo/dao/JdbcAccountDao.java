package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;


@Repository
public class JdbcAccountDao implements AccountDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


@Override
    public BigDecimal getCurrentBalance(String name) {
    String sql = "SELECT balance from account JOIN tenmo_user ON account.user_id = tenmo_user.user_id WHERE username = ?";
    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, name);

    String  accountBalance="";

    if (results.next()) {
         accountBalance = results.getString("balance");
    }
    BigDecimal balance = new BigDecimal(accountBalance);
    return balance;

}

    @Override
    public Account getAccountByAccountId(int accountId){
        String sql = "SELECT user_id, account_id, balance FROM account WHERE account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        Account a = null;

       if (results.next()){
          a = mapRowToAccount(results);
        }
        return a;
    }

    @Override
    public Account getAccountByUserId(int userId){
        String sql = "SELECT user_id, account_id, balance FROM account WHERE user_id =?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        Account a = null;

        if (results.next()){
            a = mapRowToAccount(results);
        }
        return a;
    }

    @Override
    public void updateAccount(Account accountFrom){
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        jdbcTemplate.update(sql, accountFrom.getBalance(), accountFrom.getAccountId());


    }

    private Account mapRowToAccount(SqlRowSet result) {
        int accountId = result.getInt("account_id");
        int userAccountId = result.getInt("user_id");

        String accountBalance = result.getString("balance");
        return new Account(accountId, userAccountId, (new BigDecimal(accountBalance)));
    }
}
