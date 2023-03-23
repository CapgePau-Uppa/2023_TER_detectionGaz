import {
  VentilationBreathingType,
  VentilationBreathingTypeUtils,
} from '../enums/VentilationBreathingType';
import {
  VentilationControlMode,
  VentilationControlModeUtils,
} from '../enums/VentilationControlMode';

export class VentilationMode {
  constructor(
    public breathingType: VentilationBreathingType,
    public controlMode: VentilationControlMode,
  ) {}

  public toString = (): string => {
    const breathingTypeString: string = VentilationBreathingTypeUtils.getDisplayString(
      this.breathingType,
    );
    const controlModeString: string = VentilationControlModeUtils.getDisplayString(
      this.controlMode,
    );

    if (this.breathingType === VentilationBreathingType.Assisted) {
      return `${breathingTypeString}${controlModeString}`;
    } else {
      return `${controlModeString}${breathingTypeString}`;
    }
  };
}
