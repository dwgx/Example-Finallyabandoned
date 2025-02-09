package com.example.mod.datatypes;

import com.example.mod.enums.VelocityCorrection;
import com.example.mod.enums.cmd.CmdType;
import com.example.mod.managers.RotationManager;
import com.example.utils.AbstractCallbackImpl;

public class Rotation {
    private QAngle angle;
    private boolean trace;
    private CmdType cmdType;
    private VelocityCorrection velocityCorrection;
    private int priority;
    private AbstractCallbackImpl<Rotation> callback;

    private float step = 180f;

    private boolean finished = false;

    public Rotation(QAngle angle, boolean trace, int priority, CmdType cmdType, AbstractCallbackImpl<Rotation> callback) {
        this.angle = angle;
        this.trace = trace;
        this.priority = priority;
        this.cmdType = cmdType;
        this.velocityCorrection = VelocityCorrection.NONE;
        this.callback = callback;
    }

    public Rotation(QAngle angle, boolean trace, int priority, CmdType cmdType, VelocityCorrection velocityCorrection, AbstractCallbackImpl<Rotation> callback) {
        this.angle = angle;
        this.trace = trace;
        this.priority = priority;
        this.cmdType = cmdType;
        this.velocityCorrection = velocityCorrection;
        this.callback = callback;
    }

    public Rotation(float yaw, float pitch, boolean trace, int priority, CmdType cmdType, VelocityCorrection velocityCorrection, AbstractCallbackImpl<Rotation> callback) {
        this(new QAngle(yaw, pitch), trace, priority, cmdType, velocityCorrection, callback);
    }

    public Rotation(float yaw, float pitch, boolean trace, int priority, CmdType cmdType, VelocityCorrection velocityCorrection) {
        this(new QAngle(yaw, pitch), trace, priority, cmdType, velocityCorrection, null);
    }

    public Rotation(float yaw, float pitch, boolean trace, int priority, VelocityCorrection velocityCorrection) {
        this(new QAngle(yaw, pitch), trace, priority, CmdType.LOCAL, velocityCorrection, null);
    }

    public Rotation(QAngle angle, boolean trace, int priority, VelocityCorrection velocityCorrection) {
        this(angle, trace, priority, CmdType.LOCAL, velocityCorrection, null);
    }

    public Rotation(QAngle angle, boolean trace, int priority, CmdType cmdType) {
        this(angle, trace, priority, cmdType, VelocityCorrection.NONE, null);
    }

    public Rotation(QAngle angle, boolean trace, int priority) {
        this(angle, trace, priority, CmdType.LOCAL, VelocityCorrection.NONE, null);
    }

    public Rotation(QAngle angle, boolean trace, CmdType cmdType, VelocityCorrection velocityCorrection) {
        this(angle, trace, 0, cmdType, velocityCorrection, null);
    }

    public Rotation(QAngle angle, boolean trace, VelocityCorrection velocityCorrection) {
        this(angle, trace, 0, CmdType.LOCAL, velocityCorrection, null);
    }

    public Rotation(QAngle angle, boolean trace) {
        this(angle, trace, 0, CmdType.LOCAL, VelocityCorrection.NONE, null);
    }

    public Rotation(QAngle angle) {
        this(angle, false, 0, CmdType.LOCAL, VelocityCorrection.NONE, null);
    }

    public Rotation(float yaw, float pitch, boolean trace, VelocityCorrection velocityCorrection) {
        this(new QAngle(yaw, pitch), trace, 0, CmdType.LOCAL, velocityCorrection, null);
    }

    public Rotation(float yaw, float pitch, boolean trace) {
        this(yaw, pitch, trace, VelocityCorrection.NONE);
    }

    public Rotation(AbstractCallbackImpl<Rotation> callback) {
        this(new QAngle(), false, 0, CmdType.LOCAL, VelocityCorrection.NONE, callback);
    }

    public Rotation() {
        this(new QAngle(), false, 0, CmdType.LOCAL, VelocityCorrection.NONE, null);
    }

    public static Rotation create() {
        return new Rotation();
    }

    public QAngle getAngle() {
        return angle;
    }

    public Rotation setAngle(QAngle angle) {
        this.angle = angle;
        return this;
    }

    public float getYaw() {
        return angle.getYaw();
    }

    public Rotation setYaw(float yaw) {
        angle.setYaw(yaw);
        return this;
    }

    public float getPitch() {
        return angle.getPitch();
    }

    public Rotation setPitch(float pitch) {
        angle.setPitch(pitch);
        return this;
    }

    public boolean isTrace() {
        return trace;
    }

    public Rotation setTrace(boolean trace) {
        this.trace = trace;
        return this;
    }

    public CmdType getCmdType() {
        return cmdType;
    }

    public Rotation setCmdType(CmdType cmdType) {
        this.cmdType = cmdType;
        return this;
    }

    public VelocityCorrection getVelocityCorrection() {
        return velocityCorrection;
    }

    public Rotation setVelocityCorrection(VelocityCorrection velocityCorrection) {
        this.velocityCorrection = velocityCorrection;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public Rotation setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public AbstractCallbackImpl<Rotation> getCallback() {
        return callback;
    }

    public Rotation setCallback(AbstractCallbackImpl<Rotation> callback) {
        this.callback = callback;
        return this;
    }

    public float getStep() {
        return step;
    }

    public Rotation setStep(float step) {
        this.step = step;
        return this;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinish() {
        this.finished = true;
    }

    public void trigger() {
        if (callback != null) {
            try {
                callback.onExecute(this);
                callback.onComplete(this);
            } catch (Exception e) {
                callback.onFailure(this, e);
            }
        }
    }

    public boolean submit(boolean keep) {
        return RotationManager.getInstance().offer(this, keep);
    }

    public boolean submit() {
        return submit(false);
    }
}
