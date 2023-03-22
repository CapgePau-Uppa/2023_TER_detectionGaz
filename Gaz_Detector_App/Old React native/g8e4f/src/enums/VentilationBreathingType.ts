export enum VentilationBreathingType {
  Mandatory = 0,
  Assisted = 1,
}

export class VentilationBreathingTypeUtils {
  public static getDisplayString(
    breathingType: VentilationBreathingType,
  ): string {
    if (breathingType === VentilationBreathingType.Assisted) {
      return 'AC-';
    } else {
      return 'V';
    }
  }
}
