package com.wooeen.view.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import com.google.gson.Gson;
import com.wooeen.model.api.utils.WebServiceClient;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.to.CountryTO;
import com.wooeen.utils.UserUtils;

import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.browser.about_settings.AboutSettingsBridge;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionHandler implements
        java.lang.Thread.UncaughtExceptionHandler {
    private final Context myContext;
    private final String LINE_SEPARATOR = "\n";
    Thread.UncaughtExceptionHandler defaultUEH;

    public ExceptionHandler(Context con) {
        myContext = con;
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void uncaughtException(Thread thread, Throwable exception) {

        StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append("************ CAUSE OF ERROR ************\n\n");
        errorReport.append(stackTrace.toString());

        errorReport.append("\n************ DEVICE INFORMATION ***********\n");
        errorReport.append("Brand: ");
        errorReport.append(Build.BRAND);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Device: ");
        errorReport.append(Build.DEVICE);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Model: ");
        errorReport.append(Build.MODEL);
        errorReport.append(LINE_SEPARATOR);

        int userId = UserUtils.getUserId(myContext);
        CountryTO country = UserUtils.getCountry(myContext);

        errorReport.append("\n************ USER INFORMATION ***********\n");
        errorReport.append("Version App:");
        errorReport.append(AboutSettingsBridge.getApplicationVersion());
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("User ID: ");
        errorReport.append(""+userId);
        errorReport.append(LINE_SEPARATOR);
        errorReport.append("Country: ");
        errorReport.append(country != null ? country.getId() : "");
        errorReport.append(LINE_SEPARATOR);

//        File root = myContext.getExternalFilesDir(null);
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(
//                new Date());
//
//        File dir = new File(root.getAbsolutePath() + "/dir_name/log");
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//
//        File file = new File(dir, "log.txt");
//        if(!file.exists()) {
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

        new SendLog(myContext, errorReport.toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

//        try {
//            BufferedWriter buf = new BufferedWriter(new FileWriter(file, true));
//            buf.append(currentDateTimeString + ":" + errorReport.toString());
//            buf.newLine();
//            buf.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        defaultUEH.uncaughtException(thread, exception);
        System.exit(0);
    }



    private static class SendLog extends AsyncTask<Boolean> {

        private Context context;
        private String log;

        public SendLog(Context context,String log){
            this.context = context;
            this.log = log;
        }

        @Override
        protected Boolean doInBackground() {
            return send(log);
        }

        public boolean send(String log){
            try {
                //configura a url e os parametros
                Uri.Builder builder = new Uri.Builder();
                builder.scheme(WoeDAOUtils.SCHEMA)
                        .encodedAuthority(WoeDAOUtils.AUTHORITY)
                        .path(WoeDAOUtils.PATH)
                        .appendPath("android")
                        .appendPath("log");

                Gson gsonBuilder = WoeDAOUtils.getGson();

                String urlBuilder = builder.build().toString();
                new WebServiceClient()
                        .post(
                                urlBuilder,
                                log,
                                new WebServiceClient.Header("Content-Type","text/plain"),
                                new WebServiceClient.Header("Accept","text/plain"));
                return true;
            } catch(Exception ex){
                ex.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
        }
    }

}
