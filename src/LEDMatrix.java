import processing.core.PApplet;
import processing.core.PImage;
import processing.serial.Serial;

import processing.*;
import toxi.color.TColor;
import toxi.geom.Vec2D;

//

class LEDMatrix extends PApplet {

	// The LED Controller to use with this matrix
	// LEDController ledC;
	// The size of the matrix
	int width = 40;
	int height = 25;
	int matStart = 0;

	Serial myPort;
	LEDController lc;// = new LEDController(1000);
	private int sweepCount;
	private boolean sweepAcross;
	private boolean startAdding;
	private int count;

	// LEDMatrix matrix = new LEDMatrix(lc, 5, 4);

	LEDMatrix(LEDController theLEDController, int theW, int theH) {
		width = theW;
		height = theH;
		lc = theLEDController;
		matStart = 0;
	}

	int getXY(int x, int y) {
		int ret;
		if (y % 2 > 0) {
			ret = (((y + 1) * width) - x - 1);
		} else {
			ret = ((y * width) + x);
		}

		// println("get pos for x:"+x+"  y:"+y+" pos:"+ret+" converted back?:"+getXYFromPos(ret).toString());

		return ret;
	}

	Vec2D getXYFromPos(int pos) {

		Vec2D vec = new Vec2D();
		vec.x = pos % width;
		vec.y = floor(pos / width);
		return vec;

	}

	boolean drawLine(int thex0, int they0, int thex1, int they1, int r, int g,
			int b) {
		boolean steep;

		steep = Math.abs(they1 - they0) > Math.abs(thex1 - thex0);

		IntHolder x0h = new IntHolder(thex0);
		IntHolder x1h = new IntHolder(thex1);
		IntHolder y0h = new IntHolder(they0);
		IntHolder y1h = new IntHolder(they1);

		if (steep) {
			swapInts(x0h, y0h);
			swapInts(x1h, y1h);
		}
		if (x0h.val > x1h.val) {
			swapInts(x0h, x1h);
			swapInts(y0h, y1h);
		}
		int x0 = x0h.val;
		int x1 = x1h.val;
		int y0 = y0h.val;
		int y1 = y1h.val;

		int deltax = x1 - x0;
		int deltay = Math.abs(y1 - y0);
		int error = 0;
		int ystep;
		int y = y0;
		int x;
		if (y0 < y1)
			ystep = 1;
		else
			ystep = -1;
		for (x = x0; x <= x1; x++) // from x0 to x1
		{
			if (steep) {
				lc.setRGB(getXY(y, x), r, g, b);
			} else {
				lc.setRGB(getXY(x, y), r, g, b);
			}
			error = error + deltay;
			if (2 * error >= deltax) {
				y = y + ystep;
				error = error - deltax;
			}
		}
		return true;
	}

	// Swap the values of two variables, for use when drawing lines.
	void swapInts(IntHolder a, IntHolder b) {
		int temp;
		temp = b.val;
		b.val = a.val;
		a.val = temp;
	}

	void drawBox(int x0, int y0, int x1, int y1, int r, int g, int b) {
		drawLine(x0, y0, x1, y0, r, g, b);
		drawLine(x1, y0, x1, y1, r, g, b);
		drawLine(x1, y1, x0, y1, r, g, b);
		drawLine(x0, y1, x0, y0, r, g, b);
	}

	void allTo(int r, int g, int b) {
		lc.setAllTo(r, g, b);
	}

	void allOff() {
		allTo(0, 0, 0);
	}

	void allOn() {
		allTo(255, 255, 255);
	}

	void refresh() {
	//	println("refresh");
		if (sweepAcross) {
			sweepWidth();
		}
		if (startAdding){
			addUp();
		}
		//lc.SendLEDs();
	}

	private void addUp() {
	//	println("add all:"+count);
		if (count<(width*height)){
			float huez = count/1000f;
			TColor col = TColor.BLACK.copy();
			col.setHSV(huez, 100, 100);
			lc.setRGB(count, round(col.red()*255), round(col.green()*255), round(col.blue()*255));
			count++;
			//lc.SendLEDs();
		}else{
			startAdding = false;
		}
		
	}

	void strobeOnOff(int theTimes) {
		for (int i = 0; i < theTimes; i++) {
			lc.setAllTo(0, 0, 0);
		//	lc.SendLEDs();
			lc.setAllTo(255, 255, 255);
		//	lc.SendLEDs();
		}
	}

	void sweepWidth() {
		if (sweepCount < 40) {
		//	println("line x:" + sweepCount);
			allOff();
		//	drawLine(sweepCount, 0, sweepCount, height - 1, 84, 201, 255);
			myDrawLineDown(sweepCount, 0, sweepCount, height, 84, 201, 255);
			sweepCount++;
			// refresh();
		//	lc.SendLEDs();
		} else {
			sweepAcross = false;
		}
	}

	void sweepHeight() {
		for (int j = 0; j < height; j++) {
			allOff();
			drawLine(0, j, width - 1, j, 255, 249, 84);
			refresh();
		}
	}
	

	public void runDemo3() {
		
		allOff();
		myDrawLineDown(0, 0, 0, 25, 84, 255, 133 );
		myDrawLineDown(15, 0, 15, 25, 84, 255, 133 );
		myDrawLineAcross(0, 0, 15, 0, 84, 255, 133 );
		myDrawLineAcross(0, 10, 15, 10, 84, 255, 133 );
		myDrawLineDown(18, 0, 18, 24, 84, 255, 133);
		myDrawLineAcross(18, 24, 32, 24, 84, 255, 133 );
		myDrawLineDown(35, 0, 30, 5, 84, 255, 133);
		myDrawLineDown(35, 7, 30, 25, 84, 255, 133);
		
		//lc.SendLEDs();
		// TODO Auto-generated method stub
		
	}

	private void myDrawLineDown(int i, int j, int k, int l, int r, int g, int b) {
		int targHeight = l-j;
		for (int l2 = 0; l2 < targHeight; l2++) {
			int theX = i;
			int theY = j+l2;
			int point = (theY*width)+theX;
			lc.setRGB(point, r, g, b);
		}
	}
	private void myDrawLineAcross(int i, int j, int k, int l, int r, int g, int b) {
		int targWidth= k-i;
		for (int l2 = 0; l2 < targWidth; l2++) {
			int theX = l2+i;
			int theY = j;
			int point = (theY*width)+theX;
			lc.setRGB(point, r, g, b);
		}
	}

	public void runDemo1() {
		// strobeOnOff(10);
		allOff();
		sweepCount = 0;
		sweepAcross = true;
		// sweepHeight();
	}

	public void runDemo2() {
		// sweepWidth();
		// sweepHeight();
	//	println("DEMO 2");
		allOff();
		
		//lc.SendLEDs();
		//delay(1000);
		
		startAdding = true;
		count = 0;
		//
		// for (int i = 0; i < 10; i++) {
		//
		// allOff();
		// drawBox(0, 0, width - 1, height - 1, 123, 67, 89);
		// refresh();
		// delay(2000);
		// allOff();
		// // drawBox(1, 1, width - 2, height - 2, 23, 12, 234);
		// // refresh();
		// // delay(200);
		// // allOff();
		// // drawBox(0, 0, width - 1, height - 1, 34, 23, 56);
		// // drawBox(1, 1, width - 2, height - 2, 56, 123, 78);
		// // refresh();
		// // delay(200);
		// }

	}

	// inner class to handle passing int by reference
	class IntHolder {
		int val;

		IntHolder(int theVal) {
			val = theVal;
		}
	}

	public void runDemo4(PImage img) {
		sweepAcross = false;
		startAdding =  false;
	//	allOff();
		for (int i = 0; i < img.pixels.length; i++) {
			TColor col = TColor.BLACK.copy();
			col.setARGB(img.pixels[i]);
			lc.setRGB(i, round(col.red() *255), round(col.green()*255),round(col.blue()));
			
		}
		//lc.SendLEDs();
		
		// TODO Auto-generated method stub
		
	}

}
