package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2015-01-02.
 */
public class Authentication implements Serializable {
    private String auth;

    public Authentication(String auth) {
        this.auth = auth;
    }

    public String getAuth() {
        return auth;
    }

}
