package org.mjc.test;

import ch.qos.logback.core.joran.action.SerializeModelAction;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Test {
    public static void main(String[] args) {
        Student student = new Student("张三");
        Class cls = student.getClass();
        System.out.println(cls.isAnnotationPresent(Myhat.class));
        if (cls.isAnnotationPresent(Myhat.class)){
            Myhat annotation = (Myhat) cls.getAnnotation(Myhat.class);
            if (annotation.color().equals("red")){
                System.out.println("张三部长您好，您的位置在左侧");
            }else  if (annotation.color().equals("yellow")){
                System.out.println("张三司长您好，您的位置在后排");
            }
        }else{
            System.out.println("非请入内");
        }

    }
}
@Myhat(color = "yellow")
class Student{

    public String name;

    public Student(String name) {
        this.name = name;
    }
}
//接口，抽象的最高境界（只有定义没有实体）
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@interface Myhat{
    String color() default "red";
}