package net.mxd.robotics.utils;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.text.DecimalFormat;

public class ManualCrane {

    // elevator, 0 is left, 1 is right
    private DcMotor[] motors = new DcMotor[2];
    // crane hand, 0 is rotation, 1 is grabber
    private Servo[] servos = new Servo[2];
    // crane extender
    private CRServo extender = null;

    // adding self-contained telemetry for all modules
    private Telemetry telemetry = null;

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
        servos[0].setPosition(0);
        servos[1].setPosition(0);

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
}
