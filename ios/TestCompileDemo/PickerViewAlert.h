//
//  PickerViewAlert.h
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/21.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface PickerViewAlert : NSObject

+(int)doModal:(UIViewController *)parent dataSouce:(NSArray *)array;

@end
