import { DeviceEventEmitter } from 'react-native';
import {
  RNSerialport,
  definitions,
  actions,
  ReturnedDataTypes,
} from 'react-native-serialport';
import DataConfig from '../constants/DataConfig';
import { processSerialData } from './SerialParser';
import { log } from './AppLogger';
export default function SerialDataHandler(
  serialParameters: any,
  updateReadingStateFunction: (value: any) => void,
) {
  let SerialBuffer = new Array(0);

  //to get values from two bytes
  let state = {
    serviceStarted: false,
    connected: false,
    usbAttached: false,
    output: '',
    outputArray: [],
    baudRate: serialParameters.baudRate,
    interface: '-1',
    returnedDataType: <ReturnedDataTypes>(
      definitions.RETURNED_DATA_TYPES.INTARRAY
    ),
  };

  function onServiceStarted(response: any) {
    state.serviceStarted = true;
    if (response.deviceAttached) {
      onDeviceAttached();
    }
  }
  function onServiceStopped() {
    state.serviceStarted = false;
  }
  function onDeviceAttached() {
    console.warn('Device attached');
    state.usbAttached = true;
  }
  function onDeviceDetached() {
    state.usbAttached = false;
  }
  function onConnected() {
    state.connected = true;
  }
  function onDisconnected() {
    state.connected = false;
  }
  function onError(error: any) {
    console.error(error);
  }

  function onReadData(data: any) {
    let RemainingData = 0;

        // var RNFS = require('react-native-fs');

    // create a path you want to write to
    // :warning: on iOS, you cannot write into `RNFS.MainBundlePath`,
    // but `RNFS.DocumentDirectoryPath` exists on both platforms and is writable
    // var path = RNFS.DocumentDirectoryPath + '/logs.txt';

    // // write the file
    // RNFS.writeFile(path, data.payload, 'ascii').catch((err) => {
    //   console.log(err.message);
    // });

    if (state.returnedDataType === definitions.RETURNED_DATA_TYPES.INTARRAY) {
      if (SerialBuffer.length > 0) {
        if (
          data.payload.length >=
          DataConfig.totalPacketLength - SerialBuffer.length
        ) {
          RemainingData = data.payload.splice(
            0,
            DataConfig.totalPacketLength - SerialBuffer.length,
          );

          SerialBuffer = SerialBuffer.concat(RemainingData);
          processSerialData(SerialBuffer, updateReadingStateFunction);
          SerialBuffer = [];
        } else {
          SerialBuffer = SerialBuffer.concat(data.payload);
        }
      } else {
        while (data.payload.length > 0) {
          if (
            data.payload[0] == 0x24 &&
            data.payload[1] == 0x4f &&
            data.payload[2] == 0x56 &&
            data.payload[3] == 0x50
          ) {
            if (data.payload.length >= DataConfig.totalPacketLength) {
              RemainingData = data.payload.splice(
                0,
                DataConfig.totalPacketLength,
              );
              SerialBuffer = SerialBuffer.concat(RemainingData);
              processSerialData(SerialBuffer, updateReadingStateFunction);
              SerialBuffer = [];
            } else {
              SerialBuffer = SerialBuffer.concat(RemainingData);
              data.payload = [];
            }
          } else {
            data.payload.splice(0, 1);
          }
        }
      }
    }
  }

  async function startUsbListener() {
    DeviceEventEmitter.addListener(
      actions.ON_SERVICE_STARTED,
      onServiceStarted,
    );
    DeviceEventEmitter.addListener(
      actions.ON_SERVICE_STOPPED,
      onServiceStopped,
    );
    DeviceEventEmitter.addListener(
      actions.ON_DEVICE_ATTACHED,
      onDeviceAttached,
    );
    DeviceEventEmitter.addListener(
      actions.ON_DEVICE_DETACHED,
      onDeviceDetached,
    );
    DeviceEventEmitter.addListener(actions.ON_ERROR, onError);
    DeviceEventEmitter.addListener(actions.ON_CONNECTED, onConnected);
    DeviceEventEmitter.addListener(actions.ON_DISCONNECTED, onDisconnected);
    DeviceEventEmitter.addListener(actions.ON_READ_DATA, onReadData);
    RNSerialport.setReturnedDataType(state.returnedDataType);
    RNSerialport.setAutoConnectBaudRate(parseInt(state.baudRate, 10));
    RNSerialport.setInterface(parseInt(state.interface, 10));
    RNSerialport.setAutoConnect(true);
    RNSerialport.startUsbService();
    log.info('started usb service');
  }

  async function stopUsbListener() {
    DeviceEventEmitter.removeAllListeners();
    const isOpen = RNSerialport.isOpen();
    if (isOpen) {
      RNSerialport.disconnect();
    }
    RNSerialport.stopUsbService();
  }

  return {
    startUsbListener,
    stopUsbListener,
    state,
  };
}
