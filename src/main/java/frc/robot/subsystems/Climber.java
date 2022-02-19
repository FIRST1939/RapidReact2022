package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Climber extends SubsystemBase{
    private final WPI_TalonFX leftFalcon;
    private final WPI_TalonFX rightFalcon;

    private final DoubleSolenoid climberPiston;

    private final DigitalInput climberHallEffect;

    public Climber() {
        leftFalcon = new WPI_TalonFX(Constants.climberFalconLeft);
        rightFalcon = new WPI_TalonFX(Constants.climberFalconRight);

        climberPiston = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.climberPistonForward, Constants.climberPistonReverse);

        climberHallEffect = new DigitalInput(Constants.climberHallEffect);

        rightFalcon.follow(leftFalcon);
        rightFalcon.setNeutralMode(NeutralMode.Brake);
        leftFalcon.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void periodic(){
    }

    public void setClimber(double value){
        leftFalcon.set(ControlMode.PercentOutput, value);
    }

    public void climberExtend()[
        climberPiston.set(DoubleSolenoid.Value.kForward);
    } 

    public void climberRetract(){
        climberPiston.set(DoubleSolenoid.Value.kReverse);
    }

    public void zeroEncoder(){
        leftFalcon.setSelectedSensorPosition(0);
        rightFalcon.setSelectedSensorPosition(0);
    }

    public boolean isClimberDown(){
        return climberHallEffect.get();
    }

    public double getClimberPosition(){
        return leftFalcon.getSelectedSensorPosition();
    }

}
