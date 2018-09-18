//
//  Test_C_EWallet_ViewController.m
//  TestCompileDemo
//
//  Created by 周权威 on 2018/8/15.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import "Test_C_EWallet_ViewController.h"
#import "PA_EWallet.h"
#import "ToolInputView.h"
#import "PickerViewAlert.h"
#import "TextFieldViewAlert.h"
#import "Utils.h"

@interface Test_C_EWallet_ViewController ()
{
    NSInteger logCounter;
    int lastSignState;
    BOOL authTypeCached;
    unsigned char nAuthType;
    int authTypeResult;
    BOOL pinCached;
    NSString *pin;
    int pinResult;
}

@property (nonatomic, strong) UITextView *in_outTextView;

@property (nonatomic, strong) UIButton *getDevInfoBtn;

@property (nonatomic, strong) UIButton *initiPinBtn;

@property (nonatomic, strong) UIButton *verifyPinBtn;

@property (nonatomic, strong) UIButton *changePinBtn;

@property (nonatomic, strong) UIButton *getFPListBtn;

@property (nonatomic, strong) UIButton *enrollFPBtn;

@property (nonatomic, strong) UIButton *verifyFPBtn;

@property (nonatomic, strong) UIButton *deleteFPBtn;

@property (nonatomic, strong) UIButton *formatBtn;

@property (nonatomic, strong) UIButton *genSeedBtn;

@property (nonatomic, strong) UIButton *ETHSignBtn;

@property (nonatomic, strong) UIButton *EOSSignBtn;

@property (nonatomic, strong) UIButton *CYBSignBtn;

@property (nonatomic, strong) UIButton *importMNEBtn;

@property (nonatomic, strong) UIButton *getAddressBtn;

@property (nonatomic, strong) UIButton *getDeviceCheckCodeBtn;

@property (nonatomic, strong) UIButton *freeContextBtn;

@property (nonatomic, strong) UIButton *calibrateFPBtn;

@property (nonatomic, strong) UIButton *abortBtn;
@property (nonatomic, strong) UIButton *signAbortBtn;

@property (nonatomic, strong) UIButton *clearLogBtn;

@property (nonatomic, strong) UIButton *clearScreenBtn;
@property (nonatomic, strong) UIButton *powerOffBtn;

@property (nonatomic, strong) UIButton *deviceCategoryBtn;
@property (nonatomic, strong) UIButton *fPrintCategoryBtn;
@property (nonatomic, strong) UIButton *InitCategoryBtn;
@property (nonatomic, strong) UIButton *walletCategoryBtn;

@property (nonatomic, strong) NSArray *deviceCategoryList;
@property (nonatomic, strong) NSArray *fPrintCategoryList;
@property (nonatomic, strong) NSArray *InitCategoryList;
@property (nonatomic, strong) NSArray *walletCategoryList;
@property (nonatomic, strong) NSArray *categoryList;

@property (nonatomic,strong)ToolInputView *inputView;

@property (nonatomic, assign) BOOL abortBtnState;

@property (nonatomic, strong) NSCondition *abortCondition;

@property (nonatomic, copy) void(^abortHandelBlock)(BOOL abortState);

@end

@implementation Test_C_EWallet_ViewController

static Test_C_EWallet_ViewController *selfClass =nil;


#pragma mark-- C callback for ETH/EOS sign method

const uint32_t puiDerivePathETH[] = {0, 0x8000002c, 0x8000003c, 0x80000000, 0x00000000, 0x00000000};
const uint32_t puiDerivePathEOS[] = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};
const uint32_t puiDerivePathCYB[] = {0, 0, 1, 0x00000080, 0x00000000, 0x00000000};

int GetAuthType(void * const pCallbackContext, unsigned char * const pnAuthType)
{
    int rtn = 0;
    if (!selfClass->authTypeCached) {
        [selfClass getAuthType];
    }
    rtn = selfClass->authTypeResult;
    if (rtn == PAEW_RET_SUCCESS) {
        *pnAuthType = selfClass->nAuthType;
    }
    selfClass->authTypeCached = NO;
    return rtn;
}

int GetPin(void * const pCallbackContext, unsigned char * const pbPIN, size_t * const pnPINLen)
{
    int rtn = 0;
    if (!selfClass->pinCached) {
        [selfClass getPIN];
    }
    rtn = selfClass->pinResult;
    if (rtn == PAEW_RET_SUCCESS) {
        *pnPINLen = selfClass->pin.length;
        strcpy((char *)pbPIN, [selfClass->pin UTF8String]);
    }
    selfClass->pinCached = NO;
    return rtn;
}

int PutSignState(void * const pCallbackContext, const int nSignState)
{
    if (nSignState != selfClass->lastSignState) {
        [selfClass printLog:[Utils errorCodeToString:nSignState]];
        selfClass->lastSignState = nSignState;
    }
    //here is a good place to canel sign function
    if (selfClass.abortBtnState) {
        [selfClass.abortCondition lock];
        !selfClass.abortHandelBlock ? : selfClass.abortHandelBlock(YES);
        [selfClass.abortCondition wait];
        [selfClass.abortCondition unlock];
        selfClass.abortBtnState = NO;
    }
    return 0;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.modalPresentationCapturesStatusBarAppearance = NO;
    
    [self addSubViewAfterVDLoad];
    
    _deviceCategoryList = [NSArray arrayWithObjects:_getDevInfoBtn, _initiPinBtn, _verifyPinBtn, _changePinBtn, _formatBtn, _clearScreenBtn, _freeContextBtn, _powerOffBtn, nil];
    _fPrintCategoryList = [NSArray arrayWithObjects:_getFPListBtn, _enrollFPBtn, _verifyFPBtn, _deleteFPBtn, _calibrateFPBtn, _abortBtn, nil];
    _InitCategoryList = [NSArray arrayWithObjects:_genSeedBtn, _importMNEBtn, nil];
    _walletCategoryList = [NSArray arrayWithObjects:_getAddressBtn, _getDeviceCheckCodeBtn, _ETHSignBtn, _EOSSignBtn, _CYBSignBtn,_signAbortBtn, nil];
    _categoryList = [NSArray arrayWithObjects:_deviceCategoryBtn, _fPrintCategoryBtn, _InitCategoryBtn, _walletCategoryBtn, nil];
    
    [self categoryAction:_deviceCategoryBtn];
    
    self.abortBtnState = NO;
    self->logCounter = 0;
    self->lastSignState = PAEW_RET_SUCCESS;
    self->nAuthType = 0xFF;
    selfClass = self;
    self->pinResult = PAEW_RET_SUCCESS;
    self->authTypeResult = PAEW_RET_SUCCESS;
    self->authTypeCached = NO;
    self->pinCached = NO;
}

- (void) showCategory:(NSArray *) categoryList
{
    for (UIButton* btn in _deviceCategoryList) {
        btn.hidden = categoryList == _deviceCategoryList ? NO : YES;
    }
    for (UIButton* btn in _fPrintCategoryList) {
        btn.hidden = categoryList == _fPrintCategoryList ? NO : YES;
    }
    for (UIButton* btn in _InitCategoryList) {
        btn.hidden = categoryList == _InitCategoryList ? NO : YES;
    }
    for (UIButton* btn in _walletCategoryList) {
        btn.hidden = categoryList == _walletCategoryList ? NO : YES;
    }
}

- (void)addSubViewAfterVDLoad
{
    [self.view addSubview:self.in_outTextView];
    [self.in_outTextView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.view.mas_top).offset(10);
        make.left.mas_equalTo(self.view.mas_left).offset(10);
        make.right.mas_equalTo(self.view.mas_right).offset(-10);
        make.height.mas_equalTo(self.view.mas_height).multipliedBy(0.33);
    }];
    self.in_outTextView.layoutManager.allowsNonContiguousLayout = NO;
    
    [self.view addSubview:self.deviceCategoryBtn];
//    [self.deviceCategoryBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.mas_equalTo(self.in_outTextView.mas_bottom).offset(20);
//        make.width.mas_equalTo(self.in_outTextView.mas_width).multipliedBy(0.25);
//        make.height.mas_equalTo(30);
//        make.left.mas_equalTo(self.in_outTextView.mas_left);
//    }];
    
    [self.view addSubview:self.fPrintCategoryBtn];
//    [self.fPrintCategoryBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.mas_equalTo(self.in_outTextView.mas_bottom).offset(20);
//        make.width.mas_equalTo(self.in_outTextView.mas_width).multipliedBy(0.25);
//        make.height.mas_equalTo(30);
//        make.left.mas_equalTo(self.deviceCategoryBtn.mas_right);
//    }];
    
    [self.view addSubview:self.InitCategoryBtn];
//    [self.InitCategoryBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.mas_equalTo(self.in_outTextView.mas_bottom).offset(20);
//        make.width.mas_equalTo(self.in_outTextView.mas_width).multipliedBy(0.25);
//        make.height.mas_equalTo(30);
//        make.left.mas_equalTo(self.fPrintCategoryBtn.mas_right);
//    }];
    
    [self.view addSubview:self.walletCategoryBtn];
//    [self.walletCategoryBtn mas_makeConstraints:^(MASConstraintMaker *make) {
//        make.top.mas_equalTo(self.in_outTextView.mas_bottom).offset(20);
//        make.width.mas_equalTo(self.in_outTextView.mas_width).multipliedBy(0.25);
//        make.height.mas_equalTo(30);
//        make.left.mas_equalTo(self.InitCategoryBtn.mas_right);
//    }];
    
    NSArray *catArr = @[self.deviceCategoryBtn, self.fPrintCategoryBtn, self.InitCategoryBtn, self.walletCategoryBtn];
    [catArr mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:1 leadSpacing:10 tailSpacing:10];
    [catArr mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.in_outTextView.mas_bottom).offset(20);
        make.height.mas_equalTo(30);
    }];
    
    [self.view addSubview:self.getDevInfoBtn];
    [self.view addSubview:self.initiPinBtn];
    [self.view addSubview:self.verifyPinBtn];
    
        

    [self.view addSubview:self.changePinBtn];
    [self.view addSubview:self.formatBtn];
    [self.view addSubview:self.clearScreenBtn];
    [self.view addSubview:self.freeContextBtn];
    [self.view addSubview:self.powerOffBtn];
    
    [self.view addSubview:self.getFPListBtn];
    [self.view addSubview:self.enrollFPBtn];
    [self.view addSubview:self.verifyFPBtn];
    [self.view addSubview:self.deleteFPBtn];
    [self.view addSubview:self.calibrateFPBtn];
    [self.view addSubview:self.abortBtn];
    
    [self.view addSubview:self.genSeedBtn];
    [self.view addSubview:self.importMNEBtn];
    
    [self.view addSubview:self.getAddressBtn];
    [self.view addSubview:self.getDeviceCheckCodeBtn];
    [self.view addSubview:self.signAbortBtn];
    [self.view addSubview:self.ETHSignBtn];
    [self.view addSubview:self.EOSSignBtn];
    [self.view addSubview:self.CYBSignBtn];
    
        NSArray *cat1Arr1 = @[self.getDevInfoBtn, self.initiPinBtn, self.verifyPinBtn];
        [cat1Arr1 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat1Arr1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.deviceCategoryBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        NSArray *cat1Arr2 = @[self.changePinBtn, self.formatBtn, self.clearScreenBtn];
        [cat1Arr2 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat1Arr2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.getDevInfoBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        NSArray *cat1Arr3 = @[self.freeContextBtn, self.powerOffBtn];
        [cat1Arr3 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat1Arr3 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.changePinBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        
        NSArray *cat2Arr1 = @[self.getFPListBtn, self.enrollFPBtn, self.verifyFPBtn];
        [cat2Arr1 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat2Arr1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.deviceCategoryBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        NSArray *cat2Arr2 = @[self.deleteFPBtn, self.calibrateFPBtn, self.abortBtn];
        [cat2Arr2 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat2Arr2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.getDevInfoBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        
        
        NSArray *cat3Arr1 = @[self.genSeedBtn, self.importMNEBtn];
        [cat3Arr1 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat3Arr1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.deviceCategoryBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        
        NSArray *cat4Arr1 = @[self.getAddressBtn, self.getDeviceCheckCodeBtn, self.signAbortBtn];
        [cat4Arr1 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat4Arr1 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.deviceCategoryBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
        NSArray *cat4Arr2 = @[self.ETHSignBtn, self.EOSSignBtn, self.CYBSignBtn];
        [cat4Arr2 mas_distributeViewsAlongAxis:MASAxisTypeHorizontal withFixedSpacing:10 leadSpacing:30 tailSpacing:30];
        [cat4Arr2 mas_makeConstraints:^(MASConstraintMaker *make) {
            make.top.mas_equalTo(self.getDevInfoBtn.mas_bottom).offset(20);
            make.height.mas_equalTo(30);
        }];
    
    
    
    [self.view addSubview:self.clearLogBtn];
    [self.clearLogBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.freeContextBtn.mas_bottom).offset(20);
        make.width.mas_equalTo(100);
        make.height.mas_equalTo(30);
        make.right.mas_equalTo(self.initiPinBtn.mas_right);
    }];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidDisappear:(BOOL)animated {
    if (self.savedDevice) {
        [self freeContextBtnAction];
    }
}

- (UIButton *)deviceCategoryBtn
{
    if (!_deviceCategoryBtn) {
        _deviceCategoryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_deviceCategoryBtn setTitle:@"Device" forState:UIControlStateNormal];
        [_deviceCategoryBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _deviceCategoryBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_deviceCategoryBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_deviceCategoryBtn addTarget:self action:@selector(categoryAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _deviceCategoryBtn;
}

- (UIButton *)fPrintCategoryBtn
{
    if (!_fPrintCategoryBtn) {
        _fPrintCategoryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_fPrintCategoryBtn setTitle:@"FPrint" forState:UIControlStateNormal];
        [_fPrintCategoryBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _fPrintCategoryBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_fPrintCategoryBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_fPrintCategoryBtn addTarget:self action:@selector(categoryAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _fPrintCategoryBtn;
}

- (UIButton *)InitCategoryBtn
{
    if (!_InitCategoryBtn) {
        _InitCategoryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_InitCategoryBtn setTitle:@"Init" forState:UIControlStateNormal];
        [_InitCategoryBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _InitCategoryBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_InitCategoryBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_InitCategoryBtn addTarget:self action:@selector(categoryAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _InitCategoryBtn;
}

- (UIButton *)walletCategoryBtn
{
    if (!_walletCategoryBtn) {
        _walletCategoryBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_walletCategoryBtn setTitle:@"Wallet" forState:UIControlStateNormal];
        [_walletCategoryBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _walletCategoryBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_walletCategoryBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_walletCategoryBtn addTarget:self action:@selector(categoryAction:) forControlEvents:UIControlEventTouchUpInside];
    }
    return _walletCategoryBtn;
}

- (void)categoryAction:(id)sender
{
    for (UIButton *btn in _categoryList) {
        if (sender == btn) {
            btn.backgroundColor = [UIColor whiteColor];
        } else {
            btn.backgroundColor = [UIColor brownColor];
        }
    }
    if (sender == _deviceCategoryBtn) {
        [self showCategory:_deviceCategoryList];
    } else if (sender == _fPrintCategoryBtn) {
        [self showCategory:_fPrintCategoryList];
    } else if (sender == _InitCategoryBtn) {
        [self showCategory:_InitCategoryList];
    } else if (sender == _walletCategoryBtn) {
        [self showCategory:_walletCategoryList];
    }
}

- (UIButton *)signAbortBtn
{
    if (!_signAbortBtn) {
        _signAbortBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_signAbortBtn setTitle:@"Abort" forState:UIControlStateNormal];
        [_signAbortBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _signAbortBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_signAbortBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_signAbortBtn addTarget:self action:@selector(abortBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _signAbortBtn;
}

- (UIButton *)abortBtn
{
    if (!_abortBtn) {
        _abortBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_abortBtn setTitle:@"Abort" forState:UIControlStateNormal];
        [_abortBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _abortBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_abortBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_abortBtn addTarget:self action:@selector(abortBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _abortBtn;
}

- (UIButton *)clearLogBtn
{
    if (!_clearLogBtn) {
        _clearLogBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_clearLogBtn setTitle:@"ClearLog" forState:UIControlStateNormal];
        [_clearLogBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _clearLogBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_clearLogBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_clearLogBtn addTarget:self action:@selector(clearLogBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _clearLogBtn;
}

- (UIButton *)clearScreenBtn
{
    if (!_clearScreenBtn) {
        _clearScreenBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_clearScreenBtn setTitle:@"ClearScreen" forState:UIControlStateNormal];
        [_clearScreenBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _clearScreenBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_clearScreenBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_clearScreenBtn addTarget:self action:@selector(clearScreenBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _clearScreenBtn;
}

- (UIButton *)powerOffBtn
{
    if (!_powerOffBtn) {
        _powerOffBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_powerOffBtn setTitle:@"PowerOff" forState:UIControlStateNormal];
        [_powerOffBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _powerOffBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_powerOffBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_powerOffBtn addTarget:self action:@selector(powerOffBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _powerOffBtn;
}

- (void)powerOffBtnAction
{
    [self printLog:@"ready to call PAEW_PowerOff"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_PowerOff(ppPAEWContext, devIdx);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_PowerOff returns failed: %@", [Utils errorCodeToString:iRtn]];
        } else {
            [self printLog:@"PAEW_PowerOff returns success"];
        }
        
    });
}

- (void)clearScreenBtnAction
{
    [self printLog:@"ready to call PAEW_ClearLCD"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_ClearLCD(ppPAEWContext, devIdx);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_ClearLCD returns failed: %@", [Utils errorCodeToString:iRtn]];
        } else {
            [self printLog:@"PAEW_ClearLCD returns success"];
        }
        
    });
}

- (void)clearLogBtnAction
{
    self->logCounter = 0;
    if ([NSThread isMainThread]) {
        self.in_outTextView.text = @"";
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.in_outTextView.text = @"";
        });
    }
}

- (void)abortBtnAction
{
    self.abortBtnState = YES;
    __weak typeof(self) weakSelf = self;
    self.abortHandelBlock = ^(BOOL abortState) {
        if (!abortState) {
            return ;
        }
        __strong typeof(weakSelf) strongSelf = weakSelf;
        dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
            int devIdx = 0;
            uint64_t temp = strongSelf.savedDevice;
            void *ppPAEWContext = (void*)temp;
            int iRtn = PAEW_RET_UNKNOWN_FAIL;
            
            strongSelf.abortBtnState = NO;
            [strongSelf printLog:@"ready to call PAEW_AbortFP"];
            iRtn = PAEW_AbortFP(ppPAEWContext, devIdx);
            [strongSelf.abortCondition lock];
            [strongSelf.abortCondition signal];
            [strongSelf.abortCondition unlock];
            
            if (iRtn != PAEW_RET_SUCCESS) {
                [strongSelf printLog:@"PAEW_AbortFP returns failed %@", [Utils errorCodeToString:iRtn]];
                return ;
            }
            
            [strongSelf printLog:@"PAEW_AbortFP returns success"];
        });
    };
    
}

- (UIButton *)calibrateFPBtn
{
    if (!_calibrateFPBtn) {
        _calibrateFPBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_calibrateFPBtn setTitle:@"CalibrateFP" forState:UIControlStateNormal];
        [_calibrateFPBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _calibrateFPBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_calibrateFPBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_calibrateFPBtn addTarget:self action:@selector(calibrateFPBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _calibrateFPBtn;
}

- (void)calibrateFPBtnAction
{
    [self printLog:@"ready to call PAEW_CalibrateFP"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_CalibrateFP(ppPAEWContext, devIdx);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_CalibrateFP returns failed: %@", [Utils errorCodeToString:iRtn]];
        } else {
            [self printLog:@"PAEW_CalibrateFP returns success"];
        }
        
    });
}

- (UIButton *)freeContextBtn
{
    if (!_freeContextBtn) {
        _freeContextBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_freeContextBtn setTitle:@"FreeContext" forState:UIControlStateNormal];
        [_freeContextBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _freeContextBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_freeContextBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_freeContextBtn addTarget:self action:@selector(freeContextBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _freeContextBtn;
}

- (void)freeContextBtnAction
{
    [self printLog:@"ready to call PAEW_FreeContext"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        
        iRtn = PAEW_FreeContext(ppPAEWContext);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_FreeContext returns failed: %@", [Utils errorCodeToString:iRtn]];
        } else {
            self.savedDevice = NULL;
            [self printLog:@"PAEW_FreeContext returns success"];
        }
    });
}

- (UIButton *)getAddressBtn
{
    if (!_getAddressBtn) {
        _getAddressBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_getAddressBtn setTitle:@"GetAddress" forState:UIControlStateNormal];
        [_getAddressBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _getAddressBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_getAddressBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_getAddressBtn addTarget:self action:@selector(getAddressBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _getAddressBtn;
}

- (UIButton *)getDeviceCheckCodeBtn
{
    if (!_getDeviceCheckCodeBtn) {
        _getDeviceCheckCodeBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_getDeviceCheckCodeBtn setTitle:@"DevChkCode" forState:UIControlStateNormal];
        [_getDeviceCheckCodeBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _getDeviceCheckCodeBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_getDeviceCheckCodeBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_getDeviceCheckCodeBtn addTarget:self action:@selector(getDeviceCheckCodeAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _getDeviceCheckCodeBtn;
}

- (void)getDeviceCheckCodeAction
{
    [self printLog:@"ready to call PAEW_GetTradeAddress"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char *pbCheckCode = NULL;
        size_t nAddressLen = 1024;
        
        iRtn = PAEW_GetDeviceCheckCode(ppPAEWContext, devIdx, pbCheckCode, &nAddressLen);
        if (iRtn == PAEW_RET_SUCCESS) {
            pbCheckCode = (unsigned char *)malloc(nAddressLen);
            memset(pbCheckCode, 0, nAddressLen);
            iRtn = PAEW_GetDeviceCheckCode(ppPAEWContext, 0, pbCheckCode, &nAddressLen);
            if (iRtn != PAEW_RET_SUCCESS) {
                
                [self printLog:@"PAEW_GetDeviceCheckCode returns failed: %@", [Utils errorCodeToString:iRtn]];
            } else {
                [self printLog:@"DeviceCheckCode is: %@", [Utils bytesToHexString:pbCheckCode length:nAddressLen]];
                [self printLog:@"PAEW_GetDeviceCheckCode returns success"];
            }
            if (pbCheckCode) {
                free(pbCheckCode);
            }
        } else {
            [self printLog:@"PAEW_GetDeviceCheckCode returns failed: %@", [Utils errorCodeToString:iRtn]];
        }
        
        
    });
}

- (void)getAddressBtnAction
{
    [self printLog:@"ready to call PAEW_GetTradeAddress"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char bAddress[1024] = {0};
        size_t nAddressLen = 1024;
        
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_ETH, puiDerivePathETH, sizeof(puiDerivePathETH)/sizeof(puiDerivePathETH[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_GetTradeAddress failed due to PAEW_DeriveTradeAddress on ETH returns : %@", [Utils errorCodeToString:iRtn]];
        } else {
            iRtn = PAEW_GetTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_ETH, bAddress, &nAddressLen);
            if (iRtn != PAEW_RET_SUCCESS) {
                [self printLog:@"PAEW_GetTradeAddress on ETH failed returns : %@", [Utils errorCodeToString:iRtn]];
            } else {
                //returned address is hex string only, so we should add '0x' at the begining manually
                [self printLog:@"PAEW_GetTradeAddress on ETH success, address is 0x%@", [NSString stringWithUTF8String:(char *)bAddress]];
            }
        }
        
        nAddressLen = 1024;
        memset(bAddress, 0, 1024);
        
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_EOS, puiDerivePathEOS, sizeof(puiDerivePathEOS)/sizeof(puiDerivePathEOS[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_GetTradeAddress failed due to PAEW_DeriveTradeAddress on EOS returns : %@", [Utils errorCodeToString:iRtn]];
        } else {
            iRtn = PAEW_GetTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_EOS, bAddress, &nAddressLen);
            if (iRtn != PAEW_RET_SUCCESS) {
                [self printLog:@"PAEW_GetTradeAddress on EOS failed returns : %@", [Utils errorCodeToString:iRtn]];
            } else {
                //EOS address format:  Address(ASCII) + '\0' + Signature(Hex)
                size_t addressLen = strlen(bAddress);
                NSString *signature = [Utils bytesToHexString:[NSData dataWithBytes:bAddress + addressLen + 1 length:nAddressLen - addressLen - 1] ];
                [self printLog:@"PAEW_GetTradeAddress on EOS success, address is %@, signature is: %@", [NSString stringWithUTF8String:(char *)bAddress], signature];
            }
        }
        
        nAddressLen = 1024;
        memset(bAddress, 0, 1024);
        
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_CYB, puiDerivePathCYB, sizeof(puiDerivePathEOS)/sizeof(puiDerivePathEOS[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_GetTradeAddress failed due to PAEW_DeriveTradeAddress on CYB returns : %@", [Utils errorCodeToString:iRtn]];
        } else {
            iRtn = PAEW_GetTradeAddress(ppPAEWContext, devIdx, PAEW_COIN_TYPE_CYB, bAddress, &nAddressLen);
            if (iRtn != PAEW_RET_SUCCESS) {
                [self printLog:@"PAEW_GetTradeAddress on CYB failed returns : %@", [Utils errorCodeToString:iRtn]];
            } else {
                [self printLog:@"PAEW_GetTradeAddress on CYB success, address is %@", [NSString stringWithUTF8String:(char *)bAddress]];
            }
        }
    });
}

- (UIButton *)importMNEBtn
{
    if (!_importMNEBtn) {
        _importMNEBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_importMNEBtn setTitle:@"ImportMNE" forState:UIControlStateNormal];
        [_importMNEBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _importMNEBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_importMNEBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_importMNEBtn addTarget:self action:@selector(importMNEBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _importMNEBtn;
}

- (void)importMNEBtnAction
{
    [self printLog:@"ready to call PAEW_ImportSeed"];
    NSString *number = @"mass dust captain baby mass dust captain baby mass dust captain baby mass dust captain baby mass electric";
    [self printLog:@"mnemonics to import are: %@", number];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        
        int iRtn = PAEW_ImportSeed(ppPAEWContext, devIdx, (const unsigned char *)[number UTF8String], number.length);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_ImportSeed returns failed: %@", [Utils errorCodeToString:iRtn]];
        } else {
            [self printLog:@"PAEW_ImportSeed returns success"];
        }
    });
    
}

- (UIButton *)EOSSignBtn
{
    if (!_EOSSignBtn) {
        _EOSSignBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_EOSSignBtn setTitle:@"EOSSign" forState:UIControlStateNormal];
        [_EOSSignBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _EOSSignBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_EOSSignBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_EOSSignBtn addTarget:self action:@selector(EOSSignBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _EOSSignBtn;
}

- (UIButton *)CYBSignBtn
{
    if (!_CYBSignBtn) {
        _CYBSignBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_CYBSignBtn setTitle:@"CYBSign" forState:UIControlStateNormal];
        [_CYBSignBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _CYBSignBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_CYBSignBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_CYBSignBtn addTarget:self action:@selector(CYBSignBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _CYBSignBtn;
}

- (void)CYBSignBtnAction
{
    [self printLog:@"ready to call PAEW_CYB_TXSign_Ex"];
    int rtn = [self getAuthType];
    if (rtn != PAEW_RET_SUCCESS) {
        [self printLog:@"user canceled PAEW_CYB_TXSign_Ex"];
        return;
    }
    if (self->nAuthType == PAEW_SIGN_AUTH_TYPE_PIN) {
        rtn = [self getPIN];
        if (rtn != PAEW_RET_SUCCESS) {
            [self printLog:@"user canceled PAEW_CYB_TXSign_Ex"];
            return;
        }
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char nCoinType = PAEW_COIN_TYPE_CYB;
        uint32_t puiDerivePath[] = {0, 0, 1, 0x00000080, 0x00000000, 0x00000000};
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, nCoinType, puiDerivePath, sizeof(puiDerivePath)/sizeof(puiDerivePath[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_CYB_TXSign_Ex failed due to PAEW_DeriveTradeAddress returns : %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        
        unsigned char transaction[] = {0x26,0xe9,
            0xbf,0x22,0x06,0xa1,
            0xd1,0x5c,0x7e,0x5b,
            0x01,0x00,
            0xe8,0x03,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x80,0xaf,0x02,
            0x80,0xaf,0x02,
            0x0a,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,
            0x00,
            0x01,0x04,
            0x0a,0x7a,0x68,0x61,0x6e,0x67,0x73,0x79,0x31,0x33,0x33,
            0x03,0x43,0x59,0x42,
            0x03,0x43,0x59,0x42,
            0x05,
            0x05,
            0x00};
        unsigned char *pbTXSig = (unsigned char *)malloc(1024);
        size_t pnTXSigLen = 1024;
        signCallbacks callBack;
        callBack.getAuthType = GetAuthType;
        callBack.getPIN = GetPin;
        callBack.putSignState = PutSignState;
        selfClass->lastSignState = PAEW_RET_SUCCESS;
        
        iRtn = PAEW_CYB_TXSign_Ex(ppPAEWContext, devIdx, transaction, sizeof(transaction), pbTXSig, &pnTXSigLen, &callBack, 0);
        if (iRtn) {
            [self printLog:@"PAEW_CYB_TXSign_Ex returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        [self printLog:@"CYB signature is: %@", [Utils bytesToHexString:[NSData dataWithBytes:pbTXSig length:pnTXSigLen]]];
        [self printLog:@"PAEW_CYB_TXSign_Ex returns success"];
    });
}

- (void)EOSSignBtnAction
{
    [self printLog:@"ready to call PAEW_EOS_TXSign_Ex"];
    int rtn = [self getAuthType];
    if (rtn != PAEW_RET_SUCCESS) {
        [self printLog:@"user canceled PAEW_EOS_TXSign_Ex"];
        return;
    }
    if (self->nAuthType == PAEW_SIGN_AUTH_TYPE_PIN) {
        rtn = [self getPIN];
        if (rtn != PAEW_RET_SUCCESS) {
            [self printLog:@"user canceled PAEW_EOS_TXSign_Ex"];
            return;
        }
    }

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char nCoinType = PAEW_COIN_TYPE_EOS;
        uint32_t puiDerivePath[] = {0, 0x8000002C, 0x800000c2, 0x80000000, 0x00000000, 0x00000000};
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, nCoinType, puiDerivePath, sizeof(puiDerivePath)/sizeof(puiDerivePath[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_EOS_TXSign_Ex failed due to PAEW_DeriveTradeAddress returns : %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        
        unsigned char transaction[] = {0x74, 0x09, 0x70, 0xd9, 0xff, 0x01, 0xb5, 0x04, 0x63, 0x2f, 0xed, 0xe1, 0xad, 0xc3, 0xdf, 0xe5, 0x59, 0x90, 0x41, 0x5e, 0x4f, 0xde, 0x01, 0xe1, 0xb8, 0xf3, 0x15, 0xf8, 0x13, 0x6f, 0x47, 0x6c, 0x14, 0xc2, 0x67, 0x5b, 0x01, 0x24, 0x5f, 0x70, 0x5d, 0xd7, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0xa6, 0x82, 0x34, 0x03, 0xea, 0x30, 0x55, 0x00, 0x00, 0x00, 0x57, 0x2d, 0x3c, 0xcd, 0xcd, 0x01, 0x20, 0x29, 0xc2, 0xca, 0x55, 0x7a, 0x73, 0x57, 0x00, 0x00, 0x00, 0x00, 0xa8, 0xed, 0x32, 0x32, 0x21, 0x20, 0x29, 0xc2, 0xca, 0x55, 0x7a, 0x73, 0x57, 0x90, 0x55, 0x8c, 0x86, 0x77, 0x95, 0x4c, 0x3c, 0x10, 0x27, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04, 0x45, 0x4f, 0x53, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        unsigned char *pbTXSig = (unsigned char *)malloc(1024);
        size_t pnTXSigLen = 1024;
        signCallbacks callBack;
        callBack.getAuthType = GetAuthType;
        callBack.getPIN = GetPin;
        callBack.putSignState = PutSignState;
        selfClass->lastSignState = PAEW_RET_SUCCESS;
        
        iRtn = PAEW_EOS_TXSign_Ex(ppPAEWContext, devIdx, transaction, sizeof(transaction), pbTXSig, &pnTXSigLen, &callBack, 0);
        if (iRtn) {
            [self printLog:@"PAEW_EOS_TXSign_Ex returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        [self printLog:@"EOS signature: %@" ,[[NSString alloc] initWithBytes:pbTXSig length:pnTXSigLen encoding:NSASCIIStringEncoding]];
        [self printLog:@"PAEW_EOS_TXSign_Ex returns success"];
    });
}

- (UIButton *)ETHSignBtn
{
    if (!_ETHSignBtn) {
        _ETHSignBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_ETHSignBtn setTitle:@"ETHSign" forState:UIControlStateNormal];
        [_ETHSignBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _ETHSignBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_ETHSignBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_ETHSignBtn addTarget:self action:@selector(ETHSignBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _ETHSignBtn;
}

- (void)ETHSignBtnAction
{
    [self printLog:@"ready to call PAEW_ETH_TXSign_Ex"];
    int rtn = [self getAuthType];
    if (rtn != PAEW_RET_SUCCESS) {
        [self printLog:@"user canceled PAEW_ETH_TXSign_Ex"];
        return;
    }
    if (self->nAuthType == PAEW_SIGN_AUTH_TYPE_PIN) {
        rtn = [self getPIN];
        if (rtn != PAEW_RET_SUCCESS) {
            [self printLog:@"user canceled PAEW_ETH_TXSign_Ex"];
            return;
        }
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char nCoinType = PAEW_COIN_TYPE_ETH;
        uint32_t puiDerivePath[] = {0, 0x8000002c, 0x8000003c, 0x80000000, 0x00000000, 0x00000000};
        iRtn = PAEW_DeriveTradeAddress(ppPAEWContext, devIdx, nCoinType, puiDerivePath, sizeof(puiDerivePath)/sizeof(puiDerivePath[0]));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_ETH_TXSign_Ex failed due to PAEW_DeriveTradeAddress returns : %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        
        unsigned char transaction[] = { 0xec,  0x09,  0x85,  0x04,  0xa8,  0x17,  0xc8,  0x00,  0x82,  0x52,  0x08,  0x94,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x35,  0x88,  0x0d,  0xe0,  0xb6,  0xb3,  0xa7,  0x64,  0x00,  0x00,  0x80,  0x01,  0x80,  0x80};
        unsigned char *pbTXSig = (unsigned char *)malloc(1024);
        size_t pnTXSigLen = 1024;
        signCallbacks callBack;
        callBack.getAuthType = GetAuthType;
        callBack.getPIN = GetPin;
        callBack.putSignState = PutSignState;
        selfClass->lastSignState = PAEW_RET_SUCCESS;
        iRtn = PAEW_ETH_TXSign_Ex(ppPAEWContext, devIdx, transaction, sizeof(transaction), pbTXSig,  &pnTXSigLen, &callBack, 0);
        if (iRtn) {
            [self printLog:@"PAEW_ETH_TXSign_Ex returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        [self printLog:@"ETH signature is: %@", [Utils bytesToHexString:[NSData dataWithBytes:pbTXSig length:pnTXSigLen]]];
        [self printLog:@"PAEW_ETH_TXSign_Ex returns success"];
    });
}

- (UIButton *)genSeedBtn
{
    if (!_genSeedBtn) {
        _genSeedBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_genSeedBtn setTitle:@"GenSeed" forState:UIControlStateNormal];
        [_genSeedBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _genSeedBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_genSeedBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_genSeedBtn addTarget:self action:@selector(genSeedBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _genSeedBtn;
}

- (void)genSeedBtnAction
{
    [self printLog:@"ready to call PAEW_GenerateSeed_GetMnes"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        unsigned char nSeedLen = 32;
        unsigned char pbMneWord[PAEW_MNE_MAX_LEN] = {0};
        size_t pnMneWordLen = sizeof(pbMneWord);
        size_t  pnCheckIndex[PAEW_MNE_INDEX_MAX_COUNT] = { 0 };
        size_t pnCheckIndexCount = PAEW_MNE_INDEX_MAX_COUNT;
        iRtn = PAEW_GenerateSeed_GetMnes(ppPAEWContext, devIdx, nSeedLen, pbMneWord, &pnMneWordLen, pnCheckIndex, &pnCheckIndexCount);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_GenerateSeed_GetMnes returns failed: %@", [Utils errorCodeToString:iRtn]];
            return;
        }
        [self printLog:@"PAEW_GenerateSeed_GetMnes returns success"];
        [self printLog:@"seed generated, mnemonics are: %s", pbMneWord];
        //NSData *pbMneWordData = [NSData dataWithBytes:pbMneWord length:pnMneWordLen];
        //NSString *pbMneWordStr = [[NSString alloc] initWithData:pbMneWordData encoding:NSASCIIStringEncoding];
        NSMutableString *tmpStr = [NSMutableString new];
        for (int i = 0; i < pnCheckIndexCount; i++) {
            [tmpStr appendString:[NSString stringWithFormat: i == 0 ? @"word%lu" : @", word%lu", pnCheckIndex[i] + 1]];
        }
        [self printLog:@"please input the words exactly as this sequence with ONE WHITESPACE between each words: %@", tmpStr];
        dispatch_async(dispatch_get_main_queue(), ^{
            self->_inputView =[ToolInputView toolInputViewWithCallback:^(NSString *number) {
                self->_inputView = nil;
                if (number.length == 0) {
                    return  ;
                }
                number = [number stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceCharacterSet];
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
                    int devIdx = 0;
                    int iRtn = PAEW_GenerateSeed_CheckMnes(ppPAEWContext, devIdx, (unsigned char *)[number UTF8String], number.length);
                    if (iRtn != PAEW_RET_SUCCESS) {
                        [self printLog:@"PAEW_GenerateSeed_CheckMnes returns failed: %@", [Utils errorCodeToString:iRtn]];
                        return;
                    }
                    [self printLog:@"PAEW_GenerateSeed_CheckMnes returns success"];
                });
                
            }];
        });
    });
}

- (UIButton *)formatBtn
{
    if (!_formatBtn) {
        _formatBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_formatBtn setTitle:@"Format" forState:UIControlStateNormal];
        [_formatBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _formatBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_formatBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_formatBtn addTarget:self action:@selector(formatBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _formatBtn;
}

- (void)formatBtnAction
{
    [self printLog:@"ready to call PAEW_Format"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        
        iRtn = PAEW_Format(ppPAEWContext, devIdx);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_Format returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        } else {
            [self printLog:@"PAEW_Format returns success"];
        }
    });
    
}

- (UIButton *)deleteFPBtn
{
    if (!_deleteFPBtn) {
        _deleteFPBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_deleteFPBtn setTitle:@"DeleteFP" forState:UIControlStateNormal];
        [_deleteFPBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _deleteFPBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_deleteFPBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_deleteFPBtn addTarget:self action:@selector(deleteFPBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _deleteFPBtn;
}

- (void)deleteFPBtnAction
{
    [self printLog:@"ready to call PAEW_DeleteFP"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        FingerPrintID   *localFPList = 0;
        int nFPCount = 0;
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_DeleteFP(ppPAEWContext, devIdx, localFPList, nFPCount);
        
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_DeleteFP returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        } else {
            [self printLog:@"PAEW_DeleteFP returns success"];
        }
    });
}

- (UIButton *)verifyFPBtn
{
    if (!_verifyFPBtn) {
        _verifyFPBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_verifyFPBtn setTitle:@"VerifyFP" forState:UIControlStateNormal];
        [_verifyFPBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _verifyFPBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_verifyFPBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_verifyFPBtn addTarget:self action:@selector(verifyFPBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _verifyFPBtn;
}

- (void)verifyFPBtnAction
{
    [self printLog:@"ready to call PAEW_VerifyFP"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_VerifyFP(ppPAEWContext, devIdx);
        if (iRtn != PAEW_RET_SUCCESS) {
             [self printLog:@"PAEW_VerifyFP returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        
        int lastRtn = PAEW_RET_SUCCESS;
        do {
            iRtn = PAEW_GetFPState(ppPAEWContext, devIdx);
            if (lastRtn != iRtn) {
                [self printLog:[Utils errorCodeToString:iRtn]];
                lastRtn = iRtn;
            }
        } while (iRtn == PAEW_RET_DEV_WAITING);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_VerifyFP failed due to PAEW_GetFPState returns: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        
        size_t          nFPListCount = 1;
        FingerPrintID   *fpIDList = (FingerPrintID *)malloc(sizeof(FingerPrintID) * nFPListCount);
        memset(fpIDList, 0, sizeof(sizeof(FingerPrintID) * nFPListCount));
        iRtn = PAEW_GetVerifyFPList(ppPAEWContext, devIdx, fpIDList, &nFPListCount);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_VerifyFP failed due to PAEW_GetVerifyFPList returns: %@", [Utils errorCodeToString:iRtn]];
        } else {
            if (nFPListCount != 1) {
                [self printLog:@"PAEW_VerifyFP successe but nFPListCount is: %d", nFPListCount];
            } else {
                [self printLog:@"PAEW_VerifyFP successe with No.%u fingerprint verified", fpIDList[0].data[0]];
            }
        }
        free(fpIDList);
    });
    
}


- (void)enrollFPBtnAction
{
    [self printLog:@"ready to call PAEW_EnrollFP, during the whole enroll process, you can tap Abort button to abort at any time"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int startEnrollS = PAEW_EnrollFP(ppPAEWContext, devIdx);
        if (startEnrollS != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_EnrollFP returns failed: %@", [Utils errorCodeToString:startEnrollS]];
            return ;
        }
        
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        int lastRtn = PAEW_RET_SUCCESS;
        do {
            
            iRtn = PAEW_GetFPState(ppPAEWContext, devIdx);
            if (lastRtn != iRtn) {
                [self printLog:@"fpstate:%@", [Utils errorCodeToString:iRtn]];
                lastRtn = iRtn;
            }
            if (self.abortBtnState) {
                [self.abortCondition lock];
                !self.abortHandelBlock ? : self.abortHandelBlock(YES);
                [self.abortCondition wait];
                [self.abortCondition unlock];
                self.abortBtnState = NO;
            }
        } while ((iRtn == PAEW_RET_DEV_WAITING) || (iRtn == PAEW_RET_DEV_FP_GOOG_FINGER) || (iRtn == PAEW_RET_DEV_FP_REDUNDANT) || (iRtn == PAEW_RET_DEV_FP_BAD_IMAGE) || (iRtn == PAEW_RET_DEV_FP_NO_FINGER) || (iRtn == PAEW_RET_DEV_FP_NOT_FULL_FINGER));
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_EnrollFP failed due to PAEW_GetFPState returns: %@", [Utils errorCodeToString:iRtn]];
            return ;
        }
        [self printLog:@"PAEW_EnrollFP returns success"];
    });
    
}

- (UIButton *)enrollFPBtn
{
    if (!_enrollFPBtn) {
        _enrollFPBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_enrollFPBtn setTitle:@"EnrollFP" forState:UIControlStateNormal];
        [_enrollFPBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _enrollFPBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_enrollFPBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_enrollFPBtn addTarget:self action:@selector(enrollFPBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _enrollFPBtn;
}

- (UIButton *)getFPListBtn
{
    if (!_getFPListBtn) {
        _getFPListBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_getFPListBtn setTitle:@"GetFPList" forState:UIControlStateNormal];
        [_getFPListBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _getFPListBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_getFPListBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_getFPListBtn addTarget:self action:@selector(getFPListBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _getFPListBtn;
}

- (void)getFPListBtnAction
{
    [self printLog:@"ready to call PAEW_GetFPList"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        size_t nListLen = 0;
        FingerPrintID *pFPList = NULL;
        int iRtn = PAEW_RET_UNKNOWN_FAIL;
        iRtn = PAEW_GetFPList(ppPAEWContext, devIdx, 0, &nListLen);
        if (iRtn != PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_GetFPList returns failed: %@", [Utils errorCodeToString:iRtn]];
            return ;
        } else if (nListLen == 0) {
            [self printLog:@"0 fingerprint exists"];
            [self printLog:@"PAEW_GetFPList returns success"];
            return ;
        } else {
            pFPList = (FingerPrintID *)malloc(sizeof(FingerPrintID) * nListLen);
            iRtn = PAEW_GetFPList(ppPAEWContext, devIdx, pFPList, &nListLen);
            if (iRtn == PAEW_RET_SUCCESS) {
                NSMutableString *strIndex = [NSMutableString new];
                for (int i = 0; i < nListLen; i++) {
                    [strIndex appendFormat:i == 0 ? @"No.%u" : @", No.%u", pFPList[i].data[0]];
                }
                [self printLog:(nListLen <= 1) ? @"%zu fingerprint exists at index: %@" : @"%zu fingerprints exist at index: %@", nListLen, strIndex];
                [self printLog:@"PAEW_GetFPList returns success"];
            } else {
                [self printLog:@"PAEW_GetFPList returns failed: %@", [Utils errorCodeToString:iRtn]];
            }
            free(pFPList);
        }
    });
}

- (UIButton *)changePinBtn
{
    if (!_changePinBtn) {
        _changePinBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_changePinBtn setTitle:@"ChangePin" forState:UIControlStateNormal];
        [_changePinBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _changePinBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_changePinBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_changePinBtn addTarget:self action:@selector(changePinBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _changePinBtn;
}

- (void)changePinBtnAction
{
    NSString *oldpin = nil;
    NSString *newpin = nil;
    NSString *newpinConfirm = nil;
    oldpin = [TextFieldViewAlert doModal:self
                                   title:@"Input current PIN"
                                 message:@"Please input your current PIN"
                              isPassword:YES
                       minLengthRequired:6
                            keyboardType:UIKeyboardTypeNumberPad];
    if (!oldpin) {
        return;
    }
    newpin = [TextFieldViewAlert doModal:self
                                title:@"Input new PIN"
                              message:@"Please input your new PIN"
                           isPassword:YES
                    minLengthRequired:6
                         keyboardType:UIKeyboardTypeNumberPad];
    if (!newpin) {
        return;
    }
    newpinConfirm = [TextFieldViewAlert doModal:self
                                       title:@"Input new PIN again"
                                     message:@"Please input your new PIN again"
                                  isPassword:YES
                           minLengthRequired:6
                                keyboardType:UIKeyboardTypeNumberPad];
    if (!newpin || ![newpin isEqualToString:newpinConfirm]) {
        [self printLog:@"new pin not match"];
        return;
    }
    
    [self printLog:@"ready to call PAEW_ChangePIN_Input"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int initState = PAEW_ChangePIN_Input(ppPAEWContext, devIdx, [oldpin UTF8String], [newpin UTF8String]);
        if (initState == PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_ChangePIN_Input returns success"];
        } else {
            [self printLog:@"PAEW_ChangePIN_Input returns failed: %@", [Utils errorCodeToString:initState]];
        }
    });
}

- (UIButton *)verifyPinBtn
{
    if (!_verifyPinBtn) {
        _verifyPinBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_verifyPinBtn setTitle:@"VerifyPin" forState:UIControlStateNormal];
        [_verifyPinBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _verifyPinBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_verifyPinBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_verifyPinBtn addTarget:self action:@selector(verifyPinBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _verifyPinBtn;
}

- (void)verifyPinBtnAction
{
    NSString *pin = nil;
    pin = [TextFieldViewAlert doModal:self
                                title:@"Input PIN"
                              message:@"Please input your PIN"
                           isPassword:YES
                    minLengthRequired:6
                         keyboardType:UIKeyboardTypeNumberPad];
    if (!pin) {
        return;
    }
    
    [self printLog:@"ready to call PAEW_VerifyPIN"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        int initState = PAEW_VerifyPIN(ppPAEWContext, devIdx, [pin UTF8String]);
        if (initState == PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_VerifyPIN returns success"];
        } else {
            [self printLog:@"PAEW_VerifyPIN returns failed: %@", [Utils errorCodeToString:initState]];
        }
    });
}

- (UIButton *)initiPinBtn
{
    if (!_initiPinBtn) {
        _initiPinBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_initiPinBtn setTitle:@"InitiPin" forState:UIControlStateNormal];
        [_initiPinBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _initiPinBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_initiPinBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_initiPinBtn addTarget:self action:@selector(initiPinBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _initiPinBtn;
}

- (void)initiPinBtnAction
{
    NSString *pin = nil;
    NSString *pinConfirm = nil;
    pin = [TextFieldViewAlert doModal:self
                                title:@"Input new PIN"
                              message:@"Please input your new PIN"
                           isPassword:YES
                    minLengthRequired:6
                         keyboardType:UIKeyboardTypeNumberPad];
    if (!pin) {
        return;
    }
    pinConfirm = [TextFieldViewAlert doModal:self
                                       title:@"Input new PIN again"
                                     message:@"Please input your new PIN again"
                                  isPassword:YES
                           minLengthRequired:6
                                keyboardType:UIKeyboardTypeNumberPad];
    if (!pin || ![pin isEqualToString:pinConfirm]) {
        [self printLog:@"pin not match"];
        return;
    }
    
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devIdx = 0;
        void *ppPAEWContext = (void*)self.savedDevice;
        [self printLog:@"ready to call PAEW_InitPIN"];
        int initState = PAEW_InitPIN(ppPAEWContext, devIdx, [pin UTF8String]);
        if (initState == PAEW_RET_SUCCESS) {
            [self printLog:@"PAEW_InitPIN returns success"];
        } else {
            [self printLog:@"PAEW_InitPIN returns failed: %@", [Utils errorCodeToString:initState]];
        }
    });
}

- (UIButton *)getDevInfoBtn
{
    if (!_getDevInfoBtn) {
        _getDevInfoBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_getDevInfoBtn setTitle:@"GetDevInfo" forState:UIControlStateNormal];
        [_getDevInfoBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _getDevInfoBtn.titleLabel.font = [UIFont systemFontOfSize:15.0 weight:UIFontWeightMedium];
        [_getDevInfoBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_getDevInfoBtn addTarget:self action:@selector(getDevInfoBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _getDevInfoBtn;
}

- (void)getDevInfoBtnAction
{
    if (self.savedDevice) {
        //        继续执行
    } else {
        return;
    }
    __block  size_t            i = 0;
    __block PAEW_DevInfo    devInfo;
    
    __block uint32_t        nDevInfoType = 0;
    
    nDevInfoType = PAEW_DEV_INFOTYPE_COS_TYPE | PAEW_DEV_INFOTYPE_COS_VERSION | PAEW_DEV_INFOTYPE_SN | PAEW_DEV_INFOTYPE_CHAIN_TYPE | PAEW_DEV_INFOTYPE_PIN_STATE | PAEW_DEV_INFOTYPE_LIFECYCLE;
    [self printLog:@"ready to call PAEW_GetDevInfo"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        void *ppPAEWContext = (void*)self.savedDevice;
        int devInfoState = PAEW_GetDevInfo(ppPAEWContext, i, nDevInfoType, &devInfo);
        dispatch_async(dispatch_get_main_queue(), ^{
            if (devInfoState == PAEW_RET_SUCCESS) {
                [self printLog:@"ucPINState: %02X", devInfo.ucPINState];
                [self printLog:@"ucCOSType: %02X", devInfo.ucCOSType];
                for (int i = 0; i < PAEW_DEV_INFO_SN_LEN; i++) {
                    if (devInfo.pbSerialNumber[i] == 0xFF) {
                        devInfo.pbSerialNumber[i] = 0;
                    }
                }
                [self printLog:@"SerialNumber: %@", [NSString stringWithUTF8String:(char *)devInfo.pbSerialNumber]];
                [self printLog:@"PAEW_GetDevInfo returns Success"];
            } else {
                [self printLog:@"PAEW_GetDevInfo returns failed: %@", [Utils errorCodeToString:devInfoState]];
            }
        });
    });
}

- (UITextView *)in_outTextView
{
    if (!_in_outTextView) {
        _in_outTextView = [[UITextView alloc] init];
        _in_outTextView.font = [UIFont fontWithName:@"Arial" size:12.5f];
        _in_outTextView.textColor = [UIColor colorWithRed:51/255.0f green:51/255.0f blue:51/255.0f alpha:1.0f];
        _in_outTextView.backgroundColor = [UIColor whiteColor];
        _in_outTextView.textAlignment = NSTextAlignmentLeft;
        _in_outTextView.autocorrectionType = UITextAutocorrectionTypeNo;
        _in_outTextView.layer.borderColor = [UIColor greenColor].CGColor;
        _in_outTextView.layer.borderWidth = 1;
        _in_outTextView.layer.cornerRadius =5;
        _in_outTextView.autocapitalizationType = UITextAutocapitalizationTypeNone;
        _in_outTextView.keyboardType = UIKeyboardTypeASCIICapable;
        _in_outTextView.returnKeyType = UIReturnKeyDefault;
        _in_outTextView.scrollEnabled = YES;
        _in_outTextView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
        _in_outTextView.editable = false;
        
    }
    return _in_outTextView;
}



- (void)showMessageWithInt:(int)retValue
{
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString* strResult = [Utils errorCodeToString:retValue];
        [LCProgressHUD showMessage:strResult];
    });
}

- (NSCondition *)abortCondition
{
    if (!_abortCondition) {
        _abortCondition = [[NSCondition alloc] init];
    }
    return _abortCondition;
}

- (int)getAuthType
{
    int sel = [PickerViewAlert doModal:selfClass dataSouce:@[@"fingerprint", @"PIN"]];
    self->authTypeCached = YES;
    int rtn = PAEW_RET_DEV_OP_CANCEL;
    if (sel >= 0) {
        switch (sel) {
            case 0:
                selfClass->nAuthType = PAEW_SIGN_AUTH_TYPE_FP;
                rtn = PAEW_RET_SUCCESS;
                break;
            case 1:
                selfClass->nAuthType = PAEW_SIGN_AUTH_TYPE_PIN;
                rtn = PAEW_RET_SUCCESS;
                break;
            default:
                selfClass->nAuthType = -1;
                rtn = PAEW_RET_DEV_OP_CANCEL;
        }
    }
    self->authTypeResult = rtn;
    return rtn;
}

-(int)getPIN
{
    NSString *pin = [TextFieldViewAlert doModal:selfClass
                                          title:@"Input PIN:"
                                        message:@"Please input your pin"
                                     isPassword:YES
                              minLengthRequired:6
                                   keyboardType:UIKeyboardTypeNumberPad];
    self->pinCached = YES;
    int rtn = PAEW_RET_DEV_OP_CANCEL;
    if (pin) {
        self->pin = pin;
        rtn = PAEW_RET_SUCCESS;
    }
    self->pinResult = rtn;
    return rtn;
    
    
}

- (void)printLog:(NSString *)format, ...
{
    logCounter++;
    va_list args;
    va_start(args, format);
    NSString *str = [[NSString alloc] initWithFormat:format arguments:args];
    va_end(args);
    if ([NSThread isMainThread]) {
        
        self.in_outTextView.text = [NSString stringWithFormat:@"%@[%zu]%@\n", self.in_outTextView.text, logCounter, str];
        [self.in_outTextView scrollRangeToVisible:NSMakeRange(self.in_outTextView.text.length, 1)];
    } else {
        dispatch_async(dispatch_get_main_queue(), ^{
            self.in_outTextView.text = [NSString stringWithFormat:@"%@[%zu]%@\n", self.in_outTextView.text, self->logCounter, str];
            [self.in_outTextView scrollRangeToVisible:NSMakeRange(self.in_outTextView.text.length, 1)];
        });
    }
}

/*
 #pragma mark - Navigation
 
 // In a storyboard-based application, you will often want to do a little preparation before navigation
 - (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
 // Get the new view controller using [segue destinationViewController].
 // Pass the selected object to the new view controller.
 }
 */

@end
