package Database;

import java.util.ArrayList;
import java.util.List;

import Util.PasswordEncryption;

/**
 * Created by Alexandre on 28/05/2015.
 */
public class LoginOnlineDB extends OnlineDB {

    public LoginOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("id");
        allArguments.add("username");
        allArguments.add("password");
        allArguments.add("email");
        allArguments.add("salt");
    }

    /* Tries to login returning true if success*/
    public boolean login(String username, String password) {
        boolean result = false;

        //using a different query format here to prevent SQLInjections
        String query = "SELECT * " +
                "FROM login " +
                "WHERE username = ?";
        List<List<String>> queryResult = database.loginQuery(query, this.allArguments, username);
        if(queryResult.size() == 1) {;
            LoginOnlineDBFormat formatLogin = format(queryResult).get(0);
            result = PasswordEncryption.isExpectedPassword(password, formatLogin.getSalt(), formatLogin.getPassword());
        }

        return result;
    }

    /*Creates a new account
     * Returns true if creation was successful
     */
    public boolean create(String username, String password, String email) {
        boolean result = false;

        String query = "INSERT INTO login(username, password, email, salt) " +
                "VALUES (?, ?, ?, ?)";

        //check that username and email are not taken

        if(isEmailUnique(email) && isUserNameUnique(username)) {
            int salt = PasswordEncryption.getNextSalt();
            String hash = PasswordEncryption.hash(password, salt);

            result = database.createLoginQuery(query, username, hash, email, salt);
        }

     return result;
    }

    /*
    Returns true if email not present in database
     */
    private boolean isEmailUnique(String email) {
        String queryUsername = "SELECT * " +
                "FROM login " +
                "WHERE email = ?";
        List<List<String>> queryResultEmail = database.loginQuery(queryUsername, this.allArguments, email);

        return queryResultEmail.size() == 0;
    }

    /*
    Returns true if username not rpesent is database
     */
    private boolean isUserNameUnique(String username) {
        String queryUsername = "SELECT * " +
                "FROM login " +
                "WHERE username = ?";
        List<List<String>> queryResultUsername = database.loginQuery(queryUsername, this.allArguments, username);

        return queryResultUsername.size() == 0;
    }

    private List<LoginOnlineDBFormat> format(List<List<String>> queryResult) {
        List<LoginOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            LoginOnlineDBFormat current = new LoginOnlineDBFormat(strings);
            result.add(current);
        }
        return result;
    }

}
