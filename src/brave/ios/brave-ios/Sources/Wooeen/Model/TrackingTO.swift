import Foundation

public struct TrackingTO: Decodable {
    public var id: Int
    public var platformId: Int?
    public var advertiserId: Int?
    public var advertiserType: Int?
    public var deeplink: String?
    public var params: String?
    public var domain: String?
    public var priority: Int?
    public var payout: Double?
    public var commissionType: String?
    public var commissionAvg1: Double?
    public var commissionMin1: Double?
    public var commissionMax1: Double?
    public var commissionAvg2: Double?
    public var commissionMin2: Double?
    public var commissionMax2: Double?
    public var approvalDays: Int?
}

public struct TrackingResponse: Decodable{
    public var result: Bool
    public var callback: [TrackingTO]?
    public var message: String?
}

public struct TrackingTOP{
    public var id: Int?
    public var domain: String?
    public var qtdPerPage: Int?
    public var page: Int?
}
