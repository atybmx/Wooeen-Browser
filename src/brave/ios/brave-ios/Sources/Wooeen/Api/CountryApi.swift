import Foundation

public class CountryApi{
    
    public init() {}
    
    public func search(pg: Int?, qtdPerPage: Int?, callback: @escaping (Result<[CountryTOA], Error>)->Void){
        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/country/get")
        
        var parameters = [URLQueryItem]()
        
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
                let resultData = try JSONDecoder().decode(CountryResponse.self, from: data)
                if(resultData.result){
                    if let countries = resultData.callback{
                        callback(.success(countries))
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
    
    public func detail(id : String, callback: @escaping (Result<CountryTOA, Error>)->Void){
        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/country/get")
        
        var parameters = [URLQueryItem]()
        
        parameters.append(URLQueryItem(name: "id", value: "\(id)"))
        
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
                let resultData = try JSONDecoder().decode(CountryResponse.self, from: data)
                if(resultData.result){
                    if let countries = resultData.callback{
                        if(countries.isEmpty){
                            print("WOE empty error")
                            callback(.failure(WooeenApiError.empty))
                            return
                        }
                        
                        callback(.success(countries[0]))
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
