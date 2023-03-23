export enum VentilationControlMode {
  VolumeControlled = 0,
  PressureControlled = 1,
}

export class VentilationControlModeUtils {
  public static getDisplayString(controlMode: VentilationControlMode): string {
    if (controlMode === VentilationControlMode.PressureControlled) {
      return 'PC';
    } else {
      return 'VC';
    }
  }
}
