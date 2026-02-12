package frc.robot.commands;

import frc.robot.Constants.COLORS;
import frc.robot.Constants.DASHBOARD;
import frc.robot.Constants.TIMES;
import frc.robot.subsystems.LEDSubsystem;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

public class ShiftColorsCommand extends Command {

  private final LEDSubsystem ledSub;
  private int shiftCount;
  private String initialColor;
  private boolean shiftStarting;
  private Timer timer;
  private double shiftTime;

  public ShiftColorsCommand(LEDSubsystem subsystem) {
    ledSub = subsystem;
    timer = new Timer();        
    addRequirements(subsystem);    
  }

  @Override
  public void initialize() {

    this.initialColor = "";
    shiftCount = 0;
    ledSub.resetShiftColor();    
    shiftTime = TIMES.INITIAL_SHIFT;
    timer.reset();
    timer.start();
    shiftStarting = false;

    // set initial color
    ledSub.setEnabled(true);    
    ledSub.setColor(COLORS.WHITE);    
    ledSub.setFlashing(false);
    ledSub.setDashboardColor();

  }

  @Override
  public void execute() {

    

    // update dashboard
    SmartDashboard.putNumber(DASHBOARD.SHIFT_TIME, (shiftTime - timer.get()));

    // has inactive hub color been set?
    if (!initialColor.equals("B") && 
        !initialColor.equals("R") && 
        initialColor.isEmpty()) {
   
      // get hub inactive color
      initialColor = DriverStation.getGameSpecificMessage();

    } else {

      if (timer.hasElapsed(shiftTime - TIMES.FLASH_WARNING) && !shiftStarting)
      {
        // falsh warning
        ledSub.setShiftColor(initialColor);   
        ledSub.setFlashing(true);     
        shiftStarting = true; // only set flashing once
      }

      if (timer.hasElapsed(shiftTime))
      {
        // set shift color
        ledSub.setDashboardColor();
        ledSub.setFlashing(false);
        timer.restart();
        shiftStarting = false;
        shiftTime = TIMES.HUB_SHIFT; // change to 25 seconds after initial 10 second round
        shiftCount++;
      }      

    }
  }

  @Override
  public void end(boolean interrupted) {
    timer.stop();
  }

  @Override
  public boolean isFinished() {
    return shiftCount > TIMES.MAX_SHIFTS || DriverStation.isDisabled();
  }
}
