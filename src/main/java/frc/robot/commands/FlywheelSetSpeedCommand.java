// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Constants;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.LedSubsystem;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class FlywheelSetSpeedCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final FlywheelSubsystem m_subsystem;
  private final LedSubsystem m_ledSubsystem;
  private DoubleSupplier speed;
  private DoubleSupplier torque;
  private TalonFX flywheel;
  private double flywheelTorqueCurrent;
  private double flywheelVelocity;
  private int ledDebounce;
  private double dcMultiplier;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public FlywheelSetSpeedCommand(FlywheelSubsystem subsystem, LedSubsystem ledSubsystem, DoubleSupplier setSpeed, DoubleSupplier setTorque) {
    m_subsystem = subsystem;
    m_ledSubsystem = ledSubsystem;
    speed = setSpeed;
    torque = setTorque;
    dcMultiplier = 125;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  flywheel = new TalonFX(Preferences.getInt(Constants.flywheelIdKey, Constants.flywheelIdDefaultValue));
  m_subsystem.loadPreferences();
  if (m_subsystem.fwDeadband != Preferences.getDouble(Constants.flywheelDeadbandKey, Constants.flywheelDeadbandDefaultValue) 
  || m_subsystem.fwMaxTorque != Preferences.getDouble(Constants.maxTorqueKey, Constants.torqueDefaultValue)) {
    m_subsystem.fwDeadband = Preferences.getDouble(Constants.flywheelDeadbandKey, Constants.flywheelDeadbandDefaultValue);
    m_subsystem.fwMaxTorque = Preferences.getDouble(Constants.maxTorqueKey, Constants.torqueDefaultValue);
    System.out.println("initialized preference values");
  }
  switch (m_subsystem.getMotorType()) {
    case "Falcon 500":
      dcMultiplier = Constants.DutyCycleMultipliers.falcon500;
    case "Kraken X60":
      dcMultiplier = Constants.DutyCycleMultipliers.krakenx60;
    case "Kraken X44":
      dcMultiplier = Constants.DutyCycleMultipliers.krakenx44;
    default: 
    break;
  }
  }
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  m_subsystem.setSpeed(speed.getAsDouble(),torque.getAsDouble());
      // var torqueCurrentSignal = flywheel.getTorqueCurrent();
      // flywheelTorqueCurrent = Math.abs(torqueCurrentSignal.getValueAsDouble());
      // var velocitySignal = flywheel.getVelocity();
      // flywheelVelocity = Math.abs(velocitySignal.getValueAsDouble());
    // SmartDashboard.putNumber("Flywheel Torque Current", flywheelTorqueCurrent);
    // SmartDashboard.putNumber("Flywheel Velocity", flywheelVelocity);
    flywheelTorqueCurrent = m_subsystem.getTorqueCurrent();
    flywheelVelocity = m_subsystem.getVelocity();
    SmartDashboard.putNumber("Flywheel Distance From Target", Math.abs(Math.abs(flywheelVelocity - (Math.abs(speed.getAsDouble()) * dcMultiplier))));
    if (Math.abs(flywheelVelocity - (Math.abs(speed.getAsDouble()) * dcMultiplier)) < 4 && Math.abs(speed.getAsDouble()) > Preferences.getDouble(Constants.flywheelDeadbandKey, 0.01)) {
      if (ledDebounce != -1) {
        m_ledSubsystem.setFlashing(false, false);
        m_ledSubsystem.setColor(9); // purple
        ledDebounce = -1;
      }
    }
    else if (flywheelTorqueCurrent < 1) {
      if (ledDebounce != 0) {
        m_ledSubsystem.resetColor();
        ledDebounce = 0;
      }
    }
    else if (flywheelTorqueCurrent < 8) {
      if (ledDebounce != 1) {
        m_ledSubsystem.setFlashing(false, false);
        m_ledSubsystem.setColor(4); // green
      }
    ledDebounce = 1;
    }
    else if (flywheelTorqueCurrent < 12) {
      if (ledDebounce != 2) {
        m_ledSubsystem.setFlashing(true, false);
        m_ledSubsystem.setColor(3); // yellow
      ledDebounce = 2;
      }
      }
    else if (flywheelTorqueCurrent < 16) {
      if (ledDebounce != 3) {
        m_ledSubsystem.setFlashing(true, false);
        m_ledSubsystem.setColor(1); // red
        ledDebounce = 3;
      }
      } 
  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (Math.abs(flywheelVelocity - (Math.abs(speed.getAsDouble()) * dcMultiplier)) < 4 && Math.abs(speed.getAsDouble()) > Preferences.getDouble(Constants.flywheelDeadbandKey, 0.01)) {
    return true;
    }
    else {
      return false;
    }
  }
}
