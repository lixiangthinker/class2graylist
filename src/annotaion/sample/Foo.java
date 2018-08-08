package annotaion.sample;

import android.annotation.UnsupportedAppUsage;

public class Foo {
    @UnsupportedAppUsage
    public String blabla = "filed test";
    @UnsupportedAppUsage
    Foo() {
        System.out.println("this is a test Class for annotation");
        System.out.println("Constructor test");
    }
    @UnsupportedAppUsage
    public void bar() {
        System.out.println("method test");
    }
}
