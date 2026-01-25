// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import frc.robot.Constants;

import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.FeedbackConfigs;
import com.ctre.phoenix6.configs.MotionMagicConfigs;
import com.ctre.phoenix6.configs.MotorOutputConfigs;
import com.ctre.phoenix6.configs.Slot0Configs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.PositionDutyCycle;
import com.ctre.phoenix6.controls.TorqueCurrentFOC;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.FeedbackSensorSourceValue;
import com.ctre.phoenix6.signals.GravityTypeValue;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;

public class TurretSubsystem extends SubsystemBase {
  private TalonFX turret;
  private TorqueCurrentFOC torquecurrent;
  private double torqueOutput;
  public double fwDeadband;
  public double fwMaxTorque;
  private double turretTorqueCurrent;
  private double turretVelocity;
  private boolean useTorqueCurrentFOC;
  private final SendableChooser<String> motorChooser = new SendableChooser<>();

  /** Creates a new ExampleSubsystem. */
  public TurretSubsystem() {
    Preferences.initDouble(Constants.flywheelDeadbandKey, 0.05);
    Preferences.initDouble(Constants.maxTorqueKey, 20);
    Preferences.initBoolean("Use TorqueCurrentFOC", true);
    Preferences.initInt(Constants.turretIdKey, Constants.turretIdDefaultValue);
    Preferences.initString("Motor Default", "Kraken X44");
    // motorChooser.addOption("Falcon 500", "Falcon 500");
    // motorChooser.addOption("Kraken X60", "Kraken X60");
    // motorChooser.addOption("Kraken X44", "Kraken X44");
    // motorChooser.setDefaultOption(Preferences.getString("Motor Default", "Kraken X44"), Preferences.getString("Motor Default", "Kraken X44"));
    // SmartDashboard.putData(motorChooser);
    turret = new TalonFX(Preferences.getInt(Constants.turretIdKey, Constants.turretIdDefaultValue));
    setConfiguration();
    loadPreferences();
  }

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public void setSpeed(double speed, double torque) {
    if (speed < -fwDeadband) {
      torqueOutput = Math.abs(torque)*fwMaxTorque;
    }
    else if (speed >= fwDeadband) {
      torqueOutput = -Math.abs(torque)*fwMaxTorque;
    }
    else {
      torqueOutput = 0;
      speed = 0;
    }

    // torque current
    if (useTorqueCurrentFOC) {
    torquecurrent = new TorqueCurrentFOC(torqueOutput);
    turret.setControl(torquecurrent
    .withMaxAbsDutyCycle(Math.abs(speed)));
    }
    // duty cycle
    else {
    turret.setControl(new DutyCycleOut(-speed).withEnableFOC((torque > 0)));
    }

    // System.out.println(speed);
  }

  private double wrapAround(double value, double min, double max) {
    double range = max - min + 1; // Calculate the size of the range
    // The formula ensures the result is always positive and within the range
    return ((((value - min) % range) + range) % range) + min;
}
  public void setPosition(double pos) {
    turret.setControl(new PositionDutyCycle(wrapAround(pos, 0, 1)).withSlot(0));
  }

  public void resetPosition(double pos) {
    turret.setPosition(0);
    if (DriverStation.isDisabled()) {
    setConfiguration();
    }
  }
  public void setConfiguration() {
   var currentLimitConfig = new CurrentLimitsConfigs()
      .withStatorCurrentLimitEnable(true)
      .withStatorCurrentLimit(Preferences.getDouble(Constants.maxTorqueKey, 30))
      .withSupplyCurrentLimitEnable(true)
      .withSupplyCurrentLimit(Preferences.getDouble(Constants.maxTorqueKey, 30));

    var motorOutputConfig = new MotorOutputConfigs()
      .withInverted(InvertedValue.CounterClockwise_Positive)
      .withNeutralMode(NeutralModeValue.Coast);
    
    var slot0Config = new Slot0Configs()
      .withGravityType(GravityTypeValue.Elevator_Static)
      .withKA(0)
      .withKG(0.0)
      .withKP(2)
      .withKI(0.0)
      .withKS(0.0)
      .withKV(0.0)
      .withKD(0.0);

    var feedbackConfigs = new FeedbackConfigs()
      .withFeedbackSensorSource(FeedbackSensorSourceValue.RotorSensor)
      .withSensorToMechanismRatio(11.1);

    var talonFXConfig = new TalonFXConfiguration()
      .withMotorOutput(motorOutputConfig)
      .withSlot0(slot0Config)
      .withFeedback(feedbackConfigs)
      .withCurrentLimits(currentLimitConfig);

    turret.getConfigurator().apply(talonFXConfig);
    
    SmartDashboard.putBoolean("Flywheel Config Refresh", false);
    SmartDashboard.putBoolean("Flyheel Pro Licensed", turret.getIsProLicensed(false).getValue());
  }

  public void loadPreferences() {
    fwDeadband = Preferences.getDouble(Constants.flywheelDeadbandKey, Constants.flywheelDeadbandDefaultValue);
    fwMaxTorque = Preferences.getDouble(Constants.maxTorqueKey, Constants.torqueDefaultValue);
    useTorqueCurrentFOC = Preferences.getBoolean("Use TorqueCurrentFOC", true);
  }

  public String getMotorType() {
    Preferences.setString("Motor Default", motorChooser.getSelected());
    System.out.println(motorChooser.getSelected());
    return motorChooser.getSelected();
  }

  public double getVelocity() {
    return turretVelocity;
  }
  public double getTorqueCurrent() {
    return turretTorqueCurrent;
  }

  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    var torqueCurrentSignal = turret.getTorqueCurrent();
    turretTorqueCurrent = Math.abs(torqueCurrentSignal.getValueAsDouble());
    var velocitySignal = turret.getVelocity();
    turretVelocity = Math.abs(velocitySignal.getValueAsDouble());
    //  SmartDashboard.putNumber("Flywheel Torque Current", turretTorqueCurrent);
    // SmartDashboard.putNumber("Flywheel Velocity", turretVelocity);
    // if (SmartDashboard.getBoolean("Flywheel Config Refresh", false) == true) {setConfiguration();}
    if (Preferences.getInt(Constants.turretIdKey, Constants.turretIdDefaultValue) != turret.getDeviceID()) {turret = new TalonFX(Preferences.getInt(Constants.turretIdKey, Constants.turretIdDefaultValue));} 
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
