package rlbotexample.intercept;

import rlbot.flat.PredictionSlice;
import rlbotexample.input.CarData;
import rlbotexample.vector.Vector3;

public class InterceptCalculator {


    public static Intercept getIntercept(BallPath ballPath, CarData car) {


        for (int i = 0; i < ballPath.size(); i++) {

            PredictionSlice slice = ballPath.getSlice(i);
            Vector3 ballLocation = new Vector3(slice.physics().location());
            double ballTime = slice.gameSeconds();

            double secondsAvailable = ballTime - car.elapsedSeconds;

            double maxDistance = CarDistance.maxDistance(secondsAvailable, car, ballLocation);

            if (maxDistance >= ballLocation.distance(car.position)) {
                return new Intercept(ballTime, ballLocation);
            }
        }

        return null;
    }

    private static Vector3 sliceToPosition(PredictionSlice slice) {
        return new Vector3(slice.physics().location());
    }

    public static Intercept getVolleyPoint(BallPath ballPath, CarData car, double height) {

        Vector3 prev = sliceToPosition(ballPath.getSlice(0));

        for (int i = 1; i < ballPath.size(); i++) {
            PredictionSlice slice = ballPath.getSlice(i);
            Vector3 current = sliceToPosition(slice);

            if (prev.z > height && current.z < height) {

                double ballTime = slice.gameSeconds();

                double secondsAvailable = ballTime - car.elapsedSeconds;

                double maxDistance = CarDistance.maxDistance(secondsAvailable, car, current);

                if (maxDistance >= current.distance(car.position)) {
                    return new Intercept(ballTime, current);
                }
            }
        }

        return null;
    }


}
