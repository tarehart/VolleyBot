package rlbotexample.plan;

import rlbot.Bot;
import rlbotexample.input.DataPacket;
import rlbotexample.output.ControlsOutput;
import rlbotexample.skills.Skill;

import java.util.ArrayList;
import java.util.List;

public class Plan {

    private final List<Skill> steps = new ArrayList<>();
    private int index;

    public Plan() {
    }

    public Plan withStep(Skill skill) {
        this.steps.add(skill);
        return this;
    }

    public ControlsOutput getOutput(DataPacket packet, Bot bot) {

        while (index < steps.size()) {
            Skill currentSkill = steps.get(index);
            ControlsOutput output = currentSkill.getOutput(packet, bot);

            if (output != null) {
                return output;
            }
            index++;
        }

        return null;
    }
}
