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
import sl.Africell.afr_sl_registration.loginactivity.FeedReaderContract.FeedEntry;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class simcreationactivity extends Activity {
    private static final int SCAN_SIM_BAR_CODE = 49374;
    private String text;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sim_creation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		sFillCBONDC();
		EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN); 
		txtMSISDN.setEnabled(false);
		Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
		spinnerNDC.setEnabled(false);
		Button btnCheckMSISDNAvailability = (Button) findViewById(R.id.btnCheckMSISDNAvailability);
		btnCheckMSISDNAvailability.setVisibility(View.GONE);
		Button btnCreateMSISDN = (Button) findViewById(R.id.btnCreateMSISDN);
		btnCreateMSISDN.setVisibility(View.GONE);		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}

	private void sFillCBONDC(){
		Spinner spinnerIdType = (Spinner) findViewById(R.id.spinnerNDC);
		ArrayAdapter<CharSequence> adapterIdType = ArrayAdapter.createFromResource(this,R.array.NDC_array, android.R.layout.simple_spinner_item);
		adapterIdType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerIdType.setAdapter(adapterIdType);
	}			
	
	public void onScanBarcodeClick(View V){
		IntentIntegrator scanIntegrator = new IntentIntegrator(this);
		scanIntegrator.initiateScan();
	}	
		
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  
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
	            showAlertDialog(simcreationactivity.this, "No Internet Connection",
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
			 _IMSI = "6190501"+_Barcode.substring(4, 12);
			 _HTTP = "http://register.africell.cd/SOP_XML_Query_SIM_Barcode.aspx?IMSI=" + _IMSI;
			 txtICC.setText(_IMSI);
			 new DownloadAndParseXMLTask().execute(_HTTP);	 	 
		 }
		 
	 }
	 
		public class DownloadAndParseXMLTask extends AsyncTask<String, Void, String[]> 
		{
			 // private ProgressDialog dialog;
			 private ProgressDialog dialog = new ProgressDialog(simcreationactivity.this);

			 @Override
			 protected void onPreExecute() {
			  dialog.setMessage("Checking SIM...");
			  dialog.show();
			 }
			 
			protected String[] doInBackground(String... urls) 
				{
				return parse(urls[0]);
				}
			@Override
			protected void onPostExecute(String[] result) 
				{
				dialog.dismiss();
				Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
				
				if (result[0].equals("030")){
					spinnerNDC.setSelection(0);
				} else if (result[0].equals("077")){
					spinnerNDC.setSelection(1);
				} else if (result[0].equals("088")){
					spinnerNDC.setSelection(2);
				} else if (result[0].equals("099")){
					spinnerNDC.setSelection(3);
				}
				
				
				EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN); 
	            TextView lblError = (TextView) findViewById(R.id.lblError); 
	            Button btnCheckMSISDNAvailability = (Button) findViewById(R.id.btnCheckMSISDNAvailability);
	            Button btnCreateMSISDN = (Button) findViewById(R.id.btnCreateMSISDN);
	            TextView lblInfo = (TextView) findViewById(R.id.lblInfo);
	            lblInfo.setText("");
	            btnCheckMSISDNAvailability.setVisibility(View.GONE);
	            btnCreateMSISDN.setVisibility(View.GONE);
	            
	            //check SIM authentication
				if (!result[2].equals("")){
		            //check if SIM is blank
					if (result[1].equals("")){
						txtMSISDN.setText("");
						lblError.setText("");
						spinnerNDC.setSelection(0);
						txtMSISDN.setEnabled(true);
						spinnerNDC.setEnabled(true);
						btnCheckMSISDNAvailability.setVisibility(View.VISIBLE);
					} else {
						lblError.setText("SIM already in use, make sure you are using a blank SIM.");
						txtMSISDN.setText(result[1]);										
						txtMSISDN.setEnabled(false);
						spinnerNDC.setEnabled(false);	
						btnCheckMSISDNAvailability.setVisibility(View.GONE);
						return;
					}
				} else {
					lblError.setText("The SIM you are trying to use is not authenticated.");
					txtMSISDN.setText(result[1]);										
					txtMSISDN.setEnabled(false);
					spinnerNDC.setEnabled(false);	
					btnCheckMSISDNAvailability.setVisibility(View.GONE);
					return;
				}	            

			}
		}
		
		public class DownloadAndParseXMLValidateMSISDNTask extends AsyncTask<String, Void, String[]> 
		{
			
			 // private ProgressDialog dialog;
			 private ProgressDialog dialog = new ProgressDialog(simcreationactivity.this);

			 @Override
			 protected void onPreExecute() {
			  dialog.setMessage("Checking MSISDN...");
			  dialog.show();
			 }
			 
			protected String[] doInBackground(String... urls) 
				{
				return parse(urls[0]);
				}
			@Override
			protected void onPostExecute(String[] result) 
				{
				dialog.dismiss();
				EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN); 
				Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
				Button btnCheckMSISDNAvailability = (Button) findViewById(R.id.btnCheckMSISDNAvailability);
				Button btnCreateMSISDN = (Button) findViewById(R.id.btnCreateMSISDN);
				TextView lblError = (TextView) findViewById(R.id.lblError); 	
				TextView lblInfo = (TextView) findViewById(R.id.lblInfo);
				
				if (result[2].equals("") && (result[3].equals(""))){
					if(!result[4].equals("")) {
						//reserved MSISDN
						txtMSISDN.setEnabled(true);
						spinnerNDC.setEnabled(true);
						btnCheckMSISDNAvailability.setVisibility(View.VISIBLE);
						btnCreateMSISDN.setVisibility(View.GONE);
						lblError.setText("The number you are trying to create is reserved for: " + result[4] );
						
					} else {
						//available MSISDN
						txtMSISDN.setEnabled(false);
						spinnerNDC.setEnabled(false);
						btnCheckMSISDNAvailability.setVisibility(View.GONE);
						btnCreateMSISDN.setVisibility(View.VISIBLE);
						lblError.setText("");
						lblInfo.setText("Category: " + result[5]);	
					}
				} else {
					//used MSISDN 
					txtMSISDN.setEnabled(true);
					spinnerNDC.setEnabled(true);
					btnCheckMSISDNAvailability.setVisibility(View.VISIBLE);
					btnCreateMSISDN.setVisibility(View.GONE);
					lblError.setText("The number you are trying to create is used.");
				}
			}
		}		

		public class DownloadAndParseXMLCreateHLRTask extends AsyncTask<String, Void, String[]> 
		{
			
			 // private ProgressDialog dialog;
			 private ProgressDialog dialog = new ProgressDialog(simcreationactivity.this);

			 @Override
			 protected void onPreExecute() {
			  dialog.setMessage("Creating...");
			  dialog.show();
			 }
			 
			protected String[] doInBackground(String... urls) 
				{
				return parseCreateHLR(urls[0]);
				}
			@Override
			protected void onPostExecute(String[] result) 
				{
				dialog.dismiss();
				Button btnCreateMSISDN = (Button) findViewById(R.id.btnCreateMSISDN);
				TextView lblError = (TextView) findViewById(R.id.lblError);
				if(result[0].equals("Successful")){
					lblError.setText("Created successfully.");
					btnCreateMSISDN.setVisibility(View.GONE);
				} else {
					lblError.setText("Creation failed.");
				}
					
			}
		}		
		
	    public String[] parseCreateHLR(String strURL) 
	    {
		    String _RESULT = "";
		    
	    	String[] strParseResult;
	    	strParseResult = new String[1];
	    	
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
	                    if (tagname.equalsIgnoreCase("HLRCREATION")) {
	                        // create a new instance of employee
	                    	_RESULT = "";
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
	                   if (tagname.equalsIgnoreCase("RESULT")) {
	                	   _RESULT = text;                     		
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
	        
	        strParseResult[0] = _RESULT;

	        return strParseResult;
	    }		
		
		
	    public String[] parse(String strURL) 
	    {
		    String _KI = "";
	    	String _NDC = "";
		    String _MSISDN = "";
		    String _IMSI = "";
		    String _RESERVED = "";
		    String _CATEGORY = "";
		    
	    	String[] strParseResult;
	    	strParseResult = new String[6];
	    	
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
	                    if (tagname.equalsIgnoreCase("Subscriber")) {
	                        // create a new instance of employee
	                    	_KI = "";
	            		    _NDC = "";
	            		    _MSISDN = "";
	            		    _IMSI = "";
	            		    _RESERVED = "";
	            		    _CATEGORY = "";
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
	                    } else if (tagname.equalsIgnoreCase("KI")) {
	                    	_KI = text;                     		
	                    } else if (tagname.equalsIgnoreCase("IMSI")) {
	                    	_IMSI = text;                     		
	                    } else if (tagname.equalsIgnoreCase("RESERVED")) {
	                    	_RESERVED = text;                     		
	                    } else if (tagname.equalsIgnoreCase("CATEGORY")) {
	                    	_CATEGORY = text;                     		
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
	        strParseResult[2] = _KI;
	        strParseResult[3] = _IMSI;
	        strParseResult[4] = _RESERVED;
	        strParseResult[5] = _CATEGORY;
	        
	        return strParseResult;
	    }
	    
		public void onCheckMSISDNAvailabilityClick(View V){
			 String _HTTP;
			 Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
			 EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
			 String strMSISDN = txtMSISDN.getText().toString();
			 String strNDC = spinnerNDC.getSelectedItem().toString();
			 strNDC = strNDC.substring(1, 3);
			 _HTTP = "http://register.africell.cd/SOP_XML_Query_SIM_Barcode.aspx?IMSI=&NDC="+strNDC+"&MSISDN="+strMSISDN;
			 new DownloadAndParseXMLValidateMSISDNTask().execute(_HTTP);	
		}		  
		
		public void onCreateMSISDNClick(View V){
			 String _HTTP;
			 EditText txtIMSI = (EditText) findViewById(R.id.txtICC);
			 Spinner spinnerNDC = (Spinner) findViewById(R.id.spinnerNDC);
			 EditText txtMSISDN = (EditText) findViewById(R.id.txtMSISDN);
			 String strIMSI = txtIMSI.getText().toString();
			 String strMSISDN = txtMSISDN.getText().toString();
			 String strNDC = spinnerNDC.getSelectedItem().toString();
			 strNDC = strNDC.substring(1, 3);
			 String _user = getValueFromDB("_us_Login");
			 
			 _HTTP = "http://register.africell.cd/SOP_XML_Create_HLR.aspx?IMSI="+strIMSI+"&NDC="+strNDC+"&MSISDN="+strMSISDN+"&User=A-"+_user;
			 new DownloadAndParseXMLCreateHLRTask().execute(_HTTP);	
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
	
	    
	    
		public String getValueFromDB(String strValue){
	        String label = "";
	        String selectQuery = "SELECT Value FROM securityEntry WHERE Title='"+strValue+"'"; 
		    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(simcreationactivity.this);
		    SQLiteDatabase db = mDbHelper.getReadableDatabase();
		    
	        android.database.Cursor cursor = db.rawQuery(selectQuery, null);
	        
	        if (null != cursor && cursor.moveToFirst()) {
	            label = cursor.getString(cursor.getColumnIndex("Value"));
	        }

	        return label;

	    }	    
	    
		///////SQL procedures
		public final class FeedReaderContract {
		    // To prevent someone from accidentally instantiating the contract class,
		    // give it an empty constructor.
		    public FeedReaderContract() {}

		    /* Inner class that defines the table contents */
		    public  abstract class FeedEntry implements BaseColumns {
				public static final String TABLE_NAME = "securityEntry";
		        public static final String COLUMN_NAME_ENTRY_ID = "EntryId";
		        public static final String COLUMN_NAME_TITLE = "Title";
		        public static final String COLUMN_NAME_VALUE = "Value";
		        
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

		private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
		
		
		public class FeedReaderDbHelper extends SQLiteOpenHelper {
		    // If you change the database schema, you must increment the database version.
		    public static final int DATABASE_VERSION = 1;
		    public static final String DATABASE_NAME = "AfrSLRegistration.db";

		    public FeedReaderDbHelper(Context context) {
		        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		    }
		    public void onCreate(SQLiteDatabase db) {
		        db.execSQL(SQL_CREATE_ENTRIES);
		    }
		    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		        // This database is only a cache for online data, so its upgrade policy is
		        // to simply to discard the data and start over
		        db.execSQL(SQL_DELETE_ENTRIES);
		        onCreate(db);
		    }
		    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		        onUpgrade(db, oldVersion, newVersion);
		    }

		}	    
	
}

