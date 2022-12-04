package com.aidansaull.crazyEights;

//import org.junit.runner.JUnitCore;
//import org.junit.runner.Result;
//import org.junit.runner.notification.Failure;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@SelectClasses( {GameUnitTest.class})
@Suite
public class TestSuite {
}