import tensorflow as tf
import cv2
import numpy as np
import os

# 加载训练好的模型
def load_model(model_path='handwriting_model.h5'):
    if os.path.exists(model_path):
        model = tf.keras.models.load_model(model_path)
        print("模型加载成功")
        return model
    else:
        print(f"模型文件 {model_path} 不存在，请先运行训练程序")
        return None

# 预测手写数字
def predict_digit(model, image_path):
    # 读取图像
    image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    
    if image is None:
        print(f"无法读取图像 {image_path}")
        return None
    
    # 调整图像大小为28x28
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

# 主函数
def main():
    # 加载模型
    model = load_model()
    if model is None:
        return
    
    # 示例：预测测试集中的几个图像
    # 注意：这里我们使用MNIST测试集中的图像作为示例
    from tensorflow import keras
    (_, _), (x_test, y_test) = keras.datasets.mnist.load_data()
    
    # 预测前5个测试图像
    for i in range(5):
        # 展平图像并添加批次维度
        test_image = x_test[i].reshape(1, 28, 28)
        
        # 进行预测
        prediction = model.predict(test_image)
        predicted_digit = np.argmax(prediction)
        confidence = np.max(prediction)
        
        print(f"图像 {i+1}: 预测数字 = {predicted_digit}, 实际数字 = {y_test[i]}, 置信度 = {confidence:.4f}")

if __name__ == "__main__":
    main()