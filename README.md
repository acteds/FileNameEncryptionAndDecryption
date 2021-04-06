## 加密解密文件名称
### 实现原理
 - 通过修改文件和文件夹名称,并将加密后的名称和原名称放入map,加密后的名称为**key**,原名称为**value**.
 最后导出为json进行存储.
 - 因此,并没有保存文件夹路径结构,只保留了被加密文件夹的**绝对路径**和**相对路径**(相对路径就是被加密文件夹名称).
 - 以及文件夹加密序号尾数,文件加密序号尾数,以上两项可用以`文件名称加密设置序列.bat`.
### 注意事项
 - 使用过程中导致的所有可能的损失本项目概不负责.

### 使用的库
[Apache Commons](http://commons.apache.org/)：Java 工具库集
[fastjson](https://github.com/alibaba/fastjson):alibaba - fastjson
#### 其中`文件名称加密解密.bat`用以通常的加密解密操作
 1. 拖动要加密的文件夹到该bat文件上选择`Y`即可对该文件夹下的所有文件和文件夹加密.
 2. 加密完成后将生成加密后的`JSON加密数据文件`,用以解密.
 3. 拖动json到该bat文件上选择`N`可以进行绝对路径解密操作.

#### 其中`精确加密-json.bat`用以特殊的加密操作
 1. 该文件适用于文件夹已被加密过生成了`JSON加密数据文件`,然后又被解密了,要想返回之前加密的样子即使用该文件.
 2. 与`文件名称加密解密.bat`的加密操作不同,只会加密`JSON加密数据文件`内有的value的文件名称,相当于将名字还原为之前加密的名称.

#### 其中`相对路径解密-json.bat`用以以相对路径解密
 1. 与`文件名称加密解密.bat`的解密,不同的是,该文件以相对路径进行解密.
 2. 要使用该文件,则需要将`JSON加密数据文件`和待解密文件夹放在同一个父文件夹下,并且待解密文件夹名称也要与加密时一致.
 3. 拖动`JSON加密数据文件`到该文件上即可完成解密.

#### 其中`文件名称加密解密.bat`用以特殊的加密操作.
 1. 该文件将需要用户指定文件起始序号和文件夹起始序号,可以用在在**已加密的文件夹**下追加加密的文件.
 2. 具体用法为:在其他地方新建文件夹,名称为之前**已加密的文件夹**的名称,将待加密的文件或文件夹放入该文件夹,拖动动该文件夹到此文件上,输入之前加密文件名称上的文件夹加密序号尾数,文件加密序号尾数,完成加密.
 3. 将该文件夹下的文件和文件夹移动到要追加加密文件的目录内.
 4. 合并之前文件夹的`JSON加密数据文件`和刚刚生成的`JSON加密数据文件`,并手动更新序号尾数.
 5. 具体细节为:
 5. 合并过程前中,需要删除刚刚生成的`JSON加密数据文件`内的**绝对路径**和**相对路径**
 6. 更新之前的`JSON加密数据文件`内的**最后的文件夹序列为**和**最后的文件夹序列为**两项数据为刚刚生成的`JSON加密数据文件`.
 7. 最后完成两个JSON文件的合并.
 8. **操作难度比较大,不建议使用,可以先解密然后追加文件再加密**.

 ### License
```
                Copyright 2021 by acteds

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
