package frc.robot.commands;

import frc.robot.subsystems.LEDSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class ManageShiftColors extends Command {

  private final LEDSubsystem ledSub;
  private int shiftCount;
  private String initialColor;
  private boolean shiftStarting;
  private Timer timer;
  private double noFmsConstant;

  public ManageShiftColors(LEDSubsystem subsystem) {
    ledSub = subsystem;
    addRequirements(subsystem);
    this.initialColor = "";
    shiftCount = 0;
    timer = new Timer();
    noFmsConstant = 1;
  }

  @Override
  public void initialize() {
    if (DriverStation.isFMSAttached()) {noFmsConstant = 0;}
    timer.start();
    shiftStarting = false;
  }

  @Override
  public void execute() {
    if (!initialColor.equals("B") && !initialColor.equals("R")) {
   
      initialColor = DriverStation.getGameSpecificMessage();

    } else {
      SmartDashboard.putNumber("Shift Time", DriverStation.getMatchTime() - (25 * (4-shiftCount) + 30) + noFmsConstant);
      if (shiftCount == 0 
          && timer.hasElapsed(5) 
          && !shiftStarting)
      {
        ledSub.setShiftColor(initialColor);
        ledSub.setFlashing(true);
        shiftStarting = true;
      }

      if (shiftCount == 0 && timer.hasElapsed(10))
      {
        ledSub.setFlashing(false);
        timer.restart();
        shiftStarting = false;
        shiftCount++;
      } 
      
      if (timer.hasElapsed(20) 
          && !shiftStarting)
      {
        ledSub.setShiftColor(initialColor);
        ledSub.setFlashing(true);
        shiftStarting = true;
      }

      if (timer.hasElapsed(25)) {
        ledSub.setFlashing(false);
        timer.restart();
        shiftStarting = false;
        shiftCount++;
      }

    }
  }

  @Override
  public void end(boolean interrupted) {
    shiftCount = 0;
    timer.stop();
  }

  @Override
  public boolean isFinished() {
    // return DriverStation.isDisabled();
    // return false;
    return shiftCount > 3 || DriverStation.isDisabled();
  }
}
