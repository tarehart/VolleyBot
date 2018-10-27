package rlbotexample.skills;

import rlbot.Bot;
import rlbot.cppinterop.RLBotDll;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.Steering;
import rlbotexample.goals.Goal;
import rlbotexample.input.DataPacket;
import rlbotexample.intercept.BallPath;
import rlbotexample.intercept.Intercept;
import rlbotexample.intercept.InterceptCalculator;
import rlbotexample.output.ControlsOutput;
import rlbotexample.plan.Plan;
import rlbotexample.vector.Vector2;
import rlbotexample.vector.Vector3;

import java.awt.*;
import java.io.IOException;

public class Volley extends Skill {

    public static final double VOLLEY_HEIGHT = 500;
    private final double SECONDS_FOR_VOLLEY = 0.7;

    private Plan plan;


    @Override
    public ControlsOutput getOutput(DataPacket packet, Bot bot) {

        if (plan != null) {
            ControlsOutput planOutput = plan.getOutput(packet, bot);
            if (planOutput != null) {
                return planOutput;
            }
            return null;
        }

        try {
            BallPath ballPath = new BallPath(RLBotDll.getBallPrediction());
            if (packet.ball.position.z > VOLLEY_HEIGHT) {
                Intercept volleyPoint = InterceptCalculator.getVolleyPoint(ballPath, packet.car, VOLLEY_HEIGHT);
                if (volleyPoint == null) {
                    return null;
                }

                double secondsTillAppointment = volleyPoint.getGameTime() - packet.car.elapsedSeconds;

                if (secondsTillAppointment <= SECONDS_FOR_VOLLEY) {

                    plan = new Plan()
                            .withStep(new TimedAction(0.3, new ControlsOutput().withJump().withPitch(1)))
                            .withStep(new TimedAction(0.05, new ControlsOutput()))
                            .withStep(new TimedAction(0.3, new ControlsOutput().withJump()))
                            .withStep(new TimedAction(0.3, new ControlsOutput().withPitch(-1)))
                            .withStep(new TimedAction(0.1, new ControlsOutput()));

                }

                Vector2 goalToBall = packet.ball.position.flatten()
                        .minus(Goal.getEnemyGoal(packet.team).getCenter());

                Vector3 volleyTarget = volleyPoint.getPosition().plus(goalToBall.scaledToMagnitude(50).withZ(0));

                Renderer renderer = BotLoopRenderer.forBotLoop(bot);
                renderer.drawCenteredRectangle3d(Color.ORANGE, volleyTarget, 8, 8, true);

                return Steering.steerTowardAppointment(packet.car, volleyTarget, volleyPoint.getGameTime());
            }

            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
