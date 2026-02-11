// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;

// import java.util.prefs.Preferences;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.Preferences;

/** An example command that uses an example subsystem. */
public class BangBangCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final FlywheelSubsystem m_subsystem;
  private double speedSetPoint;
  private boolean speedReady;
  private double nominalCurrent;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public BangBangCommand(FlywheelSubsystem subsystem, double speed) {
    m_subsystem = subsystem;
    speedSetPoint = speed/60;
    speedReady = false;
    Preferences.initDouble("BangBang Nominal Current", 20);
    nominalCurrent = Preferences.getDouble("BangBang Nominal Current", 20);
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    nominalCurrent = Preferences.getDouble("BangBang Nominal Current", 20);
    m_subsystem.setBangBangVoltage(16);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // System.out.println(speedSetPoint-m_subsystem.getVelocity());
    if (speedReady) {
      System.out.println(m_subsystem.getVelocity() - speedSetPoint);
      if (m_subsystem.getVelocity() < speedSetPoint) {
        m_subsystem.setBangBangTorque(nominalCurrent);
      }
      else {
        m_subsystem.setBangBangTorque(0);
      }
    }
    else {
      if (m_subsystem.getVelocity() > speedSetPoint) {speedReady = true;}
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {speedReady = false;}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (speedSetPoint < 1 || DriverStation.isDisabled()) {
      m_subsystem.setBangBangVoltage(0);
      return true;
    }
    return false;
  }
}
