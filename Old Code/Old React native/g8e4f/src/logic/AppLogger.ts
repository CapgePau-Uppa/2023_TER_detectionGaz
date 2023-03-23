//config.js
import { logger, configLoggerType } from 'react-native-logs';
import { rnFsFileAsync } from 'react-native-logs/dist/transports/rnFsFileAsync';
import { ansiColorConsoleSync } from 'react-native-logs/dist/transports/ansiColorConsoleSync';
import * as RNFS from 'react-native-fs';

console.log('starting global logger');
const nowTimeStamp: string = new Date().toISOString().replace(/\.|:/g, '-');
const logDirectory: string = `${RNFS.ExternalDirectoryPath}/app-logs`;
RNFS.mkdir(logDirectory);

const config: configLoggerType = {
  transport: (msg, level, options) => {
    ansiColorConsoleSync(msg, level, options);
    rnFsFileAsync(msg, level, {
      loggerName: `app-log-${nowTimeStamp}`,
      loggerPath: `${logDirectory}`,
    });
  },
};

var log = logger.createLogger(config);

if (__DEV__) {
  log.setSeverity('debug');
} else {
  log.setSeverity('info');
}

export { log };
