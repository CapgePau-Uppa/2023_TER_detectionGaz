import Icon from 'react-native-vector-icons/Ionicons';
import * as React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { RectButton } from 'react-native-gesture-handler';
import Colors from '../constants/Colors';

export default function OptionButton({
  icon,
  label,
  onPress,
  isLastOption,
}: any) {
  return (
    <RectButton
      style={[styles.option, isLastOption && styles.lastOption]}
      onPress={onPress}>
      <View style={{ flexDirection: 'row' }}>
        <View style={styles.optionIconContainer}>
          <Icon name={icon} size={22} color={Colors.tabIconDefault} />
        </View>
        <View>
          <Text style={styles.optionText}>{label}</Text>
        </View>
      </View>
    </RectButton>
  );
}

const styles = StyleSheet.create({
  optionIconContainer: {
    marginRight: 12,
  },
  option: {
    backgroundColor: Colors.generalBackGround,
    paddingHorizontal: 15,
    paddingVertical: 15,
    borderWidth: StyleSheet.hairlineWidth,
    borderBottomWidth: 0,
    borderColor: Colors.borders,
  },
  lastOption: {
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  optionText: {
    fontSize: 15,
    alignSelf: 'flex-start',
    marginTop: 1,
    color: Colors.textColor,
  },
});
