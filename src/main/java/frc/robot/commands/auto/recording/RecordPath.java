package frc.robot.commands.auto.recording;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class RecordPath extends CommandBase {

    private final DriveTrain driveTrain;
    private final Timer timer = new Timer();
    private final double timeStep = .1;

    private ArrayList<Double> leftSteps = new ArrayList<Double>();
    private ArrayList<Double> rightSteps = new ArrayList<Double>();
    
    public RecordPath (final DriveTrain driveTrain) {

        this.driveTrain = driveTrain;
        addRequirements(this.driveTrain);
    }

    @Override
    public void initialize () {

        this.driveTrain.coastMode(true);
        this.driveTrain.resetDistance();
        this.timer.reset();
        this.timer.start();

        System.out.println("Recording current path.");
    }

    @Override
    public void execute () {

        double time = this.timer.get();

        if (time >= this.timeStep) {

            this.leftSteps.add(this.driveTrain.getLeftEncoderClicks());
            this.rightSteps.add(this.driveTrain.getRightEncoderClicks());

            this.driveTrain.resetDistance();
            this.timer.reset();
        }
    }

    @Override
    public void end (boolean interrupted) {

        this.leftSteps.add(0.0);
        this.rightSteps.add(0.0);
        this.driveTrain.coastMode(false);

        try {

            String basePath = new File("").getAbsolutePath();
            String filePath = basePath.concat("commands/auto/recording/Recordings.json");

            ObjectMapper ObjectMapper = new ObjectMapper();
            Map<String, ArrayList<Map<String, ?>>> jsonData = ObjectMapper.readValue(Paths.get(filePath).toFile(), new TypeReference<Map<String, ArrayList<Map<String, ?>>>>(){});
            ArrayList<Map<String, ?>> recordings = jsonData.get("recordings");

            Map<String, Object> recording = new HashMap<>();
            recording.put("name", SmartDashboard.getString("New Recording's Name", "New Recording"));
            recording.put("leftSteps", this.leftSteps);
            recording.put("rightSteps", this.rightSteps);

            recordings.add(recording);
            jsonData.put("recordings", recordings);

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(jsonData.toString());
            bufferedWriter.close();
        } catch (Exception exception) {

            exception.printStackTrace();
            return;
        }

        System.out.println("Recording saved as: " + SmartDashboard.getString("New Recording's Name", "New Recording"));
    }
    
    @Override
    public boolean isFinished () {

        return false;
    }

    @Override
    public boolean runsWhenDisabled () {

        return true;
    }
}
