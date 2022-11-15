# WordCount

```mermaid
flowchart LR
	subgraph A[输入文件]
		direction LR
		483280newsML.txt
		483283newsML.txt 
		.etc
	end
	A --< DocId, DocContent >--> B(Map)
	a[< Term1, 1 >]
	b[< Term2, 1 >]
	c[< Term3, 1 >]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d[< Term1, countNum ><br>< Term2, countNum ><br>< Term3, countNum >]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

# SequenceFile

```mermaid
flowchart LR
	subgraph A[输入文件]
		direction LR
		483280newsML.txt
		483283newsML.txt 
		.etc
	end
	A --< DocId, DocContent >--> B(Map)
	a["< Class@DocName1, DocContent >"]
	b[< Class&#64DocName2, DocContent >]
	c[< Class&#64DocName3, DocContent >]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d["< Class@DocName1, DocContent ><br>< Class&#64DocName2, DocContent ><br>< Class&#64DocName3, DocContent >"]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

# p(class)统计各类别文档数目

```mermaid
flowchart LR
	g["< Class@DocName1, DocContent ><br>< Class&#64DocName2, DocContent ><br>< Class&#64DocName3, DocContent >"]
	subgraph A[输入文件]
		direction LR
		g
	end
	A --"< Class@DocName, DocContent >"--> B(Map)
	a[< Class1, 1 >]
	b[< Class2, 1 >]
	c[< Class3, 1 >]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d["< Class1, countNum ><br>< Class2, countNum ><br>< Class3, countNum >"]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

# p(term|class)统计各类别各单词出现次数

```mermaid
flowchart LR
	g["< Class@DocName1, DocContent ><br>< Class&#64DocName2, DocContent ><br>< Class&#64DocName3, DocContent >"]
	subgraph A[输入文件]
		direction LR
		g
	end
	A --"< Class@DocName, DocContent >"--> B(Map)
	a["< Class@Term1, 1 >"]
	b["< Class@Term2, 1 >"]
	c["< Class@Term3, 1 >"]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d["< Class@Term1, countNum ><br>< Class@Term2, countNum ><br>< Class@Term3, countNum >"]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

# p(term|class)统计各类别单词总数目

```mermaid
flowchart LR
	g["< Class@Term1, countNum ><br>< Class@Term2, countNum ><br>< Class@Term3, countNum >"]
	subgraph A[输入文件]
		direction LR
		g
	end
	A --"< Class@Term, countNum >"--> B(Map)
	a["< Class1, countNum >"]
	b["< Class2, countNum >"]
	c["< Class3, countNum >"]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d["< Class1, countNum ><br>< Class2, countNum ><br>< Class3, countNum >"]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

# 对测试集使用贝叶斯公式

$$
\begin{aligned} 
p(class|doc)&=\frac{p(doc|class)p(class)}{p(doc)}∝p(doc|class)p(class) \\
&=p(class)\prod_{t_k∈T=\{t_1,t_2,……,t_n\}}{p(t_k|class)} \\
\end{aligned}
$$

***floating point underflow*（浮点下溢）：取 *log***
$$
\begin{aligned} 
log(p(class|doc))&=log(p(class))+\sum_{t_k∈T=\{t_1,t_2,……,t_n\}}{log(p(t_k|class))} \\
&
\end{aligned}
$$
**先验概率 *Prior*：**
$$
p(class)=\frac{N_c}{N} \\
N_c:number\ of\ docs\ in\ class\ c \\
N:total\ number\ of\ docs
$$
**条件概率 *Conditional Probabilities*：**
$$
\begin{aligned} 
p(t|class)&=\frac{t在类型为class的文档中出现的次数}{在类型为class的文档中出现的term的总数} \\
&=\frac{T_{ct}}{\sum_{t'\in V}T_{ct'}}
\end{aligned}
$$
**存在 term 未出现时，其条件概率为 0，*log0* 无法计算：加一平滑：**
$$
\begin{aligned} 
p(t|class)&=\frac{T_{ct}+1}{\sum_{t'\in V}{(T_{ct'}+1)}} \\
&=\frac{T_{ct}+1}{(\sum_{t'\in V}{T_{ct'}})+B} \\
&B:the\ number\ of\ different\ words 
\end{aligned}
$$
**取最优：**
$$
c_{map}=arg\ max\ [log\frac{N_c}{N}+\sum_{1\leq k\leq n}{\frac{T_{ct_k}+1}{(\sum_{t'\in V}{T_{ct'}})+B}}]
$$

```mermaid
flowchart LR
	g["< Class@DocName1, DocContent ><br>< Class&#64DocName2, DocContent ><br>< Class&#64DocName3, DocContent >"]
	subgraph A[输入文件]
		direction LR
		g
	end
	A --"< Class@DocName, DocContent >"--> B(Map)
	a["< Class1@DocName, Class1@Probability >"]
	b["< Class1@DocName, Class2@Probability  >"]
	c["< Class2@DocName, Class1@Probability  >"]
	subgraph C[context]
		direction LR
		a
		b
		c
	end
	B --> C
	C --> D(Reduce)
	d["< Class1@DocName, Class1@Probability ><br>< Class2@DocName, Class1@Probability  >"]
	subgraph E[context]
		direction LR
		d
	end
	D --> E
```

