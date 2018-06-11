package downloaddemo.liyanying.example.com.downloaddemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
//        assertEquals(4, 2 + 2);
//        System.out.print(23213);
//        int a=3;
//        int b=3;

        Integer aa=3;
        Integer bb=3;
        String a="a";
        Dog dog=new Dog();
        a.toString();
        String b="a";
        printlnM(dog);
        printlnM(dog.toString());
//        printlnM(Integer.valueOf(3).toString().hashCode());
    }

    class Dog{

    }
    private void printlnM(Object o){
        System.out.println(o);
    }
    public static void printCallStatck() {
        Throwable ex = new Throwable();
        StackTraceElement[] stackElements = ex.getStackTrace();
        if (stackElements != null) {
            for (int i = 0; i < stackElements.length; i++) {
                System.out.print(stackElements[i].getClassName()+"/t");
                System.out.print(stackElements[i].getFileName()+"/t");
                System.out.print(stackElements[i].getLineNumber()+"/t");
                System.out.println(stackElements[i].getMethodName());
                System.out.println("-----------------------------------");
            }
        }
    }
}