package net.mxd.robotics.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.text.DecimalFormat;

public class ManualMecanum {

    // 0 is frontLeft, 1 is frontRight, 2 is rearLeft, 3 is rearRight
    private DcMotor[] motors = new DcMotor[4];

    // adding self-contained telemetry for all modules
    private Telemetry telemetry;

    // exposing initialization module for runMode
    public void init(HardwareMap hardwareMap, Telemetry oldTelemetry) {

        // copying over telemetry from the original runMode
        telemetry = oldTelemetry;

        // declaring each motor from configuration
        motors[0] = hardwareMap.get(DcMotor.class, "m_frontLeft");
        motors[1] = hardwareMap.get(DcMotor.class, "m_frontRight");
        motors[2] = hardwareMap.get(DcMotor.class, "m_rearLeft");
        motors[3] = hardwareMap.get(DcMotor.class, "m_rearRight");

        // setting motor directions
        motors[0].setDirection(DcMotorSimple.Direction.FORWARD);
        motors[1].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[2].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[3].setDirection(DcMotorSimple.Direction.FORWARD);

        // setting motor behavior at power level =0
        for (int i = 0; i < 4; i++)
            motors[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // setting motor run mode
        for (int i = 0; i < 4; i++) {
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }

        // update telemetry to confirm complete motor configuration to runMode and display
        telemetry.addLine().addData("Mecanum Status", "Ready for use");
    }

    // add debug module, just in case
    public void debug() {

        // power formatting for clean output
        DecimalFormat df = new DecimalFormat("#.##");

        // telemetry data output for motors
        telemetry.addLine().addData("Motor Status", "");
        telemetry.addLine()
                .addData("FL Power", df.format(motors[0].getPower()))
                .addData("FL Position", motors[0].getCurrentPosition());
        telemetry.addLine()
                .addData("FR Power", df.format(motors[1].getPower()))
                .addData("FR Position", motors[1].getCurrentPosition());
        telemetry.addLine()
                .addData("RL Power", df.format(motors[2].getPower()))
                .addData("RL Position", motors[2].getCurrentPosition());
        telemetry.addLine()
                .addData("RR Power", df.format(motors[3].getPower()))
                .addData("RR Position", motors[3].getCurrentPosition());
    }

    // digital control for motors
    public void digitalControl(Gamepad gamepad, double power) {

        // power checks to enforce limits
        if (power < 0) power = -power;
        if (power > 1) power = 1;

        // TODO: work out a method to use both rotation and directions at the same time, needs analogControl debugging
        // rotation takes priority
        if (gamepad.a) {
            motors[0].setPower(-power); motors[1].setPower(power);
            motors[2].setPower(-power); motors[3].setPower(power);
        } else if (gamepad.b) {
            motors[0].setPower(power); motors[1].setPower(-power);
            motors[2].setPower(power); motors[3].setPower(-power);
        } else if (gamepad.dpad_right || gamepad.dpad_left || gamepad.dpad_down || gamepad.dpad_up){
            // use gamePad dpad for motor power-up
            if (gamepad.dpad_up) {
                motors[0].setPower(power); motors[1].setPower(power);
                motors[2].setPower(power); motors[3].setPower(power);
            } else if (gamepad.dpad_down) {
                motors[0].setPower(-power); motors[1].setPower(-power);
                motors[2].setPower(-power); motors[3].setPower(-power);
            }
            if (gamepad.dpad_left) {
                motors[0].setPower(power); motors[1].setPower(-power);
                motors[2].setPower(-power); motors[3].setPower(power);
            } else if (gamepad.dpad_right) {
                motors[0].setPower(-power); motors[1].setPower(power);
                motors[2].setPower(power); motors[3].setPower(-power);
            }
        } else {
            motors[0].setPower(0); motors[1].setPower(0);
            motors[2].setPower(0); motors[3].setPower(0);
        }
    }

    // analog (full-range) control for motors
    public void analogControl(double x, double y, double rotation) {

        // get a power array up & running
        double[] power = new double[4];

        // calculate power for each motor
        power[0] = y - x + rotation; power[1] = y + x - rotation;
        power[2] = y + x + rotation; power[3] = y - x - rotation;

        // enforce power limits
        power[0] = Range.clip(power[0], -1, 1); power[1] = Range.clip(power[1], -1, 1);
        power[2] = Range.clip(power[2], -1, 1); power[3] = Range.clip(power[3], -1, 1);

        // update the motor power
        motors[0].setPower(power[0]); motors[1].setPower(power[1]);
        motors[2].setPower(power[2]); motors[3].setPower(power[3]);
    }
}
