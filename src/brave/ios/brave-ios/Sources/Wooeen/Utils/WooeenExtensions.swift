import Foundation

extension String {
    public func indexInt(of char: Character) -> Int? {
        return firstIndex(of: char)?.utf16Offset(in: self)
    }
    
    func utf8DecodedString()-> String {
        let data = self.data(using: .utf8)
        let message = String(data: data!, encoding: .nonLossyASCII) ?? ""
        return message
    }
    
    func utf8EncodedString()-> String {
        let messageData = self.data(using: .nonLossyASCII)
        let text = String(data: messageData!, encoding: .utf8) ?? ""
        return text
    }
    
    func urlEncodedUtf8()->String? {
        return addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
    }
}

extension URL {
    public enum Wooeen {
        public static let community = URL(string: "https://community.wooeen.com/")!
        public static let account = URL(string: "https://app.wooeen.com")!
        public static let privacy = URL(string: "https://www.wooeen.com/privacy-policy/")!
        public static let termsOfUse = URL(string: "https://www.wooeen.com/terms/")!
    }
}
