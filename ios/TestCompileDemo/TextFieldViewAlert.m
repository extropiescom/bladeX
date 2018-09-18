//
//  TextFieldViewAlert.m
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/22.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import "TextFieldViewAlert.h"

@interface TextFieldViewAlert()
{
    BOOL buttonPressed;
    int minLengthRequired;
}

@property (nonatomic) CFRunLoopRef currentRunLoop;
@property (nonatomic, strong)UIAlertAction* secureTextAlertAction;

@end


@implementation TextFieldViewAlert

+(NSString*)doModal:(UIViewController *)parent
              title:(NSString*)title
            message:(NSString*)message
         isPassword:(BOOL)isPassword
  minLengthRequired:(int)minLengthRequired
       keyboardType:(UIKeyboardType)keyboardType
{
    __block NSString* result = nil;
    TextFieldViewAlert* view = [TextFieldViewAlert new];
    view->buttonPressed = NO;
    view->minLengthRequired = minLengthRequired;
    //if ([NSThread isMainThread]) {
        view.currentRunLoop = CFRunLoopGetCurrent();
        NSString *cancelButtonTitle = NSLocalizedString(@"Cancel", nil);
        NSString *otherButtonTitle = NSLocalizedString(@"OK", nil);
        
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
        
        // Add the text field for the secure text entry.
        [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
            // Listen for changes to the text field's text so that we can toggle the current
            // action's enabled property based on whether the user has entered a sufficiently
            // secure entry.
            [[NSNotificationCenter defaultCenter] addObserver:view selector:@selector(handleTextFieldTextDidChangeNotification:) name:UITextFieldTextDidChangeNotification object:textField];
            textField.keyboardType = keyboardType;
            textField.secureTextEntry = YES;
        }];
        
        // Create the actions.
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:cancelButtonTitle style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
            [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:alertController.textFields.firstObject];
            view->buttonPressed = YES;
            result = nil;
            CFRunLoopStop(view.currentRunLoop);
            
            NSLog(@"The \"Secure Text Entry\" alert's cancel action occured.");
            
            // Stop listening for text changed notifications.
//
        }];
        
        UIAlertAction *otherAction = [UIAlertAction actionWithTitle:otherButtonTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
            [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:alertController.textFields.firstObject];
            result = alertController.textFields.firstObject.text;
            view->buttonPressed = YES;
            CFRunLoopStop(view.currentRunLoop);
            NSLog(@"The \"Secure Text Entry\" alert's other action occured.");
            
            // Stop listening for text changed notifications.
//
        }];
        
        // The text field initially has no text in the text field, so we'll disable it.
        otherAction.enabled = NO;
        
        // Hold onto the secure text alert action to toggle the enabled/disabled state when the text changed.
        view.secureTextAlertAction = otherAction;
        
        // Add the actions.
        [alertController addAction:cancelAction];
        [alertController addAction:otherAction];
        
        [parent presentViewController:alertController animated:YES completion:nil];
        while (!view->buttonPressed) {
            CFRunLoopRun();
        }
        
//    } else {
//        dispatch_sync(dispatch_get_main_queue(), ^{
//            view.currentRunLoop = CFRunLoopGetCurrent();
//            NSString *cancelButtonTitle = NSLocalizedString(@"Cancel", nil);
//            NSString *otherButtonTitle = NSLocalizedString(@"OK", nil);
//
//            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title message:message preferredStyle:UIAlertControllerStyleAlert];
//
//            // Add the text field for the secure text entry.
//            [alertController addTextFieldWithConfigurationHandler:^(UITextField *textField) {
//                // Listen for changes to the text field's text so that we can toggle the current
//                // action's enabled property based on whether the user has entered a sufficiently
//                // secure entry.
//                [[NSNotificationCenter defaultCenter] addObserver:view selector:@selector(handleTextFieldTextDidChangeNotification:) name:UITextFieldTextDidChangeNotification object:textField];
//                textField.keyboardType = UIKeyboardTypeNumberPad;
//                textField.secureTextEntry = YES;
//            }];
//
//            // Create the actions.
//            UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:cancelButtonTitle style:UIAlertActionStyleCancel handler:^(UIAlertAction *action) {
//                [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:alertController.textFields.firstObject];
//                view->buttonPressed = YES;
//                result = nil;
//                CFRunLoopStop(view.currentRunLoop);
//
//                NSLog(@"The \"Secure Text Entry\" alert's cancel action occured.");
//
//                // Stop listening for text changed notifications.
//                //
//            }];
//
//            UIAlertAction *otherAction = [UIAlertAction actionWithTitle:otherButtonTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction *action) {
//                [[NSNotificationCenter defaultCenter] removeObserver:self name:UITextFieldTextDidChangeNotification object:alertController.textFields.firstObject];
//                result = alertController.textFields.firstObject.text;
//                view->buttonPressed = YES;
//                CFRunLoopStop(view.currentRunLoop);
//                NSLog(@"The \"Secure Text Entry\" alert's other action occured.");
//
//                // Stop listening for text changed notifications.
//                //
//            }];
//
//            // The text field initially has no text in the text field, so we'll disable it.
//            otherAction.enabled = NO;
//
//            // Hold onto the secure text alert action to toggle the enabled/disabled state when the text changed.
//            view.secureTextAlertAction = otherAction;
//
//            // Add the actions.
//            [alertController addAction:cancelAction];
//            [alertController addAction:otherAction];
//
//            [parent presentViewController:alertController animated:YES completion:nil];
//            while (!view->buttonPressed) {
//                CFRunLoopRun();
//            }
//        });
//    }
    return result;
}

- (void)handleTextFieldTextDidChangeNotification:(NSNotification *)notification {
    UITextField *textField = notification.object;
    
    // Enforce a minimum length of >= 5 characters for secure text alerts.
    self.secureTextAlertAction.enabled = textField.text.length >= self->minLengthRequired;
}

@end
