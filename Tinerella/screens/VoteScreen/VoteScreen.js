import React from "react";
import { StyleSheet, Text, View, Button, TouchableOpacity } from "react-native";
import API from "../../API";
import Protocol from "../../Protocol";
import Crypto from "../../modules/Crypto";
import Toast from "../../modules/ToastExample";

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
              this.sendGarbled(true)
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>Yes</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              this.sendGarbled(false)
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>No</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  sendGarbled(choice) {
    const protocol = Protocol.getInstance()
    const api = API.getInstance()

    Crypto.generateGarbled(choice).then(data => {
      const { cipher0, cipher1, keyYes, keyNo, c } = data

      if([cipher0, cipher1, keyYes, keyNo, c].includes(null)) {
        Toast.show("Error after gerating garbled circuit", Toast.LONG)
        return
      }
      
      const privKeys = [keyNo, keyYes]
      const garbled = [cipher0, cipher1]
      protocol.setOwnKeysAndChoice(privKeys, choice, c)

      api.emit("init", {
        garbled,
        c
      })
    })
    .catch(err => {
      Toast.show("Error during gerating garbled circuit", Toast.LONG)
      console.log(err);
    })
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
