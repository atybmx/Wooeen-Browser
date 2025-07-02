package com.wooeen.model.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PubTO {

    private int id;
    private int type;
    private String content;
    private PubUserTOA user;
    private Date datePublish;
    private List<PubAdvertiserTOA> advertisers = new ArrayList<PubAdvertiserTOA>();
    private int totalViews;
    private int totalClicks;
    private int totalLikes;
    private int totalShares;
    private int totalComments;
    private boolean liked;
    private List<String> tags;

    private List<CouponTO> coupons;
    private transient int loadingCoupons;

    public int getLoadingCoupons() {
        return loadingCoupons;
    }

    public void setLoadingCoupons(int loadingCoupons) {
        this.loadingCoupons = loadingCoupons;
    }

    public List<CouponTO> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<CouponTO> coupons) {
        this.coupons = coupons;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class PubAdvertiserTOA{
        private int id;
        private String name;
        private String deeplink;
        private String params;
        private String color;
        private String logo;
        private TrackingTO tracking;
        public PubAdvertiserTOA() {
        }
        public PubAdvertiserTOA(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDeeplink() {
            return deeplink;
        }
        public void setDeeplink(String deeplink) {
            this.deeplink = deeplink;
        }
        public String getParams() {
            return params;
        }
        public void setParams(String params) {
            this.params = params;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public TrackingTO getTracking() {
            return tracking;
        }

        public void setTracking(TrackingTO tracking) {
            this.tracking = tracking;
        }
    }

    public static class PubUserTOA{
        private int id;
        private String name;
        private String photo;
        public PubUserTOA() {
        }
        public PubUserTOA(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
        public void setId(int id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPhoto() {
            return photo;
        }
        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PubAdvertiserTOA> getAdvertisers() {
        return advertisers;
    }

    public void setAdvertisers(List<PubAdvertiserTOA> advertisers) {
        this.advertisers = advertisers;
    }

    public PubUserTOA getUser() {
        return user;
    }

    public void setUser(PubUserTOA user) {
        this.user = user;
    }

    public Date getDatePublish() {
        return datePublish;
    }

    public void setDatePublish(Date datePublish) {
        this.datePublish = datePublish;
    }

    public int getTotalViews() {
        return totalViews;
    }
    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }
    public int getTotalClicks() {
        return totalClicks;
    }
    public void setTotalClicks(int totalClicks) {
        this.totalClicks = totalClicks;
    }
    public int getTotalLikes() {
        return totalLikes;
    }
    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }
    public int getTotalShares() {
        return totalShares;
    }
    public void setTotalShares(int totalShares) {
        this.totalShares = totalShares;
    }
    public int getTotalComments() {
        return totalComments;
    }
    public void setTotalComments(int totalComments) {
        this.totalComments = totalComments;
    }

    public boolean getLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
