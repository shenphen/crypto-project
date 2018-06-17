import Toast from "./modules/ToastExample";

class Protocol {
  saveOwnKeys(keys) {
    this.keys = keys
  }

  get keys() {
    return this.keys
  }

  saveInit(garbled, c) {
    this.garbled = garbled
    this.c = c
  }

  savePublicKeys(pubkeys) {
    this.pubkeys = pubkeys
  }
}

class ProtocolSingleton {

  static getInstance() {

    if(!this.instance) {
      this.instance = new Protocol()
    }

    return this.instance
  }
}

export default ProtocolSingleton
