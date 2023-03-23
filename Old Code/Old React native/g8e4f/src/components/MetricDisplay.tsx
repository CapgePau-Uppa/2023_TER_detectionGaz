import * as React from 'react';
import { Text, View } from 'react-native';
import Colors from '../constants/Colors';

export default function MetricDisplay(props: any) {
  return (
    <View>
      <Text style={{ color: Colors.textColor, alignSelf: 'center' }}>
        {props.title}
      </Text>
      <Text
        style={{
          alignSelf: 'center',
          fontSize: 25,
          color: Colors.valueColor,
        }}>
        {props.value != null ? parseFloat(props.value).toFixed(1) : '-'}{' '}
        <Text style={{ alignSelf: 'center', fontSize: 15 }}>{props.unit}</Text>
      </Text>
    </View>
  );
}

export function MetricDisplayString(props: any) {
  return (
    <View>
      <Text style={{ color: Colors.textColor, alignSelf: 'center' }}>
        {props.title}
      </Text>
      <Text
        style={{
          alignSelf: 'center',
          fontSize: 25,
          color: Colors.valueColor,
        }}>
        {props.value}{' '}
        <Text style={{ alignSelf: 'center', fontSize: 15 }}>{props.unit}</Text>
      </Text>
    </View>
  );
}
