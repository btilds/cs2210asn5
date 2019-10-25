
public class GraphEdge {
	
	private GraphNode firstEndpoint;
	private GraphNode secondEndpoint;
	private char busLine;
	
	public GraphEdge(GraphNode u, GraphNode v, char busLine) {
		this.firstEndpoint = u;
		this.secondEndpoint = v;
		this.busLine = busLine;
	}
	
	public GraphNode firstEndpoint() {
		return this.firstEndpoint;
	}
	
	public GraphNode secondEndpoint() {
		return this.secondEndpoint;
	}
	
	public char getBusLine() {
		return this.busLine;
	}
}
