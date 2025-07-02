package com.wooeen.view.auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.wooeen.model.api.CategoryAPI;
import com.wooeen.model.api.CountryAPI;
import com.wooeen.model.to.CategoryTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;

import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryListAdapter extends BaseAdapter{

    private List<CategoryTO> list = new ArrayList<CategoryTO>();
    private Context context;

    public CategoryListAdapter(Context context){
        this.context = context;

        populateList();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CategoryTO getItem(int position) {
        if(position >= 0 && position < list.size()) {
            return list.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<CategoryTO> getList(){
        return list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CategoryViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.category_dialog, parent, false);
            holder = new CategoryViewHolder();

            holder.catText = (TextView) convertView.findViewById(R.id.cat_text);


            convertView.setTag(holder);
        } else {
            holder = (CategoryViewHolder) convertView.getTag();
        }

        if(position >= 0 && position < list.size()) {
            CategoryTO category = list.get(position);

            holder.catText.setText(category.getName());
        }

        return convertView;
    }

    public static class CategoryViewHolder{
        TextView catText;
    }

    private void populateList() {
        list = new ArrayList<CategoryTO>();

        new LoadCategories(context, this, list).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setList(List<CategoryTO> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public static class LoadCategories extends AsyncTask<List<CategoryTO>> {

        private Context context;
        private CategoryListAdapter v;
        private List<CategoryTO> list;
        private CountryTO country;

        public LoadCategories(Context context, CategoryListAdapter v, List<CategoryTO> list){
            this.context = context;
            this.v = v;
            this.list = list;
            this.country = UserUtils.getCountry(context);
        }

        @Nullable
        @Override
        protected List<CategoryTO> doInBackground() {
            if(country == null || TextUtils.isEmpty(country.getId()) || country.getCategoryB2b() == null || country.getCategoryB2b().getId() <= 0)
                return null;

            CategoryAPI dao = new CategoryAPI(country.getId());
            return dao.get(country.getCategoryB2b().getId());
        }

        @Override
        protected void onPostExecute(List<CategoryTO> result) {
            if(result != null) {
                v.setList(result);
            }
        }
    }
}