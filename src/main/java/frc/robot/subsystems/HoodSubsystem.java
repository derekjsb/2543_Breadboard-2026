package frc.robot.subsystems;

import com.ctre.phoenix6.SignalLogger;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DASHBOARD;

public class HoodSubsystem extends SubsystemBase {
  
  private final Servo hoodServo;
  private static final double SLOPE = 1;
  private static final double INTERCEPT = 0;

  public HoodSubsystem() 
  {
     hoodServo = new Servo(0);
     Preferences.initDouble("Hood Custom Position", 0);
     SignalLogger.writeDouble("Hood Position", 0);
  }

  public void setServo(double position) {
    if (position > 1) {
      position = 1;
    } else if (position < 0) {
      position = 0;
    }
    SmartDashboard.putNumber(DASHBOARD.HOOD_POS, position);
    hoodServo.set(position);
    SignalLogger.writeDouble("Hood Position", position);
  }

  public void setByDistance(double dist)
  {
    // TODO: need conversion of distance to position
    double pos = (dist * SLOPE) + INTERCEPT;
    setServo(pos);
  }
  
}

