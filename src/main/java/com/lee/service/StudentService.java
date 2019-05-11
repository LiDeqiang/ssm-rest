package com.lee.service;

import com.lee.mapper.StudentExtendMapper;
import com.lee.mapper.StudentMapper;
import com.lee.model.Student;
import com.lee.model.StudentExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Random;

/**
 * 缓存机制说明：所有有缓存注解的查询结果都放进了缓存，也就是把MySQL查询的结果放到了redis中去，
 * 然后第二次发起该条查询时就可以从redis中去读取查询的结果，从而不与MySQL交互，从而达到优化的效果，
 * redis的查询速度之于MySQL的查询速度相当于 内存读写速度 /硬盘读写速度
 *
 * @Cacheable(value="xxx" key="zzz")注解：标注该方法查询的结果进入缓存，再次访问时直接读取缓存中的数据
 * 1.对于有参数的方法，指定value(缓存区间)和key(缓存的key)；
 * 对于无参数的方法，只需指定value,存到数据库中数据的key通过com.ssm.utils.RedisCacheConfig中重写的generate()方法生成。
 * 2.调用该注解标识的方法时，会根据value和key去redis缓存中查找数据，如果查找不到，则去数据库中查找，然后将查找到的数据存放入redis缓存中；
 * 3.向redis中填充的数据分为两部分：
 * 1).用来记录xxx缓存区间中的缓存数据的key的xxx~keys(zset类型)
 * 2).缓存的数据，key：数据的key；value：序列化后的从数据库中得到的数据
 * 4.第一次执行@Cacheable注解标识的方法，会在redis中新增上面两条数据
 * 5.非第一次执行@Cacheable注解标识的方法，若未从redis中查找到数据，则执行从数据库中查找，并：
 * 1).新增从数据库中查找到的数据
 * 2).在对应的zset类型的用来记录缓存区间中键的数据中新增一个值，新增的value为上一步新增的数据的key
 */

@Service
public class StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StudentExtendMapper studentExtendMapper;

    @Cacheable(value = "student", key = "'id:' + #id")
    @Transactional(readOnly = true)
    public Student getStudentById(Integer id) {
        StudentExample studentExample = new StudentExample();
        studentExample.createCriteria().andIdEqualTo(id);
        List<Student> students = studentMapper.selectByExample(studentExample);
        if (!CollectionUtils.isEmpty(students)) {
            return students.get(0);
        } else {
            return null;
        }
    }

    /**
     * 获取所有信息
     * 1.对数据一致性要求较高，所以在执行增删改操作后需要将redis中该数据的缓存清空，从数据库中获取最新数据。
     * 2.若缓存中没有所需的数据，则执行该方法后：
     * 1).在redis缓存中新增一条数据
     * key：getAllStudent  value：序列化后的List<Student>
     * key的值通过RedisCacheConfig中重写的generate()方法生成
     * 2).在用来记录studentCache缓存区间中的缓存数据的key的studentCache~keys(zset类型)中新添加一个value，值为上面新增数据的key
     */
    @Cacheable(value = "student", key = "'allStudent'")
    public List<Student> getAllStudent() {
        return studentExtendMapper.getAllStudent();
    }

    /**
     * 根据id删除用户
     */
    @Caching(evict = {
            @CacheEvict(value = "student", key = "allStudent"),
            @CacheEvict(value = "student", key = "'id:' + #id")
    })
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int delStuById(Integer id) {
        StudentExample studentExample = new StudentExample();
        studentExample.createCriteria().andIdEqualTo(id);
        return studentMapper.deleteByExample(studentExample);
    }

    /**
     * value + key 或者 value + allEntries=true
     * 1.value + key 移除value缓存区间内的键为key的数据
     * 2.value + allEntries=true 移除value缓存区间内的所有数据
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "student", key = "allStudent")
            },
            cacheable = {
                    @Cacheable(value = "student", key = "'id:' + #student.id")
            })
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insetStu(Student student) {
        return studentMapper.insert(student);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "student", key = "allStudent")
            },
            put = {
                    @CachePut(value = "student", key = "'id:' + #student.getId()")
            })
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateStu(Student student) {
        StudentExample studentExample = new StudentExample();
        studentExample.createCriteria().andIdEqualTo(student.getId());
        return studentMapper.updateByExample(student, studentExample);
    }

    /**
     * 根据关键词模糊查询，命中率较低，不存入redis缓存中
     */
    public List<Student> findStudents(String keyWords) {
        return null;
    }

    /**
     * 统计当前所有用户ID
     * 1.对数据一致性要求较高，所以在执行增删改操作后需要将redis中该数据的缓存清空，从数据库中获取最新数据。
     * 2.执行该方法后，在redis缓存中新增一条数据
     * 1) selectNowIds 缓存的数据的key，可以在RedisCacheConfig中重写generate()方法自定义
     * 3.然后在zset类型的aboutUser中添加一个值，值为上线的key
     */
    @Cacheable(value = "student")
    public List<Integer> selectNowIds() {
        return studentExtendMapper.selectNowIds();
    }

    /**
     * 统计注册用户个数
     * 对数据一致性要求不高，所以在controller中使用redisTemplate存入redis，
     * 并指定生存时间为1小时
     */
    public Integer selectStudentCount() {
        return new Random().nextInt();
    }
}
