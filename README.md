# 自适应扩展加载机制

@Adaptive(int index, String default)

index代表方法参数列表中下标index的参数指代了本次扩展的名称，default则是如果没有指代的默认扩展名称，如果都没有，则使用@SPI注解的扩展名称，如果还是没有，则使用第一个扩展。



