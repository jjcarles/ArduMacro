#include <SoftwareSerial.h>
#include <Wire.h>
#include <Adafruit_MotorShield.h>
#include "utility/Adafruit_MS_PWMServoDriver.h"

#define pinRX 9
#define pinTX 8
#define pinPH 4

SoftwareSerial bluetoothSerial(pinRX, pinTX);

Adafruit_MotorShield AFMS = Adafruit_MotorShield();
Adafruit_StepperMotor *myMotor = AFMS.getStepper(200, 2);

char data = (char)0;
boolean receiving = false;
int steptype = MICROSTEP;
boolean takephoto = false;

int photos = 0;
int totalPhotos = 0;
int steps = 0;
int velocity = 200;

String strCommand;
String strLastCommand;

void setup()  { 
  bluetoothSerial.begin(9600);
  bluetoothSerial.flush();

  pinMode(pinRX, INPUT);
  pinMode(pinTX, OUTPUT);
  pinMode(pinPH, OUTPUT);

  resetData();

  AFMS.begin();
  myMotor->setSpeed(velocity);
} 

void loop()  { 
  if (bluetoothSerial.available()) {        
    data = bluetoothSerial.read();        

    switch(data) {
    case ';':  
      receiving = false;  
      break;
    default: 
      if (receiving == false) 
        resetData();

      strCommand += data;
      receiving = true;          
    }

    delay(5);                            
  }  

  if ((receiving == false) && (strCommand.length() > 0)) {
    bluetoothSerial.print("Executing: ");
    bluetoothSerial.println(strCommand);

    strCommand.toUpperCase();

    if (strCommand == "FORWARD") {
      forwardMotor();
      strLastCommand = strCommand;
    }
    else if (strCommand == "BACKWARD") {
      backwardMotor();
      strLastCommand = strCommand;
    }
    else if (strCommand == "STOP") {
      stopMotor();
      strLastCommand = strCommand;
    }
    else if (strCommand == "SINGLE") {
      steptype = SINGLE;
      strCommand = strLastCommand;
    }
    else if (strCommand == "DOUBLE") {
      steptype = DOUBLE;
      strCommand = strLastCommand;
    }
    else if (strCommand == "INTERLEAVE") {
      steptype = INTERLEAVE;
      strCommand = strLastCommand;
    }
    else if (strCommand == "MICROSTEP") {
      steptype = MICROSTEP;
      strCommand = strLastCommand;
    }
    else if (strCommand == "TAKEPHOTO") {
      takePhoto();
      strLastCommand = strCommand;
    }
    else if (strCommand == "START") {
      startSession();
      strLastCommand = strCommand;
    }    
    else if (strCommand == "CANCEL") {
      cancelSession();
      strLastCommand = strCommand;
    }    
    else if (strCommand.startsWith("PHOTOS=")) {
      totalPhotos = extractValue(strCommand);
      strLastCommand = strCommand;
    }
    else if (strCommand.startsWith("STEPS=")) {
      steps = extractValue(strCommand);
      strLastCommand = strCommand;
    }
    else if (strCommand == "TEST") {
      test();
      strLastCommand = strCommand;
    }
  }
}

void test() {
  // Test avance
  myMotor->step(200, FORWARD, DOUBLE);
  delay(2000);
  myMotor->step(200, FORWARD, SINGLE);
  delay(2000);
  myMotor->step(200, FORWARD, INTERLEAVE);
  delay(2000);
  myMotor->step(200, FORWARD, MICROSTEP);
  delay(2000);

  // Test retroceso
  myMotor->step(200, BACKWARD, DOUBLE);
  delay(2000);
  myMotor->step(200, BACKWARD, SINGLE);
  delay(2000);
  myMotor->step(200, BACKWARD, INTERLEAVE);
  delay(2000);
  myMotor->step(200, BACKWARD, MICROSTEP);
  delay(2000);
}

void stopMotor() {
  myMotor->release();
  resetData();
}

void forwardMotor() {
  if (steps > 0) {
    myMotor->step(steps, FORWARD, steptype);
  }
}

void backwardMotor() {
  if (steps > 0) {
    myMotor->step(steps, BACKWARD, steptype);
  }  
}

void startSession() {
  if ((photos < totalPhotos) && (steps > 0)) {
    // Posiciona la cámara
    myMotor->step(steps, FORWARD, steptype);

    // Espera para evitar vibraciones de movimiento
    delay(2000);

    // Realiza la foto
    takePhoto();
    photos++;
  }
  else {
    // Finaliza la sesión        
    myMotor->release();
    resetData();
    photos = 0;
  }  
}

void cancelSession() {
  // Para el motor
  stopMotor();

  // Resetea los valores de la sesion
  photos = 0;
  steps = 0; 

  // Elimina el comando
  resetData();
}

int extractValue(String strCommand) {
  // Extrae el valor
  int pos = strCommand.indexOf('=');
  int value = strCommand.substring(pos + 1).toInt();  

  // Elimina el comando
  resetData();

  // Devuelve el valor
  return value;
}

void takePhoto() {
  digitalWrite(pinPH, HIGH);
  delay(1000);
  digitalWrite(pinPH, LOW);
  delay(1000);
}

void resetData() {
  strCommand = String((char)0);
}



