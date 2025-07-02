/* Copyright (c) 2022 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.ntp;

import static org.chromium.ui.base.ViewUtils.dpToPx;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

// import com.singular.sdk.*;

import com.wooeen.model.api.AdvertiserAPI;
import com.wooeen.model.api.CouponAPI;
import com.wooeen.model.api.PubAPI;
import com.wooeen.model.api.UserAPI;
import com.wooeen.model.api.utils.WoeDAOUtils;
import com.wooeen.model.dao.PostDAO;
import com.wooeen.model.dao.TaskDAO;
import com.wooeen.model.to.CurrencyTO;
import com.wooeen.model.to.NavigationTO;
import com.wooeen.model.to.PostTO;
import com.wooeen.model.to.PubTO;
import com.wooeen.model.to.CouponTO;
import com.wooeen.model.to.CountryTO;
import com.wooeen.model.to.WalletTO;
import com.wooeen.model.to.UserTO;
import com.wooeen.model.to.TaskTO;
import com.wooeen.model.top.PostTOP;
import com.wooeen.model.top.PubTOP;
import com.wooeen.model.top.TaskTOP;
import com.wooeen.model.to.UserQuickAccessTO;
import com.wooeen.model.to.UserTokenTO;
import com.wooeen.utils.DatetimeUtils;
import com.wooeen.view.MenuUserAdapter;
import com.wooeen.view.auth.LoginView;
import com.wooeen.view.auth.loader.UserQuickAccessNewLoader;
import com.wooeen.view.user.RecTermsView;
import com.wooeen.view.CouponItemAdapter;
import com.wooeen.view.utils.PubViewUtils;
import com.wooeen.utils.NavigationUtils;
import com.wooeen.utils.NumberUtils;
import com.wooeen.utils.PubUtils;
import com.wooeen.utils.TextUtils;
import com.wooeen.utils.TrackingUtils;
import com.wooeen.utils.UserUtils;
import com.wooeen.view.auth.RegisterCompanyView;
import com.wooeen.view.ui.CircleTransform;
import com.wooeen.view.ui.RoundedCornersTransformation;
import com.wooeen.utils.WooeenSettings;

import org.chromium.base.StrictModeContext;
import org.chromium.base.task.AsyncTask;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.about_settings.AboutSettingsBridge;
import org.chromium.chrome.browser.night_mode.GlobalNightModeStateProviderHolder;
import org.chromium.chrome.browser.util.TabUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class BraveNtpAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private RecyclerView recyclerView;
    private View mMvTilesContainerLayout;
    private View mMvSearchContainerLayout;
    private EditText mMvSearchEdittext;

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    private GetPubsLoader pubsLoader;
    private boolean isLoading;
    private boolean hasMore = false;
    private boolean isLoadingMore = false;
    private List<PubItemHolder> pubs = new ArrayList<PubItemHolder>();

    private boolean hasUpdate = false;

    private static int TYPE_HEADER = 0;
    private static int TYPE_SEARCH = 1;
    private static int TYPE_BUTTON = 2;

    private static int TYPE_TOP_SITES = -5;
    private static int TYPE_BANNER = -5;
    private static int TYPE_FEED = -5;
    private static int TYPE_WIDGET = -5;

    private static int TYPE_LOGO = -5;// 0
    private static int TYPE_SEARCH_BOX = -5;// 1
    private static int TYPE_BUTTONS = -5;// 2
    private static int TYPE_UPDATE = -5;// 3

    private static int TYPE_PUB_LOADING = -5;// 4
    private static int TYPE_PUB = -5;// 10
    private static int TYPE_POST = -5;// 11
    private static int TYPE_TASK = -5;// 12
    private static int TYPE_RETARGETING = -5;// 13

    private static final int pub_size_page = 20;
    private int pubPage = 0;
    private static final int post_size_page = 5;
    private int postPage = 0;
    private static final int task_size_page = 5;
    private int taskPage = 0;

    private int loggedType;
    private int userId;
    private UserTO user;
    private UserTokenTO token;
    private UserTO loggedCompany;
    private UserTO loggedUser;
    private CountryTO country;
    private WalletTO wallet;
    private static final double MIN_WITHDRAWN = 10;

    private CountDownTimer searchCount;
    private static long SEARCH_TIME_LIMIT = 3600 * 1000;
    private static long SEARCH_TIME_RATE = 200;
    private List<String> words = null;
    private int wordsIndex = -1;
    private String word;
    private int wordIndex = -1;

    public BraveNtpAdapter(Activity activity, RecyclerView recyclerView, View mvTilesContainerLayout, View mvSearchContainerLayout, EditText mvSearchEdittext) {
        mActivity = activity;
        this.recyclerView = recyclerView;
        mMvTilesContainerLayout = mvTilesContainerLayout;
        mMvSearchContainerLayout = mvSearchContainerLayout;
        this.mMvSearchEdittext = mvSearchEdittext;

        loadAttrs();
    }

    public void loadAttrs(){
      loggedType = UserUtils.getLoggedType(mActivity);
      userId = UserUtils.getLoggedId(mActivity);
      user = UserUtils.getLogged(mActivity);
      token = UserUtils.getLoggedToken(mActivity);
      if(loggedType == UserUtils.LOGGED_USER){
        loggedUser = user;
        loggedCompany = UserUtils.getCompany(mActivity);
      }else if(loggedType == UserUtils.LOGGED_COMPANY){
        loggedUser = UserUtils.getUser(mActivity);
        loggedCompany = user;
      }

      country = UserUtils.getCountry(mActivity);

      if(user != null && user.getWallet() != null){
          wallet = user.getWallet();

          if(country == null)
              country = new CountryTO(user.getCountry().getId());
          if(country.getCurrency() == null)
              country.setCurrency(new CurrencyTO());
      }

      /*
      //
      //TRECHO PARA LAYOUT COM BOTÕES E PUBS
      //
      hasUpdate = UserUtils.hasUpdate(mActivity, AboutSettingsBridge.getApplicationVersion());

      //init vars
      isLoading = false;
      hasMore = false;
      isLoadingMore = false;
      pubPage = 0;
      postPage = 0;
      taskPage = 0;

      //clear the pubs
      pubs = new ArrayList<PubItemHolder>();

      //load old pubs
      List<PubTO> pubsOld = null;
      if(loggedType == UserUtils.LOGGED_USER)
          pubsOld = PubUtils.getCacheUser(mActivity);
      else if(loggedType == UserUtils.LOGGED_COMPANY)
          pubsOld = PubUtils.getCacheCompany(mActivity);

      if(pubsOld != null && !pubsOld.isEmpty()) {
          for(PubTO p:pubsOld)
              pubs.add(new PubItemHolder(p));
      }

      //load new pubs if user login
      pubsLoader = new GetPubsLoader();
      pubsLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
      */
    }

    private void initSearchHint(){
      if(country != null && !TextUtils.isEmpty(country.getSearchHint()))
          words = TextUtils.getValues(country.getSearchHint());
      if(words == null || words.isEmpty())
          words = TextUtils.getValues(mActivity.getString(R.string.woe_search_words));
      if(words != null && !words.isEmpty()) {
        Collections.sort(words);
        searchCount = new CountDownTimer(SEARCH_TIME_LIMIT, SEARCH_TIME_RATE) {
              public void onTick(long millisUntilFinished) {
                  String w = getWord();
                  if(w != null){
                    if(mMvSearchEdittext != null)
                      mMvSearchEdittext.setHint(w);
                  }
              }

              public void onFinish() {

              }
        };
        searchCount.start();
      }
    }

    private String getWord(){
      wordIndex++;
      if(word == null || wordIndex == 0 || wordIndex >= word.length()) {
          if(word == null)
              wordIndex = 0;
          else if(wordIndex >= word.length())
              wordIndex = -2;

          if(wordIndex >= 0) {
              wordsIndex++;
              if (wordsIndex >= words.size())
                  wordsIndex = 0;
              word = words.get(wordsIndex);
          }
      }

      return word.substring(0, wordIndex < 0 ? word.length() : wordIndex + 1);
    }

    private MenuUserAdapter.OnItemSelectedListener menuUserListener;
    public void setListener(MenuUserAdapter.OnItemSelectedListener menuUserListener){
        this.menuUserListener = menuUserListener;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        if(pubsLoader != null && (isLoading || isLoadingMore))
            pubsLoader.cancel(true);

        if(searchCount != null){
            searchCount.cancel();
            searchCount = null;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        initSearchHint();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      if (holder instanceof HeaderViewHolder) {
          HeaderViewHolder searchViewHolder = (HeaderViewHolder) holder;

          if(searchViewHolder.headerMenu != null){
            searchViewHolder.headerMenu.setOnClickListener(new View.OnClickListener(){
              @Override
              public void onClick(View v) {
                if(menuUserListener != null) menuUserListener.open(loggedType, userId, user, loggedCompany, country);
              }
            });

          }

          if(userId > 0 && user != null && wallet != null){
            if(searchViewHolder.panelBalance != null){
              searchViewHolder.panelBalance.setVisibility(View.VISIBLE);
            }

            if(searchViewHolder.panelBtn != null){
              searchViewHolder.panelBtn.setVisibility(View.VISIBLE);
            }

            if(searchViewHolder.btnActiveCashback != null){
              searchViewHolder.btnActiveCashback.setVisibility(View.GONE);
            }

            if(searchViewHolder.txtBalanceHeader != null)
              searchViewHolder.txtBalanceHeader.setText(
                country == null ? "" :
                NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getBalance()));

            if(searchViewHolder.btnCashbackHeader != null) {
              searchViewHolder.btnCashbackHeader.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_DASHBOARD_URL);
                  }
              });
            }
          }else{
            if(searchViewHolder.panelBalance != null){
              searchViewHolder.panelBalance.setVisibility(View.GONE);
            }

            if(searchViewHolder.panelBtn != null){
              searchViewHolder.panelBtn.setVisibility(View.GONE);
            }

            if(searchViewHolder.btnActiveCashback != null){
              searchViewHolder.btnActiveCashback.setVisibility(View.VISIBLE);
              searchViewHolder.btnActiveCashback.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    //open auth wooeen
                    Intent intent = new Intent(mActivity, LoginView.class);
                    mActivity.startActivity(intent);
                  }
              });
            }

            if(searchViewHolder.txtBalanceHeader != null)
              searchViewHolder.txtBalanceHeader.setText("");
          }

          refreshSearchHeight();
      }else if (holder instanceof SearchViewHolder) {
          SearchViewHolder searchViewHolder = (SearchViewHolder) holder;

          refreshSearchHeight();

      }else if (holder instanceof ButtonViewHolder) {
          ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;

          buttonViewHolder.btnChat.setVisibility(View.VISIBLE);
          buttonViewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                // Singular.event("button_chat");

                String phone = "376350991";
                String message = "";

                if(userId > 0 && user != null)
                  message = (country != null ? country.getId() : user.getCountry().getId())+" \n"
                            + "ID: "+userId+"\n"
                            + user.getName();

                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);

                    String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    mActivity.startActivity(i);
                } catch (Exception e){
                    e.printStackTrace();
                }
              }
          });

          refreshSearchHeight();

      }else if (holder instanceof EmptyViewHolder) {
          refreshSearchHeight();

      }

      if (holder instanceof PubLoadingViewHolder) {
            loadMore();
      }else if (holder instanceof LogoViewHolder) {
            LogoViewHolder logoViewHolder = (LogoViewHolder) holder;

            // logoViewHolder.wooeenLogo.setImageResource(
            //                   GlobalNightModeStateProviderHolder.getInstance().isInNightMode()
            //                            ? R.drawable.wooeen_logo_white
            //                            : R.drawable.wooeen_logo);
            if(userId > 0 && user != null && wallet != null){
              if(logoViewHolder.panelBalance != null){
                logoViewHolder.panelBalance.setVisibility(View.VISIBLE);
              }

              if(logoViewHolder.panelBtn != null){
                logoViewHolder.panelBtn.setVisibility(View.VISIBLE);
              }

              if(logoViewHolder.btnActiveCashback != null){
                logoViewHolder.btnActiveCashback.setVisibility(View.GONE);
              }

              if(logoViewHolder.userImage != null){
                if(loggedType == UserUtils.LOGGED_USER){
                  logoViewHolder.userImage.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mActivity.getResources().getDisplayMetrics());
                  logoViewHolder.userImage.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mActivity.getResources().getDisplayMetrics());
                }else{
                  logoViewHolder.userImage.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mActivity.getResources().getDisplayMetrics());
                  logoViewHolder.userImage.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mActivity.getResources().getDisplayMetrics());
                }

                if (loggedUser.getPhotoProfile() != null && !TextUtils.isEmpty(loggedUser.getPhotoProfile().getUrl())) {
                    if(!logoViewHolder.userImageLoaded){
                      Picasso.with(mActivity).load(loggedUser.getPhotoProfile().getUrl())
                              .error(R.drawable.woe_ic_head_user)
                              .fit().centerCrop().transform(new RoundedCornersTransformation((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mActivity.getResources().getDisplayMetrics()), 0))
                              .into(logoViewHolder.userImage, new Callback() {
                                  @Override
                                  public void onSuccess() {
                                      // logoViewHolder.userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                  }

                                  @Override
                                  public void onError() {
                                      // logoViewHolder.userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                  }
                              });
                      logoViewHolder.userImageLoaded = true;
                    }
                } else {
                    // logoViewHolder.userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    logoViewHolder.userImage.setImageResource(R.drawable.woe_ic_head_user);
                }

                logoViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if(loggedType == UserUtils.LOGGED_USER){
                        if(menuUserListener != null) menuUserListener.open(loggedType, userId, user, loggedCompany, country);
                      }else{
                        //login to user
                        TabUtils.loginToUser(mActivity);
                      }
                    }
                });

              }

              if(logoViewHolder.companyImage != null){
                if((loggedCompany == null || loggedCompany.getId() <= 0) &&
                    (userId <= 0 || country.getCategoryB2b() == null || country.getCategoryB2b().getId() <= 0)){
                  logoViewHolder.companyImage.setVisibility(View.GONE);
                }else{
                  logoViewHolder.companyImage.setVisibility(View.VISIBLE);
                }

                if(loggedType == UserUtils.LOGGED_COMPANY){
                  logoViewHolder.companyImage.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mActivity.getResources().getDisplayMetrics());
                  logoViewHolder.companyImage.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, mActivity.getResources().getDisplayMetrics());
                }else{
                  logoViewHolder.companyImage.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mActivity.getResources().getDisplayMetrics());
                  logoViewHolder.companyImage.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, mActivity.getResources().getDisplayMetrics());
                }

                if (loggedCompany != null && loggedCompany.getPhotoProfile() != null && !TextUtils.isEmpty(loggedCompany.getPhotoProfile().getUrl())) {
                    if(!logoViewHolder.companyImageLoaded){
                      Picasso.with(mActivity).load(loggedCompany.getPhotoProfile().getUrl())
                              .error(R.drawable.woe_ic_head_company)
                              .fit().centerCrop().transform(new RoundedCornersTransformation((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, mActivity.getResources().getDisplayMetrics()), 0))
                              .into(logoViewHolder.companyImage, new Callback() {
                                  @Override
                                  public void onSuccess() {
                                      // logoViewHolder.companyImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                  }

                                  @Override
                                  public void onError() {
                                      // logoViewHolder.companyImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                  }
                              });
                      logoViewHolder.companyImageLoaded = true;
                    }
                } else {
                    // logoViewHolder.companyImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    logoViewHolder.companyImage.setImageResource(R.drawable.woe_ic_head_company);
                }

                logoViewHolder.companyImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      if(loggedType == UserUtils.LOGGED_COMPANY){
                        if(menuUserListener != null) menuUserListener.open(loggedType, userId, user, loggedCompany, country);
                      }else{
                        //register a company or login to company
                        if(loggedCompany != null && loggedCompany.getId() > 0){
                          TabUtils.loginToCompany(mActivity);
                        }else{
                          Intent intent = new Intent(mActivity, RegisterCompanyView.class);
                          mActivity.startActivity(intent);
                        }
                      }
                    }
                });
              }

              if(logoViewHolder.txtBalanceHeader != null)
                logoViewHolder.txtBalanceHeader.setText(
                  country == null ? "" :
                  NumberUtils.currencySymbol(country.getCurrency().getId(), country.getId(), user.getLanguage())+
                  NumberUtils.realToInt(wallet.getBalance()));
              if(logoViewHolder.txtBalanceHeaderCents != null)
                logoViewHolder.txtBalanceHeaderCents.setText(
                  country == null ? "" :
                  "."+NumberUtils.realToDecimals(wallet.getBalance()));

              // if(logoViewHolder.txtYourId != null)
              //   logoViewHolder.txtYourId.setText(""+userId);
              //
              // if(logoViewHolder.btnCopyId != null) {
              //     logoViewHolder.btnCopyId.setOnClickListener(new View.OnClickListener() {
              //         @Override
              //         public void onClick(View v) {
              //           ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
              //           ClipData clip = ClipData.newPlainText("Wooeen user ID", ""+userId);
              //           clipboard.setPrimaryClip(clip);
              //
              //           Toast.makeText(mActivity, ""+userId ,Toast.LENGTH_LONG).show();
              //         }
              //     });
              // }

              if(logoViewHolder.btnCashbackHeader != null) {
                logoViewHolder.btnCashbackHeader.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_DASHBOARD_URL);
                    }
                });
              }
            }else{
              if(logoViewHolder.panelBalance != null){
                logoViewHolder.panelBalance.setVisibility(View.GONE);
              }

              if(logoViewHolder.panelBtn != null){
                logoViewHolder.panelBtn.setVisibility(View.GONE);
              }

              if(logoViewHolder.btnActiveCashback != null){
                logoViewHolder.btnActiveCashback.setVisibility(View.VISIBLE);
                logoViewHolder.btnActiveCashback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //open auth wooeen
                      Intent intent = new Intent(mActivity, LoginView.class);
                      mActivity.startActivity(intent);
                    }
                });
              }

              if(logoViewHolder.userImage != null){
                logoViewHolder.userImage.setImageResource(R.drawable.woe_ic_head_user);
                // logoViewHolder.userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                logoViewHolder.userImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      //open auth wooeen
                      Intent intent = new Intent(mActivity, LoginView.class);
                      mActivity.startActivity(intent);
                    }
                });
              }

              if(logoViewHolder.companyImage != null){
                // logoViewHolder.companyImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                logoViewHolder.companyImage.setImageResource(R.drawable.woe_ic_head_company);
                logoViewHolder.companyImage.setVisibility(View.GONE);
              }

              if(logoViewHolder.txtBalanceHeader != null)
                logoViewHolder.txtBalanceHeader.setText("");

              if(logoViewHolder.txtBalanceHeaderCents != null)
                logoViewHolder.txtBalanceHeaderCents.setText("");
            }

        }else if (holder instanceof SearchBoxViewHolder) {
            SearchBoxViewHolder searchViewHolder = (SearchBoxViewHolder) holder;

            // searchViewHolder.tilesView.setBackgroundResource(R.color.woe_bg);

        }else if (holder instanceof TopSitesViewHolder) {
            // mMvTilesContainerLayout.setBackgroundResource(R.drawable.rounded_dark_bg_alpha);
            mMvTilesContainerLayout.setBackgroundResource(R.color.woe_bg);

        }else if (holder instanceof ButtonsViewHolder) {
            ButtonsViewHolder buttonsViewHolder = (ButtonsViewHolder) holder;
            int buttons = 0;

            /*
            * Initialize games button
            */
            buttonsViewHolder.btnGames.setVisibility(View.GONE);

            /*
            * Initialize challenges button
            */
            if(userId <= 0 || loggedType == UserUtils.LOGGED_USER){
              ((FrameLayout)buttonsViewHolder.btnChallenges.getParent()).setVisibility(View.VISIBLE);
              buttonsViewHolder.btnChallenges.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      if(userId <= 0) {
                          // Singular.event("button_tasks");

                          //open auth wooeen
                          Intent intent = new Intent(mActivity, LoginView.class);
                          mActivity.startActivity(intent);
                      }else{
                          TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_CHALLENGES_URL);
                      }
                  }
              });

              Picasso.with(mActivity).load(R.drawable.bg_buttons_orange).into(buttonsViewHolder.btnChallengesImg);
              buttonsViewHolder.btnChallengesImg.setClipToOutline(true);

              buttons++;
            }else{
              buttonsViewHolder.btnChallengesCont.setVisibility(View.GONE);
            }

            /*
            * Initialize chat button
            */
            if(country == null || TextUtils.isEmpty(country.getId()) ||
                "BR".equalsIgnoreCase(country.getId()) ||
                "ES".equalsIgnoreCase(country.getId())){
              buttonsViewHolder.btnChat.setVisibility(View.VISIBLE);

              if(loggedType == UserUtils.LOGGED_COMPANY){
                ((LinearLayout.LayoutParams) buttonsViewHolder.btnChatCont.getLayoutParams()).setMarginStart(0);//remove button margin
              }

              buttonsViewHolder.btnChat.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    // Singular.event("button_chat");

                    String phone = "376350991";
                    String message = "";

                    if(userId > 0 && user != null)
                      message = (country != null ? country.getId() : user.getCountry().getId())+" \n"
                                + "ID: "+userId+"\n"
                                + user.getName();

                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);

                        String url = "https://api.whatsapp.com/send?phone="+ phone +"&text=" + URLEncoder.encode(message, "UTF-8");
                        i.setPackage("com.whatsapp");
                        i.setData(Uri.parse(url));
                        mActivity.startActivity(i);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                  }
              });

              Picasso.with(mActivity).load(R.drawable.bg_buttons_green).into(buttonsViewHolder.btnChatImg);
              buttonsViewHolder.btnChatImg.setClipToOutline(true);

              buttons++;

            }else{
              buttonsViewHolder.btnChat.setVisibility(View.GONE);

              if(loggedType == UserUtils.LOGGED_COMPANY){
                ((LinearLayout.LayoutParams) buttonsViewHolder.btnRecommendationCont.getLayoutParams()).setMarginStart(0);//remove button margin
              }

            }

            /*
            * initialize recommendation button
            */
            buttons++;
            buttonsViewHolder.btnRecommendation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(userId <= 0) {
                        // Singular.event("button_referral");

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
                        }
                    }
                }
            });

            Picasso.with(mActivity).load(R.drawable.bg_buttons_blue).into(buttonsViewHolder.btnRecommendationImg);
            buttonsViewHolder.btnRecommendationImg.setClipToOutline(true);

            //Calc buttons line 1 height
            if(buttons == 2){
              buttonsViewHolder.btnChallengesCont.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mActivity.getResources().getDisplayMetrics());
              buttonsViewHolder.btnChatCont.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mActivity.getResources().getDisplayMetrics());
              buttonsViewHolder.btnRecommendationCont.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, mActivity.getResources().getDisplayMetrics());
            }

            /*
            * initialize advertisers button
            */
            buttonsViewHolder.btnAdvertisers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Singular.event("button_advertisers");

                    TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_ADVERTISERS_URL+"?cr="+UserUtils.getCrFinal(mActivity));
                }
            });

            /*
            * initialize company button
            */
            if((loggedCompany != null && loggedCompany.getId() > 0) || userId <= 0 ||
                  country.getCategoryB2b() == null || country.getCategoryB2b().getId() <= 0){
              buttonsViewHolder.btnCompany.setVisibility(View.GONE);
              ((LinearLayout.LayoutParams) buttonsViewHolder.btnAdvertisers.getLayoutParams()).setMarginEnd(0);//remove button margin
            }else{
              buttonsViewHolder.btnCompany.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    Intent intent = new Intent(mActivity, RegisterCompanyView.class);
                    mActivity.startActivity(intent);
                  }
              });
            }

        }else if (holder instanceof WidgetViewHolder) {
            WidgetViewHolder widgetViewHolder = (WidgetViewHolder) holder;

            if(userId > 0 && user != null && wallet != null){
              widgetViewHolder.woeWidget.setVisibility(View.VISIBLE);

              /*
              * TABS
              */
              if(widgetViewHolder.getWidgetActived() <= 0){
                widgetViewHolder.changeWidget(mActivity, WooeenSettings.getWidgetActived(mActivity));
              }

              if(widgetViewHolder.woeWidgetBalanceBtn != null)
                widgetViewHolder.woeWidgetBalanceBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetViewHolder.changeWidget(mActivity, WidgetViewHolder.widgetBalance);
                    }
                });

              if(widgetViewHolder.woeWidgetCashbackBtn != null)
                widgetViewHolder.woeWidgetCashbackBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetViewHolder.changeWidget(mActivity, WidgetViewHolder.widgetCashback);
                    }
                });

              if(widgetViewHolder.woeWidgetRecommendationsBtn != null)
                widgetViewHolder.woeWidgetRecommendationsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        widgetViewHolder.changeWidget(mActivity, WidgetViewHolder.widgetRecommendations);
                    }
                });
              if(widgetViewHolder.woeWidgetAffBtn != null)
                widgetViewHolder.woeWidgetAffBtn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      widgetViewHolder.changeWidget(mActivity, WidgetViewHolder.widgetAff);
                  }
              });

              /*
              * BALANCE
              */
              if(widgetViewHolder.txtBalance != null)
                widgetViewHolder.txtBalance.setText(country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getBalance()));

              if(widgetViewHolder.btnWithdrawn != null){
                if(wallet.getBalance() >= MIN_WITHDRAWN) {
                    widgetViewHolder.btnWithdrawn.setText(mActivity.getString(R.string.woe_widget_balance_btn));
                    widgetViewHolder.btnWithdrawn.setEnabled(true);
                }else{
                    String withdrawDisableText = mActivity.getString(R.string.woe_widget_balance_btn_disabled);
                    withdrawDisableText = withdrawDisableText.replace("[[AMOUNT]]", country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), MIN_WITHDRAWN));
                    widgetViewHolder.btnWithdrawn.setText(withdrawDisableText);
                    widgetViewHolder.btnWithdrawn.setEnabled(false);
                }
              }

              /*
              * CASHBACK
              */
              if(widgetViewHolder.txtCashbackVerified != null)
                widgetViewHolder.txtCashbackVerified.setText(country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getAmountApproved()));

              if(widgetViewHolder.txtCashbackPending != null)
                widgetViewHolder.txtCashbackPending.setText(country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getAmountPending() + wallet.getAmountRegistered()));

              if(widgetViewHolder.btnCashbackReport != null) {
                widgetViewHolder.btnCashbackReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      TabUtils.openUrlInSameTab("https://claro.wwwd.com.br/u/conversions");
                    }
                });
              }

              /*
              * RECOMMENDATIONS
              */
              if(widgetViewHolder.txtRecommendationsQuantity != null)
                widgetViewHolder.txtRecommendationsQuantity.setText(NumberUtils.integerToStringMask(wallet.getRecommendationsRegistered() + wallet.getRecommendationsConverted() + wallet.getRecommendationsConfirmed()));

              if(widgetViewHolder.txtRecommendationsPending != null)
                widgetViewHolder.txtRecommendationsPending.setText(
                        country == null ? "" :
                        NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(),
                                wallet.getRecommendationsRegisteredAmount() + wallet.getRecommendationsConvertedAmount())
                );

              if(widgetViewHolder.txtRecommendationsApproved != null)
                widgetViewHolder.txtRecommendationsApproved.setText(
                          country == null ? "" :
                          NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(),
                                  wallet.getRecommendationsConfirmedAmount())
                  );

              if(widgetViewHolder.btnRecommendationsReport != null) {
                  widgetViewHolder.btnRecommendationsReport.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          TabUtils.openUrlInSameTab("https://claro.wwwd.com.br/u/recommendations");
                        }
                    });
              }

              /*
              * AFF
              */
              if(widgetViewHolder.txtAffVerified != null)
                widgetViewHolder.txtAffVerified.setText(country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getAffAmountApproved()));

              if(widgetViewHolder.txtAffPending != null)
                widgetViewHolder.txtAffPending.setText(country == null ? "" : NumberUtils.realToString(country.getCurrency().getId(), country.getId(), user.getLanguage(), wallet.getAffAmountPending() + wallet.getAffAmountRegistered()));

              if(widgetViewHolder.btnAffReport != null) {
                widgetViewHolder.btnAffReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      TabUtils.openUrlInSameTab("https://claro.wwwd.com.br/u/conversions-aff");
                    }
                });
              }


            }else{
              widgetViewHolder.woeWidget.setVisibility(View.GONE);
            }

          }else if (holder instanceof UpdateViewHolder) {
            UpdateViewHolder updateViewHolder = (UpdateViewHolder) holder;

            updateViewHolder.ntpWoeUpdateLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  final String appPackageName = mActivity.getPackageName();
                  try {
                      mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                  } catch (android.content.ActivityNotFoundException anfe) {
                      mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                  }
                }
            });

            updateViewHolder.ntpWoeUpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  final String appPackageName = mActivity.getPackageName();
                  try {
                      mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                  } catch (android.content.ActivityNotFoundException anfe) {
                      mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                  }
                }
            });

          } else if (holder instanceof BannerViewHolder) {
            BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;

            bannerViewHolder.ntpWoeBannerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  TabUtils.openUrlInSameTab(BraveNewTabPageLayout.WOOEEN_HELP_URL);
                }
            });

          }else if (holder instanceof PubViewHolder ||
                    holder instanceof PostViewHolder ||
                    holder instanceof TaskViewHolder ||
                    holder instanceof RetargetingViewHolder) {
            int pubPosition = position - getCountItemsFixed();
            if (pubPosition >=0 && pubPosition < pubs.size()) {
                PubItemHolder item = pubs.get(pubPosition);
                if(item.getType() == PubItemHolder.PUB && item.getPub() != null) {
                    if (holder instanceof PubViewHolder) {
                        PubViewHolder pubViewHolder = (PubViewHolder) holder;
                        PubTO pub = item.getPub();
                        PubViewUtils.toFront(mActivity, country, user, token, pub, pubViewHolder);

                        if(pub.getUser() != null && pub.getUser().getId() > 0){
                            if(!TextUtils.isEmpty(pub.getUser().getPhoto())) {
                                Picasso.with(mActivity).load(pub.getUser().getPhoto())
                                        .error(R.drawable.woe_no_image_person)
                                        .fit().centerCrop().transform(new CircleTransform()).into(
                                        pubViewHolder.userImage);
                                pubViewHolder.userImage
                                        .setVisibility(View.VISIBLE);
                            }else{
                                pubViewHolder.userImage.setImageResource(R.drawable.woe_no_image_person);
                            }
                            pubViewHolder.userName.setText(pub.getUser().getName() != null && pub.getUser().getName().contains(" ") ? pub.getUser().getName().split(" ")[0] : pub.getUser().getName());
                        }else{
                            pubViewHolder.userImage.setImageResource(R.drawable.woe_no_image_person);
                            pubViewHolder.userName.setVisibility(View.INVISIBLE);
                        }

                        pubViewHolder.date.setText(
                            DatetimeUtils.getRelativeTimeFormat(
                                    pub.getDatePublish(),
                                    mActivity.getString(R.string.woe_days),
                                    mActivity.getString(R.string.woe_hours),
                                    mActivity.getString(R.string.woe_minutes),
                                    mActivity.getString(R.string.woe_ago)));

                        if(pub.getAdvertisers() != null && !pub.getAdvertisers().isEmpty()){
                            PubTO.PubAdvertiserTOA ad = pub.getAdvertisers().get(0);
                            if(!TextUtils.isEmpty(ad.getLogo())) {
                                Picasso.with(mActivity).load(ad.getLogo()).fit().into(
                                        pubViewHolder.faviconImage);
                                pubViewHolder.faviconBox.setVisibility(View.VISIBLE);
                            }else{
                                pubViewHolder.faviconBox.setVisibility(View.INVISIBLE);
                            }
                            if(TextUtils.isEmpty(ad.getColor())) {
                                pubViewHolder.faviconDrawable.setColor(Color.parseColor("#FFFFFF"));
                                pubViewHolder.faviconBox.setClipToOutline(true);
                            }else{
    //                            pubViewHolder.faviconBox.setPadding(10, 10, 10, 10);
                                pubViewHolder.faviconDrawable.setColor(Color.parseColor(ad.getColor()));
                            }

                            pubViewHolder.name.setText(ad.getName());
                        }else{
                            pubViewHolder.faviconBox.setVisibility(View.INVISIBLE);
                            pubViewHolder.name.setVisibility(View.INVISIBLE);
                        }

                        final int ps = position;
                        pubViewHolder.btnMore.setOnClickListener(new View.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                //creating a popup menu
                                PopupMenu popup = new PopupMenu(mActivity, pubViewHolder.btnMore);
                                //inflating menu from xml resource
                                popup.inflate(R.menu.woe_pub_item_menu);
                                //set items by logical pub
                                Menu menuOpts = popup.getMenu();
                                String tag = null;
                                if(pub.getTags() != null && !pub.getTags().isEmpty()){
                                    tag = pub.getTags().get(new Random().nextInt(pub.getTags().size()));
                                    if(tag != null){
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            tag = Html.fromHtml(tag, Html.FROM_HTML_MODE_COMPACT).toString();
                                        } else {
                                            tag = Html.fromHtml(tag).toString();
                                        }
                                    }
                                    menuOpts.getItem(1).setTitle(mActivity.getString(R.string.woe_not_interested_in_x).replace("[[TAG]]", tag));
                                    menuOpts.getItem(1).setVisible(true);
                                }else{
                                    menuOpts.getItem(1).setVisible(false);
                                }

                                int advertiser = 0;
                                if(pub.getAdvertisers() != null && !pub.getAdvertisers().isEmpty()){
                                    PubTO.PubAdvertiserTOA adv = pub.getAdvertisers().get(0);
                                    advertiser = adv.getId();
                                    menuOpts.getItem(2).setTitle(String.format(mActivity.getString(R.string.woe_not_show_x).replace("[[TAG]]", adv.getName())));
                                    menuOpts.getItem(2).setVisible(true);
                                }else{
                                    menuOpts.getItem(2).setVisible(false);
                                }

                                if(pub.getUser() != null && user != null && pub.getUser().getId() == userId){
                                    menuOpts.getItem(3).setVisible(true);
                                }else{
                                    menuOpts.getItem(3).setVisible(false);
                                }

                                //adding click listener
                                final String finalTag = tag;
                                final int finalAdvertiser = advertiser;
                                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem item) {
                                      if(item.getItemId() == R.id.woe_exclude_pub){
                                          pubs.remove(pubPosition);
                                          notifyItemRemoved(ps);
                                          new ExcludePubLoader(mActivity, pub).executeOnExecutor(
                                                  AsyncTask.THREAD_POOL_EXECUTOR);
                                      }else if(item.getItemId() == R.id.woe_exclude_tag){
                                          if(!TextUtils.isEmpty(finalTag)) {
                                              pubs.remove(pubPosition);
                                              notifyItemRemoved(ps);
                                              new ExcludeTagLoader(mActivity,
                                                      finalTag).executeOnExecutor(
                                                      AsyncTask.THREAD_POOL_EXECUTOR);
                                          }
                                      }else if(item.getItemId() == R.id.woe_exclude_advertiser){
                                          if(!TextUtils.isEmpty(finalAdvertiser)) {
                                              pubs.remove(pubPosition);
                                              notifyItemRemoved(ps);
                                              new ExcludeAdvertiserLoader(mActivity,
                                                      finalAdvertiser).executeOnExecutor(
                                                      AsyncTask.THREAD_POOL_EXECUTOR);
                                          }
                                      }else if(item.getItemId() == R.id.woe_remove){
                                          pubs.remove(pubPosition);
                                          notifyItemRemoved(ps);
                                          new PubRemoveLoader(mActivity, pub).executeOnExecutor(
                                                  AsyncTask.THREAD_POOL_EXECUTOR);
                                      }
                                      return true;
                                    }
                                });
                                //displaying the popup
                                popup.show();
                            }
                        });

                        pubViewHolder.btnLike.setActivated(pub.getLiked());
                        pubViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!pub.getLiked()) {
                                    pub.setLiked(true);
                                    pub.setTotalLikes(pub.getTotalLikes() + 1);
                                    pubViewHolder.btnLike.setActivated(true);
                                } else {
                                    pub.setLiked(false);
                                    pub.setTotalLikes(pub.getTotalLikes() - 1);
                                    pubViewHolder.btnLike.setActivated(false);
                                }

                                if (pub.getTotalLikes() > 0) {
                                    pubViewHolder.btnLike.setText(
                                            NumberUtils.printBigNumbers(pub.getTotalLikes()));
                                    pubViewHolder.btnLike.setCompoundDrawablePadding(20);
                                } else {
                                    pubViewHolder.btnLike.setText("");
                                    pubViewHolder.btnLike.setCompoundDrawablePadding(0);
                                }

                                //send to dao
                                new PubLikeLoader(mActivity, pub).executeOnExecutor(
                                        AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });

                        pubViewHolder.btnShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pub.setTotalShares(pub.getTotalShares() + 1);

                                if (pub.getTotalShares() > 0) {
                                    pubViewHolder.btnShare.setText(
                                            NumberUtils.printBigNumbers(pub.getTotalShares()));
                                    pubViewHolder.btnShare.setCompoundDrawablePadding(20);
                                } else {
                                    pubViewHolder.btnShare.setText("");
                                    pubViewHolder.btnShare.setCompoundDrawablePadding(0);
                                }

                                //send to dao
                                new PubShareLoader(mActivity, pub).executeOnExecutor(
                                        AsyncTask.THREAD_POOL_EXECUTOR);

                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                PubViewUtils.toShare(mActivity, country, user, pub, sendIntent);
                                sendIntent.setType("text/plain");
                                mActivity.startActivity(sendIntent);
                            }
                        });

                        if (pub.getTotalLikes() > 0) {
                            pubViewHolder.btnLike.setText(
                                    NumberUtils.printBigNumbers(pub.getTotalLikes()));
                            pubViewHolder.btnLike.setCompoundDrawablePadding(20);
                        }else {
                            pubViewHolder.btnLike.setText("");
                            pubViewHolder.btnLike.setCompoundDrawablePadding(0);
                        }

                        if (pub.getTotalComments() > 0) {
                            pubViewHolder.btnComment.setText(
                                    NumberUtils.printBigNumbers(pub.getTotalComments()));
                            pubViewHolder.btnComment.setCompoundDrawablePadding(20);
                        }

                        if (pub.getTotalShares() > 0) {
                            pubViewHolder.btnShare.setText(
                                    NumberUtils.printBigNumbers(pub.getTotalShares()));
                            pubViewHolder.btnShare.setCompoundDrawablePadding(20);
                        }else {
                            pubViewHolder.btnShare.setText("");
                            pubViewHolder.btnShare.setCompoundDrawablePadding(0);
                        }

                        if (pub.getLoadingCoupons() == 2 && pub.getCoupons() != null
                                && !pub.getCoupons().isEmpty()) {
                            pubViewHolder.couponsContainer.setVisibility(View.VISIBLE);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(
                                    pubViewHolder.coupons.getContext(),
                                    LinearLayoutManager.HORIZONTAL,
                                    false);
                            layoutManager.setInitialPrefetchItemCount(pub.getCoupons().size());
                            CouponItemAdapter childItemAdapter = new CouponItemAdapter(mActivity,
                                    pub.getCoupons(),
                                    true);
                            pubViewHolder.coupons.setLayoutManager(layoutManager);
                            pubViewHolder.coupons.setAdapter(childItemAdapter);
                            pubViewHolder.coupons.setRecycledViewPool(viewPool);
                        }else{
                            pubViewHolder.couponsContainer.setVisibility(View.GONE);
                        }

                        //Load coupons whem multiple 5
                        if (pubPosition % 5 == 0 && pub.getLoadingCoupons() <= 0) {
                            new GetPubCouponsLoader(position, pub).executeOnExecutor(
                                    AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }else if(item.getType() == PubItemHolder.POST && item.getPost() != null) {
                    if (holder instanceof PostViewHolder) {
                        PostViewHolder postViewHolder = (PostViewHolder) holder;
                        PostTO post = item.getPost();

                        postViewHolder.postHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TabUtils.openUrlInSameTab(post.getLink());
                            }
                        });

                        if(post.getAuthorName() != null){
                            if(!TextUtils.isEmpty(post.getAuthorPhoto())) {
                                Picasso.with(mActivity).load(post.getAuthorPhoto())
                                        .error(R.drawable.woe_no_image_person)
                                        .fit().centerCrop().transform(new CircleTransform()).into(
                                        postViewHolder.userImage);
                                postViewHolder.userImage
                                        .setVisibility(View.VISIBLE);
                            }else{
                                postViewHolder.userImage.setImageResource(R.drawable.woe_no_image_person);
                            }
                            postViewHolder.userName.setText(post.getAuthorName());
                        }else{
                            postViewHolder.userImage.setImageResource(R.drawable.woe_no_image_person);
                            postViewHolder.userName.setVisibility(View.INVISIBLE);
                        }

                        postViewHolder.date.setText(
                                DatetimeUtils.getRelativeTimeFormat(
                                        post.getDate(),
                                        mActivity.getString(R.string.woe_days),
                                        mActivity.getString(R.string.woe_hours),
                                        mActivity.getString(R.string.woe_minutes),
                                        mActivity.getString(R.string.woe_ago)));

                        if (postViewHolder.postImage != null) {
                            Picasso.with(mActivity).load(post.getImage()).transform(new RoundedCornersTransformation(60, 0)).into(postViewHolder.postImage);
                            postViewHolder.postImage.setClipToOutline(true);
                        }

                        if (!TextUtils.isEmpty(post.getTitle()))
                            postViewHolder.postTitle.setText(post.getTitle());
                        else
                            postViewHolder.postTitle.setText("");

                        if (!TextUtils.isEmpty(post.getExcerpt())){
                          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                              postViewHolder.postDesc.setText(
                                      Html.fromHtml(post.getExcerpt(), Html.FROM_HTML_MODE_COMPACT).toString());
                          } else {
                              postViewHolder.postDesc.setText(Html.fromHtml(post.getExcerpt()).toString());
                          }
                        }else
                            postViewHolder.postDesc.setText("");

                        if (postViewHolder.postShare != null) {
                            postViewHolder.postShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent sendIntent = new Intent();

                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TITLE, post.getTitle());
                                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, post.getTitle());
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, post.getLink());
                                    sendIntent.setType("text/plain");
                                    mActivity.startActivity(sendIntent);
                                }
                            });
                        }
                    }
                }else if(item.getType() == PubItemHolder.TASK && item.getTask() != null) {
                    if (holder instanceof TaskViewHolder) {
                        TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
                        TaskTO task = item.getTask();

                        taskViewHolder.taskHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!TextUtils.isEmpty(task.getUrl())) {
                                    UserUtils.saveUserTrkRtg(mActivity);

                                    String linkTracked =
                                            TrackingUtils.parseTrackingLink(
                                                    task.getUrl(),
                                                    null,
                                                    task.getUrl(),
                                                    userId,
                                                    TrackingUtils.SOURCE_SOCIAL,
                                                    0);
                                    TabUtils.openUrlInSameTab(linkTracked);
                                }else {
                                    TabUtils.openUrlInSameTab(
                                            "https://claro.wwwd.com.br/u/task/get?id=" + task.getId());
                                }
                            }
                        });

                        if (taskViewHolder.taskImage != null) {
                            Picasso.with(mActivity).load(task.getMedia()).into(taskViewHolder.taskImage);
                            taskViewHolder.taskImage.setClipToOutline(true);
                        }

                        if (!TextUtils.isEmpty(task.getTitle())) {
                            taskViewHolder.taskImageTitle.setText(task.getTitle());
                            taskViewHolder.taskTitle.setText(task.getTitle());
                        }else {
                            taskViewHolder.taskImageTitle.setText("");
                            taskViewHolder.taskTitle.setText("");
                        }

                        if (!TextUtils.isEmpty(task.getDescription())){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                taskViewHolder.taskDesc.setText(
                                        Html.fromHtml(task.getDescription(), Html.FROM_HTML_MODE_COMPACT).toString());
                            } else {
                                taskViewHolder.taskDesc.setText(Html.fromHtml(task.getDescription()).toString());
                            }
                        }else
                            taskViewHolder.taskDesc.setText("");

                        if (taskViewHolder.taskShare != null) {
                            taskViewHolder.taskShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent sendIntent = new Intent();

                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TITLE, task.getTitle());
                                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, task.getTitle());
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://claro.wwwd.com.br/u/task/get?id=" + task.getId());
                                    sendIntent.setType("text/plain");
                                    mActivity.startActivity(sendIntent);
                                }
                            });
                        }
                    }
                }else if(item.getType() == PubItemHolder.RETARGETING && item.getRetargeting() != null) {
                    if (holder instanceof RetargetingViewHolder) {
                        RetargetingViewHolder retargetingViewHolder = (RetargetingViewHolder) holder;
                        List<NavigationTO> navigations = item.getRetargeting();

                        if(navigations.size() >= 1){
                            NavigationTO nav = navigations.get(0);
                            retargetingViewHolder.item1.setVisibility(View.VISIBLE);
                            PubViewUtils.RetargetingToFront(nav, user, country, mActivity,
                                    retargetingViewHolder.item1,
                                    retargetingViewHolder.item1Image,
                                    retargetingViewHolder.item1ImageMedia,
                                    retargetingViewHolder.item1Title,
                                    retargetingViewHolder.item1Amount,
                                    retargetingViewHolder.item1AmountOld,
                                    retargetingViewHolder.item1FaviconBox,
                                    retargetingViewHolder.item1FaviconDrawable,
                                    retargetingViewHolder.item1FaviconImage,
                                    retargetingViewHolder.item1Name);
                        }else{
                            retargetingViewHolder.item1.setVisibility(View.GONE);
                        }
                        if(navigations.size() >= 2){
                            NavigationTO nav = navigations.get(1);
                            retargetingViewHolder.item2.setVisibility(View.VISIBLE);
                            PubViewUtils.RetargetingToFront(nav, user, country, mActivity,
                                    retargetingViewHolder.item2,
                                    retargetingViewHolder.item2Image,
                                    retargetingViewHolder.item2ImageMedia,
                                    retargetingViewHolder.item2Title,
                                    retargetingViewHolder.item2Amount,
                                    retargetingViewHolder.item2AmountOld,
                                    retargetingViewHolder.item2FaviconBox,
                                    retargetingViewHolder.item2FaviconDrawable,
                                    retargetingViewHolder.item2FaviconImage,
                                    retargetingViewHolder.item2Name);
                        }else{
                            retargetingViewHolder.item2.setVisibility(View.GONE);
                        }
                        if(navigations.size() >= 3){
                            NavigationTO nav = navigations.get(2);
                            retargetingViewHolder.item3.setVisibility(View.VISIBLE);
                            PubViewUtils.RetargetingToFront(nav, user, country, mActivity,
                                    retargetingViewHolder.item3,
                                    retargetingViewHolder.item3Image,
                                    retargetingViewHolder.item3ImageMedia,
                                    retargetingViewHolder.item3Title,
                                    retargetingViewHolder.item3Amount,
                                    retargetingViewHolder.item3AmountOld,
                                    retargetingViewHolder.item3FaviconBox,
                                    retargetingViewHolder.item3FaviconDrawable,
                                    retargetingViewHolder.item3FaviconImage,
                                    retargetingViewHolder.item3Name);
                        }else{
                            retargetingViewHolder.item3.setVisibility(View.GONE);
                        }
                        if(navigations.size() >= 4){
                            NavigationTO nav = navigations.get(3);
                            retargetingViewHolder.item4.setVisibility(View.VISIBLE);
                            PubViewUtils.RetargetingToFront(nav, user, country, mActivity,
                                    retargetingViewHolder.item4,
                                    retargetingViewHolder.item4Image,
                                    retargetingViewHolder.item4ImageMedia,
                                    retargetingViewHolder.item4Title,
                                    retargetingViewHolder.item4Amount,
                                    retargetingViewHolder.item4AmountOld,
                                    retargetingViewHolder.item4FaviconBox,
                                    retargetingViewHolder.item4FaviconDrawable,
                                    retargetingViewHolder.item4FaviconImage,
                                    retargetingViewHolder.item4Name);
                        }else{
                            retargetingViewHolder.item4.setVisibility(View.GONE);
                        }

                        retargetingViewHolder.btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TabUtils.openUrlInSameTab("https://claro.wwwd.com.br/u/history");
                            }
                        });
                    }
                }
            }
          }
    }

    private class UserQuickAccessTask extends AsyncTask<UserQuickAccessTO>{

        private int userId;
        private ProgressBar mWoeCbdProgress;
        private Button btnCashback;

        public UserQuickAccessTask(int userId, ProgressBar mWoeCbdProgress, Button btnCashback){
            this.userId = userId;
            this.mWoeCbdProgress = mWoeCbdProgress;
            this.btnCashback = btnCashback;
        }

        @Override
        protected UserQuickAccessTO doInBackground() {
            UserAPI apiDAO = new UserAPI(token);
            return apiDAO.quickAccessNew();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(mWoeCbdProgress != null) mWoeCbdProgress.setVisibility(View.VISIBLE);
            if(btnCashback != null ) btnCashback.setEnabled(false);
        }

        @Override
        protected void onPostExecute(UserQuickAccessTO result) {
            if(mWoeCbdProgress != null) mWoeCbdProgress.setVisibility(View.INVISIBLE);
            if(btnCashback != null ) btnCashback.setEnabled(true);

            if (isCancelled()) return;

            if(result != null){
                String uqa = WoeDAOUtils.BASE_URL+"u/uqa?u="+userId+"&i="+result.getId()+"&v="+result.getValidator();
                TabUtils.openUrlInSameTab(uqa);
            }
        }
    }

    public void loadMore(){
        if(hasMore && !isLoadingMore){
            isLoadingMore = true;

            pubPage++;
            postPage++;
            taskPage++;

            pubsLoader = new GetPubsLoader();
            pubsLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class GetPubsLoader extends AsyncTask<List<PubItemHolder>> {

        private List<PubTO> pubsList;

        public GetPubsLoader(){

        }

        @Override
        protected List<PubItemHolder> doInBackground() {
            //load new pubs
            PubTOP pubTOP = new PubTOP();
            pubTOP.setPg(pubPage);
            pubTOP.setQtdPerPage(pub_size_page);
            if(loggedType == UserUtils.LOGGED_COMPANY) {
                pubTOP.setB2b(1);
            }
            PubAPI apiDAO = new PubAPI(token);
            pubsList = apiDAO.get(pubTOP);

            if(pubsList == null){
                hasMore = false;
                isLoadingMore = false;
                return null;
            }

            //load cache posts
            PostTOP postTOP = new PostTOP();
            postTOP.setPage(postPage);
            postTOP.setQtdPerPage(post_size_page);
            PostDAO postDAO = new PostDAO(mActivity.getContentResolver());
            List<PostTO> posts = postDAO.get(postTOP);

            //load cache tasks if is an user
            List<TaskTO> tasks = null;
            if(loggedType == UserUtils.LOGGED_USER){
              TaskTOP taskTOP = new TaskTOP();
              taskTOP.setPage(taskPage);
              taskTOP.setQtdPerPage(task_size_page);
              TaskDAO taskDAO = new TaskDAO(mActivity.getContentResolver());
              tasks = taskDAO.get(taskTOP);
            }

            //load cache navigation
            List<NavigationTO> retarg = NavigationUtils.getCache(mActivity);

            /*
            * Mount the result with 5 Pubs and 1 Post in sequence
             */
            List<PubItemHolder> result = new ArrayList<PubItemHolder>();
            int postCount = 0;
            int postIndice = 0;
            int taskCount = 2;
            int taskIndice = 0;
            for(int i = 0; i < pubsList.size(); i++){
                PubTO p = pubsList.get(i);
                result.add(new PubItemHolder(p));
                postCount++;
                taskCount++;

                if(postCount == 4){
                    if(posts != null && postIndice < posts.size()){
                        result.add(new PubItemHolder(posts.get(postIndice)));
                        postIndice++;
                    }
                    postCount = 0;
                }

                if(taskCount == 4){
                    if(tasks != null && taskIndice < tasks.size()){
                        result.add(new PubItemHolder(tasks.get(taskIndice)));
                        taskIndice++;
                    }
                    taskCount = 0;
                }
            }

            if(retarg != null && !retarg.isEmpty()){
                int x = result.size() >= 3 ? new Random().nextInt(result.size() / 3) : result.size();
                if(x == 0 && result.size() > 1)
                    x++;
                if(x >= 0 && x < retarg.size())
                    result.add(x,new PubItemHolder(retarg));
            }

            if(pubsList.size() == pub_size_page){

                hasMore = true;
            }else{
                hasMore = false;
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!hasMore)
              isLoading = true;
        }

        @Override
        protected void onPostExecute(List<PubItemHolder> result) {
            isLoading = false;

            if (isCancelled()) return;

            if(result != null){
                if(isLoadingMore){
                  isLoadingMore = false;
                  if(pubs==null) pubs = new ArrayList<PubItemHolder>();
                  pubs.addAll(result);
                }else {
                  //update recycler
                  pubs = result;

                  //save cache
                  if(pubsList != null){
                    if(loggedType == UserUtils.LOGGED_USER)
                        PubUtils.saveCacheUser(mActivity, pubsList);
                    else if(loggedType == UserUtils.LOGGED_COMPANY)
                        PubUtils.saveCacheCompany(mActivity, pubsList);
                  }
                }
            }

            notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

            isLoading = false;
        }
    }

    private class PubLikeLoader extends AsyncTask<Boolean> {

        private Context context;
        private PubTO pub;

        public PubLikeLoader(Context context, PubTO pub){
            this.context = context;
            this.pub = pub;
        }
        @Override
        protected Boolean doInBackground() {
            PubAPI apiDAO = new PubAPI(token);
            return apiDAO.like(pub);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(loggedType == UserUtils.LOGGED_USER)
                PubUtils.setCacheLikeUser(context, pub);
            else if(loggedType == UserUtils.LOGGED_COMPANY)
                PubUtils.setCacheLikeCompany(context, pub);
        }

    }

    private class PubShareLoader extends AsyncTask<Boolean> {

        private Context context;
        private PubTO pub;

        public PubShareLoader(Context context, PubTO pub){
            this.context = context;
            this.pub = pub;
        }
        @Override
        protected Boolean doInBackground() {
            PubAPI apiDAO = new PubAPI(token);
            return apiDAO.share(pub);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
        }

    }

    private class PubRemoveLoader extends AsyncTask<Boolean> {

        private Context context;
        private PubTO pub;

        public PubRemoveLoader(Context context, PubTO pub){
            this.context = context;
            this.pub = pub;
        }
        @Override
        protected Boolean doInBackground() {
            PubAPI apiDAO = new PubAPI(token);
            return apiDAO.remove(pub);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

    }

    private class ExcludePubLoader extends AsyncTask<Boolean> {

        private Context context;
        private PubTO pub;

        public ExcludePubLoader(Context context, PubTO pub){
            this.context = context;
            this.pub = pub;
        }
        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI(token);
            return apiDAO.addExcludePub(pub);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

    }

    private class ExcludeAdvertiserLoader extends AsyncTask<Boolean> {

        private Context context;
        private int advertiser;

        public ExcludeAdvertiserLoader(Context context, int advertiser){
            this.context = context;
            this.advertiser = advertiser;
        }
        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI(token);
            return apiDAO.addExcludeAdvertiser(advertiser);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

    }

    private class ExcludeTagLoader extends AsyncTask<Boolean> {

        private Context context;
        private String tag;

        public ExcludeTagLoader(Context context, String tag){
            this.context = context;
            this.tag = tag;
        }
        @Override
        protected Boolean doInBackground() {
            UserAPI apiDAO = new UserAPI(token);
            return apiDAO.addExcludeTag(tag);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

        }

    }

    private class GetPubCouponsLoader extends AsyncTask<List<CouponTO>> {

        private PubTO pub;
        private int position;

        public GetPubCouponsLoader(int position, PubTO pub){
            this.position = position;
            this.pub = pub;
        }

        @Override
        protected List<CouponTO> doInBackground() {
            if(pub.getAdvertisers() == null || pub.getAdvertisers().isEmpty())
                return null;

            CouponAPI apiDAO = new CouponAPI(country.getId());
            return apiDAO.get(0, 10, pub.getAdvertisers().get(0).getId(), null);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pub.setLoadingCoupons(1);
        }

        @Override
        protected void onPostExecute(List<CouponTO> result) {
            pub.setLoadingCoupons(2);

            if (isCancelled()) return;

            if(result != null){
                //update recycler
                pub.setCoupons(result);
                notifyItemChanged(position);
            }
        }
    }

    @Override
    public int getItemCount() {
      int totalItems = getCountItemsFixed();

      //Layout BUTTONS E PUBS
      // totalItems += pubs.size();
      //
      // if(hasMore)
      //     totalItems++;

      return totalItems;
    }

    public int getCountItemsFixed(){
        // return 3+(hasUpdate?1:0)+(isLoading?1:0);
        return 4;
    }

    public int getTopSitesCount(){
        return TYPE_SEARCH_BOX;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_header, parent, false);
            mHeaderViewHolder = new HeaderViewHolder(view);
            return mHeaderViewHolder;
        }else if (viewType == TYPE_SEARCH) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_search, parent, false);
            mSearchViewHolder = new SearchViewHolder(view, mMvSearchContainerLayout);
            return mSearchViewHolder;
        }else if (viewType == TYPE_BUTTON) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_button, parent, false);
            mButtonViewHolder = new ButtonViewHolder(view);
            return mButtonViewHolder;
        }else if (viewType == TYPE_LOGO) {
            view = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.woe_ntp_logo, parent, false);
            return new LogoViewHolder(view);

        }else if (viewType == TYPE_SEARCH_BOX) {
            view = LayoutInflater.from(parent.getContext())
                           .inflate(R.layout.woe_ntp_searchbox, parent, false);
            return new SearchBoxViewHolder(view, mMvSearchContainerLayout, mMvTilesContainerLayout);

        }else if (viewType == TYPE_TOP_SITES) {
            return new TopSitesViewHolder(mMvTilesContainerLayout);

        } else if (viewType == TYPE_BUTTONS) {
            view = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.woe_ntp_buttons, parent, false);
            return new ButtonsViewHolder(view);

        } else if (viewType == TYPE_WIDGET) {
            view = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.woe_ntp_widget, parent, false);
            return new WidgetViewHolder(view);

        } else if (viewType == TYPE_UPDATE) {
            view = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.woe_ntp_update, parent, false);
            return new UpdateViewHolder(view);

        } else if (viewType == TYPE_BANNER) {
            view = LayoutInflater.from(parent.getContext())
                         .inflate(R.layout.woe_ntp_banner, parent, false);
            return new BannerViewHolder(view);

        } else if (viewType == TYPE_PUB_LOADING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_loading, parent, false);
            return new PubLoadingViewHolder(view);

        }else if (viewType == TYPE_PUB) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.woe_ntp_pub, parent, false);
            return new PubViewHolder(view);
        }else if (viewType == TYPE_POST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pub_post_item, parent, false);
            return new PostViewHolder(view);
        }else if (viewType == TYPE_TASK) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pub_task_item, parent, false);
            return new TaskViewHolder(view);
        }else if (viewType == TYPE_RETARGETING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.pub_retargeting_item, parent, false);
            return new RetargetingViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.woe_ntp_empty, parent, false);
            return new EmptyViewHolder(view);
        }
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public int getItemViewType(int position) {
      if(position == getCountItemsFixed() - 1 && isLoading)
          return TYPE_PUB_LOADING;
      else if(position >= getCountItemsFixed()) {
          int pubPosition = position - getCountItemsFixed();

          if (pubPosition >=0 && pubPosition < pubs.size()) {
              PubItemHolder item = pubs.get(pubPosition);
              if (item.getType() == PubItemHolder.PUB && item.getPub() != null) {
                  return TYPE_PUB;
              }else if (item.getType() == PubItemHolder.POST && item.getPost() != null) {
                  return TYPE_POST;
              }else if (item.getType() == PubItemHolder.TASK && item.getTask() != null) {
                    return TYPE_TASK;
              }else if (item.getType() == PubItemHolder.RETARGETING && item.getRetargeting() != null) {
                    return TYPE_RETARGETING;
              }

          }else if(pubPosition >= pubs.size() && hasMore){
            return TYPE_PUB_LOADING;
          }

          return TYPE_PUB;
      }

      return position;
    }

    private boolean refreshSearchHeight = false;
    private HeaderViewHolder mHeaderViewHolder;
    private SearchViewHolder mSearchViewHolder;
    private ButtonViewHolder mButtonViewHolder;

    private void refreshSearchHeight(){
        if(refreshSearchHeight) return;

        if(recyclerView != null &&
                mHeaderViewHolder != null &&
                mSearchViewHolder != null &&
                mButtonViewHolder != null){
            if(recyclerView.getHeight() > 0 &&
                    mHeaderViewHolder.ntpWoeHeaderLayout.getHeight() > 0 &&
                    mButtonViewHolder.ntpWoeButtonLayout.getHeight() > 0) {
                int height = recyclerView.getHeight();
                height -= mHeaderViewHolder.ntpWoeHeaderLayout.getHeight();
                height -= mButtonViewHolder.ntpWoeButtonLayout.getHeight();
                mSearchViewHolder.ntpWoeSearchLayout.getLayoutParams().height = height;
                refreshSearchHeight = true;

                recyclerView.post(new Runnable()
                {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ntpWoeHeaderLayout;

        Button headerMenu;

        TextView txtBalanceHeader;

        ProgressBar mWoeCbdProgress;
        Button btnCashbackHeader;

        Button btnActiveCashback;

        LinearLayout panelBalance;
        LinearLayout panelBtn;

        HeaderViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeHeaderLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_logo_layout);

            this.headerMenu = (Button) itemView.findViewById(R.id.woe_header_menu);

            this.panelBalance = (LinearLayout) itemView.findViewById(R.id.panel_balance);
            this.panelBtn = (LinearLayout) itemView.findViewById(R.id.panel_btn);

            this.txtBalanceHeader = (TextView) itemView.findViewById(R.id.txt_balance_header);
            this.mWoeCbdProgress = (ProgressBar) itemView.findViewById(R.id.woe_cbd_progress);
            this.btnCashbackHeader = (Button) itemView.findViewById(R.id.btn_cashback_header);

            this.btnActiveCashback = (Button) itemView.findViewById(R.id.btn_activate_cashback);
        }
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ntpWoeSearchLayout;

        SearchViewHolder(View itemView, View searchBox) {
            super(itemView);

            this.ntpWoeSearchLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_search_layout);

            FrameLayout ntpWoeSearhContainer = (FrameLayout) itemView.findViewById(R.id.woe_ntp_search_container);
            if(searchBox != null)
              ntpWoeSearhContainer.addView(searchBox);
        }
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ntpWoeButtonLayout;

        FrameLayout btnChatCont;
        LinearLayout btnChat;

        ButtonViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeButtonLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_button_layout);

            this.btnChatCont = (FrameLayout) itemView.findViewById(R.id.btn_buttons_chat_cont);
            this.btnChat = (LinearLayout) itemView.findViewById(R.id.btn_buttons_chat);
        }
    }

    public static class LogoViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ntpWoeLogoLayout;
        // ImageView wooeenLogo;
        ImageView userImage;
        boolean userImageLoaded;
        ImageView companyImage;
        boolean companyImageLoaded;
        TextView txtBalanceHeader;
        TextView txtBalanceHeaderCents;
        // TextView txtYourId;
        // Button btnCopyId;
        ProgressBar mWoeCbdProgress;
        Button btnCashbackHeader;

        Button btnActiveCashback;

        LinearLayout panelBalance;
        LinearLayout panelBtn;

        LogoViewHolder(View itemView) {
            super(itemView);

            this.ntpWoeLogoLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_logo_layout);

            this.panelBalance = (LinearLayout) itemView.findViewById(R.id.panel_balance);
            this.panelBtn = (LinearLayout) itemView.findViewById(R.id.panel_btn);

            // this.wooeenLogo = (ImageView) itemView.findViewById(R.id.wooeen_logo);
            this.userImage = (ImageView) itemView.findViewById(R.id.woe_user_image);
            this.companyImage = (ImageView) itemView.findViewById(R.id.woe_company_image);
            this.txtBalanceHeader = (TextView) itemView.findViewById(R.id.txt_balance_header);
            this.txtBalanceHeaderCents = (TextView) itemView.findViewById(R.id.txt_balance_header_cents);
            // this.txtYourId = (TextView) itemView.findViewById(R.id.txt_your_id);
            // this.btnCopyId = (Button) itemView.findViewById(R.id.woe_copy_id);
            this.mWoeCbdProgress = (ProgressBar) itemView.findViewById(R.id.woe_cbd_progress);
            this.btnCashbackHeader = (Button) itemView.findViewById(R.id.btn_cashback_header);

            this.btnActiveCashback = (Button) itemView.findViewById(R.id.btn_activate_cashback);
        }
    }

    public static class SearchBoxViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ntpWoeButtonsLayout;
        View tilesView;

        SearchBoxViewHolder(View itemView, View searchBox,View tilesView) {
            super(itemView);

            this.ntpWoeButtonsLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_search_layout);

            FrameLayout ntpWoeSearhContainer = (FrameLayout) itemView.findViewById(R.id.woe_ntp_search_container);
            if(searchBox != null)
              ntpWoeSearhContainer.addView(searchBox);

            FrameLayout ntpWoeTilesContainer = (FrameLayout) itemView.findViewById(R.id.woe_ntp_tiles_container);
            ntpWoeTilesContainer.addView(tilesView);
            this.tilesView = tilesView;
        }
    }

    public static class ButtonsViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ntpWoeButtonsLayout;

        RelativeLayout btnGames;

        ProgressBar mWoeChaProgress;
        FrameLayout btnChallengesCont;
        LinearLayout btnChallenges;
        ImageView btnChallengesImg;

        FrameLayout btnChatCont;
        LinearLayout btnChat;
        ImageView btnChatImg;

        FrameLayout btnRecommendationCont;
        LinearLayout btnRecommendation;
        ImageView btnRecommendationImg;

        LinearLayout btnAdvertisers;

        LinearLayout btnCompany;

        ButtonsViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeButtonsLayout = (LinearLayout) itemView.findViewById(R.id.woe_ntp_buttons_layout);

            this.btnGames = (RelativeLayout) itemView.findViewById(R.id.btn_buttons_games);

            this.mWoeChaProgress = (ProgressBar) itemView.findViewById(R.id.woe_cha_progress);
            this.btnChallengesCont = (FrameLayout) itemView.findViewById(R.id.btn_buttons_tasks_cont);
            this.btnChallenges = (LinearLayout) itemView.findViewById(R.id.btn_buttons_tasks);
            this.btnChallengesImg = (ImageView) itemView.findViewById(R.id.btn_buttons_tasks_img);

            this.btnChatCont = (FrameLayout) itemView.findViewById(R.id.btn_buttons_chat_cont);
            this.btnChat = (LinearLayout) itemView.findViewById(R.id.btn_buttons_chat);
            this.btnChatImg = (ImageView) itemView.findViewById(R.id.btn_buttons_chat_img);

            this.btnRecommendationCont = (FrameLayout) itemView.findViewById(R.id.btn_buttons_share_cont);
            this.btnRecommendation = (LinearLayout) itemView.findViewById(R.id.btn_buttons_share);
            this.btnRecommendationImg = (ImageView) itemView.findViewById(R.id.btn_buttons_share_img);

            this.btnAdvertisers = (LinearLayout) itemView.findViewById(R.id.btn_buttons_advertisers);

            this.btnCompany = (LinearLayout) itemView.findViewById(R.id.btn_buttons_company);
        }
    }

    public static class WidgetViewHolder extends RecyclerView.ViewHolder {
        FrameLayout ntpWoeWidgetLayout;

        LinearLayout woeWidget;

        TextView txtBalance;
        Button btnWithdrawn;

        TextView txtCashbackVerified;
        TextView txtCashbackPending;
        Button btnCashbackReport;

        TextView txtRecommendationsQuantity;
        TextView txtRecommendationsPending;
        TextView txtRecommendationsApproved;
        Button btnRecommendationsReport;

        TextView txtAffVerified;
        TextView txtAffPending;
        Button btnAffReport;

        TextView woeWidgetBalanceBtn;
        LinearLayout woeWidgetBalanceDiv;
        TextView woeWidgetCashbackBtn;
        LinearLayout woeWidgetCashbackDiv;
        TextView woeWidgetRecommendationsBtn;
        LinearLayout woeWidgetRecommendationsDiv;
        TextView woeWidgetAffBtn;
        LinearLayout woeWidgetAffDiv;

        public final static int widgetBalance = 1;
        public final static int widgetCashback = 2;
        public final static int widgetRecommendations = 3;
        private final static int widgetAff = 4;
        private int widgetActived = 0;

        WidgetViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeWidgetLayout = (FrameLayout) itemView.findViewById(R.id.woe_ntp_widget_layout);

            this.woeWidget = (LinearLayout) itemView.findViewById(R.id.woe_widget);

            this.txtBalance = (TextView) itemView.findViewById(R.id.txt_balance);
            this.btnWithdrawn = (Button) itemView.findViewById(R.id.btn_withdrawn);

            this.txtCashbackVerified = (TextView) itemView.findViewById(R.id.txt_cashback_verified);
            this.txtCashbackPending = (TextView) itemView.findViewById(R.id.txt_cashback_pending);
            this.btnCashbackReport = (Button) itemView.findViewById(R.id.btn_cashback_report);

            this.txtRecommendationsQuantity = (TextView) itemView.findViewById(R.id.txt_recommendations_quantity);
            this.txtRecommendationsPending = (TextView) itemView.findViewById(R.id.txt_recommendations_pending);
            this.txtRecommendationsApproved = (TextView) itemView.findViewById(R.id.txt_recommendations_approved);
            this.btnRecommendationsReport = (Button) itemView.findViewById(R.id.btn_recommendations_report);

            this.txtAffVerified = (TextView) itemView.findViewById(R.id.txt_aff_verified);
            this.txtAffPending = (TextView) itemView.findViewById(R.id.txt_aff_pending);
            this.btnAffReport = (Button) itemView.findViewById(R.id.btn_aff_report);

            this.woeWidgetBalanceBtn = (TextView) itemView.findViewById(R.id.woeWidgetBalanceBtn);
            this.woeWidgetBalanceDiv = (LinearLayout) itemView.findViewById(R.id.woeWidgetBalanceDiv);
            this.woeWidgetCashbackBtn = (TextView) itemView.findViewById(R.id.woeWidgetCashbackBtn);
            this.woeWidgetCashbackDiv = (LinearLayout) itemView.findViewById(R.id.woeWidgetCashbackDiv);
            this.woeWidgetRecommendationsBtn = (TextView) itemView.findViewById(R.id.woeWidgetRecommendationsBtn);
            this.woeWidgetRecommendationsDiv = (LinearLayout) itemView.findViewById(R.id.woeWidgetRecommendationsDiv);
            this.woeWidgetAffBtn = (TextView) itemView.findViewById(R.id.woeWidgetAffBtn);
            this.woeWidgetAffDiv = (LinearLayout) itemView.findViewById(R.id.woeWidgetAffDiv);
        }

        public int getWidgetActived(){
          return widgetActived;
        }

        public void changeWidget(Activity context, int id){
          if(id == widgetActived)
              return;
          if(id != widgetBalance && id != widgetCashback && id != widgetRecommendations && id != widgetAff)
              return;

          woeWidgetBalanceDiv.setVisibility(View.GONE);
          woeWidgetCashbackDiv.setVisibility(View.GONE);
          woeWidgetRecommendationsDiv.setVisibility(View.GONE);
          woeWidgetAffDiv.setVisibility(View.GONE);

          woeWidgetBalanceBtn.setBackgroundResource(0);
          woeWidgetCashbackBtn.setBackgroundResource(0);
          woeWidgetRecommendationsBtn.setBackgroundResource(0);
          woeWidgetAffBtn.setBackgroundResource(0);

          final int sdk = android.os.Build.VERSION.SDK_INT;
          switch (id){
                case widgetBalance:
                  if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                      woeWidgetCashbackBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetRecommendationsBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetAffBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                  } else {
                      woeWidgetCashbackBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetRecommendationsBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetAffBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                  }

                  woeWidgetBalanceDiv.setVisibility(View.VISIBLE);

                  break;

              case widgetCashback:
                  if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                      woeWidgetBalanceBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetRecommendationsBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetAffBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                  } else {
                      woeWidgetBalanceBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetRecommendationsBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetAffBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                  }

                  woeWidgetCashbackDiv.setVisibility(View.VISIBLE);

                  break;

              case widgetRecommendations:
                  if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                      woeWidgetBalanceBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetCashbackBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetAffBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                  } else {
                      woeWidgetBalanceBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetCashbackBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetAffBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                  }

                  woeWidgetRecommendationsDiv.setVisibility(View.VISIBLE);

                  break;

              case widgetAff:
                  if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                      woeWidgetBalanceBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetCashbackBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                      woeWidgetRecommendationsBtn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected) );
                  } else {
                      woeWidgetBalanceBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetCashbackBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                      woeWidgetRecommendationsBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.woe_widget_unselected));
                  }

                  woeWidgetAffDiv.setVisibility(View.VISIBLE);

                  break;
              default:
                  break;
          }

          widgetActived = id;
          WooeenSettings.setWidgetActived(context, id);
        }
    }

    public static class UpdateViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout ntpWoeUpdateLayout;
        Button ntpWoeUpdateButton;

        UpdateViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeUpdateLayout = (RelativeLayout) itemView.findViewById(R.id.woe_ntp_update_layout);
            this.ntpWoeUpdateButton = (Button) itemView.findViewById(R.id.woe_ntp_update_btn);
        }
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        FrameLayout ntpWoeBannerLayout;

        BannerViewHolder(View itemView) {
            super(itemView);
            this.ntpWoeBannerLayout = (FrameLayout) itemView.findViewById(R.id.woe_ntp_banner_layout);
        }
    }

    public static class TopSitesViewHolder extends RecyclerView.ViewHolder {
        TopSitesViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PubLoadingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;

        PubLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PubViewHolder extends RecyclerView.ViewHolder {
        public TextView text;
        public LinearLayout link;
        public RelativeLayout linkImage;
        public ImageView linkImageMedia;
        public TextView linkTitle;
        public TextView linkDesc;

        public LinearLayout offer;
        public RelativeLayout offerImage;
        public ImageView offerImageMedia;
        public TextView offerTitle;
        public TextView offerDesc;
        public TextView offerPrice;
        public TextView offerPriceOld;
        public TextView offerPriceDiscount;
        public LinearLayout offerPriceCashback;
        public TextView offerPriceCashbackTo;
        public TextView offerPriceCashbackValue;
        public LinearLayout offerSocial;
        public LinearLayout offerPlus;
        public RatingBar offerStars;

        public RelativeLayout faviconBox;
        public GradientDrawable faviconDrawable;
        public ImageView faviconImage;
        public TextView name;

        public ImageView userImage;
        public TextView userName;
        public TextView date;

        public Button btnMore;
        public Button btnLike;
        public Button btnComment;
        public Button btnShare;

        public LinearLayout couponsContainer;
        public RecyclerView coupons;

        PubViewHolder(View itemView) {
            super(itemView);
            this.text = (TextView) itemView.findViewById(R.id.woe_pub_text);

            this.link = (LinearLayout) itemView.findViewById(R.id.woe_pub_link);
            this.link.setVisibility(View.GONE);
            this.linkImage = (RelativeLayout) itemView.findViewById(R.id.woe_pub_link_image);
            this.linkImageMedia = (ImageView) itemView.findViewById(R.id.woe_pub_link_image_media);
            this.linkTitle = (TextView) itemView.findViewById(R.id.woe_pub_link_title);
            this.linkDesc = (TextView) itemView.findViewById(R.id.woe_pub_link_desc);

            this.offer = (LinearLayout) itemView.findViewById(R.id.woe_pub_offer);
            this.offer.setVisibility(View.GONE);
            this.offerImage = (RelativeLayout) itemView.findViewById(R.id.woe_pub_offer_image);
            this.offerImageMedia = (ImageView) itemView.findViewById(R.id.woe_pub_offer_image_media);
            this.offerTitle = (TextView) itemView.findViewById(R.id.woe_pub_offer_title);
            this.offerDesc = (TextView) itemView.findViewById(R.id.woe_pub_offer_desc);
            this.offerPrice = (TextView) itemView.findViewById(R.id.woe_pub_offer_price);
            this.offerPriceOld = (TextView) itemView.findViewById(R.id.woe_pub_offer_price_old);
            this.offerPriceDiscount = (TextView) itemView.findViewById(R.id.woe_pub_offer_discount);
            this.offerPriceCashback = (LinearLayout) itemView.findViewById(R.id.woe_pub_offer_cashback);
            this.offerPriceCashbackTo = (TextView) itemView.findViewById(R.id.woe_pub_offer_cashback_to);
            this.offerPriceCashbackValue = (TextView) itemView.findViewById(R.id.woe_pub_offer_cashback_value);
            this.offerSocial = (LinearLayout) itemView.findViewById(R.id.woe_pub_offer_social);
            this.offerPlus = (LinearLayout) itemView.findViewById(R.id.woe_pub_offer_plus);
            this.offerStars = (RatingBar) itemView.findViewById(R.id.woe_pub_offer_stars);

            this.faviconBox = (RelativeLayout) itemView.findViewById(R.id.woe_pub_favicon_box);
            this.faviconDrawable = (GradientDrawable)faviconBox.getBackground();
            this.faviconImage = (ImageView) itemView.findViewById(R.id.woe_pub_favicon_image);
            this.name = (TextView) itemView.findViewById(R.id.woe_pub_name);

            this.userImage = (ImageView) itemView.findViewById(R.id.woe_pub_user_image);
            this.userName = (TextView) itemView.findViewById(R.id.woe_pub_user_name);
            this.date = (TextView) itemView.findViewById(R.id.woe_pub_date);

            this.btnMore = (Button) itemView.findViewById(R.id.woe_pub_more);
            this.btnLike = (Button) itemView.findViewById(R.id.woe_pub_like);
            this.btnComment = (Button) itemView.findViewById(R.id.woe_pub_comment);
            this.btnShare = (Button) itemView.findViewById(R.id.woe_pub_share);

            this.couponsContainer = itemView.findViewById(R.id.woe_pub_coupons_container);
            this.couponsContainer.setVisibility(View.GONE);
            this.coupons = itemView.findViewById(R.id.woe_pub_coupons);
        }
    }

    public static class EmptyViewHolder extends RecyclerView.ViewHolder {

        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout postHolder;
        public ImageView postImage;
        public TextView postTitle;
        public TextView postDesc;
        public Button postShare;

        public ImageView userImage;
        public TextView userName;
        public TextView date;

        PostViewHolder(View itemView) {
            super(itemView);

            this.postHolder = itemView.findViewById(R.id.post_holder);
            this.postImage = itemView.findViewById(R.id.post_image);
            this.userImage = (ImageView) itemView.findViewById(R.id.woe_post_user_image);
            this.userName = (TextView) itemView.findViewById(R.id.woe_post_user_name);
            this.date = (TextView) itemView.findViewById(R.id.woe_post_date);
            this.postTitle = itemView.findViewById(R.id.woe_post_title);
            this.postDesc = itemView.findViewById(R.id.woe_post_desc);
            this.postShare = itemView.findViewById(R.id.post_share);
        }
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout taskHolder;
        public ImageView taskImage;
        public TextView taskImageTitle;
        public TextView taskTitle;
        public TextView taskDesc;
        public Button taskShare;

        TaskViewHolder(View itemView) {
            super(itemView);

            this.taskHolder = itemView.findViewById(R.id.task_holder);
            this.taskImage = itemView.findViewById(R.id.task_image);
            this.taskImageTitle = itemView.findViewById(R.id.woe_task_image_title);
            this.taskTitle = itemView.findViewById(R.id.woe_task_title);
            this.taskDesc = itemView.findViewById(R.id.woe_task_desc);
            this.taskShare = itemView.findViewById(R.id.task_share);
        }
    }

    public static class RetargetingViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item1;
        public RelativeLayout item1Image;
        public ImageView item1ImageMedia;
        public TextView item1Title;
        public TextView item1Amount;
        public TextView item1AmountOld;
        public RelativeLayout item1FaviconBox;
        public GradientDrawable item1FaviconDrawable;
        public ImageView item1FaviconImage;
        public TextView item1Name;

        public LinearLayout item2;
        public RelativeLayout item2Image;
        public ImageView item2ImageMedia;
        public TextView item2Title;
        public TextView item2Amount;
        public TextView item2AmountOld;
        public RelativeLayout item2FaviconBox;
        public GradientDrawable item2FaviconDrawable;
        public ImageView item2FaviconImage;
        public TextView item2Name;

        public LinearLayout item3;
        public RelativeLayout item3Image;
        public ImageView item3ImageMedia;
        public TextView item3Title;
        public TextView item3Amount;
        public TextView item3AmountOld;
        public RelativeLayout item3FaviconBox;
        public GradientDrawable item3FaviconDrawable;
        public ImageView item3FaviconImage;
        public TextView item3Name;

        public LinearLayout item4;
        public RelativeLayout item4Image;
        public ImageView item4ImageMedia;
        public TextView item4Title;
        public TextView item4Amount;
        public TextView item4AmountOld;
        public RelativeLayout item4FaviconBox;
        public GradientDrawable item4FaviconDrawable;
        public ImageView item4FaviconImage;
        public TextView item4Name;

        public Button btn;

        RetargetingViewHolder(View itemView) {
            super(itemView);

            this.item1 = itemView.findViewById(R.id.woe_retarg_item_1);
            this.item1Image = itemView.findViewById(R.id.woe_retarg_item_1_image);
            this.item1ImageMedia = itemView.findViewById(R.id.woe_retarg_item_1_media);
            this.item1Title = itemView.findViewById(R.id.woe_retarg_item_1_title);
            this.item1Amount = itemView.findViewById(R.id.woe_retarg_item_1_price);
            this.item1AmountOld = itemView.findViewById(R.id.woe_retarg_item_1_price_old);
            this.item1FaviconBox = (RelativeLayout) itemView.findViewById(R.id.woe_retarg_item_1_favicon_box);
            this.item1FaviconDrawable = (GradientDrawable)item1FaviconBox.getBackground();
            this.item1FaviconImage = (ImageView) itemView.findViewById(R.id.woe_retarg_item_1_favicon_image);
            this.item1Name = (TextView) itemView.findViewById(R.id.woe_retarg_item_1_name);

            this.item2 = itemView.findViewById(R.id.woe_retarg_item_2);
            this.item2Image = itemView.findViewById(R.id.woe_retarg_item_2_image);
            this.item2ImageMedia = itemView.findViewById(R.id.woe_retarg_item_2_media);
            this.item2Title = itemView.findViewById(R.id.woe_retarg_item_2_title);
            this.item2Amount = itemView.findViewById(R.id.woe_retarg_item_2_price);
            this.item2AmountOld = itemView.findViewById(R.id.woe_retarg_item_2_price_old);
            this.item2FaviconBox = (RelativeLayout) itemView.findViewById(R.id.woe_retarg_item_2_favicon_box);
            this.item2FaviconDrawable = (GradientDrawable)item2FaviconBox.getBackground();
            this.item2FaviconImage = (ImageView) itemView.findViewById(R.id.woe_retarg_item_2_favicon_image);
            this.item2Name = (TextView) itemView.findViewById(R.id.woe_retarg_item_2_name);

            this.item3 = itemView.findViewById(R.id.woe_retarg_item_3);
            this.item3Image = itemView.findViewById(R.id.woe_retarg_item_3_image);
            this.item3ImageMedia = itemView.findViewById(R.id.woe_retarg_item_3_media);
            this.item3Title = itemView.findViewById(R.id.woe_retarg_item_3_title);
            this.item3Amount = itemView.findViewById(R.id.woe_retarg_item_3_price);
            this.item3AmountOld = itemView.findViewById(R.id.woe_retarg_item_3_price_old);
            this.item3FaviconBox = (RelativeLayout) itemView.findViewById(R.id.woe_retarg_item_3_favicon_box);
            this.item3FaviconDrawable = (GradientDrawable)item3FaviconBox.getBackground();
            this.item3FaviconImage = (ImageView) itemView.findViewById(R.id.woe_retarg_item_3_favicon_image);
            this.item3Name = (TextView) itemView.findViewById(R.id.woe_retarg_item_3_name);

            this.item4 = itemView.findViewById(R.id.woe_retarg_item_4);
            this.item4Image = itemView.findViewById(R.id.woe_retarg_item_4_image);
            this.item4ImageMedia = itemView.findViewById(R.id.woe_retarg_item_4_media);
            this.item4Title = itemView.findViewById(R.id.woe_retarg_item_4_title);
            this.item4Amount = itemView.findViewById(R.id.woe_retarg_item_4_price);
            this.item4AmountOld = itemView.findViewById(R.id.woe_retarg_item_4_price_old);
            this.item4FaviconBox = (RelativeLayout) itemView.findViewById(R.id.woe_retarg_item_4_favicon_box);
            this.item4FaviconDrawable = (GradientDrawable)item4FaviconBox.getBackground();
            this.item4FaviconImage = (ImageView) itemView.findViewById(R.id.woe_retarg_item_4_favicon_image);
            this.item4Name = (TextView) itemView.findViewById(R.id.woe_retarg_item_4_name);

            this.btn = (Button) itemView.findViewById(R.id.woe_retarg_btn);
        }
    }

    public static class PubItemHolder{
        private int type;
        private PubTO pub;
        private PostTO post;
        private TaskTO task;
        private List<NavigationTO> retargeting;

        public static final int PUB = 1;
        public static final int POST = 2;
        public static final int TASK = 3;
        public static final int RETARGETING = 4;

        public PubItemHolder(){
        }
        public PubItemHolder(PubTO pub){
            this.pub = pub;
            this.type = PUB;
        }
        public PubItemHolder(PostTO post){
            this.post  = post;
            this.type = POST;
        }
        public PubItemHolder(TaskTO task){
            this.task  = task;
            this.type = TASK;
        }
        public PubItemHolder(List<NavigationTO> retargeting){
            this.retargeting  = retargeting;
            this.type = RETARGETING;
        }
        public PubTO getPub() {
            return pub;
        }

        public void setPub(PubTO pub) {
            this.pub = pub;
        }

        public PostTO getPost() {
            return post;
        }

        public void setPost(PostTO post) {
            this.post = post;
        }

        public TaskTO getTask() {
            return task;
        }

        public void setTask(TaskTO task) {
            this.task = task;
        }

        public List<NavigationTO> getRetargeting() {
            return retargeting;
        }

        public void setRetargeting(List<NavigationTO> retargeting) {
            this.retargeting = retargeting;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
