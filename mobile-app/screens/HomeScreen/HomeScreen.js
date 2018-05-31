import React from "react";
import {
  StyleSheet,
  Button,
  Text,
  View,
  Image,
  TouchableHighlight
} from "react-native";

export default class HomeScreen extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      chosenCharacter: null,
      characters: [
        { name: "Ron", img: require("./images/ron.png") },
        { name: "Hermione", img: require("./images/hermione.png") }
      ]
    };

    this.onPress = this.onPress.bind(this);
  }

  static navigationOptions = {
    headerStyle: {
      height: 0
    }
  };

  render() {
    const { chosenCharacter, characters } = this.state;

    return (
      <View style={styles.container}>
        <Text style={{ marginBottom: 20 }}>Choose who you are</Text>
        <Text style={{ marginBottom: 20 }}>{chosenCharacter}</Text>
        <View style={[styles.characterWrapper]}>
          {characters.map(char => (
            <TouchableHighlight
              key={char.name}
              style={[
                styles.character,
                { borderColor: chosenCharacter === char.name ? "red" : "#fff" }
              ]}
              onPress={() => this.onPress(char.name)}
            >
              <Image style={styles.image} source={char.img} />
            </TouchableHighlight>
          ))}
        </View>
        <Button
          disabled={!!!chosenCharacter}
          title="Go to Vote"
          onPress={() => this.props.navigation.navigate("Vote")}
        />
      </View>
    );
  }

  onPress(name) {
    this.setState(previousState => {
      return { chosenCharacter: name };
    });
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#dedede",
    alignItems: "center",
    justifyContent: "center",
    paddingVertical: 60
  },
  characterWrapper: {
    flex: 1,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    marginBottom: 20,
    paddingHorizontal: 20
  },
  character: {
    flex: 1,
    maxWidth: 140,
    borderRadius: 20,
    backgroundColor: "green",
    borderColor: "#fff",
    borderWidth: 5,
    shadowColor: "#0d0d0d",
    shadowRadius: 5,
    shadowOpacity: 0.3
  },
  image: {
    width: 130,
    height: 130,
    borderRadius: 15
  }
});
