// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.LedSubsystem;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class LedColorCommand extends Command {
  @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
  private final LedSubsystem m_subsystem;
  private int currentMode;
  private int state;
  private boolean flash;

  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */
  public LedColorCommand(LedSubsystem subsystem, int mode, int color, boolean flashing) {
    m_subsystem = subsystem;
    currentMode = mode;
    state = color;
    flash = flashing;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (currentMode == 1) {
    m_subsystem.setColor(state);
    //remove for debug System.out.println(state);
    }
    else if (currentMode == 2) {
    m_subsystem.setFlashing(flash,true);
    //remove for debug System.out.println(flash);
    }
    else {
      m_subsystem.modifyColor(flash);
      //remove for debug System.out.println(flash);
    }
  }
  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
