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

## Create a Non EMV Transaction

```javascript
  window.AuthorizeNetPlugin.createNonEMVTransaction({
    id_tech_blob: 'IDtechBlob',
    device_info: '4649443d49...',
    amount: 10.00,
    itens: [{
      id: '1',
      name: 'foo',
      quantity: 1,
      taxable: false,
      description: 'Small and pretty.',
      price: 10.00
    }]
  }, (sucess) => {
    alert("EMVT sucesso!")
  }, (err) => {
    alert(err);
  })

```

## Create a EMV Transaction

```javascript
  window.AuthorizeNetPlugin.createEMVTransaction({
    solution_id: 'solution_id',
    amount: 10.00,
    itens: [{
      id: '1',
      name: 'foo',
      quantity: 1,
      taxable: false,
      description: 'Small and pretty.',
      price: 10.00
    }]
  }, (sucess) => {
    alert("EMVT sucesso!")
  }, (err) => {
    alert(err);
  })

```