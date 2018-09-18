//
//  VCCellModel.h
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/13.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface VCCellModel : NSObject

@property (nonatomic, copy) NSString *peripheralName;

@property (nonatomic, assign) NSInteger RSSI;

@property (nonatomic, assign) NSInteger state;

@end
