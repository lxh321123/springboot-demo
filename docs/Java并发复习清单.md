# Java 并发复习清单

> 整理时间：2026-10-11
> 用法：**睡前过一遍，明天早上再过一遍**。加粗的是必须能脱口而出的。
> 每个知识点都配了可运行的 Demo，在 `src/main/java/` 下。

---

## 〇、全局速查表（考前 5 分钟看这个）

| 知识点 | 一句话核心 |
|---|---|
| **DCL 单例** | 两次 null 检查 + `volatile` + 私有构造 |
| **创建线程** | 继承 Thread / 实现 Runnable / Callable+FutureTask / **线程池** / 虚拟线程 |
| **wait/notify** | `wait` 必须在 `while` 里；`wait` 会**释放锁**，`sleep` 不会 |
| **线程池** | 核心 → **队列** → 最大线程 → 拒绝策略（**先排队再开线程**） |
| **锁升级** | 无锁 → 偏向锁 → 轻量级锁 → 重量级锁，**只升不降** |
| **volatile** | 保证可见性、有序性，**不保证原子性** |
| **ThreadLocal** | 数据存在 **Thread 里**，用完必须 `remove()` |
| **CAS** | 比较+交换**一条指令**，失败就自旋重试 |
| **AQS** | **volatile state + CAS + FIFO 队列** |

---

## 一、单例模式（DCL）

### 标准代码（必须能默写）

```java
public class trash.Singleton {
    private static volatile trash.Singleton instance;   // volatile 不能省！

    private trash.Singleton() {}

    public static trash.Singleton getInstance() {
        if (instance == null) {                   // 第一次检查：性能
            synchronized (trash.Singleton.class) {
                if (instance == null) {           // 第二次检查：正确性
                    instance = new trash.Singleton();
                }
            }
        }
        return instance;
    }
}
```

### 面试必答点

| 追问 | 关键词 |
|---|---|
| 为什么要**两次**检查？ | 外层为了**性能**（创建后不再加锁），内层为了**正确性**（防止重复创建） |
| `volatile` 能省吗？ | **不能**。`new` 分三步（分配内存→初始化→赋引用），可能被**指令重排**成 1-3-2，别的线程会拿到**半构造对象** |
| 锁对象为什么是 `trash.Singleton`？ | 静态方法里**没有 `this`**；`instance` 还是 null 不能当锁；`new Object()` 每次都不一样。**Class 对象全局唯一、不为 null** |
| 还有什么写法？ | **静态内部类**（推荐，利用 JVM 类加载保证安全）、**枚举**（防反射和序列化破坏） |
| 反射能破坏单例吗？ | 能，**枚举**可以防 |

### 面试话术
> "单例我用 DCL 双重检查锁实现。**第一次检查是为了性能**——实例创建好之后就直接返回，不用每次都抢锁；**第二次检查是为了正确性**——两个线程可能同时通过外层检查，不检查就会创建两个实例。`volatile` 必须加，因为 `new` 对象是三步操作，JVM 可能指令重排，导致别的线程拿到未初始化完成的对象。"

---

## 二、创建线程的方式

| 方式 | 返回值 | 抛受检异常 | 实际使用 |
|---|---|---|---|
| 继承 `Thread` | ❌ | ❌ | 几乎不用 |
| 实现 `Runnable` | ❌ | ❌ | 常用 |
| `Callable` + `FutureTask` | ✅ | ✅ | 需要返回值时 |
| **线程池** | ✅(submit) | ✅ | **实际开发唯一选择** |
| 虚拟线程（JDK 21） | ✅ | ✅ | 高并发 IO 密集 |

### 面试必答点

- **`run()` 和 `start()` 的区别**：`start()` 才**启动新线程**（JVM 内部调 `run()`）；`run()` 只是**当前线程里的普通方法调用**
- **推荐 `Runnable` 而不是继承 `Thread` 的四个理由**：
  1. Java **单继承**，继承 Thread 就不能继承别的
  2. **职责分离**（任务 vs 执行机制）
  3. 一个 Runnable 能被**多个线程共享**
  4. **线程池只接受 Runnable**（最有说服力）
- **`Runnable` vs `Callable`**：`call()` 有返回值、能抛**受检异常**；`run()` 都没有
- **获取子线程结果**：`Callable` + `Future`，`future.get()` 会**阻塞**

### 面试话术
> "主要有四种，加上 JDK 21 的虚拟线程。**实际开发我只用线程池**，因为线程创建成本高，线程池能复用线程、控制数量。我们项目用 `ThreadPoolExecutor` 手动指定参数，不用 `Executors`。"

---

## 三、wait / notify（线程通信）

### 三条铁律

1. **必须在 `synchronized` 里**（否则 `IllegalMonitorStateException`）
2. **`wait` 必须包在 `while` 里，不能写 `if`** ★
3. **优先用 `notifyAll()`**

### 为什么 `wait` 要用 `while` 不能用 `if`？

- **虚假唤醒（spurious wakeup）**：JVM 允许线程没被 notify 就自己醒
- **唤醒后条件可能已变**：`wait` 返回前要重新抢锁，这期间别的线程可能改了条件

### `wait` vs `sleep`（必考）

| | `wait()` | `sleep()` |
|---|---|---|
| 属于 | **`Object`** | **`Thread`** |
| **释放锁吗** | **✅ 释放** | **❌ 不释放** |
| 唤醒方式 | `notify` / 超时 | 到点自动醒 |

### 标准模板
```java
synchronized (lock) {
    while (条件不满足) {      // while，不是 if
        lock.wait();
    }
    // 干活（改共享状态）
    lock.notifyAll();         // 无条件通知，别被 break 跳过
}
```

> **血的教训**：`if (number > 100) break;` 如果放在 `number++` **之后**，`break` 会**跳过 `notifyAll`**，对方线程**永远等不到唤醒**，程序卡死。
> **正确做法**：边界检查放在**打印之前**，`notifyAll` 无条件执行。

---

## 四、线程池 ★★★（最高频）

### 七大参数

```java
new ThreadPoolExecutor(
    2,                                   // 1. corePoolSize     核心线程数
    4,                                   // 2. maximumPoolSize  最大线程数
    10, TimeUnit.SECONDS,                // 3. keepAliveTime    空闲存活时间
                                         // 4. (时间单位)
    new ArrayBlockingQueue<>(3),         // 5. workQueue        任务队列
    Executors.defaultThreadFactory(),    // 6. threadFactory    线程工厂
    new ThreadPoolExecutor.AbortPolicy() // 7. handler          拒绝策略
);
```

### 执行流程 ★★（必答）

```
① 核心线程没满 → 创建【核心线程】执行
② 核心满了    → 【放进队列】排队        ← 注意！不是直接创建新线程
③ 队列也满了  → 创建【临时线程】，直到最大线程数
④ 都满了      → 触发【拒绝策略】
```

**⚠️ 必考陷阱**：**不是"核心满了就一路创建到最大线程数"，而是先排队，队列满了才创建临时线程。**

**为什么？** 线程是重资源（分配栈 + 陷入内核），排队几乎零成本。**宁可让任务等，也不轻易开线程。**

### 4 种拒绝策略

| 策略 | 行为 |
|---|---|
| **`AbortPolicy`**（默认） | **抛 `RejectedExecutionException`** ← 生产推荐 |
| `CallerRunsPolicy` | **谁提交谁跑**（提交线程被拖慢 → **背压/流控**） |
| `DiscardPolicy` | **静默丢弃**（危险） |
| `DiscardOldestPolicy` | 丢队列里最老的 |

> **`CallerRunsPolicy` 会阻塞调用方**。如果调用方是 Tomcat 线程，会**拖垮整个 Web 容器**。所以生产默认选 `AbortPolicy`。

### 为什么阿里禁止 `Executors`？

| 方法 | 隐患 |
|---|---|
| `newFixedThreadPool` / `newSingleThreadExecutor` | 队列**无界**（`LinkedBlockingQueue` 容量是 `Integer.MAX_VALUE`）→ **OOM** |
| `newCachedThreadPool` | 最大线程数是 `Integer.MAX_VALUE` → **无限创建线程** → **OOM** |

**共同病根：参数写死，你没有控制权。** 正确做法是手动 `new ThreadPoolExecutor`，**用有界队列**。

### 线程池大小怎么定？

- **CPU 密集型**：**N + 1**
- **IO 密集型**：**2N**（或 2N+1）
- **收尾必说**：*"这只是理论起点，实际要靠压测，结合 QPS、任务耗时和机器配置来调。"*

### 追问
- **`execute()` vs `submit()`**：`submit` 返回 `Future`，但**异常被吞进 Future**，不调 `get()` 就发现不了
- 核心线程默认**不会被回收**，除非 `allowCoreThreadTimeOut(true)`

---

## 五、synchronized 锁升级

### 完整流程（面试标准答案）

```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
```

| | 偏向锁 | 轻量级锁 | 重量级锁 |
|---|---|---|---|
| 场景 | **只有一个线程**访问 | **少量竞争**，锁持有时间短 | **竞争激烈** |
| 做法 | 把**线程 ID** 记在 Mark Word | **CAS + 自旋** | 操作系统**互斥量** |
| 抢不到时 | —— | **自旋**（空转，烧 CPU） | **阻塞挂起**（让出 CPU，用户态↔内核态切换） |
| 开销 | 极低 | 中 | 高 |

**铁律：锁只能升级，不能降级。**

### 前置：锁存在哪？
**对象头（Object Header）里的 Mark Word**，64 位，**不同锁状态下存的内容不同**（复用）。

### 追问
- **静态 `synchronized` 方法锁的是 `Class` 对象**（全局唯一）；**实例方法锁的是 `this`**
- **可重入**：同一线程可重复获取同一把锁，靠 Mark Word 里的**重入计数**
- **其他优化**：锁消除（JIT 发现锁对象不可能被共享）、锁粗化（合并反复的加解锁）

### 面试话术
> "synchronized 的锁升级是**无锁 → 偏向锁 → 轻量级锁 → 重量级锁**。偏向锁是只有一个线程访问时，把线程 ID 记在对象头的 Mark Word 里，重入不需要任何 CAS；当第二个线程来竞争，撤销偏向锁，升级为轻量级锁，用 CAS + 自旋抢占；自旋失败说明竞争激烈，升级为重量级锁，线程被操作系统阻塞，涉及用户态和内核态的切换。整个过程是 JVM 根据竞争程度**自适应选择**的结果，**锁只能升级不能降级**。"

> **加分补充**（说完上面再补）："另外，偏向锁在 JDK 15（JEP 374）被默认禁用了，新版本 JDK 已经移除——因为撤销需要 STW 安全点，维护成本高，而单线程访问的场景变少了。"
> ⚠️ **顺序不能反**：先说四段式，再说版本差异。

---

## 六、volatile

### 三个性质（定基调）

| 性质 | 保证吗 |
|---|---|
| 可见性 | ✅ |
| 有序性（禁止重排） | ✅ |
| **原子性** | ❌ **不保证** |

### 可见性 → Java 内存模型（JMM）
- **主内存**：所有线程共享，变量真正存的地方
- **工作内存**：**每个线程私有**，是主内存的副本
- 线程读写必须在自己的工作内存里进行 → 一个线程改了值，别的线程可能还在读**旧副本**

**`volatile` 怎么做**：写时立即刷回主内存，读时强制从主内存读。

### 不保证原子性（重点）
```java
volatile int count;
count++;      // ❌ 读-改-写三步，不是原子的
```
**即使 volatile 保证"读到最新值"，两次自增仍可能合并成一次。**

### volatile vs synchronized（必考）

| | `volatile` | `synchronized` |
|---|---|---|
| 可见性 / 有序性 | ✅ | ✅ |
| **原子性** | ❌ | ✅ |
| 阻塞 | **不阻塞** | 会阻塞 |
| 修饰对象 | **只能修饰变量** | 方法/代码块 |
| 适用 | **一写多读** | 需要原子性 |

### 适用场景
- **状态标志位**（`volatile boolean stopped`）
- **DCL 单例**
- **不适合**：`i++`、`check-then-act` 这类复合操作

### 面试话术
> "volatile 保证**可见性**和**有序性**，但**不保证原子性**。可见性方面，JMM 里每个线程有自己的工作内存，普通变量修改后什么时候同步回主内存不确定；volatile 通过写时立即刷回主内存、读时强制从主内存读取来保证。有序性方面，volatile 通过**内存屏障**禁止指令重排——这正是我写 DCL 单例必须加它的原因。但它不保证原子性，因为 `i++` 是读-改-写三步，volatile 只能保证读到最新值，不能保证这三步不被打断。"

---

## 七、ThreadLocal

### 核心原理 ★★★
> **数据不在 `ThreadLocal` 里，在 `Thread` 对象里！**

```java
// Thread.java:281 —— Map 是 Thread 的字段
ThreadLocal.ThreadLocalMap threadLocals;

// ThreadLocal.java:184 —— 拿的是【当前线程】的 Map
private T get(Thread t) {
    ThreadLocalMap map = getMap(t);                  // 拿线程自己的 Map
    ThreadLocalMap.Entry e = map.getEntry(this);    // 用 ThreadLocal 当 key
    ...
}
```

**记忆口诀**：**柜子（Map）挂在线程身上，`ThreadLocal` 只是开柜子的钥匙。**

### 内存泄漏 ★★

```java
static class Entry extends WeakReference<ThreadLocal<?>> {
    Object value;        // ← key 弱引用，value 强引用！
}
```

**泄漏链条**：
1. `ThreadLocal` 对象外部没强引用了 → key 是弱引用 → **被 GC 回收，key 变 null**
2. 但 **value 是强引用**，还被 Entry 抱着 → 回收不掉
3. key 没了，这个 Entry **再也访问不到** → **内存泄漏**

**为什么线程池里特别危险？**
- 普通线程执行完就销毁，`ThreadLocalMap` 跟着回收
- **线程池线程长期存活、反复复用** → Map 一直不释放 → **泄漏累积 → OOM**

**解决**：
```java
try {
    tl.set(x);
    ...
} finally {
    tl.remove();      // ← 必须！
}
```

### 数据串味（实验现象）
线程池复用 + 不 `remove()` → **下一个任务读到上一个任务的数据**。
> **真实事故**：用 ThreadLocal 存登录用户，忘记 remove → 线程复用 → **下一个请求读到上一个用户的身份** → **越权**。

### 应用场景
登录用户信息、Spring 事务管理、链路追踪 traceId、`SimpleDateFormat`（线程不安全）

### 追问
- **`ThreadLocal` vs `synchronized`**：解决**相反**的问题。synchronized 是共享一份靠排队，ThreadLocal 是各持一份避免共享。
- **父子线程传递**：`InheritableThreadLocal`，但**线程池里不生效**，要用 `TransmittableThreadLocal`（TTL）

### 面试话术
> "ThreadLocal 提供线程本地变量，实现线程隔离。**实现原理是：数据并不存在 ThreadLocal 里，而是存在每个 Thread 对象自己的 ThreadLocalMap 字段里**，ThreadLocal 本身只是这个 Map 的 key。内存泄漏风险在于 **Entry 的 key 是弱引用但 value 是强引用**——ThreadLocal 被回收后 key 变 null，value 却还被持有且再也访问不到。**在线程池里问题会放大**，因为线程复用、长期存活，不仅泄漏累积，还会造成**数据串味**。所以必须用完 `remove()`，放 `finally` 里。"

---

## 八、CAS 与原子类

### CAS 是什么
**Compare And Swap**，三个操作数：
- **V** = 内存地址
- **A** = 预期值
- **B** = 新值

**规则**：V 处的当前值 == A 才更新为 B；否则什么都不做，返回失败。
**关键**：比较和交换是**一条 CPU 原子指令**（x86 的 `cmpxchg`），中间不可能被插队。

### 源码（`AtomicInteger.incrementAndGet`）
```java
public final int incrementAndGet() {
    return U.getAndAddInt(this, VALUE, 1) + 1;
}

// Unsafe.getAndAddInt —— 就是 CAS 自旋
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    do {
        v = getIntVolatile(o, offset);                       // ① 读
    } while (!weakCompareAndSetInt(o, offset, v, v + delta)); // ② CAS，失败就重试
    return v;
}
```

### ★ 为什么 CAS 对，volatile 错？（必须搞懂）

**volatile 失败**：两个线程**都读到 1000**，各自算 1001 写回，**丢更新**。
`volatile` 管的是"写完之后别人能看见"，但**管不了"B 的读发生在 A 的写之前"**。

**CAS 成功**：线程 A `CAS(1000→1001)` 成功；线程 B 的 `CAS(1000→1001)` **当场核对发现现在是 1001，失败**，于是**重读重试**。

> **一句话**：CAS 在修改前会**当场核对**"我读到的值现在还是不是这个值"，被改过就**放弃重试**；volatile 没有核对机制，读了就算，中间被改也不知道。

**银行柜台比喻**：volatile 是"我瞄了一眼余额 100 就走去存钱"（路上被取走也不知道）；CAS 是"**如果余额还是 100 就帮我存 1 块**"（柜员当场核对）。

### CAS 的三大问题 ★★

**① ABA 问题**
值从 A → B → A，CAS 误以为没变过。
**解决**：`AtomicStampedReference`——**值和版本号一起 CAS**。改过就翻页，翻页了骗不过。
（危害例子：栈的 pop 操作、账户余额扣款又充回）

**② 自旋开销大**
高竞争下大量线程失败重试、空转烧 CPU，极端情况比锁还慢。
**解决**：`LongAdder` 分段。

**③ 只能保证一个变量的原子性**
**解决**：`AtomicReference` 打包成一个对象，或者加锁。

### Atomic 家族
基本类型（`AtomicInteger`/`Long`/`Boolean`）、引用（`AtomicReference`）、数组（`AtomicIntegerArray`）、字段更新器、**`AtomicStampedReference`（解决 ABA）**

### LongAdder（高并发优化）
**实验数据**：50 线程 × 50 万次，`AtomicLong` 469ms vs `LongAdder` 119ms，**快 3.9 倍**

**原理**：`AtomicLong` 所有线程 CAS **同一个变量** → 互相打断、大量失败；`LongAdder` **分成多个 Cell**，线程各改各的，读取时 `sum()` 汇总。**空间换时间**。

> 思想和 `ConcurrentHashMap` 分段一样：**把竞争分散开**。

**代价**：`sum()` **不是强一致的瞬时快照**。
**选择**：写多读少（计数器/统计）→ `LongAdder`；需要立刻拿准确值 → `AtomicLong`。

---

## 九、AQS

### 是什么
**用一个 `volatile int state` 表示同步状态，用 CAS 修改它，用一个 FIFO 双向队列管理抢不到锁的线程。**

### 三大核心部件（源码为证）
```java
private volatile int state;                    // ① 同步状态
private transient volatile Node head, tail;    // ③ FIFO 双向队列
// ② 用 CAS 修改 state：U.compareAndSetInt(this, STATE, expect, update)
```

### state 的复用（精妙之处）
| 工具 | `state` 的含义 |
|---|---|
| `ReentrantLock` | 0 = 无锁；N = **重入了 N 次** |
| `CountDownLatch` | **倒计数值** |
| `Semaphore` | **剩余许可数** |

### 加锁流程
```
lock() → tryAcquire(1) → CAS 把 state 从 0 改成 1
                            ├─ 成功 → 拿到锁
                            └─ 失败 → 包装成 Node 入队 → 自旋 → 不是队首就 park() 阻塞
```

**设计模式**：AQS 是**模板方法模式**——框架负责排队/阻塞/唤醒，**子类实现 `tryAcquire`/`tryRelease`** 回答"什么叫获取成功"。

### 独占 vs 共享
- **独占**：同一时刻只有一个线程（`ReentrantLock`）
- **共享**：多个线程可同时持有（`Semaphore`、`CountDownLatch`）

### ReentrantLock vs synchronized ★★（必考）

| | `synchronized` | `ReentrantLock` |
|---|---|---|
| 层面 | **JVM**（Monitor） | **JDK**（AQS） |
| 加解锁 | **隐式、自动** | **显式**，必须手动 `unlock()` 放 `finally` |
| 可中断 | ❌ | ✅ |
| 超时获取 | ❌ | ✅ |
| 公平锁 | ❌ | ✅ 可选 |
| 条件变量 | 一个 | **多个 `Condition`** |
| 可重入 | ✅ | ✅ |

**答法收尾**：
> "**实际开发中能用 `synchronized` 就用它**（简单、不会忘记释放），只有需要可中断、超时、公平锁、多条件变量这些高级特性时，才用 `ReentrantLock`。"

### 公平 vs 非公平
`ReentrantLock` **默认非公平**。非公平锁**允许插队**（正在运行的线程可直接 CAS 抢），**减少了线程唤醒和切换的开销，吞吐量更高**；代价是可能**饿死**。

---

## 十、今天踩过的坑（⭐ 面试素材）

**这些是你亲手踩过、亲手改对的，讲出来比背八股有说服力。**

### 1. DCL 单例
- 变量名起成了类名 `Singleton`，**语法能过、功能还对**，但这是**"能跑但有毒"**的代码
- 教训：**能跑通的错误代码，比报错的代码危险十倍**

### 2. 两个线程交替打印 1~100（踩了 5 个坑）
| # | 坑 | 原因 |
|---|---|---|
| 1 | 用了 `run()` 而不是 `start()` | `run()` 就是普通方法调用，**不开新线程**，还会永久阻塞 |
| 2 | `wait()` 没处理 `InterruptedException` | 受检异常，必须 try-catch 或 throws |
| 3 | `number++` 写在 `println` 前面 | 判断的是自增前的值、打印的是自增后的值，**奇偶翻转** |
| 4 | `if (number > 100) break;` 放错位置 | **`break` 跳过了 `notifyAll`** → 对方线程永远等不到唤醒 → 程序卡死 |
| 5 | 线程 B 的花括号少闭合一层 | 把"干活"代码关进了内层 `while` 里 → **空转死循环 + 死抱锁** |

**教训**：
- **`while` 的循环体里只放 `wait`**，干活放在循环**外面**（`while` 是门卫，不是工人）
- **改动了共享状态就必须通知对方**——"我要走了也得打个招呼"，否则对方死等
- **对称的代码必须长得对称**：A 和 B 只该差一处（`%2==0` vs `%2==1`），行数不一样就一定是错的

### 3. 自己发现并修复的接口问题（demo3 的 `/search`）
- **空字符串陷阱**：`<if test="username != null">` 不判空串，前端传 `""` 会拼出 `password = ''`，**查询恒为空数组**
- **死代码**：XML 里有 `<if test="id != null">`，但 DTO 里根本没 `id` 字段，**永远不生效**
- **密码泄露**：`UserVO` 带 `password`，接口直接把密码返回给前端

**这三个都能包装成"我在开发中发现并修复的问题"，比干背八股强。**

---

## 十一、Demo 文件索引（可以重跑验证）

| 文件 | 演示内容 |
|---|---|
| `trash.Singleton`（在 `test/`） | DCL 单例 |
| `trash.PrintNumber` | 两线程交替打印 1~100 |
| `trash.PoolDemo` | 线程池执行流程 + 拒绝策略 |
| `trash.VisibilityDemo` | `volatile` 可见性（去掉 volatile 子线程会卡死） |
| `trash.AtomicityDemo` | `volatile` **不保证原子性**（丢更新） |
| `trash.CasDemo` | volatile vs synchronized vs AtomicInteger |
| `trash.LongAdderDemo` | AtomicLong vs LongAdder（3.9 倍差距） |
| `trash.ThreadLocalDemo` | 线程池复用导致**数据串味** |

**关键实验**（睡前有空可以跑一遍，加深印象）：
- `VisibilityDemo`：**去掉** `volatile` → 子线程永久卡住
- `ThreadLocalDemo`：**去掉** `finally { USER.remove(); }` → 任务 2、3 拿到"用户-1"

---

## 十二、明天早上的复习顺序（30 分钟）

按这个顺序，由高到低：

| 顺序 | 内容 | 时间 |
|---|---|---|
| 1 | **本清单第〇节速查表** | 5 分钟 |
| 2 | **线程池**（7 参数 + 执行流程 + 为什么禁 Executors） | 8 分钟 |
| 3 | **volatile 三个性质 + 和 synchronized 的区别** | 5 分钟 |
| 4 | **ThreadLocal 原理 + 内存泄漏 + remove** | 5 分钟 |
| 5 | **锁升级四段式**（出声念一遍面试话术） | 5 分钟 |
| 6 | **CAS vs volatile 的本质区别** | 2 分钟 |

**最后 5 分钟**：把第〇节速查表**出声念一遍**。

### 自测题（答不出来就回去看）

1. 线程池任务提交后的**四步流程**？为什么不是"核心满了就创建到最大线程数"？
2. `volatile` 保证什么？**不保证**什么？为什么 `i++` 用它没用？
3. ThreadLocal 的**数据存在哪**？为什么会内存泄漏？怎么避免？
4. CAS 和 volatile 的**本质区别**是什么？
5. 锁升级的四段？**静态 `synchronized` 方法锁的是谁**？
6. `ReentrantLock` 和 `synchronized` **各说两个区别**？
7. `wait` 为什么要放在 `while` 里而不是 `if`？

---

## 十三、面试技巧提醒

**1. 会的题：先给结论，再展开，90 秒内说完。**
> 例：问"为什么用 MQ" → 第一句就说"解决三个问题：解耦、异步、削峰"，然后展开。

**2. 不会的题：承认边界 + 展示推导。**
> "这块我没有深入实践过。我的理解是 X，因为 Y，但不确定细节，需要回去查证。"
> **不懂装懂是硬伤，坦诚是加分项。**

**3. 主动把话题引到准备过的地方。**
> 被问到缓存，除了答 Redis，顺势说"我在实习里做过语义推送的缓存策略，遇到过缓存和数据库不一致……"——**让面试官问你会答的题。**

**4. 机机制 ≠ 实现。**
> 面试考**机制原理**（稳定不变），版本差异只是**加分补充**。
> 先答标准答案，再说"补充一点，新版本有变化……"——**顺序不能反。**

---

## 十四、还没讲的知识点（后续补）

- 死锁（四个必要条件、怎么排查）
- `ReentrantLock` / `Condition` 实战
- Java 集合（`HashMap` 1.7 vs 1.8、`ConcurrentHashMap`）
- JVM（内存结构、GC、类加载、OOM 排查）
- MySQL（索引、事务、MVCC、锁）
- Redis（缓存三兄弟、分布式锁）
- Spring（IOC/AOP、Bean 生命周期、事务失效场景）

---

**加油，明天好好发挥。💪**
