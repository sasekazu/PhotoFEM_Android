/**
 * PhotoFEM_Android.java
 * this class includes 
 * 	main function,
 * 	layout design,
 * 	event handling (button)
 */

package info.sasekazu.photofem_android;

import info.sasekazu.photofem_android.StateManager.State;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity {
	
	StateManager stateManager;
	
	
	private WorldView wv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get display size
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		int w = displaySize.x;
		int h = displaySize.y;
		
		// parent layout of all components
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		
		// World View
		wv = new WorldView(this);
		wv.setLayoutParams(new LinearLayout.LayoutParams(w, (int)(h*0.7)));
		layout.addView(wv);
		
		// Message Text View
		TextView tv = new TextView(this);
		tv.setLayoutParams(new LinearLayout.LayoutParams(w, (int)(h*0.05)));
		tv.setPadding(10, 10, 10, 10);
		tv.setBackgroundColor(Color.argb(255, 127, 255, 212)); // aquamarine R:127 G:255 B:212
		tv.setText("Message Text View");
		layout.addView(tv);
		
		// Initialize StateManager
		stateManager = new StateManager(tv);
		wv.setStateManager(stateManager);
		
		// Button Layout
		LinearLayout buttonLayout = new LinearLayout(this);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(buttonLayout);
		
		// Reset Button (Child of buttonLayout)
		Button resetBtn = new Button(this);
		resetBtn.setText("Reset");
		resetBtn.setLayoutParams(new LinearLayout.LayoutParams(w/2, (int)(h*0.1)));
		resetBtn.setPadding(10, 10, 10, 10);
		resetBtn.setOnClickListener(new ResetButtonAdapter());
		buttonLayout.addView(resetBtn);
		
		// Mesh Button (Child of buttonLayout)
		Button meshBtn = new Button(this);
		meshBtn.setText("Generate Mesh");
		meshBtn.setLayoutParams(new LinearLayout.LayoutParams(w/2, (int)(h*0.1)));
		meshBtn.setPadding(10, 10, 10, 10);
		meshBtn.setOnClickListener(new MeshButtonAdapter());
		buttonLayout.addView(meshBtn);
		
		setContentView(layout);
	}
	
	// Adapter Class for Reset Button Listener
	class ResetButtonAdapter implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			stateManager.setState(State.DRAW_OUTLINE);
			wv.reset();
		}
	}
	
	// Adapter Class for Mesh Button Listener
	class MeshButtonAdapter implements android.view.View.OnClickListener{
		@Override
		public void onClick(View v) {
			stateManager.setState(State.GENERATE_MESH);
			TriangleMeshBuilder meshBuilder = new TriangleMeshBuilder((float)(wv.getOutline().getMinlen()*1.3));
			meshBuilder.buildMesh(wv.getOutline());
			wv.setVertices(meshBuilder.getVertices());
			wv.setIndices(meshBuilder.getIndices());
			stateManager.setState(State.CALC_PHYSICS);
			wv.reflesh();
		}
	}
	
}
