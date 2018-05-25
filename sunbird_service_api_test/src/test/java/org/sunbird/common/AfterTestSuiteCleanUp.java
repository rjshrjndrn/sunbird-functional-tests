package org.sunbird.common;

import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.dsl.runner.TestRunnerAfterSuiteSupport;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.List;
import org.sunbird.common.annotation.CleanUp;
import org.sunbird.common.util.CleanUpUtil;

/**
 * Class to provide the functionality for after test suite clean
 * by calling the clean up methods.
 * @author arvind.
 */
public class AfterTestSuiteCleanUp  extends TestRunnerAfterSuiteSupport {

  CleanUpUtil cleanUpUtil = CleanUpUtil.getInstance();
  private final String PACKAGE_TO_SCAN_FOR_CLEANUP = "org.sunbird.integration.test";

  @Override
  /**
   * Method will scan the specified package and identify
   * all the classes eligible for clean and perform
   * after test suite tasks by calling the clean up method.
   */
  public void afterSuite(TestRunner testRunner) {

    List<Class> clazzList = null;

    try {
      clazzList = cleanUpUtil.getClasses(PACKAGE_TO_SCAN_FOR_CLEANUP);
      performMethodAnnotationChecking(clazzList);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }

  }

  private void performMethodAnnotationChecking(List<Class> clazzList) {

    for(Class clazz : clazzList){
      for (Method method : clazz.getMethods())
      {
        if (method.isAnnotationPresent(CleanUp.class))
        {
          try {
            method.invoke(null);
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          } catch (Exception e){
            e.printStackTrace();
          }
        }
      }
    }
  }
}
