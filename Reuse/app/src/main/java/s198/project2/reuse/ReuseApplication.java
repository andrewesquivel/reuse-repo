package s198.project2.reuse;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Initialize Firebase with the application context. This must happen before the client is used.
 */
public class ReuseApplication extends Application {
    public static final String FIREBASE_URL = "https://reuse-app.firebaseio.com";

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}