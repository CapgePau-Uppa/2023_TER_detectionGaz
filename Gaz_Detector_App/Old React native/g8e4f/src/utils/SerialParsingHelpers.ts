import Alarms from '../constants/Alarms';

export function getAlarmValues(serialData: Array<number>): Array<string> {
  let alarms: Array<string> = [];
  var bits = 8;
  var alarmIndices = [27, 41, 42, 43];
  for (
    let alarmIndex = 0;
    alarmIndex < bits * alarmIndices.length;
    alarmIndex++
  ) {
    let alarmIndexToCheck = Math.floor(alarmIndex / bits);
    let valueByteToCheckIndex = alarmIndices[alarmIndexToCheck];
    let valueToCheck = serialData[valueByteToCheckIndex];
    let bitIndexToCheck = alarmIndex % bits;
    let isAlarmActive = getValueOfBit(valueToCheck, bitIndexToCheck);
    if (isAlarmActive) {
      alarms.push(Alarms[alarmIndex]);
    }
  }
  return alarms;
}

function getValueOfBit(valueToParse: number, bitIndex: number) {
  const bitIndexNumberForFindingValue = [1, 2, 4, 8, 16, 32, 64, 128];
  return valueToParse & bitIndexNumberForFindingValue[bitIndex];
}
