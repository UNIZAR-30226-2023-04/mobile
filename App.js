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
        <Text style={StyleSheet.welcome}>Hola,</Text>
        <Text style={StyleSheet.welcome}>¡Únete!</Text>
        <TextInput
          style={styles.input}
          placeholder="Mail"
        />
        <TextInput
          style={styles.input}
          placeholder="Nombre"
        />
        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          secureTextEntry
        />
        <TextInput
          style={styles.input}
          placeholder="Verificar contraseña"
          secureTextEntry
        />
        <View style={styles.btnContainer}>
          <TouchableOpacity
            style={styles.signUpBtn}
            onPress={() => alert("SignUp Works")}
          >
            <Text style={styles.btnTxtLogin}>REGISTRARSE</Text>
          </TouchableOpacity>
        </View>
        <Text style={StyleSheet.welcome}>¿Ya tienes cuenta?</Text>
        <View style={styles.btnContainer}>
          <TouchableOpacity
            style={styles.cuentaBtn}
            onPress={() => alert("Cuenta Works")}
          >
            <Text style={styles.btnTxtCuenta}>Iniciar sesión</Text>
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
    marginBottom: 20
  },
  btnContainer: {
    flexDirection: "row",
    justifyContent: "center",
    width: "90%"
  },
  signUpBtn: {
    backgroundColor: "#0718C4",
    padding: 15,
    width: "80%", 
    borderRadius: 25,
  },
  cuentaBtn: {
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
  btnTxtCuenta: {
    fontSize: 18,
    textAlign: "center",
    color: "#0718C4"
  },
}); 