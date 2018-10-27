package rlbotexample.goals;

import rlbotexample.vector.Vector2;

public class Goal {

    private final int team;

    public Goal(int team) {
        this.team = team;
    }

    public static Goal getDefending(int myTeam) {
        return new Goal(myTeam);
    }

    public static Goal getEnemyGoal(int myTeam) {
        return new Goal(1 - myTeam);
    }


    public Vector2 getCenter() {
        if (team == 0) {
            return new Vector2(0, -5120);
        }
        return new Vector2(0, 5120);
    }


}
