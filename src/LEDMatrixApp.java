import processing.serial.Serial;
import processing.core.*;

public class LEDMatrixApp extends PApplet {

	// ======================================== END LC & Matrix
	int currentcolor;

	RectButton rect1, rect2;
	boolean locked = false;

	private Serial myPort;

	private LEDController lc;
	private LEDMatrix matrix;

	public void setup() {
		// --- LED Controller setup

		String[] lister = Serial.list();
		
		
		println("hi hi hi hi");

		lc = new LEDController(1000, this);
		matrix = new LEDMatrix(lc, 5, 4);

		myPort = new Serial(this, Serial.list()[0], 115200);
		lc.begin(myPort);

		// --- LED Controller setup END

		size(400, 400);
		smooth();

		int baseColor = color(102);

		// Define and create circle button
		int buttoncolor = color(204);
		int highlight = color(153);
		ellipseMode(CENTER);
		currentcolor = baseColor;

		// Define and create rectangle button
		buttoncolor = color(102);
		highlight = color(51);
		rect1 = new RectButton(20, 20, 100, buttoncolor, highlight);

		// Define and create rectangle button
		buttoncolor = color(51);
		highlight = color(0);
		rect2 = new RectButton(160, 20, 100, buttoncolor, highlight);
	}

	public void draw() {

		background(currentcolor);
		lc.display();
		stroke(255);
		update(mouseX, mouseY);
		rect1.display();
		rect2.display();

	}

	void update(int x, int y) {
		if (locked == false) {
			rect1.update();
			rect2.update();
		} else {
			locked = false;
		}

		if (mousePressed) {
			if (rect1.pressed()) {
				currentcolor = rect1.basecolor;
			//	matrix.runDemo1();
				
			} else if (rect2.pressed()) {
				currentcolor = rect2.basecolor;
				matrix.runDemo2();
			}
		}
	}

	class Button {
		int x, y;
		int size;
		int basecolor, highlightcolor;
		int currentcolor;
		boolean over = false;
		boolean pressed = false;

		void update() {
			if (over()) {
				currentcolor = highlightcolor;
			} else {
				currentcolor = basecolor;
			}
		}

		boolean pressed() {
			if (over) {
				locked = true;
				return true;
			} else {
				locked = false;
				return false;
			}
		}

		boolean over() {
			return true;
		}

		boolean overRect(int x, int y, int width, int height) {
			if (mouseX >= x && mouseX <= x + width && mouseY >= y
					&& mouseY <= y + height) {
				return true;
			} else {
				return false;
			}
		}

		boolean overCircle(int x, int y, int diameter) {
			float disX = x - mouseX;
			float disY = y - mouseY;
			if (sqrt(sq(disX) + sq(disY)) < diameter / 2) {
				return true;
			} else {
				return false;
			}
		}
	}

	class RectButton extends Button {
		RectButton(int ix, int iy, int isize, int icolor, int ihighlight) {
			x = ix;
			y = iy;
			size = isize;
			basecolor = icolor;
			highlightcolor = ihighlight;
			currentcolor = basecolor;
		}

		boolean over() {
			if (overRect(x, y, size, size)) {
				over = true;
				return true;
			} else {
				over = false;
				return false;
			}
		}

		void display() {
			stroke(255);
			fill(currentcolor);
			rect(x, y, size, size);
		}
	}

}
