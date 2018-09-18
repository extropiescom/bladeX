//
//  TextFieldViewAlert.h
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/22.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface TextFieldViewAlert : NSObject

+(NSString*)doModal:(UIViewController *)parent
              title:(NSString*)title
            message:(NSString*)message
         isPassword:(BOOL)isPassword
  minLengthRequired:(int)minLengthRequired
       keyboardType:(UIKeyboardType)keyboardType;

@end
