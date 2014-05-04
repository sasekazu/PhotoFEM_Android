/**
 * 
 */
package info.sasekazu.photofem_android;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * @author sase
 *
 */
public class WorldView extends View {
	
	private Paint paint = new Paint();
	private ArrayList<PointF> points = new ArrayList<PointF>();

	public WorldView(Context context) {
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		for(int i=0; i<points.size(); i++){
			canvas.drawCircle(points.get(i).x,  points.get(i).y, 3, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN){
			points.add(new PointF(event.getX(), event.getY()));
			invalidate();
		}
		else if((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE){
			points.add(new PointF(event.getX(), event.getY()));
			invalidate();
		}
		return true;
	}
	
	public void reset(){
		points.clear();
		invalidate();
	}
	

}
