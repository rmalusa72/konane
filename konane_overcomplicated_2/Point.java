class Point{

	int x;
	int y; 

	public Point(int _x, int _y){
		x = _x;
		y = _y;
	}	

	public int x(){
		return x;
	}

	public int y(){
		return y;
	}

	@Override
	public int hashCode(){
		return 10*x + y;
	}

	public boolean equals(Object o){
		Point other = (Point)o;
		return((this.x == other.x) && (this.y == other.y));
	}

	@Override
	public String toString(){
		return Integer.toString(x) + " " + Integer.toString(y);
	}

}