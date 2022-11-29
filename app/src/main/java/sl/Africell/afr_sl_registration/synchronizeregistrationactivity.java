package sl.Africell.afr_sl_registration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.synchronizeregistrationactivity.FeedReaderContract.FeedEntry;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static sl.Africell.afr_sl_registration.apiUrl.baseUrl;

public class synchronizeregistrationactivity extends Activity {
	private String strFinalXML;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_synchronizeregistration);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		GetRownCount();
		
	}
	
	 public void GetRownCount() { 
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(synchronizeregistrationactivity.this);
	    // Gets the data repository in write mode
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor mCount= db.rawQuery("select count(*) count from "+FeedEntry.TABLE_NAME_offlineReg+";", null);
		mCount.moveToFirst();
		String count= mCount.getString(0);
		mCount.close();
		TextView lblNbrOfRegistrations = (TextView) findViewById(R.id.lblNbrOfRegistrations);
		Button syncButton = (Button)findViewById(R.id.btnSynchronize);
		syncButton.setVisibility(android.view.View.VISIBLE);
		syncButton.setActivated(true);
		syncButton.setEnabled(Integer.parseInt(count) > 0);
		lblNbrOfRegistrations.setText("Number of pending registration : "+count);
	 }
	 
	 
	 public void onButtonSynchronizeClicked(View view) {
			//check Internet connection
			boolean isConnected = isNetworkAvailable();
	    	if (isConnected)
	    	{
	            TextView lblError = (TextView) findViewById(R.id.lblError); 
	            lblError.setText("");
	    	}
	    	else
	    	{
	            TextView lblError = (TextView) findViewById(R.id.lblError); 
	            lblError.setText("You are not connected to the internet");	
	            showAlertDialog(synchronizeregistrationactivity.this, "No Internet Connection",
	                    "You are not connected to the internet.", false);
	            return;
	     	}		 
		 
		    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(synchronizeregistrationactivity.this);
		    // Gets the data repository in write mode
		    SQLiteDatabase db = mDbHelper.getReadableDatabase();
			Cursor CurAll= db.rawQuery("select * from "+FeedEntry.TABLE_NAME_offlineReg+" LIMIT 1;", null);
			if (CurAll != null) {
				CurAll.moveToFirst();
				//do {
					String _FirstName = CurAll.getString(0);
					String _MiddleName = CurAll.getString(1);
					String _lastName = CurAll.getString(2);
					String _Gender = CurAll.getString(3);
					String _DateOfBirth = CurAll.getString(4);
					String _Email = CurAll.getString(5);
					String _Education = CurAll.getString(6);
					String _PersonalImageURL = CurAll.getString(7);
					String _Nationality = CurAll.getString(8);
					String _IdType = CurAll.getString(9);
					String _RefNumber = CurAll.getString(10);
					String _IdSide1ImageURL = CurAll.getString(11);
					//String _IdSide2ImageURL = CurAll.getString(12);
					String _ResidenceTown = CurAll.getString(13);
					String _ResidenceStreet = CurAll.getString(14);
					String _OccupationCategory = CurAll.getString(15);
					String _OccupationSubCategory = CurAll.getString(16);
					String _OccupationEmployer = CurAll.getString(17);
					String _OccupationTown = CurAll.getString(18);
					String _OccupationStreet = CurAll.getString(19);
					String _SIMNDC = CurAll.getString(20);
					String _SIMMSISDN = CurAll.getString(21);
					String _SIMICC = CurAll.getString(22);
					String _CreateUser = CurAll.getString(23);
					String _CreateCellId = CurAll.getString(24);
				    String _PersonalFingerURL = CurAll.getString(25);

					String strPersonalPhoto = "";
					String strIdCardSide1 = "";
					String strIdCardSide2 = "";
				    String strPersonalFinger = "";
					
					if (!_PersonalImageURL.equals("")){
						strPersonalPhoto = strGetImageString(_PersonalImageURL);
					}
					
					if (!_IdSide1ImageURL.equals("")){
						strIdCardSide1 = strGetImageString(_IdSide1ImageURL);
					}
					/*
					if (!_IdSide2ImageURL.equals("")){
						strIdCardSide2 = strGetImageString(_IdSide2ImageURL);
					}
					*/

				if (!_PersonalFingerURL.equals("")){
					strPersonalFinger = _PersonalFingerURL;
				}
					strFinalXML = "";
					strFinalXML = strFinalXML+"<registration>";
					strFinalXML = strFinalXML+"<personalinfo>";
					strFinalXML = strFinalXML+"<firstname>"+_FirstName+"</firstname>";
					strFinalXML = strFinalXML+"<fathername>"+_MiddleName+"</fathername>";
					strFinalXML = strFinalXML+"<familyname>"+_lastName+"</familyname>";
					strFinalXML = strFinalXML+"<gender>"+_Gender+"</gender>";
					strFinalXML = strFinalXML+"<birthdate>"+_DateOfBirth+"</birthdate>";
					strFinalXML = strFinalXML+"<email>"+_Email+"</email>";
					strFinalXML = strFinalXML+"<education>"+_Education+"</education>";
					strFinalXML = strFinalXML+"</personalinfo>";
					strFinalXML = strFinalXML+"<nationalityinfo>";
					strFinalXML = strFinalXML+"<nationality>"+_Nationality+"</nationality>";
					strFinalXML = strFinalXML+"<idtype>"+_IdType+"</idtype>";
					strFinalXML = strFinalXML+"<referencenbr>"+_RefNumber+"</referencenbr>";
					strFinalXML = strFinalXML+"</nationalityinfo>";
					strFinalXML = strFinalXML+"<residenceinfo>";
					strFinalXML = strFinalXML+"<residencetown>"+_ResidenceTown+"</residencetown>";
					strFinalXML = strFinalXML+"<residencestreet>"+_ResidenceStreet+"</residencestreet>";		
					strFinalXML = strFinalXML+"</residenceinfo>";	
					strFinalXML = strFinalXML+"<occupationinfo>";
					strFinalXML = strFinalXML+"<category>"+_OccupationCategory+"</category>";
					strFinalXML = strFinalXML+"<subcategory>"+_OccupationSubCategory+"</subcategory>";
					strFinalXML = strFinalXML+"<employer>"+_OccupationEmployer+"</employer>";
					strFinalXML = strFinalXML+"<occupationtown>"+_OccupationTown+"</occupationtown>";
					strFinalXML = strFinalXML+"<occupationstreet>"+_OccupationStreet+"</occupationstreet>";
					strFinalXML = strFinalXML+"</occupationinfo>";
					strFinalXML = strFinalXML+"<siminfo>";
					strFinalXML = strFinalXML+"<ndc>"+_SIMNDC+"</ndc>";
					strFinalXML = strFinalXML+"<msisdn>"+_SIMMSISDN+"</msisdn>";
					strFinalXML = strFinalXML+"<icc>"+_SIMICC+"</icc>";
					strFinalXML = strFinalXML+"</siminfo>";
					strFinalXML = strFinalXML+"<userinfo>";
					strFinalXML = strFinalXML+"<user>"+_CreateUser+"</user>";
					strFinalXML = strFinalXML+"<cellid>"+_CreateCellId+"</cellid>";
					strFinalXML = strFinalXML+"</userinfo>";		
					strFinalXML = strFinalXML+"<capturedphoto>";
					strFinalXML = strFinalXML+"<personalphoto>"+strPersonalPhoto+"</personalphoto>";
				    strFinalXML = strFinalXML+"<personalEmpreinte>"+strPersonalFinger+"</personalEmpreinte>";
					strFinalXML = strFinalXML+"<idcardside1>"+strIdCardSide1+"</idcardside1>";
					strFinalXML = strFinalXML+"<idcardside2>"+strIdCardSide2+"</idcardside2>";
					strFinalXML = strFinalXML+"</capturedphoto>";
					strFinalXML = strFinalXML + "</registration>";
					
					Toast.makeText(this, "Uploading data for " + _SIMNDC+_SIMMSISDN + "...", Toast.LENGTH_LONG).show();

					new ImageUploadTask().execute(_SIMNDC,_SIMMSISDN,_PersonalImageURL,_IdSide1ImageURL);
					/*
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					*/
				//} while (CurAll.moveToNext());
					CurAll.close();
			}
	 }

	 public String strGetImageString(String ImageURL){
		 String StrImageString = "";
		 Bitmap thumbnail;
		 File imgFile = new File(ImageURL);
	        if (imgFile.exists()) {
			 decodeFile(ImageURL);
			 thumbnail = bitmap;
			 ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			 thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	   	   	 byte[] bytedata = bytes.toByteArray();
	   	   	 StrImageString = base64.encodeBytes(bytedata);
	        }
		 return StrImageString;
	 }
	 
	 public void decodeFile(String filePath) {
		  // Decode image size
		  BitmapFactory.Options o = new BitmapFactory.Options();
		  o.inJustDecodeBounds = true;
		  BitmapFactory.decodeFile(filePath, o);

		  // The new size we want to scale to
		  //final int REQUIRED_SIZE = 512;
		  final int REQUIRED_SIZE = 1024;
		  //final int REQUIRED_SIZE = 2048;
		  
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
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.synchronizeregistrationactivity, menu);
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
	
	public class ImageUploadTask extends AsyncTask<String, Void, String[]> {
		 private String webAddressToPost =baseUrl+ "SOP_XML_Insert_Registration.aspx";

		 // private ProgressDialog dialog;
		 private ProgressDialog dialog = new ProgressDialog(synchronizeregistrationactivity.this);

		 @Override
		 protected void onPreExecute() {
			  TextView lblError = (TextView) findViewById(R.id.lblError);
			  lblError.setText("");
			  dialog.setMessage("Uploading...");
			  Button btnSynchronize = (Button)findViewById(R.id.btnSynchronize);
			  btnSynchronize.setVisibility(View.GONE);
			  dialog.show();
		 }

		 protected String[] doInBackground(String... params) {
			 String strResponse[] = new String[5];
			 try {
			  String xmlString = strFinalXML;
			  DefaultHttpClient httpClient = new DefaultHttpClient();
			  HttpPost httppost = new HttpPost(webAddressToPost + "/orders/order"); 
			  httppost.addHeader("Accept", "text/xml");
			  httppost.addHeader("Content-Type", "application/xml");
			  try
			      {
			          StringEntity entity = new StringEntity(xmlString, "UTF-8");
			          entity.setContentType("text/xml");
			          httppost.setEntity(entity);
			          HttpResponse response = httpClient.execute(httppost);
			          BasicResponseHandler responseHandler = new BasicResponseHandler();
			             
			             if (response != null) 
			             {
			                 try {
			                     strResponse[0] = responseHandler.handleResponse(response);
			                    } catch (HttpResponseException e) 
			                    {
			                        e.printStackTrace();  
			                    } catch (IOException e) 
			                    {
			                            e.printStackTrace();
			                    }
			             }

			      }
			      catch (Exception ex)
			      {
			                 ex.printStackTrace();
			      }			  
	       
		  } catch (Exception e) {
		   // something went wrong. connection with the server error
		  }
		  strResponse[1] = params[0];
		  strResponse[2] = params[1];
		  strResponse[3] = params[2];
		  strResponse[4] = params[3];
		 // strResponse[5] = params[4];
		  return strResponse;
		 }

		 @Override
		 protected void onPostExecute(String result[]) {
		  dialog.dismiss();
		  if (result[0].equals("<Reply><MESSAGE>Successful</MESSAGE></Reply>")){
			  Toast.makeText(getApplicationContext(), "Registered successfully.",Toast.LENGTH_LONG).show();   
			  deleteSynchronizedRegistration(result[1],result[2],result[3],result[4]);
			  
			  GetRownCount();
	          
	        	/*
	        	//clean PersonalPhoto
	        	if (!strPersonalPhotoURL.equals("")){
	        		//File file1 = new File(strPersonalPhotoURL);
	        		//file1.delete();
	        		getContentResolver().delete(PersonalPhotoUri, null, null);
	            }
	            //clean Side1 
	        	if (!strSide1URL.equals("")){
	        		//File file2 = new File(strSide1URL);
		            //file2.delete();	
	        		getContentResolver().delete(Side1Uri, null, null);
	        	}
	          //clean Side2
	            if (!strSide2URL.equals("")){
		        	//File file3 = new File(strSide2URL);
		            //file3.delete();	     
		            getContentResolver().delete(Side2Uri, null, null);
	            }
	            */
	            
		  }else {
			  TextView lblError = (TextView) findViewById(R.id.lblError);
			  lblError.setText("Synchronization failed.");
			  Toast.makeText(getApplicationContext(), "Synchronization failed.",Toast.LENGTH_LONG).show(); 
		  }
		 }
		}	
	

	public boolean deleteSynchronizedRegistration(String _NDC, String _MSISDN, String _personImgURL, String _idImgURL) 
	{
		
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(synchronizeregistrationactivity.this);
	    // Gets the data repository in write mode
	    SQLiteDatabase db = mDbHelper.getWritableDatabase();
	    String strWhere = FeedEntry.COLUMN_NAME_SIMNDC + "='"+ _NDC +"'";
	    strWhere = strWhere + " and " + FeedEntry.COLUMN_NAME_SIMMSISDN + "='" + _MSISDN+"'";
		 try {
			 db.delete(FeedEntry.TABLE_NAME_offlineReg, strWhere, null);
			 Log.i("-->","Personal Img URL: "+_personImgURL);
			 Log.i("-->","ID Img URL: "+_idImgURL);
			 if (!(_personImgURL.equals("")))
				 deleteImage(_personImgURL);
			 if (!(_idImgURL.equals("")))
				 deleteImage(_idImgURL);
			 return true;
		 } catch (SQLException e) {
			 return false;
		 }
	    
	}
	
	public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
       } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
	
	public void deleteImage(String imageURL) {
        //String file_dj_path = Environment.getExternalStorageDirectory() + "/ECP_Screenshots/abc.jpg";
        File fdelete = new File(imageURL);
        if (fdelete.exists()) {
        	getContentResolver().delete(getImageContentUri(getApplicationContext(), fdelete), null, null);
        	/*
            if (fdelete.delete()) {
                Log.i("-->", "file Deleted :" + imageURL);
                callBroadCast();
            } else {
                Log.i("-->", "file not Deleted :" + imageURL);
            }
            */
        }
    }

    public void callBroadCast() {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");
            MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                /*
                 *   (non-Javadoc)
                 * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                 */
                public void onScanCompleted(String path, Uri uri) {
                    Log.e("ExternalStorage", "Scanned " + path + ":");
                    Log.e("ExternalStorage", "-> uri=" + uri);
                }
            });
        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }
	
	/*check if device is connected to the Internet*/
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
		    FeedEntry.COLUMN_NAME_CreateCellId + TEXT_TYPE +COMMA_SEP+
			FeedEntry.COLUMN_NAME_PersonalFinger + TEXT_TYPE +
		    " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
	private static final String SQL_DELETE_offlineReg = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_offlineReg;
	
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
	    }
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // This database is only a cache for online data, so its upgrade policy is
	        // to simply to discard the data and start over
	        db.execSQL(SQL_DELETE_ENTRIES);
	        db.execSQL(SQL_DELETE_offlineReg);
	        onCreate(db);
	    }
	    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        onUpgrade(db, oldVersion, newVersion);
	    }

	}	
}
