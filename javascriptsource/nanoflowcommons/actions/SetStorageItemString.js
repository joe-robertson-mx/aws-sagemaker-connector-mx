import AsyncStorage from '@react-native-community/async-storage';

// This file was generated by Mendix Studio Pro.
/**
 * Store a string value in the device storage, identified by a unique key. Can be accessed by the GetStorageItemObject action. Please note that users can clear the device storage.
 * @param {string} key - This field is required.
 * @param {string} value - This field is required.
 * @returns {Promise.<void>}
 */
async function SetStorageItemString(key, value) {
    // BEGIN USER CODE
    if (!key) {
        return Promise.reject(new Error("Input parameter 'Key' is required"));
    }
    if (!value) {
        return Promise.reject(new Error("Input parameter 'Value' is required"));
    }
    return setItem(key, value);
    function setItem(key, value) {
        if (navigator && navigator.product === "ReactNative") {
            return AsyncStorage.setItem(key, value);
        }
        if (window) {
            window.localStorage.setItem(key, value);
            return Promise.resolve();
        }
        return Promise.reject(new Error("No storage API available"));
    }
    // END USER CODE
}

export { SetStorageItemString };
