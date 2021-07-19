package weigthed.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

public class WeigthedSparseGraph<V> {

	protected ArrayList<V> vertices = new ArrayList<>(); // Store vertices
	protected ArrayList<ArrayList<WeightedEdge>> neighbors = new ArrayList<>(); // Adjacency lists

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Construct an empty graph */
	public WeigthedSparseGraph() {
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Construct a graph from vertices and edges stored in arrays */
	public WeigthedSparseGraph(V[] vertices, int[][] edges) {

		for (V v : vertices) {

			addVertex(v);
		}

		createAdjacencyLists(edges);
	}

	/** Create adjacency lists for each vertex */
	private void createAdjacencyLists(int[][] edges) {

		for (int i = 0; i < edges.length; i++) {
			double weigth = edges[i][2];
			addEdge(edges[i][0], edges[i][1], weigth);
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Construct a graph from vertices and edges stored in List */
	public WeigthedSparseGraph(List<V> vertices, ArrayList<WeightedEdge> edges) {

		for (V v : vertices) {

			addVertex(v);
		}

		createAdjacencyLists(edges);

	}

	/** Create adjacency lists for each vertex */
	private void createAdjacencyLists(ArrayList<WeightedEdge> edges) {
		for (WeightedEdge edge : edges) {
			addEdge(edge.getVertixFrom(), edge.getVertixTo(), edge.getWeight());
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Add an edge to the graph */
	public boolean addEdge(int u, int v, double weight) {
		WeightedEdge edge = new WeightedEdge(u, v, weight);
		return addEdge(edge);
	}

	/** Add an edge to the graph */
	public boolean addEdge(WeightedEdge e) {

		if (e.getVertixFrom() < 0 || e.getVertixFrom() > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.getVertixFrom());

		if (e.getVertixTo() < 0 || e.getVertixTo() > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + e.getVertixTo());

		if (!neighbors.get(e.getVertixFrom()).contains(e)) {
			neighbors.get(e.getVertixFrom()).add(e);
			return true;
		}

		return false;
	}
///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Return the number of vertices in the graph */
	public int getSize() {
		return vertices.size();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Return the vertices in the graph */
	public List<V> getVertices() {
		return vertices;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Return the object for the specified vertex */
	public V getVertex(int index) {
		return vertices.get(index);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Return the index for the specified vertex object */
	public int getIndex(V v) {
		return vertices.indexOf(v);
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Return the neighbors of the specified vertex */
	public ArrayList<Integer> getNeighbors(int index) {

		ArrayList<Integer> result = new ArrayList<>();

		for (WeightedEdge e : neighbors.get(index)) {

			result.add(e.getVertixTo());

		}

		return result;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Return the degree for a specified vertex # adjacement edges = # adjacement
	 * vertices
	 */
	public int getDegree(int v) {
		return neighbors.get(v).size();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Print the edges for all vertices */
	public void printEdges() {

		for (int u = 0; u < neighbors.size(); u++) {

			System.out.print(getVertex(u) + " (" + u + "): ");

			for (WeightedEdge e : neighbors.get(u)) {

				System.out.print("(" + getVertex(e.getVertixFrom()) + ", " + getVertex(e.getVertixTo()) + ", "
						+ e.getWeight() + " ) ");
			}

			System.out.println();
		}
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Clear the graph */
	public void clear() {

		vertices.clear();
		neighbors.clear();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Add a vertex to the graph */
	public boolean addVertex(V vertex) {

		if (!vertices.contains(vertex)) {

			vertices.add(vertex);
			neighbors.add(new ArrayList<WeightedEdge>());
			return true;

		}

		return false;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Remove vertex v and return true if successful */
	public boolean remove(V v) {

		if (vertices.contains(v)) {

			int index = vertices.indexOf(v);
			vertices.remove(index);
			neighbors.remove(index);
			removeFlightsTo(v);
			return true;
		}

		return false;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////

	private void removeFlightsTo(V v) {

		int indexFrom = getIndex(v);

		for (int j = 0; j < neighbors.size(); j++) {

			ArrayList<WeightedEdge> list = neighbors.get(j);

			for (int i = 0; i < list.size(); i++) {

				if (list.get(i).getVertixTo() == indexFrom) {

					neighbors.get(j).remove(i);

				}

			}

		}

	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	/** Remove edge (u, v) and return true if successful */
	public boolean remove(int u, int v) {

		if (u < 0 || u > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + u);

		if (v < 0 || v > getSize() - 1)
			throw new IllegalArgumentException("No such index: " + v);

		WeightedEdge edge = new WeightedEdge(u, v);

		if (neighbors.get(u).contains(edge)) {

			neighbors.get(u).remove(edge);
			return true;

		}

		return false;

	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Dijikstra Algorithm To get shortest Path By start from a given source
	 * 
	 */
	private Node[] getShortestPaths(int source) {

		Node[] nodes = getNodes(source);

		PriorityQueue<Node> vertices = new PriorityQueue<>();
		vertices.add(nodes[source]);

		//T(n) = VNlogV . more preciesly ElogV
		while (!vertices.isEmpty()) {

			Node node = vertices.poll();
			ArrayList<WeightedEdge> adjacentVertices = this.neighbors.get(node.getVertix());

			for (WeightedEdge edge : adjacentVertices) {

				int adjacentVertix = edge.getVertixTo();
				double weigth = edge.getWeight();

				double totalCost = node.getCost() + weigth;

				if (nodes[adjacentVertix].getCost() > totalCost) {
					
					nodes[adjacentVertix].setCost(totalCost);
					nodes[adjacentVertix].setParentVertix(node.getVertix());
					vertices.add(nodes[adjacentVertix]);
				}
			}
		}

		return nodes;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method will get String that contains shortest path from givern source to
	 * given destination depend on array of nodes.
	 * 
	 */
	public Stack<V> getShortestPath(int source, int destination) {

		if (source < 0 || source > vertices.size() - 1) {
			throw new IllegalArgumentException("Out of Bound");
		}

		if (destination < 0 || destination > vertices.size() - 1) {
			throw new IllegalArgumentException("Out of Bound");
		}

		Node[] nodes = getShortestPaths(source);

		int current = destination;
		Stack<V> shortestPath = new Stack<>();
		int counter = 0;
		while (current != -1) {

			Node node = nodes[current];
			shortestPath.add(getVertex(node.getVertix()));
			current = node.getParentVertix();
			counter++;

		}

		return (counter > 1) ? shortestPath : null;

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This method will build and initialize a table that will used to solve
	 * Dijikstra Algorithm for a given source
	 */
	private Node[] getNodes(int source) {

		Node[] nodes = new Node[vertices.size()];
		for (int i = 0; i < nodes.length; i++) {

			nodes[i] = new Node(i);

		}

		nodes[source].setCost(0.0);

		return nodes;

	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Inner Class That represent The Unit of Dijikstra Table
	 * 
	 * @author HP
	 *
	 */
	public static class Node implements Comparable<Node> {

		private double cost = Double.MAX_VALUE;
		private int vertix;
		private int parentVertix = -1;

		public Node(int vertix) {

			this.vertix = vertix;
		}

		public double getCost() {
			return cost;
		}

		public void setCost(double cost) {
			this.cost = cost;
		}

		public int getVertix() {
			return vertix;
		}

		public void setVertix(int vertix) {
			this.vertix = vertix;
		}

		public int getParentVertix() {
			return parentVertix;
		}

		public void setParentVertix(int parentVertix) {
			this.parentVertix = parentVertix;
		}

		@Override
		public int compareTo(Node o) {

			if (this.cost > o.cost) {

				
				return 1;

			} else if (this.cost < o.cost) {

				return -1;

			} else {
				return 0;

			}
		}

	}
}
