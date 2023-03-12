import * as RNFS from 'react-native-fs';
import Alarms from '../constants/Alarms';
import SetParameter from '../interfaces/SetParameter';
import DataConfig from '../constants/DataConfig';
import { BreathingPhase } from '../enums/BreathingPhase';
import { log } from './AppLogger';

// TODO: Add serial data packets also
export default function dataLogger() {
  const nowTimeStamp: string = new Date().toISOString().replace(/\.|:/g, '-');
  const logDirectory: string = `${RNFS.ExternalDirectoryPath}/sessions`;
  const logFile: string = `${nowTimeStamp}.csv`;
  const folderCreationPromise = RNFS.mkdir(logDirectory);
  let readingsCsv: string[] = [getDataHeaders()];
  const logFrequency: number =
    (DataConfig.graphLength / DataConfig.dataFrequency) * 1000; // log every time the graph clears

  setInterval(() => {
    if (readingsCsv.length > 0) {
      writeToLogFile(readingsCsv.length);
    }
  }, logFrequency);

  function getDataHeaders() {
    return [
      'Timestamp',
      'Measured Pressure (cmH2O)',
      getSetParameterHeader('Peep', 'cmH2O'),
      getSetParameterHeader('PIP', 'cmH2O'),
      getSetParameterHeader('Plateau Pressure', 'cmH2O'),
      getSetParameterHeader('Patient Rate', 'BPM'),
      getSetParameterHeader('Tidal Volume', 'ml'),
      'I/E Ratio',
      'VTi (ml)',
      'VTe (ml)',
      getSetParameterHeader('Minute Ventilation', 'lpm'),
      getSetParameterHeader('FiO2', '%'),
      'Flow Rate (lpm)',
      'Ventilation Mode',
      'Breathing Phase',
      getAlarmHeaders(),
    ].join(',');
  }

  function getSetParameterHeader(name: string, unit: string) {
    return [
      `${name} Set Value (${unit})`,
      `${name} Measured Value (${unit})`,
      `${name} Lower Limit (${unit})`,
      `${name} Upper Limit (${unit})`,
    ].join(',');
  }

  function getAlarmHeaders() {
    return Alarms.join(',');
  }

  function onDataReading(reading: any) {
    const readingInCsv = getCsvFormat(reading);
    readingsCsv.push(readingInCsv);
  }

  function writeToLogFile(numberOfReadingsAdded: number) {
    const readingsToAdd: string = readingsCsv.join('\n');
    folderCreationPromise.then(() => {
      RNFS.write(`${logDirectory}/${logFile}`, readingsToAdd + '\n')
        .then(() => {
          log.info(`written to ${logFile}`);
          readingsCsv = readingsCsv.slice(numberOfReadingsAdded);
        })
        .catch((err) => {
          log.error(err.message);
        });
    });
  }

  function getCsvFormat(reading: any): string {
    const {
      peep,
      measuredPressure,
      plateauPressure,
      respiratoryRate,
      tidalVolume,
      ieRatio,
      vti,
      vte,
      minuteVentilation,
      fiO2,
      flowRate,
      pip,
      mode,
      alarms,
      breathingPhase,
    } = reading;
    let readingsString: string = [
      new Date().toISOString(),
      measuredPressure,
      getSetParameterCsvFormat(peep),
      getSetParameterCsvFormat(pip),
      getSetParameterCsvFormat(plateauPressure),
      getSetParameterCsvFormat(respiratoryRate),
      getSetParameterCsvFormat(tidalVolume),
      ieRatio,
      vti,
      vte,
      getSetParameterCsvFormat(minuteVentilation),
      getSetParameterCsvFormat(fiO2),
      flowRate,
      mode,
      BreathingPhase[breathingPhase],
      getAlarmsInCsvFormat(alarms),
    ].join(',');
    return readingsString;
  }

  function getSetParameterCsvFormat(paramter: SetParameter) {
    return [
      paramter.setValueText || paramter.setValue,
      paramter.value,
      paramter.lowerLimit,
      paramter.upperLimit,
    ].join(',');
  }

  function getAlarmsInCsvFormat(alarms: string[]): string {
    let alarmPresenceArray: boolean[] = new Array(Alarms.length).fill(false);
    for (let i = 0; i < Alarms.length; i++) {
      if (alarms.includes(Alarms[i])) {
        alarmPresenceArray[i] = true;
      }
    }
    return alarmPresenceArray.join(',');
  }

  return {
    onDataReading,
  };
}
