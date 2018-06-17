import Toast from "./modules/ToastExample";

class Protocol {
 
  constructor() {
    this.keys = null
    this.garbled = null
    this.challange = null
    this.randomExponent = null
    this.pubkeys = null
    this.choice = null
    this.result = null
  }
  setOwnKeysAndChoice(keys, choice, challange) {
    this.keys = keys
    this.choice = choice
    this.challange = challange
  }

  getOwnkeys() {
    return this.keys
  }

  saveInit(garbled, randomExponent) {
    this.garbled = garbled
    this.randomExponent = randomExponent
  }

  savePublicKeys(pubkeys) {
    this.pubkeys = pubkeys
  }

  getChoice() {
    return this.choice
  }

  getGarbled() {
    return this.garbled
  }

  getRandomExponent() {
    return this.randomExponent
  }

  getChallange() {
    return this.challange
  }

  saveResult(result) {
    this.result = result
  }

  getResult() {
    return this.result
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
