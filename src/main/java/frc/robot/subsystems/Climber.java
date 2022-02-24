package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Climber extends SubsystemBase{
    private final WPI_TalonFX climberFalcon;

    private final DoubleSolenoid climberPiston;

    private final DigitalInput climberHallEffect;

    public Climber() {
        climberFalcon = new WPI_TalonFX(Constants.climberFalcon);

        climberPiston = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, Constants.climberPistonForward, Constants.climberPistonReverse);

        climberHallEffect = new DigitalInput(Constants.climberHallEffect);

        climberFalcon.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void periodic(){
    }

    public void setClimber(double value){
        climberFalcon.set(ControlMode.PercentOutput, value);
    }

    public void climberPistonExtend(){
        climberPiston.set(DoubleSolenoid.Value.kForward);
    } 

    public void climberPistonRetract(){
        climberPiston.set(DoubleSolenoid.Value.kReverse);
    }

    public void zeroEncoder(){
        climberFalcon.setSelectedSensorPosition(0);
    }

    public boolean isClimberDown(){
        return climberHallEffect.get();
    }

    public double getClimberPosition(){
        return climberFalcon.getSelectedSensorPosition();
    }

}
