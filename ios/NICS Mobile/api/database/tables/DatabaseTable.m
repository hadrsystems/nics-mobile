/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
//
//  DatabaseTable.m
//  nics_iOS
//
//

#import "DatabaseTable.h"

@implementation DatabaseTable

- (id)initWithName:(NSString *)tableName databaseQueue:(FMDatabaseQueue *)databaseQueue {
    self = [super init];
    
    if(self) {
        _tableName = tableName;
        _databaseQueue = databaseQueue;
    }
    
    return self;
}

- (void) createTableFromDictionary: (NSDictionary *) tableDictionary {
        NSMutableString *createQuery = [NSMutableString stringWithFormat:@"%@%@%@", @"create table ", _tableName, @" ("];

        BOOL isFirstColumn = true;
        
        for(NSString *key in [tableDictionary allKeys]) {
            if(isFirstColumn) {
                [createQuery appendFormat:@"%@%@%@", key, @" ", [tableDictionary objectForKey:key]];
                isFirstColumn = false;
            } else {
                [createQuery appendFormat:@"%@%@%@%@", @", ", key, @" ", [tableDictionary objectForKey:key]];
            }
        }
    
        [createQuery appendFormat:@"%@", @") "];
    
        
        [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
            if(![db tableExists:_tableName]) {
                BOOL success = [db executeUpdate:createQuery];
                
                if(success) {
                    NSLog(@"%@%@%@", @"Table ", _tableName, @" successfully created.");
                }
            } else {
                NSLog(@"%@%@%@", @"Table ", _tableName, @" already exisits.");
            }
        }];
}

- (void) dropTable {
    NSString *dropTableQuery = [NSString stringWithFormat:@"%@%@", @"drop table ", _tableName];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        if([db tableExists:_tableName]) {
            BOOL success = [db executeUpdate: dropTableQuery];
            
            if(success) {
                NSLog(@"%@%@%@", @"Table ", _tableName, @" successfully dropped.");
            }
        }
    }];
}

- (void) deleteRowsByKey: (NSString *) key value: (id) value {
    NSString *deleteRowQuery = [NSString stringWithFormat:@"%@%@%@%@%@", @"delete from ", _tableName, @" where ", key, @" =  ?"];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        BOOL success = [db executeUpdate: deleteRowQuery, value];
        if(success) {
            NSLog(@"%@%d%@%@", @"Deleted ", [db changes], @" rows from ", _tableName);
        }
    }];
}

- (void) deleteAllRows {
    NSString *deleteAllRowsQuery = [NSString stringWithFormat:@"%@%@", @"delete from ", _tableName];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        BOOL success = [db executeUpdate: deleteAllRowsQuery];
        if(success) {
            NSLog(@"%@%d%@%@", @"Deleted ", [db changes], @" rows from ", _tableName);
        }
    }];
}

- (BOOL) insertRowForTableDictionary: (NSDictionary *)tableDictionary  dataDictionary: (NSDictionary *)dataDictionary  {
    __block BOOL success = false;
    NSMutableString *insertRowQuery = [self generateInsertQuery:tableDictionary dataDictionary:dataDictionary];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        success = [db executeUpdate: insertRowQuery withParameterDictionary:dataDictionary];
        
        if(success) {
            NSLog(@"%@%d%@%@", @"Inserted ", [db changes], @" rows into ", _tableName);
        } else {
            NSLog(@"%@%@", @"Failed insert ", [db lastError]);
        }
    }];
    
    return success;
}

- (BOOL) insertAllRowsForTableDictionary: (NSDictionary *)tableDictionary  dataArray: (NSArray *)dataDictionary {
    __block BOOL success = false;
    
    if([dataDictionary count] > 0) {
        NSMutableString *insertRowQuery = [self generateInsertQuery:tableDictionary dataDictionary:[dataDictionary objectAtIndex:0]];
        
        [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
            int rowsSuccessfullyInserted = 0;
            for(NSDictionary *data in dataDictionary) {
                success = [db executeUpdate: insertRowQuery withParameterDictionary:data];
                
                if(success) {
                    rowsSuccessfullyInserted++;
                } else {
                    NSLog(@"%@", [db lastErrorMessage]);
                }
            }
            
            NSLog(@"%@%d%@%@", @"Inserted ", rowsSuccessfullyInserted, @" rows into ", _tableName);
        }];
    }
    
    return success;
}


- (NSMutableString *) generateInsertQuery: (NSDictionary *)tableDictionary  dataDictionary: (NSDictionary *)dataDictionary {
    NSMutableString *insertRowQuery =[NSMutableString stringWithFormat:@"%@%@%@", @"insert into ", _tableName, @" values ("];
    
    BOOL isFirstColumn = true;
    
    for(NSString *key in tableDictionary) {
        if([dataDictionary objectForKey:key] != nil) {
            if(isFirstColumn) {
                [insertRowQuery appendFormat:@"%@%@", @":", key];
                isFirstColumn = false;
            } else {
                [insertRowQuery appendFormat:@"%@%@", @", :", key];
            }
        } else {
            if(isFirstColumn) {
                [insertRowQuery appendFormat:@"%@", @"NULL"];
                isFirstColumn = false;
            } else {
                [insertRowQuery appendFormat:@"%@", @", NULL"];
            }
        }
    }
    
    [insertRowQuery appendString: @")"];
    
    return insertRowQuery;
}

- (NSMutableArray *) selectAllRowsAndOrderedBy: (NSArray *)columns isDescending: (BOOL) isDescending {
    NSMutableString *selectAllRowsQuery = [NSMutableString stringWithFormat:@"%@%@%@", @"select * from ", _tableName, @" order by "];
    
    BOOL isFirstColumn = true;
    for(NSString *column in columns) {
        if(isFirstColumn) {
            [selectAllRowsQuery appendFormat:@"%@", column];
            isFirstColumn = false;
        } else {
            [selectAllRowsQuery appendFormat:@"%@%@", @", ", column];
        }
    }
    
    if(isDescending) {
        [selectAllRowsQuery appendString:@" desc;"];
    } else {
        [selectAllRowsQuery appendString:@";"];
    }
    
    NSMutableArray *results = [NSMutableArray array];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        FMResultSet* rs = [db executeQuery:selectAllRowsQuery];
        while ([rs next]) {
            [results addObject:[rs resultDictionary]];
        }
    }];

    return results;
    
}
- (NSMutableArray *) selectRowsByKey: (NSString *)key value: (id)value  orderedBy: (NSArray *)columns isDescending: (BOOL) isDescending {
    NSMutableString *selectRowsByKeyQuery = [NSMutableString stringWithFormat:@"%@%@%@%@%@", @"select * from ", _tableName, @" where ", key, @" =  ? order by "];
    
    BOOL isFirstColumn = true;
    for(NSString *column in columns) {
        if(isFirstColumn) {
            [selectRowsByKeyQuery appendFormat:@"%@", column];
            isFirstColumn = false;
        } else {
            [selectRowsByKeyQuery appendFormat:@"%@%@", @", ", column];
        }
    }
    
    if(isDescending) {
        [selectRowsByKeyQuery appendString:@" desc;"];
    } else {
        [selectRowsByKeyQuery appendString:@";"];
    }
    
    NSMutableArray *results = [NSMutableArray array];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        FMResultSet* rs = [db executeQuery:selectRowsByKeyQuery, value];
        while ([rs next]) {
            [results addObject:[rs resultDictionary]];
        }
    }];
    
    return results;
}

- (NSMutableArray *) selectRowsByKeyDictionary: (NSDictionary *)keyDictionary orderedBy: (NSArray *)columns isDescending: (BOOL) isDescending {
    NSMutableString *selectRowsByKeyQuery = [NSMutableString stringWithFormat:@"%@%@%@", @"select * from ", _tableName, @" where "];
    
    BOOL isFirstColumn = true;
    NSMutableArray *valueArray = [[NSMutableArray alloc] init];
    for(NSString *key in keyDictionary) {
        if(isFirstColumn) {
            [selectRowsByKeyQuery appendFormat:@"%@%@", key, @" "];
            isFirstColumn = false;
        } else {
            [selectRowsByKeyQuery appendFormat:@"%@%@%@", @"AND ", key, @" "];
        }
        [valueArray addObject:[keyDictionary objectForKey:key]];
    }
    
    [selectRowsByKeyQuery appendString:@"order by "];
    
    isFirstColumn = true;
    for(NSString *column in columns) {
        if(isFirstColumn) {
            [selectRowsByKeyQuery appendFormat:@"%@", column];
            isFirstColumn = false;
        } else {
            [selectRowsByKeyQuery appendFormat:@"%@%@", @", ", column];
        }
    }
    
    if(isDescending) {
        [selectRowsByKeyQuery appendString:@" desc;"];
    } else {
        [selectRowsByKeyQuery appendString:@";"];
    }
    
    NSMutableArray *results = [NSMutableArray array];
    
    [_databaseQueue inTransaction:^(FMDatabase *db, BOOL *rollback) {
        FMResultSet* rs = [db executeQuery:selectRowsByKeyQuery withArgumentsInArray:valueArray];
        while ([rs next]) {
            [results addObject:[rs resultDictionary]];
        }
    }];
    
    return results;
}

@end
