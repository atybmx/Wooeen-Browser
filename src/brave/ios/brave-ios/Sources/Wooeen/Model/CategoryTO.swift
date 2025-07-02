import Foundation

public struct CategoryTO: Decodable {
    public var id: Int
    public var name: String?
    public var description: String?
    public var totalAdvertisers: Int?
}

public struct CategoryResponse: Decodable{
    public var result: Bool
    public var callback: [CategoryTO]?
    public var message: String?
}
