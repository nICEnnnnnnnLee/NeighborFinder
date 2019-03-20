# NeighborFinder
一个局域网邻居发现app, 能够列出邻居IP/MAC, 支持备注  

## 有话要说
+ 核心实现搬运自 <https://www.jianshu.com/p/3f68e4681aa6>  
+ 只是自用, 备份一下
+ 原理其实就是cat ARP的缓存表, 而为了刷新整个ARP, 采取了遍历IP 发送UDP包的形式(可以是其它形式, 如ICMP, 主要是要触发地址解析协议, 但综合来看该方法最快)

## 下载
<https://github.com/nICEnnnnnnnLee/NeighborFinder/tree/master/app/release>

## LICENSE
原作并没有LICENSE, 虽然原理并不复杂.  
Apache 2.0

