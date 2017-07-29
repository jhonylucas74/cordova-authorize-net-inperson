# Cordova Plugin Authorize Net Inperson

To install this plugin enter in your project folder and insert this comand in terminal.

``cordova plugin add https://github.com/jhonylucas74/cordova-authorize-net-inperson``

### Methods to interact with Authorize Net Inperson

### initMerchant

The function of this method is create a merchant. To do this is necessary username, password and some few arguments.

``javascript 
  AuthorizeNetPlugin.initMerchant(options, sucess, error);
``

For options all arguments are necessary:
* device_id
* device_description
* deviceNumber
* username
* password
* environment

For Environment use:
*  `` AuthorizeNetPlugin.Environment.SANDBOX ``
* `` AuthorizeNetPlugin.Environment.PRODUCTION ``

Example of use:


```javascript
  var Environment = window.AuthorizeNetPlugin.Environment;

  window.AuthorizeNetPlugin.initMerchant({ 
    device_id: 'Test EMV Android',
    device_description: 'Device description',
    device_number: '425-555-0000',
    username: 'username',
    password: 'password',
    environment: Environment.SANDBOX
  }, (msg) => {
    alert(msg);
  }, (err) => {
    alert(err);
  });
```
Remember, use this only after device is ready.