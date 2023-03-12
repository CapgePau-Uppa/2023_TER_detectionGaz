import SerialPort from 'react-native-serialport';

class MyComponent extends Component {
  constructor(props) {
    super(props);
    this.state = { gasValue: 0 };
  }

  componentDidMount() {
    SerialPort.list().then(ports => {
      console.log('Available serial ports:', ports);
      const port = ports[0].path;
      SerialPort.open(port, { baudRate: 9600 }).then(() => {
        console.log('Serial port opened:', port);
        SerialPort.on('data', this.handleData);
      });
    });
  }

  componentWillUnmount() {
    SerialPort.removeListener('data', this.handleData);
    SerialPort.close();
  }

  handleData = data => {
    this.setState({ gasValue: parseInt(data) });
  };

  render() {
    return <Text>{this.state.gasValue}</Text>;
  }
}