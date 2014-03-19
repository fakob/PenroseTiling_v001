import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class PenroseTiling_v001 extends PApplet {

PVector cornerA, cornerB, cornerC;
// aTriangle penrosePattern[i] = new aTriangle();
ArrayList<aTriangle> penrosePattern;
int NUM_SUBDIVISIONS = 4;
PImage img1;
PImage img2;

public void setup() {

	penrosePattern = new ArrayList<aTriangle>();
	img1 = loadImage("Waterplants1.png");
	img2 = loadImage("Waterplants2.png");

	size(1200, 1200);
// noFill();
	smooth();
  	background(0);

	int len = 10;
	int radius = 100;

	for (int i = 0; i < len; i++) {
		penrosePattern.add(new aTriangle());
		penrosePattern.get(i).aVariant = 0;
		penrosePattern.get(i).aCornerA = new PVector(0, 0);
		penrosePattern.get(i).aCornerB = new PVector(radius * sin((2*i - 1) * PI / 10), radius * cos((2*i - 1) * PI / 10)); 
		penrosePattern.get(i).aCornerC = new PVector(radius * sin((2*i + 1) * PI / 10), radius * cos((2*i + 1) * PI / 10));
		PVector tempVec3 = new PVector(0,0);
        penrosePattern.get(i).aAngleBC = calculateAngelBetweenVectors(penrosePattern.get(i).aCornerB, penrosePattern.get(i).aCornerC);
        // penrosePattern.get(i).aAngleBC = i*10;
	}

	for (int j = 0; j < NUM_SUBDIVISIONS; j++) {
		int formerPenrosePatternSize = penrosePattern.size();
		println("formerPenrosePatternSize" + formerPenrosePatternSize);
		for (int i = 0; i < formerPenrosePatternSize; i++) {
			subdivide(i);
		}
	}
	// println("newSize" + penrosePattern.size());

	for (int i = 0; i < penrosePattern.size(); i++) {
		// println("aDraw: " + penrosePattern.get(i).aDraw);
	}
}
public void draw() {
	background(0);
	// triangle(0, 0, mouseX, mouseY, 50, 50);

	pushMatrix();
	translate(width/2, height/2);
	scale(mouseY/10.0f);
	for (int i = 0; i < penrosePattern.size(); i++) {
		if (penrosePattern.get(i).aDraw) {
			randomSeed(i);
			if (penrosePattern.get(i).aVariant == 0){
				fill(255,0,0,random(25,50));
			} else {
				fill(0,255,0,random(25,50));
			}
			// noStroke();
			PVector tempVec = calculateCoordinatesOfTriangleIncenter(penrosePattern.get(i).aCornerA, penrosePattern.get(i).aCornerB, penrosePattern.get(i).aCornerC).get();
			float tempRadius = calculateRadiusOfTriangleIncenter(penrosePattern.get(i).aCornerA, penrosePattern.get(i).aCornerB, penrosePattern.get(i).aCornerC) * 2.0f;
			triangle(penrosePattern.get(i).aCornerA.x, penrosePattern.get(i).aCornerA.y, penrosePattern.get(i).aCornerB.x, penrosePattern.get(i).aCornerB.y, penrosePattern.get(i).aCornerC.x, penrosePattern.get(i).aCornerC.y);
			pushStyle();
			pushMatrix();
			if (keyPressed) {
				ellipse(tempVec.x, tempVec.y, tempRadius, tempRadius);
				// arc(tempVec.x, tempVec.y, tempRadius, tempRadius,0,penrosePattern.get(i).aAngleBC);
				translate(tempVec.x, tempVec.y);
				rotate(penrosePattern.get(i).aAngleBC);
				rectMode(RADIUS);
				stroke(255,255,0);
				strokeWeight(0.3f);
				fill(255,0,255,50);
				// line(penrosePattern.get(i).aCornerB.x, penrosePattern.get(i).aCornerB.y, penrosePattern.get(i).aCornerC.x, penrosePattern.get(i).aCornerC.y);
				PVector tempVec2 = penrosePattern.get(i).aCornerB.get();
				float tempRotation = PVector.angleBetween(penrosePattern.get(i).aCornerB, penrosePattern.get(i).aCornerC);
				strokeWeight(0.1f);
				// rect(0,0, tempRadius/2, tempRadius/2);
			} else {
				translate(tempVec.x, tempVec.y);
				rotate(penrosePattern.get(i).aAngleBC);
				float tempImgSize = tempRadius*2;
				if (penrosePattern.get(i).aVariant == 0){

					image(img1, -tempImgSize, -tempImgSize, tempImgSize*2, tempImgSize*2);
				} else {
					image(img2, -tempImgSize, -tempImgSize, tempImgSize*2, tempImgSize*2);
				}
			}


			popMatrix();
			popStyle();
		}
	}
	popMatrix();
}

public float calculateAngelBetweenVectors(PVector _Vec1, PVector _Vec2){
	float deltaX = _Vec1.x - _Vec2.x;
	float deltaY = _Vec1.y - _Vec2.y;
	float angleInDegrees;
	angleInDegrees = atan2(deltaY, deltaX);
	if (angleInDegrees < 0) {
		angleInDegrees = 2*PI + angleInDegrees;
	}
	return angleInDegrees;
}

public PVector calculateCoordinatesOfTriangleIncenter(PVector _CornerA, PVector _CornerB, PVector _CornerC){
	PVector _tempVec = _CornerA.get();
	float tempA = _CornerB.dist(_CornerC);
	float tempB = _CornerA.dist(_CornerC);
	float tempC = _CornerA.dist(_CornerB);
	_tempVec.x = ((tempA*_CornerA.x)+(tempC*_CornerC.x)+(tempB*_CornerB.x))/(tempA+tempB+tempC);
	_tempVec.y = ((tempA*_CornerA.y)+(tempC*_CornerC.y)+(tempB*_CornerB.y))/(tempA+tempB+tempC);
	return _tempVec;
}

public float calculateRadiusOfTriangleIncenter(PVector _CornerA, PVector _CornerB, PVector _CornerC){
	PVector _tempVec = _CornerA.get();
	float tempA = _CornerB.dist(_CornerC);
	float tempB = _CornerA.dist(_CornerC);
	float tempC = _CornerA.dist(_CornerB);
	float tempR = (tempA+tempB+tempC)/2.0f;
	tempR = sqrt(((tempR - tempA) * (tempR - tempB) * (tempR - tempC))/tempR);
	return tempR;
}

public void subdivide(int _i){
	float goldenRatio = (1 + sqrt(5)) / 2.0f;
	if (penrosePattern.get(_i).aDraw) {
			penrosePattern.get(_i).aDraw = false;
		if (penrosePattern.get(_i).aVariant == 0){
            // Subdivide red triangle
            PVector P = penrosePattern.get(_i).aCornerA.get();
            // P.add(new PVector(100,100));
            P.add(P.div(P.sub(penrosePattern.get(_i).aCornerB,penrosePattern.get(_i).aCornerA),goldenRatio));

            penrosePattern.add(new aTriangle());
            penrosePattern.get(penrosePattern.size() - 1).setValues(penrosePattern.get(_i).aColor, 0, penrosePattern.get(_i).aCornerC, P, penrosePattern.get(_i).aCornerB);
            penrosePattern.get(penrosePattern.size() - 1).aAngleBC = calculateAngelBetweenVectors(P, penrosePattern.get(_i).aCornerB);
            println("aAngleBC: " + penrosePattern.get(penrosePattern.size() - 1).aAngleBC);

            penrosePattern.add(new aTriangle());
            penrosePattern.get(penrosePattern.size() - 1).setValues(penrosePattern.get(_i).aColor, 1, P,penrosePattern.get(_i).aCornerC, penrosePattern.get(_i).aCornerA);
            penrosePattern.get(penrosePattern.size() - 1).aAngleBC = calculateAngelBetweenVectors(penrosePattern.get(_i).aCornerC, penrosePattern.get(_i).aCornerA);
            println("aAngleBC: " + penrosePattern.get(penrosePattern.size() - 1).aAngleBC);

            // println("PVector P: " + P);
            // println("old aCornerA: " + penrosePattern.get(_i).aCornerA);
            // println("old aCornerB: " + penrosePattern.get(_i).aCornerB);
            // println("old aCornerC: " + penrosePattern.get(_i).aCornerC);

            // println("new aCornerA: " + penrosePattern.get(penrosePattern.size() - 1).aCornerA);
            // println("new aCornerB: " + penrosePattern.get(penrosePattern.size() - 1).aCornerB);
            // println("new aCornerC: " + penrosePattern.get(penrosePattern.size() - 1).aCornerC);

            // penrosePattern.get(penrosePattern.size() - 1).aCornerA = new PVector(100, 100);;
            // result = [(0, aCornerC, P, aCornerB), (1, P, aCornerC, aCornerA)];
        } else {
            // Subdivide blue triangle
            PVector Q = penrosePattern.get(_i).aCornerB.get();
            PVector R = penrosePattern.get(_i).aCornerB.get();
            Q.add(Q.div(Q.sub(penrosePattern.get(_i).aCornerA,penrosePattern.get(_i).aCornerB),goldenRatio));
            R.add(R.div(R.sub(penrosePattern.get(_i).aCornerC,penrosePattern.get(_i).aCornerB),goldenRatio));

            penrosePattern.add(new aTriangle());
            penrosePattern.get(penrosePattern.size() - 1).setValues(penrosePattern.get(_i).aColor, 1, R,penrosePattern.get(_i).aCornerC, penrosePattern.get(_i).aCornerA);
            penrosePattern.get(penrosePattern.size() - 1).aAngleBC = calculateAngelBetweenVectors(penrosePattern.get(_i).aCornerC, penrosePattern.get(_i).aCornerA);
            println("aAngleBC: " + penrosePattern.get(penrosePattern.size() - 1).aAngleBC);

            penrosePattern.add(new aTriangle());
            penrosePattern.get(penrosePattern.size() - 1).setValues(penrosePattern.get(_i).aColor, 1, Q, R, penrosePattern.get(_i).aCornerB);
            penrosePattern.get(penrosePattern.size() - 1).aAngleBC = calculateAngelBetweenVectors(R, penrosePattern.get(_i).aCornerB);
            println("aAngleBC: " + penrosePattern.get(penrosePattern.size() - 1).aAngleBC);

            penrosePattern.add(new aTriangle());
            penrosePattern.get(penrosePattern.size() - 1).setValues(penrosePattern.get(_i).aColor, 0, R, Q, penrosePattern.get(_i).aCornerA);
            penrosePattern.get(penrosePattern.size() - 1).aAngleBC = calculateAngelBetweenVectors(Q, penrosePattern.get(_i).aCornerA);
            println("aAngleBC: " + penrosePattern.get(penrosePattern.size() - 1).aAngleBC);
        }
        println("----: ");
    }
    println("--------------------: ");
}

class aTriangle{
	public int aColor;
	public int aVariant;
	public boolean aDraw = true;
	public PVector aCornerA;
	public PVector aCornerB;
	public PVector aCornerC;
	public float aAngleBC;

	public void setValues(int _aColor, int _aVariant, PVector _aCornerA, PVector _aCornerB, PVector _aCornerC){
		this.aColor = _aColor;
		this.aVariant = _aVariant;
		this.aCornerA = _aCornerA;
		this.aCornerB = _aCornerB;
		this.aCornerC = _aCornerC;
	}
};
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "PenroseTiling_v001" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
