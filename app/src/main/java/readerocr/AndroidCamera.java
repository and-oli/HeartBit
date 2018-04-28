package readerocr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;

import com.firebase.uidemo.R;

import readerocr.view.CameraPreview;
import readerocr.view.CameraSurfaceView;

public class AndroidCamera extends Activity {
	static int REQUEST_IMAGE_CAPTURE = 1;
	public CameraPreview camPreview;
	CameraSurfaceView cameraSurfaceView;
	SurfaceHolder surfaceHolder;
	boolean previewing = false;
	LayoutInflater controlInflater = null;

    FloatingActionButton buttonTakePicture;
	int previewSizeWidth = 0;
	int previewSizeHeight = 0;

	ProgressDialog progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("COMPA", "on create AndroidCamera");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_android_camera);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		Bundle b = getIntent().getExtras();
		final String outputUri = b.getString("output");

		getWindow().setFormat(PixelFormat.UNKNOWN);
		cameraSurfaceView = findViewById(R.id.camerapreview);
		camPreview = new CameraPreview(this, cameraSurfaceView);

		// surfaceHolder = cameraSurfaceView.getHolder();
		// surfaceHolder.addCallback(camPreview);
		// surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		controlInflater = LayoutInflater.from(getBaseContext());
		View viewControl = controlInflater.inflate(R.layout.control, null);
		LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addContentView(viewControl, layoutParamsControl);

		buttonTakePicture = findViewById(R.id.takepicture);
		buttonTakePicture.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				camPreview.takePicture(outputUri);
			}
		});

		View decorView = getWindow().getDecorView();
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
	}

	public void callProcessImage(String output, int top, int bot, int right, int left) {
		// showProgressBar("In processing...", "Please wait a second.");
		Intent returnIntent = new Intent();
		returnIntent.putExtra("output", output);
		returnIntent.putExtra("top", top);
		returnIntent.putExtra("right", right);
		returnIntent.putExtra("bot", bot);
		returnIntent.putExtra("left", left);

		setResult(Activity.RESULT_OK, returnIntent);
		finishActivity(REQUEST_IMAGE_CAPTURE);
		finish();
	}

	public void showProgressBar(String title, String message) {
		progressBar = ProgressDialog.show(this, title, message, false, false);
	}

	public void resizeBtnTakePic(int width, int height) {
		LayoutParams pr = buttonTakePicture.getLayoutParams();

		pr.width = width;
		pr.height = height;
		buttonTakePicture.setLayoutParams(pr);
		buttonTakePicture.invalidate();
	}
}