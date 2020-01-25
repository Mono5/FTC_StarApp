package net.mxd.robotics.runmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import net.mxd.robotics.utils.ManualCrane;
import net.mxd.robotics.utils.ManualMecanum;

@TeleOp(name = "Debug Drive", group = "Debug")
public class DebugDrive extends OpMode {

    // get the mecanum interface up
    private ManualMecanum wheels = new ManualMecanum();
    private ManualCrane crane = new ManualCrane();

    @Override
    public void init() {

        // initialize the mecanum interface
        wheels.init(hardwareMap, telemetry);
        crane.init(hardwareMap, telemetry);
        telemetry.update();
    }

    @Override
    public void init_loop() {}
    @Override
    public void start() {}
    @Override
    public void loop() {

        // send continuous power updates to analog input
        wheels.analogControl(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);
        crane.clawSwitches(gamepad1.b, gamepad1.a);
        crane.craneHeightControl(gamepad1.right_trigger, gamepad1.right_bumper, gamepad1.y);
        crane.extenderCTRL(gamepad1.left_trigger, gamepad1.left_bumper);

        wheels.debug();
        crane.debug();

        // update the debug display
        telemetry.update();
    }
    @ Override
    public void stop() {}
}
