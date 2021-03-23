package me.imadenigma.gangsplugin.user;

public enum Rank {

    MEMBER(1),
    MODERATOR(2),
    CoLeader(3),
    OWNER(4);

    private final int level;
    Rank(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
