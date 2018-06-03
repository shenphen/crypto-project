/**
 * This exposes the native Crypto module as a JS module. This has a
 * function 'encrypt' which takes the following parameters:
 *
 * 1. String message: A string with the text to encrypt
 * 2. String key: A string key used to encrypt
 */
import { NativeModules } from 'react-native';
module.exports = NativeModules.Crypto;