package cn.com.aratek.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pmvungu.afr_erp.R;
import java.io.UnsupportedEncodingException;

import cn.com.aratek.qrc.CodeScanner;
import cn.com.aratek.util.Result;

@SuppressLint("HandlerLeak")
public class QqcDemo extends Activity implements View.OnClickListener {

    private static final int MSG_SHOW_ERROR = 0;
    private static final int MSG_SHOW_INFO = 1;
    private static final int MSG_UPDATE_INFO = 2;
    private static final int MSG_UPDATE_BUTTON = 3;
    private static final int MSG_UPDATE_SN = 4;
    private static final int MSG_UPDATE_DEV_VERSION = 5;
    private static final int MSG_SHOW_PROGRESS_DIALOG = 6;
    private static final int MSG_DISMISS_PROGRESS_DIALOG = 7;
    private static final int MSG_FINISH_ACTIVITY = 8;

    private CodeScanner mScanner;
    private Button mBtnScan;
    private EditText mDevSN;
    private EditText mDevVersion;
    private EditText mScanInfo;
    private ProgressDialog mProgressDialog;
    private QrcTask mTask;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_ERROR: {
                    showDialog(0, (Bundle) msg.obj);
                    break;
                }
                case MSG_SHOW_INFO: {
                    Toast.makeText(QqcDemo.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                }
                case MSG_UPDATE_INFO: {
                    mScanInfo.setText((String) msg.obj);
                    break;
                }
                case MSG_UPDATE_BUTTON: {
                    Boolean enable = (Boolean) msg.obj;
                    mBtnScan.setEnabled(enable);
                    break;
                }
                case MSG_UPDATE_SN: {
                    mDevSN.setText((String) msg.obj);
                    break;
                }
                case MSG_UPDATE_DEV_VERSION: {
                    mDevVersion.setText((String) msg.obj);
                    break;
                }
                case MSG_SHOW_PROGRESS_DIALOG: {
                    String[] info = (String[]) msg.obj;
                    mProgressDialog.setTitle(info[0]);
                    mProgressDialog.setMessage(info[1]);
                    mProgressDialog.show();
                    break;
                }
                case MSG_DISMISS_PROGRESS_DIALOG: {
                    mProgressDialog.dismiss();
                    break;
                }
                case MSG_FINISH_ACTIVITY: {
                    finish();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrc);

        mScanner = CodeScanner.getInstance(this);

        mDevSN = (EditText) findViewById(R.id.et_SN);
        mDevVersion = (EditText) findViewById(R.id.et_version);
        mScanInfo = (EditText) findViewById(R.id.et_scaninfo);
        mBtnScan = (Button) findViewById(R.id.bt_scan);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIcon(android.R.drawable.ic_dialog_info);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);

        enableControl(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        openDevice();
    }

    @Override
    protected void onPause() {
        closeDevice(false);

        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_scan:
                scanBarcode();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                closeDevice(true);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void enableControl(boolean enable) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_BUTTON, enable));
    }

    private void finishActivity() {
        mHandler.sendEmptyMessage(MSG_FINISH_ACTIVITY);
    }

    private void openDevice() {
        new Thread() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.preparing_device));
                int error;
                if ((error = mScanner.powerOn()) != CodeScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.hardware_barcode_scanner_power_on_failed), getHwBarcodeErrorString(error));
                }
                if ((error = mScanner.open()) != CodeScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.hardware_barcode_scanner_open_failed), getHwBarcodeErrorString(error));
                } else {
                    showInfoToast(getString(R.string.hardware_barcode_scanner_open_success));
                    enableControl(true);
                    Result res = mScanner.getSN();
                    if (res.error == CodeScanner.RESULT_OK) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SN, (String) res.data));
                    }
                    res = mScanner.getDriverVersion();
                    if (res.error == CodeScanner.RESULT_OK) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_DEV_VERSION, (String) res.data));
                    }
                }
                dismissProgressDialog();
            }
        }.start();
    }

    private void closeDevice(final boolean finish) {
        new Thread() {
            @Override
            public void run() {
                showProgressDialog(getString(R.string.loading), getString(R.string.closing_device));
                enableControl(false);
                int error;
                if (mTask != null && mTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mTask.cancel(false);
                    mTask.waitForDone();
                }
                if ((error = mScanner.close()) != CodeScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.hardware_barcode_scanner_close_failed), getHwBarcodeErrorString(error));
                } else {
                    showInfoToast(getString(R.string.hardware_barcode_scanner_close_success));
                }
                if ((error = mScanner.powerOff()) != CodeScanner.RESULT_OK) {
                    showErrorDialog(getString(R.string.hardware_barcode_scanner_power_off_failed), getHwBarcodeErrorString(error));
                }
                if (finish) {
                    finishActivity();
                }
                dismissProgressDialog();
            }
        }.start();
    }

    private void scanBarcode() {
        mTask = new QrcTask();
        mTask.execute("scan");
    }

    private void showBarcodeInfo(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_INFO, info));
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        String operation = args.getString("operation");
        String errString = args.getString("errString");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setTitle(R.string.info_error);
        if (errString != null && !errString.equals("")) {
            builder.setMessage(operation + "\n" + errString);
        } else {
            builder.setMessage(operation);
        }
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private void showProgressDialog(String title, String message) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_PROGRESS_DIALOG, new String[] { title, message }));
    }

    private void dismissProgressDialog() {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_DISMISS_PROGRESS_DIALOG));
    }

    private void showErrorDialog(String operation, String errString) {
        Bundle bundle = new Bundle();
        bundle.putString("operation", operation);
        bundle.putString("errString", errString);
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_ERROR, bundle));
    }

    private void showInfoToast(String info) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SHOW_INFO, info));
    }

    private String getHwBarcodeErrorString(int error) {
        int strid;
        switch (error) {
            case CodeScanner.RESULT_OK:
                strid = R.string.operation_successful;
                break;
            case CodeScanner.RESULT_FAIL:
                strid = R.string.error_operation_failed;
                break;
            case CodeScanner.WRONG_CONNECTION:
                strid = R.string.error_wrong_connection;
                break;
            case CodeScanner.DEVICE_BUSY:
                strid = R.string.error_device_busy;
                break;
            case CodeScanner.DEVICE_NOT_OPEN:
                strid = R.string.error_device_not_open;
                break;
            case CodeScanner.TIMEOUT:
                strid = R.string.error_timeout;
                break;
            case CodeScanner.NO_PERMISSION:
                strid = R.string.error_no_permission;
                break;
            case CodeScanner.WRONG_PARAMETER:
                strid = R.string.error_wrong_parameter;
                break;
            case CodeScanner.DECODE_ERROR:
                strid = R.string.error_decode;
                break;
            case CodeScanner.INIT_FAIL:
                strid = R.string.error_initialization_failed;
                break;
            case CodeScanner.UNKNOWN_ERROR:
                strid = R.string.error_unknown;
                break;
            case CodeScanner.NOT_SUPPORT:
                strid = R.string.error_not_support;
                break;
            case CodeScanner.NOT_ENOUGH_MEMORY:
                strid = R.string.error_not_enough_memory;
                break;
            case CodeScanner.DEVICE_NOT_FOUND:
                strid = R.string.error_device_not_found;
                break;
            case CodeScanner.DEVICE_REOPEN:
                strid = R.string.error_device_reopen;
                break;
            default:
                strid = R.string.error_other;
                break;
        }
        return getString(strid);
    }

    private class QrcTask extends AsyncTask<String, Integer, Void> {
        private boolean mIsDone = false;

        @Override
        protected void onPreExecute() {
            enableControl(false);
            showBarcodeInfo("");
        }

        @Override
        protected Void doInBackground(String... params) {
            Result res;
            if (params[0].equals("scan")) {
                showProgressDialog(getString(R.string.loading), getString(R.string.align_barcode));
                do {
                    res = mScanner.read();
                } while (res.error == CodeScanner.TIMEOUT && !isCancelled());
                if (!isCancelled()) {
                    if (res.error == CodeScanner.RESULT_OK) {
                        // Below is a sample for data processing, you need to process these data, for example, decode or save.
                        try {
                            showBarcodeInfo(new String((byte[]) res.data, "UTF-8").trim());
                            showInfoToast(getString(R.string.barcode_scan_success));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            showErrorDialog(getString(R.string.unsupported_encoding), null);
                        }
                    } else {
                        showErrorDialog(getString(R.string.barcode_scan_failed), getHwBarcodeErrorString(res.error));
                    }
                }
                dismissProgressDialog();
            }
            enableControl(true);
            mIsDone = true;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onCancelled() {
        }

        public void waitForDone() {
            while (!mIsDone) {
            }
        }
    }
}
