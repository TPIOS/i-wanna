# total map is 800x608, one block is 32x32
# any pairs of block should at least have 32 pixel shift 
# to make map sparse
import random
def checkPos(x, y, bigMap, blockSize):
    # print(x, y)
    if x % blockSize != 0:
        return False
    if y % blockSize != 0:
        return False
    if bigMap[x][y] == 1:
        return False
    return True

outputFile = "2024_random_5.map"
f = open(outputFile, "w")
f.write(" 1.030000\n")
f.write("random_1\n")
f.write("Jushi_Gen\n")
Nblock = 40
blockSize = 32
mapWidth = 800
mapHeight = 608
okMap = [[ 0 for i in range(mapHeight) ] for j in range(mapWidth) ]
for i in range(Nblock):
    x = random.randint(0, mapWidth-blockSize)
    y = random.randint(0, mapHeight-blockSize)
    while not checkPos(x, y, okMap, blockSize):
        x = random.randint(0, mapWidth-blockSize)
        y = random.randint(0, mapHeight-blockSize)
    for j in range(x-2*blockSize, x+2*blockSize):
        for k in range(y-2*blockSize, y+2*blockSize):
            if j < 0 or j >= mapWidth or k < 0 or k >= mapHeight:
                continue
            okMap[j][k] = 1
    f.write(" " + str(x) + " " + str(y) + " 2")
f.write(" 128 512 3")
f.close()