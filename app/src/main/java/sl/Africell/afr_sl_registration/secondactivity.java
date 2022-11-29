package sl.Africell.afr_sl_registration;

import java.util.ArrayList;

import com.example.pmvungu.afr_erp.R;
import sl.Africell.afr_sl_registration.loginactivity.FeedReaderContract.FeedEntry;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class secondactivity extends Activity {
	public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
	String[] message;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		//Toast.makeText(getApplicationContext(),	"fdfdf", Toast.LENGTH_SHORT).show();

	    String _us_login = getValueFromDB("_us_Login");
	    String _us_new_registration = getValueFromDB("_us_new_registration");
	    String _us_daily_stats = getValueFromDB("_us_daily_stats");
	    String _us_registration_query = getValueFromDB("_us_registration_query");
	    String  _us_user_registration_stats = getValueFromDB("_us_user_registration_stats");
	    String  _us_offline_registration = getValueFromDB("_us_offline_registration");
	    String  _us_synchronize_registration = getValueFromDB("_us_synchronize_registration");
	    String  _us_sim_creation = getValueFromDB("_us_sim_creation");
	    String  _us_sim_change = getValueFromDB("_us_sim_change");	    
	    String  _us_is_table_agent = getValueFromDB("_us_is_table_agent");
	    
        TextView text = (TextView) findViewById(R.id.lblWelcome); 
        text.setText(Html.fromHtml("Welcome " + _us_login));
        
        //apply security rules to new registration
		ListView listAccessMenu = (ListView) findViewById(R.id.listAccessMenu);
		final ArrayList<String> list = new ArrayList<String>();

		if (_us_is_table_agent.equals("1")){
			list.add("New SIM Registration - Table");	
		} else {
			if (_us_new_registration.equals("1")){
				list.add("New SIM Registration");	
			}
			if (_us_daily_stats.equals("1")){
				list.add("SIM Registration Daily Stats");	
			}
			if (_us_registration_query.equals("1")){
				list.add("SIM Registration Query");	
			}
			if (_us_user_registration_stats.equals("1")){
				list.add("User SIM Registration Stats");	
			}
			if (_us_offline_registration.equals("1")){
				list.add("Offline SIM Registration");	
			}
			if (_us_synchronize_registration.equals("1")){
				list.add("Synchronize Registration");	
			}
			if (_us_sim_creation.equals("1")){
				list.add("SIM Creation");	
			}
			if (_us_sim_change.equals("1")){
				list.add("SIM Change");	
			}
			if (_us_is_table_agent.equals("0")){
				list.add("New Table Agent");	
				list.add("Tables Supply");	
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listAccessMenu.setAdapter(adapter); 
        
        
        listAccessMenu.setTextFilterEnabled(true);
		 
        listAccessMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    // When clicked, show a toast with the TextView text
				String strChoice;
				strChoice = (String) ((TextView) view).getText();
			    
			    if (strChoice.equals("New SIM Registration")){
			    	onButtonNewRegistrationClicked();
			    } else if (strChoice.equals("SIM Registration Daily Stats")){
			    	onButtonStatsClicked();
			    } else if (strChoice.equals("SIM Registration Query")){
			    	onButtonregistration_queryClicked();	
			    } else if (strChoice.equals("User SIM Registration Stats")){
			    	//onButtonuser_registration_statsClicked();
			    } else if (strChoice.equals("Offline SIM Registration")){
			    	onButtonOfflineRegistrationClicked();
			    } else if (strChoice.equals("Synchronize Registration")){
			    	onButtonSynchronizeRegistrationClicked();
			    } else if (strChoice.equals("SIM Creation")){
			    	onButtonsim_creationClicked();
			    } else if (strChoice.equals("SIM Change")){
			    	//onButtonuser_registration_statsClicked();
			    } else if (strChoice.equals("Tables Supply")){
			    	onButtonTablesSupplyClicked();
			    } else if (strChoice.equals("New Table Agent")){
			    	onButtonNewTableAgentClicked();
			    } else if (strChoice.equals("New SIM Registration - Table")){
			    	onButtonNewRegistrationTableClicked();
			    } else if (strChoice.equals("About")){
			    	Toast.makeText(getApplicationContext(),	"Africell Registration. Copyright   2014, Africell, Inc. All Rights Reserved.", Toast.LENGTH_SHORT).show();
			    } else {
			    	Toast.makeText(getApplicationContext(),	strChoice, Toast.LENGTH_SHORT).show();
			    }
			}
		});

	}
	
	public void onButtonSynchronizeRegistrationClicked() {

    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, synchronizeregistrationactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}	
		
	
	public void onButtonOfflineRegistrationClicked() {

    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, offlinesimregistrationactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}	

	public String getValueFromDB(String strValue){
        String label = "";
        String selectQuery = "SELECT Value FROM securityEntry WHERE Title='"+strValue+"'"; 
	    FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(secondactivity.this);
	    SQLiteDatabase db = mDbHelper.getReadableDatabase();
	    
        android.database.Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (null != cursor && cursor.moveToFirst()) {
            label = cursor.getString(cursor.getColumnIndex("Value"));
        }

        return label;

    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.second, menu);
		return true;
	}
	
	public void onButtonNewRegistrationClicked() {
       String[] strMessage = message;
		//Intent intent = new Intent(this, typeactivity.class);
       Intent intent = new Intent(this, newregistrationactivity.class);
       intent.putExtra(EXTRA_MESSAGE, strMessage);   
       startActivity(intent);      
	}
	
	public void onButtonStatsClicked() {

    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, statsactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}
	
	public void onButtonregistration_queryClicked() {
    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, registrationqueryactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}	

	public void onButtonsim_creationClicked() {
    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, simcreationactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}	

	public void onButtonTablesSupplyClicked() {
    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, tablessupplysactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}		
	
	public void onButtonNewTableAgentClicked() {
    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, createtableuseractivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
	}	
	
	public void onButtonNewRegistrationTableClicked() {
    	String[] strMessage = message;    	
    	Intent intent = new Intent(this, Newregistrationtableactivity.class);
    	intent.putExtra(EXTRA_MESSAGE, strMessage);   
    	startActivity(intent);
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
