package rlbotexample.skills;

import rlbot.Bot;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;

public abstract class Skill {

    public abstract ControlsOutput getOutput(DataPacket packet, Bot bot);
}
