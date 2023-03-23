import { showMessage, hideMessage } from 'react-native-flash-message';
import Sound from 'react-native-sound';
import Layout from '../constants/Layout';
import { log } from './AppLogger';

Sound.setCategory('Playback');

function AlarmsManager() {
  let currentAlarms: Array<string> = [];
  let alarmSound = new Sound('ventilator_alarm.mp3');

  function onNewReading(reading: any) {
    const newAlarms: any = reading.alarms;
    if (newAlarms === undefined) {
      return;
    }
    if (changeInAlarms(newAlarms)) {
      const isDecreaseInSameAlarms: boolean = decreaseInSameAlarms(newAlarms);
      currentAlarms = newAlarms;
      handleCurrentAlarms(currentAlarms, isDecreaseInSameAlarms);
    }
  }

  function handleCurrentAlarms(
    alarms: Array<string>,
    isDecreaseInSameAlarms: boolean,
  ): void {
    if (alarms.length === 0) {
      hideMessage();
      alarmSound.stop();
    } else {
      const shouldPlaySound: boolean =
        !isDecreaseInSameAlarms || highPriorityAlarmsRaised();
      displayAlarmsBanner(shouldPlaySound);
    }
  }

  function displayAlarmsBanner(shouldPlaySound: boolean): void {
    const alarmsText = currentAlarms.join('\n');
    if (shouldPlaySound) {
      alarmSound.play();
    }
    alarmSound.setNumberOfLoops(-1);
    const widthForBanner = Layout.window.width * 0.9;
    const textAlign = 'center';
    showMessage({
      message: 'Alarm(s) active',
      description: alarmsText,
      type: 'danger',
      icon: 'warning',
      autoHide: false,
      hideOnPress: false,
      onPress: () => {
        // TODO: Create complex alarms object to check priority value instead
        if (!highPriorityAlarmsRaised()) {
          alarmSound.stop();
        }
      },
      titleStyle: { textAlign: textAlign, width: widthForBanner, fontSize: 20 },
      textStyle: { textAlign: textAlign, width: widthForBanner, fontSize: 16 },
    });
  }

  function changeInAlarms(newAlarms: Array<string>): boolean {
    if (currentAlarms.length !== newAlarms.length) {
      return true;
    } else {
      return !currentAlarms.every((value, index) => {
        return value === newAlarms[index];
      });
    }
  }

  function decreaseInSameAlarms(newAlarms: string[]): boolean {
    return newAlarms.every((alarm) => {
      return currentAlarms.includes(alarm);
    });
  }

  function highPriorityAlarmsRaised(): boolean {
    return currentAlarms.includes('High Peak Pressure');
  }

  return {
    onNewReading,
  };
}

export default AlarmsManager();
