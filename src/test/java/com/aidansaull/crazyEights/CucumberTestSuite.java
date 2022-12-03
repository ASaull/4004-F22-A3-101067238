package com.aidansaull.crazyEights;

import org.junit.platform.suite.api.SelectFile;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

@Suite
@SelectFile("src/test/resources/features/Test.feature")
public class CucumberTestSuite
{
}
