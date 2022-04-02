// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.auto;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.SHOTS;
import frc.robot.commands.shooter.SetShot;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class OneBall extends SequentialCommandGroup {
  /** Creates a new OneBall. */
  public OneBall(final Shooter shooter, final Indexer indexer, final DriveTrain driveTrain) {
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(
      new SetShot(shooter, SHOTS.fenderHigh),
      new WaitCommand(2.0),
      new AutoModeShooter(1, indexer, shooter).withTimeout(3.0),
        // Do not drive until second shot has cleared shooter.
      new WaitCommand(1.0),
        // Exit tarmac.
      new DriveStraightDistance(AutoConstants.FROM_FENDER_TO_EXIT_TARMAC_DIST, driveTrain, 0.5).withTimeout(4.0)
    );
  }
}
