# NeighborFinder
一个局域网邻居发现app, 能够列出邻居IP/MAC, 支持备注  

## 有话要说
+ 核心实现搬运自 <https://www.jianshu.com/p/3f68e4681aa6>  
+ 只是自用, 备份一下
+ 原理其实就是cat ARP的缓存表(当然也可以使用其它命令,如ip neighbor), 而为了刷新整个ARP, 采取了遍历IP 发送UDP包的形式(可以是其它形式, 如ICMP, 主要是要触发地址解析协议, 但综合来看该方法最快)
+ 当局域网有新成员加入时, 刷新能够马上反应; 但成员退出时, 则必须等到缓存超时. 因为权限问题, 无法执行清除操作. 为此, 提供了PING测试功能; 同时另一个解决方案是关开飞行模式

## 预览
<a target="_blank" rel="noopener noreferrer" href="https://github.com/nICEnnnnnnnLee/NeighborFinder/raw/master/view/screenshot-1.png">
<img src="https://github.com/nICEnnnnnnnLee/NeighborFinder/raw/master/view/screenshot-1.png" alt="" style="max-width: 360px;">
</a>
<a target="_blank" rel="noopener noreferrer" href="https://github.com/nICEnnnnnnnLee/NeighborFinder/raw/master/view/screenshot-2.png">
<img src="https://github.com/nICEnnnnnnnLee/NeighborFinder/raw/master/view/screenshot-2.png" alt="" style="max-width: 360px;">
</a>

## 下载
<https://github.com/nICEnnnnnnnLee/NeighborFinder/tree/master/app/release>

## LICENSE
原作并没有LICENSE, 虽然原理并不复杂.  
Apache 2.0

