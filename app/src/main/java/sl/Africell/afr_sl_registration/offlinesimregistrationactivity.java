package sl.Africell.afr_sl_registration;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;


import com.example.gpstracking.GPSTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.offlinesimregistrationactivity.FeedReaderContract.FeedEntry;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Base64;
import android.content.Intent;
import cn.com.aratek.demo.DemoActivity;

import static sl.Africell.afr_sl_registration.SessionManager.bytedata1;
import static sl.Africell.afr_sl_registration.SessionManager.fg;
import static sl.Africell.afr_sl_registration.SessionManager.bitmap;

public class offlinesimregistrationactivity extends Activity {
	public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
    private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
    private static final int SCAN_SIM_BAR_CODE = 49374;
	private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;

    private ImageView mPersonalPhoto;
	private ImageView mPersonalEmpreinte;
    @SuppressWarnings("unused")
	private String strPersonalPhoto;
	private String strPersonalEmpreinte;

    private Uri PersonalPhotoUri;
    private String strPersonalPhotoURL;
    
    private ImageView mIdCardSide1;
    @SuppressWarnings("unused")
	private String strIdCardSide1;
    private Uri Side1Uri;
    private String strSide1URL;
    
    private ImageView mIdCardSide2;
    @SuppressWarnings("unused")
	private String strIdCardSide2;
    private Uri Side2Uri;
    private String strSide2URL;    
	
	private Bitmap bitmap;
	
	// number of images to select
	private static final int PICK_IMAGE = 1;
	
	private GPSTracker gps;
	private String strGeoLocation;
	
	@SuppressWarnings("unused")
	private int intNbrRequestedFace;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offlinesimregistration);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		sFillCBOGender();
		sFillCBOEducation();
		sFillCBONationality();
		sFillCBOIdType();
		sFillCBOTown();
		sFillCBOOccupationCategory();
		sFillCBOOccupationSubCategory();
		sFillCBOOccupationTown();
		sFillCBONDC();	
		
		//EditText txtICC = (EditText) findViewById(R.id.txtICC);
		//txtICC.setEnabled(false);		
		
	    strPersonalPhoto = "";
	    strIdCardSide1 = "";
	    strIdCardSide2 = "";
	    intNbrRequestedFace = 0;
	    strPersonalPhotoURL = "";
	    strSide1URL = "";
	    strSide2URL = "";

		strPersonalPhoto = "";
		strPersonalEmpreinte = "";

	}
	@Override
	protected void onResume() {
		super.onResume();
		getFingerprint();
		//openDevice();
		//Toast.makeText(getApplicationContext(), "onResume" , Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		//closeDevice(false);
		fg="0";
		strPersonalEmpreinte="";
		super.onPause();
	}
	public void CapturePersonalEmpreinte(View V) {

		Intent intent = new Intent(this, DemoActivity.class);
		intent.putExtra("regTypeId", "Finger");
		startActivity(intent);
	}
	// method for base64 to bitmap


	public void getFingerprint(){

		strPersonalEmpreinte="";
		mPersonalEmpreinte = (ImageView) findViewById(R.id.fingerimage);
		//Toast.makeText(getApplicationContext(), "finger=" +fg, Toast.LENGTH_SHORT).show();

		if (!fg.equals("0")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nofinger);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] imageBytes = baos.toByteArray();
			String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

			//decode base64 string to image
			imageBytes = bytedata1;//Base64.decode(imageString, Base64.DEFAULT);
			Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
			mPersonalEmpreinte.setImageBitmap(decodedImage);
			mPersonalEmpreinte.setVisibility(View.VISIBLE);

			 strPersonalEmpreinte = base64.encodeBytes(bytedata1);
			//strPersonalEmpreinte = fg; // here with fg=fgFeat

		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.offlinesimregistrationactivity, menu);
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
		return super.onOptionsItemSelected(item);
	}
	
	private void sFillCBOGender(){
		Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		ArrayAdapter<CharSequence> adapterGender = ArrayAdapter.createFromResource(this,R.array.Gender_array, android.R.layout.simple_spinner_item);
		adapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerGender.setAdapter(adapterGender);
	}	
	
	private void sFillCBOEducation(){
		Spinner spinnerEducation = (Spinner) findViewById(R.id.spinnerEducation);
		ArrayAdapter<CharSequence> adapterEducation = ArrayAdapter.createFromResource(this,R.array.Education_array, android.R.layout.simple_spinner_item);
		adapterEducation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerEducation.setAdapter(adapterEducation);
	}	
	
	private void sFillCBONationality(){
		Spinner spinnerNationality = (Spinner) findViewById(R.id.spinnerNationality);
		ArrayAdapter<CharSequence> adapterNationality = ArrayAdapter.createFromResource(this,R.array.Nationality_array, android.R.layout.simple_spinner_item);
		adapterNationality.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerNationality.setAdapter(adapterNationality);
		spinnerNationality.setSelection(42);
	}
	
	private void sFillCBOIdType(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerIDType);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.IdType_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}

libClass libclass= new  libClass(this);

	private void sFillCBOTown(){
		try {
			String _CellId = getMyCellId();
			Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);

			if (_CellId != null && _CellId.trim().length() >0 && !_CellId.equals("")){
				String _Lac =  _CellId.substring(0, 1);
				String _Lac2 = _CellId.substring(0, 2);
				//Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);
				if (_Lac.equals("1") || (_Lac2.equals("31")) || ((_Lac.equals("4")) && (_Lac2.equals("41")))) {
					ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_kinshasa, android.R.layout.simple_spinner_item);
					adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerIdType.setAdapter(adapterIdType);
				} else if (_Lac.equals("6") || (_Lac2.equals("36")) || ((_Lac.equals("4")) && (_Lac2.equals("46")))) {
					ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_Katanga, android.R.layout.simple_spinner_item);
					adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerIdType.setAdapter(adapterIdType);
				} else if (_Lac.equals("2") || (_Lac2.equals("32"))) {
					ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_congo, android.R.layout.simple_spinner_item);
					adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerIdType.setAdapter(adapterIdType);
				}else{
					ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_town, android.R.layout.simple_spinner_item);
					adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerIdType.setAdapter(adapterIdType);
				}
			}else{
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_town, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			}

		}catch ( Exception e){

			libclass.popMessage("Error occur","Loading Town error");
		}
	}
	
	private void sFillCBOOccupationCategory(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerOccupationCategory);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.occupationCategory_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}	
	
	private void sFillCBOOccupationSubCategory(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerSubOccupationCategory);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.subOccupationCategory_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}		
	
	private void sFillCBOOccupationTown(){
		String _CellId = getMyCellId();
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);
		if (_CellId != null && _CellId.trim().length() >0 && !_CellId.equals("")) {
			String _Lac = _CellId.substring(0, 1);
			String _Lac2 = _CellId.substring(0, 2);

			if (_Lac.equals("1") || (_Lac2.equals("31"))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_kinshasa, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			} else if (_Lac.equals("6") || (_Lac2.equals("36"))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_Katanga, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			} else if (_Lac.equals("2") || (_Lac2.equals("32"))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_congo, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			}
		}else{

			ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_town, android.R.layout.simple_spinner_item);
			adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerIdType.setAdapter(adapterIdType);
		}
	}	
	
	private void sFillCBONDC(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerNDC);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.NDC_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}		
	
	public void CapturePersonalPhoto(View V){
		mPersonalPhoto = (ImageView) findViewById(R.id.camera_personal_image);
		ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Personal Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);
        
	}
	
	public void CaptureCaptureIdCardSide1(View V){
		mIdCardSide1 = (ImageView) findViewById(R.id.camera_idcard1_image);
		ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Side1 Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Side1Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Side1Uri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST_IdCardSide1);        
	}
	
	public void CaptureCaptureIdCardSide2(View V){
		mIdCardSide2 = (ImageView) findViewById(R.id.camera_idcard2_image);
		ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Side2 Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        Side2Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Side2Uri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST_IdCardSide2);
	}	
	
	public void onScanBarcodeClick(View V){
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}	
	
	public void showDatePickerDialog(View v) {
		try {

			DialogFragment newFragment = new  DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
		}catch (Exception  ex){
			//Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
			ex.printStackTrace();
		}
	}	
	
	@SuppressLint("ValidFragment")
	public static class DatePickerFragment extends DialogFragment
    	implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			String strDay = "";
			String strMonth = "";
			if (day < 10) {
				strDay = "0" + day;
			} else {
				strDay = "" + day;
			}
			month = month + 1;
			if (month < 10) {
				strMonth = "0" + month;
			} else {
				strMonth = "" + month;
			}
			offlinesimregistrationactivity activity=(offlinesimregistrationactivity)getActivity();
			activity.sSetBirthDate( strMonth+"/"+strDay+"/"+year);

		}
	}	
	
	private void sSetBirthDate(String strDate){
		final TextView textViewToChange = (TextView) findViewById(R.id.lblBirthDateValue);
		try {

			if (!strDate.equals("")) {
				//	final TextView textViewToChange = (TextView) findViewById(R.id.lblBirthDateValue);
				textViewToChange.setText(strDate);
			}
		}catch (Exception ex){
			textViewToChange.setText("01/01/1900");
		}
	}		
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);

	  if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
		    && null != data) {
		   Uri selectedImage = data.getData();
		   String[] filePathColumn = { MediaStore.Images.Media.DATA };
	
		   android.database.Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
		   cursor.moveToFirst();
	
		   int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		   String picturePath = cursor.getString(columnIndex);
		   cursor.close();
	
		   decodeFile(picturePath, 512);
	
		  }
	  
	  
      if (requestCode == CAMERA_PIC_REQUEST_PersonalPhoto) {
              if (resultCode == Activity.RESULT_OK) {
                  try {
                	  
                	  
                	  Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), PersonalPhotoUri);
                	  strPersonalPhotoURL = getRealPathFromURI(PersonalPhotoUri);  
                	  decodeFile(strPersonalPhotoURL, 512);
                	  
                	  /*
                	  //face detection
                	  intNbrRequestedFace = 0;
      	              int width = bitmap.getWidth();
      	              int height = bitmap.getHeight();
      	              Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,width,height,true);
                	  int intNbrOfFaces = detectFaces(bitmap);
                	  //rotate 270
                	  if (intNbrOfFaces == 0){
                		  Matrix matrix = new Matrix();
          	              matrix.postRotate(270);
          	              Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
          	              intNbrOfFaces = detectFaces(rotatedBitmap);
                	  } 
                	  //rotate 180
                	  if (intNbrOfFaces == 0) {
                		  Matrix matrix = new Matrix();
          	              matrix.postRotate(180);
          	              Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
          	              intNbrOfFaces = detectFaces(rotatedBitmap);
                	  } 
                	  //rotate 90
                	  if (intNbrOfFaces == 0) {
                		  Matrix matrix = new Matrix();
          	              matrix.postRotate(90);
          	              Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
          	              intNbrOfFaces = detectFaces(rotatedBitmap);  
                	  } 
                	  if (intNbrOfFaces == 0) {
            	            showAlertDialog(offlinesimregistrationactivity.this, "Face not detected",
            	                    "Make sure there's enough light and that nothing is in the way and then try again.", false);
            	            return;
                	  } 
                	  if (intNbrOfFaces > 1) {
          	            	showAlertDialog(offlinesimregistrationactivity.this, "Multiple Faces detected",
          	            			"Only one face is allowed, try again.", false);
          	            	return;
                	  } else {  
                		  Toast.makeText(getApplicationContext(), intNbrOfFaces + " face detected.",Toast.LENGTH_LONG).show();
                	  }
                	  */
                	  int intNbrOfFaces = 1;
                	  if (intNbrOfFaces == 1) {
                		  intNbrRequestedFace = 1;
                    	  mPersonalPhoto.setImageBitmap(bitmap);
                		  thumbnail = bitmap;
                		  //3
                		  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                		  thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
                		  //3.5 prepare the image to be uploaded
            	   	   	  byte[] bytedata = bytes.toByteArray();
            	   	      strPersonalPhoto = base64.encodeBytes(bytedata);
                	  }
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              }
      }
  
      if (requestCode == CAMERA_PIC_REQUEST_IdCardSide1) {
          if (resultCode == Activity.RESULT_OK) {
              try {
            	  Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), Side1Uri);
            	  strSide1URL = getRealPathFromURI(Side1Uri);  
            	  decodeFile(strSide1URL, 1024);
            	  mIdCardSide1.setImageBitmap(bitmap);
            	  thumbnail = bitmap;
                  //3
                  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                  thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
                  //3.5 prepare the image to be uploaded
        	   	   byte[] bytedata = bytes.toByteArray();
        	   	   strIdCardSide1 = base64.encodeBytes(bytedata);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }    	  
      }
      
      if (requestCode == CAMERA_PIC_REQUEST_IdCardSide2) {
          if (resultCode == Activity.RESULT_OK) {
              try {
            	  Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), Side2Uri);
            	  strSide2URL = getRealPathFromURI(Side2Uri);  
            	  decodeFile(strSide2URL, 1024);
            	  mIdCardSide2.setImageBitmap(bitmap);
            	  thumbnail = bitmap;
                  //3
                  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                  thumbnail.compress(Bitmap.CompressFormat.JPEG, 65, bytes);
                  //3.5 prepare the image to be uploaded
        	   	   byte[] bytedata = bytes.toByteArray();
        	   	   strIdCardSide2 = base64.encodeBytes(bytedata);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }  
      }      
      
      if (requestCode == SCAN_SIM_BAR_CODE) {
	  	  EditText txtICC = (EditText) findViewById(R.id.txtICC);
    	  IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
          if (resultCode == RESULT_OK) {
             String contents = scanningResult.getContents();
             //String format = scanningResult.getFormatName();
             txtICC.setText(contents);	
             getNDCMSISDN(contents);
             // Handle successful scan
          } else if (resultCode == RESULT_CANCELED) {
             // Handle cancel
        	  txtICC.setText("");
          }
       }      

	 }
	 
	 private void getNDCMSISDN(String _Barcode){
		 		 
		 String _IMSI;
		 EditText txtICC = (EditText) findViewById(R.id.txtICC);
		 if (_Barcode.length() < 12) {
			 return;
		 } else {
			 _IMSI = "6309001"+_Barcode.substring(4, 12);
			 txtICC.setText(_IMSI);
		 }
		 
	 }	 
	 /*
	 private int detectFaces(Bitmap cameraBitmap){
		    if(null != cameraBitmap){
	            int width = cameraBitmap.getWidth();
	            int height = cameraBitmap.getHeight();
	            
	            FaceDetector detector = new FaceDetector(width, height,MAX_FACES);
	            android.media.FaceDetector.Face[] faces = new android.media.FaceDetector.Face[MAX_FACES];
	            
	            Bitmap bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
	            Paint ditherPaint = new Paint();
	            Paint drawPaint = new Paint();
	            
	            ditherPaint.setDither(true);
	            drawPaint.setColor(Color.RED);
	            drawPaint.setStyle(Paint.Style.STROKE);
	            drawPaint.setStrokeWidth(2);
	            
	            Canvas canvas = new Canvas();
	            canvas.setBitmap(bitmap565);
	            canvas.drawBitmap(cameraBitmap, 0, 0, ditherPaint);
	            
	            int facesFound = detector.findFaces(bitmap565, faces);
	            return facesFound;          
	            	
	    } else {
	    	return 0;
	    }
	}	 
	*/
	 
	 public String getRealPathFromURI(Uri contentUri) {
	        String[] proj = { MediaStore.Images.Media.DATA };
	        @SuppressWarnings("deprecation")
			android.database.Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }	 

	public void selectImageFromGallery(View view) {
		 Intent intent = new Intent();
		 intent.setType("image/*");
		 intent.setAction(Intent.ACTION_GET_CONTENT);
		 startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
		}

	/* send an alert */
    @SuppressWarnings("deprecation")
	public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
         
        // Setting alert dialog icon
        // alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }	
	
	public void onUploadClick(View view) {
		if (blnCheckDataBeforeUpload() == true){
			Toast.makeText(getApplicationContext(), "Registered successfully.",Toast.LENGTH_LONG).show();
			fg="0";
	        String strMessage = "";    	
	        Intent intent = new Intent(getApplicationContext(), secondactivity.class);
	        intent.putExtra(EXTRA_MESSAGE, strMessage);   
	        startActivity(intent);	
		} else {
			Toast.makeText(getApplicationContext(), "Registration failed.",Toast.LENGTH_LONG).show();
			return;
		}
	}
	
	public boolean blnCheckDataBeforeUpload() {
		String[] strParameters;
		strParameters = new String[26];
		 
	    TextView lblError = (TextView) findViewById(R.id.lblError); 
	    lblError.setText("");
	        
		EditText txtFirstName = (EditText) findViewById(R.id.txtFirstName);
	    String sFirstName=	txtFirstName.getText().toString();
	    if ((sFirstName.equals("")) || (sFirstName == null) ){
		    lblError.setText("First Name cannot be empty.");
	    	return  false;
		}
		strParameters[0] = txtFirstName.getText().toString();
		 
		EditText txtFatherName = (EditText) findViewById(R.id.txtFatherName);
		strParameters[1] = txtFatherName.getText().toString();		 
		 
		EditText txtLastName = (EditText) findViewById(R.id.txtLastName);
		String sLastName=	txtLastName.getText().toString();
		if ((sLastName.equals("")) || (sLastName == null) ){
			lblError.setText("Last Name cannot be empty.");
			return  false;
		}
		strParameters[2] = txtLastName.getText().toString();
		 
		Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		strParameters[3] = spinnerGender.getSelectedItem().toString();
		spinnerGender.getSelectedItem().toString();


		String sGender=	spinnerGender.getSelectedItem().toString();
		if ((sGender.equals("")) || (sGender == null) ){
			lblError.setText("please select the sex.It should be Male or Female");
			return  false;
		}


        TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);
		strParameters[4] = lblBirthDateValue.getText().toString();

		//get birth Date
		//TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);
		String _BirthDate = lblBirthDateValue.getText().toString();
		String[]tBirthday=_BirthDate.split("/");
		int iYear= Calendar.getInstance().get(Calendar.YEAR);

		if (_BirthDate.equals("")){
			Toast.makeText(this, "veuillez entrer la date de Naissance.", Toast.LENGTH_LONG).show();
			lblError.setText("veuillez entrer la date de Naissance.");
			lblBirthDateValue.findFocus();
			return false;
		}else
		if (tBirthday.length >0 && (iYear-Integer.parseInt(tBirthday[2]) < 17)){
			Toast.makeText(this, "La date de Naissance entrée est invalide.", Toast.LENGTH_LONG).show();
			lblError.setText("La date de Naissance entrée est invalide.");
			lblBirthDateValue.findFocus();
			return false;
		}


        
		EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
		strParameters[5] = txtEmail.getText().toString();
		
		Spinner spinnerEducation = (Spinner) findViewById(R.id.spinnerEducation);
		strParameters[6] = spinnerEducation.getSelectedItem().toString();
		
		strParameters[7] = strPersonalPhotoURL;
		
		Spinner spinnerNationality = (Spinner) findViewById(R.id.spinnerNationality);
		strParameters[8] = spinnerNationality.getSelectedItem().toString();
		
		Spinner spinnerIDType = (Spinner) findViewById(R.id.spinnerIDType);
		strParameters[9] = spinnerIDType.getSelectedItem().toString();
		
		EditText txtReferenceNbr = (EditText) findViewById(R.id.txtReferenceNbr);
		strParameters[10] = txtReferenceNbr.getText().toString();
		
		strParameters[11] = strSide1URL;
		strParameters[12] = strSide2URL;	
		
		Spinner spinnerTown = (Spinner) findViewById(R.id.spinnerTown);
		strParameters[13] = spinnerTown.getSelectedItem().toString();
		
		EditText txtStreet = (EditText) findViewById(R.id.txtStreet);
		strParameters[14] = txtStreet.getText().toString();
		
		Spinner spinnerOccupationCategory = (Spinner) findViewById(R.id.spinnerOccupationCategory);
		strParameters[15] = spinnerOccupationCategory.getSelectedItem().toString();
		
		Spinner spinnerSubOccupationCategory = (Spinner) findViewById(R.id.spinnerSubOccupationCategory);
		strParameters[16] = spinnerSubOccupationCategory.getSelectedItem().toString();
		
		EditText txtEmployer = (EditText) findViewById(R.id.txtEmployer);
		strParameters[17] = txtEmployer.getText().toString();
		
		//Spinner spinnerOccupationTown = (Spinner) findViewById(R.id.spinnerOccupationTown);
		strParameters[18] = " ";
		
		EditText txtOccupationStreet = (EditText) findViewById(R.id.txtOccupationStreet);
		strParameters[19] = txtOccupationStreet.getText().toString();
		
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		strParameters[20] = spinnerNDC.getSelectedItem().toString();
		
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		strParameters[21] = txtMSISDN.getText().toString();
		
		EditText txtICC = (EditText) findViewById(R.id.txtICC);
		String _ICC = txtICC.getText().toString();
		String _IMSI = "";
		if (!_ICC.trim().equals("")){
			if (_ICC.length() != 16){
				Toast.makeText(this, "ICC doesn't exist.", Toast.LENGTH_LONG).show();
		    	lblError.setText("ICC doesn't exist.");
		    	return false;
			} 
			if (!_ICC.substring(0, 4).equals("2439")) {
				Toast.makeText(this, "ICC doesn't exist.", Toast.LENGTH_LONG).show();
		    	lblError.setText("ICC doesn't exist.");
		    	return false;			
			}
			_IMSI = "630900" + _ICC.substring(6,7) + _ICC.substring(8,16);
		}		
		
		strParameters[22] = _IMSI;
		
		if (_ICC.trim().equals("")){
			if (strParameters[21].equals("")){
				Toast.makeText(this, "The number you are trying to register doesn't exist.", Toast.LENGTH_LONG).show();
				lblError.setText("The number you are trying to register doesn't exist.");
				return false;
			} else {
				if (strParameters[21].length() != 7) {
					Toast.makeText(this, "The number you are trying to register doesn't exist.", Toast.LENGTH_LONG).show();
					lblError.setText("The number you are trying to register doesn't exist.");
					return false;
				}
			}		
		}

		strParameters[23] = getValueFromDB("_us_Login");
		
        gps = new GPSTracker(offlinesimregistrationactivity.this);
        strGeoLocation = "";
        if(gps.canGetLocation()){       
              double latitude = gps.getLatitude();
              double longitude = gps.getLongitude();
              strGeoLocation = "-nLat:" + latitude + "-nLong:" + longitude;
        }else{
        	/*
              gps.showSettingsAlert();
  			  Toast.makeText(this, "GPS is not enabled.", Toast.LENGTH_LONG).show();
              lblError.setText("GPS is not enabled.");              
  			  return false;
  			  */
        }

		String _CellId = getMyCellId();
		strParameters[24] = _CellId + strGeoLocation;
		strParameters[25] = strPersonalEmpreinte;

		//Toast.makeText(getApplicationContext(),strParameters[25] ,Toast.LENGTH_LONG).show();
		if (blnSaveOfflineRegistration(strParameters) == true){
			return true;
		} else {
			return false;
		}

	}
	
	public String getValueFromDB(String strValue){
        String label = "";
        String selectQuery = "SELECT Value FROM securityEntry WHERE Title='"+strValue+"'"; 
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(offlinesimregistrationactivity.this);
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (null != cursor && cursor.moveToFirst()) {
            label = cursor.getString(cursor.getColumnIndex("Value"));
        }

        return label;

    }


    private String getMyCellId()
    {

    	try{
			final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String sLocation="";
			sLocation=libclass.getMyCellId(this,telephony);
			if (sLocation != null && !sLocation.equals(""))
			{
				return sLocation;
				//return location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
			}
			else
			{
				return "";
			}
		}catch (Exception e){
			return "";

		}

    }
	 
	 public void decodeFile(String filePath, int pictsize) {
		  // Decode image size
		  BitmapFactory.Options o = new BitmapFactory.Options();
		  o.inJustDecodeBounds = true;
		  BitmapFactory.decodeFile(filePath, o);

		  // The new size we want to scale to
		  //final int REQUIRED_SIZE = 512;
		  //final int REQUIRED_SIZE = 1024;
		  //final int REQUIRED_SIZE = 2048;
		  
		  final int REQUIRED_SIZE = pictsize;
		  // Find the correct scale value. It should be the power of 2.
		  int width_tmp = o.outWidth, height_tmp = o.outHeight;
		  int scale = 1;
		  while (true) {
		   if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
		    break;
		   width_tmp /= 2;
		   height_tmp /= 2;
		   scale *= 2;
		  }

		  // Decode with inSampleSize
		  BitmapFactory.Options o2 = new BitmapFactory.Options();
		  o2.inSampleSize = scale;
		  bitmap = BitmapFactory.decodeFile(filePath, o2);

		  //image.setImageBitmap(bitmap);
		 }

	 public boolean blnSaveOfflineRegistration(String[] strPram) { 
		 String strSQL;
		 FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(offlinesimregistrationactivity.this);
		 SQLiteDatabase db = mDbHelper.getWritableDatabase();
		 strSQL = "insert into " + FeedEntry.TABLE_NAME_offlineReg + " (FirstName, MiddleName, lastName, Gender, DateOfBirth, Email, Education, PersonalImage, Nationality, IdType, RefNumber, IdSide1Image, IdSide2Image, ResidenceTown, ResidenceStreet, OccupationCategory, OccupationSubCategory, OccupationEmployer, OccupationTown, OccupationStreet, SIMNDC, SIMMSISDN, SIMICC, CreateUser, CreateCellId,PersonalFinger)";
		 strSQL = strSQL + "values('"+strPram[0]+"', '"+strPram[1]+"', '"+strPram[2]+"', '"+strPram[3]+"', '"+strPram[4]+"', '"+strPram[5]+"', '"+strPram[6]+"', '"+strPram[7]+"', '"+strPram[8]+"', '"+strPram[9]+"', '"+strPram[10]+"', '"+strPram[11]+"', '"+strPram[12]+"'";
		 strSQL = strSQL + ", '"+strPram[13]+"', '"+strPram[14]+"', '"+strPram[15]+"', '"+strPram[16]+"', '"+strPram[17]+"', '"+strPram[18]+"', '"+strPram[19]+"', '"+strPram[20]+"', '"+strPram[21]+"', '"+strPram[22]+"', '"+strPram[23]+"', '"+strPram[24]+"', '"+strPram[25]+"');";
		 try {
			    db.execSQL(strSQL);
			    return true;
		 } catch (SQLException e) {
			    return false;
		 }


	 }
	 
		///////SQL procedures
		public final class FeedReaderContract {
		    // To prevent someone from accidentally instantiating the contract class,
		    // give it an empty constructor.
		    public FeedReaderContract() {}

		    /* Inner class that defines the table contents */
		    public  abstract class FeedEntry implements BaseColumns {
				//security entries
		    	public static final String TABLE_NAME = "securityEntry";
		        public static final String COLUMN_NAME_ENTRY_ID = "EntryId";
		        public static final String COLUMN_NAME_TITLE = "Title";
		        public static final String COLUMN_NAME_VALUE = "Value";
		        //offline registration entries
		        public static final String TABLE_NAME_offlineReg = "tbl_offline_registration";
		        public static final String COLUMN_NAME_FirstName = "FirstName";
		        public static final String COLUMN_NAME_MiddleName = "MiddleName";
		        public static final String COLUMN_NAME_lastName = "lastName";
		        public static final String COLUMN_NAME_Gender = "Gender";
		        public static final String COLUMN_NAME_DateOfBirth = "DateOfBirth";
		        public static final String COLUMN_NAME_Email = "Email";
		        public static final String COLUMN_NAME_Education = "Education";
		        public static final String COLUMN_NAME_PersonalImage = "PersonalImage";
		        public static final String COLUMN_NAME_Nationality = "Nationality";
		        public static final String COLUMN_NAME_IdType = "IdType";
		        public static final String COLUMN_NAME_RefNumber = "RefNumber";
		        public static final String COLUMN_NAME_IdSide1Image = "IdSide1Image";
		        public static final String COLUMN_NAME_IdSide2Image = "IdSide2Image";
		        public static final String COLUMN_NAME_ResidenceTown = "ResidenceTown";
		        public static final String COLUMN_NAME_ResidenceStreet = "ResidenceStreet";
		        public static final String COLUMN_NAME_OccupationCategory = "OccupationCategory";
		        public static final String COLUMN_NAME_OccupationSubCategory = "OccupationSubCategory";
		        public static final String COLUMN_NAME_OccupationEmployer = "OccupationEmployer";
		        public static final String COLUMN_NAME_OccupationTown = "OccupationTown";
		        public static final String COLUMN_NAME_OccupationStreet = "OccupationStreet";
		        public static final String COLUMN_NAME_SIMNDC = "SIMNDC";
		        public static final String COLUMN_NAME_SIMMSISDN = "SIMMSISDN";
		        public static final String COLUMN_NAME_SIMICC = "SIMICC";
		        public static final String COLUMN_NAME_CreateUser = "CreateUser";
		        public static final String COLUMN_NAME_CreateCellId = "CreateCellId";
				public static final String COLUMN_NAME_PersonalFinger = "PersonalFinger";
		        //GCM info
		        public static final String TABLE_NAME_GCM_info = "tbl_GCM_info";
		        public static final String COLUMN_NAME_SENDER_ID = "SENDER_ID";	        
		        public static final String COLUMN_NAME_Registration_ID = "Registration_ID";	          
		    }
		}
		
		private static final String TEXT_TYPE = " TEXT";
		private static final String COMMA_SEP = ",";
		private static final String SQL_CREATE_ENTRIES =
		    "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
		    FeedEntry.COLUMN_NAME_ENTRY_ID + " INTEGER PRIMARY KEY," +
		    FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
		    FeedEntry.COLUMN_NAME_VALUE + TEXT_TYPE + 
		    " )";
		private static final String SQL_CREATE_offlineReg =
			    "CREATE TABLE " + FeedEntry.TABLE_NAME_offlineReg + " (" +
			    FeedEntry.COLUMN_NAME_FirstName + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_MiddleName + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_lastName + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_Gender + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_DateOfBirth + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_Email + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_Education + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_PersonalImage + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_Nationality + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_IdType + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_RefNumber + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_IdSide1Image + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_IdSide2Image + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_ResidenceTown + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_ResidenceStreet + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_OccupationCategory + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_OccupationSubCategory + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_OccupationEmployer + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_OccupationTown + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_OccupationStreet + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_SIMNDC + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_SIMMSISDN + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_SIMICC + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_CreateUser + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_CreateCellId + TEXT_TYPE +COMMA_SEP +
		        FeedEntry.COLUMN_NAME_PersonalFinger + TEXT_TYPE +
			    " )";
		private static final String SQL_CREATE_GCM_info =
			    "CREATE TABLE " + FeedEntry.TABLE_NAME_GCM_info + " (" +
			    FeedEntry.COLUMN_NAME_SENDER_ID + TEXT_TYPE + COMMA_SEP +
			    FeedEntry.COLUMN_NAME_Registration_ID + TEXT_TYPE + 
			    " )";		 	

		private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
		private static final String SQL_DELETE_offlineReg = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_offlineReg;
		private static final String SQL_DELETE_GCM_info = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_GCM_info;
		
		public class FeedReaderDbHelper extends SQLiteOpenHelper {
		    // If you change the database schema, you must increment the database version.
		    public static final int DATABASE_VERSION = 1;
		    public static final String DATABASE_NAME = "AfrSLRegistration.db";

		    public FeedReaderDbHelper(Context context) {
		        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		    }
		    public void onCreate(SQLiteDatabase db) {
		        db.execSQL(SQL_CREATE_ENTRIES);
		        db.execSQL(SQL_CREATE_offlineReg);
		        db.execSQL(SQL_CREATE_GCM_info);
		    }
		    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		        // This database is only a cache for online data, so its upgrade policy is
		        // to simply to discard the data and start over
		        db.execSQL(SQL_DELETE_ENTRIES);
		        db.execSQL(SQL_DELETE_offlineReg);
		        db.execSQL(SQL_DELETE_GCM_info);	        
		        onCreate(db);
		    }
		    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		        onUpgrade(db, oldVersion, newVersion);
		    }

		}	


	
}
	
	

