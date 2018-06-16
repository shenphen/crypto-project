import Toast from "./modules/ToastExample";
import io from 'socket.io-client';

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
      Toast.show(data.message, Toast.LONG)
    })
  }

  emit(message) {
    if(!this.adress) {
      throw new Error("Adress not specified")
    }

    this.socket.emit(this.adress, { message })
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
