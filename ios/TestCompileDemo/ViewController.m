//
//  ViewController.m
//  TestCompileDemo
//
//  Created by extropies on 2018/8/13.
//  Copyright © 2018年 extropies. All rights reserved.
//

#import "ViewController.h"
#import "ToolCell.h"
#import "VCCellModel.h"
#import "PA_EWallet.h"
#import "Utils.h"

#import "Test_C_EWallet_ViewController.h"

typedef void(^pProcCallback)(char *);

typedef void(^pCallbackParam)(char *);

@interface ViewController () <UITableViewDelegate, UITableViewDataSource, UITextFieldDelegate>

@property (nonatomic, strong) UIButton *StartScanBtn;

@property (nonatomic, strong) UITextField *devNameField;

@property (nonatomic, strong) UITableView *devTabView;

@property (nonatomic, strong) NSMutableArray *DevdataArray;

@property (nonatomic, copy) pCallbackParam pCallbackParam;

@property (nonatomic, copy) pProcCallback pProcCallback;


@end

@implementation ViewController

static ViewController *selfClass =nil;

int BatteryCallback(const int nBatterySource, const int nBatteryState)
{
    NSLog(@"current battery source is: %d, current battery state is: 0x%X", nBatterySource, nBatteryState);
    return PAEW_RET_SUCCESS;
}
int EnumCallback(const char *szDevName, int nRSSI, int nState)
{
    VCCellModel *model = [[VCCellModel alloc] init];
    model.peripheralName = [NSString stringWithUTF8String:szDevName];
    model.RSSI = nRSSI;
    model.state = nState;
    [selfClass.DevdataArray addObject:model];
    [selfClass.devTabView reloadData];
    return PAEW_RET_SUCCESS;
}
int DisconnectedCallback(const int status, const char *description)
{
    NSLog(@"device has disconnected already, status code is: %d, detail is: %s", status, description);
    return PAEW_RET_SUCCESS;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    selfClass = self;
    self.edgesForExtendedLayout = UIRectEdgeNone;
    self.extendedLayoutIncludesOpaqueBars = NO;
    self.modalPresentationCapturesStatusBarAppearance = NO;
    
    [self addSubViewAfterVDLoad];
}

- (void)addSubViewAfterVDLoad
{
    [self.view addSubview:self.StartScanBtn];
    [self.StartScanBtn mas_makeConstraints:^(MASConstraintMaker *make) {
        /*if (@available(iOS 11.0, *)) {
        make.bottom.mas_equalTo(self.view.mas_safeAreaLayoutGuideBottom);
        } else {
            // Fallback on earlier versions
            make.bottom.mas_equalTo(self.view.mas_bottom).offset(-30);
        }*/
        make.bottom.mas_equalTo(self.view.mas_bottom).offset(-30);
        make.width.mas_equalTo(120);
        make.height.mas_equalTo(50);
        make.centerX.mas_equalTo(self.view.mas_centerX);
    }];
    
    [self.view addSubview:self.devNameField];
    [self.devNameField mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.view.mas_top).offset(20);
        make.left.mas_equalTo(self.view.mas_left).offset(0);
        make.right.mas_equalTo(self.view.mas_right).offset(0);
        make.height.mas_equalTo(40);
    }];
    
    [self.view addSubview:self.devTabView];
    [self.devTabView mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.mas_equalTo(self.devNameField.mas_bottom).offset(20);
        make.width.mas_equalTo(self.view.mas_width);
        make.right.mas_equalTo(self.view.mas_right);
        make.bottom.mas_equalTo(self.StartScanBtn.mas_top).offset(-10);
    }];
    
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [ToolCell cellHeight];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.DevdataArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ToolCell *cell = [tableView dequeueReusableCellWithIdentifier:NSStringFromClass([ToolCell class]) forIndexPath:indexPath];
    cell.peripheral = self.DevdataArray[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    VCCellModel *model = self.DevdataArray[indexPath.row];
    [self connectDeviceAction:model.peripheralName];
}

void *savedDevH;//device handle

- (void)testAllIPA
{
    
}

- (void)connectDeviceAction:(NSString *)devNameID
{
    char *szDeviceName = (char *)[devNameID UTF8String];
    __block ConnectContext additional = {0};
    additional.timeout = 5;
    additional.batteryCallBack = BatteryCallback;
    additional.disconnectedCallback = DisconnectedCallback;
    __block void *ppPAEWContext = 0;
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int connectDev = PAEW_InitContextWithDevNameAndDevContext(&ppPAEWContext, szDeviceName, PAEW_DEV_TYPE_BT, &additional, sizeof(additional), 0x00, 0x00);
        NSLog(@"-------connect returns: 0x%X", connectDev);
        if (ppPAEWContext) {
            savedDevH = ppPAEWContext;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            if (connectDev == PAEW_RET_SUCCESS) {
                [LCProgressHUD showSuccess:@"Connect Success"];
                Test_C_EWallet_ViewController *testC = [[Test_C_EWallet_ViewController alloc] init];
                uint64_t pt = (uint64_t)savedDevH;
                testC.savedDevice = pt;
                [self.navigationController pushViewController:testC animated:YES];
            } else {
                [LCProgressHUD showFailure:@"Fail to Connect"];
            }
        });
        
        NSLog(@"connectDev----0x%X", connectDev);
    });
}

- (void)StartScanBtnAction
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.DevdataArray removeAllObjects];
        [self.devTabView reloadData];
    });
    __block unsigned char nDeviceType = PAEW_DEV_TYPE_BT;
    __block char *szDeviceNames = (char *)malloc(512*16);
    __block size_t nDeviceNameLen = 512*16;
    __block size_t nDevCount = 0;
    __block EnumContext DevContext = {0};
    DevContext.timeout = 2;//scanning may found nothing if timeout is lower than 2 seconds. So the suggested timeout value should be larger than 2
    DevContext.enumCallBack = EnumCallback;
    NSString *devName = self.devNameField.text;
    strcpy(DevContext.searchName, [[devName stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceCharacterSet] UTF8String]);
    [LCProgressHUD showLoading:@"PAEW_GetDeviceListWithDevContext started"];
    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        int devInfoState = PAEW_GetDeviceListWithDevContext(nDeviceType, szDeviceNames, &nDeviceNameLen, &nDevCount, &DevContext, sizeof(DevContext));
        dispatch_async(dispatch_get_main_queue(), ^{
            [LCProgressHUD hide];
            if (devInfoState == 0) {
                //you can process device list either here or EnumCallback
                
                /*NSData *data = [NSData dataWithBytes:szDeviceNames length:pnDeviceNameLen];
                float fLen = pnDeviceNameLen / pnDevCount;
                int oneNameLen = floorf(fLen);
                for (int i = 0; i < pnDevCount; i++) {
                    NSData * temData = [data subdataWithRange:NSMakeRange(i * oneNameLen, oneNameLen - 1)];
                    NSString *temStr = [[NSString alloc] initWithData:temData encoding:NSUTF8StringEncoding];
                    VCCellModel *model = [[VCCellModel alloc] init];
                    model.peripheralName = temStr;
                    [self.DevdataArray addObject:model];
                    [self.devTabView reloadData];
                }*/
                [LCProgressHUD showSuccess:@"PAEW_GetDeviceListWithDevContext Success"];
            } else {
                NSString *str = [NSString stringWithFormat:@"PAEW_GetDeviceListWithDevContext returns failed: %@", [Utils errorCodeToString:devInfoState]];
                [LCProgressHUD showFailure:str];
            }
            free(szDeviceNames);
        });
        NSLog(@"%d", devInfoState);
    });
}

- (UIButton *)StartScanBtn
{
    if (!_StartScanBtn) {
        _StartScanBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        [_StartScanBtn setTitle:@"StartScan" forState:UIControlStateNormal];
        [_StartScanBtn setTitleColor:[UIColor blueColor] forState:UIControlStateNormal];
        _StartScanBtn.titleLabel.font = [UIFont systemFontOfSize:20.0 weight:UIFontWeightMedium];
        [_StartScanBtn setBackgroundColor:[UIColor lightGrayColor]];
        [_StartScanBtn addTarget:self action:@selector(StartScanBtnAction) forControlEvents:UIControlEventTouchUpInside];
    }
    return _StartScanBtn;
}

- (UITextField *)devNameField
{
    if (!_devNameField) {
        _devNameField = [UITextField new];
        _devNameField.placeholder = @"Please input device name";
        _devNameField.clearButtonMode = UITextFieldViewModeAlways;
        _devNameField.returnKeyType = UIReturnKeyDone;
        _devNameField.text = @"WOOKONG BIO";
        _devNameField.layer.borderColor = [UIColor greenColor].CGColor;
        _devNameField.layer.borderWidth = 1;
        _devNameField.layer.cornerRadius =5;
    }
    return _devNameField;
}

- (UITableView *)devTabView
{
    if (!_devTabView) {
        _devTabView = [[UITableView alloc] initWithFrame:self.view.bounds style:UITableViewStylePlain];
        _devTabView.delegate = self;
        _devTabView.dataSource = self;
        _devTabView.backgroundColor = [UIColor groupTableViewBackgroundColor];
        [_devTabView registerNib:[UINib nibWithNibName:NSStringFromClass([ToolCell class]) bundle:nil] forCellReuseIdentifier:NSStringFromClass([ToolCell class])];
    }
    return _devTabView;
}

- (NSMutableArray *)DevdataArray
{
    if (nil == _DevdataArray) {
        _DevdataArray  =[[NSMutableArray alloc] init];
    }
    return _DevdataArray ;
}

@end
