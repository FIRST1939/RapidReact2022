package frc.robot.subsystems;

import frc.robot.Constants;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;


import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Climber extends SubsystemBase{
    private final WPI_TalonFX leftFalcon;
    private final WPI_TalonFX rightFalcon;

    private final Solenoid leftPiston;
    private final Solenoid rightPiston;

    public Climber() {
        leftFalcon = new WPI_TalonFX(Constants.LEFT_CLIMB_CAN);
        rightFalcon = new WPI_TalonFX(Constants.RIGHT_CLIMB_CAN);

        leftPiston = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.LEFT_CLIMB_PCM);
        rightPiston = new Solenoid(PneumaticsModuleType.CTREPCM, Constants.RIGHT_CLIMB_PCM);

    }
}
