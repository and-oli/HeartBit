package readerocr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.firebase.uidemo.R;
import com.firebase.uidemo.auth.Record;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import readerocr.utils.CharDetectOCR;
import readerocr.utils.CommonUtils;
import readerocr.view.TouchImageView;

import static readerocr.utils.CommonUtils.info;

public class RecognizeTextActivity extends AppCompatActivity implements View.OnClickListener {
	static int REQUEST_IMAGE_CAPTURE = 1;
	static ProcessImage processImg = new ProcessImage();
	final int MULTIPLE_PERMISSIONS = 10;

	Button btnStartCamera;
	EditText systolic;
	EditText diastolic;

	private String language;
	private TouchImageView image;
	private EditText recognizeResultSys;
	private EditText recognizeResultDia;
	DatabaseReference databaseRecords;
	private FirebaseUser user = null;

	private int sourceW = 0;
	private int sourceH = 0;
	private String lastFileName = "";
	private boolean isRecognized = false;
	Button save;

	ProgressDialog progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reader);

		//PILAS
		String[] PERMISSIONS = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.INTERNET };
		if (!hasPermissions(this, PERMISSIONS)) {
			ActivityCompat.requestPermissions(this, PERMISSIONS, MULTIPLE_PERMISSIONS);
		} else {
			CommonUtils.cleanFolder();
		}

		if(getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//FIN

		save = findViewById(R.id.btnSaveRecord);
		save.setOnClickListener(this);
		systolic = findViewById(R.id.recognize_result_sys);
		diastolic = findViewById(R.id.recognize_result_dia);
		user = FirebaseAuth.getInstance().getCurrentUser();
		databaseRecords = FirebaseDatabase.getInstance().getReference("usersData")
				.child(user.getUid())
				.child("records");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		language = "English";
		ProcessImage.language = language;
		ProcessImage.thresholdMin =  150;
		info("Language: "+ language + "   threshold: "+ ProcessImage.thresholdMin);

		btnStartCamera = findViewById(R.id.btnStartCamera);

		btnStartCamera.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_UP) {
					takePicture();
				}
				return false;
			}
		});


		recognizeResultSys = findViewById(R.id.recognize_result_sys);
		recognizeResultDia = findViewById(R.id.recognize_result_dia);
		image = findViewById(R.id.grid_img);
		image.setScaleType(ScaleType.CENTER_INSIDE);

		if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			new InitTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			new InitTask().execute();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recognize, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if(id == android.R.id.home){
			NavUtils.navigateUpFromSameTask(this);
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void takePicture() {
		Intent takePicIntent = new Intent(RecognizeTextActivity.this, AndroidCamera.class);
		lastFileName = CommonUtils.APP_PATH + "capture" + System.currentTimeMillis() + ".jpg";
		takePicIntent.putExtra("output", lastFileName);
		info(lastFileName);
		startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap imageBitmap = BitmapFactory.decodeFile(lastFileName, options);

			if (imageBitmap == null) {
				// Try again
				isRecognized = false;
				image.setImageBitmap(imageBitmap);
				hideProcessBar();
				dialogBox("Can not recognize sheet. Please try again", "Retry", "Exist", true);
				return;
			}
			final Bitmap finalImageBitmap = imageBitmap.getWidth() > imageBitmap.getHeight()
					? rotateBitmap(imageBitmap, 90) : imageBitmap;

			int top = data.getIntExtra("top", 0);
			int bot = data.getIntExtra("bot", 0);
			int right = data.getIntExtra("right", 0);
			int left = data.getIntExtra("left", 0);

			image.setImageBitmap(finalImageBitmap);
			displayResult(finalImageBitmap, top, bot, right, left);

		}
	}

	public void displayResult(Bitmap imageBitmap, int top, int bot, int right, int left) {
		info("Origin size: " + imageBitmap.getWidth() + ":" + imageBitmap.getHeight());
		// Parser
		recognizeResultSys.setText("");
		if (processImg.parseBitmap(imageBitmap, top, bot, right, left)) {
			// TODO: set result

			String recognizedText = processImg.recognizeResult;
			String [] lines = recognizedText.split("\\r?\\n");
			ArrayList numbers = new ArrayList();

			for(int i = 0; i < lines.length; i++){
				String currentLine = lines[i];
				String [] potentialNumbers = currentLine.split(" ");
				for (int j = 0; j < potentialNumbers.length; j++){
					try{
						int currentPotentialNum = Integer.parseInt(potentialNumbers[j].replaceAll("[\\D]",""));
						numbers.add(currentPotentialNum);
					}catch (Exception e){

					}
				}
			}

			if(numbers.size() < 2){
				Toast.makeText(this, "No recognized numbers. Try again.", Toast.LENGTH_SHORT).show();
			} else{
				recognizeResultSys.setText(String.valueOf(numbers.get(0)));
				recognizeResultDia.setText(String.valueOf(numbers.get(1)));
			}

			// TODO: write result to image
			// image.setImageBitmap(toBitmap(processImg.drawAnswered(numberAnswer)));
			isRecognized = true;
			hideProcessBar();
		} else {
			// Try again
			isRecognized = false;
			image.setImageBitmap(imageBitmap);
			hideProcessBar();
			dialogBox("Can not recognize sheet. Please try again", "Retry", "Exist", true);
		}
	}

	public Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}

	public void dialogBox(String message, String bt1, String bt2, final boolean flagContinue) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton(bt1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (flagContinue) {
					takePicture();
				}
			}
		});

		if (bt2 != "") {
			alertDialogBuilder.setNegativeButton(bt2, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					existApp();
					// return false;
				}
			});
		}

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public void existApp() {
		CommonUtils.cleanFolder();
		this.finish();
	}

	public void showProgressBar(String title, String message) {
		progressBar = ProgressDialog.show(this, title, message, false, false);
	}

	public void hideProcessBar() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (progressBar != null && progressBar.isShowing()) {
					progressBar.dismiss();
				}
			}
		});
	}

	private class InitTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... data) {
			try {
				CharDetectOCR.init(getAssets());
				return "";
			} catch (Exception e) {
				Log.e("COMPA", "Error init data OCR. Message: " + e.getMessage());
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {

		}
	}

	//PILAS
	public static boolean hasPermissions(Context context, String... permissions) {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MULTIPLE_PERMISSIONS: {
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permissions granted.
				} else {
					this.finish();
				}
				return;
			}
		}
	}
	//FIN

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnSaveRecord:{

				int sys = -1;
				int dia = -1;

				try{
					sys = Integer.parseInt(systolic.getText().toString());
					dia = Integer.parseInt(diastolic.getText().toString());

					if(sys <= 0 || dia <= 0) {
						throw new Exception();
					} else {
						String key = databaseRecords.push().getKey();
						long timeStamp = System.currentTimeMillis();
						Record record = new Record(sys, dia, timeStamp, key);
						databaseRecords.child(key).setValue(record);
						Toast.makeText(this, "Record added", Toast.LENGTH_SHORT).show();
						this.finish();
					}
				} catch (Exception e){
					Toast.makeText(this, "Invalid record", Toast.LENGTH_SHORT).show();
				}

			} break;
		}
	}

}
