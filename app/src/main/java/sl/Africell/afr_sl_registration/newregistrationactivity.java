package sl.Africell.afr_sl_registration;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import sl.Africell.afr_sl_registration.base64;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.pmvungu.afr_erp.R;

import cn.com.aratek.demo.FingerprintDemo;
import sl.Africell.afr_sl_registration.loginactivity.FeedReaderContract.FeedEntry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Base64;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import cn.com.aratek.demo.DemoActivity;
import android.content.pm.PackageManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static sl.Africell.afr_sl_registration.SessionManager.bytedata1;
import static sl.Africell.afr_sl_registration.SessionManager.fg;
import static sl.Africell.afr_sl_registration.SessionManager.bitmap;
import static sl.Africell.afr_sl_registration.apiUrl.baseUrl;
public class newregistrationactivity extends Activity {
	public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
	private static final int CAMERA_PIC_REQUEST_PersonalPhoto = 1111;
	private static final int CAMERA_PIC_REQUEST_IdCardSide1 = 1112;
	private static final int CAMERA_PIC_REQUEST_IdCardSide2 = 1113;
	private static final int CAMERA_PIC_REQUEST_FingerPrint = 1114;
	private static final int SCAN_SIM_BAR_CODE = 49374;

	private ImageView mPersonalPhoto;
	private String strPersonalPhoto;
	private Uri PersonalPhotoUri;
	private String strPersonalPhotoURL;

	private ImageView mPersonalEmpreinte;
	private String strPersonalEmpreinte;
	private Uri PersonalEmpreinteUri;
	private String strPersonalEmpreinteURL;

	private ImageView mIdCardSide1;
	private String strIdCardSide1;
	private Uri Side1Uri;
	private String strSide1URL;

	private ImageView mIdCardSide2;
	private String strIdCardSide2;
	private Uri Side2Uri;
	private String strSide2URL;
	private String strFinalXML;
	private String sfinger;
	private Bitmap bitmap;
	private String text;
	libClass libclass;
	// number of images to select
	private static final int PICK_IMAGE = 1;
	private String strGeoLocation;

	@SuppressWarnings("unused")
	private int intNbrRequestedFace;
	SessionManager session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_registration);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		libClass libclass = new libClass(getApplicationContext());
		try {
			//libClass libclass = new libClass(getApplicationContext());

			if (libclass.checkPermission()) {
				//  Toast.makeText(Activity_login.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
			} else {
				libclass.requestPermission(newregistrationactivity.this);
			}

			sFillCBOGender();
			sFillCBOEducation();
			sFillCBONationality();
			sFillCBOIdType();
			sFillCBOTown();
			sFillCBOOccupationCategory();
			sFillCBOOccupationSubCategory();
			sFillCBOOccupationTown();
			sFillCBONDC();

			strPersonalPhoto = "";
			strPersonalEmpreinte = "";
			sfinger="";

			strIdCardSide1 = "";
			strIdCardSide2 = "";
			intNbrRequestedFace = 0;
			strPersonalPhotoURL = "";
			strPersonalEmpreinteURL = "";
			strSide1URL = "";
			strSide2URL = "";
			sEnableDisable("0");

		} catch (Exception e) {
			e.printStackTrace();
			libclass.popMessage("Error occur",e.getMessage());
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(e.getMessage());
			builder.setTitle("Error occur");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

				}
			});
		}

		//getFingerprint();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//getFingerprint(); active it for finger print device
		//openDevice();
		//Toast.makeText(getApplicationContext(), "onResume" , Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		//closeDevice(false);
		fg="0";
		strPersonalEmpreinte="";
		bytedata1=null;
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
		mPersonalEmpreinte.setVisibility(View.GONE);
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
			//strPersonalEmpreinte = fg;  // here with fg=fgFeat
		  }

	}
	public void onValidateClick(View view) {

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
			showAlertDialog(newregistrationactivity.this, "No Internet Connection",
					"You are not connected to the internet.", false);
			return;
		}


		TextView lblError = (TextView) findViewById(R.id.lblError);

		//get and check IMSI
		EditText txtICC = (EditText) findViewById(R.id.txtICC);
		String _ICC = txtICC.getText().toString();
		String _IMSI = "";
		if (!_ICC.trim().equals("")){
			/*if (_ICC.length() != 16){
				Toast.makeText(this, "ICC doesnt exist.", Toast.LENGTH_LONG).show();
				lblError.setText("ICC doesnt exist.");
				return;
			}
			if (!_ICC.substring(0, 4).equals("2439")) {
				Toast.makeText(this, "ICC doesnt exist.", Toast.LENGTH_LONG).show();
				lblError.setText("ICC doesnt exist.");
				return;
			}
			//_IMSI = "630900" + _ICC.substring(6,7) + _ICC.substring(8,16);*/
			switch (_ICC.length()){
                case 10 :
                   // _IMSI = "630900"  + _ICC.substring(0,10);
					_IMSI = "630900"  +_ICC.substring(0, 1)+ _ICC.substring(2,10);
                    break;
                case 15:
                    if (!_ICC.substring(0, 6).equals("630900")){
                        Toast.makeText(this, "IMSI Format must be like 630900XXXXXXXXXX", Toast.LENGTH_LONG).show();
                        lblError.setText("IMSI Format must be like 630900XXXXXXXXXX.");
                        return;
                    }
                    _IMSI=_ICC;
			    break;
			    default:
                    Toast.makeText(this, "IMSI doesn't exist.", Toast.LENGTH_LONG).show();
                    lblError.setText("IMSI doesn't exist.");
                    return;
             }


		}

		//get and check MSISDN
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		String _MSISDN = "";
		String _SN = txtMSISDN.getText().toString().trim();
		String _NDC = spinnerNDC.getSelectedItem().toString().trim();

		if (!_SN.trim().equals("")){
			if (_SN.length() != 7){
				Toast.makeText(this, "MSISDN doesnt exist.", Toast.LENGTH_LONG).show();
				lblError.setText("MSISDN doesnt exist.");
				return;
			}
			_MSISDN = "243" + _NDC.substring(1, 3) + _SN;
		}

		//validate
		// String HTTP_ValidateTableSIM = "http://87.238.116.200/SOP_XML_validate_IMSI_MSISDN.aspx?IMSI="+_IMSI+"&MSISDN="+_MSISDN;
		 String HTTP_ValidateTableSIM =baseUrl + "SOP_XML_validate_IMSI_MSISDN_DIAS.aspx?IMSI="+_IMSI+"&MSISDN="+_MSISDN;

		new DownloadAndParseValidateTableSIMXMLTask().execute(HTTP_ValidateTableSIM);
		//Toast.makeText(getApplicationContext(),  HTTP_ValidateTableSIM , Toast.LENGTH_SHORT).show();
	}
	public class DownloadAndParseValidateTableSIMXMLTask extends AsyncTask<String, Void, String[]> {
		private ProgressDialog dialog = new ProgressDialog(newregistrationactivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Please wait while validating SIM");
			dialog.setCancelable(true);
			dialog.show();
		}
		protected String[] doInBackground(String... urls)
		{
			return parseValidateTableSIM(urls[0]);
		}
		@Override
		protected void onPostExecute(String[] result)
		{
			TextView lblError = (TextView) findViewById(R.id.lblError);
			EditText txtICC = (EditText) findViewById(R.id.txtICC);
			Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
			EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);

			//check if there is error
			if (!result[0].trim().equals("")){
				lblError.setText(result[0].toString());
				dialog.dismiss();
				return;
			} else {
				lblError.setText("");
				txtICC.setText(result[1]);
				String _NDC = result[2].substring(3, 5);
				String _SN = result[2].substring(5, 12);

				if (_NDC.equals("90")){
					spinnerNDC.setSelection(0);
				} else if (_NDC.equals("91")) {
					spinnerNDC.setSelection(1);
				}
				txtMSISDN.setText(_SN);
				sEnableDisable("1");
			}
			dialog.dismiss();
		}
	}

	public String[] parseValidateTableSIM(String strURL)
	{
		String _Error = "";
		String _IMSI = "";
		String _MSISDN = "";

		final int ROWS = 3;

		String[] strParseResult;
		strParseResult = new String[ROWS];

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
						if (tagname.equalsIgnoreCase("ValidateSIM")) {
							_Error = "";
							_IMSI = "";
							_MSISDN = "";
						}
						break;

					case XmlPullParser.TEXT:
						text = parser.getText();
						break;

					case XmlPullParser.END_TAG:
						if (tagname.equalsIgnoreCase("ValidateSIM")) {
							strParseResult[0] = _Error;
							strParseResult[1] = _IMSI;
							strParseResult[2] = _MSISDN;
						} else if (tagname.equalsIgnoreCase("Error")) {
							_Error = text;
						} else if (tagname.equalsIgnoreCase("IMSI")) {
							_IMSI = text;
						} else if (tagname.equalsIgnoreCase("MSISDN")) {
							_MSISDN = text;
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
		return strParseResult;
	}

	private void sEnableDisable(String strShow){
		//lock unlock
		EditText txtICC = (EditText) findViewById(R.id.txtICC);
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);

		//hide show
		Button validateButton = (Button)findViewById(R.id.validateButton);
		View LinePersonalInfo0 = (View) findViewById(R.id.LinePersonalInfo0);
		View LinePersonalInfo01 = (View) findViewById(R.id.LinePersonalInfo01);
		TextView lblPersonalInfo = (TextView) findViewById(R.id.lblPersonalInfo);
		View LinePersonalInfo = (View) findViewById(R.id.LinePersonalInfo);
		View LinePersonalInfo1 = (View) findViewById(R.id.LinePersonalInfo1);
		TextView lblFirstName = (TextView) findViewById(R.id.lblFirstName);
		EditText txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		TextView lblFatherName = (TextView) findViewById(R.id.lblFatherName);
		EditText txtFatherName = (EditText) findViewById(R.id.txtFatherName);
		TextView lblLastName = (TextView) findViewById(R.id.lblLastName);
		EditText txtLastName = (EditText) findViewById(R.id.txtLastName);
		TextView lblGender = (TextView) findViewById(R.id.lblGender);
		Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		Button btnBirthDatePick = (Button)findViewById(R.id.btnBirthDatePick);
		TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);
		TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
		EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
		TextView lblEducation = (TextView) findViewById(R.id.lblEducation);
		Spinner spinnerEducation = (Spinner) findViewById(R.id.spinnerEducation);

		Button btnCapture1 = (Button)findViewById(R.id.btnCapture1);
		Button btnEmpreinte = (Button)findViewById(R.id.btnEmpreinte);
		fg="0";

		ImageView camera_personal_image = (ImageView) findViewById(R.id.camera_personal_image);
		ImageView camera_personal_finger = (ImageView) findViewById(R.id.fingerimage);

		TextView lblEmptySeparator1 = (TextView) findViewById(R.id.lblEmptySeparator1);
		View LineNationalityInfo0 = (View) findViewById(R.id.LineNationalityInfo0);
		View LineNationalityInfo01 = (View) findViewById(R.id.LineNationalityInfo01);
		TextView lblNationalityInfo = (TextView) findViewById(R.id.lblNationalityInfo);
		View LineNationalityInfo = (View) findViewById(R.id.LineNationalityInfo);
		View LineNationalityInfo1 = (View) findViewById(R.id.LineNationalityInfo1);
		TextView lblNationality = (TextView) findViewById(R.id.lblNationality);
		Spinner spinnerNationality = (Spinner) findViewById(R.id.spinnerNationality);
		TextView lblIdType = (TextView) findViewById(R.id.lblIdType);
		Spinner spinnerIDType = (Spinner) findViewById(R.id.spinnerIDType);
		TextView lblReferenceNbr = (TextView) findViewById(R.id.lblReferenceNbr);
		EditText txtReferenceNbr = (EditText) findViewById(R.id.txtReferenceNbr);

		Button btnCapture2 = (Button)findViewById(R.id.btnCapture2);

		ImageView camera_idcard1_image = (ImageView) findViewById(R.id.camera_idcard1_image);
		TextView lblEmptySeparator2 = (TextView) findViewById(R.id.lblEmptySeparator2);
		View LineResidenceAddress0 = (View) findViewById(R.id.LineResidenceAddress0);
		View LineResidenceAddress01 = (View) findViewById(R.id.LineResidenceAddress01);
		TextView lblResidenceAddressInfo = (TextView) findViewById(R.id.lblResidenceAddressInfo);
		View LineResidenceAddress = (View) findViewById(R.id.LineResidenceAddress);
		View LineResidenceAddress1 = (View) findViewById(R.id.LineResidenceAddress1);
		TextView lblTown = (TextView) findViewById(R.id.lblTown);
		Spinner spinnerTown = (Spinner) findViewById(R.id.spinnerTown);
		TextView lblStreet = (TextView) findViewById(R.id.lblStreet);
		EditText txtStreet = (EditText) findViewById(R.id.txtStreet);
		TextView lblEmptySeparator3 = (TextView) findViewById(R.id.lblEmptySeparator3);
		Button btnUpload = (Button)findViewById(R.id.btnUpload);

		if (strShow.equals("1")){
			txtICC.setEnabled(false);
			spinnerNDC.setEnabled(false);
			txtMSISDN.setEnabled(false);
			validateButton.setVisibility(View.GONE);
			LinePersonalInfo0.setVisibility(View.VISIBLE);
			LinePersonalInfo01.setVisibility(View.VISIBLE);
			lblPersonalInfo.setVisibility(View.VISIBLE);
			LinePersonalInfo.setVisibility(View.VISIBLE);
			LinePersonalInfo1.setVisibility(View.VISIBLE);
			lblFirstName.setVisibility(View.VISIBLE);
			txtFirstName.setVisibility(View.VISIBLE);
			lblFatherName.setVisibility(View.VISIBLE);
			txtFatherName.setVisibility(View.VISIBLE);
			lblLastName.setVisibility(View.VISIBLE);
			txtLastName.setVisibility(View.VISIBLE);
			lblGender.setVisibility(View.VISIBLE);
			spinnerGender.setVisibility(View.VISIBLE);
			btnBirthDatePick.setVisibility(View.VISIBLE);
			lblBirthDateValue.setVisibility(View.VISIBLE);
			lblEmail.setVisibility(View.VISIBLE);
			txtEmail.setVisibility(View.VISIBLE);
			lblEducation.setVisibility(View.VISIBLE);
			spinnerEducation.setVisibility(View.VISIBLE);

			btnCapture1.setVisibility(View.VISIBLE);
			//btnEmpreinte.setVisibility(View.VISIBLE);
            btnEmpreinte.setVisibility(View.GONE);

			camera_personal_image.setVisibility(View.VISIBLE);
			camera_personal_finger.setVisibility(View.VISIBLE);
			camera_personal_finger.setVisibility(View.GONE);

			lblEmptySeparator1.setVisibility(View.VISIBLE);
			LineNationalityInfo0.setVisibility(View.VISIBLE);
			LineNationalityInfo01.setVisibility(View.VISIBLE);
			lblNationalityInfo.setVisibility(View.VISIBLE);
			LineNationalityInfo.setVisibility(View.VISIBLE);
			LineNationalityInfo1.setVisibility(View.VISIBLE);
			lblNationality.setVisibility(View.VISIBLE);
			spinnerNationality.setVisibility(View.VISIBLE);
			lblIdType.setVisibility(View.VISIBLE);
			spinnerIDType.setVisibility(View.VISIBLE);
			lblReferenceNbr.setVisibility(View.VISIBLE);
			txtReferenceNbr.setVisibility(View.VISIBLE);
			btnCapture2.setVisibility(View.VISIBLE);
			camera_idcard1_image.setVisibility(View.VISIBLE);
			lblEmptySeparator2.setVisibility(View.VISIBLE);
			LineResidenceAddress0.setVisibility(View.VISIBLE);
			LineResidenceAddress01.setVisibility(View.VISIBLE);
			lblResidenceAddressInfo.setVisibility(View.VISIBLE);
			LineResidenceAddress.setVisibility(View.VISIBLE);
			LineResidenceAddress1.setVisibility(View.VISIBLE);
			lblTown.setVisibility(View.VISIBLE);
			spinnerTown.setVisibility(View.VISIBLE);
			lblStreet.setVisibility(View.VISIBLE);
			txtStreet.setVisibility(View.VISIBLE);
			lblEmptySeparator3.setVisibility(View.VISIBLE);
			btnUpload.setVisibility(View.VISIBLE);
		}
		else{
			txtICC.setEnabled(true);
			spinnerNDC.setEnabled(true);
			txtMSISDN.setEnabled(true);
			validateButton.setVisibility(View.VISIBLE);
			LinePersonalInfo0.setVisibility(View.GONE);
			LinePersonalInfo01.setVisibility(View.GONE);
			lblPersonalInfo.setVisibility(View.GONE);
			LinePersonalInfo.setVisibility(View.GONE);
			LinePersonalInfo1.setVisibility(View.GONE);
			lblFirstName.setVisibility(View.GONE);
			txtFirstName.setVisibility(View.GONE);
			lblFatherName.setVisibility(View.GONE);
			txtFatherName.setVisibility(View.GONE);
			lblLastName.setVisibility(View.GONE);
			txtLastName.setVisibility(View.GONE);
			lblGender.setVisibility(View.GONE);
			spinnerGender.setVisibility(View.GONE);
			btnBirthDatePick.setVisibility(View.GONE);
			lblBirthDateValue.setVisibility(View.GONE);
			lblEmail.setVisibility(View.GONE);
			txtEmail.setVisibility(View.GONE);
			lblEducation.setVisibility(View.GONE);
			spinnerEducation.setVisibility(View.GONE);

			btnCapture1.setVisibility(View.GONE);
			btnEmpreinte.setVisibility(View.GONE);

			camera_personal_image.setVisibility(View.GONE);
			camera_personal_finger.setVisibility(View.GONE);

			lblEmptySeparator1.setVisibility(View.GONE);
			LineNationalityInfo0.setVisibility(View.GONE);
			LineNationalityInfo01.setVisibility(View.GONE);
			lblNationalityInfo.setVisibility(View.GONE);
			LineNationalityInfo.setVisibility(View.GONE);
			LineNationalityInfo1.setVisibility(View.GONE);
			lblNationality.setVisibility(View.GONE);
			spinnerNationality.setVisibility(View.GONE);
			lblIdType.setVisibility(View.GONE);
			spinnerIDType.setVisibility(View.GONE);
			lblReferenceNbr.setVisibility(View.GONE);
			txtReferenceNbr.setVisibility(View.GONE);
			btnCapture2.setVisibility(View.GONE);
			camera_idcard1_image.setVisibility(View.GONE);
			lblEmptySeparator2.setVisibility(View.GONE);
			LineResidenceAddress0.setVisibility(View.GONE);
			LineResidenceAddress01.setVisibility(View.GONE);
			lblResidenceAddressInfo.setVisibility(View.GONE);
			LineResidenceAddress.setVisibility(View.GONE);
			LineResidenceAddress1.setVisibility(View.GONE);
			lblTown.setVisibility(View.GONE);
			spinnerTown.setVisibility(View.GONE);
			lblStreet.setVisibility(View.GONE);
			txtStreet.setVisibility(View.GONE);
			lblEmptySeparator3.setVisibility(View.GONE);
			btnUpload.setVisibility(View.GONE);
		}
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

	private void sFillCBOTown(){
		try {
			String _CellId = getMyCellId();
            Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);

			//AutoCompleteTextView spinnerIdType = (AutoCompleteTextView) findViewById(R.id.spinnerTown);

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
			//spinnerIdType.setThreshold(1);
		}catch ( Exception e){

			libclass.popMessage("Error occur","Loading City error");
			return;
		}
	}

	private void sFillCBOOccupationCategory(){
		try{
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerOccupationCategory);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.occupationCategory_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}catch ( Exception e){

		libclass.popMessage("Error occur","Loading Occupation error");
	}
	}

	private void sFillCBOOccupationSubCategory(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerSubOccupationCategory);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.subOccupationCategory_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}

	private void sFillCBOOccupationTown(){

		try{
            Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);

			String _CellId = getMyCellId();
            if (_CellId != null && _CellId.trim().length() >0 && !_CellId.equals("")){
			String _Lac = _CellId.substring(0, 1);
			String _Lac2 = _CellId.substring(0, 2);

			//Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerTown);
			if (_Lac.equals("1") || (_Lac2.equals("31"))|| ((_Lac.equals("4")) && (_Lac2.equals("41")))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.Town_array_kinshasa, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			} else if  (_Lac.equals("6") || (_Lac2.equals("36"))|| ((_Lac.equals("4")) && (_Lac2.equals("46")))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.Town_array_Katanga, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			} else if (_Lac.equals("2")  || (_Lac2.equals("32"))) {
				ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.Town_array_congo, android.R.layout.simple_spinner_item);
				adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerIdType.setAdapter(adapterIdType);
			}

        }else{
            ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this, R.array.Town_array_town, android.R.layout.simple_spinner_item);
            adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerIdType.setAdapter(adapterIdType);
        }

		}catch (Exception e){


		}

	}

	private void sFillCBONDC(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerNDC);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.NDC_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}
	public void CapturePersonalPhoto(View V){
		try{
			mPersonalPhoto = (ImageView) findViewById(R.id.camera_personal_image);
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, "New Personal Picture");
			values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
			PersonalPhotoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, PersonalPhotoUri);
			startActivityForResult(intent, CAMERA_PIC_REQUEST_PersonalPhoto);
		}catch (Exception e){
 			Log.e("capture ID Card",e.getMessage());
			libclass.popMessage("Error occur","capture ID Card");
           return;
		}

	}
	public void CaptureCaptureIdCardSide1(View V){
		try {
		mIdCardSide1 = (ImageView) findViewById(R.id.camera_idcard1_image);
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "New Side1 Picture");
		values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
		Side1Uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Side1Uri);
		startActivityForResult(intent, CAMERA_PIC_REQUEST_IdCardSide1);
	}catch (Exception e){
		Log.e("capture ID Card",e.getMessage());
			libclass.popMessage("Error occur","capture ID Card");
        return;
	}
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

			 DialogFragment newFragment = new DatePickerFragment();
			 newFragment.show(getFragmentManager(), "datePicker");
		}catch (Exception  ex){
			///Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
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

		public  void onDateSet(DatePicker view, int year, int month, int day) {
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
			newregistrationactivity activity=(newregistrationactivity)getActivity();
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

					//if (intNbrOfFaces == 1) {
					intNbrRequestedFace = 1;
					mPersonalPhoto.setImageBitmap(bitmap);
					thumbnail = bitmap;
					//3
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					thumbnail.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
					//3.5 prepare the image to be uploaded
					byte[] bytedata = bytes.toByteArray();
					strPersonalPhoto = base64.encodeBytes(bytedata);
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				TextView lblError = (TextView) findViewById(R.id.lblError);
				lblError.setText("CAMERA_PIC_REQUEST : " +requestCode);
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
			showAlertDialog(newregistrationactivity.this, "No Internet Connection",
					"You are not connected to the internet.", false);
			return;
		}

		String _HTTP;
		String _IMSI;
		EditText txtICC = (EditText) findViewById(R.id.txtICC);
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		if (_Barcode.length() < 12) {
			txtICC.setText("");
			spinnerNDC.setSelection(0);
			txtMSISDN.setText("");
			return;
		} else {
			  String prefix="63090";
			_IMSI = prefix+_Barcode.substring(1, 3)+_Barcode.substring(4, 12);
			//String _IMSI1=_Barcode.substring(4, 12);
		//	_HTTP = "http://87.238.116.200/SOP_XML_Query_SIM_Barcode.aspx?IMSI=" + _IMSI;
			_HTTP = baseUrl+ "SOP_XML_Query_SIM_Barcode.aspx?IMSI=" + _IMSI;
			txtICC.setText(_IMSI);
			////new DownloadAndParseXMLTask().execute(_HTTP);  // he toke many times
		}

	}

	public class DownloadAndParseXMLTask extends AsyncTask<String, Void, String[]>
	{
		protected String[] doInBackground(String... urls)
		{
			return parse(urls[0]);
		}
		@Override
		protected void onPostExecute(String[] result)
		{
			Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);

			if (result[0].equals("090")){
				spinnerNDC.setSelection(0);
			} else if(result[0].equals("091")){
				spinnerNDC.setSelection(1);
			} /*else if (result[0].equals("077")){
					spinnerNDC.setSelection(1);
				} else if (result[0].equals("088")){
					spinnerNDC.setSelection(2);
				} else if (result[0].equals("099")){
					spinnerNDC.setSelection(3);
				}*/


			EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
			if (result[1].equals("")){
				txtMSISDN.setText("");
				spinnerNDC.setSelection(0);
				//txtMSISDN.setEnabled(true);
				//spinnerNDC.setEnabled(true);

			} else {
				txtMSISDN.setText(result[1]);
				//txtMSISDN.setEnabled(false);
				//spinnerNDC.setEnabled(false);
			}

			//txtMSISDN.setText(result[1]);


		}
	}


	public String[] parse(String strURL)
	{
		String _NDC = "";
		String _MSISDN = "";

		String[] strParseResult;
		strParseResult = new String[3];

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
							_NDC = "";
							_MSISDN = "";
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
						if (tagname.equalsIgnoreCase("NDC")) {
							_NDC = text;
						} else if (tagname.equalsIgnoreCase("MSISDN")) {
							_MSISDN = text;
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

		strParseResult[0] = _NDC;
		strParseResult[1] = _MSISDN;

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

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		@SuppressWarnings("deprecation")
		android.database.Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_registration, menu);
		return true;
	}

	public void selectImageFromGallery(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
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

	public void onUploadClick(View view) {
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
			showAlertDialog(newregistrationactivity.this, "No Internet Connection",
					"You are not connected to the internet.", false);
			return;
		}

		if (blnCheckDataBeforeUpload() == true){
			new ImageUploadTask().execute();
		}
	}

	public boolean blnCheckDataBeforeUpload() {
		strFinalXML = "";
		TextView lblError = (TextView) findViewById(R.id.lblError);
		lblError.setText("");

		//check if face detected in personal photo
        if(strIdCardSide1.trim().equals("")) {
			Toast.makeText(this, "Veuillez prendre la photo de la carte Id.", Toast.LENGTH_LONG).show();
            lblError.setText("Veuillez prendre la photo de la carte Id.");
			return false;
        }

		strFinalXML = strFinalXML+"<registration>";
		strFinalXML = strFinalXML+"<personalinfo>";
		//put here the personal info

		//get and check first Name
		EditText txtFirstName = (EditText) findViewById(R.id.txtFirstName);
		String _FirstName = txtFirstName.getText().toString();

		if (_FirstName.equals("")){
			Toast.makeText(this, "veuillez saisir le  nom.", Toast.LENGTH_LONG).show();
            lblError.setText("veuillez saisir le  nom.");
			return false;
		}
		else
		{
			if (_FirstName.length() < 2){
				Toast.makeText(this, "veuillez saisir le  nom.", Toast.LENGTH_LONG).show();
	            lblError.setText("veuillez saisir le  nom.");
				return false;
			}
		}
		//get and check first Name
		EditText txtFatherName = (EditText) findViewById(R.id.txtFatherName);
		String _FatherName = txtFatherName.getText().toString();

		//get and check the last name
		EditText txtLastName = (EditText) findViewById(R.id.txtLastName);
		String _LastName = txtLastName.getText().toString();

		if (_LastName.equals("")){
			Toast.makeText(this, "Veuillez saisir le Postnom.", Toast.LENGTH_LONG).show();
            lblError.setText("Veuillez saisir le Postnom.");
			txtLastName.findFocus();
			return false;
		}
		else
		{
			if (_LastName.length() < 2){
				Toast.makeText(this, "Family Name is not valid.", Toast.LENGTH_LONG).show();
	            lblError.setText("Family Name is not valid.");
				txtLastName.findFocus();
				return false;
			}
		}

		//get gender
		Spinner spinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		String _Gender = spinnerGender.getSelectedItem().toString();

		if (_Gender.equals("")){
			Toast.makeText(this, "Veuillez choisir le sexe.", Toast.LENGTH_LONG).show();
			lblError.setText("Veuillez choisir le sexe.");
			spinnerGender.findFocus();
			return false;
		}

		//get birth Date
		TextView lblBirthDateValue = (TextView) findViewById(R.id.lblBirthDateValue);
		String _BirthDate = lblBirthDateValue.getText().toString();

		String[]tBirthday=_BirthDate.split("/");
		int iYear= Calendar.getInstance().get(Calendar.YEAR);


		if (_BirthDate.equals("")){
			Toast.makeText(this, "veuillez entrer la date de Naissance valide.", Toast.LENGTH_LONG).show();
            lblError.setText("veuillez entrer la date de Naissance valide.");
			lblBirthDateValue.findFocus();
			return false;
		}else
		if (tBirthday.length >0 && (iYear-Integer.parseInt(tBirthday[2]) < 17)){
			Toast.makeText(this, "La date de Naissance entrée est invalide.", Toast.LENGTH_LONG).show();
			lblError.setText("La date de Naissance entrée est invalide.");
			lblBirthDateValue.findFocus();
			return false;
		}


		//get email
		EditText txtEmail = (EditText) findViewById(R.id.txtEmail);
		String _Email = txtEmail.getText().toString();

		//get education
		Spinner spinnerEducation = (Spinner) findViewById(R.id.spinnerEducation);
		String _Education = spinnerEducation.getSelectedItem().toString();

		strFinalXML = strFinalXML+"<firstname>"+_FirstName+"</firstname>";
		strFinalXML = strFinalXML+"<fathername>"+_FatherName+"</fathername>";
		strFinalXML = strFinalXML+"<familyname>"+_LastName+"</familyname>";
		strFinalXML = strFinalXML+"<gender>"+_Gender+"</gender>";
		strFinalXML = strFinalXML+"<birthdate>"+_BirthDate+"</birthdate>";
		strFinalXML = strFinalXML+"<email>"+_Email+"</email>";
		strFinalXML = strFinalXML+"<education>"+_Education+"</education>";

		strFinalXML = strFinalXML+"</personalinfo>";

		strFinalXML = strFinalXML+"<nationalityinfo>";
		//put here the nationality info

		//get nationality
		Spinner spinnerNationality = (Spinner) findViewById(R.id.spinnerNationality);
		String _Nationality = spinnerNationality.getSelectedItem().toString();

		//get id type
		Spinner spinnerIDType = (Spinner) findViewById(R.id.spinnerIDType);
		String _IDType = spinnerIDType.getSelectedItem().toString();

		//get reference number
		EditText txtReferenceNbr = (EditText) findViewById(R.id.txtReferenceNbr);
		String _ReferenceNbr = txtReferenceNbr.getText().toString();
		/*
		if (_ReferenceNbr.equals("")){
			Toast.makeText(this, "Reference Nbr cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setText("Reference Nbr cannot be empty.");
			return false;
		}*/

		strFinalXML = strFinalXML+"<nationality>"+_Nationality+"</nationality>";
		strFinalXML = strFinalXML+"<idtype>"+_IDType+"</idtype>";
		strFinalXML = strFinalXML+"<referencenbr>"+_ReferenceNbr+"</referencenbr>";
		strFinalXML = strFinalXML+"</nationalityinfo>";

		strFinalXML = strFinalXML+"<residenceinfo>";
		//put here the residence info

		//get residence town
		Spinner spinnerTown = (Spinner) findViewById(R.id.spinnerTown);
		String _residenceTown = spinnerTown.getSelectedItem().toString();

		//get residence street
		EditText txtStreet = (EditText) findViewById(R.id.txtStreet);
		String _residenceStreet = txtStreet.getText().toString();
		/*
		if (_residenceStreet.equals("")){
			Toast.makeText(this, "Residence street cannot be empty.", Toast.LENGTH_LONG).show();
            lblError.setText("Residence street cannot be empty.");
			return false;
		}*/

		strFinalXML = strFinalXML+"<residencetown>"+_residenceTown+"</residencetown>";
		strFinalXML = strFinalXML+"<residencestreet>"+_residenceStreet+"</residencestreet>";
		strFinalXML = strFinalXML+"</residenceinfo>";

		strFinalXML = strFinalXML+"<occupationinfo>";
		//put here the occupation info
		//get category
		Spinner spinnerOccupationCategory = (Spinner) findViewById(R.id.spinnerOccupationCategory);
		String _category = spinnerOccupationCategory.getSelectedItem().toString();

		//get sub category
		Spinner spinnerSubOccupationCategory = (Spinner) findViewById(R.id.spinnerSubOccupationCategory);
		String _subcategory = spinnerSubOccupationCategory.getSelectedItem().toString();

		//get employer
		EditText txtEmployer = (EditText) findViewById(R.id.txtEmployer);
		String _Employer = txtEmployer.getText().toString();

		//get occupation town
		//Spinner spinnerOccupationTown = (Spinner) findViewById(R.id.spinnerOccupationTown);
		String _OccupationTown = " ";

		//get occupation street
		EditText txtOccupationStreet = (EditText) findViewById(R.id.txtOccupationStreet);
		String _OccupationStreet = txtOccupationStreet.getText().toString();

		strFinalXML = strFinalXML+"<category>"+_category+"</category>";
		strFinalXML = strFinalXML+"<subcategory>"+_subcategory+"</subcategory>";
		strFinalXML = strFinalXML+"<employer>"+_Employer+"</employer>";
		strFinalXML = strFinalXML+"<occupationtown>"+_OccupationTown+"</occupationtown>";
		strFinalXML = strFinalXML+"<occupationstreet>"+_OccupationStreet+"</occupationstreet>";

		strFinalXML = strFinalXML+"</occupationinfo>";

		strFinalXML = strFinalXML+"<siminfo>";
		//put here the sim info
		//Get the NDC
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		String _NDC = spinnerNDC.getSelectedItem().toString();

		//get & check MSISDN
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
		String _MSISDN = txtMSISDN.getText().toString();
		if (_MSISDN.length() != 7){
			Toast.makeText(this, "The number you are trying to register doesnt exist.", Toast.LENGTH_LONG).show();
			lblError.setText("The number you are trying to register doesnt exist.");
			txtMSISDN.findFocus();
			return false;
		}
		//get & check ICC
		EditText txtICC = (EditText) findViewById(R.id.txtICC);
		String _ICC = txtICC.getText().toString();
		//if (_ICC.length() < 15){
		//	Toast.makeText(this, "The ICC you are trying to register doesnt exist.", Toast.LENGTH_LONG).show();
		//    lblError.setText("The ICC you are trying to register doesnt exist.");
		//	return false;
		//}
		strFinalXML = strFinalXML+"<ndc>"+_NDC+"</ndc>";
		strFinalXML = strFinalXML+"<msisdn>"+_MSISDN+"</msisdn>";
		strFinalXML = strFinalXML+"<icc>"+_ICC+"</icc>";
		strFinalXML = strFinalXML+"</siminfo>";

		strFinalXML = strFinalXML+"<userinfo>";
		//get Geo Location
		//get Location
		// create class object
		strGeoLocation = " ";

		/*
          gps = new GPSTracker(newregistrationactivity.this);
          strGeoLocation = "";
          // check if GPS enabled
          if(gps.canGetLocation()){

              double latitude = gps.getLatitude();
              double longitude = gps.getLongitude();



              strGeoLocation = "-nLat:" + latitude + "-nLong:" + longitude;
              // \n is for new line
              //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
          }else{
              // can't get location
              // GPS or Network is not enabled
              // Ask user to enable GPS/network in settings
              gps.showSettingsAlert();
  			  Toast.makeText(this, "GPS is not enabled.", Toast.LENGTH_LONG).show();
              lblError.setText("GPS is not enabled.");
  			  return false;
          }

		*/

		//put here the user info
		String _CellId = getMyCellId();
		_CellId = _CellId + strGeoLocation;
		String _user = getValueFromDB("_us_Login");
		strFinalXML = strFinalXML+"<user>"+_user+"</user>";
		strFinalXML = strFinalXML+"<cellid>"+_CellId+"</cellid>";
		strFinalXML = strFinalXML+"</userinfo>";

		strFinalXML = strFinalXML+"<capturedphoto>";
		//put here the image
		strFinalXML = strFinalXML+"<personalphoto>"+strPersonalPhoto+"</personalphoto>";
		strFinalXML = strFinalXML+"<personalEmpreinte>"+strPersonalEmpreinte+"</personalEmpreinte>";
		strFinalXML = strFinalXML+"<idcardside1>"+strIdCardSide1+"</idcardside1>";
		strFinalXML = strFinalXML+"<idcardside2>"+strIdCardSide2+"</idcardside2>";
		strFinalXML = strFinalXML+"</capturedphoto>";

		strFinalXML = strFinalXML + "</registration>";
		//TextView lblError = (TextView) findViewById(R.id.lblError);
		//lblError.setText(strFinalXML);
		return true;
	}

	public String getValueFromDB(String strValue){
		String label = "";
		String selectQuery = "SELECT Value FROM securityEntry WHERE Title='"+strValue+"'";
		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(newregistrationactivity.this);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		android.database.Cursor cursor = db.rawQuery(selectQuery, null);

		if (null != cursor && cursor.moveToFirst()) {
			label = cursor.getString(cursor.getColumnIndex("Value"));
		}

		return label;

	}
	public String getMyCellId()
	{
		final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			//Toast.makeText(this,"permission ok ACCESS_FINE_LOCATION",Toast.LENGTH_LONG).show();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
			return "";
		}

		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

			//Toast.makeText(this,"permission ok READ_PHONE_STATE",Toast.LENGTH_LONG).show();
		} else {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
			return "";
		}


		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
			if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
			{
				final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
				if (location != null)
				{
					return location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
				}
				else
				{

					ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
					return "";
				}
			}
			else
			{
				return "";
			}
		}
		return "";

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
	class ImageUploadTask extends AsyncTask<Void, Void, String> {
		//private String webAddressToPost = baseUrl+ "SOP_XML_DIAS_Insert_Registration.aspx";
	   private String webAddressToPost = baseUrl+ "SOP_XML_ORA_Insert_Registration.aspx";
		// private ProgressDialog dialog;
		private ProgressDialog dialog = new ProgressDialog(newregistrationactivity.this);

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
try {
			dialog.dismiss();

			String res = parseXmltoString(result);
			String[] sResponse = {""};
			if (res.contains(";")) {
				sResponse = res.split(";");
			} else {
				sResponse[0] = res.toString();
				 sResponse[1]=res.toString();
			}

			//if (result.equals("<Reply><MESSAGE>Successful</MESSAGE></Reply>")){
			if (result.toLowerCase().contains("successful")) {
				if (sResponse.length >= 1) {
					if (sResponse[1].toLowerCase().contains("successful")) {
						Toast.makeText(getApplicationContext(), "Registered successfully.", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(getApplicationContext(), sResponse[1].toString(), Toast.LENGTH_LONG).show();
					}

				}

				String strMessage = "";
				Intent intent = new Intent(getApplicationContext(), secondactivity.class);
				intent.putExtra(EXTRA_MESSAGE, strMessage);
				startActivity(intent);
				//clean PersonalPhoto
				if (!strPersonalPhotoURL.equals("")) {
					//File file1 = new File(strPersonalPhotoURL);
					//file1.delete();
					getContentResolver().delete(PersonalPhotoUri, null, null);
				}
				//clean Side1
				if (!strSide1URL.equals("")) {
					//File file2 = new File(strSide1URL);
					//file2.delete();
					getContentResolver().delete(Side1Uri, null, null);
				}
				//clean Side2
				if (!strSide2URL.equals("")) {
					//File file3 = new File(strSide2URL);
					//file3.delete();
					getContentResolver().delete(Side2Uri, null, null);
				}
				if (!fg.equals("0")) {
					//File file3 = new File(strSide2URL);
					//file3.delete();
					fg = "0";
				}
			} else {
				TextView lblError = (TextView) findViewById(R.id.lblError);
				Toast.makeText(getApplicationContext(), "Registration failed.", Toast.LENGTH_LONG).show();
				lblError.setText("Registration failed. Contact administrator.");
			}


			//if (mg
		} catch (Exception e){
	        TextView lblError = (TextView) findViewById(R.id.lblError);
			Toast.makeText(getApplicationContext(), "Registration failed:" + e.getMessage(), Toast.LENGTH_LONG).show();
			lblError.setText("Registration failed:" + e.getMessage());
			return;

        }
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
					FeedEntry.COLUMN_NAME_CreateCellId + TEXT_TYPE + COMMA_SEP +
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
	public String parseXmltoString(String strXML) {
		Document doc = null;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		String resultstrText = "";
		try {

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			InputSource ipsource = new InputSource();
			ipsource.setCharacterStream(new StringReader(strXML));
			doc = dBuilder.parse(ipsource);
			Element element=doc.getDocumentElement();
			element.normalize();

			NodeList nList = doc.getElementsByTagName("MESSAGE");
			for (int i=0; i<nList.getLength(); i++) {
				Node node = nList.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element2 = (Element) node;

					String message =libclass.getValue("MESSAGE1", element2);
					String message2 =libclass.getValue("MESSAGE2", element2);
                     resultstrText =  message + ";" + message2;

				}
			}

			return resultstrText;

		} catch (Exception e) {
			Log.d("Error occur ",e.getMessage());
			 e.printStackTrace()  ;
			return "unsucess request.";
		}
	}
}
