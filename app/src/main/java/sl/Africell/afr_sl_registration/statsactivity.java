package sl.Africell.afr_sl_registration;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.example.pmvungu.afr_erp.R;

public class statsactivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}

}
