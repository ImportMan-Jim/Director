# Director是什么？



​		Director是一个基于Java语言的多行为者（multi-actors）仿真框架。其优势在于轻量级与便捷性，只需要通过简单的注解配置，就可以快速搭建仿真系统。正如其名字一样，Director会负责管理所有的行为者（actor）并执行它们的行为（action），让开发者更专注于行为者的行为逻辑设计。

<img src="D:\study\mavenProject\director-logo.png" alt="director-logo" style="zoom:40%;" />

# Director的应用场景

​		当您需要设计模拟多个实体（行为者）互动、协作或并行操作的复杂系统时，引入Director让您的开发更加高效而优雅。例如：

1. **智能交通系统仿真**：Director 可以用于模拟交通流量和信号灯控制系统。行为者可以是车辆、行人、交通信号灯等，框架可以帮助研究交通管理策略、优化路径规划、减少交通拥堵等问题。

2. **智能制造与工业自动化**：在工业自动化中，Director可以模拟生产线上的多个机械臂、机器人、传感器等。通过仿真，可以优化生产过程、预测潜在的瓶颈并调整自动化流程。

3. **分布式计算与系统仿真**：Director可以用于模拟分布式系统中的节点、网络通讯和并行计算任务。该框架能够帮助研究网络延迟、任务调度以及分布式算法的性能。

4. **游戏AI与多角色互动**：游戏开发中，Director可以用来模拟多角色行为和互动。每个角色可以是一个行为者，负责不同的行为（例如巡逻、攻击、逃跑等），开发者可以专注于设计角色行为逻辑，而Director框架管理这些角色的并行执行。

5. **无人系统仿真**：该框架可用于无线传感网、无人机编队、自动驾驶车队等无人系统的仿真。行为者可以是每个传感器节点、无人机、自动驾驶汽车，通过仿真测试不同的网络拓扑、队形、通信协议、决策逻辑等。

6. **经济与社会行为仿真**：在社会或经济系统的仿真中，行为者可以是个体、企业或国家等。通过模拟多个行为者之间的互动，可以研究市场行为、政策影响或社会网络中的信息传播等现象。

7. **机器人群体协作**：Director 可以用于仿真多个机器人在同一环境下的协作和任务分配。比如清扫机器人、仓储机器人之间的协作与任务分配，仿真环境可以帮助优化算法和策略。

8. **智能家居系统仿真**：可以模拟智能家居中的多个设备，如灯、恒温器、摄像头等。Director可以帮助开发者测试不同设备之间的交互、自动化场景和策略，比如在家居中如何根据不同条件自动调整设置。

9. **应急响应与灾难管理**：行为者可以代表不同的应急人员或救援设备，Director可以模拟多方应急响应中的协作，如地震、火灾等灾难中的资源调度和救援策略。

10. **更多的应用场景**：……

# 从简单案例开始

​		假如现在有一个场景需求：**模拟一个小孩在路面上随机走动1000秒的过程**。让我们看看在Java中如何使用Director来实现这个需求。

1.首先，您需要将仓库中的direct-x.x.x.jar包下载到本地，之后在项目中引入即可使用。

2.创建您的项目，这里，我们直接创建一个最简单的Java项目并引入direct-x.x.x.jar包。目录结构如下所示，Director对项目结构没有明确要求，但需要一个启动类，确保该启动类与包含代码的Package处于同一级。例如案例中，`KidSimulationApplication.class`作为启动类，与actors处于同一级。这是因为Director类似于Spring，默认会扫描启动类所在的Package下的所有类。

<img src="C:\Users\Jim\AppData\Roaming\Typora\typora-user-images\image-20241007141401854.png" alt="image-20241007141401854" style="zoom:60%;" />

3.编写启动类，类似Spring项目，在main方法中调用`DirectorApplication.run()`是必不可少的。

```java
import com.jim.director.framework.core.DirectorApplication;

public class KidSimulationApplication {
    public static void main(String[] args) {
        DirectorApplication.run(KidSimulationApplication.class);
    }
}
```

4.创建Actor，即行为者。本案例中，小孩就是一个行为者。为此，我们创建一个Kid类表示小孩，属性为当前小孩的位置坐标。使用Director提供的`@Actor`注解来标记Kid类为一个Actor。

> 任何具备某些行为的实体均可视为行为者。

```java
@Actor
public class Kid {

    private int x = 0;

    private int y = 0;
}
```

5.编写Action，即行为。本案例中，小孩的行为是“在路面上随机走动”。为此，在Kid类中，我们编写一个`walk()`方法模拟小孩走动。使用`@Action`注解即可标记方法为一个Action。注意，当一个Actor中有Action时，需要使用`@Order`注解声明Action的执行顺序。

```java
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;

    @Action
    public void walk(){
        int[] numbers = {-1, 0, 1};
        Random random = new Random();
        int randomNumber = numbers[random.nextInt(numbers.length)];// choose one of numbers
        x += randomNumber;
        randomNumber = numbers[random.nextInt(numbers.length)];
        y += randomNumber;
    }
}
```

6.运行启动类。仅需上述三个注解，即可完成行为者仿真配置，是不是很简单？那么项目运行后Director会如何去执行呢？逻辑很简单！Director会统一管理所有的Actor，在它的认知里，时间单位并不是时、分或者秒，而是帧！因此，在每一帧内，Director会根据Actor的`@Order`注解配置的执行顺序去执行它们的Action。默认执行帧数为1000，执行完仿真结束（就像在播放动画一样）。例如，在本案例中，小孩在每帧执行的Action为`walk()`方法，从而实现了随机走动。

> TIPS：每一帧是时、分或者秒完全取决于您如何定义。

# 更全面的介绍

​		相信上个案例已经让您对Director有了初步的理解，接下来让我们看看它还能干些什么！

## @Actor

​		在行为者仿真中，Actor是我们的主角。正如您所看到的，`@Actor`注解作用于类，用来声明一个类为Actor。Director只会管理所有的Actor，因此，其他所有注解都只在Actor中才会生效。

​		`@Actor`支持单例模式和多例模式，当使用`@Actor`或者`@Actor(ActorType.SINGLETON)`时为单例模式，此时Director只会为对应的Actor创建一个实例，您在项目任何地方声明和使用的都是这个Actor实例。当使用`@Actor(ActorType.PROTOTYPE)`时为多例模式，此时Director会为对应的Actor创建多个实例，详细使用方法见@ActorCollectionAutowired节。

```java
@Actor //equals to @Actor(ActorType.SINGLETON)
public class Kid {

    private int x = 0;

    private int y = 0;
}
```

## @Action

​		当您希望Actor在每帧中执行一些行为时，可以写一个或者多个方法并用`@Action`标记，被标记的方法将作为Action被执行。`@Action`需要配合`@Order`使用，`@Order`作用于Actor，以声明Action在每帧内的执行顺序，`@Order`的参数值越小优先级越高。例如下所示，分别创建两个Actor：Kid、Frog，则Kid的Action会比Frog的Action优先执行。

​		`@Action(String status)`允许传参，此时为带有状态的Action，详细使用方法见@Status节。

```java
@Order(1)
@Actor
public class Kid {

    private int x = 0;

    private int y = 0;
    
    @Action
    public void walk(){
       //...
    }
}
```

```java
@Order(2)
@Actor
public class Frog {

    private int x = 0;

    private int y = 0;
    
    @Action
    public void jump(){
       //...
    }
}
```

## @ActorAutowired

​		当一个Actor内需要使用其他Actor时，可以将其他Actor作为当前Actor的属性，并使用`@ActorAutowired`标记，Director会对`@ActorAutowired`标记的单例Actor进行依赖注入，无需开发者手动创建实例。例如下所示，将Frog注入到Kid中，在Kid的方法中就可以使用Frog了。

```java
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;

    @ActorAutowired
    private Frog frog;

    @Action
    public void walk(){
        //can use frog
    }
}
```

## @ActorCollectionAutowired

​		当我们希望创建多个相同的Actor时该怎么办呢？由于Actor默认是单例的，如果希望创建能被Director管理的多例Actor，则需要开启多例模式，即`@Actor(ActorType.PROTOTYPE)`。多例Actor不会在Actor注册时立马被创建，而是在依赖注入时，根据注解`@ActorCollectionAutowired(int size)`来创建包含多例Actor的集合。例如下所示，创建20个Frog实例。

​		关于多例Actor的创建和使用，需要注意以下几点：

1. 被创建的Actor必须开启多例模式；
2. `@ActorCollectionAutowired`注解只能作用于属性字段，且为非抽象集合类型；
3. 使用`@ActorCollectionAutowired`必须指定Actor数量（集合大小）；
4. Director会为每个多例Actor管理一个集合，因此，当在多处使用 `@ActorCollectionAutowired`对同一多例Actor进行标记时，注入的都是同一集合。

```java
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;

    @ActorCollectionAutowired(20)
    private ArrayList<Frog> frogs;

    @Action
    public void walk(){
        //can use frogs
    }
}
```

## @Configure

​		`@Configure`注解用于声明当前类为配置类，它和`@Actor`的作用几乎相同，Director会为配置类创建并管理一个单例，以便于在任何地方通过`@ActorAutowired`注入和使用。例如下所示，KidConfigure类是自定义的一个配置类，用于存储Kid相关配置信息，使用`@Configure`注解后，就可以在Kid类中通过`@ActorAutowired`注入和使用了。

```java
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;

    @ActorAutowired
    private KidConfigure kidConfigure;

    @Action
    public void walk(){
        //can use kidConfigure
    }
}
```

此外，通过继承Director提供的BasicConfigure类并调用`setSimulateDuration(int duration)`可以修改仿真时长。

```java
@Configure
public class KidConfigure extends BasicConfigure {

    private int maxStepSize;

    public KidConfigure(){
        setSimulateDuration(100); //reset the SimulateDuration
        maxStepSize = 3;
    }
    public int getMaxStepSize() {
        return maxStepSize;
    }
}
```



## @Init

​		`@Init`注解作用于方法，被标记的方法将在所有Actor实例化后、正式逐帧执行Action前执行，如下图所示。因此，您可以在这一步执行仿真前的初始化工作。

<img src="C:\Users\Jim\AppData\Roaming\Typora\typora-user-images\image-20241007204757817.png" alt="image-20241007204757817" style="zoom:30%;" />

​		例如，我在Kid的`init()`方法中对属性stepSize进行计算和赋值。

```java
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;

    @ActorAutowired
    private KidConfigure kidConfigure;
    
    @Init
    public void init(){
        stepSize = kidConfigure.getMaxStepSize() / 2;
    }

    @Action
    public void walk(){
        //...
    }
}
```

## @Status

​		`@Status`注解作用于字符串属性，用于标记当前Actor的状态。在@Action小节提到`@Action(String status)`，若传入的status非空，则对应的Action为带有状态的Action。具体而言，在每一帧中，只有Actor的状态等于Action的状态时，对应的Action才会被执行。`@Action(String status)`配合`@Status`使用，可以巧妙地实现复杂的Actor状态转移，如下图所示。

<img src="C:\Users\Jim\AppData\Roaming\Typora\typora-user-images\image-20241007212242618.png" alt="image-20241007212242618" style="zoom:30%;" />

​		我们仍然以小孩随机走动为例，只不过增加一项需求：**小孩太胖了，每走一步需要休息5秒钟！**先分析一下小孩的状态转移图（如下所示）。可以看到，小孩在仿真过程中始终处于这两个状态之一，要么“WALKING”，要么“RESTING”。接下来，我们只需要实现这些状态转移方法即可。

<img src="C:\Users\Jim\AppData\Roaming\Typora\typora-user-images\image-20241007213936155.png" alt="image-20241007213936155" style="zoom:26%;" />

代码如下所示。在每一帧中，当Kid的状态为“WALKING”时，会执行`walk()`方法，当Kid的状态为“RESTING”时，会执行`rest()`方法。

```java
@Log(period = 1)
@Order(1)
@Actor
public class Kid {

    @Status
    private String status;

    private int restTime = 5;

    private int x = 0;

    private int y = 0;

    @Init
    public void init(){
        status = "WALKING";
    }

    @Action(status = "WALKING")
    public void walk(){
        int[] numbers = {-1, 0, 1};
        Random random = new Random();
        int randomNumber = numbers[random.nextInt(numbers.length)];// choose one of numbers
        x += randomNumber;
        randomNumber = numbers[random.nextInt(numbers.length)];
        y += randomNumber;
        restTime = 5;
        status = "RESTING"; // transfer to status "RESTING"
    }

    @Action(status = "RESTING")
    public void rest(){
        restTime--;
        if(restTime == 0){
            status = "WALKING"; // transfer to status "WALKING"
        }
    }
}
```

## @Log

​		Director提供了简单的日志管理，您可以在Actor上使用`@Log(int period, String target)`注解来记录当前Actor的属性值快照。支持两个参数配置，period参数必须指定，表示记录周期（每隔period帧记录一次）；target参数为日志输出文件路径（文件不存在会自动创建），默认控制台。此外，如果您想忽略Actor某些属性值时，可以使用`@LogIgnore`对属性进行标记即可。例如下所示，通过`@Log`记录Kid的仿真过程信息到kidLog.log日志文件，忽略stepSize属性。

```java
@Log(period = 1,target = "kidLog.log")
@Order(1)
@Actor
public class Kid {

    private int x;

    private int y;
    
    @LogIgnore
    private int stepSize;

    @Action
    public void walk(){
        int[] numbers = {-1, 0, 1};
        Random random = new Random();
        int randomNumber = numbers[random.nextInt(numbers.length)];// choose one of numbers
        x += randomNumber;
        randomNumber = numbers[random.nextInt(numbers.length)];
        y += randomNumber;
    }
}
```

![image-20241007222328700](C:\Users\Jim\AppData\Roaming\Typora\typora-user-images\image-20241007222328700.png)