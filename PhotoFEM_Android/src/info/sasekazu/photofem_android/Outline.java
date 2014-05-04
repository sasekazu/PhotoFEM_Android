package info.sasekazu.photofem_android;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.Coordinate;

public class Outline {

	private ArrayList<ClosedCurve> cc;	
	private int minlen;	// Minimum distance between each vertex
	
	public Outline(int minlen) {
		this.minlen = minlen;
		cc = new ArrayList<ClosedCurve>();
		cc.add(new ClosedCurve(minlen));
	}
	
	public boolean add(Coordinate coord){
		return cc.get(cc.size()-1).add(coord);
	}

	// return true when new ClosedCurve is added.
	// it is only when current ClosedCurve is closed.
	public boolean newClosedCurve(){
		if(isLastCurveClosed()){
			cc.add(new ClosedCurve(this.minlen));
			return true;
		}else{
			return false;
		}
	}

	// Methods similar to ArrayList Container
	
	public Coordinate get(int closedCurveIdx, int coodIdx){
		return cc.get(closedCurveIdx).get(coodIdx);
	}
	
	public int closedCurveNum(){
		return cc.size();
	}
	
	public int coodNum(int closedCurveIdx){
		return cc.get(closedCurveIdx).size();
	}
	
	public void clear(){
		cc.clear();
		cc.add(new ClosedCurve(minlen));
	}

	
	// is**
	
	public boolean isLastCurveClosed(){
		return cc.get(cc.size()-1).isClosed();
	}
	
	
}
