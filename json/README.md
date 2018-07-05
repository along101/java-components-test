一般情况下使用JSON只使用了java对象与字符串的转换，但是，开发APP时候，我们经常使用实体类来做转换；这样，就需要用到注解；

Jackson默认是针对get方法来生成JSON字符串的，可以使用注解来做一些特殊用途；常见的使用如下：

1. 排除属性  
@JsonIgnore，一般标记在属性或方法上；作用于序列化与反序列化；  
@JsonIgnoreProperties，如果是代理类，由于无法标记在属性或方法上，所以，可以标记在类声明上；也作用于反序列化时的字段解析；  

2. 属性别名  
@JsonProperty，序列化/反序列化都有效；

3. 属性排序
@JsonPropertyOrder，注释在类声明中；

4. 属性格式转换
使用自定义序列化/反序列化来处理；
@JsonSerialize，序列化；
@JsonDeserialize，反序列化；

> 注意：在使用hibernate的时候，查询数据库后产生的实体类是个代理类，这时候转换JSON会报错；
> 解决方法有两种：  
> 1）设置FAIL_ON_EMPTY_BEANS属性，告诉Jackson空对象不要抛异常；
  mapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
> 2）使用@JsonIgnoreProperties注解
在实体类声明处加上@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})注解；
建议使用@JsonIgnoreProperties注解，这样生成的JSON中不会产生多余的字段；