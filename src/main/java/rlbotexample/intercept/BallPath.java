package rlbotexample.intercept;

import rlbot.flat.BallPrediction;
import rlbot.flat.PredictionSlice;

public class BallPath {

    private final BallPrediction ballPrediction;

    public BallPath(BallPrediction ballPrediction) {
        this.ballPrediction = ballPrediction;
    }

    public PredictionSlice getSlice(int index) {
        return ballPrediction.slices(index);
    }

    public int size() {
        return ballPrediction.slicesLength();
    }
}
