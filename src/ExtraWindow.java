/*
From processing.org forum
 */
import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import processing.core.PApplet;

class ExtraWindow extends PApplet implements WindowListener, ComponentListener {

	protected int width = 600;
	protected int height = 200;
	protected int x = 100;
	protected int y = 100;
	protected String myName;
	protected String myTitle;
	protected boolean isCoordinates = false;
	protected boolean isLoop = true;
	public final static int NORMAL = 0;
	public final static int ECONOMIC = 1;
	protected int myMode = NORMAL;
	protected String myRenderer = "";
	protected int myFrameRate;
	boolean isUndecorated = false;
	PApplet papplet;

	public ExtraWindow(PApplet theApplet) {
		super();
		papplet = theApplet;
	}

	public ExtraWindow(PApplet theApplet, String theName, int theWidth,
			int theHeight) {
		this(theApplet, theName, theWidth, theHeight, "", 30);
	}

	public ExtraWindow(PApplet theApplet, String theName, int theWidth,
			int theHeight, String theRenderer, int theFrameRate) {
		super();
		papplet = theApplet;
		myName = theName;
		myTitle = theName;
		width = theWidth;
		height = theHeight;
		myFrameRate = theFrameRate;
		myRenderer = theRenderer;
		launch();
	}

	public ExtraWindow(PApplet theApplet, final String theName, int theX,
			int theY, int theWidth, int theHeight) {
		this(theApplet, theName, theX, theY, theWidth, theHeight, "", 30);
	}

	public ExtraWindow(PApplet theApplet, String theName, int theX, int theY,
			int theWidth, int theHeight, String theRenderer, int theFrameRate) {
		super();
		papplet = theApplet;
		myName = theName;
		myTitle = theName;
		width = theWidth;
		height = theHeight;
		x = theX;
		y = theY;
		myFrameRate = theFrameRate;
		myRenderer = theRenderer;
		launch();
	}

	public void setup() {
		if (myRenderer.length() == 0) {
			size(width, height);
		} else {
			size(width, height, myRenderer);
		}
		frameRate(myFrameRate);
	}

	public void draw() {
	}

	public Frame getFrame() {
		return frame;
	}

	public String name() {
		return myName;
	}

	public void setVisible(boolean theValue) {
		if (theValue == true) {
			frame.show();
		} else {
			frame.hide();
		}
	}

	public void setResizeable(boolean theValue) {
		frame.setResizable(theValue);
	}

	public void setTitle(String theTitle) {
		myTitle = theTitle;
		updateTitle();
	}

	protected void updateTitle() {
		String m = myTitle;
		if (isCoordinates) {
			m += " x:" + x + " y:" + y + "   " + width + "x" + height;
		}
		frame.setTitle(m);
	}

	public String title() {
		return myTitle;
	}

	public void showCoordinates() {
		isCoordinates = true;
		updateTitle();
	}

	public void hideCoordinates() {
		isCoordinates = false;
		updateTitle();
	}

	public void windowActivated(WindowEvent e) {
		isLoop = true;
		loop();
	}

	public void keyPressed(KeyEvent theKeyEvent) {
		papplet.keyPressed(theKeyEvent);
	}

	public void keyReleased(KeyEvent theKeyEvent) {
		papplet.keyReleased(theKeyEvent);
	}

	public void keyTyped(KeyEvent theKeyEvent) {
		papplet.keyTyped(theKeyEvent);
	}

	int mX;
	int mY;

	public void mousePressed() {
		if (isUndecorated) {
			mX = mouseX;
			mY = mouseY;
		}
	}

	public void mouseDragged() {
		if (isUndecorated) {
			frame.setLocation(MouseInfo.getPointerInfo().getLocation().x - mX,
					MouseInfo.getPointerInfo().getLocation().y - mY);
		}
	}

	public void windowClosed(WindowEvent e) {
		// System.out.println("window closed");
	}

	public void windowClosing(WindowEvent e) {
		dispose();
	}

	public void windowDeactivated(WindowEvent e) {
		if (myMode == ECONOMIC) {
			isLoop = false;
			noLoop();
		}
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
		Component c = e.getComponent();
		x = c.getLocation().x;
		y = c.getLocation().y;
		updateTitle();
	}

	public void componentResized(ComponentEvent e) {
		Component c = e.getComponent();
		// System.out.println("componentResized event from " +
		// c.getClass().getName() + "; new size: " + c.getSize().width
		// + ", " + c.getSize().height);
	}

	public void componentShown(ComponentEvent e) {
		// System.out.println("componentShown event from " +
		// e.getComponent().getClass().getName());
	}

	public void setMode(int theValue) {
		if (theValue == ECONOMIC) {
			myMode = ECONOMIC;
			return;
		}
		myMode = NORMAL;
	}

	public void toggleUndecorated() {
		setUndecorated(!isUndecorated());
	}

	public void setUndecorated(boolean theFlag) {
		if (theFlag != isUndecorated()) {
			isUndecorated = theFlag;
			frame.removeNotify();
			frame.setUndecorated(isUndecorated);
			setSize(width, height);
			setBounds(0, 0, width, height);
			frame.setSize(width, height);
			frame.addNotify();
			requestFocus();
		}
	}

	public boolean isUndecorated() {
		return isUndecorated;
	}

	public void dispose() {
		stop();
		removeAll();
		frame.removeAll();
		frame.dispose();
	}

	private void launch() {
		GraphicsDevice displayDevice = null;
		if (displayDevice == null) {
			GraphicsEnvironment environment = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			displayDevice = environment.getDefaultScreenDevice();
		}

		frame = new Frame(displayDevice.getDefaultConfiguration());
		// remove the grow box by default
		// users who want it back can call frame.setResizable(true)
		frame.setResizable(false);
		init();

		frame.pack(); // get insets. get more.
		frame.setLocation(x, y);
		Insets insets = frame.getInsets();

		int windowW = Math.max(width, MIN_WINDOW_WIDTH) + insets.left
				+ insets.right;
		int windowH = Math.max(height, MIN_WINDOW_HEIGHT) + insets.top
				+ insets.bottom;

		frame.setSize(windowW, windowH);
		frame.setLayout(null);
		frame.add(this);
		int usableWindowH = windowH - insets.top - insets.bottom;
		setBounds((windowW - width) / 2, insets.top + (usableWindowH - height)
				/ 2, width, height);

		frame.addWindowListener(this);
		frame.addComponentListener(this);
		frame.setName(myName);
		frame.setTitle(myName + " x:" + x + " y:" + y + "   w:" + width + " h:"
				+ height);
		frame.setVisible(true);
		requestFocus();
	}
}
