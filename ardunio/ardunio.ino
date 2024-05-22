#include <Servo.h>
#include <SoftwareSerial.h>


// << PINS >>
// BUZZER
const int BUZZER_PIN = 4;

// SONAR
const int TRIGGER_PIN = 9;
const int ECHO_PIN = 8;

// SERVO
const int SERVO_PIN = 10;

// LED
const int GREEN_PIN = 6;
const int BLUE_PIN = 5;
const int RED_PIN = 7;

// BLUETOOTH
const int BLUETOOTH_TX_PIN = 0;
const int BLUETOOTH_RX_PIN = 1;

void broadcastData(float, float);


// SONAR
const float MILLIS_TO_CM = 0.01723;
long duration;

float fetchDistance()
{
	// Set pin to output
    pinMode(TRIGGER_PIN, OUTPUT);

	// Send pulse
    digitalWrite(TRIGGER_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIGGER_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIGGER_PIN, LOW);

	// Set pin to input, unnecessary in this case
    pinMode(ECHO_PIN, INPUT);
	
	// Read data
    duration = pulseIn(ECHO_PIN, HIGH);
    return duration * MILLIS_TO_CM;

}

// BUZZER
// Buzz different sounds depending on distance
void buzz(const float distance) {
  if(distance < 5) tone(BUZZER_PIN, 6000);
  else if(distance < 10) tone(BUZZER_PIN, 4000);
  else if(distance < 15) tone(BUZZER_PIN, 2000);
}


// LED
// Define pins
const int COLOR_PINS[3] = {BLUE_PIN, RED_PIN, GREEN_PIN};

int activePin = -1;

// Loop all pins and set all low except the activated one
void setColor(const int color) {
    if(activePin == color) return;
    activePin = color;

    for(int pin : COLOR_PINS) {
        if(pin == color) digitalWrite(color, HIGH);
        else digitalWrite(pin, LOW);
    }
}

// SERVO
Servo servo;
const int MAX_ANGLE = 175;
const int MIN_ANGLE = 5;
const int STEP = 3;

int angle = 0;
int reverse = 0;

// Move servo each tick
void servoMoveNext() {
    if(reverse)angle -= STEP;
    else angle += STEP;

    if(angle >= MAX_ANGLE) reverse = 1;
    else if(angle <= MIN_ANGLE) reverse = 0;

    servo.write(angle);
}

// Set servo angle
void servoSetAngle(int newAngle) {
  angle = newAngle;
  servo.write(angle);
}

// Reset servo angle
void servoReset() {
    angle = MIN_ANGLE;
    servo.write(angle);
}

// PROCESS INFO
const float CLOSE = 15;
const float MEDIUM = 30;

// Process the distance values
void processDistance(const float distance) {
    if(distance < CLOSE) {
        setColor(RED_PIN);
        buzz(distance);

    } else if(distance < MEDIUM) {
        setColor(GREEN_PIN);
        buzz(distance);

    } else {
        setColor(BLUE_PIN);
        noTone(BUZZER_PIN);
    }
}

// SETUP
SoftwareSerial btSerial(0, 1);

void setup()
{
	// Enable color pins as output
    for(int pin : COLOR_PINS) pinMode(pin, OUTPUT);
	
	// Enable buzzer pin as output
    pinMode(BUZZER_PIN, OUTPUT);

	// Set servo pin
    servo.attach(SERVO_PIN);
	
	// Set turret to 90 degrees
    servoSetAngle(90);

	// Start listening on both serials
    Serial.begin(9600);
    btSerial.begin(9600);
}


// LOOP


int sonarActive = 1;
int autoTurn = 1;
int manualControl = 0;

float distanceCentis = 0;

void loop()
{
  // Unused, used for getting input from the bluetooth
  getInput();
  
  // Auto turn
  if(autoTurn) {
    servoMoveNext();
  }

  // Enable data reading and broadcasting
  if(sonarActive) {
    distanceCentis = fetchDistance();
    broadcastData(angle, distanceCentis);
    processDistance(distanceCentis);
  }

  // Base delay of 150 ms
  delay(150);
}

// INPUT
char input;

int parseCommand(byte * buffer, int lastIndex) {
  // Parse incoming commands from serial
  // All inputs are 2 bytes
  switch(buffer[0]) {
    case 'a': // Auto on - off
      if(buffer[1] == '1') { // Auto mode on - off
        Serial.println("m#Auto rotate mode on!");
        manualControl = 0;
        autoTurn = 1;
      } else {
        Serial.println("m#Auto rotate mode off!");
        autoTurn = 0;
      }
      break;
    case 'r': // Reset to 0
      servoReset();
      break;
    case 's': // Sonar on - off
      if(buffer[1] == '1') {
        Serial.println("m#Sonar turned on!");
        sonarActive = 1;
      } else {
        Serial.println("m#Sonar turned off!");
        sonarActive = 0;
      }
      break;
    case 'c': // Manual control mode
      if(buffer[1] == '1') {
        Serial.println("m#Manual control turned on!");
        autoTurn = 0;
        manualControl = 1;
      } else {
        Serial.println("m#Manual control turned off!");
        manualControl = 0;
      }
      break;
    case 'm': // Set angle
      if(manualControl != 1) {
        Serial.println("m#Manual control is not enabled!");
        return;
      }

      // Second byte in the buffer is the angle
      servoSetAngle(buffer[1]);
      break;
  }
}


int badInput = 0;
int index = 0;
byte buffer[2] = {};

// Wipe the buffer
void cleanBuffer(char * buffer) {
  for(int i = 0; i < 2; i++) {
    buffer[i] = NULL;
  }
}

// Get input, if input is bad, move to next batch
void getInput() {
  // Bluetooth input
  while(btSerial.available()) {
    if(index == 2) {
      badInput = 1;
        index = 0;
      }

        input = btSerial.read();
        Serial.print("got:");
        Serial.print(input);

        if(input == '\n') {
          if(!badInput) parseCommand(buffer, index);

          cleanBuffer(buffer);
          badInput = 0;
          index = 0;
        } else {
          buffer[index] = input;
          index++;
        }
  }

    // Serial input
    while(Serial.available()) {
        if(index == 2) {
          badInput = 1;
          index = 0;
        }

        input = Serial.read();

        if(input == '\n') {
          if(!badInput) parseCommand(buffer, index);

          cleanBuffer(buffer);
          badInput = 0;
          index = 0;
        } else {
          buffer[index] = input;
          index++;
        }
    }
}

// Send data to both Serial and Bluetooth since 0 and 1 port is used.
void broadcastData(const float angle, const float distanceCms) {
  Serial.print("d#");
  Serial.print(angle);
  Serial.print(" ");
  Serial.println(distanceCms);
}