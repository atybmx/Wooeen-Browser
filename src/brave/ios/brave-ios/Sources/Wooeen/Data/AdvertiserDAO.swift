import Foundation
import SQLite3

public class AdvertiserDAO{
        
    public init(){
    }
    
    public func insert(advertiser: AdvertiserTO){
        if((advertiser.name ?? "").isEmpty ||
           (advertiser.url ?? "").isEmpty ||
           (advertiser.logo ?? "").isEmpty ||
           (advertiser.color ?? "").isEmpty){
            return
        }
        
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let insertStatementString = """
            INSERT INTO advertiser (
            id,
            type,
            name,
            color,
            url,
            domain,
            logo,
            checkout_endpoint,
            checkout_data,
            product_endpoint,
            product_data,
            query_endpoint,
            query_data,
            omnibox_title,
            omnibox_description
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
            type = excluded.type,
            name = excluded.name,
            color = excluded.color,
            url = excluded.url,
            domain = excluded.domain,
            logo = excluded.logo,
            checkout_endpoint = excluded.checkout_endpoint,
            checkout_data = excluded.checkout_data,
            product_endpoint = excluded.product_endpoint,
            product_data = excluded.product_data,
            query_endpoint = excluded.query_endpoint,
            query_data = excluded.query_data,
            omnibox_title = excluded.omnibox_title,
            omnibox_description = excluded.omnibox_description
            """
            var insertStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, insertStatementString, -1, &insertStatement, nil) == SQLITE_OK {
                var index: Int32 = 1
                sqlite3_bind_int(insertStatement, index, Int32(advertiser.id));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(advertiser.type ?? 0));index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.name ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.color ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.url ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.domain ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.logo ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.checkout?.endpoint ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.checkout?.data ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.product?.endpoint ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.product?.data ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.query?.endpoint ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.query?.data ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.omniboxTitle ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((advertiser.omniboxDescription ?? "") as NSString).utf8String, -1, nil);index+=1
                
                if sqlite3_step(insertStatement) != SQLITE_DONE {
                    print("WOE advertiser could not insert row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE advertiser INSERT statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(insertStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteByID(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM advertiser WHERE id = ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE advertiser Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE advertiser DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteFrom(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM advertiser WHERE id > ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE advertiser Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE advertiser DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteTo(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM advertiser WHERE id < ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE advertiser Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE advertiser DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteByRange(from:Int, to:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM advertiser WHERE id >= ? AND id <= ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(from))
                sqlite3_bind_int(deleteStatement, 2, Int32(to))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE advertiser Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE advertiser DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func search(search: AdvertiserTOP?) -> [AdvertiserTO]? {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            var sql = """
                SELECT
                id,
                type,
                name,
                color,
                url,
                domain,
                logo,
                checkout_endpoint,
                checkout_data,
                product_endpoint,
                product_data,
                query_endpoint,
                query_data,
                omnibox_title,
                omnibox_description
                FROM advertiser
                """
            
            if let search = search {
                var sqlWhere = " WHERE "
                
                if let id = search.id, id > 0 {
                    sqlWhere += "advertiser.id = ? AND "
                }
                
                if let domain = search.domain, !domain.isEmpty{
                    sqlWhere += "? LIKE '%.' || advertiser.domain AND "
                }
                
                if let name = search.name, !name.isEmpty{
                    sqlWhere += "advertiser.name LIKE ? AND "
                }
                
                if sqlWhere.hasSuffix("AND ") {
                    sqlWhere = String(sqlWhere.prefix(4))
                }else{
                    sqlWhere = ""
                }
                
                sql += sqlWhere
                
                if let qtdPerPage = search.qtdPerPage, qtdPerPage > 0 {
                    sql += "limit \((search.page ?? 0) * (search.qtdPerPage)!),\(search.qtdPerPage!)"
                }
                
            }
            
            var queryStatement: OpaquePointer? = nil
            var result : [AdvertiserTO] = []
            if sqlite3_prepare_v2(db, sql, -1, &queryStatement, nil) == SQLITE_OK {
                
                if let search = search {
                    var index:Int32 = 1
                    if let id = search.id, id > 0 {
                        sqlite3_bind_int(queryStatement, index, Int32(id))
                        index += 1
                    }
                    
                    if let domain = search.domain, !domain.isEmpty{
                        sqlite3_bind_text(queryStatement, index, (".\(domain)" as NSString).utf8String, -1, nil)
                        index += 1
                    }
                    
                    if let name = search.name, !name.isEmpty{
                        sqlite3_bind_text(queryStatement, index, ("%\(name)%" as NSString).utf8String, -1, nil)
                        index += 1
                    }
                }
                
                while sqlite3_step(queryStatement) == SQLITE_ROW {
                    var advertiser = AdvertiserTO(id: Int(sqlite3_column_int(queryStatement, 0)));
                    
                    advertiser.type = Int(sqlite3_column_int(queryStatement, 1));
                    advertiser.name = String(describing: String(cString: sqlite3_column_text(queryStatement, 2)));
                    advertiser.color = String(describing: String(cString: sqlite3_column_text(queryStatement, 3)));
                    advertiser.url = String(describing: String(cString: sqlite3_column_text(queryStatement, 4)));
                    advertiser.domain = String(describing: String(cString: sqlite3_column_text(queryStatement, 5)));
                    advertiser.logo = String(describing: String(cString: sqlite3_column_text(queryStatement, 6)));
                    advertiser.checkout = CheckoutTO(endpoint: String(describing: String(cString: sqlite3_column_text(queryStatement, 7))),data: String(describing: String(cString: sqlite3_column_text(queryStatement, 8))));
                    advertiser.product = CheckoutTO(endpoint: String(describing: String(cString: sqlite3_column_text(queryStatement, 9))),data: String(describing: String(cString: sqlite3_column_text(queryStatement, 10))));
                    advertiser.query = CheckoutTO(endpoint: String(describing: String(cString: sqlite3_column_text(queryStatement, 11))),data: String(describing: String(cString: sqlite3_column_text(queryStatement, 12))));
                    advertiser.omniboxTitle = String(describing: String(cString: sqlite3_column_text(queryStatement, 13)));
                    advertiser.omniboxDescription = String(describing: String(cString: sqlite3_column_text(queryStatement, 14)));
                    
                    result.append(advertiser)
                }
            } else {
                print("WOE SELECT advertiser statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(queryStatement)
            sqlite3_close(db)
            return result
        }
        
        return nil
    }
    
}
