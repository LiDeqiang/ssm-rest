package com.lee.service.com.lee.redis;

import com.lee.model.Student;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class RedisClientTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testObject() {
        Student student = new Student();
        student.setId(1);
        student.setName("DeqiangLee");

        //存储到到内存中的不是map而是string，进行了序列化
        redisTemplate.opsForValue().set("student_1", student);
        Student student1 = (Student) redisTemplate.opsForValue().get("student_1");
        //上面两步不能保证每次使用RedisTemplate是操作同一个对Redis的连接

        System.out.println(student1.toString());
    }

    @Test
    public void testMap() {
        /*HashMap<String, String> map = new HashMap<>(8);
        map.put("name", "lideqiang");
        map.put("gender", "male");
        redisTemplate.opsForValue().set("map_1",map);*/
        HashMap<String, String> map_1 = (HashMap) redisTemplate.opsForValue().get("map_1");
        System.out.println(map_1.get("name"));
    }

    @Test
    public void testList() {
        redisTemplate.delete("myList");
        redisTemplate.opsForList().leftPush("myList", "T");
        redisTemplate.opsForList().leftPush("myList", "L");
        redisTemplate.opsForList().leftPush("myList", "A");
        redisTemplate.opsForList().rightPush("myList", "B");

        List myList = redisTemplate.opsForList().range("myList", 0, -1);

        System.out.println(myList);

    }

    @Test
    public void testSet() {

    }

    @Test
    public void testZSet() {

    }

    @Test
    public void testHash() {

    }

}