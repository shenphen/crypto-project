import React from "react";
import { StyleSheet, Text, View, Button, TouchableOpacity } from "react-native";

export default class VoteScreen extends React.Component {
  static navigationOptions = {
    headerStyle: {
      height: 0
    }
  };

  render() {
    const { navigate } = this.props.navigation;

    return (
      <View style={styles.container}>
        <Text>Result is:</Text>
        <Text style={styles.result}>1</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 100
  },
  result: {
    fontSize: 28
  }
});
