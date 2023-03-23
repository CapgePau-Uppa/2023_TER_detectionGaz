import React, { Component } from "react";
import {
  StyleSheet,
  Text,
  View,
  TextInput,
  TouchableOpacity,
  ScrollView,
  Alert,
  DeviceEventEmitter
} from "react-native";
//import {  definitions, actions } from "react-native-serialport";

import SerialPortAPI from 'react-native-serial-port-api';

async function example() {
  const serialPort = await SerialPortAPI.open("/dev/ttyS4", { baudRate: 38400 });

  // subscribe received data
  const sub = serialPort.onReceived(buff => {
    console.log(buff.toString('hex').toUpperCase());
  })

  // unsubscribe
  // sub.remove();

  // send data with hex format
  await serialPort.send('00FF');

  // close
  serialPort.close();
}

//type Props = {};
class ManualConnection extends Component {
  constructor(props) {
    super(props);

    this.state = {
      servisStarted: false,
      connected: false,
      usbAttached: false,
      output: "",
      outputArray: [],
      baudRate: "115200",
      interface: "-1",
      sendText: "HELLO"
    };

    example();

    //this.startUsbListener = this.startUsbListener.bind(this);
    //this.stopUsbListener = this.stopUsbListener.bind(this);
  }


  render() {
    return (
      <Text style={styles.value}>
        {this.state.connected ? "Connected" : "Not Connected"}
      </Text>
    );
  }
}

const styles = StyleSheet.create({
  full: {
    flex: 1
  },
  body: {
    flex: 1
  },
  container: {
    flex: 1,
    marginTop: 20,
    marginLeft: 16,
    marginRight: 16
  },
  header: {
    display: "flex",
    justifyContent: "center"
    //alignItems: "center"
  },
  line: {
    display: "flex",
    flexDirection: "row"
  },
  line2: {
    display: "flex",
    flexDirection: "row",
    justifyContent: "space-between"
  },
  title: {
    width: 100
  },
  value: {
    marginLeft: 20
  },
  output: {
    marginTop: 10,
    height: 300,
    padding: 10,
    backgroundColor: "#FFFFFF",
    borderWidth: 1
  },
  inputContainer: {
    marginTop: 10,
    borderBottomWidth: 2
  },
  textInput: {
    paddingLeft: 10,
    paddingRight: 10,
    height: 40
  },
  button: {
    marginTop: 16,
    marginBottom: 16,
    paddingLeft: 15,
    paddingRight: 15,
    height: 40,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#147efb",
    borderRadius: 3
  },
  buttonText: {
    color: "#FFFFFF"
  }
});

export default ManualConnection;
