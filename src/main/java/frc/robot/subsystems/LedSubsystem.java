package frc.robot.subsystems;

import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.util.Elastic;

public class LedSubsystem extends SubsystemBase {

  private final I2C arduino;
  private int allianceColor;
  private int currentColor;
  private int mode;

  // local constants
  private static int ENABLED = 1;
  private static final int SOLID_MODE = 0;
  private static final int FLASHING_MODE = 30;
  private static final int PRE_ENDGAME_COLOR = 37;
  private static final int SHIFT_ACTIVE_COLOR = 14;
  private static final int SHIFT_INACTIVE_COLOR = 31;
  private static final int ENDGAME_COLOR = 16;

  // color constants
  public static final class Color {
    public static final int BLUE = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int WHITE = 12;
  }

  public LedSubsystem() {
    arduino = new I2C(I2C.Port.kOnboard, 0x08);
    // arduino.write(1, 1);
    setAllianceColor(0);
    mode = SOLID_MODE;
    // setColor(allianceColor);
    Preferences.initInt(Constants.ledDefaultPatternKey, Constants.ledPatternDefaultValue);
    pushDashboardValues();
  }

  
  private void setAllianceColor(int endgame) {
    allianceColor = Color.BLUE;
    currentColor = Color.BLUE;
    Optional<Alliance> ally = DriverStation.getAlliance();
    if (ally.isPresent()) {
        if (ally.get() == Alliance.Red) {
            allianceColor = Color.RED;
            currentColor = Color.RED;
        }
    }
    System.out.println("alliance color: " + allianceColor);
    if (Preferences.getInt(Constants.ledDefaultPatternKey, Constants.ledPatternDefaultValue) > 1) {
      System.out.println("custom led");
      allianceColor = Preferences.getInt(Constants.ledDefaultPatternKey, Constants.ledPatternDefaultValue);
      currentColor = allianceColor;
    }
    if (endgame == 2) {
      if (SmartDashboard.getBoolean("Alliance Hub Active", false) == true && SmartDashboard.getNumber("Match Time", 0) > 0) {
      allianceColor = SHIFT_ACTIVE_COLOR;
      currentColor = SHIFT_ACTIVE_COLOR;
      }
    }
    else if (endgame == 3) {
      allianceColor = PRE_ENDGAME_COLOR;
      currentColor = PRE_ENDGAME_COLOR;
      if (SmartDashboard.getBoolean("Alliance Hub Active", false) == true && SmartDashboard.getNumber("Match Time", 140) >= 50) {
        char nextInactive = SmartDashboard.getString(Constants.nextInactiveKey, "N").charAt(0);
        System.out.println("active");
        if ((nextInactive == 'R' && ally.get() == Alliance.Red) ||
        (nextInactive == 'B' && ally.get() == Alliance.Blue)) {
          System.out.println("going red");
          allianceColor = SHIFT_INACTIVE_COLOR;
          currentColor = SHIFT_INACTIVE_COLOR;
          Elastic.Notification dashboardNotification = new Elastic.Notification(Elastic.NotificationLevel.WARNING, "Shift Ending Soon", "Your Alliance Shift is ending soon.");
          Elastic.sendNotification(dashboardNotification);
        }
      }
    }
    else if (endgame == 4) {
      allianceColor = ENDGAME_COLOR;
      currentColor = ENDGAME_COLOR;
    }
    pushDashboardValues();
  }

  public void setEnabled(int enabled) {
    // ENABLED = enabled;
    System.out.println(enabled);
    if (enabled == 0) {
      arduino.write(0, 0);
    }
    else {
    arduino.write(0, 1);
    }
    setAllianceColor(enabled);
    setColor(currentColor);
    setFlashing(false, false);
  }
  public void modifyColor(boolean direction) {
    if (direction) {
      setColor(currentColor + 1);
    }
    else {
      setColor(currentColor - 1);
    }
    System.out.print("new color: ");
    System.out.println(currentColor);
  }

  public void setFlashing(boolean flash, boolean reset) {
    if (flash) {
      mode = FLASHING_MODE;
    } else {
      mode = SOLID_MODE;
    }
    if (reset) {
    arduino.write(ENABLED, currentColor + mode);
    pushDashboardValues();
    }
  }

  public void setColor(int color) {
    currentColor = color;
    // System.out.print(ENABLED);
    // System.out.println("called setcolor");
    arduino.write(ENABLED, color + mode);
    pushDashboardValues();
  }

  public void resetColor() {
    setColor(allianceColor);
    setFlashing(false,true);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if (SmartDashboard.getNumber("LightShow Current Color", 0) != currentColor) {setColor((int)Math.round(SmartDashboard.getNumber("LightShow Current Color", currentColor)));}
  }

  public void pushDashboardValues() {
    SmartDashboard.putNumber("LightShow Current Color",currentColor);
    SmartDashboard.putBoolean("LightShow Flashing", (mode == FLASHING_MODE));
    // Elastic.Notification dashboardNotification = new Elastic.Notification(Elastic.NotificationLevel.INFO, "Dashboard Values Updated", "LED Dashboard values have been updated.");
    // Elastic.sendNotification(dashboardNotification);
  }

}
