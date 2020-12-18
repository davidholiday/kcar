package io.holitek.kcar;

public class MyBean {

    private String hi;
    private String bye;

    public MyBean(String hi, String bye) {
        this.hi = hi;
        this.bye = bye;
    }

    public String hello() {
        return hi + " how are you?";
    }

    public String bye() {
        return bye + " World";
    }
}
