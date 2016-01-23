import javax.swing.JFrame;
import java.awt.AWTException;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Trojan extends JFrame implements KeyListener {

	static int FIELD_WIDTH = 565;
	static int FIELD_HEIGHT = 325;
	static long lastUpdateRed = System.currentTimeMillis();
	static long lastUpdateBlue = System.currentTimeMillis();
	static Stack<String> keysRed = new Stack<String>();
	static Stack<String> keysBlue = new Stack<String>();
	static GameField game = new GameField();
	static int speedRed = 10;
	static int speedBlue = 10;
	static Point p;
	static int length = 0;
	static ToastMessage toastMessage;

	public Trojan() {
		game = new GameField();
		game.setFocusable(true);
		add(game);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
	}

	public static void main(String args[]) {
		Trojan frame = new Trojan();
		WindowListener exitListener = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				File file = new File("scores.txt");
				if (file.exists())
					file.delete();
				System.exit(0);
			}
		};
		frame.addWindowListener(exitListener);
		frame.pack();
		centreWindow(frame);
		frame.setVisible(true);
		p = frame.getLocationOnScreen();
		length = frame.getWidth();
		while (game.animation) {
			System.out.print("");
			continue;
		}
		File file = new File("scores.txt");
		if (file.exists()) {
			Scanner scan = null;
			try {
				scan = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = "";
			while (scan.hasNext())
				line = scan.nextLine();
			int red = Integer.parseInt((line.split("-"))[0]);
			int blue = Integer.parseInt((line.split("-"))[1]);
			Trojan.toastMessage = new ToastMessage("Red: " + red + "\t" + "Blue: " + blue, 500);
			Trojan.toastMessage.setVisible(true);
			Trojan.toastMessage.setFocusableWindowState(false);
		}
		while (true) {
			if (System.currentTimeMillis() - Trojan.lastUpdateRed > Trojan.speedRed) {
				int result = 0;
				if (Trojan.keysRed.isEmpty()) {
					result = Trojan.game.keepRedGoing();
				} else {
					String key = Trojan.keysRed.pop();
					Trojan.keysRed.clear();
					if (key.equals("DOWN") && Trojan.game.directionRed != 0)
						result = Trojan.game.moveRedDown();
					else if (key.equals("UP") && Trojan.game.directionRed != 2)
						result = Trojan.game.moveRedUp();
					else if (key.equals("RIGHT") && Trojan.game.directionRed != 3)
						result = Trojan.game.moveRedRight();
					else if (key.equals("LEFT") && Trojan.game.directionRed != 1)
						result = Trojan.game.moveRedLeft();
				}
				if (result == -1) {
					Trojan.game.redDead = true;
					game.repaint();
					new Thread() {
						public void run() {
							Trojan.toastMessage = new ToastMessage(
									"Press the space bar key to restart or the esc key to exit!", 5000);
							Trojan.toastMessage.setVisible(true);
							Trojan.toastMessage.setFocusableWindowState(false);
						}
					}.start();
					break;
				}
				Trojan.lastUpdateRed = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() - Trojan.lastUpdateBlue > Trojan.speedBlue) {
				int result = 0;
				if (Trojan.keysBlue.isEmpty()) {
					result = Trojan.game.keepBlueGoing();
				} else {
					String key = Trojan.keysBlue.pop();
					Trojan.keysBlue.clear();
					if (key.equals("BDOWN") && Trojan.game.directionBlue != 0)
						result = Trojan.game.moveBlueDown();
					else if (key.equals("BUP") && Trojan.game.directionBlue != 2)
						result = Trojan.game.moveBlueUp();
					else if (key.equals("BRIGHT") && Trojan.game.directionBlue != 3)
						result = Trojan.game.moveBlueRight();
					else if (key.equals("BLEFT") && Trojan.game.directionBlue != 1)
						result = Trojan.game.moveBlueLeft();
				}
				if (result == -1) {
					Trojan.game.blueDead = true;
					game.repaint();
					new Thread() {
						public void run() {
							Trojan.toastMessage = new ToastMessage(
									"Press the space bar key to restart or the esc key to exit!", 5000);
							Trojan.toastMessage.setVisible(true);
							Trojan.toastMessage.setFocusableWindowState(false);
						}
					}.start();
					break;
				}
				Trojan.lastUpdateBlue = System.currentTimeMillis();
			}
			Trojan.game.repaint();
		}
		while (!game.redDead && !game.blueDead) {
			System.out.print("");
			continue;
		}
		boolean spaceBarPressed = false;
		while (!spaceBarPressed) {
			String key = "";
			if (!keysRed.isEmpty()) {
				key = keysRed.pop();
				keysRed.clear();
			}
			if (key.equals("SPACE")) {
				System.out.println(game.redDead + "|" + game.blueDead);
				File file1 = new File("scores.txt");
				if (!file1.exists()) {
					try {
						file1.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					FileWriter fw = null;
					try {
						fw = new FileWriter(file1.getAbsoluteFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(game.redDead + "|" + game.blueDead);
					BufferedWriter bw = new BufferedWriter(fw);
					if (game.redDead) {
						try {
							bw.write("0-1");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (game.blueDead) {
						try {
							bw.write("1-0");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Scanner scan = null;
					try {
						scan = new Scanner(file1);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String line = "";
					while (scan.hasNext())
						line = scan.nextLine();
					int red = Integer.parseInt((line.split("-"))[0]);
					int blue = Integer.parseInt((line.split("-"))[1]);
					System.out.println(red + "|" + blue);
					FileWriter fw = null;
					try {
						fw = new FileWriter(file1.getAbsoluteFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BufferedWriter bw = new BufferedWriter(fw);
					if (game.redDead) {
						blue++;
						try {
							bw.write(red + "-" + blue);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else if (game.blueDead) {
						red++;
						try {
							bw.write(red + "-" + blue);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						bw.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				toastMessage.dispose();
				main(args);
			} else if (key.equals("ESC")) {
				File file1 = new File("scores.txt");
				if (file.exists()) {
					Scanner scan = null;
					try {
						scan = new Scanner(file);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String line = "";
					while (scan.hasNext())
						line = scan.nextLine();
					int red = Integer.parseInt((line.split("-"))[0]);
					int blue = Integer.parseInt((line.split("-"))[1]);
					Trojan.toastMessage.setVisible(false);
					Trojan.toastMessage = new ToastMessage("The final score is Red: " + red + "\t" + "Blue: " + blue, 2000);
					Trojan.toastMessage.setVisible(true);
					Trojan.toastMessage.setFocusableWindowState(false);
					file.delete();
				}
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						System.exit(0);
					}
				}, 2100);
			} else {
			}
		}
	}
	
	public static void centreWindow(Window frame) {
	    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
	    frame.setLocation(x, y);
	}
	
	public static int getRGB(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			Trojan.keysRed.push("DOWN");
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			Trojan.keysRed.push("UP");
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			Trojan.keysRed.push("LEFT");
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			Trojan.keysRed.push("RIGHT");
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			Trojan.keysRed.push("ESC");
			Trojan.keysBlue.push("ESC");
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			Trojan.keysRed.push("SPACE");
			Trojan.keysBlue.push("SPACE");
		} else if (e.getKeyCode() == KeyEvent.VK_W) {
			Trojan.keysBlue.push("BUP");
		} else if (e.getKeyCode() == KeyEvent.VK_S) {
			Trojan.keysBlue.push("BDOWN");
		} else if (e.getKeyCode() == KeyEvent.VK_A) {
			Trojan.keysBlue.push("BLEFT");
		} else if (e.getKeyCode() == KeyEvent.VK_D) {
			Trojan.keysBlue.push("BRIGHT");
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}
}

class GameField extends JPanel implements ActionListener {

	public BufferedImage img;
	private BufferedImage redCar;
	private BufferedImage blueCar;
	private BufferedImage logo;
	private BufferedImage explosion;
	int[] currentRedPixel;
	int[] currentBluePixel;
	public int directionRed = 1;
	public int directionBlue = 3;
	int[] red = { 255, 0, 0 };
	int[] black = { 0, 0, 0 };
	int[] blue = { 0, 0, 255 };
	public boolean animation = true;
	public boolean redDead = false;
	public boolean blueDead = false;
	Timer timer = new Timer(8, this);
	private float alpha = 1f;
	public boolean restartMess = false;

	public GameField() {
		currentRedPixel = new int[2];
		currentBluePixel = new int[2];
		InputStream input = Trojan.class.getClassLoader().getResourceAsStream("map.png");
		try {
			img = ImageIO.read(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		InputStream input2 = Trojan.class.getClassLoader().getResourceAsStream("red.png");
		try {
			redCar = ImageIO.read(input2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input3 = Trojan.class.getClassLoader().getResourceAsStream("blue.png");
		try {
			blueCar = ImageIO.read(input3);
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input4 = Trojan.class.getClassLoader().getResourceAsStream("logo.png");
		try {
			logo = ImageIO.read(input4);
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input5 = Trojan.class.getClassLoader().getResourceAsStream("explosion.png");
		try {
			explosion = ImageIO.read(input5);
		} catch (IOException e) {
			e.printStackTrace();
		}

		currentRedPixel[0] = img.getWidth() / 2 - 100;
		currentRedPixel[1] = img.getHeight() / 2;
		img.setRGB(currentRedPixel[0], currentRedPixel[1], getRGB(255, 0, 0));
		currentBluePixel[0] = img.getWidth() / 2 + 100;
		currentBluePixel[1] = img.getHeight() / 2;
		img.setRGB(currentBluePixel[0], currentBluePixel[1], getRGB(0, 0, 255));
		double radians;
		radians = -Math.PI;
		AffineTransform tx = new AffineTransform();
		tx.rotate(radians, blueCar.getWidth() / 2, blueCar.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		blueCar = op.filter(blueCar, null);
		timer.start();
	}

	public int moveRedRight() {
		int[] nextPixel = { currentRedPixel[0] + 1, currentRedPixel[1] };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentRedPixel[0] + 6, currentRedPixel[1] - 1));
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentRedPixel = nextPixel;
		img.setRGB(currentRedPixel[0], currentRedPixel[1], getRGB(255, 0, 0));
		redCar = rotate(directionRed, 1, redCar);
		directionRed = 1;
		return 1;
	}

	public int moveRedLeft() {
		int[] nextPixel = { currentRedPixel[0] - 1, currentRedPixel[1] };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentRedPixel[0] - 6, currentRedPixel[1] - 1));
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentRedPixel = nextPixel;
		img.setRGB(currentRedPixel[0], currentRedPixel[1], getRGB(255, 0, 0));
		redCar = rotate(directionRed, 3, redCar);
		directionRed = 3;
		return 1;
	}

	public int moveRedUp() {
		int[] nextPixel = { currentRedPixel[0], currentRedPixel[1] - 1 };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentRedPixel[0] - 1, currentRedPixel[1] - 6));
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentRedPixel = nextPixel;
		img.setRGB(currentRedPixel[0], currentRedPixel[1], getRGB(255, 0, 0));
		redCar = rotate(directionRed, 0, redCar);
		directionRed = 0;
		return 1;
	}

	public int moveRedDown() {
		int[] nextPixel = { currentRedPixel[0], currentRedPixel[1] + 1 };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentRedPixel[0] - 1, currentRedPixel[1] + 6));
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentRedPixel = nextPixel;
		img.setRGB(currentRedPixel[0], currentRedPixel[1], getRGB(255, 0, 0));
		redCar = rotate(directionRed, 2, redCar);
		directionRed = 2;
		return 1;
	}

	public int moveBlueRight() {
		int[] nextPixel = { currentBluePixel[0] + 1, currentBluePixel[1] };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentBluePixel[0] + 6, currentBluePixel[1] - 1));
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentBluePixel = nextPixel;
		img.setRGB(currentBluePixel[0], currentBluePixel[1], getRGB(0, 0, 255));
		blueCar = rotate(directionBlue, 1, blueCar);
		directionBlue = 1;
		return 1;
	}

	public int moveBlueLeft() {
		int[] nextPixel = { currentBluePixel[0] - 1, currentBluePixel[1] };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentBluePixel[0] - 6, currentBluePixel[1] - 1));
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentBluePixel = nextPixel;
		img.setRGB(currentBluePixel[0], currentBluePixel[1], getRGB(0, 0, 255));
		blueCar = rotate(directionBlue, 3, blueCar);
		directionBlue = 3;
		return 1;
	}

	public int moveBlueUp() {
		int[] nextPixel = { currentBluePixel[0], currentBluePixel[1] - 1 };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentBluePixel[0] - 1, currentBluePixel[1] - 6));
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentBluePixel = nextPixel;
		img.setRGB(currentBluePixel[0], currentBluePixel[1], getRGB(0, 0, 255));
		blueCar = rotate(directionBlue, 0, blueCar);
		directionBlue = 0;
		return 1;
	}

	public int moveBlueDown() {
		int[] nextPixel = { currentBluePixel[0], currentBluePixel[1] + 1 };
		Color color = new Color(img.getRGB(nextPixel[0], nextPixel[1]));
		Color color2 = new Color(img.getRGB(currentBluePixel[0] - 1, currentBluePixel[1] + 6));
		int[] rgb2 = { color2.getRed(), color2.getGreen(), color2.getBlue() };
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		if (Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue) || Arrays.equals(rgb2, red)
				|| Arrays.equals(rgb2, black) || Arrays.equals(rgb2, blue))
			return -1;
		currentBluePixel = nextPixel;
		img.setRGB(currentBluePixel[0], currentBluePixel[1], getRGB(0, 0, 255));
		blueCar = rotate(directionBlue, 2, blueCar);
		directionBlue = 2;
		return 1;
	}

	public int[] nextColor(int[] currentLoc, int direction) {
		if (direction == 0) {
			int[] rgb = getRGB(currentLoc[0], currentLoc[1]);
			while (!(Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue))) {
				int[] newLoc = { currentLoc[0], currentLoc[1] - 1 };
				currentLoc = newLoc;
				rgb = getRGB(currentLoc[0], currentLoc[1]);
			}
			return currentLoc;
		} else if (direction == 1) {
			int[] rgb = getRGB(currentLoc[0], currentLoc[1]);
			while (!(Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue))) {
				int[] newLoc = { currentLoc[0] + 1, currentLoc[1] };
				currentLoc = newLoc;
				rgb = getRGB(currentLoc[0], currentLoc[1]);
			}
			return currentLoc;
		} else if (direction == 2) {
			int[] rgb = getRGB(currentLoc[0], currentLoc[1]);
			while (!(Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue))) {
				int[] newLoc = { currentLoc[0], currentLoc[1] };
				currentLoc = newLoc;
				rgb = getRGB(currentLoc[0], currentLoc[1] + 1);
			}
			return currentLoc;
		} else if (direction == 3) {
			int[] rgb = getRGB(currentLoc[0], currentLoc[1]);
			while (!(Arrays.equals(rgb, red) || Arrays.equals(rgb, black) || Arrays.equals(rgb, blue))) {
				int[] newLoc = { currentLoc[0] - 1, currentLoc[1] };
				currentLoc = newLoc;
				rgb = getRGB(currentLoc[0], currentLoc[1]);
			}
			return currentLoc;
		} else {
		}
		return currentLoc;
	}

	public int keepRedGoing() {
		int result = 0;
		switch (directionRed) {
		case 0:
			result = moveRedUp();
			break;
		case 1:
			result = moveRedRight();
			break;
		case 2:
			result = moveRedDown();
			break;
		case 3:
			result = moveRedLeft();
			break;
		}
		return result;
	}

	public int keepBlueGoing() {
		int result = 0;
		switch (directionBlue) {
		case 0:
			result = moveBlueUp();
			break;
		case 1:
			result = moveBlueRight();
			break;
		case 2:
			result = moveBlueDown();
			break;
		case 3:
			result = moveBlueLeft();
			break;
		}
		return result;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(565, 325);
	}

	public int getRGB(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	public int[] getRGB(int x, int y) {
		Color color = new Color(img.getRGB(x, y));
		int[] rgb = { color.getRed(), color.getGreen(), color.getBlue() };
		return rgb;
	}

	public boolean contains(HashSet<int[]> c, int[] a) {
		for (int[] b : c)
			if (Arrays.equals(a, b))
				return true;
		return false;
	}

	public HashSet<int[]> getOutline(BufferedImage im) {
		HashSet<int[]> outline = new HashSet<int[]>();
		int height = im.getHeight();
		int width = im.getWidth();
		for (int i = 0; i < width; i++) {
			System.out.println(i);
			int heightOn = 0;
			while (heightOn < height && isTransparent(im, i, heightOn)) {
				heightOn++;
			}
			heightOn--;
			if (heightOn != height) {
				int[] lol = { i, heightOn };
				outline.add(lol);
			}
			heightOn = height - 1;
			while (heightOn > 0 && isTransparent(im, i, heightOn)) {
				heightOn--;
			}
			heightOn++;
			if (heightOn != 0) {
				int[] lol = { i, heightOn };
				outline.add(lol);
			}
		}
		for (int i = 0; i < height; i++) {
			int widthOn = 0;
			while (widthOn < width && isTransparent(im, widthOn, i)) {
				widthOn++;
			}
			widthOn--;
			if (widthOn != width) {
				int[] lol = { widthOn, i };
				outline.add(lol);
			}
			widthOn = width - 1;
			while (widthOn > 0 && isTransparent(im, widthOn, i)) {
				widthOn--;
			}
			widthOn++;
			if (widthOn != 0) {
				int[] lol = { widthOn, i };
				outline.add(lol);
			}
		}
		for (Iterator<int[]> i = outline.iterator(); i.hasNext();) {
			int[] element = i.next();
			int x = element[0];
			int y = element[1];
			if (x == 0 || x == 51 || y == 0 || y == 51 || x == 1 || x == 50 || y == 1 || y == 50) {
				i.remove();
			}
		}
		return outline;
	}

	public boolean isTransparent(BufferedImage i, int x, int y) {
		int pixel = i.getRGB(x, y);
		if ((pixel >> 24) == 0x00)
			return true;
		return false;
	}

	public void drawCenteredCircle(Graphics2D g, int x, int y, int r) {
		x = x - (r / 2);
		y = y - (r / 2);
		g.fillOval(x, y, r, r);
	}

	public BufferedImage rotate(int prev, int dir, BufferedImage im) {
		AffineTransform tx = new AffineTransform();
		double radians = 0.0;
		if (prev == 3 && dir == 0)
			radians = Math.PI / 2;
		else if (prev == 1 && dir == 0)
			radians = -Math.PI / 2;
		else if (prev == 2 && dir == 1)
			radians = -Math.PI / 2;
		else if (prev == 0 && dir == 1)
			radians = Math.PI / 2;
		else if (prev == 3 && dir == 2)
			radians = -Math.PI / 2;
		else if (prev == 1 && dir == 2)
			radians = Math.PI / 2;
		else if (prev == 2 && dir == 3)
			radians = Math.PI / 2;
		else if (prev == 0 && dir == 3)
			radians = -Math.PI / 2;
		tx.rotate(radians, im.getWidth() / 2, im.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		im = op.filter(im, null);
		return im;
	}

	public void actionPerformed(ActionEvent e) {
		alpha += -0.01f;
		if (alpha <= 0) {
			alpha = 0;
			timer.stop();
			animation = false;
		}
		repaint();
	}

	public HashSet<int[]> updateOutline(int[] base, HashSet<int[]> out) {
		HashSet<int[]> outline = new HashSet<int[]>();
		for (int[] obj : out) {
			int x = obj[0];
			int y = obj[1];
			int newX = base[0] + x;
			int newY = base[1] + y;
			int[] a = { newX, newY };
			outline.add(a);
		}
		return outline;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (animation) {
			g.drawImage(img, 0, 0, null);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.drawImage(logo, img.getWidth() / 2 - logo.getWidth() / 2, img.getHeight() / 2 - logo.getHeight() / 2,
					null);
		} else if (redDead) {
			g.drawImage(img, 0, 0, null);
			int[] b = new int[2];
			switch (directionBlue) {
			case 0:
				int[] ab = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 + 5 };
				b = ab;
				break;
			case 1:
				int[] cb = { currentBluePixel[0] - blueCar.getWidth() / 2 - 5,
						currentBluePixel[1] - blueCar.getHeight() / 2 };
				b = cb;
				break;
			case 2:
				int[] db = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 - 5 };
				b = db;
				break;
			default:
				int[] eb = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 };
				b = eb;
				break;
			}
			g.drawImage(blueCar, b[0], b[1], null);
			b = new int[2];
			int[] ab = { currentRedPixel[0] - explosion.getWidth() / 2,
					currentRedPixel[1] - explosion.getHeight() / 2 };
			b = ab;
			g.drawImage(explosion, b[0], b[1], null);
			restartMess = true;
		} else if (blueDead) {
			g.drawImage(img, 0, 0, null);
			int[] b = new int[2];
			switch (directionRed) {
			case 0:
				int[] ab = { currentRedPixel[0] - redCar.getWidth() / 2,
						currentRedPixel[1] - redCar.getHeight() / 2 + 5 };
				b = ab;
				break;
			case 1:
				int[] cb = { currentRedPixel[0] - redCar.getWidth() / 2 - 5,
						currentRedPixel[1] - redCar.getHeight() / 2 };
				b = cb;
				break;
			case 2:
				int[] db = { currentRedPixel[0] - redCar.getWidth() / 2,
						currentRedPixel[1] - redCar.getHeight() / 2 - 5 };
				b = db;
				break;
			default:
				int[] eb = { currentRedPixel[0] - redCar.getWidth() / 2, currentRedPixel[1] - redCar.getHeight() / 2 };
				b = eb;
				break;
			}
			g.drawImage(redCar, b[0], b[1], null);
			b = new int[2];
			int[] ab = { currentBluePixel[0] - explosion.getWidth() / 2,
					currentBluePixel[1] - explosion.getHeight() / 2 };
			b = ab;
			g.drawImage(explosion, b[0], b[1], null);
			restartMess = true;
		} else {
			g.drawImage(img, 0, 0, null);
			int[] b = new int[2];
			switch (directionRed) {
			case 0:
				int[] ab = { currentRedPixel[0] - redCar.getWidth() / 2,
						currentRedPixel[1] - redCar.getHeight() / 2 + 5 };
				b = ab;
				break;
			case 1:
				int[] cb = { currentRedPixel[0] - redCar.getWidth() / 2 - 5,
						currentRedPixel[1] - redCar.getHeight() / 2 };
				b = cb;
				break;
			case 2:
				int[] db = { currentRedPixel[0] - redCar.getWidth() / 2,
						currentRedPixel[1] - redCar.getHeight() / 2 - 5 };
				b = db;
				break;
			default:
				int[] eb = { currentRedPixel[0] - redCar.getWidth() / 2, currentRedPixel[1] - redCar.getHeight() / 2 };
				b = eb;
				break;
			}
			g.drawImage(redCar, b[0], b[1], null);
			b = new int[2];
			switch (directionBlue) {
			case 0:
				int[] ab = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 + 5 };
				b = ab;
				break;
			case 1:
				int[] cb = { currentBluePixel[0] - blueCar.getWidth() / 2 - 5,
						currentBluePixel[1] - blueCar.getHeight() / 2 };
				b = cb;
				break;
			case 2:
				int[] db = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 - 5 };
				b = db;
				break;
			default:
				int[] eb = { currentBluePixel[0] - blueCar.getWidth() / 2,
						currentBluePixel[1] - blueCar.getHeight() / 2 };
				b = eb;
				break;
			}
			g.drawImage(blueCar, b[0], b[1], null);
		}
	}
}