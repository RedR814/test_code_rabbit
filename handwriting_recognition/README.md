# 手写字识别模块

这是一个使用TensorFlow和Keras实现的手写字识别系统，基于MNIST数据集训练。

## 功能特性

- 使用深度学习模型识别手写数字（0-9）
- 基于MNIST数据集训练
- 提供模型训练和预测功能

## 依赖安装

```bash
pip install -r requirements.txt
```

## 文件说明

- `handwriting_recognition.py`: 主要的训练和评估代码
- `predict.py`: 使用训练好的模型进行预测的示例代码
- `requirements.txt`: Python依赖包列表

## 使用方法

### 1. 训练模型

```bash
python handwriting_recognition.py
```

这将：
- 下载MNIST数据集
- 训练一个简单的神经网络模型
- 保存训练好的模型为`handwriting_model.h5`
- 显示训练过程和结果

### 2. 使用模型进行预测

```bash
python predict.py
```

这将：
- 加载训练好的模型
- 对测试集中的图像进行预测
- 显示预测结果和置信度

## 模型架构

- 输入层: 28x28像素的灰度图像
- 展平层: 将图像展平为784个像素值
- 全连接层: 128个神经元，ReLU激活函数
- Dropout层: 防止过拟合，丢弃率0.2
- 输出层: 10个神经元，Softmax激活函数（对应0-9数字）

## 准确率

在MNIST测试集上，该模型通常能达到约97%的准确率。