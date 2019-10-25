import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Stack;

public class BusLines {
	
	private Graph graph; // Graph
	private int start, destination; // Node number for start and destination
	private int busChangesAllowed; // Number of bus changes allowed for map
	private int numChanges = 0; // Current number of bus changes in path
	private Stack<GraphNode> path; // Path stack
	private Stack<GraphNode> changeNodes; // Node stack storing nodes where a bus change occurs
	
	public BusLines(String inputFile) throws MapException {
		int numNodes = 0;
		char[][] map = null; // Map converted into a 2D char array
		int[][] nodeNumber = null; // Nodes and corresponding numbers in 2D integer array
		int numRows = 0, numColumns = 0; // Number of rows and columns in map
		
		// Convert the file to a 2D char array
		
		try {
			File f = new File(inputFile);
			Scanner s = new Scanner(f);
			String[] scale;
			String buffer;
			scale = s.nextLine().split(" ");
			// Get beginning scaling values
			numColumns = (Integer.parseInt(scale[1]) * 2) - 1;
			numRows = (Integer.parseInt(scale[2]) * 2) - 1;
			busChangesAllowed = Integer.parseInt(scale[3]);
			map = new char[numRows][numColumns];
			// Convert map to 2D char array
			for(int i = 0; i < numRows; i++) {
				buffer = s.nextLine();
				for(int j = 0; j < numColumns; j++) {
						map[i][j] = buffer.charAt(j);
				}
			}
			s.close();
		}
		catch(FileNotFoundException e) {
			throw new MapException("File not found");
		}
		
		// Assign each node in map a number and put it in adjacency matrix nodeNumber
		
		nodeNumber = new int[numRows][numColumns];
		int nodeNum = 1;
		for(int i = 0; i < numRows; i = i + 2) {
			for(int j = 0; j < numColumns; j = j + 2) {
				// Scan through map and if a node is found, assign a number to it and store in nodeNumber
				if(map[i][j] == 'S' || map[i][j] == 'D' || map[i][j] == '.') {
					numNodes++;
					nodeNumber[i][j] = nodeNum;
					nodeNum++;
				}
			}
		}
		
		// Create new graph and connect edges
		
		graph = new Graph(numNodes);
		// Read every second line and column of map and connect adjacent nodes in graph
		for(int r = 0; r < numRows; r = r + 2) {
			for(int c = 0; c < numColumns; c = c + 2) {
				if(map[r][c] == 'S') {
					start = (nodeNumber[r][c] - 1);
					connectNeighbours(r, c, map, nodeNumber, numRows, numColumns);
				}
				if(map[r][c] == 'D') {
					destination = (nodeNumber[r][c] - 1);
					connectNeighbours(r, c, map, nodeNumber, numRows, numColumns);
				}
				if(map[r][c] == '.') {
					connectNeighbours(r, c, map, nodeNumber, numRows, numColumns);
				}
			}
		}
	}
	
	private void connectNeighbours(int row, int column, char[][] map, int[][] nodeNumber, int numRows, int numColumns) {
		
		// Connect the edges of a node
		
		// +- 2 for each row and column to get surrounding nodes
		for(int r = -2; r <= 2; r = r + 2) {
			for(int c = -2; c <= 2; c = c + 2) {
				// Ensure added +r and +c are within the map boundaries
				if((row+r) < numRows && (row+r) >= 0 && (column+c) < numColumns && (column+c) >= 0) {
					GraphNode first = null;
					GraphNode second = null;
					try {
						first = graph.getNode(nodeNumber[row][column] - 1);
						second = graph.getNode(nodeNumber[row+r][column+c] - 1);
					} catch (GraphException e) {
					}
					if(r == -2 && c == 0) {
						// If there is a bus line connecting nodes then connect the nodes
						if(map[row-1][column] != ' ') {
							try {
								graph.insertEdge(first, second, map[row-1][column]);
							} catch (GraphException e) {
							}
						}
					}
					if(r == 2 && c == 0) {
						// If there is a bus line connecting nodes then connect the nodes
						if(map[row+1][column] != ' ') {
							try {
								graph.insertEdge(first, second, map[row+1][column]);
							} catch (GraphException e) {
							}
						}	
					}
					if(r == 0 && c == -2) {
						// If there is a bus line connecting nodes then connect the nodes
						if(map[row][column-1] != ' ') {
							try {
								graph.insertEdge(first, second, map[row][column-1]);
							} catch (GraphException e) {
							}
						}
					}
					if(r == 0 && c == 2) {
						// If there is a bus line connecting nodes then connect the nodes
						if(map[row][column+1] != ' ') {
							try {
								graph.insertEdge(first, second, map[row][column+1]);
							} catch (GraphException e) {
							}
						}	
					}
				}
			}
		}
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	// Private trip helper method that returns true if there is a path from start to destination node
	
	private boolean trip_helper(int curNode, char currentBus){
		
		// Check if we have exceeded the number of changes allowed for the map
		if(numChanges > busChangesAllowed) {
			numChanges--;
			changeNodes.pop();
			return false;
		}
		// Get the current node
		GraphNode currentNode = null;
		try {
			currentNode = graph.getNode(curNode);
		}
		catch(GraphException e) {
			e.printStackTrace();
		}
		char curBus = currentBus;
		currentNode.setMark(true);
		path.push(currentNode);
		// Get the edges that connect to the node
		Iterator<GraphEdge> iter = null;
		try {
			iter = graph.incidentEdges(currentNode);
		}
		catch(GraphException e) {
			e.printStackTrace();
		}
		// Check if we have reached the destination
		if(path.peek().getName() == destination) {
			return true;
		}
		else {
			// While the node still has edges
			while(iter.hasNext()) {
				GraphEdge temp = iter.next();
				char tempBus = temp.getBusLine();
				// If the connected node is not marked
				if(!temp.secondEndpoint().getMark()) {
					// If we are still at the start point, set current bus to the bus line of this edge
					if(temp.firstEndpoint().getName() == start) {
						curBus = tempBus;
					}
					// If the bus line is different than the current one then increase the bus changes
					if(tempBus != curBus) {
						numChanges++;
						changeNodes.push(currentNode);
					}
					// If the adjacent node does not provide a path to the destination return false
					boolean result = trip_helper(temp.secondEndpoint().getName(), tempBus);
					if(result == true) {
						// Return true if adjacent node provides path to destination
						return true;
					}
				}
			}
			// If the path and changeNodes stacks are not empty
			if(!path.isEmpty() && !changeNodes.isEmpty()) {
				// If we are backtracking and get to a node where we switched buses, 
				// then pop the node from the changeNodes stack
				if(path.peek().getName() == changeNodes.peek().getName()) {
					numChanges--;
					changeNodes.pop();
				}
			}
			path.pop();
			currentNode.setMark(false);
			return false;
		}
	}
	
	public Iterator<GraphNode> trip(){
		path = new Stack<GraphNode>();
		changeNodes = new Stack<GraphNode>();
		// Call the trip_helper method with the start node and no current bus
		if(trip_helper(start, ' ') == true)
			return path.iterator();
		else {
			return null;
		}
	}
}
