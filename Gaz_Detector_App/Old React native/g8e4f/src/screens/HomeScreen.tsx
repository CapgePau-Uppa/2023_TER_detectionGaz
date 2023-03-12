import * as React from 'react';
import { StyleSheet, View, Text } from 'react-native';
import PressureDisplay from '../components/PressureDisplay';
import Graphs from '../components/Graphs';
import MetricDisplay from '../components/MetricDisplay';
import { useReading } from '../logic/useReading';
import { MetricDisplayString } from '../components/MetricDisplay';
import Colors from '../constants/Colors';
import TidalVolumeMetricDisplay from '../components/TidalVolumeMetricDisplay';

export default function HomeScreen() {
  const reading = useReading();
  const readingValues = reading.values;

  return (
    <View style={styles.container}>
      <View style={styles.pressureDisplay}>
        <PressureDisplay
          measuredPressure={readingValues.measuredPressure}
          peep={readingValues.peep}
          pip={readingValues.pip}
          plateauPressure={readingValues.plateauPressure}
        />
      </View>
      <View style={styles.graphs}>
        <Text style={styles.graphTitle}>Pressure [cmH20]</Text>
        <View style={{ flex: 1, paddingTop: 0, paddingBottom: 0 }}>
          <Graphs
            data={readingValues.graphPressure}
            yMin={-30}
            yMax={60}
            numberOfTicks={4}
            fillColor={Colors.graphPressure}
            strokeColor={Colors.graphPressureStrokeColor}
          />
        </View>
        <Text style={styles.graphTitle}>Tidal Volume [ml]</Text>
        <View style={{ flex: 1, paddingTop: 0, paddingBottom: 0 }}>
          <Graphs
            data={readingValues.graphVolume}
            yMin={-250}
            yMax={600}
            numberOfTicks={4}
            fillColor={Colors.graphVolume}
            strokeColor={Colors.graphVolumeStrokeColor}
          />
        </View>
        <Text style={styles.graphTitle}>Flow Rate [lpm]</Text>
        <View style={{ flex: 1, paddingTop: 0, paddingBottom: 0 }}>
          <Graphs
            data={readingValues.graphFlow}
            yMin={-110}
            yMax={100}
            numberOfTicks={4}
            fillColor={Colors.graphFlow}
            strokeColor={Colors.graphFlowStrokeColor}
            markers={readingValues.breathMarkers}
          />
        </View>
      </View>
      <View style={styles.configuredValues}>
        <MetricDisplay
          style={styles.configuredValueDisplay}
          title={readingValues.fiO2.name}
          value={readingValues.fiO2.value}
          unit={readingValues.fiO2.unit}
        />
        <MetricDisplay
          style={styles.configuredValueDisplay}
          title={readingValues.respiratoryRate.name}
          value={readingValues.respiratoryRate.value}
          unit={readingValues.respiratoryRate.unit}
        />
        <MetricDisplayString
          style={styles.configuredValueDisplay}
          title={'I:E Ratio'}
          value={readingValues.ieRatio}
          unit={''}
        />
        <TidalVolumeMetricDisplay
          style={styles.configuredValueDisplay}
          parameter={readingValues.tidalVolume}
          ventilationMode={readingValues.mode}
        />
        <MetricDisplay
          style={styles.configuredValueDisplay}
          title={'VTi'}
          value={readingValues.vti}
          unit={'ml'}
        />
        <MetricDisplay
          style={styles.configuredValueDisplay}
          title={'VTe'}
          value={readingValues.vte}
          unit={'ml'}
        />
        <MetricDisplay
          style={styles.configuredValueDisplay}
          title={readingValues.minuteVentilation.name}
          value={readingValues.minuteVentilation.value}
          unit={readingValues.minuteVentilation.unit}
        />
        <MetricDisplayString
          style={styles.configuredValueDisplay}
          title={'Ventilation Mode'}
          value={readingValues.mode}
          unit={''}
        />
      </View>
    </View>
  );
}

HomeScreen.navigationOptions = {
  header: null,
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    flex: 1,
    flexDirection: 'row',
    alignItems: 'stretch',
    backgroundColor: Colors.generalBackGround,
    padding: 2,
  },
  pressureDisplay: {
    flex: 1,
    height: '100%',
    backgroundColor: Colors.generalBackGround,
    flexDirection: 'column',
    borderWidth: 2,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
    borderColor: Colors.borders,
    margin: 1,
  },
  configuredValues: {
    borderWidth: 2,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
    borderColor: Colors.borders,
    flex: 1,
    flexDirection: 'column',
    justifyContent: 'space-around',
    margin: 1,
    height: '100%',
  },
  configuredValueDisplay: {
    flex: 0.2,
  },
  graphTitle: {
    color: Colors.textColor,
    textAlign: 'center',
  },
  graphs: {
    flex: 8,
    flexDirection: 'column',
    justifyContent: 'space-around',
    borderWidth: 2,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    borderBottomRightRadius: 20,
    borderBottomLeftRadius: 20,
    borderColor: Colors.borders,
    height: '100%',
    margin: 1,
  },
});
