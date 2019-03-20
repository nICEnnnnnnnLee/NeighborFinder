# NeighborFinder
一个免Root实现类似PC端Host功能的app 

## 有话要说
+ 核心实现搬运自 <https://www.jianshu.com/p/3f68e4681aa6>  
+ 只是自用, 备份一下
+ 原理其实就是cat ARP的缓存表, 而为了刷新整个ARP, 采取了遍历IP 发送UDP包的形式(可以是其它形式, 如ICMP, 主要是要触发地址解析协议, 但综合来看该方法最快)

## LICENSE
原作并没有LICENSE, 虽然原理并不复杂.  

