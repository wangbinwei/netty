## NIO通信   会问Netty实现，优势是什么？

## NIO介绍

NIO同步非阻塞: 服务器实现模式为**一个线程可以处理多个请求(连接)**，客户端发送的连接请求都会注册到**多路复用器selector**上，多路复用器轮询到连接有IO请求就进行处理，JDK1.4开始引入。

**应用场景：** NIO方式适用于连接数目多且连接比较短（轻操作） 的架构， 比如聊天服务器， 弹幕系统， 服务器间通讯，编程比较复杂 

I/O事件

```java
OP_ACCEPT 接收连接
OP_WRITE  写事件
...
```

1、并发高 2、传输快 3、封装好



#### 补充：

#### InetAddress

一、**InetAddress**:类的主要作用是封装IP及DNS，因为这个类没有构造器，所以我们要用他的一些方法来获得对象常用的有

1、使用getLocalHost方法为InetAddress创建对象；

2、根据域名得到InetAddress对象

3、根据ip得到InetAddress对象

#### **InetSocketAddress**

二、**InetSocketAddress**类主要作用是封装端口 他是在在InetAddress基础上加端口，但它是有构造器的。具体的一些方法可以去帮助文档查看。



### **1、Netty为什么高并发**

原因是Netty的Selector，在NIO中，当一个Socket建立好了，Thread不会阻塞这个去接受这个Socket，而是将这个请求交给Selector，selector会不断的遍历所有的Socket一旦有一个Socket建立完成，他会通知Thread，然后Thread处理完数据再返回给客户端——这个过程是不阻塞的，这样就能让一个Thread处理更多的请求了。

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\adfd80e56956469f87a8138e05888352\clipboard.png)

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\5be7139cdd2c468cbdabacfaf94be624\clipboard.png)

## NIO的处理流程

### **2、Netty为什么传输快？**

Netty的传输快其实也是依赖了NIO的一个特性——*零拷贝*。我们知道，Java的内存有堆内存、栈内存和字符串常量池等等，其中堆内存是占用内存空间最大的一块，也是Java对象存放的地方，一般我们的数据如果需要从IO读取到堆内存，中间需要经过Socket缓冲区，也就是说一个数据会被拷贝两次才能到达他的的终点，如果数据量大，就会造成不必要的资源浪费。

Netty针对这种情况，使用了NIO中的另一大特性——零拷贝，当他需要接收数据的时候，他会在堆内存之外开辟一块内存，数据就直接从IO读到了那块内存中去，在netty里面通过ByteBuf可以直接对这些数据进行直接操作，从而加快了传输速度。

传统的数据拷贝：平时写一个服务端程序，文件发送，一般先查询自己缓存里面有没有，如果没有则到内存上找这一过程一般通过DMA来完成，将内核缓存区的内容拷贝到应用程序缓冲区中去，接下来write系统调用用户程序缓存区上的内容拷贝网络堆栈相关的内核缓冲区中，最后socket再把内核缓冲区的内容发送到网卡。

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\dfaf39f82dbf47a79b49d3c9de12524e\clipboard.png)

传统数据拷贝

从上图中可以看出，共产生了四次数据拷贝，即使使用了DMA来处理了与硬件的通讯，CPU仍然需要处理两次数据拷贝，与此同时，在用户态与内核态也发生了多次上下文切换，无疑也加重了CPU负担。

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\04abfa5b273443a98718ec6a79b8d129\clipboard.png)

零拷贝

应用程序调用mmap()，磁盘上的数据会通过DMA被拷贝的内核缓冲区，接着操作系统会把这段内核缓冲区与应用程序共享，这样就不需要把内核缓冲区的内容往用户空间拷贝。应用程序再调用write(),操作系统直接将内核缓冲区的内容拷贝到socket缓冲区中，这一切都发生在内核态，最后，socket缓冲区再把数据发到网卡去。

### 3、Netty封装好

缓冲区Buffer

通道Channel

多路复用器Selector  不断轮询Channel,如果Channel以就绪则就可以读or写其中消息，SelectorKey	

Netty   NioEventLoop  ---》thread  建立连接、读写客户端的数据

两个线程：监听客户端的连接、客户端的读写

Curl:用于请求Web服务器。他的名字是Client 的URL工具的意思

Channel 简单对一条连接的封装

Channel  ---有个Pinpline ，依次处理其中的Handler  

![image-20210725235014430](C:\Users\Think\AppData\Roaming\Typora\typora-user-images\image-20210725235014430.png)

![img](C:\Users\Think\AppData\Roaming\Typora\typora-user-images\image-20210726082620537.png)

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\5a042ec0bd4b49a58638ec97c8fee7af\clipboard.png)

  

Pipeline 逻辑处理链

ChannelHandler 处理逻辑

ByteBuf 基于BuyeBuf来操作

伪异步I/O通信 

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\1ec070d74b0842e7af94cb4e3b94a762\clipboard.png)

AIO 连接注册读写事件和回调函数

读写方法异步

主动通知程序

BIO  Blocking I/O

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\4d8236a7f046450da724575004851b20\clipboard.png)

### 4、Netty的生命周期

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\80bc35c4f78241a59eca389407d877e0\clipboard.png)



# Netty的Reactor线程模型

![image-20210719131932565](C:\Users\Think\AppData\Roaming\Typora\typora-user-images\image-20210719131932565.png)

```java
EventLoop mianGroup = new NioEventLoop //主线程，负责连接。 subGroup负责处理hanle建立服务器  
ServerBootStrap server = new ServerBootStrap();

server.group(mianGroup, subGroup)

.channel(NioServerChannel.class)

.chileHandler(自己定于) ;

//启动server,并设置8080的端口号

ChannelFuture  future = server.bind(8080).sync();

future.channel().closeFuture().sync();

ServerInitializer ChannelPipeline pipeline = socketChannel.pipiline();// 初始化

pipeline.addLast();
```





```java
EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)EventLoopGroup workerGroup = new NioEventLoopGroup();try {
    ServerBootstrap b = new ServerBootstrap(); // (2)
    b.group(bossGroup, workerGroup)　　// (3)
     .channel(NioServerSocketChannel.class) // (4)     .handler(new LoggingHandler())    // (5)
     .childHandler(new ChannelInitializer<SocketChannel>() { // (6)
         @Override
         public void initChannel(SocketChannel ch) throws Exception {
             ch.pipeline().addLast(new DiscardServerHandler());
         }
     })
     .option(ChannelOption.SO_BACKLOG, 128)          // (7)
     .childOption(ChannelOption.SO_KEEPALIVE, true); // (8)
    
     // Bind and start to accept incoming connections.
     ChannelFuture f = b.bind(port).sync(); // (9)
    
     // Wait until the server socket is closed.
     // In this example, this does not happen, but you can do that to gracefully
     // shut down your server.
     f.channel().closeFuture().sync();
} finally {
    workerGroup.shutdownGracefully();
    bossGroup.shutdownGracefully();
}
```



上面这段代码展示了服务端的一个基本步骤：

(1)、 初始化用于Acceptor的主"线程池"以及用于I/O工作的从"线程池"；
(2)、 初始化ServerBootstrap实例， 此实例是netty服务端应用开发的入口，也是本篇介绍的重点， 下面我们会深入分析；
(3)、 通过ServerBootstrap的group方法，设置（1）中初始化的主从"线程池"；
(4)、 指定通道channel的类型，由于是服务端，故而是NioServerSocketChannel；
(5)、 设置ServerSocketChannel的处理器（此处不详述，后面的系列会进行深入分析）
(6)、 设置子通道也就是SocketChannel的处理器， 其内部是实际业务开发的"主战场"（此处不详述，后面的系列会进行深入分析）
(7)、 配置ServerSocketChannel的选项
(8)、 配置子通道也就是SocketChannel的选项
(9)、 绑定并侦听某个端口

客户端

```java
public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // (1)
        
        try {
            Bootstrap b = new Bootstrap(); // (2)
            b.group(workerGroup); // (3)
            b.channel(NioSocketChannel.class); // (4)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (5)
            b.handler(new ChannelInitializer<SocketChannel>() { // (6)
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (7)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
```

(1)、 初始化用于连接及I/O工作的"线程池"；
(2)、 初始化Bootstrap实例， 此实例是netty客户端应用开发的入口，也是本篇介绍的重点， 下面我们会深入分析；
(3)、 通过Bootstrap的group方法，设置（1）中初始化的"线程池"；
(4)、 指定通道channel的类型，由于是客户端，故而是NioSocketChannel；
(5)、 设置SocketChannel的选项（此处不详述，后面的系列会进行深入分析）；
(6)、 设置SocketChannel的处理器， 其内部是实际业务开发的"主战场"（此处不详述，后面的系列会进行深入分析）；
(7)、 连接指定的服务地址；





```java
//进程流测试
bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))
```



## Netty注解

- netty的channelHandler @ChannelHandler.Sharable 添加这个注解，他在生命周期中就是以单例模式中存在

计算机组成原理学的差不多

psk: Public Sharing Key

refactor 重构 

maven是一个项目管理工具，包含pom: Project Object Model项目对象模型



### ByteBuf 

### Netty的 编解码

编解码器是继承Handler,Handler中有InBoundHandler和OutBoundHandler两个类

```java
//向pipeline加入解码器
pipeline.addLast("decoder", new StringDecoder());
//向pipeline加入编码器
pipeline.addLast("encoder", new StringEncoder());
```

![image-20210817210450285](G:\技术积累\Netty学习.assets\image-20210817210450285.png)

![https://note.youdao.com/yws/public/resource/bbc5cfef81b2951d769807ed748343b9/xmlnote/C48453E100EB42049B7349168EA17EC1/85277](G:\技术积累\Netty学习.assets\85277)

**模型解释：** 

1) Netty 抽象出两组线程池BossGroup和WorkerGroup，BossGroup专门负责接收客户端的连接, WorkerGroup专 门负责网络的读写 

2) BossGroup和WorkerGroup类型都是NioEventLoopGroup 

3) NioEventLoopGroup 相当于一个事件循环**线程组**, 这个组中含有多个事件循环线程 ， 每一个事件循环线程是 NioEventLoop 

4) 每个NioEventLoop都有一个selector , 用于监听注册在其上的socketChannel的网络通讯 

5) 每个Boss NioEventLoop线程内部循环执行的步骤有 3 步 

- 处理accept事件 , 与client 建立连接 , 生成 NioSocketChannel 

- 将NioSocketChannel注册到某个worker NIOEventLoop上的selector 

- 处理任务队列的任务 ， 即runAllTasks 

6) 每个worker NIOEventLoop线程循环执行的步骤 

- 轮询注册到自己selector上的所有NioSocketChannel 的read, write事件 

- 处理 I/O 事件， 即read , write 事件， 在对应NioSocketChannel 处理业务 

- runAllTasks处理任务队列TaskQueue的任务 ，一些耗时的业务处理一般可以放入TaskQueue中慢慢处 

  理，这样不影响数据在 pipeline 中的流动处理 

7) 每个worker NIOEventLoop处理NioSocketChannel业务时，会使用 pipeline (管道)，管道中维护了很多 handler 

处理器用来处理 channel 中的数据 



简单易懂的图，类似这种

![img](G:\技术积累\Netty学习.assets\20200905153733867.png)

### Netty心跳检测机制

在TCP长连接中，C/S定期发送特殊数据包，通知对方自己还在线

异步模型通过大量的线程

IdleStateHandler 心跳包机制，Netty底层帮你实现心跳报

不同客户端的心跳不一样

AWT

B/S 



# Netty01——深入理解IO模型与Epoll

## **IO模型**

IO模型就是说用什么样的通道进行数据的发送和接收，Java共支持3种网络编程IO模式：**BIO，NIO，AIO**

### **BIO(Blocking IO)**

同步阻塞模型，一个客户端连接对应一个处理线程

![img](G:\有道云\qq2D1D5CB92B2C0FF061B3D3F82DA32CD1\9e69331a6d7a4e55aaa7ea4e1da461dd\clipboard.png)

BIO代码示例：

```java
package com.tuling.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        while (true) {
            System.out.println("等待连接。。");
            //阻塞方法一个阻塞,accept()也是一个阻塞
            Socket clientSocket = serverSocket.accept();
            System.out.println("有客户端连接了。。");
            //handler(clientSocket);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        handler(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    //处理读
    private static void handler(Socket clientSocket) throws IOException {
        byte[] bytes = new byte[1024];
        System.out.println("准备read。。");
        //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
        int read = clientSocket.getInputStream().read(bytes);
        System.out.println("read完毕。。");
        if (read != -1) {
            System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
        }
        clientSocket.getOutputStream().write("HelloClient".getBytes());
        clientSocket.getOutputStream().flush();
    }
}
```

```java
//客户端代码
public class SocketClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9000);
        //向服务端发送数据
        socket.getOutputStream().write("HelloServer".getBytes());
        socket.getOutputStream().flush();
        System.out.println("向服务端发送数据结束");
        byte[] bytes = new byte[1024];
        //接收服务端回传的数据
        socket.getInputStream().read(bytes);
        System.out.println("接收到服务端的数据：" + new String(bytes));
        socket.close();
    }
}
```

上述服务端代码中一共有两处阻塞，一处是accept()阻塞，一处是read()阻塞。第一个accept()阻塞采用多线程的方式来解决。

**缺点：**

1、IO代码里read操作是阻塞操作，如果连接不做数据读写操作会导致线程阻塞，浪费资源

2、如果线程很多，会导致服务器线程太多，压力太大，比如C10K问题

**应用场景：**

BIO 方式适用于连接数目比较小且固定的架构， 这种方式对服务器资源要求比较高，  但程序简单易理解。



### **NIO(Non Blocking IO)**

同步非阻塞，服务器实现模式为**一个线程可以处理多个请求(连接)**，客户端发送的连接请求都会注册到**多路复用器selector**上，多路复用器轮询到连接有IO请求就进行处理，JDK1.4开始引入。

**应用场景：**

NIO方式适用于连接数目多且连接比较短（轻操作） 的架构， 比如聊天服务器， 弹幕系统， 服务器间通讯，编程比较复杂

#### NIO非阻塞代码示例：

```java
package com.tuling.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NioServer {

    // 保存客户端连接
    static List<SocketChannel> channelList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {

        // 创建NIO ServerSocketChannel,与BIO的serverSocket类似
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);
        System.out.println("服务启动成功");

        while (true) {
            // 非阻塞模式accept方法不会阻塞，否则会阻塞
            // NIO的非阻塞是由操作系统内部实现的，底层调用了linux内核的accept函数
            SocketChannel socketChannel = serverSocket.accept();
            if (socketChannel != null) { // 如果有客户端进行连接
                System.out.println("连接成功");
                // 设置SocketChannel为非阻塞
                socketChannel.configureBlocking(false);
                // 保存客户端连接在List中
                channelList.add(socketChannel);
            }
            // 遍历连接进行数据读取
            Iterator<SocketChannel> iterator = channelList.iterator();
            while (iterator.hasNext()) {
                SocketChannel sc = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                // 非阻塞模式read方法不会阻塞，否则会阻塞
                int len = sc.read(byteBuffer);
                // 如果有数据，把数据打印出来
                if (len > 0) {
                    System.out.println("接收到消息：" + new String(byteBuffer.array()));
                } else if (len == -1) { // 如果客户端断开，把socket从集合中去掉
                    iterator.remove();
                    System.out.println("客户端断开连接");
                }
            }
        }
    }
}
```

总结：如果连接数太多的话，会有大量的无效遍历，假如有10000个连接，其中只有1000个连接有写数据，但是由于其他9000个连接并没有断开，我们还是要每次轮询遍历一万次，其中有十分之九的遍历都是无效的，这显然不是一个让人很满意的状态。



#### NIO引入**多路复用器**代码示例：

基于事件响应

```java
package com.tuling.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        // 创建NIO ServerSocketChannel
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞
        serverSocket.configureBlocking(false);
        // 打开Selector处理Channel，即创建epoll
        Selector selector = Selector.open();
        // 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务启动成功");

        while (true) {
            // 阻塞等待需要处理的事件发生，监听这个channel有没有事件，有事件就跳出来
            selector.select();

            // 获取selector中注册的全部事件的 SelectionKey 实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            // 遍历SelectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if (key.isReadable()) {  // 如果是OP_READ事件，则进行读取和打印
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = socketChannel.read(byteBuffer);
                    // 如果有数据，把数据打印出来
                    if (len > 0) {
                        System.out.println("接收到消息：" + new String(byteBuffer.array()));
                    } else if (len == -1) { // 如果客户端断开连接，关闭Socket
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }
                //从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }
        }
    }
}
```

NIO 有三大核心组件： **Channel(通道)， Buffer(缓冲区)，Selector(多路复用器)**

1、channel 类似于流，每个 channel 对应一个 buffer缓冲区，buffer 底层就是个数组

2、channel 会注册到 selector 上，由 selector 根据 channel 读写事件的发生将其交由某个空闲的线程处理

3、NIO 的 Buffer 和 channel 都是既可以读也可以写

![img](G:\技术积累\Netty学习.assets\clipboard-1644890596273.png)

NIO底层在JDK1.4版本是用linux的内核函数select()或poll()来 实现，跟上面的NioServer代码类似，selector每次都会轮询所有的sockchannel看下哪个channel有读写事件，有的话就处理， 没有就继续遍历，JDK1.5开始引入了epoll基于事件响应机制来优化NIO。

### Epoll详解

NioSelectorServer 代码里如下几个方法非常重要，我们从Hotspot与Linux内核函数级别来理解下

```java
Selector.open()  //创建多路复用器  epoll_create()
socketChannel.register(selector, SelectionKey.OP_READ)  //将channel注册到多路复用器上 
selector.select()  //阻塞等待需要处理的事件发生 epoll_wait()一旦有事件就退出等待 ,选取epoll_ctl() 调用
```

![img](G:\技术积累\Netty学习.assets\nio底层epoll实现源码剖析 (2).jpg)

总结：NIO整个调用流程就是Java调用了操作系统的内核函数来创建Socket，**获取到Socket的文件描述符**，再创建一个Selector对象，对应操作系统的Epoll描述符，将获取到的Socket连接的文件描述符的事件**绑定到Selector对应的Epoll、文件描述符上**，进行事件的异步通知，这样就实现了使用一条线程，并且不需要太多的无效的遍历，将事件处理交给了操作系统内核(操作系统中断程序实现)，大大提高了效率。

**Epoll函数详解**

```c
int epoll_create(int size);
```

创建一个epoll实例，并返回一个非负数作为文件描述符，用于对epoll接口的所有后续调用。参数size代表可能会容纳size个描述符，但size不是一个最大值，只是提示操作系统它的数量级，现在这个参数基本上已经弃用了。

```c
int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
```

使用文件描述符epfd引用的epoll实例，对目标文件描述符fd执行op操作。

参数epfd表示epoll对应的文件描述符，参数fd表示socket对应的文件描述符。

参数op有以下几个值：

```c
EPOLL_CTL_ADD：注册新的fd到epfd中，并关联事件event；

EPOLL_CTL_MOD：修改已经注册的fd的监听事件；

EPOLL_CTL_DEL：从epfd中移除fd，并且忽略掉绑定的event，这时event可以为null；
```

参数event是一个结构体

```c
    struct epoll_event {
	    __uint32_t   events;      /* Epoll events */
	    epoll_data_t data;        /* User data variable */
	};
	
	typedef union epoll_data {
	    void        *ptr;
	    int          fd;
	    __uint32_t   u32;
	    __uint64_t   u64;
	} epoll_data_t;

```

events有很多可选值，这里只举例最常见的几个：

```shell
EPOLLIN ：表示对应的文件描述符是可读的；

EPOLLOUT：表示对应的文件描述符是可写的；

EPOLLERR：表示对应的文件描述符发生了错误；
```

成功则返回0，失败返回-1

```c
int epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout);
```

等待文件描述符epfd上的事件。

epfd是Epoll对应的文件描述符，events表示调用者所有可用事件的集合，maxevents表示最多等到多少个事件就返回，timeout是超时时间。

#### I/O多路复用底层

I/O多路复用底层主要用的Linux 内核·函数（select，poll，epoll）来实现，windows不支持epoll实现，windows底层是基于winsock2的select函数实现的(不开源)

|              | **select**                                   | **poll**                                 | **epoll(jdk 1.5及以上)**                                     |
| ------------ | -------------------------------------------- | ---------------------------------------- | ------------------------------------------------------------ |
| **操作方式** | 遍历                                         | 遍历                                     | 回调                                                         |
| **底层实现** | 数组                                         | 链表                                     | 哈希表                                                       |
| **IO效率**   | 每次调用都进行**线性遍历**，时间复杂度为O(n) | 每次调用都进行线性遍历，时间复杂度为O(n) | 事件通知方式，每当有IO事件就绪，系统注册的回调函数就会被调用，时间复杂度O(1) |
| **最大连接** | 有上限1024                                   | 无上限                                   | 无上限                                                       |