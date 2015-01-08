package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2015-01-06.
 */
public class NewUser implements Serializable {
    String name;
    String lastName;
    String email;
    String userId;
    String password;

    public NewUser() {
    }

    public NewUser(String name, String lastName, String email, String userId, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.userId = userId;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }
}
