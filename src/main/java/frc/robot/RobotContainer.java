// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.jni.SwerveJNI.DriveState;
import com.ctre.phoenix6.swerve.SwerveRequest;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.JoystickChannels;
import frc.robot.Constants.ButtonIndex;
import frc.robot.Constants.ButtonIndex.DriverLeft;
import frc.robot.Constants.ButtonIndex.DriverRight;
import frc.robot.Constants.COLORS;
import frc.robot.Constants.DASHBOARD;
import frc.robot.commands.Autos;
import frc.robot.commands.BangBangCommand;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.commands.FlywheelSetSpeedCommand;
import frc.robot.commands.ShiftColorsCommand;
// import frc.robot.commands.LedColorCommand;
// import frc.robot.commands.LedEnableCommand;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.LEDSubsystem;
// import frc.robot.subsystems.LedSubsystem;
import frc.robot.util.Elastic;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.TIMER_CONSTANTS;
import frc.robot.Constants.TIMES;

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
  private final LEDSubsystem LEDSub = new LEDSubsystem();
  private final HoodSubsystem hoodSub = new HoodSubsystem();
  private final Trigger autonomousTrigger;
  private final Trigger enableTrigger;
  private final Trigger disableTrigger;
  private final Trigger endgameWarningTrigger;
  private final Trigger endgameTrigger;
  private final SendableChooser<String> autoChoose = new SendableChooser<String>();
  public int shiftIndex =  0;
  public boolean hubActive = true;
  public double hubTimer = 0.0;
  // private final Trigger shiftStartTrigger;
  // private final Trigger shiftPreEndTrigger;
  DigitalInput m_limitSwitch = new DigitalInput(0);
  // private final Trigger turretResetTrigger = new Trigger(() -> (!m_limitSwitch.get()));


  // private final Joystick operatorLeftStick;
  // private final Joystick operatorRightStick;
  // private final Joystick driverLeftStick;
  // private final Joystick driverRightStick;
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
    // triggers
    autonomousTrigger = new Trigger(RobotModeTriggers.autonomous());    
    enableTrigger = new Trigger(RobotModeTriggers.teleop());
    disableTrigger = new Trigger(RobotModeTriggers.disabled());
    endgameWarningTrigger = new Trigger(() -> (LEDSub.isEndgame(TIMES.FLASH_WARNING)));
    endgameTrigger = new Trigger(() -> (LEDSub.isEndgame(0)));
    // operatorLeftStick = new Joystick(JoystickChannels.OPERATOR_LEFT_JOYSTICK);
    // operatorRightStick = new Joystick(JoystickChannels.OPERATOR_RIGHT_JOYSTICK);
    // driverLeftStick = new Joystick(JoystickChannels.DRIVER_LEFT_JOYSTICK);
    // driverRightStick = new Joystick(JoystickChannels.DRIVER_RIGHT_JOYSTICK);
    
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
    // configureSwerveBindings();
    setupDashboard();
    triggerBinding();
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
// private double getSpeedFactor() {
//     double speedFactor = 1;
//     if (driverRightStick.getRawButton(DriverRight.PRECISION_MODE_BUTTON)) {
//       speedFactor =  Settings.getSwervePrecisionFactor();
//     } else if (!driverLeftStick.getRawButton(DriverLeft.TURBO_MODE_BUTTON)) {
//       speedFactor = Settings.getSwerveSpeedFactor();
//     } 
//     return MaxSpeed * speedFactor;
//   }

// private void configureSwerveBindings() {
      
//     // drive system  
//     drivetrain.setDefaultCommand(
//           drivetrain.applyRequest(() ->
//               drive
//                 .withVelocityX(-driverLeftStick.getY() * getSpeedFactor()) 
//                 .withVelocityY(-driverLeftStick.getX() * getSpeedFactor()) 
//                 .withRotationalRate(-driverRightStick.getZ() * getSpeedFactor()) 
//           )
//       );
//       final var idle = new SwerveRequest.Idle();
//       RobotModeTriggers.disabled().whileTrue(
//           drivetrain.applyRequest(() -> idle).ignoringDisable(true)
//       );
//       drivetrain.registerTelemetry(logger::telemeterize);

// SmartDashboard.putData("Swerve Drive", new Sendable() {
//   @Override
//   public void initSendable(SendableBuilder builder) {
//     builder.setSmartDashboardType("SwerveDrive");

//     builder.addDoubleProperty("Front Left Angle", () -> drivetrain.getModule(0).getPosition(true).angle.getRadians(), null);
//     builder.addDoubleProperty("Front Left Velocity", () -> drivetrain.getModule(0).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

//     builder.addDoubleProperty("Front Right Angle", () -> drivetrain.getModule(1).getPosition(true).angle.getRadians(), null);
//     builder.addDoubleProperty("Front Right Velocity", () -> drivetrain.getModule(1).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

//     builder.addDoubleProperty("Back Left Angle", () -> drivetrain.getModule(2).getPosition(true).angle.getRadians(), null);
//     builder.addDoubleProperty("Back Left Velocity", () -> drivetrain.getModule(2).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

//     builder.addDoubleProperty("Back Right Angle", () -> drivetrain.getModule(3).getPosition(true).angle.getRadians(), null);
//     builder.addDoubleProperty("Back Right Velocity", () -> drivetrain.getModule(3).getDriveMotor().getVelocity(true).getValueAsDouble(), null);

//     builder.addDoubleProperty("Robot Angle", () -> drivetrain.getState().RawHeading.getRadians(), null);
//   }
// });

//       // pigeon reset
//       new JoystickButton(driverRightStick, 4).onTrue(drivetrain.runOnce(() -> drivetrain.seedFieldCentric()));

//       // strafe right
//       double strafeSpeed = Settings.getSwerveStrafeSpeed();
//       new JoystickButton(driverLeftStick, 4).whileTrue(drivetrain.applyRequest(() -> strafe
//         .withVelocityY(0)
//         .withVelocityX(strafeSpeed)
//         .withRotationalRate(0)));

//       // strafe left
//       new JoystickButton(driverLeftStick, 3).whileTrue(drivetrain.applyRequest(() -> strafe
//         .withVelocityY(0)
//         .withVelocityX(strafeSpeed * -1)
//         .withRotationalRate(0)));
//   }

private void setupDashboard() {
  SmartDashboard.putNumber("Hood Position", 45);
  SmartDashboard.putNumber("Elevator Position", 25); // 
  SmartDashboard.putString("Turret State", "AUTO"); // in large text
  SmartDashboard.putNumber("Turret Angle", 60); // in gyro
  SmartDashboard.putNumber("Turret Angle to Hub", 60); // in gyro
  SmartDashboard.putNumber("Turret Distance to Hub", 90); // in number bar
  SmartDashboard.putNumber("Intake Angle", 75); // in degrees to horizontal
  autoChoose.setDefaultOption("Nothing Burger", "Nothing Burger");
  autoChoose.addOption("Nothing Fries", "Nothing Fries");
    SmartDashboard.putData(DASHBOARD.AUTO_CHOOSER, autoChoose);
}

private boolean isEndgame(int warning)
  {
    return DriverStation.getMatchTime() <= TIMER_CONSTANTS.ENDGAME_SECONDS + warning 
      && DriverStation.getMatchTime() > 1 
      && DriverStation.isTeleopEnabled();
  }  

  private void triggerBinding() {

    // go alliance color duing auto
    autonomousTrigger.onTrue(
      Commands.sequence(
        LEDSub.runOnce(() -> LEDSub.setEnabled(true)),
        LEDSub.runOnce(() -> LEDSub.setAllianceColor()),
        LEDSub.runOnce(() -> LEDSub.setFlashing(false))
      )
    );

    // handle shifts during teleop
    enableTrigger.onTrue(new ShiftColorsCommand(LEDSub));

    // go to default light show
    disableTrigger.onTrue(
      LEDSub.runOnce(() -> LEDSub.setEnabled(false)).ignoringDisable(true)
    );

    // falsh endgame warning
    endgameWarningTrigger.onTrue(
      Commands.sequence(
        LEDSub.runOnce(() -> LEDSub.setEnabled(true)),
        LEDSub.runOnce(() -> LEDSub.setColor(COLORS.GREEN)),
        LEDSub.runOnce(() -> LEDSub.setFlashing(true))
      )

    );

    // endgame color change
    endgameTrigger.onTrue(
      Commands.sequence(
        LEDSub.runOnce(() -> LEDSub.setDashboardColor()),
        LEDSub.runOnce(() -> LEDSub.setFlashing(false))
      )
    );
    
  }

  private void configureBindings() {
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    new JoystickButton(m_driverController, 1).onTrue(new BangBangCommand(m_flywheelSubsystem, Preferences.getDouble("BangBang Custom Speed", 2000)));
    new JoystickButton(m_driverController, 5).onTrue(new BangBangCommand(m_flywheelSubsystem, 0));
    new JoystickButton(m_driverController, 3).onTrue(new BangBangCommand(m_flywheelSubsystem, 2000));
    new JoystickButton(m_driverController, 4).onTrue(new BangBangCommand(m_flywheelSubsystem, 4000));
    new JoystickButton(m_driverController, 6).onTrue(new BangBangCommand(m_flywheelSubsystem, 5500));
    new JoystickButton(m_driverController, 2).onTrue(hoodSub.runOnce(() -> hoodSub.setServo(Preferences.getDouble("Hood Custom Position", 0))));
    new JoystickButton(m_driverController, 9).onTrue(hoodSub.runOnce(() -> hoodSub.setServo(0.25)));
    new JoystickButton(m_driverController, 10).onTrue(hoodSub.runOnce(() -> hoodSub.setServo(0.5)));
    new JoystickButton(m_driverController, 11).onTrue(hoodSub.runOnce(() -> hoodSub.setServo(0.75)));
    new JoystickButton(m_driverController, 12).onTrue(hoodSub.runOnce(() -> hoodSub.setServo(1)));

    // turretResetTrigger.onTrue(new TurretResetCommand(m_turretSubsystem).ignoringDisable(true));

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
    return Autos.fwAuto(m_exampleSubsystem,m_flywheelSubsystem,LEDSub);
    }
    else {
      return Autos.exampleAuto(m_exampleSubsystem);
    }
  }
}
