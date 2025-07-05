package org.apache.ibatis.test;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;

public class MetaClassTest {

  public static void main(String[] args) {
    MetaClass metaClass = MetaClass.forClass(Son.class, new DefaultReflectorFactory());
    MetaClass person = metaClass.metaClassForProperty("person");
    System.out.println("person = " + person);
    String person1 = metaClass.findProperty("person");
    System.out.println("person1 = " + person1);
  }
}
class Person{



}
class Son{

  public Person getPerson(){
    return new Person();
  }
}
