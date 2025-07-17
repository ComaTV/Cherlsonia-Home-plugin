package org.homes.homes.utils;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class EconomyUtils {
    private static final String MONEY_OBJECTIVE = "money";

    public static int getMoney(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(MONEY_OBJECTIVE);
        if (obj == null) return 0;
        Score score = obj.getScore(player.getName());
        return score.isScoreSet() ? score.getScore() : 0;
    }

    public static boolean hasMoney(Player player, int amount) {
        return getMoney(player) >= amount;
    }

    public static void addMoney(Player player, int amount) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(MONEY_OBJECTIVE);
        if (obj == null) return;
        Score score = obj.getScore(player.getName());
        int current = score.isScoreSet() ? score.getScore() : 0;
        score.setScore(current + amount);
    }

    public static boolean removeMoney(Player player, int amount) {
        if (!hasMoney(player, amount)) return false;
        Scoreboard scoreboard = player.getScoreboard();
        Objective obj = scoreboard.getObjective(MONEY_OBJECTIVE);
        if (obj == null) return false;
        Score score = obj.getScore(player.getName());
        int current = score.isScoreSet() ? score.getScore() : 0;
        score.setScore(current - amount);
        return true;
    }
} 