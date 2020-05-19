# ESPBridge
NativeModule bridging for ESP8266 Smart Config

## Description

This project is an example of how to implement native code bridging for React Native to use ESP8266 Smart Config in both
Android and iOS. The project is using official EspressifTouch SDK provided by EspressifCorp.

iOS     : https://github.com/EspressifApps/EspressifTouchSDK
Android  : https://github.com/EspressifApp/EsptouchForAndroid/

## Getting started

1) Download project
2) Run `npm install` or `yarn install` in project directory
3) Go to `ios` directory and run `pod install`
4) Go to `android` directory and run `./gradlew clean` and `./gradlew build`

## Usage

```javascript
import {
  NativeModules,
  PermissionsAndroid,
} from 'react-native';

export class App extends React.Component {

  componentDidMount() {
  // required on Android 9 and above to get SSID
  
    PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
    );
  }
  
  onStart() {
    var espBridge = NativeModules.ESPBridge;
    var type = 'esptouch';
    var deviceSSID = this.state.ssid;
    var deviceBSSID = this.state.bssid;
    var wifiPassword = this.state.password;

    espBridge
      .start({
        type: type,
        ssid: deviceSSID,
        bssid: deviceBSSID,
        password: wifiPassword,
        timeout: 50000,
        taskCount: 1,
      })
      .then(function (results) {
        console.log(results);
      })
      .catch(function (error) {
        console.log(error);
      });
  }
  
 onStop() {
    var espBridge = NativeModules.ESPBridge;
    espBridge.stop();
 }
}
```


