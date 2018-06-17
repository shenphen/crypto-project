import React from "react";
import { StyleSheet, Text, View, Button, TouchableOpacity } from "react-native";
import API from "../../API";
import Protocol from "../../Protocol";
import Crypto from "../../modules/Crypto";

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
              {/* API.getInstance().emit("Yes") */}
              this.testProtocol(true)
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>Yes</Text>
          </TouchableOpacity>
          <TouchableOpacity
            style={styles.button}
            onPress={() => {
              {/* API.getInstance().emit("No") */}
              this.testProtocol(false)
              navigate("Result")
            }}
          >
            <Text style={styles.buttonText}>No</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  testProtocol(choice) {
    const protocol = Protocol.getInstance()

    Crypto.generateGarbled(choice).then(data => {
      const { cipher0, cipher1, keyYes, keyNo, c } = data
      console.log({cipher0, cipher1, keyYes, keyNo, c});

      const privKeys = [keyNo, keyYes]
      protocol.saveOwnKeys([keyYes, keyNo])

      const garbled = [cipher0, cipher1]
      console.log({garbled});

      const choice2 = true
      Crypto.generatePublicKeys(c, choice2).then(data => {
        const { pk0, pk1, k } = data
        console.log({pk0, pk1, k});
        const pubKeys = [pk0, pk1]

        Crypto.checkPublicKeys(c, pubKeys[0], pubKeys[1]).then(data => {
          const { result } = data
          console.log({checkPubKEys: result});

          if(result) {
            console.log({pubKeys, keys: privKeys});
            Crypto.encryptElGamal(pubKeys[0], pubKeys[1], privKeys[0], privKeys[1]).then(data => {
              const { c0, c1 } = data
              console.log({c0, c1});
              const ciphers = [c0,c1]
              const index = Number(choice2)
              Crypto.decryptElGamal(ciphers[index], k).then(data => {
                const { key } = data
                console.log({key});
                Crypto.decryptGarbled(garbled, key).then(data => {
                  const { decrypted } = data
                  console.log({decrypted: decrypted});
                })
                .catch(err => {
                  console.log(err);
                })
              })
              .catch(err => {
                console.log(err);
              })
            })
            .catch(err => {
              console.log("EncryptEL" + err);
            })
          }


        })
        .catch(err => {
          console.log(err);
        })
      })
      .catch(err => {
        console.log(err);
      })
    })
    .catch(err => {
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
