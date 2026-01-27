package frc.robot;

import edu.wpi.first.wpilibj.Preferences;

public class Settings {

  // keys
  private static final String SWERVE_SPEED_FACTOR = "Swerve Speed Factor";
  private static final String SWERVE_PRECISION_FACTOR = "Swerve Precision Factor";
  private static final String SWERVE_STRAFE_SPEED = "Swerve Strafe Speed";
  
  //default values
  private static final double SWERVE_SPEED_FACTOR_DEFAULT = 0.5;
  private static final double SWERVE_PRECISION_FACTOR_DEFAULT = 0.15;
  private static final double SWERVE_STRAFE_SPEED_DEFAULT = 0.25;

  public static final void Init() {
      if (!Preferences.containsKey(SWERVE_SPEED_FACTOR)) {
        Preferences.initDouble(SWERVE_SPEED_FACTOR, SWERVE_SPEED_FACTOR_DEFAULT);
      }
      if (!Preferences.containsKey(SWERVE_PRECISION_FACTOR)) {
        Preferences.initDouble(SWERVE_PRECISION_FACTOR, SWERVE_PRECISION_FACTOR_DEFAULT);
      }
      if (!Preferences.containsKey(SWERVE_STRAFE_SPEED)) {
        Preferences.initDouble(SWERVE_SPEED_FACTOR, SWERVE_STRAFE_SPEED_DEFAULT);
      }
     } 

    public static final void Reset() {
      Preferences.setDouble(SWERVE_SPEED_FACTOR, SWERVE_SPEED_FACTOR_DEFAULT);
      Preferences.setDouble(SWERVE_PRECISION_FACTOR, SWERVE_PRECISION_FACTOR_DEFAULT);
      Preferences.setDouble(SWERVE_STRAFE_SPEED, SWERVE_STRAFE_SPEED_DEFAULT);
    }

    public static double getSwerveStrafeSpeed() {
      return Preferences.getDouble(SWERVE_STRAFE_SPEED, SWERVE_STRAFE_SPEED_DEFAULT);
    }

    public static double getSwerveSpeedFactor() {
      return Preferences.getDouble(SWERVE_SPEED_FACTOR, SWERVE_SPEED_FACTOR_DEFAULT);
    }

    public static double getSwervePrecisionFactor() {
      return Preferences.getDouble(SWERVE_PRECISION_FACTOR, SWERVE_PRECISION_FACTOR_DEFAULT);
    }
}
