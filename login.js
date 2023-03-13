/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow strict-local
 */

import React, { Component } from 'react';
import { StyleSheet, Text, View, TextInput, TouchableOpacity, StatusBar } from 'react-native';

type Props = {};
export default class App extends Component<Props> {
  render() {
    return (
      <View style={StyleSheet.container}>
        <StatusBar
          backgroundColor="#000"
          barStyle="light-content"
        />
        <Text style={StyleSheet.welcome}>Bienvenido de nuevo,</Text>
        <Text style={StyleSheet.welcome}>¡Inicia sesión!</Text>
        <TextInput
          style={styles.input}
          placeholder="Usuario"
        />
        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          secureTextEntry
        />
        <View style={styles.btnContainer}>
          <TouchableOpacity
            style={styles.paswordBtn}
            onPress={() => alert("Contraseña")}
          >
            <Text style={styles.btnTxtPasword}>¿olvidaste tú contraseña?</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.btnContainer}>
          <TouchableOpacity
            style={styles.loginBtn}
            onPress={() => alert("Login Works")}
          >
            <Text style={styles.btnTxtLogin}>INICIAR SESIÓN</Text>
          </TouchableOpacity>
        </View>
        <Text style={StyleSheet.welcome}>¿No tienes cuenta?</Text>
        <View style={styles.btnContainer}>
          <TouchableOpacity
            style={styles.paswordBtn}
            onPress={() => alert("Registro")}
          >
            <Text style={styles.btnTxtPasword}>Registrarse</Text>
          </TouchableOpacity>
        </View>
        </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#F5FCFF',
    alignItems: "center",
    justifyContent: "center", 
  },
  welcome: {
    fontSize: 30,
    textAlign: "center",
    margin: 10,
    color: "#fff",
    //fontFamily: "IBM Plex Sans Condensed Extra Light",
    
  },
  input: {
    fontSize: 20,
    width: "80%",
    backgroundColor: "#fff",
    padding: 15,
    //marginBottom: 80
  },
  btnContainer: {
    flexDirection: "row",
    justifyContent: "center",
    width: "90%"
  },
  loginBtn: {
    backgroundColor: "#0718C4",
    padding: 15,
    width: "80%", 
    borderRadius: 25,
  },
  paswordBtn: {
    backgroundColor: "#fff",
    padding: 15,
    width: "80%", 
    borderRadius: 25,
    marginBottom: 80,
  },
  btnTxtLogin: {
    fontSize: 18,
    textAlign: "center",
    color: "#fff",
  },
  btnTxtPasword: {
    fontSize: 18,
    textAlign: "center",
    color: "#0718C4"
  },
});