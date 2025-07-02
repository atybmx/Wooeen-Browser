import Foundation

public class AdvertiserApi{
    
    var country: String?
    
    public init(country: String?) {
        self.country = country
    }
    
    public func search(pg: Int?, qtdPerPage: Int?, callback: @escaping (Result<[AdvertiserTO], Error>)->Void){
        if(country ?? "").isEmpty{
            callback(.failure(WooeenApiError.requiredParams))
            return
        }

        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/advertiser/get")
        
        var parameters = [URLQueryItem]()
        
        parameters.append(URLQueryItem(name: "cr", value: "\(country!)"))
        parameters.append(URLQueryItem(name: "st", value: "1"))
        	
        if(0 != pg){
            parameters.append(URLQueryItem(name: "pg", value: "\(pg!)"))
        }
        if(0 != qtdPerPage){
            parameters.append(URLQueryItem(name: "qpp", value: "\(qtdPerPage!)"))
        }
        
        urlBuilder?.queryItems = parameters
        
        guard let url = urlBuilder?.url else {
            callback(.failure(WooeenApiError.url))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("\(WooeenApi.appId)", forHTTPHeaderField: "app_id")
        request.setValue("\(WooeenApi.appToken)", forHTTPHeaderField: "app_token")
        
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            if let error = error {
                callback(.failure(error))
                return
            }
            
            guard let data = data else{ return }
            do {
                let resultData = try JSONDecoder().decode(AdvertiserResponse.self, from: data)
                if(resultData.result){
                    if let advertisers = resultData.callback{
                        callback(.success(advertisers))
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
