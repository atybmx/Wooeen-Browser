import Foundation

public struct CurrencyTO: Decodable {
    public var id: String
    public var name: String?
    public var symbol: String?
}
