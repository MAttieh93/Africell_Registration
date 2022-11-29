package sl.Africell.afr_sl_registration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

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
import android.widget.Toast;
import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.Newregistrationtableactivity.DownloadAndParseValidateTableSIMXMLTask;
import sl.Africell.afr_sl_registration.loginactivity.FeedReaderContract.FeedEntry;

import static sl.Africell.afr_sl_registration.apiUrl.baseUrl;


public class tablessupplysactivity extends Activity {
	private String text;
	String _TableAgent = "";
	String _User = "";
	String _FromIMSI = "";
	String _TillIMSI = "";
	String HTTP_check_user_limit = "";
	String HTTP_insert_supply = "";
	
	EditText txtTableAgent, txtFromICC, txtTillICC;
	Button validateButton, supplyButton;
	TextView lblFromICC, lblTillICC, lblError;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tables_supply);
		
		//fill spinner table agents
		//String strHTTP_table_agents = "http://87.238.116.200/SOP_XML_GetTableUsers.aspx";
		//new DownloadAndParseTableAgentXMLTask().execute(strHTTP_table_agents); 	
		
		// get reference to the views
		txtTableAgent = (EditText) findViewById(R.id.txtTableAgent);
		txtFromICC = (EditText) findViewById(R.id.txtFromICC);
		txtTillICC = (EditText) findViewById(R.id.txtTillICC);
		lblFromICC = (TextView) findViewById(R.id.lblFromICC);
		lblTillICC = (TextView) findViewById(R.id.lblTillICC);
		validateButton= (Button) findViewById(R.id.validateButton);
		supplyButton= (Button) findViewById(R.id.supplyButton);
		
		lblError= (TextView) findViewById(R.id.lblError);
		showHide(false);
	}

	void showHide(boolean blnShow){
		if (blnShow) {
			txtTableAgent.setEnabled(false);
			validateButton.setVisibility(View.GONE);
			lblFromICC.setVisibility(View.VISIBLE);
			txtFromICC.setVisibility(View.VISIBLE);
			lblTillICC.setVisibility(View.VISIBLE);
			txtTillICC.setVisibility(View.VISIBLE);
			supplyButton.setVisibility(View.VISIBLE);
		} else {
			txtTableAgent.setEnabled(true);
			validateButton.setVisibility(View.VISIBLE);
			lblFromICC.setVisibility(View.GONE);
			txtFromICC.setVisibility(View.GONE);
			lblTillICC.setVisibility(View.GONE);
			txtTillICC.setVisibility(View.GONE);
			supplyButton.setVisibility(View.GONE);
		}

	}
	
	public void onValidateClick(View view) {
		String TableAgent = txtTableAgent.getText().toString();
		if (TableAgent.equals("")){
			lblError.setText("Table agent cannot be empty.");	
			return;
		}
		String HTTP_ValidateTableUser =baseUrl+ "SOP_XML_validate_table_user.aspx?TableUser="+TableAgent;
		new DownloadAndParseValidateTableUSERXMLTask().execute(HTTP_ValidateTableUser); 
		
	}
	
	public class DownloadAndParseValidateTableUSERXMLTask extends AsyncTask<String, Void, String[]> 
	{
		private ProgressDialog dialog = new ProgressDialog(tablessupplysactivity.this);
		 @Override
		 protected void onPreExecute() {			 
			  dialog.setMessage("Please wait while validating");
			  dialog.show();
		 }
		protected String[] doInBackground(String... urls) 
			{
			return parseValidateTableUSER(urls[0]);
			}
		@Override
		protected void onPostExecute(String[] result) 
		{					
			if (result[0].trim().equals("")){
				if (result[1].trim().equals("true")){
					lblError.setText("");
					showHide(true);
				} else {
					lblError.setText("Table agent is not valid.");
					showHide(false);
				}

			}else {
				lblError.setText(result[0].trim().toString());
				showHide(false);
			}
			
			dialog.dismiss();
		}
	}	

    public String[] parseValidateTableUSER(String strURL) 
    {
    	String _Error = "";
    	String _valid = "";
    	
    	final int ROWS = 2;
    	
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
                    if (tagname.equalsIgnoreCase("ValidateTableUSER")) {
                    	_Error = "";
                    	_valid = "";
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("ValidateTableUSER")) {
                    	strParseResult[0] = _Error;
                    	strParseResult[1] = _valid;
                    } else if (tagname.equalsIgnoreCase("Error")) {
                    	_Error = text;                     		
                    } else if (tagname.equalsIgnoreCase("valid")) {
                    	_valid = text;                     		
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}
	
	public void onSupplyClick(View view) {		
		String _FromICC = "";
		String _TillICC = "";
		
		//check Internet connection
		boolean isConnected = isNetworkAvailable();
    	if (isConnected)
    	{
            lblError.setText("");
    	}
    	else
    	{
            lblError.setText("You are not connected to the internet");	
            showAlertDialog(tablessupplysactivity.this, "No Internet Connection",
                    "You are not connected to the internet.", false);
            return;
    	}		
		
    	//check the table agent user
    	lblError.setText("");
		_TableAgent = txtTableAgent.getText().toString();
		if (_TableAgent.equals("")){
			Toast.makeText(this, "Table agent cannot be empty.", Toast.LENGTH_LONG).show();
	        lblError.setText("Table agent cannot be empty.");
	        return ;			
		}
		
		//get and check from IMSI
		_FromICC = txtFromICC.getText().toString();
		if (_FromICC.length() != 16){
			Toast.makeText(this, "From ICC doesnt exist.", Toast.LENGTH_LONG).show();
	    	lblError.setText("From ICC doesnt exist.");
	    	return;
		} 
		if (!_FromICC.substring(0, 4).equals("2439")) {
			Toast.makeText(this, "From ICC doesnt exist.", Toast.LENGTH_LONG).show();
	    	lblError.setText("From ICC doesnt exist.");
	    	return;			
		}
		_FromIMSI = "630900" + _FromICC.substring(6,7) + _FromICC.substring(8,16);
		
		//get and check till IMSI
		_TillICC = txtTillICC.getText().toString();
		if (_TillICC.length() != 16){
			Toast.makeText(this, "Till ICC doesnt exist.", Toast.LENGTH_LONG).show();
	    	lblError.setText("Till ICC doesnt exist.");
	    	return;
		} 
		if (!_TillICC.substring(0, 4).equals("2439")) {
			Toast.makeText(this, "Till ICC doesnt exist.", Toast.LENGTH_LONG).show();
	    	lblError.setText("Till ICC doesnt exist.");
	    	return;			
		}		
		_TillIMSI = "630900" + _TillICC.substring(6,7) + _TillICC.substring(8,16);
		
		//check if till IMSI < fromIMSI
		if (Double.parseDouble(_TillIMSI) < Double.parseDouble(_FromIMSI)) {
			Toast.makeText(this, "ICC range doesnt exist.", Toast.LENGTH_LONG).show();
	    	lblError.setText("ICC range doesnt exist.");
	    	return;				
		}

		//get the user
		_User = getValueFromDB("_us_Login");

		//check user limit
		HTTP_check_user_limit = baseUrl+"SOP_XML_Get_table_Agent_CurrentSIM_nbr.aspx?TableUser="+_TableAgent;
		HTTP_insert_supply =baseUrl + "SOP_XML_Insert_Table_Supply.aspx?salesUser="+_User+"&TableUser="+_TableAgent+"&FromIMSI="+_FromIMSI+"&TillIMSI="+_TillIMSI;

		new DownloadAndParseUserLimitXMLTask().execute(HTTP_check_user_limit); 
	}	
	
	public class DownloadAndParseUserLimitXMLTask extends AsyncTask<String, Void, String[]> 
	{
		private ProgressDialog dialog = new ProgressDialog(tablessupplysactivity.this);
		 @Override
		 protected void onPreExecute() {
			  Button supplyButton = (Button)findViewById(R.id.supplyButton);
			  supplyButton.setVisibility(View.GONE);			 
			  dialog.setMessage("Please wait while retrieving table agents limits");
			  dialog.show();
		 }
		protected String[] doInBackground(String... urls) 
			{
			return parseUserLimit(urls[0]);
			}
		@Override
		protected void onPostExecute(String[] result) 
		{					
			Button supplyButton = (Button)findViewById(R.id.supplyButton);
	    	TextView lblError = (TextView) findViewById(R.id.lblError); 
	    	//check if there is error
			if (!result[0].trim().equals("")){

				lblError.setText(result[0].toString());	
				dialog.dismiss();
				supplyButton.setVisibility(View.VISIBLE);	
				return;
			}
			
			//check if supply quatity is allowed for user
			double _remaining = 0;
			double _supply = 0;			
			_remaining = (Double.parseDouble(result[2]) - Double.parseDouble(result[1]));
			_supply = (Double.parseDouble(_TillIMSI) - Double.parseDouble(_FromIMSI) + 1);
			if (_remaining < _supply)  {
				lblError.setText("The quantity you are trying to supply exceed the agent limit. Only " +_remaining+ " SIMs are still allowed.");	
				dialog.dismiss();
				supplyButton.setVisibility(View.VISIBLE);	
				return;			
			}
			lblError.setText("");
			dialog.dismiss();
			//call table supply HTTP SOAP
			new DownloadAndParseInsertSupplyXMLTask().execute(HTTP_insert_supply); 
		}
	}	

    public String[] parseUserLimit(String strURL) 
    {
    	String _error = "";
    	String _SIMCount = "";
    	String _userLimit = "";
    	
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
                    if (tagname.equalsIgnoreCase("table_agent")) {
                    	_error = "";
                    	_SIMCount = "";
                    	_userLimit = "";
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("table_agent")) {
                    	strParseResult[0] = _error;
                    	strParseResult[1] = _SIMCount;
                    	strParseResult[2] = _userLimit;
                    } else if (tagname.equalsIgnoreCase("Error")) {
                    	_error = text;                     		
                    } else if (tagname.equalsIgnoreCase("SIMCount")) {
                    	_SIMCount = text;                     		
                    } else if (tagname.equalsIgnoreCase("userLimit")) {
                    	_userLimit = text;                     		
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
	
	
	public class DownloadAndParseInsertSupplyXMLTask extends AsyncTask<String, Void, String[]> 
	{
		private ProgressDialog dialog = new ProgressDialog(tablessupplysactivity.this);
		 @Override
		 protected void onPreExecute() {
			  Button supplyButton = (Button)findViewById(R.id.supplyButton);
			  supplyButton.setVisibility(View.GONE);			 
			  dialog.setMessage("Please wait while loading SIMs to table agent");
			  dialog.show();
		 }
		protected String[] doInBackground(String... urls) 
			{
			return parseInsertSupply(urls[0]);
			}
		@Override
		protected void onPostExecute(String[] result) 
		{	
	    	TextView lblError = (TextView) findViewById(R.id.lblError); 
			if (!result[0].equals("")){
		    	lblError.setText(result[0].toString());	
			} else {
				lblError.setText(result[1].toString());	
			}
	        dialog.dismiss();
		}
	}	

	
    public String[] parseInsertSupply(String strURL) 
    {
    	String _error = "";
    	String _MESSAGE = "";
    	
    	final int ROWS = 2;
    	
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
                    if (tagname.equalsIgnoreCase("table_supply")) {
                    	_error = "";
                    	_MESSAGE = "";
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("table_supply")) {
                    	strParseResult[0] = _error;
                    	strParseResult[1] = _MESSAGE;
                    } else if (tagname.equalsIgnoreCase("Error")) {
                    	_error = text;                     		
                    } else if (tagname.equalsIgnoreCase("MESSAGE")) {
                    	_MESSAGE = text;                     		
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
	

	public class DownloadAndParseTableAgentXMLTask extends AsyncTask<String, Void, String[]> 
	{
		private ProgressDialog dialog = new ProgressDialog(tablessupplysactivity.this);
		 @Override
		 protected void onPreExecute() {
		  dialog.setMessage("Please wait while retrieving table agents");
		  dialog.show();
		 }
		protected String[] doInBackground(String... urls) 
			{
			return parseTableAgent(urls[0]);
			}
		@Override
		protected void onPostExecute(String[] result) 
			{
			
			int _new_len = 0;
			for (int i=0; i<result.length; i++) {
				if(result[i] != null){
					_new_len = _new_len + 1;
				}
			}
			String _BankList[];
			_BankList = new String[_new_len + 1];
			_BankList[0] = "";
			for (int i=0; i<_new_len; i++) {
				_BankList[i + 1] = result[i];
			} 
			//Fill Spinner
		    //final Spinner SpinnerBank = (Spinner) findViewById(R.id.SpinnerTableAgent);
		    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(tablessupplysactivity.this,
		   // 		android.R.layout.simple_spinner_item, _BankList);
		    //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		    //SpinnerBank.setAdapter(adapter);
		    
		    
	        dialog.dismiss();
			}
	}	

	
    public String[] parseTableAgent(String strURL) 
    {
    	String _user = "";

    	int _Counter = 0;
    	final int ROWS = 1500;
    	
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
                    if (tagname.equalsIgnoreCase("user")) {
                    	_user = "";
                    }
                    break;

                case XmlPullParser.TEXT:
                    text = parser.getText();
                    break;
 
                case XmlPullParser.END_TAG:
                    if (tagname.equalsIgnoreCase("user")) {
                    	strParseResult[_Counter] = _user;
                    	_Counter = _Counter + 1;
                    } else if (tagname.equalsIgnoreCase("login")) {
                    	_user = text;                     		
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
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(tablessupplysactivity.this);
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
		    FeedEntry.COLUMN_NAME_CreateCellId + TEXT_TYPE +
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