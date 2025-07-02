import Foundation

public class TrackingUtils{

    public static let sourceApp = 16
    public static let sourceSocial = 6
    public static let sourceRtgAndroid = 7
    public static let sourcePush = 4
    public static let sourceSearch = 5

    public static let woeEligibleDomains =
      [
        ".wooeen.com",
      ]
    
    public static let woeEligibleLoginPath =
      [
        "/u/login",
        "/u/logout",
        "/u/register",
        "/u/uqa",
        "/u/recover-password"
      ]
    
    public static func tracked(domain: String) -> TrackingTO?{
        let trackingDAO = TrackingDAO()
        guard let items = trackingDAO.search(search: TrackingTOP(domain: ".\(domain)")), !items.isEmpty else { return nil }
        
        //return if has only one
        if 0 == items.count {
            return items[0]
        }
        
        //return by priority
        var trk:TrackingTO? = nil
        var hasPriority:Bool = false
        for t in items{
            if trk == nil {
                trk = t
                continue
            }
            
            if t.priority ?? 0 != trk?.priority ?? 0{
                hasPriority = true
            }
            
            if t.priority ?? 0 > trk?.priority ?? 0{
                trk = t
            }
        }
        if hasPriority{
            return trk
        }
        
        return items.randomElement()!
    }
    
    public static func parseTrackingLink(deeplink:String?, params:String?, link:String?, userId:Int, source:Int?, affiliate:Int?) -> String?{
        if var link = link{
            var userTag = userId > 0 ? "\(userId)" : ""
            if(!userTag.isEmpty) {
                if let source = source, source > 0{
                    userTag += "_\(source)"
                    
                    if let affiliate = affiliate{
                        userTag += "_\(affiliate)"
                    }
                }
            }
            
            //parse the params and add into final link
            if var params = params, !params.isEmpty{
                if params.contains("{user_id}"){
                    params = params.replacingOccurrences(of: "{user_id}", with: userTag, options: .literal, range: nil)
                }
                if let l = URL.init(string: link){
                    var lps = [URLQueryItem]()
                    if let query = l.query(), !query.isEmpty{
                        let lpsQuery = query.components(separatedBy: "&")
                        if !lpsQuery.isEmpty{
                            for q in lpsQuery{
                                let qv = q.components(separatedBy: "=")
                                if qv.count >= 2{
                                    lps.append(URLQueryItem(name: qv[0], value: qv[1]))
                                }
                            }
                        }
                    }
                        
                    let ps = params.components(separatedBy: "&")
                    if !ps.isEmpty{
                        for p in ps{
                            let pv = p.components(separatedBy: "=")
                            if pv .count >= 2{
                                let pName = pv[0]
                                let pValue = pv[1]
                                var setted:Bool = false
                                for x in 0 ... lps.count - 1{
                                    var lp = lps[x]
                                    if lp.name == pName{
                                        lp.value = pValue
                                        lps[x] = lp
                                        setted = true
                                    }
                                }
                                if !setted{
                                    lps.append(URLQueryItem(name: pName, value: pValue))
                                }
                            }
                        }
                    }
                    
                    if link.contains("?"){
                        link = String(link.prefix(link.indexInt(of: "?") ?? link.count))
                    }
                    link += "?";
                    for x in 0...lps.count - 1{
                        let p = lps[x]
                        if x > 0{
                            link += "&"
                        }
                        
                        link += "\(p.name)=\(p.value!)"
                    }
                    
                }else{
                    if link.contains("?"){
                        link += "&"
                    }else{
                        link += "?"
                    }
                    link += params
                }
            }
        
            if var deeplink = deeplink, !deeplink.isEmpty{
                if deeplink.contains("{link}"){
                    deeplink = deeplink.replacingOccurrences(of: "{link}", with: link)
                }
                if deeplink.contains("{link_encode_utf8}"){
                    deeplink = deeplink.replacingOccurrences(of: "{link_encode_utf8}", with: link.urlEncodedUtf8() ?? link)
                }
                if deeplink.contains("{user_id}"){
                    deeplink = deeplink.replacingOccurrences(of: "{user_id}", with: userTag)
                }
                
                return deeplink
            }
    
            return link
        }
            
        return nil
    }
    
}

private class TrackingInfo {
    init() {
    }
    public var mTracking:Bool = false
    public var mDomain:String?
    public var mId:Int?
    public var mPlatform:Int? = 0
    public var mAdvertiser:Int? = 0
    public var checkoutEndpoint:String?
    public var checkoutData:String?
    public var productEndpoint:String?
    public var productData:String?
    public var queryEndpoint:String?
    public var queryData:String?
    public var alreadyTracked:Bool = false
}

public class TrackingHandler {

    public init(){
        
    }
    
    private var mTabsStat = [UUID: TrackingInfo]()

    public func addTracking(
            tabId: UUID,
            domain: String?,
            id: Int?,
            platform:Int?,
            advertiser:Int?,
            checkoutEndpoint:String?,
            checkoutData:String?,
            productEndpoint:String?,
            productData:String?,
            queryEndpoint:String?,
            queryData:String?) {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
                
        var trackingInfo = mTabsStat[tabId]
        trackingInfo?.mDomain = domain
        trackingInfo?.mId = id
        trackingInfo?.mPlatform = platform
        trackingInfo?.mAdvertiser = advertiser
        trackingInfo?.checkoutEndpoint = checkoutEndpoint
        trackingInfo?.checkoutData = checkoutData
        trackingInfo?.productEndpoint = productEndpoint
        trackingInfo?.productData = productData
        trackingInfo?.queryEndpoint = queryEndpoint
        trackingInfo?.queryData = queryData

        if !(trackingInfo?.alreadyTracked ?? false) {
            trackingInfo?.mTracking = true;
        }

        trackingInfo?.alreadyTracked = false;
    }

    public func addPreTracking(tabId: UUID){
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        trackingInfo.alreadyTracked = true;
    }

    public func isTracking(tabId:UUID) -> Bool {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.mTracking
    }

    public func getDomain(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.mDomain
    }

    public func getCheckoutEndpoint(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.checkoutEndpoint
    }

    public func getCheckoutData(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.checkoutData
    }

    public func getProductEndpoint(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.productEndpoint
    }

    public func getProductData(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.productData
    }

    public func getQueryEndpoint(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.queryEndpoint
    }

    public func getQueryData(tabId:UUID) -> String? {
        if mTabsStat[tabId] == nil {
            mTabsStat[tabId] = TrackingInfo()
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.queryData
    }

    public func getPlatform(tabId:UUID) -> Int {
        if mTabsStat[tabId] == nil {
            return 0
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.mPlatform ?? 0
    }

    public func getAdvertiser(tabId:UUID) -> Int {
        if mTabsStat[tabId] == nil {
            return 0
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.mAdvertiser ?? 0
    }

    public func getTracking(tabId:UUID) -> Int {
        if mTabsStat[tabId] == nil {
            return 0
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        return trackingInfo.mId ?? 0
    }

    public func tracked(tabId:UUID) {
        if mTabsStat[tabId] == nil {
            return
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        trackingInfo.mTracking = false
    }

    public func changeDomain(tabId:UUID) {
        if mTabsStat[tabId] == nil {
            return
        }
        var trackingInfo:TrackingInfo = mTabsStat[tabId] ?? TrackingInfo()
        trackingInfo.mDomain = nil
        trackingInfo.mPlatform = 0
        trackingInfo.checkoutEndpoint = nil
        trackingInfo.checkoutData = nil
        trackingInfo.productEndpoint = nil
        trackingInfo.productData = nil
        trackingInfo.queryEndpoint = nil
        trackingInfo.queryData = nil
    }
}
