// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Limelight;
import frc.robot.Constants.SHOTS;
import frc.robot.commands.ManualMoveAndTurnToTarget;
import frc.robot.commands.ManualMoveToTarget;
import frc.robot.commands.ManualTurnToTarget;
import frc.robot.commands.RumbleController;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class OneBall extends SequentialCommandGroup {
  /** Creates a new OneBall. */
  public OneBall(final Shooter shooter, final Indexer indexer, final DriveTrain driveTrain, final Limelight limelight, final RumbleController rumbleController) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new SetShot(shooter, SHOTS.cargoRing),
      new DriveStraightDistance(-47, driveTrain, 0.5),
      new WaitCommand(0.5),
      //new ManualTurnToTarget(driveTrain, limelight, 0, rumbleController),
      //new WaitCommand(0.5),
      new ManualMoveAndTurnToTarget(driveTrain, limelight, 0, rumbleController),
      new WaitCommand(1.0),
      new AutoModeShooter(1, indexer, shooter).withTimeout(3.0),
        // Do not drive until second shot has cleared shooter.
      new WaitCommand(0.5),
        // Exit tarmac.
      new DriveStraightDistance(-5, driveTrain, 0.5).withTimeout(4.0)
    );
  }
}
