package com.along101.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

/**
 * @author yinzuolong
 */
public class JacksonTest {


    @Test
    public void testProperty() throws JsonProcessingException {

        Cat cat = new Cat();
        cat.setName("tom");
        cat.setAge(1);
        cat.setOwner("yzl");
        cat.setNotuse("not use");

        ObjectMapper objectMapper = new ObjectMapper();
        //默认字段为null会输出，这里可以设置为null不输出
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //美化输出
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();

        String json = objectWriter.writeValueAsString(cat);

        System.out.println(json);
    }


    public static class Cat {

        private String name;

        @JsonProperty("Age")
        private int age;

        private String Owner;

        @JsonIgnore
        private String notuse;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return this.age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getOwner() {
            return Owner;
        }

        public void setOwner(String owner) {
            Owner = owner;
        }

        public String getNotuse() {
            return notuse;
        }

        public void setNotuse(String notuse) {
            this.notuse = notuse;
        }
    }

}
