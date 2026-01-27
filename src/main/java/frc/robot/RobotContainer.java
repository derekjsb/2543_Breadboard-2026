// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.jni.SwerveJNI.DriveState;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.JoystickChannels;
import frc.robot.Constants.ButtonIndex;
import frc.robot.Constants.ButtonIndex.DriverLeft;
import frc.robot.Constants.ButtonIndex.DriverRight;
import frc.robot.commands.Autos;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.commands.FlywheelSetSpeedCommand;
import frc.robot.commands.LedColorCommand;
import frc.robot.commands.LedEnableCommand;
import frc.robot.commands.TurretResetCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.LedSubsystem;
import frc.robot.subsystems.TurretSubsystem;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;

// import java.util.prefs.Preferences;

// import com.pathplanner.lib.auto.AutoBuilder;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();
  private final FlywheelSubsystem m_flywheelSubsystem = new FlywheelSubsystem();
  private final LedSubsystem m_ledSubsystem = new LedSubsystem();
  private final TurretSubsystem m_turretSubsystem = new TurretSubsystem();
  private final Trigger enableTrigger = new Trigger(DriverStation::isEnabled);
  private final Trigger disableTrigger = new Trigger(DriverStation::isDisabled);
  private final Trigger preEndgameTrigger = new Trigger(() -> (DriverStation.getMatchTime() <= Constants.endgameSeconds + Constants.endgameWarning && DriverStation.getMatchTime() > 1 && DriverStation.isTeleopEnabled()));
  private final Trigger endgameTrigger = new Trigger(() -> (DriverStation.getMatchTime() <= Constants.endgameSeconds && DriverStation.getMatchTime() > 1 && DriverStation.isTeleopEnabled()));
  public int shiftIndex =  0;
  public boolean hubActive = true;
  public double hubTimer = 0.0;
  private final Trigger shiftStartTrigger = new Trigger(() -> (shiftIndex <= 4 && (hubTimer <= 25 && hubTimer > 9) && DriverStation.getMatchTime() > 40 && DriverStation.isTeleopEnabled()));
  private final Trigger shiftPreEndTrigger = new Trigger(() -> (shiftIndex <= 4 && hubTimer < Constants.shiftEndWarning && DriverStation.getMatchTime() > 40 && DriverStation.isTeleopEnabled()));
  DigitalInput m_limitSwitch = new DigitalInput(0);
  private final Trigger turretResetTrigger = new Trigger(() -> (!m_limitSwitch.get()));


  private final Joystick operatorLeftStick;
  private final Joystick operatorRightStick;
  private final Joystick driverLeftStick;
  private final Joystick driverRightStick;
  private double MaxSpeed;
  private double MaxAngularRate;
  private final SwerveRequest.FieldCentric drive;
  private final SwerveRequest.RobotCentric strafe;
  private final Telemetry logger;
  public final CommandSwerveDrivetrain drivetrain;

  // Replace with CommandPS4Controller or CommandJoystick if needed
  // private final CommandXboxController m_driverController =
  //     new CommandXboxController(OperatorConstants.kDriverControllerPort);
  private final Joystick m_driverController =
      new Joystick(OperatorConstants.kDriverControllerPort);
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings
    // initi create joysticks
    operatorLeftStick = new Joystick(JoystickChannels.OPERATOR_LEFT_JOYSTICK);
    operatorRightStick = new Joystick(JoystickChannels.OPERATOR_RIGHT_JOYSTICK);
    driverLeftStick = new Joystick(JoystickChannels.DRIVER_LEFT_JOYSTICK);
    driverRightStick = new Joystick(JoystickChannels.DRIVER_RIGHT_JOYSTICK);
    
    // swerve system
    MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top speed
    MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per second max angular velocity
    drive = new SwerveRequest.FieldCentric()
      .withDeadband(MaxSpeed * 0.05).withRotationalDeadband(MaxAngularRate * 0.05) 
      .withDriveRequestType(DriveRequestType.OpenLoopVoltage); 
    strafe = new SwerveRequest.RobotCentric()
      .withDriveRequestType(DriveRequestType.OpenLoopVoltage);
    logger = new Telemetry(MaxSpeed);
    drivetrain = TunerConstants.createDrivetrain();
    Settings.Init();
configureSwerveBindings();
    configureBindings();
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */

   // check for precision or turbo mode
private double getSpeedFactor() {
    double speedFactor = 1;
    if (driverRightStick.getRawButton(DriverRight.PRECISION_MODE_BUTTON)) {
      speedFactor =  Settings.getSwervePrecisionFactor();
    } else if (!driverLeftStick.getRawButton(DriverLeft.TURBO_MODE_BUTTON)) {
      speedFactor = Settings.getSwerveSpeedFactor();
    } 
    return MaxSpeed * speedFactor;
  }

private void configureSwerveBindings() {
      
    // drive system  
    drivetrain.setDefaultCommand(
          drivetrain.applyRequest(() ->
              drive
                .withVelocityX(-driverLeftStick.getY() * getSpeedFactor()) 
                .withVelocityY(-driverLeftStick.getX() * getSpeedFactor()) 
                .withRotationalRate(-driverRightStick.getZ() * getSpeedFactor()) 
          )
      );
      final var idle = new SwerveRequest.Idle();
      RobotModeTriggers.disabled().whileTrue(
          drivetrain.applyRequest(() -> idle).ignoringDisable(true)
      );
      drivetrain.registerTelemetry(logger::telemeterize);

SmartDashboard.putData("Swerve Drive", new Sendable() {
  @Override
  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("SwerveDrive");

    builder.addDoubleProperty("Front Left Angle", () -> drivetrain.getModule(0).getPosition(true).angle.getRadians(), null);
    builder.addDoubleProperty("Front Left Velocity", () -> drivetrain.getModule(0).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

    builder.addDoubleProperty("Front Right Angle", () -> drivetrain.getModule(1).getPosition(true).angle.getRadians(), null);
    builder.addDoubleProperty("Front Right Velocity", () -> drivetrain.getModule(1).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

    builder.addDoubleProperty("Back Left Angle", () -> drivetrain.getModule(2).getPosition(true).angle.getRadians(), null);
    builder.addDoubleProperty("Back Left Velocity", () -> drivetrain.getModule(2).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

    builder.addDoubleProperty("Back Right Angle", () -> drivetrain.getModule(3).getPosition(true).angle.getRadians(), null);
    builder.addDoubleProperty("Back Right Velocity", () -> drivetrain.getModule(3).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

    builder.addDoubleProperty("Robot Angle", () -> drivetrain.getState().RawHeading.getRadians(), null);
  }
});

      // pigeon reset
      new JoystickButton(driverRightStick, 4).onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

      // strafe right
      double strafeSpeed = Settings.getSwerveStrafeSpeed();
      new JoystickButton(driverLeftStick, 4).whileTrue(drivetrain.applyRequest(() -> strafe
        .withVelocityY(0)
        .withVelocityX(strafeSpeed)
        .withRotationalRate(0)));

      // strafe left
      new JoystickButton(driverLeftStick, 3).whileTrue(drivetrain.applyRequest(() -> strafe
        .withVelocityY(0)
        .withVelocityX(strafeSpeed * -1)
        .withRotationalRate(0)));
  }


  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));



    m_flywheelSubsystem.setDefaultCommand(new FlywheelSetSpeedCommand(m_flywheelSubsystem, m_ledSubsystem, () -> m_driverController.getY(), () -> m_driverController.getX()));
    enableTrigger.onTrue(new LedEnableCommand(m_ledSubsystem, 1).ignoringDisable(true));
    disableTrigger.whileTrue(new LedEnableCommand(m_ledSubsystem, 0).ignoringDisable(true));
    shiftStartTrigger.onTrue(new LedEnableCommand(m_ledSubsystem, 2).ignoringDisable(true));
    shiftPreEndTrigger.onTrue(new LedEnableCommand(m_ledSubsystem, 3).ignoringDisable(true));
    preEndgameTrigger.onTrue(new LedEnableCommand(m_ledSubsystem, 3).ignoringDisable(true));
    endgameTrigger.onTrue(new LedEnableCommand(m_ledSubsystem, 4).ignoringDisable(true));
    new JoystickButton(m_driverController, 9).onTrue(new LedEnableCommand(m_ledSubsystem, 0).ignoringDisable(true));
    new JoystickButton(m_driverController, 10).onTrue(new LedEnableCommand(m_ledSubsystem, 1).ignoringDisable(true));
    new JoystickButton(m_driverController, 1).onTrue(new LedColorCommand(m_ledSubsystem,1, 2,false));
    new JoystickButton(m_driverController, 2).onTrue(new LedColorCommand(m_ledSubsystem,1, 3,false));
    new JoystickButton(m_driverController, 3).onTrue(new LedColorCommand(m_ledSubsystem,1, 4,false));
    new JoystickButton(m_driverController, 4).onTrue(new LedColorCommand(m_ledSubsystem,1, 5,false));
    new JoystickButton(m_driverController, 5).onTrue(new LedColorCommand(m_ledSubsystem,2, 6,false));
    new JoystickButton(m_driverController, 6).onTrue(new LedColorCommand(m_ledSubsystem,2, 6,true));
    new JoystickButton(m_driverController, 7).onTrue(new LedColorCommand(m_ledSubsystem,3, 6,false));
    new JoystickButton(m_driverController, 8).onTrue(new LedColorCommand(m_ledSubsystem,3, 6,true));

    turretResetTrigger.onTrue(new TurretResetCommand(m_turretSubsystem).ignoringDisable(true));

    SmartDashboard.putBoolean("Auto",false);
    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    // m_driverController.ax.whileTrue(m_exampleSubsystem.exampleMethodCommand());
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    if (SmartDashboard.getBoolean("Auto", false)) {
    return Autos.fwAuto(m_exampleSubsystem,m_flywheelSubsystem,m_ledSubsystem);
    }
    else {
      return Autos.exampleAuto(m_exampleSubsystem);
    }
  }
}
