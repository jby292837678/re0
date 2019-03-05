package com.king.re0;


import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/*该标识用于描述注解的使用范围（即：被描述的注解可以用在什么地方）： packages、types（类、接口、枚举、Annotation类型）、类型成员（方法、构造方法、成员变量、枚举值）、方法参数和本地变量（如循环变量、catch参数）。在Annotation类型的声明中使用了target可更加明晰其修饰的目标。
取值(ElementType)有：
　　1.CONSTRUCTOR:用于描述构造器
　　2.FIELD:用于描述域
　　3.LOCAL_VARIABLE:用于描述局部变量
　　4.METHOD:用于描述方法
　　5.PACKAGE:用于描述包
　　6.PARAMETER:用于描述参数
　　7.TYPE:用于描述类、接口(包括注解类型) 或enum声明*/

/*取值(ElementType)有：
        　 1.SOURCE:在源文件中有效（即源文件保留）
        　　2.CLASS:在class文件中有效（即class保留）
        　　3.RUNTIME:在运行时有效（即运行时保留）*/
@Target(value = TYPE)
@Retention(value = SOURCE)
//@Repeatable(Object.class)
@Inherited
public @interface TestInterface {
    String path();
}
