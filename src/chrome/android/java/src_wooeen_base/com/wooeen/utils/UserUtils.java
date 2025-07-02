package com.wooeen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.Nullable;

import com.scottyab.aescrypt.AESCrypt;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.UserInstallAPI;
import com.wooeen.model.api.UtilsAPI;
import com.wooeen.model.to.CategoryTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.CurrencyTO;
import com.wooeen.model.to.MediaTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.model.to.VersionTO;
import com.wooeen.model.to.WalletTO;
import com.wooeen.model.to.WoeTrkClickTO;

import org.chromium.base.StrictModeContext;
import org.chromium.base.task.AsyncTask;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UserUtils {

    public static boolean mLoginWeb = false;

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

            if(!TextUtils.isEmpty(locale.getCountry())){
                mUser.setCountry(new CountryTO(locale.getCountry()));
            }
        }

        new LoadCountry(context, mUser).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return mUser;
    }

    public static class LoadCountry extends AsyncTask<String> {

        private Context context;
        private UserTO mUser;
        private WoeTrkClickTO click;

        public LoadCountry(Context context, UserTO mUser){
            this.context = context;
            this.mUser = mUser;

            //get the click
            click = WoeTrkUtils.getClick(context);
        }

        @Nullable
        @Override
        protected String doInBackground() {
            //attribute the click
            if(click == null){
                click = new WoeTrkClickTO();
            }

            if(!TextUtils.isEmpty(click.getCountry())){
                mUser.setCountry(new CountryTO(TextUtils.getLimit(click.getCountry(),2).toUpperCase()));
            }

            //try to get the country from ip
            if(mUser.getCountry() == null || TextUtils.isEmpty(mUser.getCountry().getId())) {
                UtilsAPI.IpInfo ipInfo = UtilsAPI.getIpInfo();
                if (ipInfo != null)
                    mUser.setCountry(new CountryTO(ipInfo.getCountrycode()));
            }

            if(mUser.getCountry() == null || TextUtils.isEmpty(mUser.getCountry().getId())) {
                mUser.setCountry(new CountryTO("BR"));
                return null;
            }

            return mUser.getCountry().getId();
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    public static UserTO newCompany(Context context) {
        return newCompany(context, new UserTO());
    }

    public static UserTO newCompany(Context context,UserTO mCompany){
        UserTO mUser = getUser(context);

        mCompany.setTimezone(mUser.getTimezone());
        mCompany.setLanguage(mUser.getLanguage());
        mCompany.setCountry(mUser.getCountry());

        return mCompany;
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

            SharedPreferences companyPreferences = context.getSharedPreferences("com.wooeen.cp",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor companyEditor = companyPreferences.edit();
            companyEditor.clear();
            companyEditor.apply();
        }
    }

    public static void saveUserData(Context context,UserTO mUser, UserTokenTO token) {
        saveUserData(context, mUser, token, null);
    }

    public static void saveUserData(Context context,UserTO mUser, UserTokenTO token, UserTokenTO tokenCompany){
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

            String tokenCpIdCrypt = "";
            String tokenCpAccessCrypt = "";
            if(mUser.getCompany() != null && mUser.getCompany().getId() > 0 && tokenCompany != null) {
                tokenCpIdCrypt = tokenCompany.getIdToken();
                tokenCpAccessCrypt = tokenCompany.getAccessToken();
                try {
                    tokenCpIdCrypt = AESCrypt.encrypt(
                            mUser.getCompany().getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw"
                                    + mUser.getCompany().getId(),
                            tokenCpIdCrypt);
                    tokenCpAccessCrypt = AESCrypt.encrypt(
                            mUser.getCompany().getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw"
                                    + mUser.getCompany().getId(),
                            tokenCpAccessCrypt);
                } catch (GeneralSecurityException e) {
                    tokenCpIdCrypt = tokenCompany.getIdToken();
                    tokenCpAccessCrypt = tokenCompany.getAccessToken();
                }
            }

            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("t", LOGGED_USER);
            tokenEditor.putString("i", tokenIdCrypt);
            tokenEditor.putString("a", tokenAccessCrypt);
            tokenEditor.putString("cpi",tokenCpIdCrypt);
            tokenEditor.putString("cpa",tokenCpAccessCrypt);
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
            if (mUser.getPhotoProfile() != null &&
                    !TextUtils.isEmpty(mUser.getPhotoProfile().getUrl()))
                userEditor.putString("photo_profile", mUser.getPhotoProfile().getUrl());
            if (mUser.getCountry() != null && !TextUtils.isEmpty(mUser.getCountry().getId())) userEditor.putString("country",
                    mUser.getCountry().getId());
            if (!TextUtils.isEmpty(mUser.getLanguage())) userEditor.putString("language",
                    mUser.getLanguage());
            if (!TextUtils.isEmpty(mUser.getTimezone())) userEditor.putString("timezone",
                    mUser.getTimezone());
            if (mUser.getRecTerms()) userEditor.putBoolean("rec_terms", mUser.getRecTerms());

            if(mUser.getWallet() != null){
                userEditor.putInt("w_conversions_pending", mUser.getWallet().getConversionsPending());
                userEditor.putInt("w_conversions_registered", mUser.getWallet().getConversionsRegistered());
                userEditor.putInt("w_conversions_approved", mUser.getWallet().getConversionsApproved());
                userEditor.putInt("w_conversions_rejected", mUser.getWallet().getConversionsRejected());
                userEditor.putInt("w_conversions_withdrawn", mUser.getWallet().getConversionsWithdrawn());

                userEditor.putFloat("w_amount_pending", (float) mUser.getWallet().getAmountPending());
                userEditor.putFloat("w_amount_registered", (float) mUser.getWallet().getAmountRegistered());
                userEditor.putFloat("w_amount_approved", (float) mUser.getWallet().getAmountApproved());
                userEditor.putFloat("w_amount_rejected", (float) mUser.getWallet().getAmountRejected());
                userEditor.putFloat("w_amount_withdrawn", (float) mUser.getWallet().getAmountWithdrawn());

                userEditor.putInt("w_aff_conversions_pending", mUser.getWallet().getAffConversionsPending());
                userEditor.putInt("w_aff_conversions_registered", mUser.getWallet().getAffConversionsRegistered());
                userEditor.putInt("w_aff_conversions_approved", mUser.getWallet().getAffConversionsApproved());
                userEditor.putInt("w_aff_conversions_rejected", mUser.getWallet().getAffConversionsRejected());
                userEditor.putInt("w_aff_conversions_withdrawn", mUser.getWallet().getAffConversionsWithdrawn());

                userEditor.putFloat("w_aff_amount_pending", (float) mUser.getWallet().getAffAmountPending());
                userEditor.putFloat("w_aff_amount_registered", (float) mUser.getWallet().getAffAmountRegistered());
                userEditor.putFloat("w_aff_amount_approved", (float) mUser.getWallet().getAffAmountApproved());
                userEditor.putFloat("w_aff_amount_rejected", (float) mUser.getWallet().getAffAmountRejected());
                userEditor.putFloat("w_aff_amount_withdrawn", (float) mUser.getWallet().getAffAmountWithdrawn());

                userEditor.putInt("w_recommendations_registered", mUser.getWallet().getRecommendationsRegistered());
                userEditor.putInt("w_recommendations_converted", mUser.getWallet().getRecommendationsConverted());
                userEditor.putInt("w_recommendations_confirmed", mUser.getWallet().getRecommendationsConfirmed());

                userEditor.putFloat("w_recommendations_registered_amount", (float) mUser.getWallet().getRecommendationsRegisteredAmount());
                userEditor.putFloat("w_recommendations_converted_amount", (float) mUser.getWallet().getRecommendationsConvertedAmount());
                userEditor.putFloat("w_recommendations_confirmed_amount", (float) mUser.getWallet().getRecommendationsConfirmedAmount());

                userEditor.putFloat("w_balance", (float) mUser.getWallet().getBalance());
            }

            userEditor.apply();

            if(mUser.getCompany() != null && mUser.getCompany().getId() > 0) {
                SharedPreferences companyPreferences = context.getSharedPreferences("com.wooeen.cp",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor companyEditor = companyPreferences.edit();
                companyEditor.putInt("id", mUser.getCompany().getId());
                if (!TextUtils.isEmpty(mUser.getCompany().getName())) companyEditor.putString("name",
                        mUser.getCompany().getName());
                if (TextUtils.isEmailValid(mUser.getCompany().getEmail())) companyEditor.putString("email",
                        mUser.getCompany().getEmail());
                if (!TextUtils.isEmpty(mUser.getCompany().getPhone())) companyEditor.putString("phone",
                        mUser.getCompany().getPhone());
                if (!TextUtils.isEmpty(mUser.getCompany().getLanguage())) companyEditor.putString("language",
                        mUser.getCompany().getLanguage());
                if (!TextUtils.isEmpty(mUser.getCompany().getTimezone())) companyEditor.putString("timezone",
                        mUser.getCompany().getTimezone());
                if (mUser.getCompany().getRecTerms()) companyEditor.putBoolean("rec_terms", mUser.getCompany().getRecTerms());

                if(mUser.getCompany().getWallet() != null){
                    companyEditor.putInt("w_conversions_pending", mUser.getCompany().getWallet().getConversionsPending());
                    companyEditor.putInt("w_conversions_registered", mUser.getCompany().getWallet().getConversionsRegistered());
                    companyEditor.putInt("w_conversions_approved", mUser.getCompany().getWallet().getConversionsApproved());
                    companyEditor.putInt("w_conversions_rejected", mUser.getCompany().getWallet().getConversionsRejected());
                    companyEditor.putInt("w_conversions_withdrawn", mUser.getCompany().getWallet().getConversionsWithdrawn());

                    companyEditor.putFloat("w_amount_pending", (float) mUser.getCompany().getWallet().getAmountPending());
                    companyEditor.putFloat("w_amount_registered", (float) mUser.getCompany().getWallet().getAmountRegistered());
                    companyEditor.putFloat("w_amount_approved", (float) mUser.getCompany().getWallet().getAmountApproved());
                    companyEditor.putFloat("w_amount_rejected", (float) mUser.getCompany().getWallet().getAmountRejected());
                    companyEditor.putFloat("w_amount_withdrawn", (float) mUser.getCompany().getWallet().getAmountWithdrawn());

                    companyEditor.putInt("w_aff_conversions_pending", mUser.getCompany().getWallet().getAffConversionsPending());
                    companyEditor.putInt("w_aff_conversions_registered", mUser.getCompany().getWallet().getAffConversionsRegistered());
                    companyEditor.putInt("w_aff_conversions_approved", mUser.getCompany().getWallet().getAffConversionsApproved());
                    companyEditor.putInt("w_aff_conversions_rejected", mUser.getCompany().getWallet().getAffConversionsRejected());
                    companyEditor.putInt("w_aff_conversions_withdrawn", mUser.getCompany().getWallet().getAffConversionsWithdrawn());

                    companyEditor.putFloat("w_aff_amount_pending", (float) mUser.getCompany().getWallet().getAffAmountPending());
                    companyEditor.putFloat("w_aff_amount_registered", (float) mUser.getCompany().getWallet().getAffAmountRegistered());
                    companyEditor.putFloat("w_aff_amount_approved", (float) mUser.getCompany().getWallet().getAffAmountApproved());
                    companyEditor.putFloat("w_aff_amount_rejected", (float) mUser.getCompany().getWallet().getAffAmountRejected());
                    companyEditor.putFloat("w_aff_amount_withdrawn", (float) mUser.getCompany().getWallet().getAffAmountWithdrawn());

                    companyEditor.putInt("w_recommendations_registered", mUser.getCompany().getWallet().getRecommendationsRegistered());
                    companyEditor.putInt("w_recommendations_converted", mUser.getCompany().getWallet().getRecommendationsConverted());
                    companyEditor.putInt("w_recommendations_confirmed", mUser.getCompany().getWallet().getRecommendationsConfirmed());

                    companyEditor.putFloat("w_recommendations_registered_amount", (float) mUser.getCompany().getWallet().getRecommendationsRegisteredAmount());
                    companyEditor.putFloat("w_recommendations_converted_amount", (float) mUser.getCompany().getWallet().getRecommendationsConvertedAmount());
                    companyEditor.putFloat("w_recommendations_confirmed_amount", (float) mUser.getCompany().getWallet().getRecommendationsConfirmedAmount());

                    companyEditor.putFloat("w_balance", (float) mUser.getCompany().getWallet().getBalance());
                }

                companyEditor.apply();
            }
        }
    }

    public static void saveUserData(Context context,UserTO mUser){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            if (!TextUtils.isEmpty(mUser.getName())) userEditor.putString("name",
                    mUser.getName());
            if (!TextUtils.isEmpty(mUser.getEmail())) userEditor.putString("email",
                    mUser.getEmail());
            if (mUser.getPhotoProfile() != null &&
                    !TextUtils.isEmpty(mUser.getPhotoProfile().getUrl()))
                userEditor.putString("photo_profile", mUser.getPhotoProfile().getUrl());
            else
                userEditor.putString("photo_profile", "");
            if (mUser.getCountry() != null && !TextUtils.isEmpty(mUser.getCountry().getId())) userEditor.putString("country",
                    mUser.getCountry().getId());
            if (!TextUtils.isEmpty(mUser.getLanguage())) userEditor.putString("language",
                    mUser.getLanguage());
            if (!TextUtils.isEmpty(mUser.getTimezone())) userEditor.putString("timezone",
                    mUser.getTimezone());
            if (mUser.getRecTerms()) userEditor.putBoolean("rec_terms", mUser.getRecTerms());

            if(mUser.getWallet() != null){
                userEditor.putInt("w_conversions_pending", mUser.getWallet().getConversionsPending());
                userEditor.putInt("w_conversions_registered", mUser.getWallet().getConversionsRegistered());
                userEditor.putInt("w_conversions_approved", mUser.getWallet().getConversionsApproved());
                userEditor.putInt("w_conversions_rejected", mUser.getWallet().getConversionsRejected());
                userEditor.putInt("w_conversions_withdrawn", mUser.getWallet().getConversionsWithdrawn());

                userEditor.putFloat("w_amount_pending", (float) mUser.getWallet().getAmountPending());
                userEditor.putFloat("w_amount_registered", (float) mUser.getWallet().getAmountRegistered());
                userEditor.putFloat("w_amount_approved", (float) mUser.getWallet().getAmountApproved());
                userEditor.putFloat("w_amount_rejected", (float) mUser.getWallet().getAmountRejected());
                userEditor.putFloat("w_amount_withdrawn", (float) mUser.getWallet().getAmountWithdrawn());

                userEditor.putInt("w_aff_conversions_pending", mUser.getWallet().getAffConversionsPending());
                userEditor.putInt("w_aff_conversions_registered", mUser.getWallet().getAffConversionsRegistered());
                userEditor.putInt("w_aff_conversions_approved", mUser.getWallet().getAffConversionsApproved());
                userEditor.putInt("w_aff_conversions_rejected", mUser.getWallet().getAffConversionsRejected());
                userEditor.putInt("w_aff_conversions_withdrawn", mUser.getWallet().getAffConversionsWithdrawn());

                userEditor.putFloat("w_aff_amount_pending", (float) mUser.getWallet().getAffAmountPending());
                userEditor.putFloat("w_aff_amount_registered", (float) mUser.getWallet().getAffAmountRegistered());
                userEditor.putFloat("w_aff_amount_approved", (float) mUser.getWallet().getAffAmountApproved());
                userEditor.putFloat("w_aff_amount_rejected", (float) mUser.getWallet().getAffAmountRejected());
                userEditor.putFloat("w_aff_amount_withdrawn", (float) mUser.getWallet().getAffAmountWithdrawn());

                userEditor.putInt("w_recommendations_registered", mUser.getWallet().getRecommendationsRegistered());
                userEditor.putInt("w_recommendations_converted", mUser.getWallet().getRecommendationsConverted());
                userEditor.putInt("w_recommendations_confirmed", mUser.getWallet().getRecommendationsConfirmed());

                userEditor.putFloat("w_recommendations_registered_amount", (float) mUser.getWallet().getRecommendationsRegisteredAmount());
                userEditor.putFloat("w_recommendations_converted_amount", (float) mUser.getWallet().getRecommendationsConvertedAmount());
                userEditor.putFloat("w_recommendations_confirmed_amount", (float) mUser.getWallet().getRecommendationsConfirmedAmount());

                userEditor.putFloat("w_balance", (float) mUser.getWallet().getBalance());
            }

            userEditor.apply();
        }
    }

    public static final int LOGGED_USER = 1;
    public static final int LOGGED_COMPANY = 2;

    public static int getLoggedType(Context context){
        int userId = getUserId(context);
        if(userId <= 0)
            return 0;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            return tokenPreferences.getInt("t", 1);
        }
    }

    public static int loginToUser(Context context) {
        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            mLoginWeb = false;

            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("t", LOGGED_USER);
            tokenEditor.apply();
        }

        return LOGGED_USER;
    }

    public static int loginToCompany(Context context) {
        try (StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            mLoginWeb = false;

            if(getCompanyId(context) <= 0)
                return LOGGED_USER;

            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
            tokenEditor.putInt("t", LOGGED_COMPANY);
            tokenEditor.apply();
        }

        return LOGGED_COMPANY;
    }

    public static boolean isLoggedUser(Context context){
        return getLoggedType(context) == LOGGED_USER;
    }

    public static boolean isLoggedCompany(Context context){
        return getLoggedType(context) == LOGGED_COMPANY;
    }

    public static UserTO getLogged(Context context){
        if(isLoggedUser(context))
            return getUser(context);
        else if(isLoggedCompany(context))
            return getCompany(context);
        else
            return null;
    }

    public static int getLoggedId(Context context){
        if(isLoggedUser(context))
            return getUserId(context);
        else if(isLoggedCompany(context))
            return getCompanyId(context);
        else
            return 0;
    }

    public static UserTokenTO getLoggedToken(Context context){
        if(isLoggedUser(context))
            return getUserToken(context);
        else if(isLoggedCompany(context))
            return getCompanyToken(context);
        else
            return null;
    }

    public static UserTO getUser(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);

            UserTO user = new UserTO();
            user.setId(userPreferences.getInt("id", 0));
            user.setName(userPreferences.getString("name", null));
            user.setEmail(userPreferences.getString("email", null));
            user.setLanguage(userPreferences.getString("language", null));
            user.setTimezone(userPreferences.getString("timezone", null));
            user.setCountry(new CountryTO(userPreferences.getString("country", null)));
            user.setRecTerms(userPreferences.getBoolean("rec_terms", false));
            user.setRecTermsSocial(userPreferences.getBoolean("rec_terms_social", false));

            String photoProfileUrl = userPreferences.getString("photo_profile", null);
            if(!TextUtils.isEmpty(photoProfileUrl)){
                MediaTO photoProfile = new MediaTO();
                photoProfile.setUrl(photoProfileUrl);
                user.setPhotoProfile(photoProfile);
            }

            WalletTO wallet = new WalletTO();
            wallet.setConversionsPending(userPreferences.getInt("w_conversions_pending", 0));
            wallet.setConversionsRegistered(userPreferences.getInt("w_conversions_registered", 0));
            wallet.setConversionsApproved(userPreferences.getInt("w_conversions_approved", 0));
            wallet.setConversionsRejected(userPreferences.getInt("w_conversions_rejected", 0));
            wallet.setConversionsWithdrawn(userPreferences.getInt("w_conversions_withdrawn", 0));

            wallet.setAmountPending(userPreferences.getFloat("w_amount_pending", 0));
            wallet.setAmountRegistered(userPreferences.getFloat("w_amount_registered", 0));
            wallet.setAmountApproved(userPreferences.getFloat("w_amount_approved", 0));
            wallet.setAmountRejected(userPreferences.getFloat("w_amount_rejected", 0));
            wallet.setAmountWithdrawn(userPreferences.getFloat("w_amount_withdrawn", 0));

            wallet.setAffConversionsPending(userPreferences.getInt("w_aff_conversions_pending", 0));
            wallet.setAffConversionsRegistered(userPreferences.getInt("w_aff_conversions_registered", 0));
            wallet.setAffConversionsApproved(userPreferences.getInt("w_aff_conversions_approved", 0));
            wallet.setAffConversionsRejected(userPreferences.getInt("w_aff_conversions_rejected", 0));
            wallet.setAffConversionsWithdrawn(userPreferences.getInt("w_aff_conversions_withdrawn", 0));

            wallet.setAffAmountPending(userPreferences.getFloat("w_aff_amount_pending", 0));
            wallet.setAffAmountRegistered(userPreferences.getFloat("w_aff_amount_registered", 0));
            wallet.setAffAmountApproved(userPreferences.getFloat("w_aff_amount_approved", 0));
            wallet.setAffAmountRejected(userPreferences.getFloat("w_aff_amount_rejected", 0));
            wallet.setAffAmountWithdrawn(userPreferences.getFloat("w_aff_amount_withdrawn", 0));

            wallet.setRecommendationsRegistered(userPreferences.getInt("w_recommendations_registered", 0));
            wallet.setRecommendationsConverted(userPreferences.getInt("w_recommendations_converted", 0));
            wallet.setRecommendationsConfirmed(userPreferences.getInt("w_recommendations_confirmed", 0));

            wallet.setRecommendationsRegisteredAmount(userPreferences.getFloat("w_recommendations_registered_amount", 0));
            wallet.setRecommendationsConvertedAmount(userPreferences.getFloat("w_recommendations_converted_amount", 0));
            wallet.setRecommendationsConfirmedAmount(userPreferences.getFloat("w_recommendations_confirmed_amount", 0));

            wallet.setBalance(userPreferences.getFloat("w_balance", 0));

            user.setWallet(wallet);

            return user;
        }
    }

    public static int getUserId(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getInt("id", 0);
        }
    }

    public static void saveCompanyData(Context context,UserTO mUser){
        saveCompanyData(context, mUser, null);
    }

    public static void saveCompanyData(Context context,UserTO mUser, UserTokenTO tokenCompany){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.cp",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putInt("id", mUser.getId());
            if (!TextUtils.isEmpty(mUser.getName())) userEditor.putString("name",
                    mUser.getName());
            if (!TextUtils.isEmpty(mUser.getEmail())) userEditor.putString("email",
                    mUser.getEmail());
            if (mUser.getPhotoProfile() != null &&
                    !TextUtils.isEmpty(mUser.getPhotoProfile().getUrl()))
                userEditor.putString("photo_profile", mUser.getPhotoProfile().getUrl());
            else
                userEditor.putString("photo_profile", "");
            if (!TextUtils.isEmpty(mUser.getLanguage())) userEditor.putString("language",
                    mUser.getLanguage());
            if (!TextUtils.isEmpty(mUser.getTimezone())) userEditor.putString("timezone",
                    mUser.getTimezone());
            if (mUser.getRecTerms()) userEditor.putBoolean("rec_terms", mUser.getRecTerms());

            if(mUser.getWallet() != null){
                userEditor.putInt("w_conversions_pending", mUser.getWallet().getConversionsPending());
                userEditor.putInt("w_conversions_registered", mUser.getWallet().getConversionsRegistered());
                userEditor.putInt("w_conversions_approved", mUser.getWallet().getConversionsApproved());
                userEditor.putInt("w_conversions_rejected", mUser.getWallet().getConversionsRejected());
                userEditor.putInt("w_conversions_withdrawn", mUser.getWallet().getConversionsWithdrawn());

                userEditor.putFloat("w_amount_pending", (float) mUser.getWallet().getAmountPending());
                userEditor.putFloat("w_amount_registered", (float) mUser.getWallet().getAmountRegistered());
                userEditor.putFloat("w_amount_approved", (float) mUser.getWallet().getAmountApproved());
                userEditor.putFloat("w_amount_rejected", (float) mUser.getWallet().getAmountRejected());
                userEditor.putFloat("w_amount_withdrawn", (float) mUser.getWallet().getAmountWithdrawn());

                userEditor.putInt("w_aff_conversions_pending", mUser.getWallet().getAffConversionsPending());
                userEditor.putInt("w_aff_conversions_registered", mUser.getWallet().getAffConversionsRegistered());
                userEditor.putInt("w_aff_conversions_approved", mUser.getWallet().getAffConversionsApproved());
                userEditor.putInt("w_aff_conversions_rejected", mUser.getWallet().getAffConversionsRejected());
                userEditor.putInt("w_aff_conversions_withdrawn", mUser.getWallet().getAffConversionsWithdrawn());

                userEditor.putFloat("w_aff_amount_pending", (float) mUser.getWallet().getAffAmountPending());
                userEditor.putFloat("w_aff_amount_registered", (float) mUser.getWallet().getAffAmountRegistered());
                userEditor.putFloat("w_aff_amount_approved", (float) mUser.getWallet().getAffAmountApproved());
                userEditor.putFloat("w_aff_amount_rejected", (float) mUser.getWallet().getAffAmountRejected());
                userEditor.putFloat("w_aff_amount_withdrawn", (float) mUser.getWallet().getAffAmountWithdrawn());

                userEditor.putInt("w_recommendations_registered", mUser.getWallet().getRecommendationsRegistered());
                userEditor.putInt("w_recommendations_converted", mUser.getWallet().getRecommendationsConverted());
                userEditor.putInt("w_recommendations_confirmed", mUser.getWallet().getRecommendationsConfirmed());

                userEditor.putFloat("w_recommendations_registered_amount", (float) mUser.getWallet().getRecommendationsRegisteredAmount());
                userEditor.putFloat("w_recommendations_converted_amount", (float) mUser.getWallet().getRecommendationsConvertedAmount());
                userEditor.putFloat("w_recommendations_confirmed_amount", (float) mUser.getWallet().getRecommendationsConfirmedAmount());

                userEditor.putFloat("w_balance", (float) mUser.getWallet().getBalance());
            }

            userEditor.apply();

            //SAVE TOKEN
            if(tokenCompany != null && !TextUtils.isEmpty(tokenCompany.getIdToken()) && !TextUtils.isEmpty(tokenCompany.getAccessToken())){
                String tokenCpIdCrypt = tokenCompany.getIdToken();
                String tokenCpAccessCrypt = tokenCompany.getAccessToken();
                try {
                    tokenCpIdCrypt = AESCrypt.encrypt(
                            mUser.getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw"
                                    + mUser.getId(),
                            tokenCpIdCrypt);
                    tokenCpAccessCrypt = AESCrypt.encrypt(
                            mUser.getId() + "77mKfcAtF42xJGfEFqbcMMTEDs9nJbRw"
                                    + mUser.getId(),
                            tokenCpAccessCrypt);
                } catch (GeneralSecurityException e) {
                    tokenCpIdCrypt = tokenCompany.getIdToken();
                    tokenCpAccessCrypt = tokenCompany.getAccessToken();
                }


                SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor tokenEditor = tokenPreferences.edit();
                tokenEditor.putString("cpi",tokenCpIdCrypt);
                tokenEditor.putString("cpa",tokenCpAccessCrypt);
                tokenEditor.apply();
            }
        }
    }

    public static UserTO getCompany(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.cp",
                    Context.MODE_PRIVATE);

            UserTO user = new UserTO();
            user.setId(userPreferences.getInt("id", 0));
            user.setName(userPreferences.getString("name", null));
            user.setEmail(userPreferences.getString("email", null));
            user.setLanguage(userPreferences.getString("language", null));
            user.setTimezone(userPreferences.getString("timezone", null));
            user.setRecTerms(userPreferences.getBoolean("rec_terms", false));
            user.setRecTermsSocial(userPreferences.getBoolean("rec_terms_social", false));

            String photoProfileUrl = userPreferences.getString("photo_profile", null);
            if(!TextUtils.isEmpty(photoProfileUrl)){
                MediaTO photoProfile = new MediaTO();
                photoProfile.setUrl(photoProfileUrl);
                user.setPhotoProfile(photoProfile);
            }

            WalletTO wallet = new WalletTO();
            wallet.setConversionsPending(userPreferences.getInt("w_conversions_pending", 0));
            wallet.setConversionsRegistered(userPreferences.getInt("w_conversions_registered", 0));
            wallet.setConversionsApproved(userPreferences.getInt("w_conversions_approved", 0));
            wallet.setConversionsRejected(userPreferences.getInt("w_conversions_rejected", 0));
            wallet.setConversionsWithdrawn(userPreferences.getInt("w_conversions_withdrawn", 0));

            wallet.setAmountPending(userPreferences.getFloat("w_amount_pending", 0));
            wallet.setAmountRegistered(userPreferences.getFloat("w_amount_registered", 0));
            wallet.setAmountApproved(userPreferences.getFloat("w_amount_approved", 0));
            wallet.setAmountRejected(userPreferences.getFloat("w_amount_rejected", 0));
            wallet.setAmountWithdrawn(userPreferences.getFloat("w_amount_withdrawn", 0));

            wallet.setAffConversionsPending(userPreferences.getInt("w_aff_conversions_pending", 0));
            wallet.setAffConversionsRegistered(userPreferences.getInt("w_aff_conversions_registered", 0));
            wallet.setAffConversionsApproved(userPreferences.getInt("w_aff_conversions_approved", 0));
            wallet.setAffConversionsRejected(userPreferences.getInt("w_aff_conversions_rejected", 0));
            wallet.setAffConversionsWithdrawn(userPreferences.getInt("w_aff_conversions_withdrawn", 0));

            wallet.setAffAmountPending(userPreferences.getFloat("w_aff_amount_pending", 0));
            wallet.setAffAmountRegistered(userPreferences.getFloat("w_aff_amount_registered", 0));
            wallet.setAffAmountApproved(userPreferences.getFloat("w_aff_amount_approved", 0));
            wallet.setAffAmountRejected(userPreferences.getFloat("w_aff_amount_rejected", 0));
            wallet.setAffAmountWithdrawn(userPreferences.getFloat("w_aff_amount_withdrawn", 0));

            wallet.setRecommendationsRegistered(userPreferences.getInt("w_recommendations_registered", 0));
            wallet.setRecommendationsConverted(userPreferences.getInt("w_recommendations_converted", 0));
            wallet.setRecommendationsConfirmed(userPreferences.getInt("w_recommendations_confirmed", 0));

            wallet.setRecommendationsRegisteredAmount(userPreferences.getFloat("w_recommendations_registered_amount", 0));
            wallet.setRecommendationsConvertedAmount(userPreferences.getFloat("w_recommendations_converted_amount", 0));
            wallet.setRecommendationsConfirmedAmount(userPreferences.getFloat("w_recommendations_confirmed_amount", 0));

            wallet.setBalance(userPreferences.getFloat("w_balance", 0));

            user.setWallet(wallet);

            return user;
        }
    }

    public static int getCompanyId(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.cp",
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
                countryEditor.apply();
            }else{
                if (!TextUtils.isEmpty(mCountry.getId())) countryEditor.putString("id",
                        mCountry.getId());
                if (!TextUtils.isEmpty(mCountry.getName())) countryEditor.putString("name",
                        mCountry.getName());
                if (!TextUtils.isEmpty(mCountry.getLanguage())) countryEditor.putString("language",
                        mCountry.getLanguage());
                if (mCountry.getCurrency() != null &&!TextUtils.isEmpty(mCountry.getCurrency().getId())) countryEditor.putString("currency",
                        mCountry.getCurrency().getId());
                if (!TextUtils.isEmpty(mCountry.getSearchHint())) countryEditor.putString("search_hint",
                        mCountry.getSearchHint());
                countryEditor.putBoolean("load_posts",mCountry.getLoadPosts());
                countryEditor.putBoolean("load_offers",mCountry.getLoadOffers());
                countryEditor.putBoolean("load_coupons",mCountry.getLoadCoupons());
                countryEditor.putBoolean("load_tasks",mCountry.getLoadTasks());
                countryEditor.putBoolean("load_games",mCountry.getLoadGames());
                if (mCountry.getCategoryB2b() != null && !TextUtils.isEmpty(mCountry.getCategoryB2b().getId())) {
                    countryEditor.putInt("cat_b2b_id", mCountry.getCategoryB2b().getId());
                    if(!TextUtils.isEmpty(mCountry.getCategoryB2b().getName()))
                        countryEditor.putString("cat_b2b_name", mCountry.getCategoryB2b().getName());
                    countryEditor.putInt("cat_b2b_advs", mCountry.getCategoryB2b().getTotalAdvertisers());
                }
                countryEditor.apply();
            }
        }
    }

    public static void saveVersionData(Context context, VersionTO versionScript){
        if(versionScript == null)
            return;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences versionPreferences = context.getSharedPreferences("com.wooeen.version",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor versionEditor = versionPreferences.edit();

            if (versionScript.getCheckout() > 0) versionEditor.putInt("checkout",versionScript.getCheckout());
            if (versionScript.getProduct() > 0) versionEditor.putInt("product",versionScript.getProduct());
            if (versionScript.getQuery() > 0) versionEditor.putInt("query",versionScript.getQuery());
            if (versionScript.getPub() > 0) versionEditor.putInt("pub",versionScript.getPub());
            if(!TextUtils.isEmpty(versionScript.getAndroid())) versionEditor.putString("android", versionScript.getAndroid());

            versionEditor.apply();
        }
    }

    public static VersionTO getVersion(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences versionPreferences = context.getSharedPreferences("com.wooeen.version",
                    Context.MODE_PRIVATE);


            VersionTO version = new VersionTO();

            version.setCheckout(versionPreferences.getInt("checkout", 0));
            version.setProduct(versionPreferences.getInt("product", 0));
            version.setQuery(versionPreferences.getInt("query", 0));
            version.setPub(versionPreferences.getInt("pub", 0));
            version.setAndroid(versionPreferences.getString("android", null));

            return version;
        }
    }

    public static boolean hasUpdate(Context context, String currentVersion){
        VersionTO v = getVersion(context);
        if(v == null || TextUtils.isEmpty(v.getAndroid()))
            return false;

        String officialVersion = v.getAndroid();
        if(TextUtils.isEmpty(currentVersion))
            return false;

        currentVersion = currentVersion.split(",")[0];
        if(currentVersion.contains("Wooeen"))
            currentVersion = currentVersion.substring(7);

        return !officialVersion.equals(currentVersion);
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
            country.setCurrency(new CurrencyTO(countryPreferences.getString("currency", null)));
            country.setSearchHint(countryPreferences.getString("search_hint", null));
            country.setLoadPosts(countryPreferences.getBoolean("load_posts", false));
            country.setLoadOffers(countryPreferences.getBoolean("load_offers", false));
            country.setLoadCoupons(countryPreferences.getBoolean("load_coupons", false));
            country.setLoadTasks(countryPreferences.getBoolean("load_tasks", false));
            country.setLoadGames(countryPreferences.getBoolean("load_games", false));

            int catB2bId = countryPreferences.getInt("cat_b2b_id", 0);
            if(catB2bId > 0){
                CategoryTO catB2b = new CategoryTO();
                catB2b.setId(catB2bId);
                catB2b.setName(countryPreferences.getString("cat_b2b_name", null));
                catB2b.setTotalAdvertisers(countryPreferences.getInt("cat_b2b_advs", 0));
                country.setCategoryB2b(catB2b);
            }

            return country;
        }
    }

    public static void saveUserFcmToken(Context context,String fcmToken, String fcmTokenOld){
        if(TextUtils.isEmpty(fcmToken))
            return;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putString("fcm_token", fcmToken);
            userEditor.apply();

            //set in dao
            new UserSetFcmToken(context, fcmToken, fcmTokenOld).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserSetFcmToken extends AsyncTask<Integer> {

        private Context context;
        private String fcmToken;
        private String fcmTokenOld;

        public UserSetFcmToken(Context context,String fcmToken,String fcmTokenOld){
            this.context = context;
            this.fcmToken = fcmToken;
            this.fcmTokenOld = fcmTokenOld;
        }

        @Override
        protected Integer doInBackground() {
            UserInstallAPI apiDAO = new UserInstallAPI(UserUtils.getUserToken(context));
            return apiDAO.newUserInstall(fcmToken, fcmTokenOld, null);
        }

        @Override
        protected void onPostExecute(Integer result) {
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

            UserAPI apiDAO = new UserAPI(UserUtils.getLoggedToken(context));
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

    public static void saveUserRecTermsSocial(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean("rec_terms_social", true);
            userEditor.apply();

            //set in dao
            new UserSetRecTermsSocial(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserSetRecTermsSocial extends AsyncTask<Boolean> {

        private Context context;

        public UserSetRecTermsSocial(Context context){
            this.context = context;

        }

        @Override
        protected Boolean doInBackground() {
            UserTO user = new UserTO();
            user.setRecTermsSocial(true);
            user.setRecTermsSocialIp(DeviceUtils.getHostAddress());

            UserAPI apiDAO = new UserAPI(UserUtils.getLoggedToken(context));
            return apiDAO.setRecTermsSocial(user);
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

    public static boolean getUserRecTermsSocial(Context context){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.us",
                    Context.MODE_PRIVATE);
            return userPreferences.getBoolean("rec_terms_social", false);
        }
    }

    public static UserTokenTO getUserToken(Context context){
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

    public static UserTokenTO getCompanyToken(Context context){
        int userId = getCompanyId(context);
        if(userId <= 0)
            return null;

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences tokenPreferences = context.getSharedPreferences("com.wooeen.tk",
                    Context.MODE_PRIVATE);
            String tokenIdCrypt = tokenPreferences.getString("cpi", null);
            String tokenAccessCrypt = tokenPreferences.getString("cpa", null);

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

    public static List<String> getSearchTags(Context context, String prefix){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.search."+prefix,
                    Context.MODE_PRIVATE);
            List<String> list = new ArrayList<String>();

            String text1 = userPreferences.getString("text1", null);
            if(!TextUtils.isEmpty(text1))
                list.add(text1);

            String text2 = userPreferences.getString("text2", null);
            if(!TextUtils.isEmpty(text2))
                list.add(text2);

            String text3 = userPreferences.getString("text3", null);
            if(!TextUtils.isEmpty(text3))
                list.add(text3);

            return list;
        }
    }

    public static void removeSearchTags(Context context, String prefix, String q){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.search."+prefix,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();

            String text1 = userPreferences.getString("text1", null);
            if(!TextUtils.isEmpty(text1) && q.equalsIgnoreCase(text1))
                userEditor.remove("text1");

            String text2 = userPreferences.getString("text2", null);
            if(!TextUtils.isEmpty(text2) && q.equalsIgnoreCase(text2))
                userEditor.remove("text2");

            String text3 = userPreferences.getString("text3", null);
            if(!TextUtils.isEmpty(text3) && q.equalsIgnoreCase(text3))
                userEditor.remove("text3");

            userEditor.apply();
        }
    }

    public static boolean saveSearchTags(Context context,String prefix,String q){
        if(TextUtils.isEmpty(q))
            return false;

        List<String> texts = getSearchTags(context, prefix);

        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            SharedPreferences userPreferences = context.getSharedPreferences("com.wooeen.search."+prefix,
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();

            boolean alreadyRegistered = false;
            for(String text:texts){
                if(q.equalsIgnoreCase(text))
                    alreadyRegistered = true;
            }

            if(!alreadyRegistered) {
                if(texts.size() >= 2)
                    userEditor.putString("text3", texts.get(1));

                if(texts.size() >= 1)
                    userEditor.putString("text2", texts.get(0));

                userEditor.putString("text1", q);
            }

            userEditor.apply();

            return !alreadyRegistered;
        }
    }

    public static void saveUserTrkApp(Context context){
        saveUserTrk(context, TrackingUtils.SOURCE_APP);
    }

    public static void saveUserTrkSocial(Context context){
        saveUserTrk(context, TrackingUtils.SOURCE_SOCIAL);
    }

    public static void saveUserTrkRtg(Context context){
        saveUserTrk(context, TrackingUtils.SOURCE_RTG_ANDROID);
    }

    public static void saveUserTrkPush(Context context){
        saveUserTrk(context, TrackingUtils.SOURCE_PUSH);
    }

    public static void saveUserTrkSearch(Context context){
        saveUserTrk(context, TrackingUtils.SOURCE_SEARCH);
    }

    public static void saveUserTrk(Context context,int media){
        try(StrictModeContext ignored = StrictModeContext.allowDiskWrites()) {
            //set in dao
            new UserSetTrk(context, media).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private static class UserSetTrk extends AsyncTask<Boolean> {

        private Context context;
        private int media;

        public UserSetTrk(Context context,int media){
            this.context = context;
            this.media = media;
        }

        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI(UserUtils.getLoggedToken(context));
            return apiDAO.setTrk(media);
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }
}
