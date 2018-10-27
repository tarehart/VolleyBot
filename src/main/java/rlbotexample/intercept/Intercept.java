package rlbotexample.intercept;

import rlbotexample.vector.Vector3;

public class Intercept {

    private final double gameTime;
    private final Vector3 position;

    public Intercept(double gameTime, Vector3 position) {
        this.gameTime = gameTime;
        this.position = position;
    }

    public double getGameTime() {
        return gameTime;
    }

    public Vector3 getPosition() {
        return position;
    }
}
