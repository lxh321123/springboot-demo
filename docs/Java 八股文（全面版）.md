好，我帮你整理一份**全面的 Java 八股文**，按板块分，**问题 + 大白话答案**，可以直接背。

---

# Java 八股文（全面版）

---

## 一、Java 基础

**1. == 和 equals 的区别？**

`==` 对基本类型比值，对引用类型比地址；`equals` 默认比地址，String、Integer 等重写后比内容。

**2. String、StringBuilder、StringBuffer 区别？**

String 不可变；StringBuilder 可变、线程不安全、快；StringBuffer 可变、线程安全、慢。

**3. String 为什么不可变？**

类被 final 修饰，内部 char 数组也是 final，且不提供修改方法。

**4. `String s = "a"` 和 `new String("a")` 区别？**

`"a"` 在常量池，`new String("a")` 在堆里新建。`==` 比地址，`equals` 比内容。

**5. 重写 equals 为什么必须重写 hashCode？**

hashCode 的约定是：equals 相等的对象，hashCode 必须相等。不重写会导致 HashMap/HashSet 出现重复元素。

**6. final、finally、finalize 区别？**

final 关键字，不能改；finally try-catch 里必执行；finalize 是 Object 方法，回收前调用。

**7. 接口和抽象类的区别？**

接口描述能力，可多实现；抽象类描述同类，只能单继承。

**8. 受检异常和运行时异常的区别？**

受检异常编译器强制处理；运行时异常不强制。Error 不处理。

**9. String 的 intern() 是干什么的？**

把字符串放进常量池，返回池里的引用。

**10. ArrayList 和 LinkedList 区别？**

ArrayList 数组，随机访问快；LinkedList 链表，插入删除快。

---

## 二、集合

**11. HashMap 底层？**

数组 + 链表 + 红黑树。链表 ≥8 且数组 ≥64 才树化，否则先扩容。

**12. HashMap 为什么容量是 2 的幂？**

为了用 `(n-1) & hash` 代替取模，位运算更快。

**13. HashMap 的 put 流程？**

① 算 hash 定位下标 ② 槽位空直接放 ③ 有值则链表尾插/树插 ④ 超过容量×0.75 扩容 2 倍。

**14. HashMap 1.7 和 1.8 的区别？**

1.7 头插法（并发扩容可能成环），1.8 尾插法 + 红黑树。

**15. ConcurrentHashMap 1.8 怎么保证线程安全？**

空桶用 CAS，非空桶用 synchronized 锁住桶的头节点。

**16. CAS 是什么？**

比较并交换。是期望值就改，不是就重试。原子操作，不加锁。有 ABA 问题，用版本号解决。

**17. HashMap 和 HashTable 的区别？**

HashMap 线程不安全，允许 null；HashTable 线程安全（synchronized），不允许 null。

---

## 三、并发

**18. 多线程三大问题？**

原子性（synchronized/AtomicInteger）、可见性（volatile）、有序性（volatile/synchronized）。

**19. synchronized 和 volatile 的区别？**

synchronized 保证原子性+可见性+有序性；volatile 只保证可见性+有序性。

**20. 线程池执行流程？**

核心线程 → 队列 → 临时线程 → 拒绝策略。先排队后扩容。

**21. 线程池的拒绝策略？**

AbortPolicy（抛异常）、CallerRunsPolicy（谁提交谁干）、DiscardPolicy（丢弃）、DiscardOldestPolicy（丢最老）。

**22. 为什么不用 Executors？**

newFixedThreadPool 队列无界，任务堆积会 OOM。要手动 new ThreadPoolExecutor。

**23. ThreadLocal 的数据存在哪里？**

存在当前线程自带的 ThreadLocalMap 中，以 ThreadLocal 实例自身为 key。

**24. ThreadLocal 为什么 key 用弱引用？**

让 ThreadLocal 对象能被 GC 回收。但会留下僵尸 Entry，必须 finally 里 remove()。

**25. 什么是死锁？怎么避免？**

两个线程互相等对方的锁。避免：按顺序加锁、超时释放、死锁检测。

**26. synchronized 锁升级过程？**

无锁 → 偏向锁 → 轻量级锁 → 重量级锁。

**27. 线程的几种状态？**

新建、就绪、运行、阻塞、等待、超时等待、终止。

---

## 四、JVM

**28. JVM 内存结构？**

堆、方法区（共享）；虚拟机栈、本地方法栈、程序计数器（私有）。程序计数器唯一不会 OOM。

**29. 对象怎么判断是否存活？**

可达性分析，从 GC Roots 出发顺着引用找。GC Roots：栈局部变量、静态变量、常量、native 方法引用。

**30. 为什么不用引用计数法？**

循环引用回收不掉。

**31. 对象什么时候晋升老年代？**

四条路径：年龄到 15、大对象直接进、动态年龄判断、Survivor 装不下。

**32. Minor GC 和 Full GC 区别？**

Minor GC 只扫新生代，快；Full GC 整堆，慢、STW 长。

**33. 垃圾回收算法有哪些？**

标记-清除（有碎片）、标记-复制（费空间）、标记-整理（慢但无碎片）。

**34. 分代收集理论？**

新生代用复制（存活率低），老年代用标记-清除/整理（存活率高）。

**35. 常见垃圾回收器？**

Serial、Parallel、CMS、G1、ZGC。G1 是 JDK9+ 默认。

**36. G1 的特点？**

不物理分代，把堆切成等大 Region，优先回收垃圾多的，可预测停顿。

**37. OOM 怎么排查？**

jmap -histo 看什么对象最多，jmap -dump 导快照，MAT 分析引用链，修代码。

**38. 常用 JVM 调优参数？**

-Xms、-Xmx（堆大小）、-Xmn（新生代）、-XX:MetaspaceSize、-XX:+HeapDumpOnOutOfMemoryError。

---

## 五、Spring

**39. IOC 和 DI 的关系？**

IOC 是思想，把对象创建权交给容器；DI 是实现方式，容器把对象注入进来。

**40. AOP 是什么？**

把公共逻辑（日志/事务/权限）抽成切面，织入到方法。底层靠动态代理。

**41. AOP 四个核心概念？**

切点（在哪织入）、通知（织入什么）、切面（切点+通知）、连接点（候选方法）。

**42. AOP 默认 JDK 还是 CGLIB？**

看 Bean 有没有实现接口：实现了用 JDK，没实现用 CGLIB。

**43. JDK 动态代理和 CGLIB 的区别？**

JDK 只能代理实现了接口的类；CGLIB 生成目标类的子类，不能代理 final 方法/类。

**44. Bean 的生命周期？**

实例化 → 属性填充 → @PostConstruct → BeanPostProcessor → 使用 → @PreDestroy。

**45. Spring 事务失效的场景？**

自调用、非 public、final/static、不是 Bean、异常被吞、受检异常没配 rollbackFor、传播行为错。

**46. 自调用为什么事务失效？**

this.save() 绕过代理，切面没机会拦截。本质是代理。

**47. 抛受检异常会回滚吗？**

默认不会，Spring 默认只回滚 RuntimeException 和 Error。要配 rollbackFor。

**48. Spring 的传播行为？**

REQUIRED（默认，有就加入，没有就新建）、REQUIRES_NEW（总是新建）、NESTED 等。

**49. SpringBoot 自动装配原理？**

@SpringBootApplication 包含 @EnableAutoConfiguration，去 META-INF/spring.factories 找自动配置类，按 @ConditionalOnXxx 条件装配。

**50. SpringMVC 执行流程？**

DispatcherServlet → HandlerMapping → HandlerAdapter → Controller → 视图/JSON。

**51. Spring 循环依赖怎么解决？**

三级缓存。但构造器注入不能解决，要用字段/Setter。

**52. MyBatis 的 #{} 和 ${} 区别？**

#{} 预编译防注入；${} 字符串拼接有风险。

**53. MyBatis 一级缓存和二级缓存？**

一级 SqlSession 级别默认开；二级 namespace 级别需配置。

---

## 六、MySQL

**54. 索引为什么用 B+ 树？**

非叶子只存索引，扇出大，树矮 IO 少；叶子双向链表，范围查询快。

**55. B+ 树 3 层能存多少？**

约 2000 万行。超过变 4 层，多一次 IO。

**56. 聚簇索引和二级索引区别？**

聚簇索引叶子存整行；二级索引叶子存索引列+主键值。

**57. 什么是回表？**

用二级索引查到主键后，再去聚簇索引查整行。

**58. 什么是覆盖索引？**

索引里已包含查询所需字段，不用回表。

**59. 最左前缀原则？**

联合索引从最左列开始用，不能跳。范围条件后面的列会失效。

**60. 索引失效的场景？**

函数、类型转换、前置 %、范围后、or/!=、违背最左前缀。

**61. 事务 ACID？**

原子性 undo log，持久性 redo log，隔离性 MVCC+锁，一致性是目标。

**62. 脏读、不可重复读、幻读？**

脏读读到未提交；不可重复读两次读同一行结果不同（UPDATE）；幻读两次查多出数据（INSERT）。

**63. 隔离级别？**

读未提交、读已提交 RC、可重复读 RR（InnoDB 默认）、串行化。

**64. MVCC 怎么实现的？**

undo log 版本链 + ReadView。RC 每次查询生成新 ReadView，RR 只第一次生成并复用。

**65. 有哪些锁？**

行锁、间隙锁（防幻读）、临键锁（行锁+间隙锁）。

**66. explain 怎么看？**

先看 type，出现 ALL 就是全表扫；再看 key 有没有走索引；最后看 rows 越少越好。

**67. 深分页为什么慢？怎么优化？**

LIMIT 1000000,10 要扫前 100 万行丢弃。优化：延迟关联（先查 id 再回表）或 WHERE id > 上次最大 id。

**68. 自增主键为什么快？**

顺序追加，不触发页分裂。

---

## 七、Redis

**69. 缓存穿透、击穿、雪崩？**

穿透（数据不存在）→ 布隆过滤器/缓存空值；击穿（热 key 过期）→ 互斥锁；雪崩（大量 key 同时过期）→ 随机过期。

**70. 布隆过滤器原理？**

一个大位数组 + 几个哈希函数。说"不存在"一定对，说"存在"可能错。

**71. RDB 和 AOF 区别？**

RDB 快照，恢复快但丢多；AOF 命令日志，丢少但恢复慢。

**72. 混合持久化？**

AOF 重写时把当前 RDB 快照存进 AOF 头，后面追加增量。恢复快 + 丢得少。

**73. Redis 分布式锁怎么加？**

`SET lock "userId" NX EX 10`，一条原子命令。解锁用 Lua 脚本。

**74. 为什么用 Lua 解锁？**

保证"判断是我的 + 删除"是原子的。

**75. 看门狗是什么？**

Redisson 的机制。没指定过期时间时，后台线程定期续期。

**76. Redis 的过期策略？**

惰性删除 + 定期删除。

**77. Redis 的内存淘汰策略？**

noeviction、allkeys-lru、volatile-lru 等。常用 allkeys-lru。

**78. Redis 为什么快？**

内存操作、单线程无锁、IO 多路复用。

**79. Redis 的持久化怎么选？**

生产常用混合持久化，AOF everysec。

---

## 八、消息队列

**80. MQ 解决了什么问题？**

解耦、异步、削峰。

**81. 消息可能在哪丢失？**

生产者、MQ 自身、消费者。对应：确认机制、持久化、手动 ACK。

**82. 怎么防止重复消费？**

幂等。唯一 ID 去重、数据库唯一索引、状态机。

**83. 消息堆积怎么办？**

增加消费者、临时扩容、提高消费速度、丢非核心消息。

**84. 什么是幂等性？怎么实现？**

同一个操作执行多次，结果一样。唯一 ID+去重表、唯一索引、乐观锁、状态机、Token。

---

## 九、分布式

**85. CAP 定理？**

一致性、可用性、分区容错性，三者最多同时满足两个。Zookeeper 是 CP，Eureka、Nacos 是 AP。

**86. 分布式事务方案？**

2PC/3PC、TCC、本地消息表、事务消息、Saga。强一致短事务用 2PC/TCC，最终一致高并发用本地消息表/事务消息。

**87. 熔断、降级、限流？**

熔断切断调用，降级返回兜底，限流限制速率。

**88. 分布式锁怎么实现？**

Redis（SETNX+EX+Lua）、Zookeeper（临时顺序节点）、数据库（唯一索引）。

---

## 十、算法

**89. 反转链表？**

三指针：先存 next，反转，前进。

**90. 判断链表有环？**

快慢指针，相遇有环。

**91. 最近公共祖先？**

递归，左右都找到则 root 是 LCA。

**92. 最长无重复子串？**

滑动窗口 + HashSet。

**93. 快排思路？**

选基准，分区，递归。

**94. 归并思路？**

分治，递归分左右，合并有序数组。

**95. 动态规划框架？**

定义 dp → 转移方程 → 边界 → 顺序。

---

## 十一、计算机网络

**96. TCP 三次握手？**

SYN → SYN+ACK → ACK。

**97. TCP 四次挥手？**

FIN → ACK → FIN → ACK。

**98. TCP 和 UDP 区别？**

TCP 可靠、面向连接；UDP 不可靠、无连接、快。

**99. HTTP 和 HTTPS 区别？**

HTTPS 加了 SSL/TLS 加密。

**100. 常见 HTTP 状态码？**

200 成功、301/302 重定向、304 缓存、401 未认证、403 无权限、404 找不到、500 服务器错误、502 网关错误。

---

这份八股覆盖了**Java 岗最常问的 100 道题**。建议你**按板块背，每天一个板块**，背完自己讲一遍。

如果还有哪个板块想展开（比如"操作系统"、"设计模式"），直接说。