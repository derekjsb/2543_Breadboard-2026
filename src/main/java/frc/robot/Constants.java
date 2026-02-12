// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }
  public static final class JoystickChannels {
    public static final int OPERATOR_RIGHT_JOYSTICK = 0;
    public static final int OPERATOR_LEFT_JOYSTICK = 1;
    public static final int DRIVER_RIGHT_JOYSTICK = 2;
    public static final int DRIVER_LEFT_JOYSTICK = 3;
  }
  public static final class ButtonIndex {

    public static final class DriverRight {
      public static final int PRECISION_MODE_BUTTON = 1;
      public static final int RESET_PIGEON_BUTTON = 4;
      
    }

    public static final class DriverLeft {
      public static final int TURBO_MODE_BUTTON = 1;
      public static final int CAMERA_TEST_BUTTON = 2;
      public static final int STRAFE_LEFT_BUTTON = 3;
      public static final int STRAFE_RIGHT_BUTTON = 4;
    }
  }
  public static final class COLORS
  {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int CYAN = 3;
    public static final int MAGENTA = 4;
    public static final int YELLOW = 5;
    public static final int TURQUOISE = 6;
    public static final int VIOLET = 7;
    public static final int ORANGE = 8;
    public static final int SPRING_GREEN = 9;
    public static final int OCEAN = 10;
    public static final int RASSBERRY = 11;
    public static final int WHITE = 12;
  }
  public static final class DASHBOARD
  {
    public static final String ELEVATOR_POS = "Elevator Position";
    public static final String FLYWHEEL_SPEED = "Flywheel Speed";
    public static final String AIM_ENABLED = "Auto Aim Enabled";
    public static final String HAS_TARGET = "Has Target";
    public static final String MATCH_TIME = "Match Time";
    public static final String SHIFT_TIME = "Shift Time";
    public static final String LED_COLOR = "LED Color";
    public static final String TARGET_ID = "Target ID";
    public static final String HOOD_POS = "Hood Position";
    public static final String ANGLE_HUB = "Angle to Hub";
    public static final String DIST_HUB = "Distance to Hub";
    public static final String TURRET_SET = "Turret Angle";
  }
  public static final class TIMES  // in seconds
  {
    public static final int ENDGAME = 30;
    public static final int FLASH_WARNING = 5;
    public static final int INITIAL_SHIFT = 10;
    public static final int HUB_SHIFT = 25;
    public static final int MAX_SHIFTS = 3;
  }

  public static final class CAMERA
  {
    public static final String CAMERA1 = "limelight-eleven";
    public static final String CAMERA2 = "limelight-twelve";
    public static final double AMBIGUITY_THRESHOLD = 0.3;   // percent ambiguity
    public static final double DISTTOCAMERA_THRESHOLD = 3.0;  // meters
  }
  public static final class TIMER_CONSTANTS 
  {
    public static final int ENDGAME_SECONDS = 30;
    public static final int ENDGAME_WARNING = 5;
  }
  
  public static final double disabledSeconds = 140;
  public static final double endgameSeconds = 30;
  public static final double endgameWarning = 5;
  public static final double shiftEndWarning = 3;
  public static final String nextInactiveKey = "Next Inactive Alliance";
  public static class ShiftEndConstants {
    public static final double transitionShift = 130;
    public static final double shift1 = 105;
    public static final double shift2 = 80;
    public static final double shift3 = 55;
    public static final double shift4 = 30;
  }
  public static final String ledDefaultPatternKey = "LEDDefaultPattern";
  public static final int ledPatternDefaultValue = 0;
  public static final String flywheelIdKey = "Flywheel ID";
  public static final int flywheelIdDefaultValue = 25;
  public static final String flywheelDeadbandKey = "FOCFlywheelDeadband";
  public static final double flywheelDeadbandDefaultValue = 0.05;
  public static final String turretIdKey = "Turret ID";
  public static final int turretIdDefaultValue = 25;
  public static final String maxTorqueKey = "MaxFOCTorque";
  public static final double torqueDefaultValue = 20.0;
  public static class DutyCycleMultipliers {
    public static final double falcon500 = 119;
    public static final double krakenx60 = 110;
    public static final double krakenx44 = 125;
  }
}
