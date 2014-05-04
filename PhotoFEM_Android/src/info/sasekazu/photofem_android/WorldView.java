/**
 * 
 */
package info.sasekazu.photofem_android;

import info.sasekazu.photofem_android.StateManager.State;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/**
 * @author sase
 *
 */
public class WorldView extends View {
	
	private Paint paint = new Paint();
	private Path path = new Path();
	private boolean stateInitFlag = false; 
	private StateManager stateManager;
	private MultiLineString edges;
	private Outline outline = new Outline(20);

	public WorldView(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(!stateInitFlag){
			return;
		}
		
		paint.setAntiAlias(true);
		
		State state = stateManager.getState();
		// DRAW_OUTLINE
		if(state == StateManager.State.DRAW_OUTLINE){
			// Draw vertices
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			for(int i=0; i<outline.closedCurveNum(); i++){
				for(int j=0; j<outline.coodNum(i); j++){
					canvas.drawCircle((float)outline.get(i, j).x,  (float)outline.get(i, j).y, 3, paint);
				}
			}
			// Draw lines
			path.reset();
			for(int i=0; i<outline.closedCurveNum(); i++){
				if(outline.coodNum(i)>0){
					path.moveTo((float)outline.get(i, 0).x, (float)outline.get(i, 0).y);
					for(int j=1; j<outline.coodNum(i); j++){
						path.lineTo((float)outline.get(i, j).x, (float)outline.get(i, j).y);
					}
				}
			}
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			paint.setStrokeWidth(1);
		}
		// GENERATE_MESH
		else if(state == StateManager.State.GENERATE_MESH){
		}
		// CALC_PHYSICS
		else if(state == StateManager.State.CALC_PHYSICS){
			// Draw edges
			int nEds = edges.getNumGeometries();
			LineString ls;
			Point start, end;
			for(int i=0; i<nEds; i++){
				ls = (LineString)edges.getGeometryN(i);
				start = ls.getStartPoint();
				end = ls.getEndPoint();
				canvas.drawLine((float)start.getX(), (float)start.getY(), (float)end.getX(), (float)end.getY(), paint);
			}
			// Draw vertices
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			for(int i=0; i<outline.closedCurveNum(); i++){
				for(int j=0; j<outline.coodNum(i); j++){
					canvas.drawCircle((float)outline.get(i, j).x,  (float)outline.get(i, j).y, 3, paint);
				}
			}
			// Draw outline
			path.reset();
			for(int i=0; i<outline.closedCurveNum(); i++){
				if(outline.coodNum(i)>0){
					path.moveTo((float)outline.get(i, 0).x, (float)outline.get(i, 0).y);
					for(int j=1; j<outline.coodNum(i); j++){
						path.lineTo((float)outline.get(i, j).x, (float)outline.get(i, j).y);
					}
				}
			}
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			paint.setStrokeWidth(1);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();

		// DOWN and MOVE:
		// Add new coordinates
		if(
				((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN)
				||
				((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE)
				)
		{
			outline.add(new Coordinate(event.getX(), event.getY()));
			invalidate();
		}
		// UP:
		// Try to add new ClosedCurve
		// New ClosedCurve is created 
		// only when last ClosedCurve is "closed" (adding process finished).
		else if((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP){
			outline.newClosedCurve();
		}
		return true;
	}
	
	public void reset(){
		outline.clear();
		invalidate();
	}

	public void reflesh(){
		invalidate();
	}

	// getter
	
	public ArrayList<Coordinate> getVertices(){
		ArrayList<Coordinate> tmp = new ArrayList<Coordinate>();
		for(int i=0; i<outline.closedCurveNum(); i++){
			for(int j=0; j<outline.coodNum(i); j++){
				tmp.add(new Coordinate(outline.get(i,j).x, outline.get(i,j).y, 0));
			}
		}
		return tmp;
	}
	
	// setter
	
	public void setEdges(MultiLineString edges){
		this.edges = edges;
	}
	
	public void setStateManager(StateManager stateManager){
		this.stateManager = stateManager;
		this.stateManager.getState();
		this.stateInitFlag = true;	// now onDraw() goes actual draw phase
	}
	
}
