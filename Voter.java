public class Voter extends AbstractUser {

    private boolean hasVoted;

    public Voter(String username) {
        super(username);
        this.hasVoted = false;
    }

    @Override
    public boolean canVote() {
        return !hasVoted;
    }

    public void setVoted() {
        this.hasVoted = true;
    }
}