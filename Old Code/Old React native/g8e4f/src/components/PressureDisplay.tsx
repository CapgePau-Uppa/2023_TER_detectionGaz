import * as React from 'react';
import { View, StyleSheet } from 'react-native';
import { BarChart, Grid, YAxis } from 'react-native-svg-charts';
import MetricDisplay from './MetricDisplay';
import Colors from '../constants/Colors';
import InitialReading from '../constants/InitialReading';

export default function PressureDisplay({
  measuredPressure,
  peep,
  pip,
  plateauPressure,
}: any) {
  return (
    <View style={{ height: '100%' }}>
      <MetricDisplay
        value={measuredPressure}
        title={'Pressure'}
        unit={'cmH2O'}
      />
      <View style={{}}>
        <View style={styles.peepGaugeWithAxis}>
          <YAxis
            data={[0, InitialReading.pressureGraph.upperLimit]}
            contentInset={{ top: 4, bottom: 3 }}
            svg={{
              fill: Colors.textColor,
              fontSize: 10,
            }}
            numberOfTicks={6}
            formatLabel={(value: number) => `${value}`}
            style={{ flex: 1 }}
          />

          <BarChart
            style={styles.peepGauge}
            yMin={InitialReading.pressureGraph.lowerLimit}
            yMax={InitialReading.pressureGraph.upperLimit}
            data={[measuredPressure]}
            svg={{ fill: Colors.barColor }}
            animate={true}
            numberOfTicks={6}>
            <Grid svg={{ stroke: Colors.gridLines }}></Grid>
          </BarChart>
          <View style={{ flex: 1 }}></View>
        </View>
        <View
          style={{
            justifyContent: 'space-around',
            flexDirection: 'column',
          }}>
          <MetricDisplay
            style={styles.peep}
            value={pip.value}
            title={pip.name}
            unit={pip.unit}
          />
          <MetricDisplay
            style={styles.peep}
            title={plateauPressure.name}
            value={plateauPressure.value}
            unit={plateauPressure.unit}
          />
          <MetricDisplay
            style={styles.peep}
            value={peep.value}
            title={peep.name}
            unit={peep.unit}
          />
        </View>
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  peepGaugeWithAxis: {
    flexDirection: 'row',
    height: '59%',
    paddingTop: 10,
    paddingBottom: 20,
    padding: 5,
    flexGrow: 1,
    borderWidth: 2,
    borderColor: Colors.generalBackGround,
    borderBottomColor: Colors.borders,
    justifyContent: 'space-around',
  },
  peepGauge: {
    paddingLeft: 5,
    paddingRight: 5,
    borderWidth: 2,
    borderColor: Colors.borders,
    flex: 1,
  },
  peep: {
    flex: 1,
  },
});
