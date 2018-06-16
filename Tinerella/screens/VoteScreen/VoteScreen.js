import React from "react";
import { StyleSheet, Text, View, Button, TouchableOpacity } from "react-native";
import API from "../../API";

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
        <Text>Do you like other person?</Text>
        <View style={styles.buttonWrapper}>
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              API.getInstance().emit("Yes")
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>Yes</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              API.getInstance().emit("No")
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>No</Text>
          </TouchableOpacity>
        </View>
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
  buttonWrapper: {
    flex: 2,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center"
  },
  button: {
    flex: 1,
    marginHorizontal: 20,
    width: 64,
    height: 32,
    backgroundColor: "blue"
  },
  buttonText: {
    textAlign: "center",
    lineHeight: 32,
    color: "#fff"
  }
});
