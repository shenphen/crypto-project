import React from "react";
import { StyleSheet, Text, View } from "react-native";
import Protocol from "../../Protocol"

const protocol = Protocol.getInstance()

export default class VoteScreen extends React.Component {
  static navigationOptions = {
    headerStyle: {
      height: 0
    }
  };

  constructor(props) {
    super(props)

    this.state = {
      status: null
    }
  }

  componentDidMount() {
    this.interval = setInterval(() => {
      if(protocol.getResult()) {
        clearInterval(this.interval)
        this.setState({ status: protocol.getResult() })
      }
    }, 500)
  }

  componentWillUnmount() {
    if(this.interval) {
      clearInterval(this.interval)
    }
  }

  render() {
    return (
      <View style={styles.container}>
        <Text>Result is:</Text>
        <Text style={styles.result}>{this.state.status || "Waiting..."}</Text>
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
