//
//  PickerViewAlert.m
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/21.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import "PickerViewAlert.h"


@interface PickerViewAlert () <UIPickerViewDelegate,UIPickerViewDataSource>
{
    NSInteger selectedRow;
    BOOL buttonPressed;
}

@property (nonatomic,strong) UIPickerView * picker; //不解释
@property (nonatomic,strong) NSArray * myDataSource;//pickerView的数据源
//@property (nonatomic,strong) NSMutableDictionary * sel_items;//记录pickerView选择的每一列的数据

@property (nonatomic) CFRunLoopRef currentRunLoop;

@end


@implementation PickerViewAlert

+(int)doModal:(UIViewController *)parent dataSouce:(NSArray *)array
{
    __block int result = 0;
    PickerViewAlert* view = [[PickerViewAlert alloc] init];
    view->buttonPressed = NO;
    view.myDataSource = array;
    //if (![NSThread isMainThread]) {
        view.currentRunLoop = CFRunLoopGetCurrent();
        //sheet的提示信息
        NSString * message = @"\n\n\n\n\n\n\n\n\n\n";
        //<1>初始化
        UIAlertController * sheet = [UIAlertController alertControllerWithTitle:nil message:message preferredStyle:UIAlertControllerStyleActionSheet];
        //<2>添加'确定'按钮
        [sheet addAction:[UIAlertAction actionWithTitle:@"Confirm" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            //点击'确定'的处理...
            view->buttonPressed = YES;
            result = view->selectedRow;
            CFRunLoopStop(view.currentRunLoop);
        }]];
        
        //<3>添加'取消按钮'
        [sheet addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            //点击'取消'的处理...
            result = -1;
            view->buttonPressed = YES;
            CFRunLoopStop(view.currentRunLoop);
        }]];
        
        CGFloat pickerX = 0;
        CGFloat pickerY = 60;
        CGFloat pickerW = sheet.view.frame.size.width - 20;
        CGFloat pickerH = 7 * 20 + 10;// 一个换行为20
        
        UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, pickerW, 40)];
        label.text = @"Please choose signature verify method:";
        [sheet.view addSubview:label];
        view.picker = [[UIPickerView alloc]initWithFrame:CGRectMake(pickerX, pickerY, pickerW, pickerH)];
        view.picker.backgroundColor = [UIColor colorWithRed:247/255.0 green:247/255.0 blue:247/255.0 alpha:1];
        view.picker.delegate = view;   // 设置代理
        view.picker.dataSource = view; // 设置数据源
        [sheet.view addSubview:view.picker];
        [view.picker selectRow:0 inComponent:0 animated:YES];
        
        [parent presentViewController:sheet animated:YES completion:nil];
        while (!view->buttonPressed) {
            CFRunLoopRun();
        }
        NSLog(@"CFRunLoopRun ends");
//    } else {
//        dispatch_async(dispatch_get_main_queue(), ^{
//            view.currentRunLoop = CFRunLoopGetCurrent();
//            //sheet的提示信息
//            NSString * message = @"\n\n\n\n\n\n\n\n\n";
//            //<1>初始化
//            UIAlertController * sheet = [UIAlertController alertControllerWithTitle:nil message:message preferredStyle:UIAlertControllerStyleActionSheet];
//            //<2>添加'确定'按钮
//            [sheet addAction:[UIAlertAction actionWithTitle:@"Confirm" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//                //点击'确定'的处理...
//                view->buttonPressed = YES;
//                result = view->selectedRow;
//                CFRunLoopStop(view.currentRunLoop);
//            }]];
//            
//            //<3>添加'取消按钮'
//            [sheet addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
//                //点击'取消'的处理...
//                result = -1;
//                view->buttonPressed = YES;
//                CFRunLoopStop(view.currentRunLoop);
//            }]];
//            
//            CGFloat pickerX = 0;
//            CGFloat pickerY = 40;
//            CGFloat pickerW = sheet.view.frame.size.width - 20;
//            CGFloat pickerH = 7 * 20 + 10;// 一个换行为20
//            
//            UILabel* label = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, pickerW, 40)];
//            label.text = @"Please choose signature verify method:";
//            [sheet.view addSubview:label];
//            view.picker = [[UIPickerView alloc]initWithFrame:CGRectMake(pickerX, pickerY, pickerW, pickerH)];
//            view.picker.backgroundColor = [UIColor colorWithRed:247/255.0 green:247/255.0 blue:247/255.0 alpha:1];
//            view.picker.delegate = view;   // 设置代理
//            view.picker.dataSource = view; // 设置数据源
//            [sheet.view addSubview:view.picker];
//            [view.picker selectRow:0 inComponent:0 animated:YES];
//            
//            [parent presentViewController:sheet animated:YES completion:nil];
//            while (!view->buttonPressed) {
//                CFRunLoopRun();
//            }
//            NSLog(@"CFRunLoopRun ends");
//        });
//    }
    return result;
}

- (NSInteger)numberOfComponentsInPickerView:(nonnull UIPickerView *)pickerView {
    return 1;
}

- (NSInteger)pickerView:(nonnull UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component {
    return self.myDataSource.count;
}

//<3>设置每行显示的信息
-(NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    //component表示的是列号
    //row表示的是每一列中的行号
    return _myDataSource[row];
    
}
-(void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    selectedRow = row;
}


@end
