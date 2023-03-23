import {FC, createContext, useContext, useRef, useMemo, useEffect, useCallback} from 'react'
import {Subject, Subscription} from 'rxjs'
import {DeviceEventEmitter} from 'react-native'
import {actions, RNSerialport, definitions, ReturnedDataTypes} from 'react-native-serialport'

import {BAUD_RATE, INTERFACE} from './constants'

const SerialContext = createContext({
  subscribe: () => {
    return {}
  },
})

export const SerialProvider = (props) => {
  const {children} = props

  const serviceStarted = useRef(false)
  const usbAttached = useRef(false)
  const connected = useRef(false)
  const subject = useRef(new Subject())

  const manager = useMemo(() => {
    return {
      subscribe: (callback) => {
        return subject.current.subscribe(callback)
      },
    }
  }, [])

  const handleOnDeviceAttached = useCallback(() => {
    usbAttached.current = true
  }, [])

  const handleOnDeviceDetached = useCallback(() => {
    usbAttached.current = false
  }, [])

  const handleOnServiceStarted = useCallback(
    (res) => {
      const {deviceAttached} = res

      serviceStarted.current = true

      if (deviceAttached) {
        handleOnDeviceAttached()
      }
    },
    [handleOnDeviceAttached],
  )

  const handleOnServiceStopped = useCallback(() => {
    serviceStarted.current = false
  }, [])

  const handleOnError = useCallback((err) => {
    console.log(err)
  }, [])

  const handleOnConnected = useCallback(() => {
    connected.current = true
  }, [])

  const handleOnDisconnected = useCallback(() => {
    connected.current = false
  }, [])

  const handleOnReadData = useCallback((data) => {
    const {payload} = data

    const event = {
      type: 'ON_READ_DATA',
      payload,
    }

    subject.current.next(event)
  }, [])

  useEffect(() => {
    DeviceEventEmitter.addListener(actions.ON_SERVICE_STARTED, handleOnServiceStarted)
    DeviceEventEmitter.addListener(actions.ON_SERVICE_STOPPED, handleOnServiceStopped)
    DeviceEventEmitter.addListener(actions.ON_DEVICE_ATTACHED, handleOnDeviceAttached)
    DeviceEventEmitter.addListener(actions.ON_DEVICE_DETACHED, handleOnDeviceDetached)
    DeviceEventEmitter.addListener(actions.ON_ERROR, handleOnError)
    DeviceEventEmitter.addListener(actions.ON_CONNECTED, handleOnConnected)
    DeviceEventEmitter.addListener(actions.ON_DISCONNECTED, handleOnDisconnected)
    DeviceEventEmitter.addListener(actions.ON_READ_DATA, handleOnReadData)

    return () => {
      DeviceEventEmitter.removeAllListeners()
    }
  }, [
    handleOnServiceStarted,
    handleOnServiceStopped,
    handleOnDeviceAttached,
    handleOnDeviceDetached,
    handleOnError,
    handleOnConnected,
    handleOnDisconnected,
    handleOnReadData,
  ])

  useEffect(() => {
    RNSerialport.setReturnedDataType(definitions.RETURNED_DATA_TYPES.INTARRAY)
    RNSerialport.setAutoConnectBaudRate(BAUD_RATE)
    RNSerialport.setInterface(INTERFACE)
    RNSerialport.setAutoConnect(true)
    RNSerialport.startUsbService()

    return () => {
      RNSerialport.isOpen()
        .then((opened) => {
          if (opened) {
            RNSerialport.disconnect()
          }
          RNSerialport.stopUsbService()
        })
        .catch((e) => {
          console.log('RNSerialport isOpen failed', e)
        })
    }
  }, [])

  return <SerialContext.Provider value={manager}>{children}</SerialContext.Provider>
}

export const useSerial = () => useContext(SerialContext)