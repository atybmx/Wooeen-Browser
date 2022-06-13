package com.wooeen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.scottyab.aescrypt.AESCrypt;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;

import org.chromium.base.StrictModeContext;
import org.chromium.base.task.AsyncTask;

import java.security.GeneralSecurityException;
import java.util.Locale;
import java.util.TimeZone;

public class UserUtils {

    public static UserTO newInstance(Context context) {
        return newInstance(context, new UserTO());
    }

    public static UserTO newInstance(Context context,UserTO mUser){
        mUser.setTimezone(TimeZone.getDefault().getID());

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }

        if(locale != null){
            mUser.setLanguage(locale.getLanguage()+"_"+locale.getCountry());
        }

        return mUser;
    }

    public static void logout(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.clear();
            tokenEditor.apply();

            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.clear();
            userEditor.apply();
        }
    }

    public static void saveUserData(Context context,UserTO mUser, UserTokenTO token){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            String tokenIdCrypt = token.getIdToken();
            String tokenAccessCrypt = token.getAccessToken();
            try {
                tokenIdCrypt = AESCrypt.encrypt(
                        mUser.getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw" + mUser.getId(),
                        tokenIdCrypt);
                tokenAccessCrypt = AESCrypt.encrypt(
                        mUser.getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw" + mUser.getId(),
                        tokenAccessCrypt);
            } catch (GeneralSecurityException e) {
                tokenIdCrypt = token.getIdToken();
                tokenAccessCrypt = token.getAccessToken();
            }

            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putString("i", tokenIdCrypt);
            tokenEditor.putString("a", tokenAccessCrypt);
            tokenEditor.apply();

            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putInt("id", mUser.getId());
            if (!TextUtils.isEmpty(mUser.getName())) userEditor.putString("name",
                    mUser.getName());
            if (TextUtils.isEmailValid(mUser.getEmail())) userEditor.putString("email",
                    mUser.getEmail());
            if (!TextUtils.isEmpty(mUser.getPhone())) userEditor.putString("phone",
                    mUser.getPhone());
            if (mUser.getCountry() != null && !TextUtils.isEmpty(mUser.getCountry().getId())) userEditor.putString("country",
                    mUser.getCountry().getId());
            if (!TextUtils.isEmpty(mUser.getLanguage())) userEditor.putString("language",
                    mUser.getLanguage());
            if (!TextUtils.isEmpty(mUser.getTimezone())) userEditor.putString("timezone",
                    mUser.getTimezone());
            if (mUser.getRecTerms()) userEditor.putBoolean("rec_terms", mUser.getRecTerms());
            userEditor.apply();
        }
    }

    public static void saveUserData(Context context,UserTO mUser){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            if (!TextUtils.isEmpty(mUser.getName())) userEditor.putString("name",
                    mUser.getName());
            if (mUser.getCountry() != null && !TextUtils.isEmpty(mUser.getCountry().getId())) userEditor.putString("country",
                    mUser.getCountry().getId());
            if (!TextUtils.isEmpty(mUser.getLanguage())) userEditor.putString("language",
                    mUser.getLanguage());
            if (!TextUtils.isEmpty(mUser.getTimezone())) userEditor.putString("timezone",
                    mUser.getTimezone());
            if (mUser.getRecTerms()) userEditor.putBoolean("rec_terms", mUser.getRecTerms());
            userEditor.apply();
        }
    }

    public static int getUserId(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("id", 0);
        }
    }

    public static String getUserCr(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getString("country", null);
        }
    }

    public static void saveCrData(Context context,String country){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.cr",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            if (!TextUtils.isEmpty(country)) userEditor.putString("id",
                    country);
            userEditor.apply();
        }
    }

    public static String getCr(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.cr",
                    Context.MODE_PRIVATE);
            return userPreferences.getString("id", "BR");
        }
    }

    public static String getCrFinal(Context context){
        String userCr = getUserCr(context);
        if(!TextUtils.isEmpty(userCr))
            return userCr;
        else
            return getCr(context);
    }

    public static void saveCountryData(Context context, CountryTO mCountry){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences countryPreferences = context.getSharedPreferences("com.wooeen.country",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor countryEditor = countryPreferences.edit();
            if(mCountry == null){
                countryEditor.clear();
                countryEditor.commit();
            }else{
                if (!TextUtils.isEmpty(mCountry.getId())) countryEditor.putString("id",
                        mCountry.getId());
                if (!TextUtils.isEmpty(mCountry.getName())) countryEditor.putString("name",
                        mCountry.getName());
                if (!TextUtils.isEmpty(mCountry.getLanguage())) countryEditor.putString("language",
                        mCountry.getLanguage());
                if (!TextUtils.isEmpty(mCountry.getCurrency())) countryEditor.putString("currency",
                        mCountry.getCurrency());
                countryEditor.putBoolean("load_posts",mCountry.getLoadPosts());
                countryEditor.putBoolean("load_offers",mCountry.getLoadOffers());
                countryEditor.putBoolean("load_coupons",mCountry.getLoadCoupons());
                countryEditor.putBoolean("load_tasks",mCountry.getLoadTasks());
                countryEditor.apply();
            }
        }
    }

    public static CountryTO getCountry(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences countryPreferences = context.getSharedPreferences("com.wooeen.country",
                    Context.MODE_PRIVATE);


            String id = countryPreferences.getString("id", null);
            if(TextUtils.isEmpty(id))
                return null;

            CountryTO country = new CountryTO();

            country.setId(id);
            country.setName(countryPreferences.getString("name", null));
            country.setLanguage(countryPreferences.getString("language", null));
            country.setCurrency(countryPreferences.getString("currency", null));
            country.setLoadPosts(countryPreferences.getBoolean("load_posts", false));
            country.setLoadOffers(countryPreferences.getBoolean("load_offers", false));
            country.setLoadCoupons(countryPreferences.getBoolean("load_coupons", false));
            country.setLoadTasks(countryPreferences.getBoolean("load_tasks", false));

            return country;
        }
    }

    public static void saveUserFcmToken(Context context,String fcmToken){
        if(TextUtils.isEmpty(fcmToken))
            return;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putString("fcm_token", fcmToken);
            userEditor.apply();

            //set in dao
            new UserSetFcmToken(context, fcmToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserSetFcmToken extends AsyncTask<Boolean> {

        private Context context;
        private String fcmToken;

        public UserSetFcmToken(Context context,String fcmToken){
            this.context = context;
            this.fcmToken = fcmToken;
        }

        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getToken(context));
            return apiDAO.setFcmToken(fcmToken);
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    public static String getUserFcmToken(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getString("fcm_token", null);
        }
    }

    public static void saveUserRecTerms(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean("rec_terms", true);
            userEditor.apply();

            //set in dao
            new UserSetRecTerms(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserSetRecTerms extends AsyncTask<Boolean> {

        private Context context;

        public UserSetRecTerms(Context context){
            this.context = context;

        }

        @Override
        protected Boolean doInBackground() {
            UserTO user = new UserTO();
            user.setRecTerms(true);
            user.setRecTermsIp(DeviceUtils.getHostAddress());

            UserAPI apiDAO = new UserAPI(UserUtils.getToken(context));
            return apiDAO.setRecTerms(user);
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    public static boolean getUserRecTerms(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getBoolean("rec_terms", false);
        }
    }

    public static UserTokenTO getToken(Context context){
        int userId = getUserId(context);
        if(userId <= 0)
            return null;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            String tokenIdCrypt = tokenPreferences.getString("i", null);
            String tokenAccessCrypt = tokenPreferences.getString("a", null);

            if (TextUtils.isEmpty(tokenIdCrypt) || TextUtils.isEmpty(tokenAccessCrypt))
                return null;

            UserTokenTO token = new UserTokenTO();
            try {
                token.setIdToken(
                        AESCrypt.decrypt(userId + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw" + userId,
                                tokenIdCrypt));
                token.setAccessToken(
                        AESCrypt.decrypt(userId + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw" + userId,
                                tokenAccessCrypt));
            } catch (GeneralSecurityException e) {
                token.setIdToken(tokenIdCrypt);
                token.setAccessToken(tokenAccessCrypt);
            }

            return token;
        }
    }
}
