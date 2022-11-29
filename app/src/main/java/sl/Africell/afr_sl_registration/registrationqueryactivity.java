package sl.Africell.afr_sl_registration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.example.pmvungu.afr_erp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class registrationqueryactivity extends Activity {	
	private String text;
	private ImageView mPersonalPhoto;
	private ImageView mIdCardSide1;
	private ImageView mIdCardSide2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registrationquery);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		sFillCBONDC();
	}
	
	private void sFillCBONDC(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerNDC);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.NDC_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
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
	
	public void onButtonQueryClicked(View view) {
    	
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
            showAlertDialog(registrationqueryactivity.this, "No Internet Connection",
                    "You are not connected to the internet.", false);
            return;
    	}
		
    	String strNDC;
    	String strMSISDN;
    	String strGetPhoto;
    	
    	//call the login HTTP API
    	Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
    	strNDC = spinnerNDC.getSelectedItem().toString();
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		strMSISDN = txtMSISDN.getText().toString();
    	
		if (strMSISDN.length() != 7){
			Toast.makeText(this, "The number you are trying to query doesn�t exist.", Toast.LENGTH_LONG).show();
			return;
		} 		
		
		CheckBox checkbox_with_image = (CheckBox) findViewById(R.id.checkbox_with_image);
		if (checkbox_with_image.isChecked()){
			strGetPhoto = "1";
		}
		else {
			strGetPhoto = "0";
		}
		
		
		String strHTTPLink = "http://register.africell.cd/SOP_XML_Get_Registration.aspx?NDC="+strNDC+"&MSISDN="+strMSISDN+"&ID=0&WithImage="+strGetPhoto;
    	new DownloadAndParseXMLTask().execute(strHTTPLink);
	}
	
    private void fillActivity(String[] result)
    {
    	
		EditText txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		txtFirstName.setText(result[0]);
    	
		EditText txtFatherName = (EditText) findViewById(R.id.txtFatherName);
		txtFatherName.setText(result[1]);
    	
		EditText txtLastName = (EditText) findViewById(R.id.txtLastName);
		txtLastName.setText(result[2]);		

		EditText txtGender = (EditText) findViewById(R.id.txtGender);
		txtGender.setText(result[3]);		
		
		EditText txtBirthDate = (EditText) findViewById(R.id.txtBirthDate);
		txtBirthDate.setText(result[4]);		

		EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtEmail.setText(result[5]);		

		EditText txtEducation = (EditText) findViewById(R.id.txtEducation);
		txtEducation.setText(result[6]);		

		EditText txtNationality = (EditText) findViewById(R.id.txtNationality);
		txtNationality.setText(result[8]);	
		
		EditText txtIDType = (EditText) findViewById(R.id.txtIDType);
		txtIDType.setText(result[9]);			
		
		EditText txtReferenceNbr = (EditText) findViewById(R.id.txtReferenceNbr);
		txtReferenceNbr.setText(result[10]);			

		EditText txtTown = (EditText) findViewById(R.id.txtTown);
		txtTown.setText(result[15]);

		EditText txtStreet = (EditText) findViewById(R.id.txtStreet);
		txtStreet.setText(result[16]);
		
		EditText txtOccupationCategory = (EditText) findViewById(R.id.txtOccupationCategory);
		txtOccupationCategory.setText(result[17]);		
		
		EditText txtSubOccupationCategory = (EditText) findViewById(R.id.txtSubOccupationCategory);
		txtSubOccupationCategory.setText(result[18]);			

		EditText txtEmployer = (EditText) findViewById(R.id.txtEmployer);
		txtEmployer.setText(result[19]);			

		EditText txtOccupationTown = (EditText) findViewById(R.id.txtOccupationTown);
		txtOccupationTown.setText(result[22]);			

		EditText txtOccupationStreet = (EditText) findViewById(R.id.txtOccupationStreet);
		txtOccupationStreet.setText(result[23]);			
		
		//get and encode photo
		String strPersonalPhoto;
		String strSide1Photo;
		@SuppressWarnings("unused")
		String strSide2Photo;
		CheckBox checkbox_with_image = (CheckBox) findViewById(R.id.checkbox_with_image);
		mPersonalPhoto = (ImageView) findViewById(R.id.camera_personal_image);
		mIdCardSide1 = (ImageView) findViewById(R.id.camera_idcard1_image);
		mIdCardSide2 = (ImageView) findViewById(R.id.camera_idcard2_image);
		mPersonalPhoto.setImageBitmap(null);
		mIdCardSide1.setImageBitmap(null);
		mIdCardSide2.setImageBitmap(null);
		
		if (checkbox_with_image.isChecked()){
			//camera_personal_image
			strPersonalPhoto = result[7];
			if (strPersonalPhoto == "") {
				mPersonalPhoto.setVisibility(View.GONE);
			} else {
				mPersonalPhoto.setVisibility(View.VISIBLE);
				byte[] decodedString = Base64.decode(strPersonalPhoto, Base64.DEFAULT);
				Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
				mPersonalPhoto.setImageBitmap(decodedByte);
			}

			//Id side 1 image
			strSide1Photo = result[11];
			if (strSide1Photo == "") {
				mIdCardSide1.setVisibility(View.GONE);
			} else {
				mIdCardSide1.setVisibility(View.VISIBLE);
				byte[] decodedString1 = Base64.decode(strSide1Photo, Base64.DEFAULT);
				Bitmap decodedByte1 = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.length);
				mIdCardSide1.setImageBitmap(decodedByte1);
			}

			//Id side 2 image
			//strSide2Photo = result[12];
			//if (strSide2Photo == "") {
			//	mIdCardSide2.setVisibility(View.GONE);
			//} else {
			//	mIdCardSide2.setVisibility(View.VISIBLE);
			//	byte[] decodedString2 = Base64.decode(strSide2Photo, Base64.DEFAULT);
			//	Bitmap decodedByte2 = BitmapFactory.decodeByteArray(decodedString2, 0, decodedString2.length);
			//	mIdCardSide2.setImageBitmap(decodedByte2);
			//}			

		}
		else {
			mPersonalPhoto.setVisibility(View.GONE);
			mIdCardSide1.setVisibility(View.GONE);
			mIdCardSide2.setVisibility(View.GONE);
		}
		mIdCardSide2.setVisibility(View.GONE);
    }
    
	public class DownloadAndParseXMLTask extends AsyncTask<String, Void, String[]> 
	{
		 // private ProgressDialog dialog;
		 private ProgressDialog dialog = new ProgressDialog(registrationqueryactivity.this);
		 
		 @Override
		 protected void onPreExecute() {
		  dialog.setMessage("Seeking Registration...");
		  dialog.show();
		 }
		 
		protected String[] doInBackground(String... urls) 
			{
			return parse(urls[0]);
			}
		@Override
		protected void onPostExecute(String[] result) 
			{

	        fillActivity(result);  
	        dialog.dismiss();
	        //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
			}
		
		
	}
	

    public String[] parse(String strURL) 
    {
    	String _FirstName = "";
    	String _MiddleName = "";
    	String _lastName = "";
    	String _Gender = "";
    	String _DateOfBirth = "";
    	String _Email = "";
    	String _Education = "";
    	String _PersonalImage = "";
    	String _Nationality = "";
    	String _IdType = "";
    	String _RefNumber = "";
    	String _IdSide1Image = "";
    	String _IdSide2Image = "";
    	String _ResidenceRegion = "";
    	String _ResidenceDistrict = "";
    	String _ResidenceTown = "";
    	String _ResidenceStreet = "";
    	String _OccupationCategory = "";
    	String _OccupationSubCategory = "";
    	String _OccupationEmployer = "";
    	String _OccupationRegion = "";
    	String _OccupationDistrict = "";
    	String _OccupationTown = "";
    	String _OccupationStreet = "";
    	String _SIMNDC = "";
    	String _SIMMSISDN = "";
    	String _SIMICC = "";
	    
    	String[] strParseResult;
    	strParseResult = new String[27];
    	
    	InputStream is = null;	
    	try 
		{
    		is = OpenHttpConnection(strURL);
		}
		catch (IOException e1) 
		{
			Log.d("NetworkingActivity", e1.getLocalizedMessage());
		}
    	
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();
 
            parser.setInput(is, null);
 
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                case XmlPullParser.START_TAG:
                    if (tagname.equalsIgnoreCase("Login")) {
                        // create a new instance of employee
                    	_FirstName = "";
                    	_MiddleName = "";
                    	_lastName = "";
                    	_Gender = "";
                    	_DateOfBirth = "";
                    	_Email = "";
                    	_Education = "";
                    	_PersonalImage = "";
                    	_Nationality = "";
                    	_IdType = "";
                    	_RefNumber = "";
                    	_IdSide1Image = "";
                    	_IdSide2Image = "";
                    	_ResidenceRegion = "";
                    	_ResidenceDistrict = "";
                    	_ResidenceTown = "";
                    	_ResidenceStreet = "";
                    	_OccupationCategory = "";
                    	_OccupationSubCategory = "";
                    	_OccupationEmployer = "";
                    	_OccupationRegion = "";
                    	_OccupationDistrict = "";
                    	_OccupationTown = "";
                    	_OccupationStreet = "";
                    	_SIMNDC = "";
                    	_SIMMSISDN = "";
                    	_SIMICC = "";
                    }
                    break;
 
                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    //if (tagname.equalsIgnoreCase("AccountInfo")) {
                    //    // add employee object to list
                    //	clsaccountinfoList.add(ClsAccountInfo);
                    //} else 
                   if (tagname.equalsIgnoreCase("FirstName")) {
                	   _FirstName = text;                     		
                    } else if (tagname.equalsIgnoreCase("MiddleName")) {
                    	_MiddleName = text;                     		
                    } else if (tagname.equalsIgnoreCase("lastName")) {
                    	_lastName = text;                     		
                    } else if (tagname.equalsIgnoreCase("Gender")) {
                    	_Gender = text;                     		
                    } else if (tagname.equalsIgnoreCase("DateOfBirth")) {
                    	_DateOfBirth = text;                     		
                    } else if (tagname.equalsIgnoreCase("Email")) {
                    	_Email = text;                     		
                    } else if (tagname.equalsIgnoreCase("Education")) {
                    	_Education = text;                     		
                    } else if (tagname.equalsIgnoreCase("PersonalImage")) {
                    	_PersonalImage = text;                     		
                    } else if (tagname.equalsIgnoreCase("Nationality")) {
                    	_Nationality = text;                     		
                    } else if (tagname.equalsIgnoreCase("IdType")) {
                    	_IdType = text;                     		
                    } else if (tagname.equalsIgnoreCase("RefNumber")) {
                    	_RefNumber = text;                     		
                    } else if (tagname.equalsIgnoreCase("IdSide1Image")) {
                    	_IdSide1Image = text;                     		
                    } else if (tagname.equalsIgnoreCase("IdSide2Image")) {
                    	_IdSide2Image = text;                     		
                    } else if (tagname.equalsIgnoreCase("ResidenceRegion")) {
                    	_ResidenceRegion = text;                     		
                    } else if (tagname.equalsIgnoreCase("ResidenceDistrict")) {
                    	_ResidenceDistrict = text;                     		
                    } else if (tagname.equalsIgnoreCase("ResidenceTown")) {
                    	_ResidenceTown = text;                     		
                    } else if (tagname.equalsIgnoreCase("ResidenceStreet")) {
                    	_ResidenceStreet = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationCategory")) {
                    	_OccupationCategory = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationSubCategory")) {
                    	_OccupationSubCategory = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationEmployer")) {
                    	_OccupationEmployer = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationRegion")) {
                    	_OccupationRegion = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationDistrict")) {
                    	_OccupationDistrict = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationTown")) {
                    	_OccupationTown = text;                     		
                    } else if (tagname.equalsIgnoreCase("OccupationStreet")) {
                    	_OccupationStreet = text;                     		
                    } else if (tagname.equalsIgnoreCase("SIMNDC")) {
                    	_SIMNDC = text;                     		
                    } else if (tagname.equalsIgnoreCase("SIMMSISDN")) {
                    	_SIMMSISDN = text;                     		
                    } else if (tagname.equalsIgnoreCase("SIMICC")) {
                    	_SIMICC = text;                     		
                    }
                   
               		text = "";
                    break;
                default:
                    break;
                }
                eventType = parser.next();
            }
 
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        strParseResult[0] = _FirstName;
        strParseResult[1] = _MiddleName;
        strParseResult[2] = _lastName;
        strParseResult[3] = _Gender;
        strParseResult[4] = _DateOfBirth;
        strParseResult[5] = _Email;
        strParseResult[6] = _Education;
        strParseResult[7] = _PersonalImage;
        strParseResult[8] = _Nationality;
        strParseResult[9] = _IdType;
        strParseResult[10] = _RefNumber;
        strParseResult[11] = _IdSide1Image;
        strParseResult[12] = _IdSide2Image;
        strParseResult[13] = _ResidenceRegion;
        strParseResult[14] = _ResidenceDistrict;
        strParseResult[15] = _ResidenceTown;
        strParseResult[16] = _ResidenceStreet;
        strParseResult[17] = _OccupationCategory;
        strParseResult[18] = _OccupationSubCategory;
        strParseResult[19] = _OccupationEmployer;
        strParseResult[20] = _OccupationRegion;
        strParseResult[21] = _OccupationDistrict;
        strParseResult[22] = _OccupationTown;
        strParseResult[23] = _OccupationStreet;
        strParseResult[24] = _SIMNDC;
        strParseResult[25] = _SIMMSISDN;
        strParseResult[26] = _SIMICC;
        
        return strParseResult;
    }

	//HTTP Post
	private InputStream OpenHttpConnection(String urlString) throws IOException
	{
		InputStream in = null;
		int response = -1;
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		if (!(conn instanceof HttpURLConnection))
			throw new IOException("Not an HTTP connection");
		try
			{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) 
				{
				in = httpConn.getInputStream();
				}
			}
		catch (Exception ex)
			{
			Log.d("Networking", ex.getLocalizedMessage());
			throw new IOException("Error connecting");
			}
        
		return in;
	}
	
	
	
}
