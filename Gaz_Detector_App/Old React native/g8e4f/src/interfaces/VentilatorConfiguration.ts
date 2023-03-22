import SetParameter from './SetParameter';
import { VentilationMode } from '../models/VentilationMode';
import { IERatio } from '../models/IERatio';

export default interface VentilatorConfiguration {
  ventilationMode: VentilationMode;
  peep: SetParameter;
  fiO2: SetParameter;
  tidalVolume: SetParameter;
  ieRatio: IERatio;
  flow: SetParameter;
  respiratoryRate: SetParameter;
  pressureControl: SetParameter;
  peakPressure: SetParameter;
  plateauPressure: SetParameter;
}
