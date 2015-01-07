package cs.umu.se;

import java.io.Serializable;

/**
 * Created by oi11ejn on 2014-12-30.
 */
public class UserInfo implements Serializable {

    private String userId;
    private String name;
    private String lastName;
    private String email;
    private String profilePicture;
    private UserId[] friendList;
    private String lastOnline;
    private boolean yourFriend;

    public UserInfo() {

    }

    public UserInfo(String userId, String name, String lastName, String email) {
        this.userId = userId;
        this.name = name;
        this.lastName = name;
        this.email = email;
    }

    public String getUserId() {
        return userId;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public UserId[] getFriendList() {
        return friendList;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public boolean isYourFriend() {
        return yourFriend;
    }
}
