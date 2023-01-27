//**patou diasivi lasted update : 12:00 2020-12-15 **/
package sl.Africell.afr_sl_registration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.loginactivity.FeedReaderContract.FeedEntry;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkSpecifier;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.BuildConfig;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


import static android.support.v4.content.FileProvider.getUriForFile;
import static sl.Africell.afr_sl_registration.apiUrl.baseUrl;

public class loginactivity extends Activity {
    public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
    private String text;
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private Activity activity  ;
	libClass libclass;
	SessionManager session;
	private static final int REQUEST_CODE = 101;
	public static final int RequestPermissionCode = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_login);
			session = new SessionManager(getApplicationContext());
			PackageInfo pinfo;
			activity = (Activity) getApplicationContext();
			String _version = "";
			try {
				pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				// _version = String.valueOf(pinfo.versionCode);
				_version=pinfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			try {
				//onClickbtn();
				libclass = new libClass(loginactivity.this);
				if (libclass.checkPermission()) {
					//  Toast.makeText(Activity_login.this, "All Permissions Granted Successfully", Toast.LENGTH_LONG).show();
				} else {
					libclass.requestPermission(loginactivity.this);
				}
			} catch (Exception e) {
				e.printStackTrace();
				popMessage("Error occur",e.getMessage());
			}
			String _CellId = getMyCellId();
			String _strHTML = ""
					+ "<small>"
					+ "<b>App Version: </b>" + _version + "<br />"
					+ "<b>LAC-CELLID: </b>" + _CellId + "<br />"
					+ "</small>";
			TextView lblWelcome1 = (TextView) findViewById(R.id.lblWelcome1);
			lblWelcome1.setText(Html.fromHtml(_strHTML));
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	/**public void onClickbtn(){
		try{

			String isconnectedTo = haveNetworkConnection();
			Toast.makeText(this,  isconnectedTo  ,Toast.LENGTH_LONG).show();

			ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cManager.getActiveNetworkInfo();
			// activeNetworkInfo=cManager.getNetworkCapabilities(cManager.getActiveNetwork());
			NetworkCapabilities netc = cManager.getNetworkCapabilities(cManager.getActiveNetwork());
			int downSpeed =  (netc.getLinkDownstreamBandwidthKbps())/1000;  //Get Mbps
			int upSpeed = (netc.getLinkUpstreamBandwidthKbps())/1000;  //Get Mbps

            String _strHTML = ""
                    + "<small>"
                    + "<b>Down Speed: </b>" + downSpeed + " Mbps<br/>"
                    + "<b>UP Speed: </b>" + upSpeed + " Mbps<br/>"
					+ "<b>Network Connection : </b>" + isconnectedTo + "<br/>"
                    + "</small>";
            //TextView strHTML = (TextView) findViewById(R.id.lblspeed);
           // strHTML.setText(Html.fromHtml(_strHTML));

			Toast.makeText(this,_strHTML,Toast.LENGTH_LONG).show();

		}catch (Exception e){
			Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
		}


	}*/
	private String haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;
          String result="";
		  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	       NetworkInfo[] netInfo = cm.getAllNetworkInfo();

		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI")){
				if (ni.isConnected()) {
					haveConnectedWifi = true;
					result = "WIFI";
					 return result;
				}
		    } else

			if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
//result = "MOBILE";
				if (ni.isConnected()) {
					haveConnectedMobile = true;
					 switch (ni.getSubtype()){
						 case TelephonyManager.NETWORK_TYPE_GPRS :
							 result="2G";
							 break;
							case TelephonyManager.NETWORK_TYPE_EDGE:
							 result="2G";
								break;
							case TelephonyManager.NETWORK_TYPE_CDMA:
							 result="2G";
								break;
							case TelephonyManager.NETWORK_TYPE_1xRTT:
							 result="2G";
								break;
							case TelephonyManager.NETWORK_TYPE_IDEN:
							 result="2G";
								break;
							case TelephonyManager.NETWORK_TYPE_GSM :
							 result="2G";
						 	break;
						 case TelephonyManager.NETWORK_TYPE_LTE:
							 result="4G";
							 break;
						case 	TelephonyManager.NETWORK_TYPE_IWLAN:
						 case 19 :  result="4G";
						 break;
						 default: result="3G";
						 	break;
					 }


				}

				return result;
			}

		}
		return result;
		//return haveConnectedWifi || haveConnectedMobile;

	}
    private void DownloadNewVersion(String _Version) {
        String url = baseUrl+ "VirtualD/AFR_ERP_" + _Version + ".apk";
        new DownloadFileAsync().execute(url);
    }
    class DownloadFileAsync extends AsyncTask<String, String, String> {

    	@SuppressWarnings("deprecation")
		@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		showDialog(DIALOG_DOWNLOAD_PROGRESS);
    	}

    	@Override
    	protected String doInBackground(String... aurl) {
    		int count;

    	try {

    	URL url = new URL(aurl[0]);
    	URLConnection conexion = url.openConnection();
    	conexion.connect();

    	int lenghtOfFile = conexion.getContentLength();
    	Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

    	InputStream input = new BufferedInputStream(url.openStream());
    	OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory()+ "/download/AFR_ERP.apk");

    	byte data[] = new byte[1024];

    	long total = 0;

    		while ((count = input.read(data)) != -1) {
    			total += count;
    			publishProgress(""+(int)((total*100)/lenghtOfFile));
    			output.write(data, 0, count);
    		}

    		output.flush();
    		output.close();
    		input.close();
    	} catch (Exception e) {}
    	return null;

    	}
    	protected void onProgressUpdate(String... progress) {
    		 Log.d("ANDRO_ASYNC",progress[0]);
    		 mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    	}

    	@SuppressWarnings("deprecation")
		@Override
    	protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
			//UnInstallApp("sl.Africell.afr_sl_registration");
			installApp();
    	}
    }	
    
    protected Dialog onCreateDialog(int id) {
        switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading new AFR ERP version. Please wait..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
        }
    } 
    
	public void installApp(){
		try{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ "/Download/"  + "AFR_ERP.apk")), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
//			//get destination to update file and set Uri
//			//TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
//			//aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
//			//solution, please inform us in comment
//			Context context = this;
//			File filepath = new File(Environment.getExternalStorageDirectory(), "download/AFR");
//			File newFile = new File(filepath, "AFR_ERP.apk");
//			Uri contentUri = getUriForFile(context, "com.example.pmvungu.afr_erp.provider", newFile);
//			Intent install = new Intent(Intent.ACTION_VIEW);
//			install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////			install.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ "/download/"  + "AFR_ERP.apk")),
////					"application/vnd.android.package-archive");
//						install.setDataAndType(contentUri,
//					"application/vnd.android.package-archive");
//			startActivity(install);
//			finish();

		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
	public void UnInstallApp(String packageName)// Passing com.example.homelauncher as package name.
	{
		try {
			Uri packageURI = Uri.parse(packageName.toString());
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
			uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(uninstallIntent);
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/*check if device is connected to the Internet*/
	private boolean isNetworkAvailable() {
		try{
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
		} catch (Exception e) {
			return false;
	 }
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	public void onButtonLoginClicked(View view) {
    	//call the login HTTP API
        String strCellId = getMyCellId();
        String strIMEI = getMyIMEI(); 
        String strICC = getMyICC(); 
        String strLogin;
        String strPassword;
		EditText txtLogin = (EditText) findViewById(R.id.txtLogin);
		strLogin = txtLogin.getText().toString();
		EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
		strPassword = txtPassword.getText().toString();
		//String strHTTPLink = "http://register.africell.cd/SOP_Reg_login.aspx?login="+strLogin+"&password="+strPassword+"&IMEI="+strIMEI+"&ICC="+strICC+"&IP=&location="+strCellId;
    	//new DownloadAndParseXMLTask().execute(strHTTPLink);
		//check Internet connection
		boolean isConnected = isNetworkAvailable();
    	if (isConnected)
    	{
            TextView lblLoginResult = (TextView) findViewById(R.id.lblLoginResult); 
            lblLoginResult.setText("");
        	//call the login link
    		String strHTTPLink =baseUrl+ "SOP_Reg_login.aspx?login="+strLogin+"&password="+strPassword+"&IMEI="+strIMEI+"&ICC="+strICC+"&IP=&location="+strCellId;
            //String strHTTPLink = "https://www.africell-bsu.com/SOP_Reg_login.aspx?login="+strLogin+"&password="+strPassword+"&IMEI="+strIMEI+"&ICC="+strICC+"&IP=&location="+strCellId;
			session.createLoginSession(strLogin);
            new DownloadAndParseXMLTask().execute(strHTTPLink);
    	}
    	else
    	{
            TextView lblLoginResult = (TextView) findViewById(R.id.lblLoginResult); 
            lblLoginResult.setText("You are not connected to the internet");	
            //showAlertDialog(loginactivity.this, "No Internet Connection",
            //        "You are not connected to the internet.", false);
            //offline login 
            String _DBLogin;
            String _DBPassword;
            _DBLogin = getValueFromDB("_us_Login");
            _DBPassword = getValueFromDB("_us_password");
            if (strLogin.equals(_DBLogin)){
            	if (strPassword.equals(_DBPassword)){
                    String[] _Message = new String[1];
                    _Message[0] = "1";
                    openSecondActivity(_Message);
            	} else{
            		return;
            	}
            } else {
            	return;
            }
    	}
	}
    private void openSecondActivity(String[] result)
    {
        if (result[0].equals("1")  )
        {
        	String strMessage = "";    	
        	Intent intent = new Intent(this, secondactivity.class);
        	intent.putExtra(EXTRA_MESSAGE, strMessage);   
        	startActivity(intent);
        }
    }
    private String getMyICC(){
		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
		} else {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
		}
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        return mTelephonyMgr.getSimSerialNumber();
    }
    private String getMyIMEI(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
		} else {
			ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);

		}
        return mTelephonyMgr.getDeviceId();
    }
    private String getMyCellId()
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
    	if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) 
    	{
    	    final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
    	    if (location != null) 
    	    {
				TextView lblVersion = (TextView) findViewById(R.id.lblWelcome1);
				lblVersion.setVisibility(View.VISIBLE);

				String strlblVersion = "<small> Cell ID : " + location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff) + "</small>";
				lblVersion.setText(Html.fromHtml(strlblVersion));
    	        return location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);

    	    }
    	    else
    	    {
    	    	return "";
    	    }
    	} 
    	else
    	{
    		return "";
    	}
    }
	public class DownloadAndParseXMLTask extends AsyncTask<String, Void, String[]> 
	{
        private ProgressDialog dialog = new ProgressDialog(loginactivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait while...");

            dialog.setCancelable(false);
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
				TextView text = (TextView) findViewById(R.id.lblLoginResult);

				try{

					/*if (result[0].contains("error")){
						text.setText(Html.fromHtml(" " +result[0]));
						return;
					}else{*/

						text.setText(Html.fromHtml("	" +result[1]));
						PackageInfo pinfo;
						try {
							//openSecondActivity(result);
							//Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

							pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
							String versionNumber = String.valueOf(pinfo.versionCode);
							//String versionName = pinfo.versionName;
							String _DBversionNumber = getValueFromDB("_us_APP_Version_Code");

							if (Float.parseFloat(versionNumber) != Float.parseFloat(_DBversionNumber)){
								if (result[0].equals("1"))
								{
									DownloadNewVersion (_DBversionNumber);
								}
							} else {
								openSecondActivity(result);
							}

						} catch (NameNotFoundException e) {

							e.printStackTrace();
						}
					/*}*/

				}catch (Exception e){
					text.setText(Html.fromHtml(" Error occur :" +e.getMessage()));
						return ;
				}

		}
	}
    public String[] parse(String strURL) 
    {
	    String _ll_status = "";
	    String _ll_status_description = "";
	    String _us_new_registration = "";
	    String _us_daily_stats = "";	    
	    String _us_registration_query = "";
	    String  _us_user_registration_stats = "";
	    String  _us_offline_registration = "";
	    String  _us_synchronize_registration = "";
	    String  _us_sim_creation = "";
	    String  _us_sim_change = "";
	    String 	_us_APP_Version_Code = "";
	    String 	_us_is_table_agent = "";
	    String 	_us_table_agent_SIMs = "";
	    
    	String[] strParseResult;
    	strParseResult = new String[5];
    	
    	InputStream is = null;	
    	try 
		{
    		is = OpenHttpConnection(strURL);

		}
		catch (IOException e1)
		{
			Log.d("NetworkingActivity", e1.getLocalizedMessage());
            strParseResult[0]=e1.getMessage();
			return  strParseResult;
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
                        _ll_status = "";
                        _ll_status_description= "";
                        _us_new_registration= "";
                        _us_daily_stats= "";
                	    _us_registration_query = "";
                	    _us_user_registration_stats = "";
                	    _us_offline_registration = "";
                	    _us_synchronize_registration = "";
                	    _us_sim_creation = "";
                	    _us_sim_change = "";
                	    _us_APP_Version_Code = "";
                	    _us_is_table_agent = "";
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
                   if (tagname.equalsIgnoreCase("StatusId")) {
                	   _ll_status = text;
                    } else if (tagname.equalsIgnoreCase("StatusDescription")) {
                    	_ll_status_description = text;
                    } else if (tagname.equalsIgnoreCase("new_registration")) {
                    	_us_new_registration = text;
                    } else if (tagname.equalsIgnoreCase("daiyStats")) {
                    	_us_daily_stats = text;
                    } else if (tagname.equalsIgnoreCase("us_registration_query")) {
                    	_us_registration_query = text;
                    } else if (tagname.equalsIgnoreCase("us_user_registration_stats")) {
                    	_us_user_registration_stats = text;
                    } else if (tagname.equalsIgnoreCase("us_offline_registration")) {
                    	_us_offline_registration = text;
                    } else if (tagname.equalsIgnoreCase("us_synchronize_registration")) {
                    	_us_synchronize_registration = text;
                    } else if (tagname.equalsIgnoreCase("us_sim_creation")) {
                    	_us_sim_creation = text;
                    } else if (tagname.equalsIgnoreCase("us_sim_change")) {
                    	_us_sim_change = text;
                    } else if (tagname.equalsIgnoreCase("us_APP_Version_Code")) {
                    	_us_APP_Version_Code = text;
                    } else if (tagname.equalsIgnoreCase("us_is_table_agent")) {
                    	_us_is_table_agent = text;
                    } else if (tagname.equalsIgnoreCase("us_table_agent_SIMs")) {
                    	_us_table_agent_SIMs = text;
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

        String strLogin;
		EditText txtLogin = (EditText) findViewById(R.id.txtLogin);
        String strTypedPassword;
		EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

		strLogin = txtLogin.getText().toString();
		strTypedPassword = txtPassword.getText().toString();
        strParseResult[0] = _ll_status;
        strParseResult[1] = _ll_status_description;
        strParseResult[2] = _us_new_registration;
        strParseResult[3] = _us_daily_stats;
        strParseResult[4] = strLogin;

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(loginactivity.this);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //empty the table
        db.delete(FeedEntry.TABLE_NAME,null,null);

        ContentValues values2 = new ContentValues();
        values2.put(FeedEntry.COLUMN_NAME_TITLE, "_us_new_registration");
        values2.put(FeedEntry.COLUMN_NAME_VALUE, _us_new_registration);
        db.insert(FeedEntry.TABLE_NAME,null,values2);

        ContentValues values3 = new ContentValues();
        values3.put(FeedEntry.COLUMN_NAME_TITLE, "_us_daily_stats");
        values3.put(FeedEntry.COLUMN_NAME_VALUE, _us_daily_stats);
        db.insert(FeedEntry.TABLE_NAME,null,values3);

        ContentValues values4 = new ContentValues();
        values4.put(FeedEntry.COLUMN_NAME_TITLE, "_us_Login");
        values4.put(FeedEntry.COLUMN_NAME_VALUE, strLogin);
        db.insert(FeedEntry.TABLE_NAME,null,values4);

        ContentValues values5 = new ContentValues();
        values5.put(FeedEntry.COLUMN_NAME_TITLE, "_us_registration_query");
        values5.put(FeedEntry.COLUMN_NAME_VALUE, _us_registration_query);
        db.insert(FeedEntry.TABLE_NAME,null,values5);

        ContentValues values6 = new ContentValues();
        values6.put(FeedEntry.COLUMN_NAME_TITLE, "_us_user_registration_stats");
        values6.put(FeedEntry.COLUMN_NAME_VALUE, _us_user_registration_stats);
        db.insert(FeedEntry.TABLE_NAME,null,values6);

        ContentValues values7 = new ContentValues();
        values7.put(FeedEntry.COLUMN_NAME_TITLE, "_us_offline_registration");
        values7.put(FeedEntry.COLUMN_NAME_VALUE, _us_offline_registration);
        db.insert(FeedEntry.TABLE_NAME,null,values7);

        ContentValues values8 = new ContentValues();
        values8.put(FeedEntry.COLUMN_NAME_TITLE, "_us_synchronize_registration");
        values8.put(FeedEntry.COLUMN_NAME_VALUE, _us_synchronize_registration);
        db.insert(FeedEntry.TABLE_NAME,null,values8);

        ContentValues values9 = new ContentValues();
        values9.put(FeedEntry.COLUMN_NAME_TITLE, "_us_sim_creation");
        values9.put(FeedEntry.COLUMN_NAME_VALUE, _us_sim_creation);
        db.insert(FeedEntry.TABLE_NAME,null,values9);

        ContentValues values10 = new ContentValues();
        values10.put(FeedEntry.COLUMN_NAME_TITLE, "_us_sim_change");
        values10.put(FeedEntry.COLUMN_NAME_VALUE, _us_sim_change);
        db.insert(FeedEntry.TABLE_NAME,null,values10);

        ContentValues values11 = new ContentValues();
        values11.put(FeedEntry.COLUMN_NAME_TITLE, "_us_password");
        values11.put(FeedEntry.COLUMN_NAME_VALUE, strTypedPassword);
        db.insert(FeedEntry.TABLE_NAME,null,values11);

        ContentValues values12 = new ContentValues();
        values12.put(FeedEntry.COLUMN_NAME_TITLE, "_us_APP_Version_Code");
        values12.put(FeedEntry.COLUMN_NAME_VALUE, _us_APP_Version_Code);
        db.insert(FeedEntry.TABLE_NAME,null,values12);

        ContentValues values13 = new ContentValues();
        values13.put(FeedEntry.COLUMN_NAME_TITLE, "_us_is_table_agent");
        values13.put(FeedEntry.COLUMN_NAME_VALUE, _us_is_table_agent);
        db.insert(FeedEntry.TABLE_NAME,null,values13);

        ContentValues values14 = new ContentValues();
        values14.put(FeedEntry.COLUMN_NAME_TITLE, "_us_table_agent_SIMs");
        values14.put(FeedEntry.COLUMN_NAME_VALUE, _us_table_agent_SIMs);
        db.insert(FeedEntry.TABLE_NAME,null,values14);

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

	public String getValueFromDB(String strValue){
        String label = "";
        String selectQuery = "SELECT Value FROM securityEntry WHERE Title='"+strValue+"'"; 
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(loginactivity.this);
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
	public void popMessage(String strTitle, String strMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(loginactivity.this);
		builder.setMessage(strMessage);
		builder.setTitle(strTitle);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				finish();

			}
		});
     /*  builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //callForm(MainActivity.this,MainActivity.class);
            }
        });*/
		AlertDialog alert = builder.create();
		alert.show();
		return;
	}
}
