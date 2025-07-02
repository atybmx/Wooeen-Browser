import Foundation

public class VersionApi{
    
    public init() {
    }
    
    public func search(callback: @escaping (Result<VersionTO, Error>)->Void){
        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/version/script")
        
        var parameters = [URLQueryItem]()
        
        urlBuilder?.queryItems = parameters
        
        guard let url = urlBuilder?.url else {
            callback(.failure(WooeenApiError.url))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            if let error = error {
                callback(.failure(error))
                return
            }
            
            guard let data = data else{ return }
            do {
                let resultData = try JSONDecoder().decode(VersionResponse.self, from: data)
                if(resultData.result){
                    if let version = resultData.callback{
                        callback(.success(version))
                    }
                }else{
                    print("WOE result error \(error)")
                    callback(.failure(WooeenApiError.authentication))
                }
                
            } catch {
                print("WOE decoder erro \(error)")
                callback(.failure(error))
            }
            return
            
            
        }
        
        task.resume()
    }
}
