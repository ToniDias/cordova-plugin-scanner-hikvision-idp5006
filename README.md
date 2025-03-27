Cordova Plugin For Hikvision idp5006 Scanner
======
[Cordova][cordova] plugin to manage the scanner on a Hikvision idp5006 device.
## Installation

The plugin can be installed via [Cordova-CLI][CLI].

Execute from the projects root folder:

```bash
    cordova plugin add https://github.com/ToniDias/cordova-plugin-scanner-hikvision-idp5006.git
```

## Remove
To remove the plugin

```bash
    cordova plugin remove cordova-plugin-scanner-hikvision-idp5006
```

## Usage

The plugin creates the object `ScanPlugin` and is accessible after *deviceready* has been fired.

```js
document.addEventListener('deviceready', function () {
    // ScanPlugin is now available
}, false);
```

### Enable/Disable the scanner
When the scanner is enable, it allows you to trigger the scan with the hardware key
```js
// Enable
ScanPlugin.openScanner();

// Disable
ScanPlugin.closeScanner();
```
To catch the barcode you should set a callback with the register function
```js
// Register a callback to manage the scan object
ScanPlugin.register(
  function (barcode) {
    console.log(barcode) // What barcode was scanned
  },
  function (error) {
    console.log(error) // Catch the error
  }
)
```
When the callback is fired, you have to set it again to catch the next barcode
### Trigger manually the scanner
With that function you can trigger the scan by software
```js
// Trigger the scanner manually (by software)
ScanPlugin.startDecode(
  function (barcode) {
   console.log(barcode) // What barcode was scanned
  },
  function (error) {
   console.log(error) // Catch the error
  }
)

// Stop the scanner
ScanPlugin.stopDecode()
```

## Testing on Device

Here is the step-by-step guide to test the plugin on a Hikvision MV-IDP5006 scanner.

### 1. Remove previous plugin (if present)

```bash
cordova plugin remove cordova-plugin-...
```

### 2. Add the Hikvision plugin

```bash
    cordova plugin add ./plugins/cordova-plugin-scanner-hikvision-idp5006
```

### 3. Re Build the app

```bash
  cordova platform rm android
  cordova platform add android
  cordova build android
```

### 4. Run the app on the device

```bash
  adb install platforms/android/app/build/outputs/apk/debug/app-debug.apk
```
Or copy and install it manually on the device.

### 5. Test the plugin and debug

```bash
  adb logcat | grep "ScanPlugin"
```

[cordova]: https://cordova.apache.org
[CLI]: http://cordova.apache.org/docs/en/edge/guide_cli_index.md.html#The%20Command-line%20Interface

# License
This software is released under the Apache 2.0 License.

© 2025
