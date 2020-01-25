package net.mxd.robotics.utils;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.text.DecimalFormat;

public class ManualCrane {

    // elevator, 0 is left, 1 is right
    private DcMotor[] motors = new DcMotor[2];
    // crane hand, 0 is rotation, 1 is grabber
    private Servo[] servos = new Servo[2];
    // crane extender
    private CRServo extender = null;

    ElapsedTime timer = new ElapsedTime();
    // adding self-contained telemetry for all modules
    private Telemetry telemetry = null;

    // externally saved checks
    private boolean[] craneCheck = new boolean[4];

    // exposing initialization module for runMode
    public void init(HardwareMap hardwareMap, Telemetry oldTelemetry) {

        // copying over telemetry from the original runMode
        telemetry = oldTelemetry;

        // declaring hardware from configuration
        motors[0] = hardwareMap.get(DcMotor.class, "m_left");
        motors[1] = hardwareMap.get(DcMotor.class, "m_right");
        servos[0] = hardwareMap.get(Servo.class, "s_rotate");
        servos[1] = hardwareMap.get(Servo.class, "s_grabber");
        extender = hardwareMap.get(CRServo.class, "crs_extender");

        // setting motor/servo directions
        motors[0].setDirection(DcMotorSimple.Direction.REVERSE);
        motors[1].setDirection(DcMotorSimple.Direction.FORWARD);
        extender.setDirection(DcMotorSimple.Direction.FORWARD);
        servos[0].setDirection(Servo.Direction.FORWARD);
        servos[1].setDirection(Servo.Direction.FORWARD);

        // setting motor behavior at power =0
        motors[0].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        motors[1].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // setting motor run mode
        for (int i = 0; i < 2; i++) {
            motors[i].setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motors[i].setMode(DcMotor.RunMode.RUN_TO_POSITION);
        }

        // TODO: determine servo start positions
        // setting servo start position
        servos[0].setPosition(0.92); craneCheck[0] = false; craneCheck[2] = true;
        servos[1].setPosition(0.84); craneCheck[1] = false; craneCheck[3] = false;
        extender.setPower(0);

        timer.reset();

        // update telemetry to confirm configuration to runMode and display
        telemetry.addLine().addData("Crane Status", "Ready for use");
    }

    // add debug module, just in case
    public void debug() {

        // power formatting for clean output
        DecimalFormat df = new DecimalFormat("#.##");

        // telemetry data output for crane hardware
        telemetry.addLine().addData("Crane Status", "");
        telemetry.addLine()
                .addData("L Power", df.format(motors[0].getPower()))
                .addData("L Position", motors[0].getCurrentPosition());
        telemetry.addLine()
                .addData("R Power", df.format(motors[1].getPower()))
                .addData("R Position", motors[1].getCurrentPosition());
        telemetry.addLine()
                .addData("Grabber", df.format(servos[1].getPosition()))
                .addData("Rotation", df.format(servos[0].getPosition()));
    }

    //
    public void craneHeightControl(double power, boolean trigger, boolean pwrTrigger) {

        // get a power array up
        int[] powerI = new int[2];

        // calculate position updates
        powerI[0] = (int)(100 * power);
        powerI[1] = (int)(-100 * power);

        if (timer.seconds() > 0.2 && pwrTrigger) {
            if (motors[0].getPower() > 0.5) {
                motors[0].setPower(0);
                motors[1].setPower(0);
            } else {
                motors[0].setPower(0.8);
                motors[1].setPower(0.8);
            }
            timer.reset();
        }

        // TODO: Get values for maximum and minimum height
        // check direction
        if (trigger) {
            // check limits
            if (motors[0].getCurrentPosition() + powerI[0] < 999999 && motors[1].getCurrentPosition() + powerI[1] > -999999) {
                motors[0].setTargetPosition(motors[0].getCurrentPosition() + powerI[0]);
                motors[1].setTargetPosition(motors[1].getCurrentPosition() + powerI[1]);
            }
        } else {
            if (motors[0].getCurrentPosition() - powerI[0] > -999999 && motors[1].getCurrentPosition() - powerI[1] < 999999) {
                motors[0].setTargetPosition(motors[0].getCurrentPosition() - powerI[0]);
                motors[1].setTargetPosition(motors[1].getCurrentPosition() - powerI[1]);
            }
        }
    }

    // TODO: Get servo positions
    public void clawSwitches(boolean rotate, boolean grab) {

        // checks to not spam the servo changes
        if (timer.seconds() > 0.2 && rotate) {
            if (!craneCheck[3]) {
                servos[0].setPosition(0.92);
                craneCheck[3] = true;
            } else {
                servos[0].setPosition(0.04);
                craneCheck[3] = false;
            }
            timer.reset();
        }

        if (timer.seconds() > 0.2 && grab) {
            if (craneCheck[2]) {
                servos[1].setPosition(0.43); craneCheck[2] = false;
            }
            else {
                servos[1].setPosition(0.84); craneCheck[2] = true;
            }
            timer.reset();
        }
    }

    public void extenderCTRL(double power, boolean direction) {
        if (direction) extender.setPower(Range.clip(power, 0, 1));
        else extender.setPower(-Range.clip(power, 0, 1));
    }
}
