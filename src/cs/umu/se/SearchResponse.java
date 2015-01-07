package cs.umu.se;

/**
 * Created by oi11ejn on 2014-12-29.
 */
public class SearchResponse {
    private String userId;
    private String name;
    private String lastName;
    private String email;
    private String profilePicture;
    private String[] friendList;
    private String lastOnline;
    private boolean yourFriend;

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

    public String[] getFriendList() {
        return friendList;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public boolean isYourFriend() {
        return yourFriend;
    }

}
