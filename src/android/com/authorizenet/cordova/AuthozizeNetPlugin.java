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

import java.math.BigDecimal;

public class AuthozizeNetPlugin extends CordovaPlugin {
  private static final String TAG = "AuthozizeNetPlugin";

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Initializing AuthozizeNetPlugin");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    switch(action){
      case "createEMVTransaction":
        createEMVTransaction(args);
        break;
      case "createNonEMVTransaction":
        createNonEMVTransaction(args);
        break;
    }

    return true;
  }

  /* Create and Submit an EMV Transaction */
  public void createEMVTransaction(JSONArray args, CallbackContext callbackContext) {
    net.authorize.mobile.Result result;
    String deviceID = args.getString("device_id");
    String deviceDescription = args.getString("device_description");
    String deviceNumber = args.getString("device_number");

    String username = args.getString("username");
    String password = args.getString("password");
    String environment = args.getString("environment");
    

    PasswordAuthentication passAuth = PasswordAuthentication
      .createMerchantAuthentication(username, password, deviceID);
    
    switch(environment){
      case "sandbox":
        AppManager.merchant = Merchant.createMerchant(Environment.SANDBOX, passAuth);
        break;
      case "production":
        AppManager.merchant = Merchant.createMerchant(Environment.PRODUCTION, passAuth);
        break;
    }

    net.authorize.mobile.Transaction transaction = AppManager.merchant
            .createMobileTransaction(net.authorize.mobile.TransactionType.MOBILE_DEVICE_LOGIN);
    MobileDevice mobileDevice = MobileDevice
            .createMobileDevice(deviceID, deviceDescription, deviceNumber, "Android");
    transaction.setMobileDevice(mobileDevice);
    
    result = (net.authorize.mobile.Result) AppManager.merchant.postTransaction(transaction);

    if(result.isOk()){
      try {
          SessionTokenAuthentication sessionTokenAuthentication = SessionTokenAuthentication
                  .createMerchantAuthentication(AppManager.merchant
                          .getMerchantAuthentication().getName(), result
                          .getSessionToken(), deviceID);
          if ((result.getSessionToken() != null) && (sessionTokenAuthentication != null)) {
              AppManager.merchant.setMerchantAuthentication(sessionTokenAuthentication);

              callbackContext.success("sucesso");
          }
      } catch (Exception ex) {
         callbackContext.error("erro");
      }
    } else {
         callbackContext.error("erro");
        // Log.e("EMVResponse",result.getXmlResponse());
    }

    
  }

  /* Create Non-EMV transaction Using Encrypted Swiper Data */
  public void createNonEMVTransaction(){

  }
}
