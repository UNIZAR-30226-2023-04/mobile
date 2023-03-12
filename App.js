
import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TextInput } from 'react-native';
//import Main from './src/components/Main';

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={StyleSheet.container}>
        <Text style={StyleSheet.welcome}>Welcome to React Native!</Text>
        <TextInput
          style={styles.input}
          placeholder="Username"
        />
        </View>
    );
  }
}

//comentario

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 30,
    textAlign: 'center',
    margin: 10,
    color: "#fff",
    fontFamily: "DancingScript-Bold"
  },
  input: {
    width: "90%",
    backgroundColor: "#fff",
    padding: 15
  }
})
