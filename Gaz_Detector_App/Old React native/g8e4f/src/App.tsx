import * as React from 'react';
import { Platform, StatusBar, StyleSheet, View } from 'react-native';

import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';

import BottomTabNavigator from './navigation/BottomTabNavigator';
import { ProvideReading } from './logic/useReading';
import FlashMessage from 'react-native-flash-message';
import AlarmsBanner from './components/AlarmsBanner';
import KeepAwake from 'react-native-keep-awake';
import Colors from './constants/Colors';
import { log } from './logic/AppLogger';

const Stack = createStackNavigator();

export default function App() {
  log.info('app');
  return (
    <View style={styles.container}>
      {Platform.OS === 'ios' && <StatusBar barStyle="default" />}
      <ProvideReading>
        <AlarmsBanner />
        <NavigationContainer>
          <Stack.Navigator>
            <Stack.Screen
              name="Main"
              component={BottomTabNavigator}
              initialParams={[34, 45]}
            />
          </Stack.Navigator>
        </NavigationContainer>
        <FlashMessage />
      </ProvideReading>
      <KeepAwake />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.rootViewColor,
  },
});
