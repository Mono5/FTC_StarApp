package net.mxd.robotics.runmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.mxd.robotics.utils.ManualMecanum;

@TeleOp(name = "Mecanum Analog Debug Drive", group = "Debug")
public class MecanumAnalogDebugDrive extends OpMode {

    // get the mecanum interface up
    private ManualMecanum wheels = null;

    @Override
    public void init() {

        // initialize the mecanum interface
        wheels.init(hardwareMap, telemetry);
        telemetry.update();
    }

    @Override
    public void init_loop() {}
    @Override
    public void start() {}
    @Override
    public void loop() {
        wheels.analogControl(-gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
        wheels.debug();
        telemetry.update();
    }
    @ Override
    public void stop() {}
}
