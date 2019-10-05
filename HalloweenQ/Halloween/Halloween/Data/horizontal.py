import cv2
import matplotlib.pyplot as plt
def show(image):
    plt.imshow(image)
    plt.axis('off')
    plt.show()

image = cv2.imread("sprite185.png", cv2.IMREAD_UNCHANGED)

image = cv2.flip(image,0)
show(image)

cv2.imwrite("sprUpside.png", image)