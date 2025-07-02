import Foundation

public struct CountryTO: Decodable {
    public var id: String
    public var name: String?
    public var language: String?
    public var imageUrl: String?
    public var currency: CurrencyTO?
    public var loadPosts: Bool?
    public var loadOffers: Bool?
    public var loadCoupons: Bool?
    public var loadTasks: Bool?
    public var loadGames: Bool?
    public var categoryB2b: CategoryTO?
    public var searchHint: String?
}

public struct CountryTOA: Decodable {
    public var id: String
    public var name: String?
    public var language: String?
    public var imageUrl: String?
    public var currency: String?
    public var loadPosts: Bool?
    public var loadOffers: Bool?
    public var loadCoupons: Bool?
    public var loadTasks: Bool?
    public var loadGames: Bool?
    public var categoryB2b: CategoryTO?
    public var searchHint: String?
}

public struct CountryResponse: Decodable{
    public var result: Bool
    public var callback: [CountryTOA]?
    public var message: String?
}
