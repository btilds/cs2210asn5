import java.util.ArrayList;
import java.util.Iterator;

public class Graph implements GraphADT {
	
	private ArrayList<ArrayList<GraphEdge>> graph; // Graph of a double array list 
	private GraphNode[] list; // List
	
	public Graph(int n) {
		// Create an array list of array lists
		graph = new ArrayList<ArrayList<GraphEdge>>();
		list = new GraphNode[n];
		for(int i = 0; i < n; i++) {
			list[i] = new GraphNode(i);
			ArrayList<GraphEdge> temp = new ArrayList<GraphEdge>();
			graph.add(temp);
		}
	}
	
	// Private method that checks if node u exists
	private void checkIfNodeExists(GraphNode u) throws GraphException{
		int index = u.getName();
		if(index > list.length - 1) {
			throw new GraphException("Node does not exist.");
		}
	}
	
	// Private method that gets the edge between node u and node v, otherwise returns null
	private GraphEdge getGraphEdge(GraphNode u, GraphNode v) throws GraphException{
		GraphEdge temp = null;
		Iterator<GraphEdge> iter1 = incidentEdges(u);
		if(iter1 != null) {
			while(iter1.hasNext()) {
				temp = iter1.next();
				if(temp.firstEndpoint().equals(u) && temp.secondEndpoint().equals(v)) {
					return temp;
				}
			}
		}
		Iterator<GraphEdge> iter2 = incidentEdges(v);
		if(iter2 != null) {
			while(iter2.hasNext()) {
				temp = iter2.next();
				if(temp.firstEndpoint().equals(v) && temp.secondEndpoint().equals(u)) {
					return temp;
				}
			}
		}
		return null;
	}
	
	// Method that inserts an edge between node u and node v
	public void insertEdge(GraphNode nodeu, GraphNode nodev, char busLine) throws GraphException{
		checkIfNodeExists(nodeu);
		checkIfNodeExists(nodev);
		if(getGraphEdge(nodeu, nodev) != null) {
			throw new GraphException("Edge already exists between these nodes");
		}
		GraphEdge edge1 = new GraphEdge(nodeu, nodev, busLine);
		GraphEdge edge2 = new GraphEdge(nodev, nodeu, busLine);
		graph.get(nodeu.getName()).add(edge1);
		graph.get(nodev.getName()).add(edge2);
	}
	
	// Method that returns a node in the graph if it exists
	public GraphNode getNode(int name) throws GraphException {
		if(name > list.length - 1) {
			throw new GraphException("Node does not exist");
		}
		return list[name];
	}
	
	// Method that returns an iterator of the edges of node u
	public Iterator<GraphEdge> incidentEdges(GraphNode u) throws GraphException {
		checkIfNodeExists(u);
		if(graph.get(u.getName()).isEmpty()) {
			return null;
		}
		return graph.get(u.getName()).iterator();
	}
	
	// Method that returns the edge connecting node u and node v if it exists
	public GraphEdge getEdge(GraphNode u, GraphNode v) throws GraphException{
		checkIfNodeExists(u);
		checkIfNodeExists(v);
		if(graph.get(u.getName()).isEmpty() || graph.get(v.getName()).isEmpty()) {
			throw new GraphException("Graph edge does not exist");
		}
		GraphEdge result = getGraphEdge(u, v);
		if(result == null) {
			throw new GraphException("Graph edge does not exist");
		}
		return result;
	}
	
	// Method that returns true or false if node u and node v are adjacent
	public boolean areAdjacent(GraphNode u, GraphNode v) throws GraphException{
		checkIfNodeExists(u);
		checkIfNodeExists(v);
		GraphEdge result = getGraphEdge(u, v);
		if(result == null) {
			return false;
		}
		return true;
	}
	
}
