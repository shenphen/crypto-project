import React from "react";
import { StyleSheet, Text, View } from "react-native";
import { createStackNavigator } from "react-navigation";
import HomeScreen from "./screens/HomeScreen";
import VoteScreen from "./screens/VoteScreen";
import ResultScreen from "./screens/ResultScreen";

const RootStack = createStackNavigator(
  {
    Home: { screen: HomeScreen },
    Vote: { screen: VoteScreen },
    Result: { screen: ResultScreen }
  },
  { initialRouteName: "Home" }
);

export default class App extends React.Component {
  render() {
    return <RootStack />;
  }
}
