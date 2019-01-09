import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.*;

public class Filler extends JPanel{
	
	
	//initializing canvas with width and height globally to use it throughout the code
	Canvas canvas = new Canvas(109, 197);
	
	//rest of graphics and switches global
	JFrame frame;
	JSlider slider;
	boolean fill = false;
	boolean draw = true;
	boolean erase = false;
	
	//current editing color
	Color editing_color = Color.black;
	Color[][] colors;
	
	private class Canvas extends JPanel implements MouseMotionListener, MouseListener{
		
		//the compressed canvas
		
		//keeping track of every fill to undo
		ArrayList<ArrayList<Pixel>> fills = new ArrayList<ArrayList<Pixel>>();
		
		
		public Canvas(int height, int width) {
			
			colors = new Color[height][width];
			this.addMouseMotionListener(this);
			this.addMouseListener(this);
			
			//start the canvas white
			for (int i = 0; i < colors.length; i++) {
				for (int j = 0; j < colors[i].length; j++) {
					colors[i][j] = Color.white;
				}
			}
			
			super.setMinimumSize(new Dimension(500, 400));
			
		}
		
		
		
		Color starting_color = null;
		
		public void fill() {
			
			//get the pixel clicked
			Pixel current = new Pixel((int)this.getMousePosition().getX()/6, (int)this.getMousePosition().getY()/6);
			
			//if the pixel we want to fill is already the color chosen, then don't fill
			if(colors[current.y][current.x].equals(editing_color)) {
				return;
			}
			
			//keeping track of every pixel filled so we can undo later
			ArrayList<Pixel> pixels_filled = new ArrayList<Pixel>();
			
			ArrayList<Pixel> to_visit = new ArrayList<Pixel>();
			ArrayList<Pixel> visited = new ArrayList<Pixel>();
			
			
			//color of the pixel clicked
			starting_color = colors[current.y][current.x];
			
			to_visit.add(current);
			
			while(to_visit.size() > 0) {
				
				//take the first pixel in the to_visit list, color it, add it to the visited and the pixels_filled list, and remove it from to_visited
				current = to_visit.get(0);
				colors[current.y][current.x] = editing_color;
				pixels_filled.add(current);
				to_visit.remove(0);
				visited.add(current);
				
				
				//if the neighbors have't been visited already (or are out of bounds or the border), then add them to the t_visit list
				
				
				Pixel above = new Pixel(current.x, current.y-1);
				//does this exist in visited?? is the color here not the same as the starting color?
				if(!contains(above, visited) && !contains(above, to_visit) && above.y >= 0 && colors[above.y][above.x].equals(starting_color)) {
					to_visit.add(above);
				}
				
				Pixel below = new Pixel(current.x, current.y+1);
				//does this exist in visited?? is the color here not the same as the starting color?
				if(!contains(below, visited) && !contains(below, to_visit) && below.y < colors.length && colors[below.y][below.x].equals(starting_color)) {
					to_visit.add(below);
				}
				
				Pixel right = new Pixel(current.x+1, current.y);
				//does this exist in visited?? is the color here not the same as the starting color?
				if(!contains(right, visited) && !contains(right, to_visit) && right.x < colors[0].length && colors[right.y][right.x].equals(starting_color)) {
					to_visit.add(right);
				}
				
				Pixel left = new Pixel(current.x-1, current.y);
				//does this exist in visited?? is the color here not the same as the starting color?
				if(!contains(left, visited) && !contains(left, to_visit) && left.x >= 0 && colors[left.y][left.x].equals(starting_color)) {
					to_visit.add(left);
				}
				
				
			}
			
			
			fills.add(pixels_filled);
			
		}
		
		public boolean contains(Pixel neighbor, ArrayList<Pixel> list) {
			for (Pixel point : list) {
				if(point.x == neighbor.x && point.y == neighbor.y)
					return true;
			}
			return false;
		}
		
		public void undoFill() {
			
			if(fills.size()>0) {
				ArrayList<Pixel> pixels_filled = fills.get(0);
				for (int i = 0; i < pixels_filled.size(); i++) {
					colors[pixels_filled.get(i).y][pixels_filled.get(i).x] = starting_color;
					pixels_filled.remove(i);
					i--;
				}
				
				fills.remove(0);
			}
			
			
		}
		
		
		@Override
		public void paint(Graphics g) {
			
			//this is necessary because it makes sure that all the lightweight components to this JPanel are painted
			//according to Java docs...
			super.paint(g);
			
			//expanding the compressed canvas (by factor of 6) to the full-size canvas
			//each compressed pixel covers 6 normal pixels
			for (int i = 0 ; i < colors.length ; i++) {
				for (int j = 0; j < colors[i].length; j++) {
					g.setColor(colors[i][j]);
					g.fillRect(j * 6, i * 6, 6, 6);
				}
			}
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			int weight = slider.getValue() - 1;
			
			try {
				
				int mouseY = (int)this.getMousePosition().getY();
				int mouseX = (int)this.getMousePosition().getX();
				
				
				if(draw) {
					//if slider value is zero, then just paint one pixel, otherwise 
					//paint box around it
					if(weight > 0) {
						//start at the top left corner and make our way down the "box"
						for (int i = mouseY/6 - (weight*2+1); i <= mouseY/6 +  (weight*2+1) && weight > 0; i++) {
							for (int j = mouseX/6 - (weight*2+1); j <= mouseX/6 + (weight*2+1); j++) {
								colors[i][j] = editing_color;
							}
						}
					}else {
						colors[(int)((int)this.getMousePosition().getY()/6)][(int)((int)this.getMousePosition().getX()/6)] = editing_color;
					}
				
				
				}
				if(erase) {
					//same with the erase
					if(weight > 0) {
						for (int i = mouseY/6 - (weight*2+1); i <= mouseY/6 +  (weight*2+1) && weight > 0; i++) {
							for (int j = mouseX/6 - (weight*2+1); j <= mouseX/6 + (weight*2+1); j++) {
								colors[i][j] = Color.white;
							}
						}
					}else {
						colors[(int)((int)this.getMousePosition().getY()/6)][(int)((int)this.getMousePosition().getX()/6)] = Color.WHITE;
					}
				}
				
			} catch (Exception e2) {
				// TODO: handle exception
				System.out.println();
			}
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
			//handling fill switch for button click
			if(fill) {
				draw = false;
				try {
					canvas.fill();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
				
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public Filler() {
		
		frame = new JFrame();
		frame.setSize(1200, 804);
		frame.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		BoxLayout box = new BoxLayout(this, BoxLayout.Y_AXIS);
		
		setLayout(box);
		
		GridLayout tool_grid = new GridLayout(2,3, 30, 20);
		
		JPanel tool_panel = new JPanel();
		
		tool_panel.setLayout(tool_grid);
		
		tool_panel.setMaximumSize(new Dimension(600, 600));
		
		JButton btn_pen = new JButton("Pen");
		
		btn_pen.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				draw = true;
				erase = false;
				fill = false;
				
			}
		});
		
		JButton btn_eraser = new JButton("Eraser");
		
		btn_eraser.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				erase = true;
				draw = false;
				//frame.setCursor(getToolkit().createCustomCursor(cursorImage, new Point(0,0), "erase cursor"));
			}
		});
		
		JButton btn_fill = new JButton("Fill");
		btn_fill.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("sss");
				fill = true;
				System.out.println(fill);
				draw = false;
				erase = false;
				
				
			}
		});
		
		JButton btn_undo = new JButton("Undo");
		btn_undo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.out.println("clicked");
				canvas.undoFill();
			}
		});
		
		
		JButton color_picker = new JButton();
		color_picker.setMaximumSize(new Dimension(25,25));
		color_picker.setBackground(Color.black);
		color_picker.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				editing_color = JColorChooser.showDialog(frame, "Set a color.", Color.black);
				color_picker.setBackground(editing_color);
			}
		});
		
		slider = new JSlider();
		slider.setValue(1);
		slider.setPaintLabels(true);
		slider.setMinorTickSpacing(1);
		slider.setMinimum(1);
		slider.setMaximum(4);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setBounds(76, 171, 200, 26);
		//create labels for the slider using a hashtable
		Dictionary labelTable = new Hashtable();
		labelTable.put( new Integer( 1 ), new JLabel("1") );
		labelTable.put( new Integer( 2 ), new JLabel("2") );
		labelTable.put( new Integer( 3 ), new JLabel("3") );
		labelTable.put( new Integer( 4 ), new JLabel("4") );
		slider.setLabelTable(labelTable);
		
		tool_panel.add(color_picker);
		tool_panel.add(slider);
		tool_panel.add(btn_pen);
		tool_panel.add(btn_eraser);
		
		tool_panel.add(btn_fill);
		tool_panel.add(btn_undo);
		
		
		
		add(tool_panel);
		
		//small space between canvas and tool panel
		add(Box.createRigidArea(new Dimension(0, 20)));
		
		add(canvas);
		
		frame.setResizable(false);
	
		this.setFocusable(true);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(this);
		
		frame.setVisible(true);
		
		run();
		
	}
	
	public void run() {
		
		while (true) {
			frame.getContentPane().repaint();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void main(String[] args) {
		new Filler();
	}
}