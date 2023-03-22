import * as React from 'react';
import { StyleSheet } from 'react-native';
import { ScrollView } from 'react-native-gesture-handler';
import OptionButton from '../components/OptionButton';
import Colors from '../constants/Colors';

export default function LinksScreen() {
  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={styles.contentContainer}>
      <OptionButton
        icon="md-school"
        label="Configurations"
        isLastOption={() => {}}
        onPress={() => {}}
      />
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.generalBackGround,
  },
  contentContainer: {
    paddingTop: 15,
  },
});
