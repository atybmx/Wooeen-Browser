package com.wooeen.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// import com.singular.sdk.Singular;
import com.squareup.picasso.Picasso;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.auth.LoginView;
import com.wooeen.view.auth.RegisterCompanyView;
import com.wooeen.view.ui.CircleTransform;
import com.wooeen.view.user.RecTermsView;

import org.chromium.chrome.R;
import org.chromium.chrome.browser.ntp.BraveNewTabPageLayout;
import org.chromium.chrome.browser.util.TabUtils;

import java.util.ArrayList;
import java.util.List;

public class MenuUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Activity mActivity;

    private static int TYPE_HEAD = 0;
    private static int TYPE_ITEM = 1;

    private int loggedType;
    private int userId;
    private UserTO user;
    private UserTO loggedCompany;
    private CountryTO country;

    private List<MenuItemHolder> items = new ArrayList<MenuItemHolder>();
    public void loadAttrs(int loggedType,int userId, UserTO user, UserTO loggedCompany, CountryTO country){
        this.loggedType = loggedType;
        this.userId = userId;
        this.user = user;
        this.loggedCompany = loggedCompany;
        this.country = country;

        notifyItemChanged(TYPE_HEAD);
    }

    public MenuUserAdapter(Activity mActivity){
        this.mActivity = mActivity;

        PopupMenu popup = new PopupMenu(mActivity, null);
        popup.inflate(R.menu.woe_user_menu_items);
        Menu menu = popup.getMenu();
        for(int x=0;x<menu.size();x++){
            MenuItem menuitem = menu.getItem(x);

            MenuItemHolder item = new MenuItemHolder();
            item.setId(menuitem.getItemId());
            item.setText(menuitem.getTitle().toString());
            item.setImage(menuitem.getIcon());
            items.add(item);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEAD) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_user_head, parent, false);
            return new HeadViewHolder(view);

        }else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_user_menu_item, parent, false);
            return new ItemViewHolder(view);

        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.woe_ntp_empty, parent, false);
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeadViewHolder) {
            HeadViewHolder viewHolder = (HeadViewHolder) holder;

            if(user != null) {
                if (viewHolder.userImage != null) {
                    if (user.getPhotoProfile() != null && !TextUtils.isEmpty(
                            user.getPhotoProfile().getUrl())) {
                        Picasso.with(mActivity).load(user.getPhotoProfile().getUrl())
                                .error(R.drawable.woe_ic_head_user)
                                .fit().centerCrop().transform(new CircleTransform()).into(
                                viewHolder.userImage);
                    } else {
                        viewHolder.userImage.setImageResource(R.drawable.woe_ic_head_user);
                    }
                }

                if (viewHolder.userName != null) {
                    viewHolder.userName.setText(user.getName());
                } else {
                    viewHolder.userName.setText("");
                }

                if (viewHolder.userEmail != null) {
                    viewHolder.userEmail.setText(user.getEmail());
                } else {
                    viewHolder.userEmail.setText("");
                }

                if (viewHolder.userType != null) {
                    if (loggedType == UserUtils.LOGGED_USER) {
                        viewHolder.userType.setText(mActivity.getText(R.string.woe_type_pf));
                    } else if (loggedType == UserUtils.LOGGED_COMPANY) {
                        viewHolder.userType.setText(mActivity.getText(R.string.woe_type_pj));
                    }
                } else {
                    viewHolder.userType.setText("");
                }

                if (viewHolder.userId != null) {
                    viewHolder.userId.setText("" + user.getId());
                } else {
                    viewHolder.userId.setText("");
                }

                if (viewHolder.userIdCopy != null) {
                    viewHolder.userIdCopy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard =
                                    (ClipboardManager) mActivity.getSystemService(
                                            Context.CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Wooeen User ID", "" + userId);
                            clipboard.setPrimaryClip(clip);

                            Toast.makeText(mActivity.getBaseContext(),
                                    mActivity.getString(R.string.woe_your_id) + ": " + userId,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                if((loggedCompany != null && loggedCompany.getId() > 0) || userId <= 0 ||
                        country.getCategoryB2b() == null || country.getCategoryB2b().getId() <= 0){
                    viewHolder.companyCreate.setVisibility(View.GONE);
                }else{
                    viewHolder.companyCreate.setVisibility(View.VISIBLE);
                }
            }

        }else if (holder instanceof ItemViewHolder) {
            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            if(position > 0 && position <= items.size()) {
                MenuItemHolder item = items.get(position - 1);

                if (viewHolder.image != null) {
                    viewHolder.image.setImageDrawable(item.getImage());
                }
                if(viewHolder.text != null){
                    viewHolder.text.setText(item.getText());
                }
                if(viewHolder.content != null){
                    viewHolder.content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(item.getId() == R.id.woe_my_cashback){
                                // Singular.event("menu_cashback");
                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(
                                            BraveNewTabPageLayout.WOOEEN_DASHBOARD_URL);
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_tasks){
                                // Singular.event("menu_tasks");

                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(
                                            BraveNewTabPageLayout.WOOEEN_CHALLENGES_URL);
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_pubs){
                                // Singular.event("menu_offers");

                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_PUBS);
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_share){
                                // Singular.event("menu_referral");

                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else{
                                    if(UserUtils.getUserRecTerms(mActivity)) {
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_TITLE, mActivity.getString(R.string.app_name)+" - "+mActivity.getString(R.string.woe_slogan));
                                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.woe_rec_share_title));
                                        sendIntent.putExtra(Intent.EXTRA_TEXT,
                                                mActivity.getString(R.string.woe_rec_share_text)+
                                                        "\n\n"+
                                                        String.format(BraveNewTabPageLayout.WOOEEN_RECOMMENDATION_URL,""+userId));
                                        sendIntent.setType("text/plain");
                                        mActivity.startActivity(sendIntent);
                                    }else{
                                        Intent intent = new Intent(mActivity, RecTermsView.class);
                                        mActivity.startActivity(intent);

                                        if(listener != null) listener.close();
                                    }
                                }
                            }else if(item.getId() == R.id.woe_advertisers){
                                // Singular.event("menu_advertisers");

                                TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_ADVERTISERS_URL);
                                if(listener != null) listener.close();
                            }else if(item.getId() == R.id.woe_blog){
                                // Singular.event("menu_blog");

                                TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_BLOG);
                                if(listener != null) listener.close();
                            }else if(item.getId() == R.id.woe_company){
                                // Singular.event("menu_company");
                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    if ((loggedCompany != null && loggedCompany.getId() > 0) ||
                                            (userId > 0 && country.getCategoryB2b() == null
                                                    && country.getCategoryB2b().getId() <= 0)) {
                                        if (loggedCompany != null && loggedCompany.getId() > 0) {
                                            TabUtils.loginToCompany(mActivity);
                                            if (listener != null) listener.close();
                                        } else {
                                            if (listener != null) listener.close();

                                            Intent intent = new Intent(mActivity,
                                                    RegisterCompanyView.class);
                                            mActivity.startActivity(intent);
                                        }
                                    }
                                }

                            }else if(item.getId() == R.id.woe_profile){
                                // Singular.event("menu_profile");

                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_PROFILE);
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_history){
                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab("https://app.wooeen.com/u/history");
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_favorites){
                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(
                                            "https://app.wooeen.com/u/history?fav=1");
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_preferences){
                                if(userId <= 0) {
                                    if(listener != null) listener.close();

                                    //open auth wooeen
                                    Intent intent = new Intent(mActivity, LoginView.class);
                                    mActivity.startActivity(intent);
                                }else {
                                    TabUtils.openUrlInSameTab(
                                            "https://app.wooeen.com/u/preferences");
                                    if (listener != null) listener.close();
                                }
                            }else if(item.getId() == R.id.woe_logout){
                                TabUtils.logout(mActivity);
                                if(listener != null) listener.close();
                            }
                        }
                    });
                }
            }
        }
    }

    public static class HeadViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView userName;
        public TextView userEmail;
        public TextView userType;
        public TextView userId;
        public Button userIdCopy;
        public Button companyCreate;

        HeadViewHolder(View itemView) {
            super(itemView);
            this.userImage = (ImageView) itemView.findViewById(R.id.woe_user_image);
            this.userName = (TextView) itemView.findViewById(R.id.txt_name);
            this.userEmail = (TextView) itemView.findViewById(R.id.txt_email);
            this.userType = (TextView) itemView.findViewById(R.id.txt_type);
            this.userId = (TextView) itemView.findViewById(R.id.txt_id);
            this.userIdCopy = (Button) itemView.findViewById(R.id.woe_id_copy);
            this.companyCreate = (Button) itemView.findViewById(R.id.woe_company_create);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout content;
        public ImageView image;
        public TextView text;

        ItemViewHolder(View itemView) {
            super(itemView);

            this.content = (LinearLayout) itemView.findViewById(R.id.woe_menu_item_content);
            this.image = (ImageView) itemView.findViewById(R.id.woe_menu_item_image);
            this.text = (TextView) itemView.findViewById(R.id.woe_menu_item_text);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position > getCountItemsFixed())
            return TYPE_ITEM;

        return position;
    }

    @Override
    public int getItemCount() {
        int totalItems = getCountItemsFixed();

        totalItems += items.size();

        return totalItems;
    }

    private int getCountItemsFixed(){
        return 1;
    }

    public static class MenuItemHolder{
        private int id;
        private String text;
        private Drawable image;

        public MenuItemHolder(){
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Drawable getImage() {
            return image;
        }

        public void setImage(Drawable image) {
            this.image = image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    private OnItemSelectedListener listener;
    public interface OnItemSelectedListener {
        public void open(int loggedType,int userId, UserTO user,UserTO loggedCompany, CountryTO country);
        public void close();
    }

    public void setListener(OnItemSelectedListener listener){
        this.listener = listener;
    }
}
