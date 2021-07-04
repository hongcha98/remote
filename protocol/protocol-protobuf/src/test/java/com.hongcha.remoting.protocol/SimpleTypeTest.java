package com.hongcha.remoting.protocol;


import org.junit.Assert;
import org.junit.Test;


public class SimpleTypeTest {
    @Test
    public void demo() {
        User.Demo.Builder builder = User.Demo.newBuilder();

        User.Demo a = builder.setName("a")
                .setAge(1).build();
        Protocol protobufProtocol = new ProtobufProtocol();
        for (int i = 0; i < 500; i++) {
            byte[] bytes = protobufProtocol.encode(a);
            long l = System.nanoTime();
            User.Demo decode = protobufProtocol.decode(bytes, User.Demo.class);
            Assert.assertNotNull(decode);
            System.out.println(System.nanoTime() - l);
        }

    }


}