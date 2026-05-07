package project.models;

public class Hero {
    private final String name;
    private int wins;
    private int totalGames;
    private String adminComment;

    public Hero(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWins() {
        return wins;
    }

    public int getTotalGames() {
        return totalGames;
    }

    public String getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    public void updateStats(int wins, int losses) {
        this.wins = wins;
        this.totalGames = wins + losses;
    }

    public double getWinRate() {
        if (totalGames == 0) {
            return 0.0;
        }
        return (double) wins / totalGames;
    }
}
