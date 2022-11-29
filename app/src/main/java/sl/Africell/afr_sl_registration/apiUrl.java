package sl.Africell.afr_sl_registration;

/**
 *//**patou diasivi lasted update : 12:00 2020-12-15 **/


public class apiUrl {
    public String msisdn;
    public boolean postpaid = false;
    private String authpin;

    //APIs URLs//
     public static String baseUrl="http://87.238.116.200/";
    //public static String baseUrl="http://10.100.16.84:8282/";
    protected String awun = "$@";
    protected String awp = "@auth@$@";

    public apiUrl(String Msisdn){
        msisdn = Msisdn;
    }

}
