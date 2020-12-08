import os, math 
from PIL import Image 
import datetime
def circle(pictureName, outputName): 
    ima = Image.open(pictureName).convert("RGBA")
    size = ima.size
    print(size)
    r2 = min(size[0], size[1]) 
    r3 = r2//2
    imb = Image.new('RGBA', (r3*2, r3*2),(255,255,255,0)) 
    pima = ima.load() 
    pimb = imb.load() 
    r = float(r2/2) 
    for i in range(r2): 
        for j in range(r2): 
            lx = abs(i-r)
            ly = abs(j-r)
            l = (pow(lx,2) + pow(ly,2))** 0.5
            if l < r3: 
                pimb[i-(r-r3),j-(r-r3)] = pima[i,j] 

    imb = imb.resize((32, 32), resample=Image.LANCZOS)
    imb.save(outputName)
  
circle("wujian.jpg", "wujian32.png")
# circle("bao.jpg", "bao32.png")
# circle("jushi.jpg", "jushi32.png")
# circle("zero.jpg", "zero32.png")
# circle("rong.jpg", "rong32.png")
# circle("zoulv.jpg", "zoulv32.png")