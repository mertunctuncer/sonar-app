import processing.serial.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

Serial myPort; 

// Angle and distance to draw on each tick
int iAngle, iDistance;

void setup() {
 // Set window size
 size (1920, 1080);
 smooth();
 // Open serial port
 myPort = new Serial(this,"COM5", 9600);
 // Buffer until \n character
 myPort.bufferUntil('\n');
}

void draw() {
  fill(98,245,31);
  noStroke();
  fill(0,4); 
  
  // Draw window
  rect(0, 0, width, height-height*0.065); 
  
  fill(98,245,31);
  
  // Draw radar
  drawRadar(); 
  
  // Draw lines
  drawLine();
  
  // Draw detection
  drawObject();
  
  // Draw texts
  drawText();
}

void serialEvent (Serial myPort) {
  // Read the stream
  String data = myPort.readStringUntil('\n');
  // Cut the first two letters
  String type = data.substring(0, 2);
  
  // If type is data
  if(type.equals("d#")) {
    // Parse the string to remove the first 2 and the last 1 letter -> 12.123 1231.2 left
    data = data.substring(2,data.length()-1);
    
    // Cut the string in two to seperate variables
    int index = data.indexOf(" ");
    
    // Get angle and distance
    iAngle = int(data.substring(0, index));
    iDistance = int(data.substring(index + 1, data.length()));
    
    // If data is a message, UNUSED, was added for other operations
  } else if(type.equals("m#")) {
    data = data.substring(2,data.length()-1);
    System.out.println(data);
  }
}


void drawRadar() {
  pushMatrix();
  translate(width/2,height-height*0.074);
  
  // Brush
  noFill();
  strokeWeight(2);
  stroke(98,245,31);

  // Arcs
  arc(0,0,(width-width*0.0625),(width-width*0.0625),PI,TWO_PI);
  arc(0,0,(width-width*0.27),(width-width*0.27),PI,TWO_PI);
  arc(0,0,(width-width*0.479),(width-width*0.479),PI,TWO_PI);
  arc(0,0,(width-width*0.687),(width-width*0.687),PI,TWO_PI);

  // Angle lines
  line(-width/2,0,width/2,0);
  line(0,0,(-width/2)*cos(radians(30)),(-width/2)*sin(radians(30)));
  line(0,0,(-width/2)*cos(radians(60)),(-width/2)*sin(radians(60)));
  line(0,0,(-width/2)*cos(radians(90)),(-width/2)*sin(radians(90)));
  line(0,0,(-width/2)*cos(radians(120)),(-width/2)*sin(radians(120)));
  line(0,0,(-width/2)*cos(radians(150)),(-width/2)*sin(radians(150)));
  line((-width/2)*cos(radians(30)),0,width/2,0);
  
  popMatrix();
}

void drawObject() {
  pushMatrix();
  translate(width/2,height-height*0.074);
  
  strokeWeight(9);
  stroke(255,10,10);
  
  // Calculate length
  float pixsDistance = iDistance*((height-height*0.1666)*0.025); 
  
  // Draw the line if it fits in the radar
  if(iDistance<20){
    line(pixsDistance*cos(radians(iAngle)),-pixsDistance*sin(radians(iAngle)),(width-width*0.505)*cos(radians(iAngle)),-(width-width*0.505)*sin(radians(iAngle)));
  }
  popMatrix();
}


void drawLine() {
  pushMatrix();
  
  // Brush settings
  strokeWeight(9);
  stroke(30,250,60);
  
  translate(width/2,height-height*0.074);
  
  // Draw the line, convert from polar coordinates
  line(0,0,(height-height*0.12)*cos(radians(iAngle)),-(height-height*0.12)*sin(radians(iAngle)));
  popMatrix();
}

void drawText() {
  pushMatrix();
  
  // Brush
  fill(0,0,0);
  noStroke();
  rect(0, height-height*0.0648, width, height);
  fill(98,245,31);
  textSize(25);
  
  // Distance markers
  text("10cm",width-width*0.3854,height-height*0.0833);
  text("20cm",width-width*0.281,height-height*0.0833);
  text("30cm",width-width*0.177,height-height*0.0833);
  text("40cm",width-width*0.0729,height-height*0.0833);
  textSize(25);
  text("Angle: " + iAngle +" °  Distance: " + iDistance + " cm", width-width*0.26, height-height*0.0277);
  popMatrix(); 
}
