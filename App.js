import React from 'react';
import {
  SafeAreaView,
  StyleSheet,
  View,
  Text,
  TouchableOpacity,
  NativeModules,
  Dimensions,
  TextInput,
} from 'react-native';
import {NetworkInfo} from 'react-native-network-info';

class App extends React.Component {
  state = {
    password: '',
  };

  handlePassword = (text) => {
    this.setState({password: text});
  };

  onPressStart = () => {
    var espBridge = NativeModules.ESPBridge;
    var type = 'esptouch';
    var deviceSSID = '';
    var deviceBSSID = '';
    var wifiPassword = this.state.password;

    NetworkInfo.getBSSID().then((bssid) => {
      deviceBSSID = bssid;
    });

    NetworkInfo.getSSID().then((ssid) => {
      deviceSSID = ssid;
    });

    espBridge
      .start({
        type: type,
        ssid: deviceSSID,
        bssid: deviceBSSID,
        password: wifiPassword,
        timeout: 50000,
      })
      .then(function (results) {
        console.log(results);
      })
      .catch(function (error) {
        console.log(error);
      });
  };

  onPressStop = () => {
    var espBridge = NativeModules.ESPBridge;
    espBridge.stop();
  };

  render() {
    const screenHeight = Math.round(Dimensions.get('window').height);
    return (
      <SafeAreaView>
        <View style={styles.container}>
          <View
            style={[styles.buttonsContainer, {marginTop: screenHeight / 3}]}>
            <TextInput
              style={styles.input}
              underlineColorAndroid="transparent"
              placeholder="Wifi Password"
              placeholderTextColor="#9a73ef"
              autoCapitalize="none"
              onChangeText={this.handlePassword}
            />
            <TouchableOpacity
              onPress={this.onPressStart}
              style={styles.buttonStyle}
              disabled={this.state.password === '' ? true : false}>
              <Text style={{color: '#ffffff', fontWeight: 'bold'}}>
                Start ESP Bridge
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={this.onPress}
              style={[styles.buttonStyle, {marginTop: 20}]}>
              <Text style={{color: '#ffffff', fontWeight: 'bold'}}>
                Stop ESP Bridge
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </SafeAreaView>
    );
  }
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
  },
  buttonsContainer: {
    alignItems: 'center',
  },
  buttonStyle: {
    backgroundColor: '#7892c2',
    elevation: 10,
    padding: 20,
    borderRadius: 10,
  },
  input: {
    margin: 15,
    height: 40,
    borderColor: '#7a42f4',
    borderWidth: 1,
    width: '70%',
  },
});

export default App;
