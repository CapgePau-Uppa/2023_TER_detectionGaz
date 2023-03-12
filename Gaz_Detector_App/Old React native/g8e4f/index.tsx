import { AppRegistry } from 'react-native';
import App from './src/App';
import { name } from './app.json';
import { log } from './src/logic/AppLogger';

log.info(name);
AppRegistry.registerComponent(name, () => App);
