package com.example.mod.events.client.input;

import com.example.event.Event;
import com.example.mod.utils.input.PlayerInputModifiable;
import net.minecraft.util.PlayerInput;

public class MovementInputEvent extends Event {
    private PlayerInputModifiable input;

    public MovementInputEvent(PlayerInputModifiable input) {
        this.input = input;
    }

    public MovementInputEvent(PlayerInput input) {
        this(new PlayerInputModifiable(input));
    }

    public PlayerInputModifiable getInput() {
        return input;
    }

    public void setInput(PlayerInputModifiable input) {
        this.input = input;
    }

    public void setInput(PlayerInput input) {
        this.input = new PlayerInputModifiable(input);
    }
}
