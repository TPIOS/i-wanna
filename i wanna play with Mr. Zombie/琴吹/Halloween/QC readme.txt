Key Sounds Label-Gentle Jena ～Extended Version～  stage bgm
Falcom Sound Team jdk-永遠の眠り(A still time)      penalty area bgm

——————————————————————
objPlayer和objBullet和objBlood均有修改，需要整合的时候一起加上

objPlayer新增了和我其他obj的碰撞事件，且在step事件中（第268至322行）加入了一段关于我obj的代码

objBullet: 把原有的 和objBlock 碰撞事件 更换为 Step 事件
objBlood 
新增了和我其他obj的碰撞事件

——————————————————————