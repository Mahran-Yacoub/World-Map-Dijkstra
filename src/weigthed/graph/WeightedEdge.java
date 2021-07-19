package weigthed.graph;

public class WeightedEdge {

	/** An Index of start Vertix of An Edge */
	private int vertixFrom;

	/** An Index of End Vertix of An Edge */
	private int vertixTo;

	private double weight;

	public WeightedEdge(int vertixFrom, int vertixTo, double weight) {

		this(vertixFrom,vertixTo);
		this.weight = weight;
	}
	
	public WeightedEdge(int vertixFrom, int vertixTo) {
		
		this.vertixFrom = vertixFrom;
		this.vertixTo = vertixTo;
	}

	public int getVertixFrom() {
		return vertixFrom;
	}

	public void setVertixFrom(int vertixFrom) {
		this.vertixFrom = vertixFrom;
	}

	public int getVertixTo() {
		return vertixTo;
	}

	public void setVertixTo(int vertixTo) {
		this.vertixTo = vertixTo;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null) {

			return false;
		}

		if (this == o) {
			return true;
		}

		if (o.getClass() == this.getClass()) {

			WeightedEdge edge = (WeightedEdge) o;
			//&& this.weight == edge.weight
			return (this.vertixFrom == edge.vertixFrom && this.vertixTo == edge.vertixTo) ? true : false;

		}

		return false;
	}
}
