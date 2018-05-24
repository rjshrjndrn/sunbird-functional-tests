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
 * @author arvind.
 */
public class AfterTestSuiteCleanUp  extends TestRunnerAfterSuiteSupport {

  CleanUpUtil cleanUpUtil = CleanUpUtil.getInstance();
  private final String PACKAGE_TO_SCAN_FOR_CLEANUP = "org.sunbird.integration.test";

  @Override
  public void afterSuite(TestRunner testRunner) {
    System.out.println("AFTER TEST SUITE STARTED");
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

    System.out.println("AFTER CLEAN UP COMPLETED .");

  }

  private void performMethodAnnotationChecking(List<Class> clazzList) {

    for(Class clazz : clazzList){
      for (Method method : clazz.getMethods())
      {
        if (method.isAnnotationPresent(CleanUp.class))
        {
          System.out.println("class name : "+clazz.getName()+" method name "+method.getName());
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
