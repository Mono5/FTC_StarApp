package net.mxd.robotics.runmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Mecanum-Only", group = "Debug")
public class StandaloneMecanumDrive extends OpMode {
    //load the motors
    private DcMotor fl, fr, rl, rr;

    @Override
    public void init() {
        // initialize the motors with the configured names
        fl = hardwareMap.get(DcMotor.class, "m_frontLeft");
        fr = hardwareMap.get(DcMotor.class, "m_frontRight");
        rl = hardwareMap.get(DcMotor.class, "m_rearLeft");
        rr = hardwareMap.get(DcMotor.class, "m_rearRight");
        // set motor directions
        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        fl.setDirection(DcMotorSimple.Direction.FORWARD);
        rr.setDirection(DcMotorSimple.Direction.REVERSE);
        rl.setDirection(DcMotorSimple.Direction.FORWARD);
    }
    
    @Override
    public void loop() {
        double[] power = new double[4];
        // calculate the power for each motor for full mecanum drive
        power[0] = -gamepad1.left_stick_y + gamepad1.left_stick_x + gamepad1.right_stick_x;
        power[1] = -gamepad1.left_stick_y - gamepad1.left_stick_x - gamepad1.right_stick_x;
        power[2] = -gamepad1.left_stick_y - gamepad1.left_stick_x + gamepad1.right_stick_x;
        power[3] = -gamepad1.left_stick_y + gamepad1.left_stick_x - gamepad1.right_stick_x;
        // enforce power limits on each motor
        power[0] = Range.clip(power[0], -1, 1); power[1] = Range.clip(power[1], -1, 1);
        power[2] = Range.clip(power[2], -1, 1); power[3] = Range.clip(power[3], -1, 1);
        // apply the calculated power
        fl.setPower(power[0]); fr.setPower(power[1]);
        rl.setPower(power[2]); rr.setPower(power[3]);
    }
}
