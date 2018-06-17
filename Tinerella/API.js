import Toast from "./modules/ToastExample";
import Crypto from "./modules/Crypto";
import io from 'socket.io-client';
import Protocol from "./Protocol";

class API {
  constructor() {
    this.socket = io('https://pogrzebyonline.com');
    this.adress = null
    this.listener = null
  }

  setAdress(adress) {
    this.adress = adress
  }

  setListener(listener) {
    this.listener = listener

    this.socket.on(this.listener, (data) => {
      switch(data.title) {
        case "init":
          this.onInit(data)
          break

        case "pubKeys":
          this.onPubKeys(data)
          break

        case "ciphers":
          this.onCiphers(data)
          break

        default:
          break
      }
    })
  }

  emit(title, message) {
    if(!this.adress) {
      throw new Error("Adress not specified")
    }

    this.socket.emit(this.adress, { title, message })
  }

  onInit(data) {
    const protocol = Protocol.getInstance()
    const { garbled, c } = data.message

    const interval = setInterval(() => {
      if(typeof protocol.getChoice() === 'boolean') {
        clearInterval(interval)
        Crypto.generatePublicKeys(c, protocol.getChoice()).then(data => {
          const { pk0, pk1, k } = data
          protocol.saveInit(garbled, k)
          const pubKeys = [pk0, pk1]
          this.emit("pubKeys", { pubKeys })
        })
        .catch(err => {
          Toast.show("Error during generating public keys", Toast.LONG)
          console.log(err);
        })
      }
    }, 500)
  }

  onPubKeys(data) {
    const protocol = Protocol.getInstance()
    const { pubKeys } = data.message

    Crypto.checkPublicKeys(protocol.getChallange(), pubKeys[0], pubKeys[1]).then(data => {
      const { result } = data

      if(result) {
        const privKeys = protocol.getOwnkeys()
        Crypto.encryptElGamal(pubKeys[0], pubKeys[1], privKeys[0], privKeys[1]).then(data => {
          const { c0, c1 } = data
          const ciphers = [c0,c1]
          this.emit("ciphers", { ciphers })
        })
        .catch(err => {
          Toast.show("Error during ElGamal encryption", Toast.LONG)
          console.log(err);
        })
      } else {
        Toast.show("Wrong public keys", Toast.LONG)
      }
    })
  }

  onCiphers(data) {
    const protocol = Protocol.getInstance()
    const { ciphers } = data.message
    const index = Number(protocol.getChoice())

    Crypto.decryptElGamal(ciphers[index], protocol.getRandomExponent()).then(data => {
      const { key } = data

      Crypto.decryptGarbled(protocol.getGarbled(), key).then(data => {
        const { decrypted } = data
        protocol.saveResult(decrypted)
      })
      .catch(err => {
        Toast.show("Error during garbled circuit decryption", Toast.LONG)
        console.log(err);
      })
    })
    .catch(err => {
      Toast.show("Error during ElGamal decryption", Toast.LONG)
      console.log(err);
    })
  }
}

class SocketSingleton {

  static getInstance() {

    if(!this.instance) {
      this.instance = new API()
    }

    return this.instance
  }
}

export default SocketSingleton
