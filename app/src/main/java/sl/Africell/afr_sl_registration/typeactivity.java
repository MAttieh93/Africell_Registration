package sl.Africell.afr_sl_registration;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.pmvungu.afr_erp.R;

import cn.com.aratek.demo.DemoActivity;

public class typeactivity extends Activity {
	public static final String EXTRA_MESSAGE = "sl.Africell.afr_sl_registration.MESSAGE";
	String[] message;
	public RadioButton radiotype1;
	public RadioButton radiotype2;

	public String regTypeId="1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_type);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}
	public void onRadioButtonClicked(View view) {
		//Toast.makeText(getApplicationContext(),	"checked  type activite", Toast.LENGTH_SHORT).show();
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch(view.getId()) {
			case R.id.lblRegistrationType1:
				if (checked) {
					regTypeId = "1";
					break;
				}
			case R.id.lblRegistrationType2:
				if (checked) {
					regTypeId = "2";
					break;
				}
		}
		 //Toast.makeText(getApplicationContext(), regTypeId , Toast.LENGTH_SHORT).show();
	}

	public void onButtonOkClicked(View view) {
		//boolean checked = ((RadioButton) view).isChecked();
		String strRegtypeMessage=null;
		//Toast.makeText(getApplicationContext(), regTypeId , Toast.LENGTH_SHORT).show();


			if (regTypeId.equals("1")){
				      strRegtypeMessage = "NatinalID";
				Intent intent= new Intent(this, newregistrationactivity.class);
				intent.putExtra("regTypeId", strRegtypeMessage);
				startActivity(intent);

			}else{
				if (regTypeId.equals("2")){
					  strRegtypeMessage = "Finger";
					Intent intent= new Intent(this, DemoActivity.class);
					intent.putExtra("regTypeId", strRegtypeMessage);
					startActivity(intent);
				}
			}

	}



}
