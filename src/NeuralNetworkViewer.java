import general.Util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;

import javax.swing.*;

import files.TextFileHandler;
import maths.M;

class NeuralNetworkViewer implements Runnable, ActionListener, MouseListener {
	static final int width = 400, height = 400;
	static JFrame frame;
	static String windowTitle = "Neural Network Viewer";
	static JPanel panel = new JPanel(){
		private static final long serialVersionUID = 1L;
		
		@Override
		public void paint(Graphics g){
			draw((Graphics2D)g);
		}
	};
	static JMenuBar menuBar = new JMenuBar();
	static JMenuItem loadMenuItem = new JMenuItem("Load Cell From File");
	static boolean running;
	
	static Color backgroundColor = new Color(100, 100, 255);
	static Color labelColor = Color.black;
	static Color neuronColor = Color.black;
	static Color neuronColor_firing = Color.white;
	static Color[] neuronColorList = neuronColorList(100);
	
	static Cell loadedCell = null;
	static LinkedList<NeuronVertex> neuronVertexList;
	static NeuronVertex selectedVertex = null;
	static int pointSize = 4;
	static int arrowGap = 8;
	static int arrowSize = 12;
	static int selectionDistance = pointSize + 2;
	static int arrowLabelOffset = 8;
	static int vertexLabelOffset = 16;
	
	public static void draw(Graphics2D g){
		Rectangle panelBounds = panel.getBounds();
		
		//Draw background
		g.setColor(backgroundColor);
		g.fillRect(0, 0, panelBounds.width, panelBounds.height);
		
		if(loadedCell != null){
			Color neuronColor;
			for(NeuronVertex nv : neuronVertexList){
				neuronColor = neuronColor((nv.neuron.isFiring) ? nv.neuron.firingStrength : 1.0f);
				g.setColor(neuronColor);
				drawVertex(nv, g);
				for(NeuronVertex connectedNeuron : nv.connectionList){
					g.setColor(neuronColor);
					drawArrow(nv, connectedNeuron, g);
				}
			}
		}
	}
	
	private static void drawArrow(NeuronVertex vertex1, NeuronVertex vertex2, Graphics2D g){
		float dx = vertex2.x - vertex1.x;
		float dy = vertex2.y - vertex1.y;
		float d = (float)Math.sqrt(dx*dx + dy*dy);
		
		int lineStartX = Math.round(vertex1.x + arrowGap*dx/d);
		int lineStartY = Math.round(vertex1.y + arrowGap*dy/d);
		int lineEndX = Math.round(vertex2.x - arrowGap*dx/d);
		int lineEndY = Math.round(vertex2.y - arrowGap*dy/d);
		
		g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);
		
		double reverseArrowAngle = Math.atan2(-dy, -dx);
		
		int arrowEndX, arrowEndY;
		
		arrowEndX = lineEndX + (int) Math.round( Math.cos(reverseArrowAngle + Math.PI/6)*arrowSize );
		arrowEndY = lineEndY + (int) Math.round( Math.sin(reverseArrowAngle + Math.PI/6)*arrowSize );
		g.drawLine(lineEndX, lineEndY, arrowEndX, arrowEndY);
		
		arrowEndX = lineEndX + (int) Math.round( Math.cos(reverseArrowAngle - Math.PI/6)*arrowSize );
		arrowEndY = lineEndY + (int) Math.round( Math.sin(reverseArrowAngle - Math.PI/6)*arrowSize );
		g.drawLine(lineEndX, lineEndY, arrowEndX, arrowEndY);
	}
	
	private static void drawVertex(NeuronVertex vertex, Graphics2D g){
		g.fillOval(vertex.x - pointSize, vertex.y - pointSize, pointSize*2, pointSize*2);
		g.setColor(labelColor);
		Util.drawStringCenteredXY(vertex.label, vertex.x, vertex.y + 1*vertexLabelOffset, g);
		Util.drawStringCenteredXY("-->"+vertex.neuron.currentInputStrength, vertex.x, vertex.y + 2*vertexLabelOffset, g);
		char thresholdRelation = vertex.neuron.isThresholdUpperLimit ? '<' : '>';
		Util.drawStringCenteredXY(thresholdRelation+""+vertex.neuron.threshold, vertex.x, vertex.y + 3*vertexLabelOffset, g);
		Util.drawStringCenteredXY(vertex.neuron.firingStrength+"-->", vertex.x, vertex.y + 4*vertexLabelOffset, g);
	}
	
	public static void main(String[] args) {
		Organ.setup();
		new NeuralNetworkViewer();
	}
	
	private static Color neuronColor(float firingStrength){
		firingStrength = 1.0f - firingStrength;
		int colIndex = (int)(firingStrength*(neuronColorList.length - 1));
		return neuronColorList[colIndex];
	}
	
	private static Color[] neuronColorList(int size){
		Color[] neuronColorList = new Color[size];
		for(int i = 0; i < size; i ++){
			float value = 0.5f + 0.5f*((float)i/(float)(size - 1));
			neuronColorList[i] = new Color(value, value, value);
		}
		return neuronColorList;
	}
	
	NeuralNetworkViewer(){
		frame = new JFrame();
		frame.setResizable(true);
		frame.setSize(width, height);
		frame.setTitle(windowTitle);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(menuBar, BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		menuBar.add(loadMenuItem);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		loadMenuItem.addActionListener(this);
		frame.addMouseListener(this);
		
		new Thread(this).start();
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loadMenuItem){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File("logs"));
			fileChooser.showOpenDialog(null);
			loadFile(fileChooser.getSelectedFile());
			frame.repaint();
			return;
		}
	}
	
	private void clickVertex(NeuronVertex vertex, MouseEvent e){
		if(e.getButton() == 1){ // Left mouse button. //
			selectedVertex = vertex;
			return;
		}
		if(e.getButton() == 3){ // Right mouse button. //
			vertex.neuron.setFiring(!vertex.neuron.isFiring);
			return;
		}
	}
	
	private Point getMousePosition(){
		return panel.getMousePosition();
	}
	
	public void loadCell(Cell cell){
		loadedCell = cell;
		neuronVertexList = new LinkedList<NeuronVertex>();
		
		// Neurons //
		int motorNeuronCount = loadedCell.motorNeuronList.size();
		int sensoryNeuronCount = loadedCell.sensoryNeuronList.size();
		int i = 0, m = 0, s = 0;
		for(Neuron neuron : loadedCell.neuronList){
			NeuronVertex vertex = new NeuronVertex(neuron);
			if(loadedCell.motorNeuronList.contains(neuron)){
				vertex.setLocation(panel.getWidth() - 50, 50 + m*(panel.getHeight() - 100)/motorNeuronCount);
				m ++;
			} else if(loadedCell.sensoryNeuronList.contains(neuron)){
				vertex.setLocation(50, 50 + s*(panel.getHeight() - 100)/sensoryNeuronCount);
				s ++;
			} else {
				vertex.setLocation(M.randInt(100, panel.getWidth() - 100), M.randInt(50, panel.getHeight() - 50));
			}
			neuronVertexList.add(vertex);
			vertex.setLabel(i);
			i ++;
		}
		
		
		
		// Connections //
		for(NeuronVertex nv : neuronVertexList){
			nv.setConnections();
		}
	}
	
	private void loadFile(File file){
		if(file != null){
			frame.setTitle(windowTitle+" - "+file.getPath());
			LinkedList<String> lineList = TextFileHandler.readEntireFile(file.getPath());
			loadCell(new Cell(lineList));
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		;
	}
	
	public void mouseEntered(MouseEvent e) {
		;
	}
	
	public void mouseExited(MouseEvent e) {
		;
	}
	
	public void mousePressed(MouseEvent e) {
		Point mousePosition = getMousePosition();
		if(loadedCell != null && selectedVertex == null && mousePosition != null){
			double smallestDistance = Double.MAX_VALUE;
			NeuronVertex closestVertex = null;
			for(NeuronVertex nv : neuronVertexList){
				double distance = mousePosition.distance(nv.x, nv.y);
				if(distance < smallestDistance){
					smallestDistance = distance;
					closestVertex = nv;
				}
			}
			if(smallestDistance < selectionDistance){
				clickVertex(closestVertex, e);
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		selectedVertex = null;
	}
	
	public void run() {
		running = true;
		while(running){
			step();
			frame.repaint();
			try{
				Thread.sleep(33);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
	public void step(){
		Point mousePosition = getMousePosition();
		if(selectedVertex != null && mousePosition != null) {
			selectedVertex.setLocation(mousePosition);
		}
	}
	
	private class NeuronVertex {
		Neuron neuron;
		LinkedList<NeuronVertex> connectionList;
		String label;
		int x, y;
		
		NeuronVertex(Neuron neuron){
			this.neuron = neuron;
		}
		
		void setConnections(){
			connectionList = new LinkedList<NeuronVertex>();
			for(NeuronVertex nv : neuronVertexList){
				if(neuron.connectionList.contains(nv.neuron)){
					connectionList.add(nv);
				}
			}
			
		}
		
		void setLabel(int index){
			for(Organ organ : loadedCell.organList){
				String organLabel = organ.getLabel(neuron);
				if(!organLabel.equals("")){
					label = organLabel+":"+organ;
					return;
				}
			}
			label = "N"+index;
		}
		
		void setLocation(int x, int y){
			this.x = x;
			this.y = y;
		}
		
		void setLocation(Point p){
			this.x = p.x;
			this.y = p.y;
		}
	}
}