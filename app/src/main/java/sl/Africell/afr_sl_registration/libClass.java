package sl.Africell.afr_sl_registration;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.net.HttpURLConnection;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;
import static sl.Africell.afr_sl_registration.apiUrl.baseUrl;

/**
 * Created by pmvungu on 7/10/2018.
 */



import java.security.cert.Certificate;
import java.security.cert.CertificateException;
public class libClass {
    private final Context context;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static final int REQUEST_CODE=101;
    public static final int RequestPermissionCode = 1;
    private String pathUrl;
    public String strResult;
    public libClass(Context ctx)
    {
        this.context = ctx;


    }
    public boolean checkPermission() {
try {
    int FirstPermissionResult = ContextCompat.checkSelfPermission(context, CAMERA);
    //int SecondPermissionResult = ContextCompat.checkSelfPermission(context, READ_CONTACTS);
    int ThirdPermissionResult = ContextCompat.checkSelfPermission(context, READ_PHONE_STATE);
    int FourthPermissionResult = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
    int fivethPermissionResult = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);

    return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
            //SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
            ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
            FourthPermissionResult == PackageManager.PERMISSION_GRANTED &&
            fivethPermissionResult == PackageManager.PERMISSION_GRANTED;

}catch (Exception e){
    Log.e(e.getMessage(),"");
    return false;

}
    }
    public void requestPermission(Activity activity) {
        try {
            ActivityCompat.requestPermissions(activity, new String[]
                    {
                            CAMERA,
                            READ_CONTACTS,
                            READ_PHONE_STATE,
                            ACCESS_FINE_LOCATION,
                            WRITE_EXTERNAL_STORAGE

                    }, RequestPermissionCode);
        }catch (Exception e){
           e.getMessage();

        }
    }
    public void popMessage(String strTitle, String strMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(strMessage);
        builder.setTitle(strTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


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
    public String getMyCellId(Activity activity,TelephonyManager telephony)
    {

       // final TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Toast.makeText(this,"permission ok ACCESS_FINE_LOCATION",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return "";
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

            //Toast.makeText(this,"permission ok READ_PHONE_STATE",Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
            return "";
        }


        if (ContextCompat.checkSelfPermission(context , Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM)
            {
                final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
                if (location != null)
                {
                    return location.getLac() + "-" + String.valueOf(location.getCid() & 0xffff);
                }
                else
                {

                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE);
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
    public String parseXmltoString(String strXML,String TagRowItem,String eltChildvalue){
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        String resultstrText="";
        try {

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource ipsource = new InputSource();
            ipsource.setCharacterStream(new StringReader(strXML));
            doc = dBuilder.parse(ipsource);

            NodeList nl = doc.getElementsByTagName(TagRowItem);  //Tag RowItem : siteRecord

            for (int i = 0; i < nl.getLength(); i++) {
                Element e = (Element) nl.item(i);
                String strObj = getValue(eltChildvalue,e); // SITENAME : siteName child value
                // String xcost = getValue(e, "BirthDate"); // xcost child value
                if (!strObj.trim().equals("")){
                    if (!resultstrText.trim().equals("")){
                        resultstrText = resultstrText +";"+ strObj ;
                    }else{
                        resultstrText =  strObj  ;
                    }
                }
            }

            return resultstrText;

        }catch (Exception e) {
            e.printStackTrace();
            return "unsucess request.";
        }
    }
    public static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        try{
        Node node = nodeList.item(0);
        return node.getNodeValue();}catch (Exception e){
            return "";
        }
    }
    public String getValues(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }
    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
