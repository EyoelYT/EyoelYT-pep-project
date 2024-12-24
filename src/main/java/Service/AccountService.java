package Service;

import DAO.AccountDAO;
import Model.Account;
import Model.Message;

import java.util.List;

public class AccountService {

    public AccountDAO accountDAO;

    public AccountService() {
        accountDAO = new AccountDAO();
    }

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public List<Message> getAllMessagesByAccount(int account_id) {
        return accountDAO.getAllMessagesByAccount(account_id);
    }

    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account);
    }

    public Account getAccountByUsername(String username) {
        return accountDAO.getAccountByUsername(username);
    }

    public Account getMessageByAccount(int posted_by) {
        return accountDAO.getMessageByAccount(posted_by);
    }

    public Account getAccountById(int posted_by) {
        return accountDAO.getAccountById(posted_by);
    }

    public Account verifyAccountLogin(String username, String password) {
        return accountDAO.verifyAccountExists(username, password);
    }
}
