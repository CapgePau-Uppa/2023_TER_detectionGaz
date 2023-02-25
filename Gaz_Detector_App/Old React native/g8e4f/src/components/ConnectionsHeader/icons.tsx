import React from 'react';
import Octicons from 'react-native-vector-icons/Octicons';
import Entypo from 'react-native-vector-icons/Entypo';
import MaterialCommunityIcons from 'react-native-vector-icons/MaterialCommunityIcons';
import AntDesign from 'react-native-vector-icons/AntDesign';
import Feather from 'react-native-vector-icons/Feather';
import MaterialIcons from 'react-native-vector-icons/MaterialIcons';
import FontAwesome from 'react-native-vector-icons/FontAwesome';
import FontSize from '../../constants/FontSize';

export const ALARMS = {
  'System Reset': {
    status: 'System Running',
    icon: (status: boolean) => {
      switch (status) {
        case true:
          return (
            <MaterialCommunityIcons
              size={FontSize.iconSize}
              name="replay"
              color={'red'}
            />
          );
        case false:
          return (
            <MaterialCommunityIcons
              size={FontSize.iconSize}
              name="play"
              color={'green'}
            />
          );
        default:
          return null;
      }
    },
  },
  'Circuit Integrity Failed': {
    status: 'Circuit Integrity Passing',
    icon: (status: boolean) => (
      <Octicons
        size={FontSize.iconSize}
        name="circuit-board"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Battery in Use': {
    status: 'Battery Disconnected',
    icon: (status: boolean) => (
      <Entypo
        size={FontSize.iconSize}
        name="battery"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Patient Vent Circuit Disconnected': {
    status: 'Patient Vent Circuit Connected',
    icon: (status: boolean) => (
      <MaterialCommunityIcons
        size={FontSize.iconSize}
        name="heart-pulse"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Flow Sensor Disconnected': {
    status: 'Flow Sensor Connected',
    icon: (status: boolean) => (
      <MaterialCommunityIcons
        size={FontSize.iconSize}
        name="gauge"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Pressure Sensor Disconnected': {
    status: 'Pressure Sensor Connected',
    icon: (status: boolean) => (
      <AntDesign
        size={FontSize.iconSize}
        name="upcircleo"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Oxygen Sensor Disconnected': {
    status: 'Oxygen Sensor Connected',
    icon: (status: boolean) => (
      <MaterialCommunityIcons
        size={FontSize.iconSize}
        name="gas-cylinder"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Oxygen Failure': {
    status: 'Oxygen Intact',
    icon: (status: boolean) => {
      switch (status) {
        case true:
          return (
            <Feather
              size={FontSize.iconSize}
              name="alert-circle"
              color={'red'}
            />
          );
        case false:
          return (
            <AntDesign
              size={FontSize.iconSize}
              name="checkcircleo"
              color={'green'}
            />
          );
        default:
          return null;
      }
    },
  },
  'Mechanical Integrity Failed': {
    status: 'Mechanical Integrity Intact',
    icon: (status: boolean) => (
      <FontAwesome
        size={FontSize.iconSize}
        name="gears"
        color={status ? 'red' : 'green'}
      />
    ),
  },
  'Homing not Done': {
    status: 'Homing Done',
    icon: (status: boolean) => {
      switch (status) {
        case true:
          return (
            <Entypo
              size={FontSize.iconSize}
              name="circle-with-cross"
              color={'red'}
            />
          );
        case false:
          return (
            <MaterialIcons
              size={FontSize.iconSize}
              name="done"
              color={'green'}
            />
          );
        default:
          return null;
      }
    },
  },
  '96 Hours of Operation': {
    status: 'Hrs of Operation',
    icon: (status: boolean) => (
      <Entypo
        size={FontSize.iconSize}
        name="cycle"
        color={status ? 'red' : 'green'}
      />
    ),
  },
};
