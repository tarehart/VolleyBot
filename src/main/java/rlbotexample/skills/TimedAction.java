package rlbotexample.skills;

import rlbot.Bot;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;

public class TimedAction extends Skill {

    private final double duration;
    private final ControlsOutput controls;

    private double endTime = -1;

    public TimedAction(double duration, ControlsOutput controls) {
        this.duration = duration;
        this.controls = controls;
    }

    @Override
    public ControlsOutput getOutput(DataPacket packet, Bot bot) {
        if (endTime < 0) {
            endTime = packet.car.elapsedSeconds + duration;
        }

        if (endTime < packet.car.elapsedSeconds) {
            return null;
        }

        return controls;
    }
}
