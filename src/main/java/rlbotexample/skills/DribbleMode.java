package rlbotexample.skills;

import rlbot.Bot;
import rlbot.cppinterop.RLBotDll;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.Steering;
import rlbotexample.goals.Goal;
import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.intercept.BallPath;
import rlbotexample.intercept.Intercept;
import rlbotexample.intercept.InterceptCalculator;
import rlbotexample.output.ControlsOutput;
import rlbotexample.vector.Vector2;
import rlbotexample.vector.Vector3;

import java.awt.*;
import java.io.IOException;

public class DribbleMode extends Skill {


    public static final int BASIS_SIZE = 250;

    @Override
    public ControlsOutput getOutput(DataPacket packet, Bot bot) {

        Renderer renderer = BotLoopRenderer.forBotLoop(bot);

        BallPath ballPath;
        try {
            ballPath = new BallPath(RLBotDll.getBallPrediction());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        CarData car = packet.car;
        Intercept intercept = InterceptCalculator.getIntercept(ballPath, car);

        if (intercept == null) {
            System.out.println("No intercept for dribble!");
            return null;
        }

//        if (packet.ball.velocity.magnitude() < 500) {
//
//
//            return Steering.steerTowardPosition(car, intercept.getPosition());
//        }

        renderer.drawCenteredRectangle3d(Color.CYAN, intercept.getPosition(), 4, 4, true);



        renderer.drawLine3d(Color.YELLOW, car.position, packet.ball.position);

        renderer.drawLine3d(Color.GREEN, car.position, intercept.getPosition());

        Goal enemyGoal = Goal.getEnemyGoal(packet.team);

        Vector2 ballToGoal = enemyGoal.getCenter().minus(packet.ball.position.flatten());

        double ballCorrectionRadians = packet.ball.velocity.flatten().correctionAngle(ballToGoal);

        boolean approachFromLeft = ballCorrectionRadians < 0;

        if (approachFromLeft) {
            renderer.drawString2d("approach from left", Color.white, new Point(2, 2), 2, 2);
        }

        Vector2 carToIntercept = intercept.getPosition().minus(car.position).flatten();

        double magnitude = Math.min(carToIntercept.magnitude() / 5, BASIS_SIZE);
        Vector2 approachBasis = carToIntercept.orthogonal(approachFromLeft).scaledToMagnitude(magnitude);
        Vector3 basisTip = intercept.getPosition().plus(approachBasis.withZ(0));

        renderer.drawLine3d(Color.ORANGE, intercept.getPosition(), basisTip);

        Vector3 steerTarget = basisTip;
//        Vector3 steerTarget = basisTip.minus(carToIntercept.scaledToMagnitude(100).withZ(0));

        renderer.drawCenteredRectangle3d(Color.RED, steerTarget, 8, 8, true);
        return Steering.steerTowardAppointment(car, steerTarget, intercept.getGameTime());

    }
}
