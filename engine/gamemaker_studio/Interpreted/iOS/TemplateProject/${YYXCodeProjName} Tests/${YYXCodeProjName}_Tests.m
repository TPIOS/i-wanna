//
//  BitwiseOperatorIssue_Tests.m
//  BitwiseOperatorIssue Tests
//
//  Created by Russell Kay on 28/11/2014.
//  Copyright (c) 2014 YoYo Games Ltd. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <XCTest/XCTest.h>

@interface ${YYXCodeProjName}_Tests : XCTestCase

@end

@implementation ${YYXCodeProjName}_Tests

- (void)setUp {
    [super setUp];
    // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
    // Put teardown code here. This method is called after the invocation of each test method in the class.
    [super tearDown];
}

- (void)testExample {
    
    XCTestExpectation *appExpectation = [self expectationWithDescription:@"app is working"];
    
    [self waitForExpectationsWithTimeout:60*60*24*365 handler:^(NSError *error) {
        NSLog(@"Goodbye cruel world");
    }];
    
    
    // This is an example of a functional test case.
    XCTAssert(YES, @"Pass");
}

@end
