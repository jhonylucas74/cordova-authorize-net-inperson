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
  private static Merchant merchant;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Initializing AuthozizeNetPlugin");
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    switch(action){
      case "initMerchant":
        initMerchant(args, callbackContext);
        break;
      case "createEMVTransaction":
        createEMVTransaction(args, callbackContext);
        break;
      case "createNonEMVTransaction":
        createNonEMVTransaction(args, callbackContext);
        break;
    }

    return true;
  }


  private Transaction createLoginTransaction(){
    return this.merchant.createMobileTransaction(TransactionType.MOBILE_DEVICE_LOGIN);
  }

  private void setEnvironment(String environment, PasswordAuthentication passAuth){
    switch(environment) {
      case "sandbox":
        this.merchant = Merchant.createMerchant(Environment.SANDBOX, passAuth);
        break;
      case "production":
        this.merchant = Merchant.createMerchant(Environment.PRODUCTION, passAuth);
        break;
    }
  }

 
  public void initMerchant(JSONArray args, CallbackContext callbackContext) {
    net.authorize.mobile.Result result;
    String deviceID = args.getString("device_id");
    String deviceDescription = args.getString("device_description");
    String deviceNumber = args.getString("device_number");
    String username = args.getString("username");
    String password = args.getString("password");
    String environment = args.getString("environment");
    

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
  public void createNonEMVTransaction(JSONArray args, CallbackContext callbackContext){
    EMVTransactionManager.EMVTransactionListener iemvTransaction = 
      new EMVTransactionManager.EMVTransactionListener() {
          @Override
          public void onEMVTransactionSuccessful(net.authorize.aim.emv.Result result) {
            callbackContext.success(result.getEmvTlvMap().toString());
          }

          @Override
          public void onEMVReadError(EMVErrorCode emvError) {
             callbackContext.error(emvError.getErrorString());
          }

          @Override
          public void onEMVTransactionError(net.authorize.aim.emv.Result result, EMVErrorCode emvError) {
            callbackContext.error(emvError.getErrorString());
          }
      };

    Order order = Order.createOrder();
    OrderItem oi = OrderItem.createOrderItem();
    oi.setItemId("1");
    oi.setItemName("name");

    oi.setItemQuantity("1");
    oi.setItemTaxable(false);
    oi.setItemDescription("desc");
    oi.setItemDescription("Goods");

    order.addOrderItem(oi);
    BigDecimal transAmount = new BigDecimal("1.20");
    oi.setItemPrice(transAmount);
    order.setTotalAmount(transAmount);
    EMVTransaction emvTransaction = EMVTransactionManager.createEMVTransaction(AppManager.merchant, transAmount);
    emvTransaction.setEmvTransactionType(EMVTransactionType.GOODS);
    emvTransaction.setOrder(order);
    emvTransaction.setSolutionID("SOLUTION ID");
    EMVTransactionManager.startEMVTransaction(emvTransaction, iemvTransaction, context);
  }
}
