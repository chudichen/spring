package com.michael.demo;

import com.michael.context.ApplicationContext;
import com.michael.context.support.ClassPathXmlApplicationContext;

/**
 * @author Michael Chu
 * @since 2019-08-26 19:25
 */
public class ApplicationApp {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beanFactory.xml");
        MyTestBean bean = (MyTestBean) context.getBean("myTestBean");
        bean.setName("test");
        System.out.println(bean.getName());
    }
}
