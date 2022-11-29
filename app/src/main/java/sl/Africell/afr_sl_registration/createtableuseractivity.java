package sl.Africell.afr_sl_registration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.newregistrationactivity.ImageUploadTask;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class createtableuseractivity extends Activity {
	public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
    private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
    private ImageView mPersonalPhoto;
    private String strPersonalPhoto = "";
    private Uri PersonalPhotoUri;
    private String strPersonalPhotoURL;
	private Bitmap bitmap;    
    private String strFinalXML = "";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_createtableuseractivity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		sFillCBONDC();
		
	}
	
	private void sFillCBONDC(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerNDC);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.NDC_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}	
	
	public void onUploadClick(View view) {
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
            showAlertDialog(createtableuseractivity.this, "No Internet Connection",
                    "You are not connected to the internet.", false);
            return;
    	}		
		
        TextView lblError = (TextView) findViewById(R.id.lblError); 
        lblError.setText("");	
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		String _MSISDN = txtMSISDN.getText().toString();
		if (_MSISDN.length() != 7){
            lblError.setText("MSISDN cannot be empty.");	
            return;
		}		
		if (strPersonalPhoto.length() == 0 ){ 
            lblError.setText("Contract picture cannot be empty.");	
            return;
		}


		sPrepareDataBeforeUpload();
		new ImageUploadTask().execute();
		
	}
	
	public void CaptureContractPhoto(View V){
		mPersonalPhoto = (ImageView) findViewById(R.id.camera_personal_image);
		ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Personal Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
        startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);
        
	}	
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == CAMERA_PIC_REQUEST_PersonalPhoto) {
          if (resultCode == Activity.RESULT_OK) {
              try {
            	  Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), PersonalPhotoUri);
            	  strPersonalPhotoURL = getRealPathFromURI(PersonalPhotoUri);  
            	  decodeFile(strPersonalPhotoURL, 1024);
                  mPersonalPhoto.setImageBitmap(bitmap);
                  thumbnail = bitmap;
            	  //3
            	  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            	  thumbnail.compress(Bitmap.CompressFormat.JPEG, 75, bytes);
                  //3.5 prepare the image to be uploaded
        	   	  byte[] bytedata = bytes.toByteArray();
        	   	  strPersonalPhoto = base64.encodeBytes(bytedata);
            	  //}
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }	 
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
      
 	 public String getRealPathFromURI(Uri contentUri) {
	        String[] proj = { MediaStore.Images.Media.DATA };
	        @SuppressWarnings("deprecation")
			android.database.Cursor cursor = managedQuery(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    }	      
 	 
 	public void sPrepareDataBeforeUpload() {
 		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		
		String _NDC = spinnerNDC.getSelectedItem().toString();
		String _MSISDN = txtMSISDN.getText().toString();
 		strFinalXML = "";
		strFinalXML = strFinalXML+"<contract>";
		strFinalXML = strFinalXML+"<SIMNDC>"+_NDC+"</SIMNDC>";
		strFinalXML = strFinalXML+"<SIMSN>"+_MSISDN+"</SIMSN>";
		strFinalXML = strFinalXML+"<contractImage>"+strPersonalPhoto+"</contractImage>";
		strFinalXML = strFinalXML+"</contract>";
		
 	}
 	 
 	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		 private String webAddressToPost = "http://register.africell.cd/SOP_XML_insert_table_agent_contract.aspx";

		 // private ProgressDialog dialog;
		 private ProgressDialog dialog = new ProgressDialog(createtableuseractivity.this);

		 @Override
		 protected void onPreExecute() {
			  dialog.setMessage("Uploading...");
			  Button btnUpload = (Button)findViewById(R.id.btnUpload);
			  btnUpload.setVisibility(View.GONE);
			  dialog.show();
		 }

		 @Override
		 protected String doInBackground(Void... params) {
			 String strResponse = null;
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
			                     strResponse = responseHandler.handleResponse(response);
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
		  return strResponse;
		 }

		 @Override
		 protected void onPostExecute(String result) {
		  dialog.dismiss();
		  if (result.equals("<Reply><MESSAGE>Successful</MESSAGE></Reply>")){
			  Toast.makeText(getApplicationContext(), "Registered successfully.",Toast.LENGTH_LONG).show();
	        	String strMessage = "";    	
	        	Intent intent = new Intent(getApplicationContext(), secondactivity.class);
	        	intent.putExtra(EXTRA_MESSAGE, strMessage);   
	        	startActivity(intent);	
	        	//clean PersonalPhoto
	        	if (!strPersonalPhotoURL.equals("")){
	        		//File file1 = new File(strPersonalPhotoURL);
	        		//file1.delete();
	        		getContentResolver().delete(PersonalPhotoUri, null, null);
	            }
		  }else {
			  TextView lblError = (TextView) findViewById(R.id.lblError);
			  Toast.makeText(getApplicationContext(), "Registration failed.",Toast.LENGTH_LONG).show(); 
             lblError.setText("Registration failed. Contact administrator."); 			  
		  }
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
	 	
 	 
 	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.createtableuseractivity, menu);
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
}
