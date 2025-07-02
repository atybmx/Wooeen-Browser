package com.wooeen.view.auth;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.wooeen.model.api.CountryAPI;
import com.wooeen.model.to.CountryTO;
import com.wooeen.utils.TextUtils;

import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.List;

public class CountryListAdapter extends BaseAdapter{

    private List<CountryAPI.CountryTOA> list = new ArrayList<CountryAPI.CountryTOA>();
    private Context context;

    public CountryListAdapter(Context context){
        this.context = context;

        populateList();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CountryAPI.CountryTOA getItem(int position) {
        if(position >= 0 && position < list.size()) {
            return list.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<CountryAPI.CountryTOA> getList(){
        return list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CountryViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.country_dialog, parent, false);
            holder = new CountryViewHolder();

            holder.crImg = (ImageView) convertView.findViewById(R.id.cr_img);
            holder.crText = (TextView) convertView.findViewById(R.id.cr_text);


            convertView.setTag(holder);
        } else {
            holder = (CountryViewHolder) convertView.getTag();
        }

        if(position >= 0 && position < list.size()) {
            CountryAPI.CountryTOA country = list.get(position);

            holder.crText.setText(country.getId() + (!TextUtils.isEmpty(country.getName())
                    ? " - " + country.getName() : ""));
            Picasso.with(context).load(RegisterStepCrView.getCountryFlag(country.getId(),
                    40)).fit().centerCrop().into(holder.crImg);
        }

        return convertView;
    }

    public static class CountryViewHolder{
        ImageView crImg;
        TextView crText;
    }

    static class ViewHolder {
        protected TextView name;
        protected ImageView flag;
    }

    private void populateList() {
        list = new ArrayList<CountryAPI.CountryTOA>();

        list.add(new CountryAPI.CountryTOA("BR", RegisterStepCrView.getCountryFlag("BR", 40)));
        list.add(new CountryAPI.CountryTOA("DE", RegisterStepCrView.getCountryFlag("DE", 40)));
        list.add(new CountryAPI.CountryTOA("ES", RegisterStepCrView.getCountryFlag("ES", 40)));
        list.add(new CountryAPI.CountryTOA("FR", RegisterStepCrView.getCountryFlag("FR", 40)));

        new LoadCountries(context, this, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setList(List<CountryAPI.CountryTOA> list) {
        this.list = list;

        notifyDataSetChanged();
    }

    public static class LoadCountries extends AsyncTask<List<CountryAPI.CountryTOA>> {

        private Context context;
        private CountryListAdapter v;
        private List<CountryAPI.CountryTOA> list;

        public LoadCountries(Context context, CountryListAdapter v, List<CountryAPI.CountryTOA> list){
            this.context = context;
            this.v = v;
            this.list = list;
        }

        @Nullable
        @Override
        protected List<CountryAPI.CountryTOA> doInBackground() {
            CountryAPI dao = new CountryAPI();
            return dao.get();
        }

        @Override
        protected void onPostExecute(List<CountryAPI.CountryTOA> result) {
            if(result != null) {
                v.setList(result);
            }
        }
    }
}