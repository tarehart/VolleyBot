package rlbotexample;

import rlbot.Bot;
import rlbot.ControllerState;
import rlbot.cppinterop.RLBotDll;
import rlbot.flat.GameTickPacket;
import rlbot.flat.QuickChatSelection;
import rlbot.manager.BotLoopRenderer;
import rlbot.render.Renderer;
import rlbotexample.boost.BoostManager;
import rlbotexample.input.CarData;
import rlbotexample.input.DataPacket;
import rlbotexample.intercept.BallPath;
import rlbotexample.intercept.CarDistance;
import rlbotexample.intercept.Intercept;
import rlbotexample.intercept.InterceptCalculator;
import rlbotexample.output.ControlsOutput;
import rlbotexample.plan.Plan;
import rlbotexample.skills.DribbleMode;
import rlbotexample.skills.TimedAction;
import rlbotexample.skills.Volley;
import rlbotexample.vector.Vector2;
import rlbotexample.vector.Vector3;

import java.awt.*;
import java.io.IOException;

import static rlbotexample.skills.Volley.VOLLEY_HEIGHT;

public class VolleyBot implements Bot {

    private static final float BALL_RADIUS = 92.75F;

    private final int playerIndex;

    private Plan plan;

    public VolleyBot(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    /**
     * This is where we keep the actual bot logic. This function shows how to chase the ball.
     * Modify it to make your bot smarter!
     */
    private ControlsOutput processInput(DataPacket input) throws IOException {





        if (plan != null) {
            ControlsOutput planOutput = plan.getOutput(input, this);
            if (planOutput == null) {
                plan = null;
            } else {
                return planOutput;
            }
        }

        boolean isKickoff = input.ball.velocity.flatten().magnitude() < 1;

        if (isKickoff) {

            if (input.car.position.magnitude() < 1100) {
                plan = new Plan()
                        .withStep(new TimedAction(0.1, new ControlsOutput().withJump()))
                        .withStep(new TimedAction(0.05, new ControlsOutput()))
                        .withStep(new TimedAction(0.05, new ControlsOutput().withJump().withPitch(-1)));
            }

            return Steering.steerTowardPosition(input.car, new Vector3())
                    .withBoost();
        }

        Vector3 ballPosition = input.ball.position;
        boolean canPopBall = ballPosition.z > BALL_RADIUS * 1.5 &&
                ballPosition.z < BALL_RADIUS * 3 &&
                ballPosition.flatten().distance(input.car.position.flatten()) < 50;

        Renderer renderer = BotLoopRenderer.forBotLoop(this);

        if (canPopBall) {
            renderer.drawString2d("Can pop!", Color.white, new Point(10, 200), 2, 2);
            plan = new Plan()
                    .withStep(new TimedAction(0.2, new ControlsOutput().withJump()))
                    .withStep(new TimedAction(0.05, new ControlsOutput()))
                    .withStep(new TimedAction(0.05, new ControlsOutput().withJump().withPitch(1)));
            return plan.getOutput(input, this);
        }


        try {
            BallPath ballPath = new BallPath(RLBotDll.getBallPrediction());

            if (input.ball.position.z > VOLLEY_HEIGHT) {
                Intercept volleyPoint = InterceptCalculator.getVolleyPoint(ballPath, input.car, VOLLEY_HEIGHT);

                if (volleyPoint != null) {
                    renderer.drawString2d("Volley available!", Color.white, new Point(10, 200), 2, 2);

                    plan = new Plan().withStep(new Volley());
                    return plan.getOutput(input, this);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }



        // final double maxDistanceOfEnemy = CarDistance.maxDistance(2.0, input.enemyCar, new Vector3());

        // renderer.drawString2d("enemyMaxDistance: " + maxDistanceOfEnemy, Color.white, new Point(20, 20), 2, 2);

        DribbleMode dribbleMode = new DribbleMode();
        return dribbleMode.getOutput(input, this);
//        return new ControlsOutput().withThrottle(1.0F);
    }

    /**
     * This is a nice example of using the rendering feature.
     */
    private void drawDebugLines(DataPacket input, CarData myCar, boolean goLeft) {
        // Here's an example of rendering debug data on the screen.
        Renderer renderer = BotLoopRenderer.forBotLoop(this);

        // Draw a line from the car to the ball
        renderer.drawLine3d(Color.LIGHT_GRAY, myCar.position, input.ball.position);

        // Draw a line that points out from the nose of the car.
        renderer.drawLine3d(goLeft ? Color.BLUE : Color.RED,
                myCar.position.plus(myCar.orientation.noseVector.scaled(150)),
                myCar.position.plus(myCar.orientation.noseVector.scaled(300)));

        renderer.drawString3d(goLeft ? "left" : "right", Color.WHITE, myCar.position, 2, 2);
    }


    @Override
    public int getIndex() {
        return this.playerIndex;
    }

    /**
     * This is the most important function. It will automatically get called by the framework with fresh data
     * every frame. Respond with appropriate controls!
     */
    @Override
    public ControllerState processInput(GameTickPacket packet) {

        if (packet.playersLength() <= playerIndex || packet.ball() == null || !packet.gameInfo().isRoundActive()) {
            // Just return immediately if something looks wrong with the data. This helps us avoid stack traces.
            return new ControlsOutput();
        }

        // Update the boost manager with the latest data
        BoostManager.loadGameTickPacket(packet);

        // Translate the raw packet data (which is in an unpleasant format) into our custom DataPacket class.
        // The DataPacket might not include everything from GameTickPacket, so improve it if you need to!
        DataPacket dataPacket = new DataPacket(packet, playerIndex);

        // Do the actual logic using our dataPacket.

        try {
            return processInput(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
            return new ControlsOutput();
        }
    }

    public void retire() {
        System.out.println("Retiring sample bot " + playerIndex);
    }
}
