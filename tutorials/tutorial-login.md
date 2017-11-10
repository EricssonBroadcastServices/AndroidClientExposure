# Login

First, it is necessary to bind the Context to our library.
Second, it is necessary to tell the library the backend **ApiUrl**, **Customer** and **BusinessUnit**.


```java
// ...
import net.ericsson.emovs.utilities.ContextRegistry;
import net.ericsson.emovs.exposure.auth.EMPAuthProviderWithStorage;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ContextRegistry.bind(this);
        EMPAuthProvider authProvider = EMPAuthProviderWithStorage.getInstance(Constants.API_URL, Constants.CUSTOMER, Constants.BUSSINESS_UNIT);
		// ...
	}
	
	// ...
}
```

Our library provides an easy way to check if the user is logged in and if the session is still valid. Based on this result, the app should show the login form or not.

```java
authProvider.checkAuth(new IAuthenticationListener() {
	@Override
	public void onAuthSuccess(String sessionToken) {
		// Although the sessionToken is passed, it is not really necessary to keep it in most scenarios as the library takes care of the token lifecycle
	}

	@Override
	public void onAuthError(ExposureError error) {
		// Perform login if error means user is not authenticated
	}
});
```

If the user is not logged in, then the **EMPAuthProvider** exposes the **login** method. If your login does not require 2-factor auth, then pass null in the corresponsing field.


```java
authProvider.login(isPersistentLoginBoolean, usernameString, passwordString, twoFactorAuthCodeString, new IAuthenticationListener() {
	@Override
	public void onAuthSuccess(String sessionToken) {
		// Perfect!
	}

	@Override
	public void onAuthError(ExposureError error) {
		// Ups, handle error!
	}
});
```