import * as Background from '../../common/Background'

import MessageTypes = Background.MessageTypes

export function getUser():Promise<Wooeen.User> {
  return Background.send<Wooeen.User>(MessageTypes.User.getUser);
}

export function getPosts():Promise<Wooeen.FeedPost> {
  return Background.send<Wooeen.FeedPost>(MessageTypes.Post.getPosts);
}

export function getTasks():Promise<Wooeen.FeedTask> {
  return Background.send<Wooeen.FeedTask>(MessageTypes.Task.getTasks);
}

export function getOffers():Promise<Wooeen.FeedOffer> {
  return Background.send<Wooeen.FeedOffer>(MessageTypes.Offer.getOffers);
}

export function getCoupons():Promise<Wooeen.FeedCoupon> {
  return Background.send<Wooeen.FeedCoupon>(MessageTypes.Coupon.getCoupons);
}

export function getMyCountry():Promise<Wooeen.Country> {
  return Background.send<Wooeen.Country>(MessageTypes.Country.getMyCountry);
}
