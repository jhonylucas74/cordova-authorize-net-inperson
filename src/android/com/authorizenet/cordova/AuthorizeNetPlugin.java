/**
 */
package com.authorizenet.cordova;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import net.authorize.Environment;
import net.authorize.Merchant;
import net.authorize.TransactionType;
import net.authorize.aim.cardpresent.DeviceType;
import net.authorize.aim.cardpresent.MarketType;
import net.authorize.aim.emv.EMVTransaction;
import net.authorize.aim.emv.EMVTransactionManager;
import net.authorize.auth.PasswordAuthentication;
import net.authorize.auth.SessionTokenAuthentication;
import net.authorize.data.Order;
import net.authorize.data.OrderItem;
import net.authorize.data.creditcard.CreditCard;
import net.authorize.data.creditcard.CreditCardPresenceType;
import net.authorize.data.mobile.MobileDevice;
import net.authorize.mobile.Transaction;

import java.math.BigDecimal;

public class AuthorizeNetPlugin extends CordovaPlugin {
  private static final String TAG = "AuthorizeNetPlugin";
  private static Merchant merchant;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Initializing AuthozizeNetPlugin");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

    if(action.equals("initMerchant")){
      initMerchant(args, callbackContext);
      return true;
    } else if (action.equals("createEMVTransaction")) {
      createEMVTransaction(args, callbackContext);
      return true;
    } else if (action.equals("createNonEMVTransaction")) {
      createNonEMVTransaction(args, callbackContext);
      return true;      
    }
    return true;
  }


  private Transaction createLoginTransaction(){
    return this.merchant.createMobileTransaction(net.authorize.mobile.TransactionType.MOBILE_DEVICE_LOGIN);
  }

  private void setEnvironment(String environment, PasswordAuthentication passAuth){
      if(environment.equals("sandbox")) {
        this.merchant = Merchant.createMerchant(Environment.SANDBOX, passAuth);
      } else if (environment.equals("production")){
        this.merchant = Merchant.createMerchant(Environment.PRODUCTION, passAuth);
      }
  }

 
  public void initMerchant(JSONArray args, CallbackContext callbackContext) throws JSONException {
    net.authorize.mobile.Result result;

    String deviceID = args.getString(0);
    String deviceDescription = args.getString(1);
    String deviceNumber = args.getString(2);
    String username = args.getString(3);
    String password = args.getString(4);
    String environment = args.getString(5);
    

    PasswordAuthentication passAuth = PasswordAuthentication
      .createMerchantAuthentication(username, password, deviceID);

    setEnvironment(environment, passAuth);

    Transaction transaction = createLoginTransaction();

    MobileDevice mobileDevice = MobileDevice
            .createMobileDevice(deviceID, deviceDescription, deviceNumber, "Android");
    transaction.setMobileDevice(mobileDevice);
    
    result = (net.authorize.mobile.Result) this.merchant.postTransaction(transaction);

    if(result.isOk()){
      try {
          SessionTokenAuthentication sessionTokenAuthentication = SessionTokenAuthentication
                  .createMerchantAuthentication(this.merchant
                          .getMerchantAuthentication().getName(), result
                          .getSessionToken(), deviceID);

          if ((result.getSessionToken() != null) && (sessionTokenAuthentication != null)) {
              this.merchant.setMerchantAuthentication(sessionTokenAuthentication);
              callbackContext.success("sucesso");
          }
      } catch (Exception ex) {
         callbackContext.error(ex.toString());
      }
    } else {
      callbackContext.error(result.getXmlResponse().toString());
    }
  }

  /* Create and Submit an EMV Transaction */
  public void createEMVTransaction(JSONArray args, CallbackContext callbackContext){

  }
  /* Create Non-EMV transaction Using Encrypted Swiper Data */
  public void createNonEMVTransaction(JSONArray args, final CallbackContext callbackContext){
  }
}
