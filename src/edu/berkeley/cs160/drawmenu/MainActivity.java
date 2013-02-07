package edu.berkeley.cs160.drawmenu;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class MainActivity extends Activity {

	   CustomView mCustomView;
	   OnTouchListener touchListener;
	   ShapeDrawable mDrawable;
	   private String filePath;
	   private ShareActionProvider mShareActionProvider;
	   
	   int startx = 0; 
	   int starty = 0;
	   int endx = 0; 
	   int endy = 0;
	   int realstartx = 0; 
	   int realstarty = 0;
	   int realendx = 0; 
	   int realendy = 0;
	   int color = Color.RED;

	   LinearLayout layout;
		
	   public class CustomView extends View {

		   	private Bitmap mBitmap;

	        public CustomView(Context context) {
	            super(context);
	        }
	        
	        @Override
	        protected void onDraw(Canvas canvas) {
	            mBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.RGB_565);
	            Canvas currentCanvas = new Canvas(mBitmap);
	            
	            Paint p = new Paint();
	            p.setColor(Color.WHITE);
	            currentCanvas.drawPaint(p);
	            currentCanvas.setBitmap(mBitmap);
	        	if (mDrawable != null) {	  
	        		mDrawable.draw(currentCanvas);	
	        	}
	        
	        	canvas.drawBitmap(mBitmap, 0, 0, null);

	        }
	        
	        protected Bitmap getBitmap() {
	        	return mBitmap;
	        }
	    }  
		
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);

	        layout = (LinearLayout) findViewById(R.id.ll);
	        mCustomView = new CustomView(this);
	        
	        //disable hardware acceleration
	        mCustomView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	   
	        layout.addView(mCustomView);
	        
	        touchListener = new OnTouchListener() {
	        	public boolean onTouch(View v, MotionEvent event) {
	        		if (event.getAction() == MotionEvent.ACTION_DOWN) {
	        			realstartx = (int) event.getX();
	        			realstarty = (int) event.getY();
	        			realendx = (int) event.getX();
	        			realendy = (int) event.getY();
	        			startx = (int) event.getX();
	        			starty = (int) event.getY();
	        			endx = (int) event.getX();
	        			endy = (int) event.getY();
	           		 
	           		 	mDrawable = new ShapeDrawable(new OvalShape());
	        			mDrawable.setBounds(startx, starty, endx, endy);
	        			mDrawable.getPaint().setColor(color);     			            
	        			mCustomView.invalidate();
	        		}
	        		else if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_UP)
	        		{

	        			realendx = (int) event.getX();
	        			realendy = (int) event.getY();
	        			startx = realstartx;
	        			starty = realstarty;
	        			endx = realendx;
	        			endy = realendy;
	        			
	        			if (endx < startx)
	        			{
	        				int temp = startx;
	        				startx = endx;
	        				endx = temp;
	        			}
	        			if (endy < starty)
	        			{
	        				int temp = starty;
	        				starty = endy;
	        				endy = temp;
	        			}

	        			mDrawable.setBounds(startx, starty, endx, endy);
	        			mDrawable.getPaint().setColor(color);
	        			mCustomView.invalidate();
	        		}
	        		return(true);
	        	}
	        };  
	        mCustomView.setOnTouchListener(touchListener);
	       }
	    

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
	   return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
	      switch (item.getItemId()) {
	      case R.id.red:
	            Toast.makeText(this, "You have chosen " + getResources().getString(R.string.red) + " .",
	                        Toast.LENGTH_SHORT).show();
	            color = Color.RED;         
	            return true;
	      case R.id.green:
	            Toast.makeText(this, "You have chosen " + getResources().getString(R.string.green) + " .",
	                        Toast.LENGTH_SHORT).show();
	            color = Color.GREEN;
	            return true;
	      case R.id.blue:
	            Toast.makeText(this, "You have chosen " + getResources().getString(R.string.blue) + " .",
	                        Toast.LENGTH_SHORT).show();
	            color = Color.BLUE;
	            return true;
	      case R.id.menu_item_share:
	    	  	Boolean aSuccess = saveBitmap();
	    	  	if (aSuccess) {
	    	  		Intent share = new Intent(Intent.ACTION_SEND);
	                share.setType("image/jpeg");
	                share.putExtra(Intent.EXTRA_STREAM,Uri.parse(filePath));
	                startActivity(Intent.createChooser(share, "Share Image"));  
	    	  	} else {
	    	  		Toast.makeText(this, "There was a problem saving the image.",
	                        Toast.LENGTH_SHORT).show();
	    	  	}
	            return true;  
	      case R.id.menu_item_save:
	    	  	Boolean bSuccess = saveBitmap();
	    	  	if (bSuccess) {
	    	  		Toast.makeText(this, "Image saved successfully.",
	                        Toast.LENGTH_SHORT).show();
	    	  	} else {
	    	  		Toast.makeText(this, "There was a problem saving the image.",
	                        Toast.LENGTH_SHORT).show();
	    	  	}
	    	  	return true;
	      default:
	            return super.onOptionsItemSelected(item);
	      }
	}

	private void setShareIntent(Intent shareIntent) {
	    if (mShareActionProvider != null) {
	        mShareActionProvider.setShareIntent(shareIntent);
	    }
	}
	
	private Boolean saveBitmap() {
		
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    Bitmap currentBitmap = mCustomView.getBitmap();
	    currentBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);

	    //will create "test.jpg" in sdcard folder.
	    File f = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "test.jpg");

	    try {
			f.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	    //write the bytes in file
	    FileOutputStream fo;
		try {
			fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
			fo.flush();
			fo.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		filePath = f.getAbsolutePath();
		return true;
	
	}
	
	
}
