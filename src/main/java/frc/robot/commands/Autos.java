// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.LedSubsystem;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public final class Autos {
  /** Example static factory for an autonomous command. */
  public static Command exampleAuto(ExampleSubsystem subsystem) {
    return Commands.sequence(subsystem.exampleMethodCommand(), new ExampleCommand(subsystem));
  }
  public static Command fwAuto(ExampleSubsystem subsystem, FlywheelSubsystem f_subsystem, LedSubsystem l_subsystem) {
    // return new FlywheelSetSpeedCommand(f_subsystem, l_subsystem, () -> 0.5, () -> 0.5);
    return Commands.sequence(new FlywheelSetSpeedCommand(f_subsystem, l_subsystem, () -> 0.5, () -> 0.5), 
    new FlywheelSetSpeedCommand(f_subsystem, l_subsystem, () -> -0.25, () -> 1),
    Commands.waitSeconds(2),
    new FlywheelSetSpeedCommand(f_subsystem, l_subsystem, () -> 0.75, () -> 0.75));
  }

  private Autos() {
    throw new UnsupportedOperationException("This is a utility class!");
  }
}
