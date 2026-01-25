// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Optional;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The methods in this class are called automatically corresponding to each mode, as described in
 * the TimedRobot documentation. If you change the name of this class or the package after creating
 * this project, you must also update the Main.java file in the project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;
  private double noFmsConstant;

  private final RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  public Robot() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer();
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());
    double matchTime = DriverStation.getMatchTime();
    System.out.println(matchTime);
    m_robotContainer.shiftIndex = 0;
    m_robotContainer.hubActive = false;
    m_robotContainer.hubTimer = 0.0;
  }

  @Override
  public void disabledPeriodic() {
    if (DriverStation.isFMSAttached()) {noFmsConstant = 0;} else {noFmsConstant = 1;}
    double matchTime = DriverStation.getMatchTime();
    if (matchTime > -1) {
      SmartDashboard.putNumber("Match Time", matchTime);
      }
      else {
        SmartDashboard.putNumber("Match Time", Constants.disabledSeconds);
     }
     SmartDashboard.putNumber("Shift Time", DriverStation.getMatchTime() + noFmsConstant);
    m_robotContainer.hubActive = false;
    SmartDashboard.putBoolean("Alliance Hub Active", m_robotContainer.hubActive);
  }
  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    SmartDashboard.putString(Constants.nextInactiveKey, "N");
    if (DriverStation.isFMSAttached()) {noFmsConstant = 0;}
    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    SmartDashboard.putNumber("Match Time", DriverStation.getMatchTime() + noFmsConstant);
    SmartDashboard.putNumber("Shift Time", DriverStation.getMatchTime() + noFmsConstant);
    m_robotContainer.hubActive = true;
    SmartDashboard.putBoolean("Alliance Hub Active", m_robotContainer.hubActive);
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (DriverStation.isFMSAttached()) {noFmsConstant = 0;}
    SmartDashboard.putString(Constants.nextInactiveKey, "N");
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    double matchTime = DriverStation.getMatchTime();
    SmartDashboard.putNumber("Match Time", matchTime + noFmsConstant);
    if(DriverStation.getGameSpecificMessage().length() > 0)
{

  double shiftTime = matchTime;
  if (matchTime > Constants.ShiftEndConstants.transitionShift) {
    
    SmartDashboard.putString(Constants.nextInactiveKey, DriverStation.getGameSpecificMessage());
    m_robotContainer.shiftIndex = 0;
    shiftTime = matchTime - Constants.ShiftEndConstants.transitionShift;
  }
  else if (matchTime > Constants.ShiftEndConstants.shift1) {
    m_robotContainer.shiftIndex = 1;
    shiftTime = matchTime - Constants.ShiftEndConstants.shift1;
  }
  else if (matchTime > Constants.ShiftEndConstants.shift2) {
    m_robotContainer.shiftIndex = 2;
    shiftTime = matchTime - Constants.ShiftEndConstants.shift2;
  }
  else if (matchTime > Constants.ShiftEndConstants.shift3) {
    m_robotContainer.shiftIndex = 3;
    shiftTime = matchTime - Constants.ShiftEndConstants.shift3;
  }
  else if (matchTime > Constants.ShiftEndConstants.shift4) {
    m_robotContainer.shiftIndex = 4;
    shiftTime = matchTime - Constants.ShiftEndConstants.shift4;
  }
  else if (matchTime < Constants.endgameSeconds) {
    m_robotContainer.shiftIndex = 5;
    shiftTime = matchTime;
  }
  SmartDashboard.putNumber("Shift Time", shiftTime + noFmsConstant);
  m_robotContainer.hubTimer = shiftTime;
  m_robotContainer.hubActive = true;
  Optional<Alliance> ally = DriverStation.getAlliance();
  switch (DriverStation.getGameSpecificMessage().charAt(0))
  {
    case 'B' :
      //Blue case code
      if (m_robotContainer.shiftIndex == 1 || m_robotContainer.shiftIndex == 3) {
        SmartDashboard.putString(Constants.nextInactiveKey, "R");
      }
      else if (m_robotContainer.shiftIndex == 2 || m_robotContainer.shiftIndex == 4) {
        SmartDashboard.putString(Constants.nextInactiveKey, "B");
      }
      else if (m_robotContainer.shiftIndex == 5) {
        SmartDashboard.putString(Constants.nextInactiveKey, "N");
      }
      if (ally.isPresent()) {
        if (ally.get() == Alliance.Red) {
            if (m_robotContainer.shiftIndex == 2 || m_robotContainer.shiftIndex == 4) {
              m_robotContainer.hubActive = false;
            }
        }
        else {
          if (m_robotContainer.shiftIndex == 1 || m_robotContainer.shiftIndex == 3) {
            m_robotContainer.hubActive = false;
          }
        }
    }
      break;
    case 'R' :
      //Red case code
      if (m_robotContainer.shiftIndex == 1 || m_robotContainer.shiftIndex == 3) {
        SmartDashboard.putString(Constants.nextInactiveKey, "B");
      }
      else if (m_robotContainer.shiftIndex == 2 || m_robotContainer.shiftIndex == 4) {
        SmartDashboard.putString(Constants.nextInactiveKey, "R");
      }
      else if (m_robotContainer.shiftIndex == 5) {
        SmartDashboard.putString(Constants.nextInactiveKey, "N");
      }
      if (ally.isPresent()) {
        if (ally.get() == Alliance.Red) {
            if (m_robotContainer.shiftIndex == 1 || m_robotContainer.shiftIndex == 3) {
              m_robotContainer.hubActive = false;
            }
        }
        else {
          if (m_robotContainer.shiftIndex == 2 || m_robotContainer.shiftIndex == 4) {
            m_robotContainer.hubActive = false;
          }
        }
      }
      break;
    default :
      m_robotContainer.hubActive = true;
      //This is corrupt data
      break;
  }
} else {
  //Code for no data received yet
  SmartDashboard.putNumber("Shift Time", matchTime);
  SmartDashboard.putString(Constants.nextInactiveKey, "N");
  m_robotContainer.hubActive = true;
}
SmartDashboard.putBoolean("Alliance Hub Active", m_robotContainer.hubActive);
    // if (matchTime <= Constants.endgameSeconds) {

    // }
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
