package frc.robot.subsystems;

import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.COLORS;

public class LEDSubsystem extends SubsystemBase {

  private final I2C arduino;
  private int allianceColor;
  private int currentColor;
  private int mode;
  private int shiftColor;

  // local constants
  private static final int ADDRESS_STATUS = 0;
  private static final int ADDRESS_COLOR = 1;
  private static final int ENABLED = 1;
  private static final int DISABLED = 0;
  private static final int SOLID_MODE = 0;
  private static final int FLASHING_MODE = 30;

  public LEDSubsystem() {
    arduino = new I2C(I2C.Port.kOnboard, 0x08);
    arduino.write(ADDRESS_STATUS, ENABLED);
    setEnabled(true);
    setAllianceColor();
    mode = SOLID_MODE;
    shiftColor = -1;
  }

  public void setShiftColor(String initColor) {
    int color = COLORS.BLUE;
    if(initColor.equals("R")) {
      color = COLORS.RED;
    }

    if (shiftColor == -1) {
      shiftColor = color;
    } else if (shiftColor == COLORS.BLUE) {
      shiftColor = COLORS.RED;
    } else {
      shiftColor = COLORS.BLUE;
    }

    if(shiftColor != allianceColor) {
      resetColor();
    } else {
      setColor(shiftColor);
    }
  }

  public void setEnabled(boolean Enabled) {
    if (Enabled == true) {
      arduino.write(ADDRESS_STATUS, ENABLED);
    }
    else
    {
      arduino.write(ADDRESS_STATUS, DISABLED);
    }
  }

  public void setAllianceColor() {
    allianceColor = COLORS.BLUE;
    Optional<Alliance> ally = DriverStation.getAlliance();
    if (ally.isPresent()) {
        if (ally.get() == Alliance.Red) {
            allianceColor = COLORS.RED;
        }
    }
  }

  public void setFlashing(boolean flash) {
    if (flash) {
      mode = FLASHING_MODE;
    } else {
      mode = SOLID_MODE;
    }
    setColor(this.currentColor);
  }

  public void setColor(int color) {
    currentColor = color;
    color = color + mode;
    arduino.write(ADDRESS_COLOR, color);
  }

  public void setDashboardColor() {
    Color color = new Color(255, 255, 255);
    switch (currentColor) { 
      case (COLORS.BLUE):
        color = new Color(0, 0, 255);
        break;
      case (COLORS.RED):
        color = new Color(255, 0, 0);
        break;
      case (COLORS.GREEN):
        color = new Color(0, 255, 0);
        break;
    }
    SmartDashboard.putString("Example Color", color.toHexString());
  }

  public void resetColor() {
    setFlashing(false);
    setColor(COLORS.WHITE);
  }

}
