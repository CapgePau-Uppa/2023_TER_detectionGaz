import { processSerialData } from './SerialParser';
import { log } from './AppLogger';

export default function dummyDataGenerator(
  updateReadingStateFunction: (value: any) => void,
  dataFrequency: number,
) {
  let intervalFunction: number;
  const intervalFrequency = 1000 / dataFrequency;
  let data: string = '';
  let counter = 0;

  function generateDummyReadings() {
    let dataPacket = new Array(49);
    if (counter < data.length) {
      for (let i = 0; i < 49; i++) {
        dataPacket[i] = data.substring(counter, counter + 1).charCodeAt(0);
        counter++;
      }
      processSerialData(dataPacket, updateReadingStateFunction);
    }
  }

  function startGenerating() {
    log.info('starting generator');
    var RNFS = require('react-native-fs');
    RNFS.readFileAssets('sample_data.txt', 'ascii').then((result: any) => {
      data = result;
    });
    intervalFunction = setInterval(() => {
      generateDummyReadings();
    }, intervalFrequency);
  }

  function stopGenerating() {
    clearInterval(intervalFunction);
  }

  return {
    startGenerating,
    stopGenerating,
  };
}
