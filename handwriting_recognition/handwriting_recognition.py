import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
import numpy as np
import matplotlib.pyplot as plt
import cv2
import os

# 加载MNIST数据集
def load_data():
    (x_train, y_train), (x_test, y_test) = keras.datasets.mnist.load_data()
    # 数据预处理
    x_train = x_train.astype("float32") / 255.0
    x_test = x_test.astype("float32") / 255.0
    return (x_train, y_train), (x_test, y_test)

# 创建模型
def create_model():
    model = keras.Sequential([
        layers.Flatten(input_shape=(28, 28)),  # 将28x28的图像展平为一维
        layers.Dense(128, activation='relu'),  # 全连接层，128个神经元
        layers.Dropout(0.2),                   # Dropout层，防止过拟合
        layers.Dense(10, activation='softmax') # 输出层，10个类别
    ])
    return model

# 训练模型
def train_model(model, x_train, y_train, x_test, y_test):
    model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])
    
    # 训练模型
    history = model.fit(x_train, y_train,
                        epochs=5,
                        validation_data=(x_test, y_test))
    return history

# 评估模型
def evaluate_model(model, x_test, y_test):
    test_loss, test_acc = model.evaluate(x_test, y_test, verbose=2)
    print(f'\n测试准确率: {test_acc:.4f}')

# 预测单个图像
def predict_digit(model, image):
    # 确保图像是28x28的灰度图
    if image.shape != (28, 28):
        image = cv2.resize(image, (28, 28))
    
    # 转换为浮点数并归一化
    image = image.astype("float32") / 255.0
    
    # 展平图像并添加批次维度
    image = image.reshape(1, 28, 28)
    
    # 进行预测
    prediction = model.predict(image)
    predicted_digit = np.argmax(prediction)
    confidence = np.max(prediction)
    
    return predicted_digit, confidence

# 显示训练历史
def plot_history(history):
    # 绘制训练准确率和验证准确率
    plt.figure(figsize=(12, 4))
    
    plt.subplot(1, 2, 1)
    plt.plot(history.history['accuracy'], label='训练准确率')
    plt.plot(history.history['val_accuracy'], label='验证准确率')
    plt.title('模型准确率')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.legend()
    
    plt.subplot(1, 2, 2)
    plt.plot(history.history['loss'], label='训练损失')
    plt.plot(history.history['val_loss'], label='验证损失')
    plt.title('模型损失')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend()
    
    plt.tight_layout()
    plt.show()

# 主函数
def main():
    print("加载数据...")
    (x_train, y_train), (x_test, y_test) = load_data()
    
    print("创建模型...")
    model = create_model()
    model.summary()
    
    print("训练模型...")
    history = train_model(model, x_train, y_train, x_test, y_test)
    
    print("评估模型...")
    evaluate_model(model, x_test, y_test)
    
    # 保存模型
    model.save('handwriting_model.h5')
    print("模型已保存为 'handwriting_model.h5'")
    
    # 显示训练历史图表
    plot_history(history)
    
    # 示例预测
    print("\n进行示例预测...")
    sample_image = x_test[0]
    predicted_digit, confidence = predict_digit(model, sample_image)
    print(f"预测数字: {predicted_digit}, 置信度: {confidence:.4f}")
    print(f"实际数字: {y_test[0]}")

if __name__ == "__main__":
    main()