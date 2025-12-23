public abstract class AbstractUser {
    protected String username;

    public AbstractUser(String username) {
        this.username = username;
    }

    public abstract boolean canVote();
}