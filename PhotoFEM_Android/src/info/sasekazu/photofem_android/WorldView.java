/**
 * WorldView
 * this class includes:
 * 	2D graphics process
 * 	Event handling (Touch)
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

/**
 * @author sase
 *
 */
public class WorldView extends View {
	
	private Paint paint = new Paint();
	private Path path = new Path();
	private boolean stateInitFlag = false; 
	private StateManager stateManager;
	private Outline outline = new Outline(40);

	private float[][] vtx;
	private int[][] idx;

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
			
			// Draw vertices
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			for(int i=0; i<vtx.length; i++){
				canvas.drawCircle(vtx[i][0], vtx[i][1], 3, paint);
			}
			
			// Draw triangles
			path.reset();
			for(int i=0; i<idx.length; i++){
				path.moveTo(vtx[idx[i][0]][0], vtx[idx[i][0]][1]);
				path.lineTo(vtx[idx[i][1]][0], vtx[idx[i][1]][1]);
				path.lineTo(vtx[idx[i][2]][0], vtx[idx[i][2]][1]);
				path.lineTo(vtx[idx[i][0]][0], vtx[idx[i][0]][1]);
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
		return outline.getVertices();
	}
	
	public Outline getOutline(){
		return outline;
	}
	
	// setter
	
	public void setStateManager(StateManager stateManager){
		this.stateManager = stateManager;
		this.stateManager.getState();
		this.stateInitFlag = true;	// now onDraw() goes actual draw phase
	}
	
	public void setVertices(float vert[][]){
		vtx = vert.clone();
	}
	
	public void setIndices(int indices[][]){
		this.idx = indices.clone();
	}

	public void setOutline(Outline outline) {
		this.outline = outline;
	}

}
