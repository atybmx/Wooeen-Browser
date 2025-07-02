import Foundation
import SQLite3

public class TrackingDAO{
    
    public init(){
        
    }
    
    public func insert(tracking: TrackingTO){
        if((tracking.domain ?? "").isEmpty){
            return
        }
        
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let insertStatementString = """
            INSERT INTO tracking (
            id,
            id_platform,
            id_advertiser,
            advertiser_type,
            priority,
            deeplink,
            params,
            domain,
            payout,
            commission_type,
            commission_avg_1,
            commission_min_1,
            commission_max_1,
            commission_avg_2,
            commission_min_2,
            commission_max_2,
            approval_days
            ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            ON CONFLICT(id) DO UPDATE SET
            id_platform = excluded.id_platform,
            id_advertiser = excluded.id_advertiser,
            advertiser_type = excluded.advertiser_type,
            priority = excluded.priority,
            deeplink = excluded.deeplink,
            params = excluded.params,
            domain = excluded.domain,
            payout = excluded.payout,
            commission_type = excluded.commission_type,
            commission_avg_1 = excluded.commission_avg_1,
            commission_min_1 = excluded.commission_min_1,
            commission_max_1 = excluded.commission_max_1,
            commission_avg_2 = excluded.commission_avg_2,
            commission_min_2 = excluded.commission_min_2,
            commission_max_2 = excluded.commission_max_2,
            approval_days = excluded.approval_days
            """
            var insertStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, insertStatementString, -1, &insertStatement, nil) == SQLITE_OK {
                var index: Int32 = 1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.id));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.platformId ?? 0));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.advertiserId ?? 0));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.advertiserType ?? 0));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.priority ?? 0));index+=1
                sqlite3_bind_text(insertStatement, index, ((tracking.deeplink ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((tracking.params ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_text(insertStatement, index, ((tracking.domain ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.payout ?? 0));index+=1
                sqlite3_bind_text(insertStatement, index, ((tracking.commissionType ?? "") as NSString).utf8String, -1, nil);index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionAvg1 ?? 0));index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionMin1 ?? 0));index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionMax1 ?? 0));index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionAvg2 ?? 0));index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionMin2 ?? 0));index+=1
                sqlite3_bind_double(insertStatement, index, Double(tracking.commissionMax2 ?? 0));index+=1
                sqlite3_bind_int(insertStatement, index, Int32(tracking.approvalDays ?? 0));index+=1
                
                if sqlite3_step(insertStatement) != SQLITE_DONE {
                    print("WOE tracking could not insert row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE tracking INSERT statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(insertStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteByID(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM tracking WHERE id = ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE tracking Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE tracking DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteFrom(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, 500)
            
            let deleteStatementStirng = "DELETE FROM tracking WHERE id > ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE tracking Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE tracking DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteTo(id:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM tracking WHERE id < ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(id))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE tracking Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE tracking DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func deleteByRange(from:Int, to:Int) {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            let deleteStatementStirng = "DELETE FROM tracking WHERE id >= ? AND id <= ?;"
            var deleteStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, deleteStatementStirng, -1, &deleteStatement, nil) == SQLITE_OK {
                sqlite3_bind_int(deleteStatement, 1, Int32(from))
                sqlite3_bind_int(deleteStatement, 2, Int32(to))
                if sqlite3_step(deleteStatement) != SQLITE_DONE {
                    print("WOE tracking Could not delete row. \(String(cString: sqlite3_errmsg(db)))")
                }
            } else {
                print("WOE tracking DELETE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(deleteStatement)
            sqlite3_close(db)
        }
    }
    
    public func search(search: TrackingTOP?) -> [TrackingTO]? {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, WooeenDAO.busyTimeout)
            
            var sql = """
                SELECT
                id,
                id_platform,
                id_advertiser,
                advertiser_type,
                priority,
                deeplink,
                params,
                domain,
                payout,
                commission_type,
                commission_avg_1,
                commission_min_1,
                commission_max_1,
                commission_avg_2,
                commission_min_2,
                commission_max_2,
                approval_days
                FROM tracking
                """
            
            if let search = search {
                var sqlWhere = " WHERE "
                
                if let id = search.id, id > 0 {
                    sqlWhere += "tracking.id = ? AND "
                }
                
                if let domain = search.domain, !domain.isEmpty{
                    sqlWhere += "? LIKE '%.' || tracking.domain AND "
                }
                
                if sqlWhere.hasSuffix("AND ") {
                    sqlWhere = String(sqlWhere.prefix(sqlWhere.count - 4))
                }else{
                    sqlWhere = ""
                }
                
                sql += " "+sqlWhere
                
                if let qtdPerPage = search.qtdPerPage, qtdPerPage > 0 {
                    sql += "limit \((search.page ?? 0) * (search.qtdPerPage)!),\(search.qtdPerPage!)"
                }
                
            }
            
            var queryStatement: OpaquePointer? = nil
            var result : [TrackingTO] = []
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
                }
                
                while sqlite3_step(queryStatement) == SQLITE_ROW {
                    var tracking = TrackingTO(id: Int(sqlite3_column_int(queryStatement, 0)));
                    
                    tracking.platformId = Int(sqlite3_column_int(queryStatement, 1));
                    tracking.advertiserId = Int(sqlite3_column_int(queryStatement, 2));
                    tracking.advertiserType = Int(sqlite3_column_int(queryStatement, 3));
                    tracking.priority = Int(sqlite3_column_int(queryStatement, 4));
                    tracking.deeplink = String(describing: String(cString: sqlite3_column_text(queryStatement, 5)));
                    tracking.params = String(describing: String(cString: sqlite3_column_text(queryStatement, 6)));
                    tracking.domain = String(describing: String(cString: sqlite3_column_text(queryStatement, 7)));
                    tracking.payout = Double(sqlite3_column_double(queryStatement, 8));
                    tracking.commissionType = String(describing: String(cString: sqlite3_column_text(queryStatement, 9)));
                    tracking.commissionAvg1 = Double(sqlite3_column_double(queryStatement, 10));
                    tracking.commissionMin1 = Double(sqlite3_column_double(queryStatement, 11));
                    tracking.commissionMax1 = Double(sqlite3_column_double(queryStatement, 12));
                    tracking.commissionAvg2 = Double(sqlite3_column_double(queryStatement, 13));
                    tracking.commissionMin2 = Double(sqlite3_column_double(queryStatement, 14));
                    tracking.commissionMax2 = Double(sqlite3_column_double(queryStatement, 15));
                    tracking.approvalDays = Int(sqlite3_column_int(queryStatement, 16));
                    
                    result.append(tracking)
                }
            } else {
                print("WOE SELECT tracking statement could not be prepared \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(queryStatement)
            sqlite3_close(db)
            
            return result
        }
        
       return nil
    }
    
}
