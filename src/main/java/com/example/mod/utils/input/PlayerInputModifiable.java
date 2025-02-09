package com.example.mod.utils.input;

import net.minecraft.util.PlayerInput;

public class PlayerInputModifiable {
    private boolean forward, backward, left, right;
    private boolean jump, sneak;
    private boolean sprint;

    public PlayerInputModifiable(boolean forward, boolean backward, boolean left, boolean right, boolean jump, boolean sneak, boolean sprint) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.sneak = sneak;
        this.sprint = sprint;
    }

    public PlayerInputModifiable(PlayerInput input) {
        this(input.forward(), input.backward(), input.left(), input.right(), input.jump(), input.sneak(), input.sprint());
    }

    public PlayerInputModifiable() {
        this(PlayerInput.DEFAULT);
    }

    public void setDirection(float movementForward, float movementSideways) {
        this.forward = movementForward > 0.0;
        this.backward = movementForward < 0.0;
        this.left = movementSideways > 0.0;
        this.right = movementSideways < 0.0;
    }

    public boolean isForward() {
        return forward;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isBackward() {
        return backward;
    }

    public void setBackward(boolean backward) {
        this.backward = backward;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isJump() {
        return jump;
    }

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    public boolean isSneak() {
        return sneak;
    }

    public void setSneak(boolean sneak) {
        this.sneak = sneak;
    }

    public boolean isSprint() {
        return sprint;
    }

    public void setSprint(boolean sprint) {
        this.sprint = sprint;
    }

    public PlayerInput get() {
        return new PlayerInput(
                this.forward,
                this.backward,
                this.left,
                this.right,
                this.jump,
                this.sneak,
                this.sprint
        );
    }
}
