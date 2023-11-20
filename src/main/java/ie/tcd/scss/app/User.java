package ie.tcd.scss.app;

public class User {

    private String username;
    private int encodedId;

    public User(String username) {
        this.username = username;
        this.encodedId = hashCode();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    public String getUserName() {
        return username;
    }
    
    public int getEncodedId() {
        return encodedId;
    }
}
