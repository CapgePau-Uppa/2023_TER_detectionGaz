import VentilatorConfiguration from '../interfaces/VentilatorConfiguration';
import { VentilationMode } from '../models/VentilationMode';
import { VentilationBreathingType } from '../enums/VentilationBreathingType';
import { VentilationControlMode } from '../enums/VentilationControlMode';
import { IERatio } from '../models/IERatio';

const initalVentilatorConfiguration: VentilatorConfiguration = {
  ventilationMode: new VentilationMode(
    VentilationBreathingType.Assisted,
    VentilationControlMode.PressureControlled,
  ),
  peep: {
    name: 'PEEP',
    upperLimit: 20,
    lowerLimit: 5,
    unit: 'cmH2O',
    setValue: 10,
    value: 10,
  },
  fiO2: {
    name: 'Oxygen',
    upperLimit: 100,
    lowerLimit: 50,
    unit: '%',
    setValue: 75,
    value: 75,
  },
  tidalVolume: {
    name: 'Tidal Volume',
    upperLimit: 700,
    lowerLimit: 200,
    unit: 'mL',
    setValue: 500,
    value: 500,
  },
  flow: {
    name: 'Flow',
    upperLimit: 10,
    lowerLimit: -10,
    unit: 'mL/s',
    setValue: 0,
    value: 0,
  },
  ieRatio: new IERatio(
    {
      name: 'inspiratory',
      upperLimit: 1,
      lowerLimit: 1,
      unit: '',
      setValue: 1,
      value: 1,
    },
    {
      name: 'expiratory',
      upperLimit: 3,
      lowerLimit: 1,
      unit: '',
      setValue: 1,
      value: 1,
    },
  ),
  respiratoryRate: {
    name: 'Respiratory Rate',
    upperLimit: 35,
    lowerLimit: 8,
    unit: 'BPM',
    setValue: 20,
    value: 20,
  },
  pressureControl: {
    name: 'Pressure',
    upperLimit: 40,
    lowerLimit: 0,
    unit: 'cmH2O',
    setValue: 10,
    value: 10,
  },
  peakPressure: {
    name: 'Peak Pressure',
    upperLimit: 20,
    lowerLimit: 5,
    unit: 'cmH2O',
    setValue: 10,
    value: 10,
  },
  plateauPressure: {
    name: 'Plateau Pressure',
    upperLimit: 20,
    lowerLimit: 5,
    unit: 'cmH2O',
    setValue: 10,
    value: 10,
  },
};

export default initalVentilatorConfiguration;
