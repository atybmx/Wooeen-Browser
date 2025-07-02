import Foundation

public struct VersionTO: Decodable {
    public var checkout: Int?
    public var product: Int?
    public var query: Int?
    public var pub: Int?
    public var android: String?
    public var ios: String?
}

public struct VersionResponse: Decodable{
    public var result: Bool
    public var callback: VersionTO?
    public var message: String?
}
