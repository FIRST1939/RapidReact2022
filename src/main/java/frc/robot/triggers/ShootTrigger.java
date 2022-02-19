// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.triggers;

import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;

import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.subsystems.Indexer;
import frc.robot.subsystems.Shooter;


/** Add your docs here. */
public class ShootTrigger extends Trigger {
  private final Indexer indexer;
  private final Shooter shooter;
  private final Button button;
  
  public ShootTrigger(Indexer indexer, Shooter shooter, Button button) {
    this.indexer = indexer;
    this.shooter = shooter;
    this.button =  button;
  }
  @Override
  public boolean get() {
    return indexer.isCargoAtSensor() && shooter.isShooterReady() && button.get();
  }
}
