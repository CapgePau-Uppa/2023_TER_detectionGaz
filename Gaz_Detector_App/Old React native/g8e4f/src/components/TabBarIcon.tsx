import Icon from 'react-native-vector-icons/Ionicons';
import * as React from 'react';

import Colors from '../constants/Colors';

export default function TabBarIcon(props: any) {
  return (
    <Icon
      name={props.name}
      size={30}
      style={{ marginBottom: -3 }}
      color={props.focused ? Colors.tabIconSelected : Colors.tabIconDefault}
    />
  );
}
