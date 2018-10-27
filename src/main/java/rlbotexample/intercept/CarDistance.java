package rlbotexample.intercept;

import rlbotexample.input.CarData;
import rlbotexample.vector.Vector3;

public class CarDistance {


    private static final double REGULAR_SPEED = 1400;
    private static final double MAX_SPEED = 2300;
    private static final double ACCELERATION = 1000;

    public static double maxDistance(double seconds, CarData car, Vector3 targetLocation) {
        final double currentSpeed = car.velocity.magnitude();
        final double velocityTillMaxSpeed = REGULAR_SPEED - currentSpeed;
        final double secondsTillMaxSpeed = velocityTillMaxSpeed / ACCELERATION;

        final double secondsForFirstPart = Math.min(seconds, secondsTillMaxSpeed);

        final double distanceDuringAccel = currentSpeed * secondsForFirstPart + 0.5 * ACCELERATION * secondsForFirstPart * secondsForFirstPart;

        if (seconds < secondsTillMaxSpeed) {
            return distanceDuringAccel;
        }


        final double secondsForSecondPart = seconds - secondsTillMaxSpeed;

        final double distanceDuringCruise = REGULAR_SPEED * secondsForSecondPart;

        return distanceDuringAccel + distanceDuringCruise;
    }

}
